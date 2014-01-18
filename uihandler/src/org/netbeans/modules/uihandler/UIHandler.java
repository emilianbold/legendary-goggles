/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.uihandler;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.modules.uihandler.api.Controller;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;

/**
 *
 * @author Jaroslav Tulach
 */
public class UIHandler extends Handler 
implements ActionListener, Runnable, Callable<JButton> {
    private final boolean exceptionOnly;
    private volatile boolean exiting;
    public static final PropertyChangeSupport SUPPORT = new PropertyChangeSupport(Controller.getDefault());
    static final int MAX_LOGS = 1000;
    /** Maximum allowed size of log file 20MB */
    static final long MAX_LOGS_SIZE = 20L * 1024L * 1024L;
    private static Task lastRecord = Task.EMPTY;
    private static RequestProcessor FLUSH = new RequestProcessor("Flush UI Logs"); // NOI18N
    private static boolean flushOnRecord;
    private static final AtomicInteger recordsToWriteOut = new AtomicInteger(0);
    private static final int MAX_RECORDS_TO_WRITE_OUT = 1111; // Be sure not to hold more than this number of log records.
    private final SlownessReporter reporter;

    private final AtomicBoolean someRecordsScheduled = new AtomicBoolean(false);

    private static boolean exceptionHandler;
    public static void registerExceptionHandler(boolean enable) {
        exceptionHandler = enable;
    }
    
    public UIHandler(boolean exceptionOnly) {
        setLevel(Level.FINEST);
        this.exceptionOnly = exceptionOnly;
        if (exceptionOnly){
            this.reporter = null;
            AfterRestartExceptions.report();
        } else {
            this.reporter = new SlownessReporter();
        }
    }

    @Override
    public void publish(LogRecord record) {
        if ((record.getLevel().equals(Level.CONFIG)) &&
                (record.getMessage().startsWith("NotifyExcPanel: "))) {//NOI18N
            Installer.setSelectedExcParams(record.getParameters());
            return;
        }

        if ("KILL_PENDING_TASKS".equals(record.getMessage())) { //NOI18N
            exiting = true;
        }
        if (!exiting) { 
            if ("SCAN_CANCELLED".equals(record.getMessage())) { //NOI18N
                if (shouldReportScanCancel()) {
                    class WriteOut implements Runnable {
                        public LogRecord r;
                        @Override
                        public void run() {
                            Installer.writeOut(r);
                            SUPPORT.firePropertyChange(null, null, null);
                            byte[] profData = (byte[])r.getParameters()[2];
                            
                            SlownessData sdata = null;
                            if (profData != null) {
                                sdata = new SlownessData((Long)r.getParameters()[1], profData, "background_scan", (String)r.getParameters()[3]); // NOI18N
                            }
                            r = null;
                            TimeToFailure.logAction();
                            Installer.displaySummary("ERROR_URL", true, false, true, sdata); //NOI18N
                        }
                    }
                    WriteOut wo = new WriteOut();
                    wo.r = record;
                    lastRecord = FLUSH.post(wo);
                }
                return;
            } else if ("SCAN_CANCELLED_EARLY".equals(record.getMessage())) { // NOI18N
                final NotifyDescriptor nd = new NotifyDescriptor(
                    NbBundle.getMessage(UIHandler.class, "MSG_SCAN_CANCELLED_EARLY"),
                    NbBundle.getMessage(UIHandler.class, "TITLE_SCAN_CANCELLED_EARLY"),
                    NotifyDescriptor.DEFAULT_OPTION,
                    NotifyDescriptor.INFORMATION_MESSAGE,
                    new Object[] {DialogDescriptor.OK_OPTION},
                    NotifyDescriptor.OK_OPTION);
                DialogDisplayer.getDefault().notify(nd);
                return;
            }
        }

        if (exceptionOnly) {
            if (record.getThrown() == null) {
                return;
            }
            if (!exceptionHandler) {
                return;
            }
            if (AfterRestartExceptions.willSchedule(record)) {
                // Set this ASAP:
                someRecordsScheduled.set(true);
                boolean scheduled = AfterRestartExceptions.schedule(record);
                if (!scheduled) {
                    someRecordsScheduled.set(false);
                }
            } else {
                someRecordsScheduled.set(false);
            }
        } else {
            if ((record.getLevel().equals(Level.CONFIG)) && record.getMessage().equals("Slowness detected")){
                Object[] params = record.getParameters();
                byte[] nps = (byte[]) params[0];
                long time = (Long) params[1];
                String slownessType = params.length > 2 ? params[2].toString() : null;
                assert nps != null: "nps param should be not null";
                assert nps.length > 0 : "nps param should not be empty";
                reporter.notifySlowness(nps, time, record.getMillis(), slownessType);
                return;
            }
        }

        if (Installer.isImmediateWriteOut(record)) {
            return ;
        }
        class WriteOut implements Runnable {
            public LogRecord r;
            @Override
            public void run() {
                recordsToWriteOut.decrementAndGet();
                Installer.writeOut(r);
                SUPPORT.firePropertyChange(null, null, null);
                r = null;
                TimeToFailure.logAction();
            }
        }
        WriteOut wo = new WriteOut();
        wo.r = record;
        recordsToWriteOut.incrementAndGet();
        lastRecord = FLUSH.post(wo);
        
        if (flushOnRecord || recordsToWriteOut.get() > MAX_RECORDS_TO_WRITE_OUT) {
            waitFlushed(true);
        }
    }

    @Override
    public void flush() {
        waitFlushed();
    }
    
    static void flushImmediatelly() {
        flushOnRecord = true;
    }
    
    static void waitFlushed() {
        waitFlushed(false);
    }
    
    private static void waitFlushed(boolean forced) {
        if (!forced) {
            assert !SwingUtilities.isEventDispatchThread() : "Must not wait in AWT here"; // NOI18N
        }
        try {
            lastRecord.waitFinished(0);
        } catch (InterruptedException ex) {
            Installer.LOG.log(Level.FINE, null, ex);
        }
    }
    
    // Accessed in test
    boolean isExceptionOnly() {
        return exceptionOnly;
    }

    @Override
    public void close() throws SecurityException {
    }
    
    @Override
    public void run() {
        Installer.displaySummary("ERROR_URL", true, false,true); // NOI18N
        Installer.setSelectedExcParams(null);
    }

    private JButton button;
    @Override
    public JButton call() throws Exception {
        if (someRecordsScheduled.getAndSet(false)) {
            return null;    // No submits when some records are scheduled after the next start.
        }
        if (button == null) {
            button = new JButton();
            Mnemonics.setLocalizedText(button, NbBundle.getMessage(UIHandler.class, "MSG_SubmitButton")); // NOI18N
            button.addActionListener(this);
        }
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        JComponent c = (JComponent)ev.getSource();
        Window w = SwingUtilities.windowForComponent(c);
        if (w != null) {
            w.dispose();
        } 
        Installer.RP.post(this);
    }

    private boolean shouldReportScanCancel() {
        final JButton sendOption = new JButton(NbBundle.getMessage(UIHandler.class, "LBL_SendReport"));
        final JButton sendAndProfileOption = new JButton(NbBundle.getMessage(UIHandler.class, "LBL_SendReportAndProfile"));
        final NotifyDescriptor nd;
        
        if (System.getProperty("org.netbeans.modules.parsing.impl.indexing.RepositoryUpdater.indexerSampling") == null) { // NOI18N
            nd = new NotifyDescriptor(
            NbBundle.getMessage(UIHandler.class, "MSG_SCAN_CANCELLED2"),
            NbBundle.getMessage(UIHandler.class, "TITLE_SCAN_CANCELLED"),
            NotifyDescriptor.YES_NO_CANCEL_OPTION,
            NotifyDescriptor.QUESTION_MESSAGE,
            new Object[] {sendOption, sendAndProfileOption, DialogDescriptor.CANCEL_OPTION},
            sendOption);
        } else {
            nd = new NotifyDescriptor(
            NbBundle.getMessage(UIHandler.class, "MSG_SCAN_CANCELLED"),
            NbBundle.getMessage(UIHandler.class, "TITLE_SCAN_CANCELLED"),
            NotifyDescriptor.YES_NO_CANCEL_OPTION,
            NotifyDescriptor.QUESTION_MESSAGE,
            new Object[] {sendOption, DialogDescriptor.CANCEL_OPTION},
            sendOption);
        }
        Object opt = DialogDisplayer.getDefault().notify(nd);
        if (opt == sendAndProfileOption) {
            System.setProperty("org.netbeans.modules.parsing.impl.indexing.RepositoryUpdater.indexerSampling", "true"); // NOI18N
        }
        return opt == sendAndProfileOption || opt == sendOption;
    }
}
