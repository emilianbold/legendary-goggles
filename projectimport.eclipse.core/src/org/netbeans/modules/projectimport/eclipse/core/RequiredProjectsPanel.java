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

package org.netbeans.modules.projectimport.eclipse.core;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 */
class RequiredProjectsPanel extends javax.swing.JPanel {

    private Map<String, String> values;
    private List<String> rows;
    
    /** Creates new form ConfirmProjectList */
    public RequiredProjectsPanel(Map<String, String> values) {
        this.values = values;
        rows = new ArrayList<String>(values.keySet());
        initComponents();
        jTable1.setModel(new ProjectTableModel());
        jTable1.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(RequiredProjectsPanel.class, "LABEL_Eclipse_Project"));
        jTable1.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(RequiredProjectsPanel.class, "LABEL_NetBeans_Project_Destination"));
    }

    private class ProjectTableModel extends AbstractTableModel {

        public int getRowCount() {
            return rows.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int row, int column) {
            String key = rows.get(row);
            if (column == 0) {
                return key;
            } else {
                return values.get(key);
            }
        }
        
    }
    
    public static boolean showConfirmation(Collection<UpdateAllProjects.ProjectsAndDestination> projsAndDests) {
        Map<String, String> values = new HashMap<String, String>();
        for (UpdateAllProjects.ProjectsAndDestination pad : projsAndDests) {
            for (EclipseProject ep : pad.getEclipseProjects()) {
                values.put(ep.getName(), pad.getDestination().getPath());
            }
        }
        RequiredProjectsPanel p = new RequiredProjectsPanel(values);
        DialogDescriptor dd = new DialogDescriptor (p, org.openide.util.NbBundle.getMessage(RequiredProjectsPanel.class, "TITLE_Synchronize_with_Eclipse"),
            true, DialogDescriptor.OK_CANCEL_OPTION, null, null);
        Dialog dlg = DialogDisplayer.getDefault().createDialog (dd);
        dlg.setVisible(true);
        return dd.getValue() == DialogDescriptor.OK_OPTION;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(RequiredProjectsPanel.class, "RequiredProjectsPanel.jLabel1.text")); // NOI18N

        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
