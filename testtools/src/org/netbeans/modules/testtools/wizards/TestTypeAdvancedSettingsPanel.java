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
 * TestTypeAdvancedSettingsPanel.java
 *
 * Created on April 10, 2002, 1:44 PM
 */

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import javax.swing.event.ChangeListener;
import org.openide.loaders.TemplateWizard;
import java.awt.CardLayout;
import java.io.File;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import java.util.StringTokenizer;

/**
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 */
public class TestTypeAdvancedSettingsPanel extends javax.swing.JPanel implements WizardDescriptor.Panel {
    
    private File baseDir=null;
    private String netbeansHome=null;
    
    /** Creates new form TestTypePanel */
    public TestTypeAdvancedSettingsPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        excludesLabel = new javax.swing.JLabel();
        excludesField = new javax.swing.JTextField();
        compileLabel = new javax.swing.JLabel();
        compileField = new javax.swing.JTextField();
        compileButton = new javax.swing.JButton();
        executeLabel = new javax.swing.JLabel();
        executeField = new javax.swing.JTextField();
        executeButton = new javax.swing.JButton();
        jvmLabel = new javax.swing.JLabel();
        jvmField = new javax.swing.JTextField();
        jemmyLabel = new javax.swing.JLabel();
        jemmyField = new javax.swing.JTextField();
        jemmyButton = new javax.swing.JButton();
        jellyLabel = new javax.swing.JLabel();
        jellyField = new javax.swing.JTextField();
        jellyButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        excludesLabel.setText("Compilation Exclude Pattern: ");
        excludesLabel.setDisplayedMnemonic(88);
        excludesLabel.setLabelFor(excludesField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(excludesLabel, gridBagConstraints);

        excludesField.setText("**/data/**");
        excludesField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                excludesFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(excludesField, gridBagConstraints);

        compileLabel.setText("Compliation Class Path: ");
        compileLabel.setDisplayedMnemonic(67);
        compileLabel.setLabelFor(compileField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(compileLabel, gridBagConstraints);

        compileField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                compileFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(compileField, gridBagConstraints);

        compileButton.setText("...");
        compileButton.setPreferredSize(new java.awt.Dimension(30, 20));
        compileButton.setMinimumSize(new java.awt.Dimension(30, 20));
        compileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(compileButton, gridBagConstraints);

        executeLabel.setText("Execution Extra Jars: ");
        executeLabel.setDisplayedMnemonic(69);
        executeLabel.setLabelFor(executeField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(executeLabel, gridBagConstraints);

        executeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                executeFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(executeField, gridBagConstraints);

        executeButton.setText("...");
        executeButton.setPreferredSize(new java.awt.Dimension(30, 20));
        executeButton.setMinimumSize(new java.awt.Dimension(30, 20));
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(executeButton, gridBagConstraints);

        jvmLabel.setText("Command Line Suffix: ");
        jvmLabel.setDisplayedMnemonic(83);
        jvmLabel.setLabelFor(jvmField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jvmLabel, gridBagConstraints);

        jvmField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jvmFieldFocusGained(evt);
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
        add(jvmField, gridBagConstraints);

        jemmyLabel.setText("Jemmy Jar Home: ");
        jemmyLabel.setDisplayedMnemonic(77);
        jemmyLabel.setLabelFor(jemmyField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(jemmyLabel, gridBagConstraints);

        jemmyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jemmyFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jemmyField, gridBagConstraints);

        jemmyButton.setText("...");
        jemmyButton.setPreferredSize(new java.awt.Dimension(30, 20));
        jemmyButton.setMinimumSize(new java.awt.Dimension(30, 20));
        jemmyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jemmyButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(jemmyButton, gridBagConstraints);

        jellyLabel.setText("Jelly Jar Home: ");
        jellyLabel.setDisplayedMnemonic(76);
        jellyLabel.setLabelFor(jellyField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(jellyLabel, gridBagConstraints);

        jellyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jellyFieldFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jellyField, gridBagConstraints);

        jellyButton.setText("...");
        jellyButton.setPreferredSize(new java.awt.Dimension(30, 20));
        jellyButton.setMinimumSize(new java.awt.Dimension(30, 20));
        jellyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jellyButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(jellyButton, gridBagConstraints);

    }//GEN-END:initComponents

    private String substitutePath(File file, File dir, String subst) {
        try {
            if (!(dir.exists() && file.exists()))
                return null;
            String d=dir.getCanonicalPath()+file.separator;
            String f=file.getCanonicalPath();
            if (f.startsWith(d))
                return subst+'/'+f.substring(d.length()).replace('\\','/');
        } catch (Exception e) {}
        return null;
    }
    
    private String add(String path, File elem) {
        String file=null;
        if (netbeansHome!=null)
            file=substitutePath(elem, new File(baseDir, netbeansHome), "${netbeans.home}");
        if (file==null && netbeansHome!=null)
            file=substitutePath(elem, new File(netbeansHome), "${netbeans.home}");
        if (file==null)
            file=substitutePath(elem, new File(System.getProperty("netbeans.home")), "${netbeans.home}");
        if (file==null)
            file=substitutePath(elem, baseDir, "..");
        if (file==null)
            file=elem.getAbsolutePath();
        if (path.length()==0) 
            return file;
        StringTokenizer tok=new StringTokenizer(path, ":;");
        while (tok.hasMoreTokens())
            if (file.equals(tok.nextToken())) return path;
        return path+';'+file;
    }    
    
    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed
        File elem=WizardIterator.showFileChooser(this, "Select Extra Jar", false, true);
        if (elem!=null) {
            executeField.setText(add(executeField.getText(), elem));
        }
    }//GEN-LAST:event_executeButtonActionPerformed

    private void compileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileButtonActionPerformed
        File jar=WizardIterator.showFileChooser(this, "Select Class Path Element", true, true);
        if (jar!=null) {
            compileField.setText(add(compileField.getText(), jar));
        }
    }//GEN-LAST:event_compileButtonActionPerformed

