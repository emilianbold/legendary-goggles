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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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

package org.netbeans.modules.mercurial.ui.repository;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.diff.options.AccessibleJFileChooser;
import org.openide.util.NbBundle;

/**
 *
 * @author  Petr Kuzel
 */
public class RepositoryPanel extends javax.swing.JPanel {

    private Runnable postInitRoutine;

    /** Creates new form RepositoryPanel */
    public RepositoryPanel() {
        initComponents();
    }

    void schedulePostInitRoutine(Runnable postInitRoutine) {
        this.postInitRoutine = postInitRoutine;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        setPreferredSize(getPreferredSize());
        if (postInitRoutine != null) {
            postInitRoutine.run();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(480, 160));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/mercurial/ui/repository/Bundle"); // NOI18N
        setName(bundle.getString("BK2018")); // NOI18N
        setVerifyInputWhenFocusTarget(false);

        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, bundle.getString("BK0001")); // NOI18N

        jLabel2.setLabelFor(urlComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0002")); // NOI18N

        urlComboBox.setEditable(true);
        java.awt.Component editorComp = urlComboBox.getEditor().getEditorComponent();
        if (editorComp instanceof javax.swing.JTextField) {
            ((javax.swing.JTextField) editorComp).setColumns(35);
        }

        org.openide.awt.Mnemonics.setLocalizedText(proxySettingsButton, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0006")); // NOI18N
        proxySettingsButton.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "ACSD_ProxyDialog")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(tipLabel, "-"); // NOI18N
        tipLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));

        userPasswordField.setColumns(8);
        userPasswordField.setMinimumSize(new java.awt.Dimension(11, 22));

        passwordLabel.setLabelFor(userPasswordField);
        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0004")); // NOI18N
        passwordLabel.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "TT_Password")); // NOI18N

        userLabel.setLabelFor(userTextField);
        org.openide.awt.Mnemonics.setLocalizedText(userLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0003")); // NOI18N
        userLabel.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "TT_UserName")); // NOI18N

        userTextField.setColumns(8);
        userTextField.setMinimumSize(new java.awt.Dimension(11, 22));

        org.openide.awt.Mnemonics.setLocalizedText(leaveBlankLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0005")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(tunnelLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0010")); // NOI18N

        tunnelCommandTextField.setColumns(35);

        org.openide.awt.Mnemonics.setLocalizedText(tunnelCommandLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0009")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(tunnelHelpLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "TT_svn_xxx")); // NOI18N

        savePasswordCheckBox.setMnemonic('v');
        org.openide.awt.Mnemonics.setLocalizedText(savePasswordCheckBox, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0007")); // NOI18N
        savePasswordCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(chooseFolderButton, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.chooseFolderButton.text")); // NOI18N
        chooseFolderButton.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.chooseFolderButton.toolTipText")); // NOI18N
        chooseFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseFolderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titleLabel)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(userLabel)
                    .addComponent(passwordLabel))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(savePasswordCheckBox)
                        .addContainerGap())
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tipLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(userPasswordField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(userTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(leaveBlankLabel)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(urlComboBox, 0, 206, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(chooseFolderButton)))
                            .addContainerGap()))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proxySettingsButton)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(tunnelHelpLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(tunnelLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tunnelCommandTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                        .addComponent(tunnelCommandLabel, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(urlComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseFolderButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userLabel)
                    .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(leaveBlankLabel))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(userPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordLabel)))
                .addGap(3, 3, 3)
                .addComponent(savePasswordCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(proxySettingsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tunnelCommandLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tunnelLabel)
                    .addComponent(tunnelCommandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tunnelHelpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        titleLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "ACSD_RepositoryPanel_Title")); // NOI18N
        titleLabel.getAccessibleContext().setAccessibleParent(this);
        jLabel2.getAccessibleContext().setAccessibleParent(this);
        urlComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "ACSN_RepositoryURL")); // NOI18N
        urlComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "ACSD_RepositoryURL")); // NOI18N
        urlComboBox.getAccessibleContext().setAccessibleParent(this);
        proxySettingsButton.getAccessibleContext().setAccessibleParent(this);
        userPasswordField.getAccessibleContext().setAccessibleParent(this);
        passwordLabel.getAccessibleContext().setAccessibleParent(this);
        userLabel.getAccessibleContext().setAccessibleParent(this);
        userTextField.getAccessibleContext().setAccessibleParent(this);
        leaveBlankLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "ACSD_InfoLabel")); // NOI18N
        leaveBlankLabel.getAccessibleContext().setAccessibleParent(this);
        savePasswordCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "BK0011")); // NOI18N

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "ACSD_RepositoryPanel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void chooseFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseFolderButtonActionPerformed
        JTextComponent comboEditor = ((JTextComponent) urlComboBox.getEditor().getEditorComponent());
        File file = null;
        try {
            URI uri = new URI(comboEditor.getText());
            file = new File(uri);
        } catch (URISyntaxException | IllegalArgumentException ex) {
            //
        }
        JFileChooser fileChooser = new AccessibleJFileChooser(NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.FileChooser.Descritpion"), //NOI18N
                file);
        fileChooser.setDialogTitle(NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.FileChooser.Title")); //NOI18N
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showDialog(this, null);
        File f = fileChooser.getSelectedFile();
        if (f != null) {
            comboEditor.setText(f.toURI().toString());
        }
    }//GEN-LAST:event_chooseFolderButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton chooseFolderButton = new javax.swing.JButton();
    private final javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    final javax.swing.JLabel leaveBlankLabel = new javax.swing.JLabel();
    final javax.swing.JLabel passwordLabel = new javax.swing.JLabel();
    final javax.swing.JButton proxySettingsButton = new javax.swing.JButton();
    final javax.swing.JCheckBox savePasswordCheckBox = new javax.swing.JCheckBox();
    final javax.swing.JLabel tipLabel = new javax.swing.JLabel();
    final javax.swing.JLabel titleLabel = new javax.swing.JLabel();
    final javax.swing.JLabel tunnelCommandLabel = new javax.swing.JLabel();
    final javax.swing.JTextField tunnelCommandTextField = new javax.swing.JTextField();
    final javax.swing.JLabel tunnelHelpLabel = new javax.swing.JLabel();
    final javax.swing.JLabel tunnelLabel = new javax.swing.JLabel();
    final javax.swing.JComboBox urlComboBox = new javax.swing.JComboBox();
    final javax.swing.JLabel userLabel = new javax.swing.JLabel();
    final javax.swing.JPasswordField userPasswordField = new javax.swing.JPasswordField();
    final javax.swing.JTextField userTextField = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables
        
}
