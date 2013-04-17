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

package org.netbeans.modules.project.ui.groups;

/**
 * Panel to configure state of an existing ad-hoc group.
 * Applicable in advanced mode.
 * @author Jesse Glick
 */
public class AdHocGroupEditPanel extends GroupEditPanel {

    private final AdHocGroup g;

    public AdHocGroupEditPanel(AdHocGroup g) {
        this.g = g;
        initComponents();
        nameField.setText(g.getName());
        autoSynchCheckbox.setSelected(g.isAutoSynch());
        updateSynchButton();
        startPerformingNameChecks(nameField, g.getName());
    }

    @Override
    public void applyChanges() {
        g.setName(nameField.getText().trim());
        g.setAutoSynch(autoSynchCheckbox.isSelected());
    }

    private void updateSynchButton() {
        synchButton.setEnabled(!autoSynchCheckbox.isSelected() && !g.isPristine());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        autoSynchCheckbox = new javax.swing.JCheckBox();
        synchButton = new javax.swing.JButton();

        nameLabel.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.nameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(autoSynchCheckbox, org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.autoSynchCheckbox.text")); // NOI18N
        autoSynchCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        autoSynchCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSynchCheckboxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(synchButton, org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.synchButton.text")); // NOI18N
        synchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                synchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoSynchCheckbox)
                            .addComponent(synchButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoSynchCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(synchButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.nameLabel.AccessibleContext.accessibleDescription")); // NOI18N
        nameField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.nameField.AccessibleContext.accessibleName")); // NOI18N
        nameField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.nameField.AccessibleContext.accessibleDescription")); // NOI18N
        autoSynchCheckbox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.autoSynchCheckbox.AccessibleContext.accessibleDescription")); // NOI18N
        synchButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.synchButton.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AdHocGroupEditPanel.class, "AdHocGroupEditPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void synchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_synchButtonActionPerformed
        g.synch();
        updateSynchButton();
    }//GEN-LAST:event_synchButtonActionPerformed

    private void autoSynchCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSynchCheckboxActionPerformed
        updateSynchButton();
    }//GEN-LAST:event_autoSynchCheckboxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoSynchCheckbox;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton synchButton;
    // End of variables declaration//GEN-END:variables
    
}
