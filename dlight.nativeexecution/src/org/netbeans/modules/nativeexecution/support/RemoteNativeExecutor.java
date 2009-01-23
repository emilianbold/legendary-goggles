/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nativeexecution.support;

import org.netbeans.modules.nativeexecution.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.NativeTask;
import org.netbeans.modules.nativeexecution.api.NativeExecutor;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.netbeans.modules.nativeexecution.support.Logger;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class RemoteNativeExecutor extends NativeExecutor {

    private ChannelExec channel;
    private ExecutionEnvironment execEnv;
    private InputStream out;
    private InputStream err;
    private OutputStream in;

    public RemoteNativeExecutor(NativeTask task) {
        super(task);
        this.execEnv = task.getExecEnv();
    }

    @Override
    protected int doInvoke() throws Exception {
        final Session session = ConnectionManager.getInstance().getConnectionSession(execEnv);

        if (session == null) {
            return -1;
        }

        setProgress(loc("NativeExecutor_Progress_ExecutingTask", task.toString())); // NOI18N

        synchronized (session) {
            channel = (ChannelExec) session.openChannel("exec"); // NOI18N
            channel.setCommand("/bin/echo $$; exec " + task.getCommand()); // NOI18N
            channel.connect();
        }

        try {
            out = channel.getInputStream();
            err = channel.getErrStream();
            in = channel.getOutputStream();
        } catch (Exception e) {
            Logger.getInstance().severe("Failed to get streams from ChannelExec"); // NOI18N
            e.printStackTrace();
        }

        // Read-out pid from the first line of output (result of 'echo $$')
        BufferedReader br = new BufferedReader(new InputStreamReader(out));
        String pidLine = br.readLine();
        int pid = -1;

        if (pidLine == null) {
            log.severe("Cannot get PID for " + task.getCommand()); // NOI18N
        } else {
            try {
                pid = Integer.parseInt(pidLine.trim());
            } catch (NumberFormatException ex) {
                log.severe("Cannot get PID for " + task.getCommand()); // NOI18N
            }
        }

        return pid;
    }

    public boolean cancel() {
        if (channel != null) {
            channel.disconnect();
        }

        // TODO: When to cancel session?
//    if (session != null) {
//      session.disconnect();
//    }

        return true;
    }

    @Override
    protected InputStream getTaskInputStream() throws IOException {
        return out;
    }

    @Override
    protected InputStream getTaskErrorStream() throws IOException {
        return err;
    }

    @Override
    protected OutputStream getTaskOutputStream() throws IOException {
        return in;
    }

    private static String loc(String key, Object... params) {
        return NbBundle.getMessage(RemoteNativeExecutor.class, key, params);
    }

    @Override
    protected synchronized Integer get() {
        while (!channel.isClosed()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        channel.disconnect();
        return channel.getExitStatus();
    }
}
