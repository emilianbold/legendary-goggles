/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.customizer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.SpecificationVersion;
import org.openide.util.NbBundle;

/**
 * Represents <em>Sources</em> panel in Netbeans Module customizer.
 *
 * @author mkrauskopf
 */
final class CustomizerSources extends NbPropertyPanel.Single {
    
    private boolean srcLevelValueBeingUpdated;
    private final CustomizerCompiling compilingPanel;
    
    CustomizerSources(final SingleModuleProperties props, CustomizerCompiling compilingPanel) {
        super(props, CustomizerSources.class);
        this.compilingPanel = compilingPanel;
        initComponents();
        initAccessibility();
        refresh();
        srcLevelValue.addActionListener(new ActionListener() { // #66278
            public void actionPerformed(ActionEvent e) {
                if (srcLevelValueBeingUpdated) {
                    return;
                }
                final String oldLevel = getProperty(SingleModuleProperties.JAVAC_SOURCES);
                final String newLevel = (String) srcLevelValue.getSelectedItem();
                SpecificationVersion jdk5 = new SpecificationVersion("1.5"); // NOI18N
                if (new SpecificationVersion(oldLevel).compareTo(jdk5) < 0 && new SpecificationVersion(newLevel).compareTo(jdk5) >= 0) {
                    EventQueue.invokeLater(new Runnable() { // wait for combo to close, at least
                        public void run() {
                            DialogDescriptor d = new DialogDescriptor(
                                    getMessage("CustomizerSources.text.enable_lint_unchecked"),
                                    getMessage("CustomizerSources.title.enable_lint_unchecked"));
                            d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
                            d.setModal(true);
                            JButton enable = new JButton(getMessage("CustomizerSources.button.enable_lint_unchecked"));
                            enable.setDefaultCapable(true);
                            d.setOptions(new Object[] {
                                enable,
                                new JButton(getMessage("CustomizerSources.button.skip_lint_unchecked")),
                            });
                            if (!DialogDisplayer.getDefault().notify(d).equals(enable)) {
                                return;
                            }
                            String options = getProperty(SingleModuleProperties.JAVAC_COMPILERARGS);
                            String added = "-Xlint:unchecked"; // NOI18N
                            if (options == null || options.length() == 0) {
                                options = added;
                            } else {
                                options = options + " " + added; // NOI18N
                            }
                            setProperty(SingleModuleProperties.JAVAC_COMPILERARGS, options);
                            // XXX don't know of any cleaner way to do this; setProperty fires no changes
                            // that CustomizerCompiling could listen to; refreshProperties is protected,
                            // and even if made accessible, refreshes JAVAC_SOURCES too and messes up combo!
                            CustomizerSources.this.compilingPanel.refresh();
                        }
                    });
                }
            }
        });
    }
    
    void refresh() {
        if (getProperties().getSuiteDirectoryPath() == null) {
            moduleSuite.setVisible(false);
            moduleSuiteValue.setVisible(false);
        } else {
            UIUtil.setText(moduleSuiteValue, getProperties().getSuiteDirectoryPath());
        }
        assert !srcLevelValueBeingUpdated;
        srcLevelValueBeingUpdated = true;
        try {
            srcLevelValue.removeAllItems();
            for (int i = 0; i < SingleModuleProperties.SOURCE_LEVELS.length; i++) {
                srcLevelValue.addItem(SingleModuleProperties.SOURCE_LEVELS[i]);
            }
            srcLevelValue.setSelectedItem(getProperty(SingleModuleProperties.JAVAC_SOURCES));
        } finally {
            srcLevelValueBeingUpdated = false;
        }
        UIUtil.setText(prjFolderValue, getProperties().getProjectDirectory());
    }
    
    public void store() {
        setProperty(SingleModuleProperties.JAVAC_SOURCES,
                (String) srcLevelValue.getSelectedItem());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        prjFolder = new javax.swing.JLabel();
        srcLevel = new javax.swing.JLabel();
        srcLevelValue = new javax.swing.JComboBox();
        filler = new javax.swing.JLabel();
        prjFolderValue = new javax.swing.JTextField();
        moduleSuite = new javax.swing.JLabel();
        moduleSuiteValue = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        prjFolder.setLabelFor(prjFolderValue);
        org.openide.awt.Mnemonics.setLocalizedText(prjFolder, org.openide.util.NbBundle.getMessage(CustomizerSources.class, "LBL_ProjectFolder"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(prjFolder, gridBagConstraints);

        srcLevel.setLabelFor(srcLevelValue);
        org.openide.awt.Mnemonics.setLocalizedText(srcLevel, org.openide.util.NbBundle.getMessage(CustomizerSources.class, "LBL_SourceLevel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 12);
        add(srcLevel, gridBagConstraints);

        srcLevelValue.setPrototypeDisplayValue("mmm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        add(srcLevelValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

        prjFolderValue.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(prjFolderValue, gridBagConstraints);

        moduleSuite.setLabelFor(moduleSuiteValue);
        org.openide.awt.Mnemonics.setLocalizedText(moduleSuite, org.openide.util.NbBundle.getMessage(CustomizerSources.class, "LBL_ModeleSuite"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 12);
        add(moduleSuite, gridBagConstraints);

        moduleSuiteValue.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(moduleSuiteValue, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filler;
    private javax.swing.JLabel moduleSuite;
    private javax.swing.JTextField moduleSuiteValue;
    private javax.swing.JLabel prjFolder;
    private javax.swing.JTextField prjFolderValue;
    private javax.swing.JLabel srcLevel;
    private javax.swing.JComboBox srcLevelValue;
    // End of variables declaration//GEN-END:variables
    
    private static String getMessage(String key) {
        return NbBundle.getMessage(CustomizerSources.class, key);
    }
    
    private void initAccessibility() {
        srcLevelValue.getAccessibleContext().setAccessibleDescription(getMessage("ACS_SrcLevelValue"));
        moduleSuiteValue.getAccessibleContext().setAccessibleDescription(getMessage("ACS_ModuleSuiteValue"));
        prjFolderValue.getAccessibleContext().setAccessibleDescription(getMessage("ACS_PrjFolderValue"));
    }
    
}
