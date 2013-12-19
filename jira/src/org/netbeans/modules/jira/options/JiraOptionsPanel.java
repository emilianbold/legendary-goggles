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
package org.netbeans.modules.jira.options;

import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tomas Stupka
 */
@OptionsPanelController.Keywords(keywords={"jira", "#KW_IssueTracking"}, location="Team", tabTitle="#LBL_IssueTracking")
public class JiraOptionsPanel extends javax.swing.JPanel {

    /** Creates new form SvnOptionsPanel */
    public JiraOptionsPanel() {
        initComponents();
        errorLabel.setForeground(new Color(153,0,0));
        Image img = ImageUtilities.loadImage("org/netbeans/modules/jira/resources/error.gif"); //NOI18N
        errorLabel.setIcon(new ImageIcon(img));
        errorLabel.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jScrollPane2.setBorder(null);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.jLabel1.text")); // NOI18N

        issuesTextField.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.issuesTextField.text")); // NOI18N
        issuesTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issuesTextFieldActionPerformed(evt);
            }
        });

        jLabel3.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.jLabel3.text")); // NOI18N

        queriesTextField.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.queriesTextField.text")); // NOI18N
        queriesTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queriesTextFieldActionPerformed(evt);
            }
        });

        errorLabel.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.errorLabel.text")); // NOI18N

        buttonGroup1.add(xmlrpcRadioButton);
        xmlrpcRadioButton.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.xmlrpcRadioButton.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.jLabel2.text")); // NOI18N

        buttonGroup1.add(restRadioButton);
        restRadioButton.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.restRadioButton.text")); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/jira/resources/warning.gif"))); // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(JiraOptionsPanel.class, "JiraOptionsPanel.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(errorLabel)))
                        .addGap(0, 194, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(restRadioButton)
                            .addComponent(xmlrpcRadioButton)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(issuesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(queriesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(issuesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(queriesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(restRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xmlrpcRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)))
                .addGap(28, 28, 28)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void issuesTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issuesTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_issuesTextFieldActionPerformed

    private void queriesTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queriesTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_queriesTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    final javax.swing.JLabel errorLabel = new javax.swing.JLabel();
    final javax.swing.JTextField issuesTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    final javax.swing.JTextField queriesTextField = new javax.swing.JTextField();
    final javax.swing.JRadioButton restRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JRadioButton xmlrpcRadioButton = new javax.swing.JRadioButton();
    // End of variables declaration//GEN-END:variables

}
