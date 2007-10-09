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

package org.netbeans.modules.profiler.ui.wizards.panels;

import java.text.MessageFormat;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.lib.profiler.common.integration.IntegrationUtils;
import org.openide.util.NbBundle;
import java.awt.Font;
import javax.swing.UIManager;

/**
 *
 * @author  j.bachorik
 */
public class AttachSettingsPanelUI extends javax.swing.JPanel {
  private class ForceSelectionComboBoxModel extends DefaultComboBoxModel {
    private String firstLine = ""; // NOI18N
    private boolean internalChange = false;
    private Object selectedItem;

    public ForceSelectionComboBoxModel(final Object[] options) {
      super(options);
    }

    public ForceSelectionComboBoxModel(final String selectionText, final Object[] options) {
      super(options);
      this.insertElementAt(selectionText, 0);
      firstLine = selectionText;
      this.selectedItem = null;
    }
    
    public void setSelectedItem(Object anObject) {
      if (internalChange)
        return;
      
      if (anObject == null || anObject.equals(firstLine)) {
        this.selectedItem= null;
        return;
      }
      
      if (this.getElementAt(0).equals(firstLine)) {
        try {
          internalChange = true;
          this.removeElementAt(0);
        } finally {
          internalChange = false;
        }
      }
      selectedItem = anObject;
      super.setSelectedItem(anObject);
    }
    
    public Object getSelectedItem() {
      if (this.selectedItem == null && this.getSize() > 0 && this.getElementAt(0).equals(firstLine))
        return firstLine;
      else
        return super.getSelectedItem();
    }
    
    public boolean isSelectionMade() {
      return selectedItem != null;
    }
  }
  
  private final String SELECT_GROUP_TITLE = NbBundle.getMessage(this.getClass(), "AttachWizard_SelectTargetTypeForceString"); // NOI18N
  private final String SELECT_TARGET_TITLE = NbBundle.getMessage(this.getClass(), "AttachWizard_SelectTargetForceString"); // NOI18N
  
