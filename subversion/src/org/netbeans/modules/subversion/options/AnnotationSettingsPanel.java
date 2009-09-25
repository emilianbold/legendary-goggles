/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.subversion.options;

import org.netbeans.modules.versioning.util.FilePathCellRenderer;

/**
 *
 * @author  Tomas Stupka
 */
public class AnnotationSettingsPanel extends javax.swing.JPanel {
    
    /** Creates new form AnnotationSettingsPanel */
    public AnnotationSettingsPanel() {
        initComponents();
        initModel();
        expresionsTable.setDefaultRenderer(String.class, new FilePathCellRenderer());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.resetButton.text")); // NOI18N

        tableLabel.setLabelFor(expresionsTable);
        tableLabel.setText(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.tableLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(upButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.upButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(downButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.downButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(newButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.newButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.removeButton.text")); // NOI18N

        expressionsPane.setViewportView(expresionsTable);
        expresionsTable.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSN_LablesTable")); // NOI18N
        expresionsTable.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSD_LablesTable")); // NOI18N

        warningLabel.setForeground(new java.awt.Color(255, 0, 0));
        warningLabel.setText(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.warningLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wizardButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.wizardButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.editButton.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tableLabel)
                    .add(layout.createSequentialGroup()
                        .add(resetButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(warningLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
                    .add(expressionsPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
                .add(4, 4, 4)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(removeButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, editButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(newButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(wizardButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, upButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, downButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tableLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(newButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(wizardButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton)
                        .add(18, 18, 18)
                        .add(upButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(downButton))
                    .add(expressionsPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(resetButton)
                    .add(warningLabel))
                .addContainerGap())
        );

        resetButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSN_Reset")); // NOI18N
        resetButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSD_Reset")); // NOI18N
        newButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSN_Add")); // NOI18N
        newButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSD_Add")); // NOI18N
        wizardButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSN_AddWizard")); // NOI18N
        wizardButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "ACSD_AddWizard")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    private void initModel() {
        expresionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", ""}
            },
            new String [] {
                org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.expresionsTable.column1.name"),
                org.openide.util.NbBundle.getMessage(AnnotationSettingsPanel.class, "AnnotationSettingsPanel.expresionsTable.column2.name")
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton downButton = new javax.swing.JButton();
    final javax.swing.JButton editButton = new javax.swing.JButton();
    final javax.swing.JTable expresionsTable = new javax.swing.JTable();
    final javax.swing.JScrollPane expressionsPane = new javax.swing.JScrollPane();
    final javax.swing.JButton newButton = new javax.swing.JButton();
    final javax.swing.JButton removeButton = new javax.swing.JButton();
    final javax.swing.JButton resetButton = new javax.swing.JButton();
    final javax.swing.JLabel tableLabel = new javax.swing.JLabel();
    final javax.swing.JButton upButton = new javax.swing.JButton();
    final javax.swing.JLabel warningLabel = new javax.swing.JLabel();
    final javax.swing.JButton wizardButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables
    
}
