/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * CategoryPanelStepFilters.java
 *
 * Created on Jan 20, 2009, 3:30:38 PM
 */

package org.netbeans.modules.debugger.jpda.ui.options;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.netbeans.api.debugger.Properties;

/**
 *
 * @author Martin Entlicher
 */
class CategoryPanelStepFilters extends StorablePanel {

    /** Creates new form CategoryPanelStepFilters */
    public CategoryPanelStepFilters() {
        initComponents();
        initFilterClassesList();
        // Set column sizes:
        filterClassesTable.getColumnModel().getColumn(0).setPreferredWidth(new JCheckBox().getPreferredSize().width);
        filterClassesTable.getColumnModel().getColumn(0).setMaxWidth(new JCheckBox().getPreferredSize().width);
        filterClassesTable.getColumnModel().getColumn(0).setResizable(false);
        filterClassesTable.getColumnModel().getColumn(1).setResizable(true);
        filterClassesTable.setRowHeight(Math.max(new JCheckBox().getPreferredSize().height, new JLabel("W").getPreferredSize().height));
        DisablingCellRenderer.apply(filterClassesTable);
        useStepFiltersCheckBoxActionPerformed(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        useStepFiltersCheckBox = new javax.swing.JCheckBox();
        filterSyntheticCheckBox = new javax.swing.JCheckBox();
        filterStaticInitCheckBox = new javax.swing.JCheckBox();
        filterConstructorsCheckBox = new javax.swing.JCheckBox();
        filterClassesLabel = new javax.swing.JLabel();
        filterClassesScrollPane = new javax.swing.JScrollPane();
        filterClassesTable = new javax.swing.JTable() {
            public boolean getScrollableTracksViewportHeight() {
                return true;
            }
        };
        stepThroughFiltersCheckBox = new javax.swing.JCheckBox();
        filterAddButton = new javax.swing.JButton();
        filterRemoveButton = new javax.swing.JButton();
        filtersCheckAllButton = new javax.swing.JButton();
        filtersUncheckAllButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(useStepFiltersCheckBox, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.useStepFiltersCheckBox.text")); // NOI18N
        useStepFiltersCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useStepFiltersCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filterSyntheticCheckBox, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterSyntheticCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(filterStaticInitCheckBox, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterStaticInitCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(filterConstructorsCheckBox, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterConstructorsCheckBox.text")); // NOI18N

        filterClassesLabel.setLabelFor(filterClassesTable);
        org.openide.awt.Mnemonics.setLocalizedText(filterClassesLabel, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterClassesLabel.text")); // NOI18N

        filterClassesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        filterClassesTable.setShowHorizontalLines(false);
        filterClassesTable.setShowVerticalLines(false);
        filterClassesTable.setTableHeader(null);
        filterClassesScrollPane.setViewportView(filterClassesTable);
        filterClassesTable.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterClassesLabel.a11y.name")); // NOI18N
        filterClassesTable.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterClassesLabel.a11y.description")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(stepThroughFiltersCheckBox, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.stepThroughFiltersCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(filterAddButton, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterAddButton.text")); // NOI18N
        filterAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterAddButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filterRemoveButton, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterRemoveButton.text")); // NOI18N
        filterRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterRemoveButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filtersCheckAllButton, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filtersCheckAllButton.text")); // NOI18N
        filtersCheckAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtersCheckAllButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filtersUncheckAllButton, org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filtersUncheckAllButton.text")); // NOI18N
        filtersUncheckAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtersUncheckAllButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(useStepFiltersCheckBox)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(stepThroughFiltersCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(filterStaticInitCheckBox)
                                    .add(filterSyntheticCheckBox)
                                    .add(filterConstructorsCheckBox)
                                    .add(filterClassesLabel)
                                    .add(filterClassesScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(filterAddButton)
                                    .add(filterRemoveButton)
                                    .add(filtersCheckAllButton)
                                    .add(filtersUncheckAllButton)))))))
        );

        layout.linkSize(new java.awt.Component[] {filterAddButton, filterRemoveButton, filtersCheckAllButton, filtersUncheckAllButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(useStepFiltersCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filterSyntheticCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filterStaticInitCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filterConstructorsCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filterClassesLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(filterAddButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(filterRemoveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(filtersCheckAllButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(filtersUncheckAllButton)
                        .add(36, 36, 36))
                    .add(filterClassesScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(stepThroughFiltersCheckBox)
                .addContainerGap())
        );

        useStepFiltersCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.useStepFiltersCheckBox.AccessibleContext.accessibleDescription")); // NOI18N
        filterSyntheticCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterSyntheticCheckBox.AccessibleContext.accessibleDescription")); // NOI18N
        filterStaticInitCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterStaticInitCheckBox.AccessibleContext.accessibleDescription")); // NOI18N
        filterConstructorsCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterConstructorsCheckBox.AccessibleContext.accessibleDescription")); // NOI18N
        filterClassesLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterClassesLabel.AccessibleContext.accessibleDescription")); // NOI18N
        stepThroughFiltersCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.stepThroughFiltersCheckBox.AccessibleContext.accessibleDescription")); // NOI18N
        filterAddButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterAddButton.AccessibleContext.accessibleDescription")); // NOI18N
        filterRemoveButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filterRemoveButton.AccessibleContext.accessibleDescription")); // NOI18N
        filtersCheckAllButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filtersCheckAllButton.AccessibleContext.accessibleDescription")); // NOI18N
        filtersUncheckAllButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CategoryPanelStepFilters.class, "CategoryPanelStepFilters.filtersUncheckAllButton.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void initFilterClassesList() {
        filterClassesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                filterRemoveButton.setEnabled(filterClassesTable.getSelectedRow() >= 0);
            }
        });
        filterRemoveButton.setEnabled(filterClassesTable.getSelectedRow() >= 0);
        filterClassesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void useStepFiltersCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useStepFiltersCheckBoxActionPerformed
        boolean enabled = useStepFiltersCheckBox.isSelected();
        filterSyntheticCheckBox.setEnabled(enabled);
        filterStaticInitCheckBox.setEnabled(enabled);
        filterConstructorsCheckBox.setEnabled(enabled);
        filterClassesLabel.setEnabled(enabled);
        filterClassesTable.setEnabled(enabled);
        filterClassesScrollPane.setEnabled(enabled);
        filterAddButton.setEnabled(enabled);
        filterRemoveButton.setEnabled(enabled && filterClassesTable.getSelectedRow() >= 0);
        filtersCheckAllButton.setEnabled(enabled);
        filtersUncheckAllButton.setEnabled(enabled);
        stepThroughFiltersCheckBox.setEnabled(enabled);
    }//GEN-LAST:event_useStepFiltersCheckBoxActionPerformed

    private void filterAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterAddButtonActionPerformed
        final DefaultTableModel model = (DefaultTableModel) filterClassesTable.getModel();
        model.addRow(new Object[] { Boolean.TRUE, "" });
        final int index = model.getRowCount() - 1;
        filterClassesTable.getSelectionModel().setSelectionInterval(index, index);
        filterClassesTable.editCellAt(index, 1);
        filterClassesTable.getEditorComponent().requestFocus();
         //DefaultCellEditor ed = (DefaultCellEditor)
        /*filterClassesTable.getCellEditor(index, 1).shouldSelectCell(
                new ListSelectionEvent(filterClassesTable,
                                       index, index, true));*/
        filterRemoveButton.setEnabled(false);
        filterAddButton.setEnabled(false);
        filterClassesTable.getCellEditor(index, 1).addCellEditorListener(new CellEditorListener() {
            public void editingStopped(ChangeEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (index < filterClassesTable.getRowCount()) {
                            String value = (String) model.getValueAt(index, 1);
                            if (value.trim().length() == 0) {
                                model.removeRow(index);
                            }
                        }
                    }
                });
                filterClassesTable.getCellEditor(index, 1).removeCellEditorListener(this);
                filterRemoveButton.setEnabled(true);
                filterAddButton.setEnabled(true);
            }

            public void editingCanceled(ChangeEvent e) {
                model.removeRow(index);
                filterClassesTable.getCellEditor(index, 1).removeCellEditorListener(this);
                filterRemoveButton.setEnabled(true);
                filterAddButton.setEnabled(true);
            }
        });

    }//GEN-LAST:event_filterAddButtonActionPerformed

    private void filterRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterRemoveButtonActionPerformed
        int index = filterClassesTable.getSelectedRow();
        if (index < 0) return ;
        DefaultTableModel model = (DefaultTableModel) filterClassesTable.getModel();
        model.removeRow(index);
        if (index < filterClassesTable.getRowCount() || --index >= 0) {
            filterClassesTable.setRowSelectionInterval(index, index);
        }
    }//GEN-LAST:event_filterRemoveButtonActionPerformed

    private void filtersCheckAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtersCheckAllButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) filterClassesTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(Boolean.TRUE, i, 0);
        }
        filterClassesTable.repaint();
    }//GEN-LAST:event_filtersCheckAllButtonActionPerformed

    private void filtersUncheckAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtersUncheckAllButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) filterClassesTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(Boolean.FALSE, i, 0);
        }
        filterClassesTable.repaint();
    }//GEN-LAST:event_filtersUncheckAllButtonActionPerformed

    @Override
    void load() {
        //Preferences p = NbPreferences.root().node("Debugger/JPDA");
        Properties p = Properties.getDefault().getProperties("debugger.options.JPDA");
        useStepFiltersCheckBox.setSelected(p.getBoolean("UseStepFilters", true));
        filterSyntheticCheckBox.setSelected(p.getBoolean("FilterSyntheticMethods", true));
        filterStaticInitCheckBox.setSelected(p.getBoolean("FilterStaticInitializers", false));
        filterConstructorsCheckBox.setSelected(p.getBoolean("FilterConstructors", false));
        //String[] filterClasses = (String[]) pp.getArray("FilterClasses", new String[] {});
        DefaultTableModel filterClassesModel = (DefaultTableModel) filterClassesTable.getModel();
        Set enabledFilters = (Set) Properties.getDefault ().getProperties ("debugger").
                getProperties ("sources").getProperties ("class_filters").
                getCollection (
                    "enabled",
                    Collections.EMPTY_SET
                );
        Set<String> allFilters = (Set<String>) Properties.getDefault ().getProperties ("debugger").
                getProperties ("sources").getProperties ("class_filters").
                getCollection (
                    "all",
                    Collections.EMPTY_SET
                );
        filterClassesModel.setRowCount(0);
        for (String filter : allFilters) {
            filterClassesModel.addRow(new Object[] { enabledFilters.contains(filter), filter });
        }
        stepThroughFiltersCheckBox.setSelected(p.getBoolean("StepThroughFilters", false));
        useStepFiltersCheckBoxActionPerformed(null);
    }

    @Override
    void store() {
        Properties p = Properties.getDefault().getProperties("debugger.options.JPDA");
        p.setBoolean("UseStepFilters", useStepFiltersCheckBox.isSelected());
        p.setBoolean("FilterSyntheticMethods", filterSyntheticCheckBox.isSelected());
        p.setBoolean("FilterStaticInitializers", filterStaticInitCheckBox.isSelected());
        p.setBoolean("FilterConstructors", filterConstructorsCheckBox.isSelected());
        TableModel filterClassesModel = filterClassesTable.getModel();
        Set<String> allFilters = new LinkedHashSet<String>();
        Set<String> enabledFilters = new HashSet<String>();
        int n = filterClassesModel.getRowCount();
        for (int i = 0; i < n; i++) {
            boolean isEnabled = (Boolean) filterClassesModel.getValueAt(i, 0);
            String clazz = (String) filterClassesModel.getValueAt(i, 1);
            allFilters.add(clazz);
            if (isEnabled) {
                enabledFilters.add(clazz);
            }
        }
        Properties.getDefault ().getProperties ("debugger").
                getProperties ("sources").getProperties ("class_filters").
                setCollection (
                    "all",
                    allFilters
                );
        Properties.getDefault ().getProperties ("debugger").
                getProperties ("sources").getProperties ("class_filters").
                setCollection (
                    "enabled",
                    enabledFilters
                );
        p.setBoolean("StepThroughFilters", stepThroughFiltersCheckBox.isSelected());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton filterAddButton;
    private javax.swing.JLabel filterClassesLabel;
    private javax.swing.JScrollPane filterClassesScrollPane;
    private javax.swing.JTable filterClassesTable;
    private javax.swing.JCheckBox filterConstructorsCheckBox;
    private javax.swing.JButton filterRemoveButton;
    private javax.swing.JCheckBox filterStaticInitCheckBox;
    private javax.swing.JCheckBox filterSyntheticCheckBox;
    private javax.swing.JButton filtersCheckAllButton;
    private javax.swing.JButton filtersUncheckAllButton;
    private javax.swing.JCheckBox stepThroughFiltersCheckBox;
    private javax.swing.JCheckBox useStepFiltersCheckBox;
    // End of variables declaration//GEN-END:variables

}