    private void jellyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jellyButtonActionPerformed
        File home=WizardIterator.showFileChooser(this, "Select Jelly Home Directory", true, false);
        if (home!=null) 
            jellyField.setText(home.getAbsolutePath());
    }//GEN-LAST:event_jellyButtonActionPerformed

    private void jemmyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jemmyButtonActionPerformed
        File home=WizardIterator.showFileChooser(this, "Select Jemmy Home Directory", true, false);
        if (home!=null) 
            jemmyField.setText(home.getAbsolutePath());
    }//GEN-LAST:event_jemmyButtonActionPerformed

    private void jellyFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jellyFieldFocusGained
        jellyField.selectAll();
    }//GEN-LAST:event_jellyFieldFocusGained

    private void jemmyFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jemmyFieldFocusGained
        jemmyField.selectAll();
    }//GEN-LAST:event_jemmyFieldFocusGained

    private void jvmFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jvmFieldFocusGained
        jvmField.selectAll();
    }//GEN-LAST:event_jvmFieldFocusGained

    private void executeFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_executeFieldFocusGained
        executeField.selectAll();
    }//GEN-LAST:event_executeFieldFocusGained

    private void compileFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_compileFieldFocusGained
        compileField.selectAll();
    }//GEN-LAST:event_compileFieldFocusGained

    private void excludesFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_excludesFieldFocusGained
        excludesField.selectAll();
    }//GEN-LAST:event_excludesFieldFocusGained

    public void addChangeListener(javax.swing.event.ChangeListener l) {
    }    
    
    public java.awt.Component getComponent() {
        return this;
    }    
    
    public org.openide.util.HelpCtx getHelp() {
        return new HelpCtx(TestTypeAdvancedSettingsPanel.class);
    }
    
    public void readSettings(Object obj) {
        WizardSettings set=WizardSettings.get(obj);
        if (set.typeJVMSuffix!=null)
            jvmField.setText(set.typeJVMSuffix);
        if (set.typeExcludes!=null)
            compileField.setText(set.typeExcludes);
        if (set.typeCompPath!=null)
            compileField.setText(set.typeCompPath);
        if (set.typeExecPath!=null)
            executeField.setText(set.typeExecPath);
        if (set.typeJemmyHome!=null)
            jemmyField.setText(set.typeJemmyHome);
        if (set.typeJellyHome!=null)
            jellyField.setText(set.typeJellyHome);
        TemplateWizard wizard=(TemplateWizard)obj;
        if (baseDir==null) try {
            baseDir=FileUtil.toFile(wizard.getTargetFolder().getPrimaryFile());
            if (set.startFromWorkspace) {
                netbeansHome=set.netbeansHome;
            } else {
                baseDir=baseDir.getParentFile();
                XMLDocument doc=new XMLDocument(DataObject.find(wizard.getTargetFolder().getPrimaryFile().getFileObject("build","xml")));
                netbeansHome=doc.getProperty("netbeans.home","location");
            }
        } catch (Exception e) {}
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener l) {
    }
    
    public void storeSettings(Object obj) {
        WizardSettings set=WizardSettings.get(obj);
        set.typeJVMSuffix=jvmField.getText();
        set.typeExcludes=excludesField.getText();
        set.typeCompPath=compileField.getText();
        set.typeExecPath=executeField.getText();
        set.typeJemmyHome=jemmyField.getText();
        set.typeJellyHome=jellyField.getText();
    }

    public boolean isValid() {
        return true;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jemmyButton;
    private javax.swing.JLabel executeLabel;
    private javax.swing.JButton compileButton;
    private javax.swing.JLabel jemmyLabel;
    private javax.swing.JLabel jvmLabel;
    private javax.swing.JTextField executeField;
    private javax.swing.JTextField jemmyField;
    private javax.swing.JTextField jvmField;
    private javax.swing.JLabel compileLabel;
    private javax.swing.JLabel jellyLabel;
    private javax.swing.JButton jellyButton;
    private javax.swing.JButton executeButton;
    private javax.swing.JTextField compileField;
    private javax.swing.JTextField jellyField;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel excludesLabel;
    private javax.swing.JTextField excludesField;
    // End of variables declaration//GEN-END:variables
    
}
