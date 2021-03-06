/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx2.project.ui;

import java.awt.Dialog;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import org.netbeans.modules.javafx2.project.JFXProjectConfigurations;
import org.netbeans.modules.javafx2.project.JFXProjectProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Somol
 */
public class JFXPackagingPanel extends javax.swing.JPanel {

    private static JFXProjectProperties jfxProps = null;
    private final JFXProjectProperties.JFXConfigs configs;
    private static final String appManifestEntriesColumnNames[] = new String[] {
            NbBundle.getMessage(JFXRunPanel.class, "JFXPackagingPanel.customManifestEntries.name"), // NOI18N
            NbBundle.getMessage(JFXRunPanel.class, "JFXPackagingPanel.customManifestEntries.value") // NOI18N
        };
    
    /**
     * Creates new form JFXPackagingPanel
     */
    public JFXPackagingPanel(JFXProjectProperties properties) {
        initComponents();
        jfxProps = properties;
        configs = jfxProps.getConfigs();
        binaryCSSCheckBox.setModel(jfxProps.getBinaryEncodeCSSModel());
        updateManifestMessage();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        binaryCSSCheckBox = new javax.swing.JCheckBox();
        labelManifest = new javax.swing.JLabel();
        labelManifestMessage = new javax.swing.JLabel();
        buttonManifest = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(binaryCSSCheckBox, org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "LBL_JFXPackagingPanel.binaryCSSCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        add(binaryCSSCheckBox, gridBagConstraints);
        binaryCSSCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "AN_JFXPackagingPanel.binaryCSSCheckBox.text")); // NOI18N
        binaryCSSCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "AD_JFXPackagingPanel.binaryCSSCheckBox.text")); // NOI18N

        labelManifest.setText(org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "JFXPackagingPanel.labelManifest.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 15);
        add(labelManifest, gridBagConstraints);
        labelManifest.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "AN_JFXPackagingPanel.labelManifest.text")); // NOI18N
        labelManifest.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "AD_JFXPackagingPanel.labelManifest.text")); // NOI18N

        labelManifestMessage.setText(org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "JFXPackagingPanel.labelManifestMessage.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        add(labelManifestMessage, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(buttonManifest, org.openide.util.NbBundle.getMessage(JFXPackagingPanel.class, "JFXPackagingPanel.buttonManifest.text")); // NOI18N
        buttonManifest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonManifestActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(buttonManifest, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 5, 0);
        add(jSeparator1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonManifestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonManifestActionPerformed
        List<Map<String, String>> props = configs.getDefaultManifestEntriesTransparent();
        JFXProjectProperties.PropertiesTableModel appManifestEntriesTableModel = 
                new JFXProjectProperties.PropertiesTableModel(props, null, JFXProjectConfigurations.APP_MANIFEST_SUFFIXES, appManifestEntriesColumnNames);
        JFXApplicationMultiPropertyPanel panel = new JFXApplicationMultiPropertyPanel(appManifestEntriesTableModel);
        panel.setTableTitle(NbBundle.getMessage(JFXPackagingPanel.class, "LBL_ApplicationCustomManifestEntries.tablelabel")); // NOI18N
        panel.setRemark(NbBundle.getMessage(JFXPackagingPanel.class, "LBL_ApplicationCustomManifestEntries.remark")); // NOI18N
        DialogDescriptor dialogDesc = new DialogDescriptor(panel, NbBundle.getMessage(JFXPackagingPanel.class, "TITLE_ApplicationCustomManifestEntries"), true, null); //NOI18N
        panel.registerListeners();
        panel.setDialogDescriptor(dialogDesc);
        //panel.setColumnRenderer();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
        dialog.setVisible(true);
        if (dialogDesc.getValue() == DialogDescriptor.OK_OPTION) {
            appManifestEntriesTableModel.removeEmptyRows();
            configs.setDefaultManifestEntriesTransparent(props);
            updateManifestMessage();
        }
        panel.unregisterListeners();
        dialog.dispose();
    }//GEN-LAST:event_buttonManifestActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox binaryCSSCheckBox;
    private javax.swing.JButton buttonManifest;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelManifest;
    private javax.swing.JLabel labelManifestMessage;
    // End of variables declaration//GEN-END:variables

    private void updateManifestMessage() throws MissingResourceException {
        int entries = configs.getNoOfDefaultManifestEntries();
        if(entries > 0) {
            labelManifestMessage.setText(NbBundle.getMessage(JFXPackagingPanel.class, "MSG_CustomManifestEntries.some", entries)); // NOI18N
        } else {
            labelManifestMessage.setText(NbBundle.getMessage(JFXPackagingPanel.class, "MSG_CustomManifestEntries.none")); // NOI18N
        }
    }
}
