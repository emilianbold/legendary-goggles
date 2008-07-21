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

package org.netbeans.modules.projectimport.eclipse.core;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 */
public class ImportProblemsPanel extends javax.swing.JPanel {

    /** Creates new form ImportProblemsPanel */
    private ImportProblemsPanel(List<String> problems) {
        initComponents();
        text.setEditable(false);
        StringBuffer sb = new StringBuffer();
        for (String s : problems) {
            if (s.startsWith(" ")) { //NOI18N
                sb.append(" \u2022 "); //NOI18N
            }
            sb.append(s);
            sb.append("\r\n"); //NOI18N
            sb.append("\r\n"); //NOI18N
        }
        text.setText(sb.toString());
    }
    
    public static void showReport(String title, List<String> problems) {
        if (problems.size() == 0) {
            return;
        }
        ImportProblemsPanel p = new ImportProblemsPanel(problems);
        DialogDescriptor dd = new DialogDescriptor (p, title,
            true, new Object[]{DialogDescriptor.CLOSED_OPTION}, null, 
            DialogDescriptor.DEFAULT_ALIGN, null, null);
        Dialog dlg = DialogDisplayer.getDefault().createDialog (dd);
        dlg.setVisible(true);
    }
    
    public static List<String> indentAllButFirst(List<String> importProblems) {
        List<String> l = new ArrayList<String>();
        boolean first = true;
        for (String s : importProblems) {
            l.add((first ? "" : " ") + s); //NOI18N
            first = false;
        }
        return l;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextPane();

        jScrollPane1.setViewportView(text);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane text;
    // End of variables declaration//GEN-END:variables

}
