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
/*
 * MailPropertyVisualPanel.java
 *
 * Created on December 17, 2002, 1:19 PM
 */

package org.netbeans.modules.j2ee.sun.ide.sunresources.wizards;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.ListSelectionModel;
import org.openide.util.NbBundle;
import java.util.ResourceBundle;

import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroup;
import org.netbeans.modules.j2ee.sun.sunresources.beans.WizardConstants;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldHelper;

/**
 *
 * @author  Jennifer Chou
 */
public class MailPropertyVisualPanel extends javax.swing.JPanel implements WizardConstants, TableModelListener {
    
    private final MailPropertyPanel panel;
    private ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.j2ee.sun.ide.sunresources.wizards.Bundle"); //NOI18N
    private FieldGroup generalGroup;
    private FieldGroup propertiesGroup;
    private boolean inProcessingTableChange = false;
    private PropertiesTableModel tableModel;
    private ResourceConfigHelper helper;
    private javax.swing.table.TableColumn propNameColumn;
    private javax.swing.table.TableColumn propValueColumn;
        
    /** Creates new form MailPropertyVisualPanel */
    public MailPropertyVisualPanel(MailPropertyPanel panel) {
        this.panel = panel;
        this.helper = panel.getHelper();
        this.generalGroup = panel.getFieldGroup(__General);  
        this.propertiesGroup = panel.getFieldGroup(__Properties);  

        tableModel = new PropertiesTableModel(this.helper.getData());
        initComponents();
        setPropTableCellEditor();
        tableModel.addTableModelListener(this);
        propertyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propertyTable.setRowSelectionAllowed(true);
        
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(MailPropertyVisualPanel.class, "TITLE_MailPropertyPanel"));  //NOI18N
      
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        descriptionTextArea = new javax.swing.JTextArea();
        propertyInfo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        propertyTable = new javax.swing.JTable();
        buttonsPane = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(627, 305));
        setPreferredSize(new java.awt.Dimension(627, 305));
        getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("TITLE_MailPropertyPanel"));
        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("MailPropertyPanel_Description"));
        descriptionTextArea.setBackground(new java.awt.Color(204, 204, 204));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setFont(javax.swing.UIManager.getFont("Label.font"));
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setText(NbBundle.getMessage(MailPropertyVisualPanel.class, "MailPropertyPanel_Description", this.helper.getData().getString(__JndiName)));
        descriptionTextArea.setDisabledTextColor(javax.swing.UIManager.getColor("Label.foreground"));
        descriptionTextArea.setMinimumSize(new java.awt.Dimension(500, 17));
        descriptionTextArea.setPreferredSize(new java.awt.Dimension(500, 17));
        descriptionTextArea.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 11);
        add(descriptionTextArea, gridBagConstraints);
        descriptionTextArea.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("MailPropertyPanel_Description"));
        descriptionTextArea.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("MailPropertyPanel_Description"));

        propertyInfo.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_properties_Mnemonic").charAt(0));
        propertyInfo.setLabelFor(propertyTable);
        propertyInfo.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_properties"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        add(propertyInfo, gridBagConstraints);
        propertyInfo.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_properties"));
        propertyInfo.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("ACS_propTableMail_A11yDesc"));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(453, 17));
        propertyTable.setModel(tableModel);
        jScrollPane1.setViewportView(propertyTable);
        propertyTable.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_AddProperty"));
        propertyTable.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("ACS_propTableMail_A11yDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 10, 11);
        add(jScrollPane1, gridBagConstraints);
        jScrollPane1.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_properties"));
        jScrollPane1.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("ACS_propTableMail_A11yDesc"));

        buttonsPane.setLayout(new java.awt.GridBagLayout());

        addButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_Add_Mnemonic").charAt(0));
        addButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_Add"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        buttonsPane.add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("ACS_AddButtonA11yDesc"));

        removeButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_Remove_Mnemonic").charAt(0));
        removeButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_Remove"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        buttonsPane.add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("ACS_RemoveButtonA11yDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 150;
        add(buttonsPane, gridBagConstraints);
        buttonsPane.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_properties"));
        buttonsPane.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("ACS_propTableMail_A11yDesc"));

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        //Fix for bug#4958730 - value overwrites into next row
        propertyTable.editingStopped(new ChangeEvent (this));
        ResourceConfigData data = this.helper.getData();
        data.addProperty(new NameValuePair());
        tableModel.fireTableDataChanged();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        int selectedRow = propertyTable.getSelectedRow();
        if (selectedRow != -1) {
            //Fix for bug#4958730 - value overwrites into next row
            propertyTable.editingStopped(new ChangeEvent (this));
            this.helper.getData().removeProperty(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_removeButtonActionPerformed
    
    public void tableChanged(TableModelEvent evt) {
        if (!inProcessingTableChange) {
            setPropTableCellEditor();
            inProcessingTableChange = true;
            this.panel.fireChangeEvent();
        }
        /*else {
           inProcessingTableChange = false;
        }*/
        
    }

    public void setPropTableCellEditor() {
        javax.swing.JComboBox propNameComboBox = new javax.swing.JComboBox();
        String[] remainingProperties = FieldHelper.getRemainingFieldNames(propertiesGroup, this.helper.getData().getPropertyNames());

        for (int i = 0; i < remainingProperties.length; i++) 
            propNameComboBox.addItem(remainingProperties[i]);
        propNameComboBox.setEditable(true);
        this.propNameColumn = propertyTable.getColumnModel().getColumn(0);
        propNameColumn.setCellEditor(new javax.swing.DefaultCellEditor(propNameComboBox));
        this.propValueColumn = propertyTable.getColumnModel().getColumn(1);
        
        javax.swing.DefaultCellEditor editor = new javax.swing.DefaultCellEditor(new javax.swing.JTextField());
        editor.setClickCountToStart(1);
        propValueColumn.setCellEditor(editor);
    }    
   
    public void refreshFields() {
        ResourceConfigData data = this.helper.getData();
        ((PropertiesTableModel)propertyTable.getModel()).setData(this.helper.getData());
        descriptionTextArea.setText(NbBundle.getMessage(MailPropertyVisualPanel.class, "MailPropertyPanel_Description", data.getString(__JndiName))); //NOI18N
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonsPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel propertyInfo;
    private javax.swing.JTable propertyTable;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
     public void setInitialFocus(){
         new setFocus(addButton);
     }  
}
