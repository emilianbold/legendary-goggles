/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview.ui;

import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import javax.swing.*;

/**
 * @author pfiala
 */
public class EjbImplementationAndInterfacesForm extends SectionInnerPanel {

    /**
     * Creates new form BeanForm
     */
    public EjbImplementationAndInterfacesForm(SectionNodeView sectionNodeView) {
        super(sectionNodeView);
        initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        beanClassTextField = new javax.swing.JTextField();
        localInterfaceCheckBox = new javax.swing.JCheckBox();
        localComponentTextField = new javax.swing.JTextField();
        localHomeTextField = new javax.swing.JTextField();
        remoteInterfaceCheckBox = new javax.swing.JCheckBox();
        remoteComponentTextField = new javax.swing.JTextField();
        remoteHomeTextField = new javax.swing.JTextField();
        moveClassButton = new javax.swing.JButton();
        renameClassButton = new javax.swing.JButton();
        spacerLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setOpaque(false);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_BeanClass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_LocalInterface"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_Component"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel3, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_Home"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel4, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_RemoteInterface"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel5, gridBagConstraints);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_Component"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel6, gridBagConstraints);

        jLabel7.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_Home"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jLabel7, gridBagConstraints);

        beanClassTextField.setColumns(35);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(beanClassTextField, gridBagConstraints);

        localInterfaceCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(localInterfaceCheckBox, gridBagConstraints);

        localComponentTextField.setColumns(35);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(localComponentTextField, gridBagConstraints);

        localHomeTextField.setColumns(35);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(localHomeTextField, gridBagConstraints);

        remoteInterfaceCheckBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(remoteInterfaceCheckBox, gridBagConstraints);

        remoteComponentTextField.setColumns(35);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(remoteComponentTextField, gridBagConstraints);

        remoteHomeTextField.setColumns(35);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(remoteHomeTextField, gridBagConstraints);

        moveClassButton.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_MoveClass"));
        moveClassButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(moveClassButton, gridBagConstraints);

        renameClassButton.setText(org.openide.util.NbBundle.getMessage(EjbImplementationAndInterfacesForm.class, "LBL_RenameClass"));
        renameClassButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(renameClassButton, gridBagConstraints);

        spacerLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(spacerLabel, gridBagConstraints);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField beanClassTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField localComponentTextField;
    private javax.swing.JTextField localHomeTextField;
    private javax.swing.JCheckBox localInterfaceCheckBox;
    private javax.swing.JButton moveClassButton;
    private javax.swing.JTextField remoteComponentTextField;
    private javax.swing.JTextField remoteHomeTextField;
    private javax.swing.JCheckBox remoteInterfaceCheckBox;
    private javax.swing.JButton renameClassButton;
    private javax.swing.JLabel spacerLabel;
    // End of variables declaration//GEN-END:variables

    public JTextField getBeanClassTextField() {
        return beanClassTextField;
    }

    public JTextField getLocalComponentTextField() {
        return localComponentTextField;
    }

    public JTextField getLocalHomeTextField() {
        return localHomeTextField;
    }

    public JCheckBox getLocalInterfaceCheckBox() {
        return localInterfaceCheckBox;
    }

    public JTextField getRemoteComponentTextField() {
        return remoteComponentTextField;
    }

    public JTextField getRemoteHomeTextField() {
        return remoteHomeTextField;
    }

    public JCheckBox getRemoteInterfaceCheckBox() {
        return remoteInterfaceCheckBox;
    }

    public JComponent getErrorComponent(String errorId) {
        return null;
    }

    public void setValue(JComponent source, Object value) {

    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {

    }

    public JButton getMoveClassButton() {
        return moveClassButton;
    }

    public JButton getRenameClassButton() {
        return renameClassButton;
    }
}
