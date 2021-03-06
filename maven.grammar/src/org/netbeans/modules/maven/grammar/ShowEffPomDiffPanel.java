/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.grammar;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.netbeans.modules.maven.api.MavenConfiguration;
import org.netbeans.modules.maven.spi.customizer.TextToValueConversions;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;

/**
 *
 * @author mkleint
 */
class ShowEffPomDiffPanel extends javax.swing.JPanel {

    /**
     * Creates new form ShowEffPomDiffPanel
     */
    public ShowEffPomDiffPanel(ProjectConfigurationProvider<MavenConfiguration> configs) {
        initComponents();
        ComboBoxModel<MavenConfiguration> model = new DefaultComboBoxModel<MavenConfiguration>(configs.getConfigurations().toArray(new MavenConfiguration[0]));
        comConfiguration.setModel(model);
        comConfiguration.setEditable(false);
        comConfiguration.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((ProjectConfiguration)value).getDisplayName(), index, isSelected, cellHasFocus);
            }
        });
        comConfiguration.setSelectedItem(configs.getActiveConfiguration());
        enableFields();
        epProperties.setContentType("text/x-properties");
    }
    
    MavenConfiguration getSelectedConfig() {
        return (MavenConfiguration) comConfiguration.getSelectedItem();
    }
    
    boolean isConfigurationSelected() {
        return rbConfiguration.isSelected();
    }
    
    List<String> getSelectedProfiles() {
        StringTokenizer tok = new StringTokenizer(txtProfiles.getText().trim(), " ,");
        ArrayList<String> lst = new ArrayList<String>();
        while (tok.hasMoreTokens()) {
            lst.add(tok.nextToken());
        }
        return lst;
    }
    
    Map<String, String> getSelectedProperties() {
        return TextToValueConversions.convertStringToActionProperties(epProperties.getText());
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblConfiguration = new javax.swing.JLabel();
        comConfiguration = new javax.swing.JComboBox();
        rbConfiguration = new javax.swing.JRadioButton();
        rbCustom = new javax.swing.JRadioButton();
        lblProfiles = new javax.swing.JLabel();
        txtProfiles = new javax.swing.JTextField();
        lblProperties = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        epProperties = new javax.swing.JEditorPane();

        lblConfiguration.setLabelFor(comConfiguration);
        org.openide.awt.Mnemonics.setLocalizedText(lblConfiguration, org.openide.util.NbBundle.getMessage(ShowEffPomDiffPanel.class, "ShowEffPomDiffPanel.lblConfiguration.text")); // NOI18N

        buttonGroup1.add(rbConfiguration);
        rbConfiguration.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(rbConfiguration, org.openide.util.NbBundle.getMessage(ShowEffPomDiffPanel.class, "ShowEffPomDiffPanel.rbConfiguration.text")); // NOI18N
        rbConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbConfigurationActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbCustom);
        org.openide.awt.Mnemonics.setLocalizedText(rbCustom, org.openide.util.NbBundle.getMessage(ShowEffPomDiffPanel.class, "ShowEffPomDiffPanel.rbCustom.text")); // NOI18N
        rbCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbCustomActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblProfiles, org.openide.util.NbBundle.getMessage(ShowEffPomDiffPanel.class, "ShowEffPomDiffPanel.lblProfiles.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblProperties, org.openide.util.NbBundle.getMessage(ShowEffPomDiffPanel.class, "ShowEffPomDiffPanel.lblProperties.text")); // NOI18N

        jScrollPane1.setViewportView(epProperties);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbConfiguration)
                            .addComponent(rbCustom))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProfiles)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtProfiles))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblConfiguration)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comConfiguration, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProperties)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(rbConfiguration)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConfiguration)
                    .addComponent(comConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(rbCustom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProfiles)
                    .addComponent(txtProfiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblProperties)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rbConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbConfigurationActionPerformed
        enableFields();
    }//GEN-LAST:event_rbConfigurationActionPerformed

    private void rbCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCustomActionPerformed
        enableFields();
    }//GEN-LAST:event_rbCustomActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox comConfiguration;
    private javax.swing.JEditorPane epProperties;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblConfiguration;
    private javax.swing.JLabel lblProfiles;
    private javax.swing.JLabel lblProperties;
    private javax.swing.JRadioButton rbConfiguration;
    private javax.swing.JRadioButton rbCustom;
    private javax.swing.JTextField txtProfiles;
    // End of variables declaration//GEN-END:variables

    private void enableFields() {
        comConfiguration.setEnabled(rbConfiguration.isSelected());
        epProperties.setEnabled(rbCustom.isSelected());
        txtProfiles.setEnabled(rbCustom.isSelected());
    }
}
