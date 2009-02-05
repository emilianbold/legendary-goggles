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

package org.netbeans.modules.profiler.oql.language.parser;

import java.lang.reflect.Field;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.api.languages.ParserResult;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.profiler.spi.OQLEditorImpl;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;

/**
 *
 * @author Jaroslav Bachorik
 */
public class OQLParserTask extends ParserResultTask<ParserResult> {
    private Document doc;
    private EditorCookie ec;
    private JEditorPane parentPane;
    private boolean wasOk = false;

    public OQLParserTask(Document doc) {
        this.doc = doc;
        this.ec = (EditorCookie)doc.getProperty(EditorCookie.class);
        this.parentPane = (JEditorPane)doc.getProperty(JEditorPane.class);
    }
    
    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void run(ParserResult result, SchedulerEvent event) {
        try {
            if (ec != null) {
                ec.saveDocument();
            }
            if (doc.getLength() == 0) {
                this.parentPane.firePropertyChange(OQLEditorImpl.VALIDITY_PROPERTY, wasOk, false);
                wasOk = false;
                return;
            }
            Field errorsFld = result.getClass().getDeclaredField("syntaxErrors");
            errorsFld.setAccessible(true);
            List errors = (List) errorsFld.get(result);
            boolean isOk = errors.size() == 0;
            if (!isOk) {
                StringBuilder sb = new StringBuilder();
                for(Object error : errors) {
                    sb.append(error.toString()).append("\n");
                }
                StatusDisplayer.getDefault().setStatusText(sb.toString());
            }
            this.parentPane.firePropertyChange(OQLEditorImpl.VALIDITY_PROPERTY, wasOk, isOk);
            wasOk = isOk;
        } catch (Exception e) {
        }
//        System.out.println(result.getSyntaxErrors());
//        JEditorPane parentPane = (JEditorPane)doc.getProperty(JEditorPane.class);
//        if (parentPane != null) {
//            parentPane.setBackground(Color.YELLOW);
//        }
    }

    @Override
    public void cancel() {
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

}
