/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.testtools.wizards;

/*
 * TestWorkspaceSettingsPanel.java
 *
 * Created on April 10, 2002, 1:43 PM
 */

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.loaders.TemplateWizard;
import java.awt.CardLayout;
import org.openide.loaders.DataFolder;
import java.io.File;

/**
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 */
public class TestWorkspaceSettingsPanel extends javax.swing.JPanel implements WizardDescriptor.FinishPanel {
    
    private boolean stop=true;
    private static final String netbeansPath="../../../nb_all/nbbuild/netbeans";
    private static final String xtestPath="../../../nb_all/xtest";
    private static final String jemmyPath="../../../nbextra/jemmy";
    private static final String jellyPath="../../../nbextra/jellytools";
    private String type="qa-functional";
    private String attr="all, ide";
    private String source="ide";
    
    /** Creates new form TestWorkspacePanel */
    public TestWorkspaceSettingsPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panel = new javax.swing.JPanel();
        levelLabel = new javax.swing.JLabel();
        levelCombo = new javax.swing.JComboBox();
        typeLabel = new javax.swing.JLabel();
        typeField = new javax.swing.JTextField();
        attrLabel = new javax.swing.JLabel();
        attrField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        advancedCheck = new javax.swing.JCheckBox();
        netbeansLabel = new javax.swing.JLabel();
        netbeansField = new javax.swing.JTextField();
        xtestLabel = new javax.swing.JLabel();
        xtestField = new javax.swing.JTextField();
        sourceLabel = new javax.swing.JLabel();
        sourceCombo = new javax.swing.JComboBox();
        netbeansButton = new javax.swing.JButton();
        xtestButton = new javax.swing.JButton();
        jemmyLabel = new javax.swing.JLabel();
        jemmyField = new javax.swing.JTextField();
        jemmyButton = new javax.swing.JButton();
        jellyLabel = new javax.swing.JLabel();
        jellyField = new javax.swing.JTextField();
        jellyButton = new javax.swing.JButton();
        stopLabel = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        panel.setLayout(new java.awt.GridBagLayout());

        levelLabel.setText("Test Workspace possition in CVS: ");
        levelLabel.setDisplayedMnemonic(87);
        levelLabel.setLabelFor(levelCombo);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(levelLabel, gridBagConstraints);

        levelCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "On top of the module (repository / module)", "One level lower (repository / module / package)", "Two levels lower (repository / module / package / package)", "Out of CVS structute (for local use only)" }));
        levelCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelComboActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(levelCombo, gridBagConstraints);

        typeLabel.setText("Default Test Type: ");
        typeLabel.setDisplayedMnemonic(84);
        typeLabel.setLabelFor(typeField);
        typeLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(typeLabel, gridBagConstraints);

        typeField.setEnabled(false);
        typeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                typeFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(typeField, gridBagConstraints);

        attrLabel.setText("Default Attributes: ");
        attrLabel.setDisplayedMnemonic(65);
        attrLabel.setLabelFor(attrField);
        attrLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(attrLabel, gridBagConstraints);

        attrField.setEnabled(false);
        attrField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                attrFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(attrField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        panel.add(jSeparator1, gridBagConstraints);

        advancedCheck.setText("Advanced Settings");
        advancedCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedCheckActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 0);
        panel.add(advancedCheck, gridBagConstraints);

        netbeansLabel.setText("Netbeans Home: ");
        netbeansLabel.setDisplayedMnemonic(78);
        netbeansLabel.setLabelFor(netbeansField);
        netbeansLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(netbeansLabel, gridBagConstraints);

        netbeansField.setEnabled(false);
        netbeansField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                netbeansFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(netbeansField, gridBagConstraints);

        xtestLabel.setText("XTest Home: ");
        xtestLabel.setDisplayedMnemonic(88);
        xtestLabel.setLabelFor(xtestField);
        xtestLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(xtestLabel, gridBagConstraints);

        xtestField.setEnabled(false);
        xtestField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                xtestFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(xtestField, gridBagConstraints);

        sourceLabel.setText("Source Location: ");
        sourceLabel.setDisplayedMnemonic(83);
        sourceLabel.setLabelFor(sourceCombo);
        sourceLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(sourceLabel, gridBagConstraints);

        sourceCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ide", "src", "jar" }));
        sourceCombo.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(sourceCombo, gridBagConstraints);

        netbeansButton.setText("...");
        netbeansButton.setPreferredSize(new java.awt.Dimension(30, 20));
        netbeansButton.setMinimumSize(new java.awt.Dimension(30, 20));
        netbeansButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        panel.add(netbeansButton, gridBagConstraints);

        xtestButton.setText("...");
        xtestButton.setPreferredSize(new java.awt.Dimension(30, 20));
        xtestButton.setMinimumSize(new java.awt.Dimension(30, 20));
        xtestButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        panel.add(xtestButton, gridBagConstraints);

        jemmyLabel.setText("Jemmy Home: ");
        jemmyLabel.setDisplayedMnemonic(74);
        jemmyLabel.setLabelFor(jemmyField);
        jemmyLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(jemmyLabel, gridBagConstraints);

        jemmyField.setEnabled(false);
        jemmyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jemmyFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(jemmyField, gridBagConstraints);

        jemmyButton.setText("...");
        jemmyButton.setPreferredSize(new java.awt.Dimension(30, 20));
        jemmyButton.setMinimumSize(new java.awt.Dimension(30, 20));
        jemmyButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        panel.add(jemmyButton, gridBagConstraints);

        jellyLabel.setText("Jelly Home: ");
        jellyLabel.setDisplayedMnemonic(76);
        jellyLabel.setLabelFor(jellyField);
        jellyLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel.add(jellyLabel, gridBagConstraints);

        jellyField.setEnabled(false);
        jellyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jellyFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(jellyField, gridBagConstraints);

        jellyButton.setText("...");
        jellyButton.setPreferredSize(new java.awt.Dimension(30, 20));
        jellyButton.setMinimumSize(new java.awt.Dimension(30, 20));
        jellyButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        panel.add(jellyButton, gridBagConstraints);

        add(panel, "ok");

        stopLabel.setText("Test Workspace already exists in selected package.");
        stopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(stopLabel, "stop");

    }//GEN-END:initComponents

    private void jellyFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jellyFieldFocusGained
        jellyField.selectAll();
    }//GEN-LAST:event_jellyFieldFocusGained

    private void jemmyFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jemmyFieldFocusGained
        jemmyField.selectAll();
    }//GEN-LAST:event_jemmyFieldFocusGained

    private void xtestFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xtestFieldFocusGained
        xtestField.selectAll();
    }//GEN-LAST:event_xtestFieldFocusGained

    private void netbeansFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_netbeansFieldFocusGained
        netbeansField.selectAll();
    }//GEN-LAST:event_netbeansFieldFocusGained

    private void attrFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_attrFieldFocusGained
        attrField.selectAll();
    }//GEN-LAST:event_attrFieldFocusGained

    private void typeFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_typeFieldFocusGained
        typeField.selectAll();
    }//GEN-LAST:event_typeFieldFocusGained

    private void levelComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelComboActionPerformed
        updatePanel();
    }//GEN-LAST:event_levelComboActionPerformed

    private void advancedCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedCheckActionPerformed
        updatePanel();
    }//GEN-LAST:event_advancedCheckActionPerformed

    private void updatePanel() {
        boolean advanced=advancedCheck.isSelected();
        levelLabel.setEnabled(!advanced);
        levelCombo.setEnabled(!advanced);
        typeLabel.setEnabled(advanced);
        typeField.setEnabled(advanced);
        attrLabel.setEnabled(advanced);
        attrField.setEnabled(advanced);
        netbeansLabel.setEnabled(advanced);
        netbeansField.setEnabled(advanced);
        netbeansButton.setEnabled(advanced);
        xtestLabel.setEnabled(advanced);
        xtestField.setEnabled(advanced);
        xtestButton.setEnabled(advanced);
        jemmyLabel.setEnabled(advanced);
        jemmyField.setEnabled(advanced);
        jemmyButton.setEnabled(advanced);
        jellyLabel.setEnabled(advanced);
        jellyField.setEnabled(advanced);
        jellyButton.setEnabled(advanced);
        sourceLabel.setEnabled(advanced);
        sourceCombo.setEnabled(advanced);
        if (!advanced) {
            typeField.setText(type);
            attrField.setText(attr);
            sourceCombo.setSelectedItem(source);
             switch (levelCombo.getSelectedIndex()) {
                 case 0:netbeansField.setText(netbeansPath);
                        xtestField.setText(xtestPath);
                        jemmyField.setText(jemmyPath);
                        jellyField.setText(jellyPath);
                        break;
                 case 1:netbeansField.setText("../"+netbeansPath);
                        xtestField.setText("../"+xtestPath);
                        jemmyField.setText("../"+jemmyPath);
                        jellyField.setText("../"+jellyPath);
                        break;
                 case 2:netbeansField.setText("../../"+netbeansPath);
                        xtestField.setText("../../"+xtestPath);
                        jemmyField.setText("../../"+jemmyPath);
                        jellyField.setText("../../"+jellyPath);
                        break;
                 case 3:String home=System.getProperty("netbeans.home").replace('\\','/');
                        netbeansField.setText(home);
                        if (!new File(home+"/xtest-distribution").exists()) 
                            home=System.getProperty("netbeans.user").replace('\\','/');
                        xtestField.setText(home+"/xtest-distribution");
                        jemmyField.setText(home+"/lib/ext");
                        jellyField.setText(home+"/lib/ext");
                        break;
             }
        }
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener changeListener) {
    }    
    
    public java.awt.Component getComponent() {
        return this;
    }    
    
    public org.openide.util.HelpCtx getHelp() {
        return new HelpCtx(TestWorkspaceSettingsPanel.class);
    }
    
    public void readSettings(Object obj) {
        TemplateWizard wizard=(TemplateWizard)obj;
        DataFolder df=null;
        stop=true;
        try {
            df=wizard.getTargetFolder();
            stop=WizardIterator.detectBuildScript(df);
        } catch (Exception e) {}
        if (stop)
            ((CardLayout)getLayout()).show(this, "stop");
        else {
            ((CardLayout)getLayout()).show(this, "ok");
            levelCombo.setSelectedIndex(WizardIterator.detectWorkspaceLevel(df));
            String s;
            s=(String)wizard.getProperty(WizardIterator.TESTWORKSPACE_TYPE_PROPERTY);
            if (s!=null) type=s;
            s=(String)wizard.getProperty(WizardIterator.TESTWORKSPACE_ATTRIBUTES_PROPERTY);
            if (s!=null) attr=s;
            s=(String)wizard.getProperty(WizardIterator.TESTWORKSPACE_SOURCE_PROPERTY);
            if (s!=null) source=s;
            updatePanel();
        }
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener changeListener) {
    }
    
    public void storeSettings(Object obj) {
        TemplateWizard wizard=(TemplateWizard)obj;
        wizard.putProperty(WizardIterator.TESTWORKSPACE_NETBEANS_PROPERTY, netbeansField.getText());
        wizard.putProperty(WizardIterator.TESTWORKSPACE_XTEST_PROPERTY, xtestField.getText());
        wizard.putProperty(WizardIterator.TESTWORKSPACE_JEMMY_PROPERTY, jemmyField.getText());
        wizard.putProperty(WizardIterator.TESTWORKSPACE_JELLY_PROPERTY, jellyField.getText());
        wizard.putProperty(WizardIterator.TESTWORKSPACE_TYPE_PROPERTY, typeField.getText());
        wizard.putProperty(WizardIterator.TESTWORKSPACE_ATTRIBUTES_PROPERTY, attrField.getText());
        wizard.putProperty(WizardIterator.TESTWORKSPACE_SOURCE_PROPERTY, sourceCombo.getSelectedItem());
    }

    public boolean isValid() {
        return !stop;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JLabel stopLabel;
    private javax.swing.JButton xtestButton;
    private javax.swing.JButton jemmyButton;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JLabel jemmyLabel;
    private javax.swing.JLabel xtestLabel;
    private javax.swing.JTextField jemmyField;
    private javax.swing.JTextField xtestField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton netbeansButton;
    private javax.swing.JCheckBox advancedCheck;
    private javax.swing.JTextField typeField;
    private javax.swing.JPanel panel;
    private javax.swing.JLabel jellyLabel;
    private javax.swing.JButton jellyButton;
    private javax.swing.JLabel attrLabel;
    private javax.swing.JTextField jellyField;
    private javax.swing.JTextField attrField;
    private javax.swing.JComboBox sourceCombo;
    private javax.swing.JLabel netbeansLabel;
    private javax.swing.JComboBox levelCombo;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField netbeansField;
    // End of variables declaration//GEN-END:variables
    
}
