/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nativeexecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.nativeexecution.api.NativeProcess;
import org.netbeans.modules.nativeexecution.api.NativeProcess.State;
import org.netbeans.modules.nativeexecution.support.Logger;
import org.netbeans.modules.nativeexecution.support.NativeTaskExecutorService;

public abstract class AbstractNativeProcess extends NativeProcess {

    private final static java.util.logging.Logger log = Logger.getInstance();
    private final static Integer PID_TIMEOUT =
            Integer.valueOf(System.getProperty(
            "dlight.nativeexecutor.pidtimeout", "70")); // NOI18N
    private final Object stateLock = new Object();
    private final String id;
    private final Collection<ChangeListener> listeners;
    private State state = State.INITIAL;
    private Integer exitValue = null;
    private Integer pid = null;

    public AbstractNativeProcess(NativeProcessInfo info) {
        this.id = info.getCommandLine();
        this.listeners = info.getListeners() == null
                ? Collections.synchronizedList(new ArrayList<ChangeListener>())
                : info.getListeners();
        setState(State.STARTING);
    }

    public final synchronized int getPID() {
        if (pid == null) {
            throw new IllegalStateException("Process was not started"); // NOI18N
        }

        return pid.intValue();
    }

    /**
     * To be implemented by a successor.
     * It must terminate the underlaying system process on this method call.
     */
    public abstract void cancel();

    /**
     * To be implemented by a successor. This method must cause the current
     * thread to wait until the underlaying system process is done and return
     * it's exit code.
     *
     * @return exit code of underlaying system process.
     * @exception  InterruptedException if the current thread is
     *             {@link Thread#interrupt() interrupted} by another thread
     *             while it is waiting, then the wait is ended and an
     *             {@link InterruptedException} is thrown.
     */
    public abstract int waitResult() throws InterruptedException;

    public final State getState() {
        return state;
    }

    /**
     * Returns human-readable identification of the <tt>AbstractNativeProcess</tt>.
     * @return string that identifies the <tt>AbstractNativeProcess</tt>.
     */
    @Override
    public String toString() {
        return (id == null) ? super.toString() : id.trim();
    }

    /**
     * Terminates the underlaying system process. The system process represented
     * by this <code>AbstractNativeProcess</code> object is forcibly terminated.
     */
    @Override
    public final void destroy() {
        synchronized (stateLock) {
            if (this.state == State.RUNNING) {
                cancel();
                setState(State.CANCELLED);
            }
        }
    }

    /**
     * Causes the current thread to wait, if necessary, until the
     * process represented by this <code>AbstractNativeProcess</code> object has
     * terminated. This method returns immediately if the subprocess has already
     * terminated. If the subprocess has not yet terminated, the calling thread
     * will be blocked until the subprocess exits.
     *
     * @return     the exit value of the process. By convention,
     *             <code>0</code> indicates normal termination.
     * @exception  InterruptedException  if the current thread is
     *             {@link Thread#interrupt() interrupted} by another thread
     *             while it is waiting, then the wait is ended and an
     *             {@link InterruptedException} is thrown.
     */
    @Override
    public final int waitFor() throws InterruptedException {
        setExitValue(waitResult());

        if (exitValue == null) {
            throw new InterruptedException(
                    "Process has been cancelled."); // NOI18N
        }

        return exitValue.intValue();
    }

    /**
     * Returns the exit code for the underlaying system process.
     *
     * @return  the exit code of the system process represented by this
     *          <code>AbstractNativeProcess</code> object. By convention, the value
     *          <code>0</code> indicates normal termination.
     * @exception  IllegalThreadStateException if the system process
     *             represented by this <code>Process</code> object has not
     *             yet terminated.
     */
    @Override
    public final int exitValue() {
        if (exitValue == null) {
            if (state == State.CANCELLED) {
                // TODO: ??
                // Removed CancellationException because it is not proceeded
                // in ExecutionService...
                // throw new CancellationException("Process has been cancelled");
                return -1;
            } else {
                // Process not started yet...
                throw new IllegalThreadStateException();
            }
        }

        return exitValue;
    }

    private void notifyListeners(State oldState, State newState) {
        log.fine(this.toString() + " State change: " + // NOI18N
                oldState + " -> " + newState); // NOI18N

        if (listeners.isEmpty()) {
            return;
        }

        final ChangeEvent event = new ChangeEvent(this);

        for (ChangeListener l : listeners) {
            l.stateChanged(event);
        }

    }

    private void setExitValue(int exitValue) {
        if (this.exitValue != null) {
            return;
        }

        if (state == State.CANCELLED || state == State.ERROR) {
            return;
        }

        this.exitValue = Integer.valueOf(exitValue);
        setState(State.FINISHED);
    }

    private final void setState(State state) {
        synchronized (stateLock) {
            if (this.state == state) {
                return;
            }

            State oldState = this.state;
            this.state = state;

            notifyListeners(oldState, state);
        }
    }

    protected synchronized final void readPID(final InputStream is) {
        Callable<Integer> pidReaderTask = new Callable<Integer>() {

            public Integer call() throws Exception {
                int i;
                StringBuilder sb = new StringBuilder();

                while ((i = is.read()) != (int) '\n') {
                    sb.append((char) i);
                }

                Integer result = null;

                try {
                    result = Integer.parseInt(sb.toString());
                } catch (NumberFormatException e) {
                }

                return result;
            }
        };


        Future<Integer> futurePID =
                NativeTaskExecutorService.submit(pidReaderTask, "Read PID of " + id); // NOI18N

        try {
            pid = futurePID.get(PID_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        } catch (TimeoutException ex) {
            futurePID.cancel(true);
        }

        if (pid == null) {
            // was unable to get real pid
            setState(State.ERROR);

            // Try to read stderr...
            final InputStream errorStream = getErrorStream();
            if (errorStream != null) {
                Callable<Integer> readErrorTask = new Callable<Integer>() {

                    public Integer call() {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(errorStream));
                        String s = null;
                        try {
                            while ((s = reader.readLine()) != null) {
                                System.err.println(s);
                            }
                        } catch (IOException ex) {
                        }

                        return -1;
                    }
                };

                Future<Integer> result =
                        NativeTaskExecutorService.submit(readErrorTask,
                        "Read error from " + id); // NOI18N
                try {
                    result.get(3, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                } catch (TimeoutException ex) {
                    result.cancel(true);
                }

            }
        } else {
            setState(State.RUNNING);
        }
    }
}
