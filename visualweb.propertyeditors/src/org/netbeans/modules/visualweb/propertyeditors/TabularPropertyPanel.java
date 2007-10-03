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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.visualweb.propertyeditors;

import java.awt.Color;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 * A custom property editor panel for editing a property that consists of
 * tabular data.
 *
 * @see com.sun.rave.propertyeditors.TabularPropertyEditor
 * @see com.sun.rave.propertyeditors.TabularPropertyModel
 * @author gjmurphy
 */

public class TabularPropertyPanel extends PropertyPanelBase implements TableModelListener {

    static ResourceBundle bundle =
        ResourceBundle.getBundle(TabularPropertyPanel.class.getPackage().getName() + ".Bundle"); //NOI18N

    private TableModelSupport tableModelSupport;

    /** Creates new form ItemsEditorPanel */
    public TabularPropertyPanel(TabularPropertyModel tableModel, PropertyEditorBase editor) {
        super(editor);
        tableModel.addTableModelListener(this);
        this.tableModelSupport = new TableModelSupport(tableModel);
        initComponents();

        // Have the first row selected by default
        if( tableModel.getRowCount() > 0 )
            dataTable.changeSelection(0,0,false,false);
    }

    public Object getPropertyValue() throws IllegalStateException {
        return this.tableModelSupport.getValue();
    }

    /**
     * Called whenever the tabular property model changes itself.
     */
    public void tableChanged(TableModelEvent event) {
        int f = event.getFirstRow();
        int l = event.getLastRow();
        if (f == l) {
            this.tableModelSupport.fireTableCellUpdated(f, event.getColumn());
        } else if (event.getType() == TableModelEvent.DELETE) {
            this.tableModelSupport.fireTableRowsDeleted(f, l);
        } else if (event.getType() == TableModelEvent.INSERT) {
            this.tableModelSupport.fireTableRowsInserted(f, l);
        } else {
            this.tableModelSupport.fireTableRowsUpdated(f, l);
        }

    }

    /** This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        helpLabel = new javax.swing.JLabel();
        dataPane = new javax.swing.JScrollPane();
        dataTable = new JTable(tableModelSupport);

        // Only one row selectable at a time
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create a default cell editor for String values that forces a stop
        // editing event whenever focus is lost.
        JTextField textField = new JTextField();
        final TableCellEditor cellEditor = new CellEditor(dataTable, textField);
        this.dataTable.setDefaultEditor(String.class, cellEditor);
        this.dataTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //NOI18N

        // Single click to start editing
        ((DefaultCellEditor)dataTable.getDefaultEditor(String.class)).setClickCountToStart( 1 );

        // Create a default cell renderer for String values that consistently renders
        // background colors.
        dataTable.setDefaultRenderer(String.class, new HomogonousCellRenderer());

        // Create a custom renderer for column headers, with background color set to
        // match NetBeans table header background color
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        renderer.setBackground(new java.awt.Color(212, 208, 200));
        dataTable.getTableHeader().setDefaultRenderer(renderer);
        buttonsPanel = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        helpLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/visualweb/propertyeditors/Bundle").getString("TabularPropertyPanel.helpLabel").charAt(0));
        helpLabel.setLabelFor(dataTable);
        helpLabel.setText(bundle.getString("TablePropertyPanel.help")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 10, 10);
        add(helpLabel, gridBagConstraints);
        helpLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TablePropertyPanel.help.desc")); // NOI18N

        dataPane.setEnabled(false);

        dataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataTable.setTableHeader(dataTable.getTableHeader());
        dataPane.setViewportView(dataTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        add(dataPane, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.GridLayout(4, 1, 0, 5));

        newButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/visualweb/propertyeditors/Bundle").getString("TabularPropertyPanel").charAt(0));
        newButton.setText(bundle.getString("SelectOneDomainPanel.button.new")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newActionPerformed(evt);
            }
        });
        buttonsPanel.add(newButton);
        newButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TabularPropertyPanel.newButton.desc")); // NOI18N

        deleteButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/visualweb/propertyeditors/Bundle").getString("TabularPropertyPanel.deleteButton").charAt(0));
        deleteButton.setText(bundle.getString("SelectOneDomainPanel.button.delete")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        buttonsPanel.add(deleteButton);
        deleteButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TabularPropertyPanel.deleteButton.desc")); // NOI18N

        upButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/visualweb/propertyeditors/Bundle").getString("TabularPropertyPanel.upButton").charAt(0));
        upButton.setText(bundle.getString("SelectOneDomainPanel.button.up")); // NOI18N
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });
        buttonsPanel.add(upButton);
        upButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TabularPropertyPanel.upButton.desc")); // NOI18N

        downButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/visualweb/propertyeditors/Bundle").getString("TabularPropertyPanel.downButton").charAt(0));
        downButton.setText(bundle.getString("SelectOneDomainPanel.button.down")); // NOI18N
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downActionPerformed(evt);
            }
        });
        buttonsPanel.add(downButton);
        downButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TabularPropertyPanel.downButton.desc")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 10);
        add(buttonsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * When focus moves from table to a button, make sure that editor for string
     * cell is done editing (if it was in edit mode), to avoid problems when rows
     * are moved or added, and to make sure any edited data is saved in the 
     * correct row.
     */
    private void downActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downActionPerformed

