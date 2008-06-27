/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.db.dataview.output;

import java.sql.Connection;
import java.sql.SQLException;
import net.java.hulp.i18n.Logger;
import org.netbeans.modules.db.dataview.meta.DBException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.db.dataview.logger.Localizer;
import org.netbeans.modules.db.dataview.meta.DBConnectionFactory;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;

/**
 * @author Ahimanikya Satapathy
 */
abstract class SQLStatementExecutor implements Runnable, Cancellable {

    protected final DataView dataView;
    protected Connection conn = null;
    protected boolean error = false;
    protected volatile Throwable ex;
    protected String errorMsg = "";
    protected boolean lastCommitState;
    private String title;
    private String titleMsg;
    private volatile RequestProcessor.Task task;
    private static Logger mLogger = Logger.getLogger(SQLStatementExecutor.class.getName());
    private static transient final Localizer mLoc = Localizer.get();

    public SQLStatementExecutor(DataView parent, String title, String msg) {
        this.title = title;
        this.titleMsg = msg;
        this.dataView = parent;
    }

    public void setTask(RequestProcessor.Task task) {
        this.task = task;
    }

    public void run() {
        String nbBundle53 = mLoc.t("RESC053: Should have called setTask()");
        assert task != null : nbBundle53.substring(15);
        try {
            ProgressHandle handle = ProgressHandleFactory.createHandle(title, this);
            handle.setDisplayName(titleMsg);
            handle.start();
            try {
                handle.switchToIndeterminate();
                dataView.setInfoStatusText(""); // NOI18N

                synchronized (dataView) {
                    dataView.disableButtons();
                    dataView.clearErrorMessages();
                }

                conn = DBConnectionFactory.getInstance().getConnection(dataView.getDatabaseConnection());
                if (conn == null) {
                    String nbBundle54 = mLoc.t("RESC054: Unable to Connect to database");
                    this.ex = new DBException(nbBundle54.substring(15));
                    return;
                }
                lastCommitState = setAutocommit(conn, false);

                execute(); // delegate 

            } finally {
                handle.finish();
            }
        } catch (Exception e) {
            this.ex = e;
        } finally {
            if (ex != null) {
                errorMsg += ex.getMessage();
                error = true;
            }

            finished(); // delegate 

            resetAutocommitState(conn, lastCommitState);
        }
    }

    public boolean cancel() {
        return task.cancel();
    }

    public abstract void finished();

    public abstract void execute() throws SQLException, DBException;

    protected void executeOnSucess() {
    }

    protected void reinstateToolbar() {
        // reinstate the toolbar
        synchronized (dataView) {
            dataView.resetToolbar(false);
        }
    }

    protected void commitOrRollback(String cmdName) {
        if (!error && commit(conn)) {
            String nbBundle55 = mLoc.t("RESC055:  Executed successfully\n");
            String infoMsg = cmdName + nbBundle55.substring(15);
            dataView.setInfoStatusText(infoMsg);
            executeOnSucess(); // delegate 
        } else {
            rollback(conn);
            reinstateToolbar();
            errorMsg = cmdName + " failed:";
            dataView.setErrorStatusText(new DBException(errorMsg, ex));
        }
    }

    private boolean setAutocommit(Connection conn, boolean newState) {
        try {
            boolean lastState = conn.getAutoCommit();
            conn.setAutoCommit(newState);
            return lastState;
        } catch (SQLException e) {
            return newState;
        }
    }

    private void resetAutocommitState(Connection conn, boolean lastState) {
        if (conn != null) {
            try {
                conn.setAutoCommit(lastState);
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    private boolean commit(Connection conn) {
        try {
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            String nbBundle56 = mLoc.t("RESC056: \nFailure while commiting changes to database.\n");
            String msg = nbBundle56.substring(15);
            dataView.setErrorStatusText(new DBException(msg, e));
            return false;
        }
        return true;
    }

    private void rollback(Connection conn) {
        try {
            if (!conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            String nbBundle57 = mLoc.t("RESC057: \nFail to rollback.\n");
            String msg  = nbBundle57.substring(15);
            dataView.setErrorStatusText(new DBException(msg, e));
        }
    }
}