  /** Creates new form AttachSettingsPanelUI */
  public AttachSettingsPanelUI(AttachSettingsPanel.PanelModel model) {
    this.model = model;
    initComponents();
    loadModel();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupMethod = new org.netbeans.modules.profiler.ui.wizards.panels.components.ButtonGroupEx();
        groupInvocation = new org.netbeans.modules.profiler.ui.wizards.panels.components.ButtonGroupEx();
        panelType = new javax.swing.JPanel();
        labelTargetType = new javax.swing.JLabel();
        comboGroups = new javax.swing.JComboBox();
        labelTargetName = new javax.swing.JLabel();
        comboTargets = new javax.swing.JComboBox();
        panelDetails = new javax.swing.JPanel();
        panelMethod = new javax.swing.JPanel();
        buttonLocal = new javax.swing.JRadioButton();
        buttonRemote = new javax.swing.JRadioButton();
        blankPanel = new javax.swing.JPanel();
        panelInvocation = new javax.swing.JPanel();
        buttonDirect = new javax.swing.JRadioButton();
        buttonDynamic16 = new javax.swing.JRadioButton();
        hintPanel = new org.netbeans.modules.profiler.ui.wizards.panels.components.ResizableHintPanel();

        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(300, 300));
        setPreferredSize(new java.awt.Dimension(500, 400));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/ui/wizards/panels/Bundle"); // NOI18N
        panelType.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("TargetTypeWizardPanelUI_SelectAttachTargetString"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N

        labelTargetType.setLabelFor(comboGroups);
        org.openide.awt.Mnemonics.setLocalizedText(labelTargetType, bundle.getString("TargetTypeWizardPanelUI_TargetTypeString")); // NOI18N

        comboGroups.setModel(getGroupsModel());
        comboGroups.setMaximumSize(new java.awt.Dimension(130, 24));
        comboGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboGroupsActionPerformed(evt);
            }
        });

        labelTargetName.setLabelFor(comboTargets);
        org.openide.awt.Mnemonics.setLocalizedText(labelTargetName, bundle.getString("TargetTypeWizardPanelUI_TargetNameTypeString")); // NOI18N

        comboTargets.setModel(getTargetsModel(true));
        comboTargets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTargetsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panelTypeLayout = new org.jdesktop.layout.GroupLayout(panelType);
        panelType.setLayout(panelTypeLayout);
        panelTypeLayout.setHorizontalGroup(
            panelTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelTypeLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelTypeLayout.createSequentialGroup()
                        .add(labelTargetType)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 399, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(comboGroups, 0, 458, Short.MAX_VALUE)
                    .add(comboTargets, 0, 458, Short.MAX_VALUE)
                    .add(labelTargetName))
                .addContainerGap())
        );
        panelTypeLayout.setVerticalGroup(
            panelTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelTypeLayout.createSequentialGroup()
                .addContainerGap()
                .add(labelTargetType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboGroups, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(14, 14, 14)
                .add(labelTargetName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboTargets, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        comboGroups.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AttachSettingsPanelUI.class, "AttachSettingsPanelUI.comboGroups.AccessibleContext.accessibleName")); // NOI18N
        comboGroups.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AttachSettingsPanelUI.class, "AttachSettingsPanelUI.comboGroups.AccessibleContext.accessibleDescription")); // NOI18N
        comboTargets.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AttachSettingsPanelUI.class, "AttachSettingsPanelUI.comboTargets.AccessibleContext.accessibleDescription")); // NOI18N

        panelDetails.setLayout(new javax.swing.BoxLayout(panelDetails, javax.swing.BoxLayout.LINE_AXIS));

        panelMethod.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("AttachWizard_AttachMethodString"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N

        groupMethod.add(buttonLocal);
        buttonLocal.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(buttonLocal, bundle.getString("TargetSettingsWizardPanelUI_LocalRadioName")); // NOI18N
        buttonLocal.setToolTipText(bundle.getString("TargetSettingsWizardPanelUI_LocalRadioAccessDescr")); // NOI18N
        buttonLocal.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonLocal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLocalActionPerformed(evt);
            }
        });

        groupMethod.add(buttonRemote);
        org.openide.awt.Mnemonics.setLocalizedText(buttonRemote, bundle.getString("TargetSettingsWizardPanelUI_RemoteRadioName")); // NOI18N
        buttonRemote.setToolTipText(bundle.getString("TargetSettingsWizardPanelUI_RemoteRadioAccessDescr")); // NOI18N
        buttonRemote.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonRemote.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoteActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panelMethodLayout = new org.jdesktop.layout.GroupLayout(panelMethod);
        panelMethod.setLayout(panelMethodLayout);
        panelMethodLayout.setHorizontalGroup(
            panelMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelMethodLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(buttonLocal)
                    .add(buttonRemote))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        panelMethodLayout.setVerticalGroup(
            panelMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelMethodLayout.createSequentialGroup()
                .addContainerGap()
                .add(buttonLocal)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonRemote)
                .add(0, 26, Short.MAX_VALUE))
        );

        panelDetails.add(panelMethod);

        blankPanel.setMaximumSize(new java.awt.Dimension(80, 10));
        blankPanel.setMinimumSize(new java.awt.Dimension(20, 10));
        blankPanel.setPreferredSize(new java.awt.Dimension(20, 10));

        org.jdesktop.layout.GroupLayout blankPanelLayout = new org.jdesktop.layout.GroupLayout(blankPanel);
        blankPanel.setLayout(blankPanelLayout);
        blankPanelLayout.setHorizontalGroup(
            blankPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 20, Short.MAX_VALUE)
        );
        blankPanelLayout.setVerticalGroup(
            blankPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );

        panelDetails.add(blankPanel);

        panelInvocation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("AttachWizard_AttachInvocationString"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N

        groupInvocation.add(buttonDirect);
        buttonDirect.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(buttonDirect, bundle.getString("TargetSettingsWizardPanelUI_DirectRadioName")); // NOI18N
        buttonDirect.setToolTipText(bundle.getString("TargetSettingsWizardPanelUI_DirectRadioAccessDescr")); // NOI18N
        buttonDirect.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonDirect.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonDirect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDirectActionPerformed(evt);
            }
        });

        groupInvocation.add(buttonDynamic16);
        org.openide.awt.Mnemonics.setLocalizedText(buttonDynamic16, org.openide.util.NbBundle.getMessage(AttachSettingsPanelUI.class, "TargetSettingsWizardPanelUI_Dynamic16RadioName")); // NOI18N
        buttonDynamic16.setToolTipText(org.openide.util.NbBundle.getMessage(AttachSettingsPanelUI.class, "AttachSettingsPanelUI.buttonDynamic16.toolTipText")); // NOI18N
        buttonDynamic16.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonDynamic16.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonDynamic16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDynamic16ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panelInvocationLayout = new org.jdesktop.layout.GroupLayout(panelInvocation);
        panelInvocation.setLayout(panelInvocationLayout);
        panelInvocationLayout.setHorizontalGroup(
            panelInvocationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelInvocationLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelInvocationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(buttonDirect)
                    .add(buttonDynamic16))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        panelInvocationLayout.setVerticalGroup(
            panelInvocationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelInvocationLayout.createSequentialGroup()
                .addContainerGap()
                .add(buttonDirect)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonDynamic16)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        panelDetails.add(panelInvocation);

        hintPanel.setMaximumSize(new java.awt.Dimension(500, 150));
        hintPanel.setMinimumSize(new java.awt.Dimension(500, 40));
        hintPanel.setPreferredSize(new java.awt.Dimension(500, 80));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(panelType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(panelDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)))
                .addContainerGap())
            .add(hintPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panelType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelDetails, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 21, Short.MAX_VALUE)
                .add(hintPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        hintPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AttachSettingsPanelUI.class, "AttachSettingsPanelUI.hintPanel.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void buttonDynamic16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDynamic16ActionPerformed
      model.setDynamicAttach16(buttonDynamic16.isEnabled());
      loadModel();
    }//GEN-LAST:event_buttonDynamic16ActionPerformed
      
    private void buttonDirectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDirectActionPerformed
      model.setDirectAttach(buttonDirect.isEnabled());
      loadModel();
    }//GEN-LAST:event_buttonDirectActionPerformed
    
    private void buttonRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoteActionPerformed
      model.setRemote(buttonRemote.isEnabled());
      loadModel();
    }//GEN-LAST:event_buttonRemoteActionPerformed
    
    private void buttonLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLocalActionPerformed
      model.setLocal(buttonLocal.isEnabled());
      loadModel();
    }//GEN-LAST:event_buttonLocalActionPerformed
    
    private void comboTargetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTargetsActionPerformed
      if (!((ForceSelectionComboBoxModel)comboTargets.getModel()).isSelectionMade()) {
        return;
      }
      model.setTarget((AttachSettingsPanel.Target)comboTargets.getSelectedItem());
      loadModel();
    }//GEN-LAST:event_comboTargetsActionPerformed
    
    private void comboGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboGroupsActionPerformed
      if (!((ForceSelectionComboBoxModel)comboGroups.getModel()).isSelectionMade()) {
        return;
      }
      
      if (model.getTargetGroup() != null && comboGroups.getSelectedItem() != null && model.getTargetGroup().equals(comboGroups.getSelectedItem()))
        return;
      
      model.setTargetGroup((AttachSettingsPanel.TargetGroup)comboGroups.getSelectedItem());
      if (model.getTargetGroup().isSingular()) {
        comboTargets.setModel(getTargetsModel(false));
        comboTargets.getModel().setSelectedItem(model.getTargetGroup().getTargets()[0]);
        model.setTarget((AttachSettingsPanel.Target)comboTargets.getModel().getSelectedItem());
      } else {
        comboTargets.setModel(getTargetsModel(true));
        comboTargets.getModel().setSelectedItem(null);
        model.setTarget(null);
      }
      loadModel();
    }//GEN-LAST:event_comboGroupsActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blankPanel;
    private javax.swing.JRadioButton buttonDirect;
    private javax.swing.JRadioButton buttonDynamic16;
    private javax.swing.JRadioButton buttonLocal;
    private javax.swing.JRadioButton buttonRemote;
    private javax.swing.JComboBox comboGroups;
    private javax.swing.JComboBox comboTargets;
    private org.netbeans.modules.profiler.ui.wizards.panels.components.ButtonGroupEx groupInvocation;
    private org.netbeans.modules.profiler.ui.wizards.panels.components.ButtonGroupEx groupMethod;
    private org.netbeans.modules.profiler.ui.wizards.panels.components.ResizableHintPanel hintPanel;
    private javax.swing.JLabel labelTargetName;
    private javax.swing.JLabel labelTargetType;
    private javax.swing.JPanel panelDetails;
    private javax.swing.JPanel panelInvocation;
    private javax.swing.JPanel panelMethod;
    private javax.swing.JPanel panelType;
    // End of variables declaration//GEN-END:variables
  
  private AttachSettingsPanel.PanelModel model;
  
  /*default*/ void init() {
    loadModel();
  }
  
  
  /*default*/ void applyCombos() {
    final AttachSettingsPanel.Target selectedTarget = model.getTarget();
    comboTargets.setModel(getTargetsModel(true));
    comboGroups.setSelectedItem(model.getTargetGroup());
    comboTargets.setSelectedItem(selectedTarget);
  }
  /*default*/ void loadModel() {
    AttachSettingsPanel.Target target = model.getTarget();
    
    if (!(model.getTargetGroup() == null || model.getTargetGroup().isNull() || model.getTargetGroup().isSingular())) {
      org.openide.awt.Mnemonics.setLocalizedText(labelTargetName, MessageFormat.format(NbBundle.getBundle(AttachSettingsPanel.class).getString("TargetTypeWizardPanelUI_TargetNameTypeString"), new Object[]{model.getTargetGroup().getName()})); // NOI18N
      labelTargetName.setVisible(true);
      comboTargets.setVisible(true);
    } else {
      labelTargetName.setVisible(false);
      comboTargets.setVisible(false);
    }
    
    if (model.getTarget() == null) {
      panelDetails.setVisible(false);
      hintPanel.setVisible(false);
      return;
    } else {
      panelDetails.setVisible(true);
      hintPanel.setVisible(true);
    }
    String platform = IntegrationUtils.getLocalJavaPlatform();
    boolean remote = target.supportsRemoteProfiling() && model.isRemote();
    boolean local = (target.supportsLocalProfiling() && model.isLocal()) || !target.supportsRemoteProfiling();
    boolean directEnabled = target.supportsDirectAttach();
    boolean dynamicEnabled16 = target.supportsDynamicAttach() && model.isLocal() && (platform.equals(IntegrationUtils.PLATFORM_JAVA_60) || platform.equals(IntegrationUtils.PLATFORM_JAVA_70));
    boolean direct = ((directEnabled && model.isDirectAttach()) || (!dynamicEnabled16 && model.isDynamicAttach16()));
    boolean dynamic16 = (dynamicEnabled16 && model.isDynamicAttach16() && model.isLocal());
//
//    // fix the dynamic button in case the selected target doesn't support dynamic attach
//    if (!dynamicEnabled && model.isDynamicAttach()) {
//      dynamic = false;
//      direct = true;
//    }
    
    buttonRemote.setSelected(remote);
    buttonLocal.setSelected(local);
    
    buttonRemote.setEnabled(target.supportsRemoteProfiling());
    buttonLocal.setEnabled(target.supportsLocalProfiling());
    buttonDynamic16.setEnabled(dynamicEnabled16);
    buttonDirect.setEnabled(directEnabled);
    
    final boolean methodEnabled = buttonLocal.isEnabled() || buttonRemote.isEnabled();
    final boolean invocationEnabled = directEnabled || dynamicEnabled16;
    
//
//    labelAttachMethod.setEnabled(buttonLocal.isEnabled() || buttonRemote.isEnabled());
//    labelAttachInvocation.setEnabled(directEnabled || dynamicEnabled);
    
    if (!methodEnabled) {
      groupMethod.clearSelection();
    }
    if (!invocationEnabled) {
      groupInvocation.clearSelection();
    } else {
      buttonDynamic16.setSelected(dynamic16);
      buttonDirect.setSelected(direct);
    }
    if (direct) {
      model.setDirectAttach(direct);
    }
    if (dynamic16) {
      model.setDynamicAttach16(dynamic16);
    }
    hintPanel.setHint(model.getHints());
//    this.invalidate();
  }
  
  public ComboBoxModel getGroupsModel() {
    return new ForceSelectionComboBoxModel(SELECT_GROUP_TITLE, model.getTargetGroups());
  }
  
  public ComboBoxModel getTargetsModel(final boolean forcedModel) {
    if (forcedModel)
      return new ForceSelectionComboBoxModel(SELECT_TARGET_TITLE, model.getTargetGroup() != null ? model.getTargetGroup().getTargets() : new Object[]{});
    else
      return new ForceSelectionComboBoxModel(model.getTargetGroup().getTargets());
  }
  
}