        TableModelSupport tableModel = (TableModelSupport)dataTable.getModel();
        if (dataTable.getSelectedRowCount() > 0) {
            int[] selectedRows = dataTable.getSelectedRows();
            if (selectedRows[0] < dataTable.getRowCount() - 1 && tableModel.canMoveRow(selectedRows[0], selectedRows[0] + 1)) {
                tableModel.moveRow(selectedRows[0], selectedRows[0] + 1);
                dataTable.setModel(tableModel);
                dataTable.changeSelection(selectedRows[0] + 1, 0, false, false);
            }
        }
    }//GEN-LAST:event_downActionPerformed

    private void upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upActionPerformed
        TableModelSupport tableModel = (TableModelSupport)dataTable.getModel();
        if (dataTable.getSelectedRowCount() > 0) {
            int[] selectedRows = dataTable.getSelectedRows();
            if (selectedRows[0] > 0 && tableModel.canMoveRow(selectedRows[0], selectedRows[0] - 1)) {
                tableModel.moveRow(selectedRows[0], selectedRows[0] - 1);
                dataTable.setModel(tableModel);
                dataTable.changeSelection(selectedRows[0] - 1, 0, false, false);
            }
        }
    }//GEN-LAST:event_upActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        TableModelSupport tableModel = (TableModelSupport)dataTable.getModel();
        int numRows = tableModel.getRowCount();
       
        if (dataTable.getSelectedRowCount() > 0) {
            int[] selectedRows = dataTable.getSelectedRows();
            boolean lastRowRemoved = (selectedRows[0]==(numRows-1)) ? true : false;
            if (tableModel.canRemoveRow(selectedRows[0])) {
                tableModel.removeRow(selectedRows[0]);
                dataTable.setModel(tableModel);
                
                // If this is not the last row we removed, have the next row selected
                if( tableModel.getRowCount() == 0 ) 
                    return;
                else {
                    if( lastRowRemoved ) {
                        // Have the last row selected again
                        dataTable.changeSelection(tableModel.getRowCount()-1, 0, false, false);
                    } else {
                        // Have the same row index selected again
                        dataTable.changeSelection(selectedRows [0], 0, false, false);
                    }
                }
            }
        }
    }//GEN-LAST:event_deleteActionPerformed

    private void newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newActionPerformed
        TableModelSupport tableModel = (TableModelSupport)dataTable.getModel();
        if (tableModel.canAddRow()) {
            tableModel.addRow();
            dataTable.setModel(tableModel);
            dataTable.changeSelection(tableModel.getRowCount() - 1, 0, false, false);
            dataTable.requestFocusInWindow();
            
            // Let's not call this. This makes the new row acting very weird. -dongmei
            //dataTable.editCellAt(tableModel.getRowCount() - 1, 0);
        }
    }//GEN-LAST:event_newActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JScrollPane dataPane;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downButton;
    private javax.swing.JLabel helpLabel;
    private javax.swing.JButton newButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
    
    static class TableModelSupport extends AbstractTableModel {
        
        TabularPropertyModel tableModel;
        
        TableModelSupport(TabularPropertyModel tableModel) {
            this.tableModel = tableModel;
        }
        
        public Object getValue() {
            return this.tableModel.getValue();
        }

        public Class getColumnClass(int columnIndex) {
            return tableModel.getColumnClass(columnIndex);
        }

        public String getColumnName(int columnIndex) {
            return tableModel.getColumnName(columnIndex);
        }

        public int getColumnCount() {
            return tableModel.getColumnCount();
        }

        public int getRowCount() {
            return tableModel.getRowCount();
        }
        
        public boolean canRemoveRow(int index) {
            return tableModel.canRemoveRow(index);
        }

        public boolean removeRow(int index) {
            if (tableModel.removeRow(index)) {
                this.fireTableRowsDeleted(index, index);
                return true;
            }
            return false;
        }
        
        public boolean removeAllRows() {
            int numRows = tableModel.getRowCount();
            if (tableModel.removeAllRows()) { 
                this.fireTableRowsDeleted(0, numRows-1);
                return true;
            }
            return false;
        }

        public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
            tableModel.setValueAt(newValue, rowIndex, columnIndex);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return tableModel.getValueAt(rowIndex, columnIndex);
        }

        public boolean canAddRow() {
            return tableModel.canAddRow();
        }

        public boolean addRow() {
            if (tableModel.addRow()) {
                this.fireTableRowsInserted(getRowCount(), getRowCount());
                return true;
            }
            return false;
        }

        public boolean canMoveRow(int indexFrom, int indexTo) {
            return tableModel.canMoveRow(indexFrom, indexTo);
        }

        public boolean moveRow(int indexFrom, int indexTo) {
            if (tableModel.moveRow(indexFrom, indexTo)) {
                this.fireTableDataChanged();
                return true;
            }
            return false;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return tableModel.isCellEditable(rowIndex, columnIndex);
        }
        
    }
    
    /**
     * A delegating renderer class that consistently sets the background color
     * of cells to reflect "selected" and "unselected" states.
     */
    class HomogonousCellRenderer extends DefaultTableCellRenderer {
        
        Color SELECTION_BACKGROUND =
                UIManager.getDefaults().getColor("TextField.selectionBackground");
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table,  value, isSelected, hasFocus, row, column);
            if (isSelected) 
                c.setBackground(SELECTION_BACKGROUND);
            else 
                c.setBackground(Color.WHITE);
            return c;
        }
    }
    
    /**
     * A JTextField cell editor by default all the text is selected
     */
    class CellEditor extends DefaultCellEditor  {
        
        private JTable table;
        
        public CellEditor( JTable table, JTextField component ) {
            super( component );
            this.table = table;
            component.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TablePropertyPanel.textField.accessibleName")); // NOI18N
            component.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TabularPropertyPanel.class, "TablePropertyPanel.textField.accessibleDesc")); // NOI18N
        }
    
        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            
            // Configure the component with the specified value
            ((JTextField)super.getComponent()).setText((String)value);
            
            if (isSelected) {
                ((JTextField)super.getComponent()).selectAll();
                table.repaint();
            }
    
            // Return the configured component
            return super.getComponent();
        }
    }
}
