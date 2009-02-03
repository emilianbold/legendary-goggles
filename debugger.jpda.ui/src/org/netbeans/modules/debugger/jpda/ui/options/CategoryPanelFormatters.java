/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * CategoryPanelFormatters.java
 *
 * Created on Jan 20, 2009, 3:30:49 PM
 */

package org.netbeans.modules.debugger.jpda.ui.options;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.netbeans.api.debugger.Properties;
import org.netbeans.modules.debugger.jpda.ui.VariablesFormatter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author martin
 */
class CategoryPanelFormatters extends StorablePanel {

    /** Creates new form CategoryPanelFormatters */
    public CategoryPanelFormatters() {
        initComponents();
        initFormattersList();
        loadSelectedFormatter(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        formattersScrollPane = new javax.swing.JScrollPane();
        formattersList = new javax.swing.JList();
        formatterNameLabel = new javax.swing.JLabel();
        formatterNameTextField = new javax.swing.JTextField();
        formatterClassTypesLabel = new javax.swing.JLabel();
        formatterClassTypesTextField = new javax.swing.JTextField();
        formatterClassTypesSubtypesCheckBox = new javax.swing.JCheckBox();
        formatValueLabel = new javax.swing.JLabel();
        formatValueScrollPane = new javax.swing.JScrollPane();
        formatValueEditorPane = new javax.swing.JEditorPane();
        formatChildrenLabel = new javax.swing.JLabel();
        formatChildrenAsCodeRadioButton = new javax.swing.JRadioButton();
        formatChildrenCodeScrollPane = new javax.swing.JScrollPane();
        formatChildrenCodeEditorPane = new javax.swing.JEditorPane();
        formatChildrenAsListRadioButton = new javax.swing.JRadioButton();
        formatChildrenListScrollPane = new javax.swing.JScrollPane();
        formatChildrenListTable = new javax.swing.JTable();
        childrenExpandExpressionLabel = new javax.swing.JLabel();
        childrenExpandExpressionTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        formattersAddButton = new javax.swing.JButton();
        formattersRemoveButton = new javax.swing.JButton();
        formattersMoveUpButton = new javax.swing.JButton();
        formattersMoveDownButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        variableAddButton = new javax.swing.JButton();
        variableRemoveButton = new javax.swing.JButton();
        variableMoveUpButton = new javax.swing.JButton();
        variableMoveDownButton = new javax.swing.JButton();

        formattersScrollPane.setViewportView(formattersList);

        formatterNameLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatterNameLabel.text")); // NOI18N

        formatterClassTypesLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatterClassTypesLabel.text")); // NOI18N

        formatterClassTypesSubtypesCheckBox.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatterClassTypesSubtypesCheckBox.text")); // NOI18N

        formatValueLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatValueLabel.text")); // NOI18N

        formatValueScrollPane.setViewportView(formatValueEditorPane);

        formatChildrenLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenLabel.text")); // NOI18N

        buttonGroup1.add(formatChildrenAsCodeRadioButton);
        formatChildrenAsCodeRadioButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenAsCodeRadioButton.text")); // NOI18N

        formatChildrenCodeScrollPane.setViewportView(formatChildrenCodeEditorPane);

        buttonGroup1.add(formatChildrenAsListRadioButton);
        formatChildrenAsListRadioButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenAsListRadioButton.text")); // NOI18N

        formatChildrenListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        formatChildrenListScrollPane.setViewportView(formatChildrenListTable);

        childrenExpandExpressionLabel.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.childrenExpandExpressionLabel.text")); // NOI18N

        formattersAddButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersAddButton.text")); // NOI18N
        formattersAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formattersAddButtonActionPerformed(evt);
            }
        });

        formattersRemoveButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersRemoveButton.text")); // NOI18N
        formattersRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formattersRemoveButtonActionPerformed(evt);
            }
        });

        formattersMoveUpButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersMoveUpButton.text")); // NOI18N
        formattersMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formattersMoveUpButtonActionPerformed(evt);
            }
        });

        formattersMoveDownButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formattersMoveDownButton.text")); // NOI18N
        formattersMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formattersMoveDownButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(formattersMoveDownButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
            .add(formattersMoveUpButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
            .add(formattersRemoveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
            .add(formattersAddButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(formattersAddButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formattersRemoveButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formattersMoveUpButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(formattersMoveDownButton))
        );

        variableAddButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableAddButton.text")); // NOI18N
        variableAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableAddButtonActionPerformed(evt);
            }
        });

        variableRemoveButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableRemoveButton.text")); // NOI18N
        variableRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableRemoveButtonActionPerformed(evt);
            }
        });

        variableMoveUpButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableMoveUpButton.text")); // NOI18N
        variableMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableMoveUpButtonActionPerformed(evt);
            }
        });

        variableMoveDownButton.setText(org.openide.util.NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.variableMoveDownButton.text")); // NOI18N
        variableMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableMoveDownButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(variableAddButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
            .add(variableMoveUpButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
            .add(variableMoveDownButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(variableRemoveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(variableAddButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(variableRemoveButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(variableMoveUpButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(variableMoveDownButton))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(formatterClassTypesLabel)
                    .add(formatterNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(formatterNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(formatterClassTypesTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(formatterClassTypesSubtypesCheckBox)
                        .add(12, 12, 12))))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(formatValueScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                    .add(formatValueLabel))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(childrenExpandExpressionLabel)
                .addContainerGap(72, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(formatChildrenListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(formattersScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(formatChildrenAsListRadioButton)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(formatChildrenCodeScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
                    .add(formatChildrenAsCodeRadioButton)
                    .add(formatChildrenLabel))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(24, 24, 24)
                .add(childrenExpandExpressionTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(formattersScrollPane, 0, 0, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(13, 13, 13)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(formatterNameLabel)
                    .add(formatterNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(formatterClassTypesLabel)
                    .add(formatterClassTypesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(formatterClassTypesSubtypesCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatValueLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatValueScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenAsCodeRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenCodeScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(formatChildrenAsListRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(formatChildrenListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(childrenExpandExpressionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(childrenExpandExpressionTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formattersAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formattersAddButtonActionPerformed
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Name:", "Add Variable Formatter");
        DialogDisplayer.getDefault().notify(nd);
        VariablesFormatter f = new VariablesFormatter(nd.getInputText());
        ((DefaultListModel) formattersList.getModel()).addElement(f);
        formattersList.setSelectedValue(f, true);
        //JCheckBox cb = new JCheckBox(nd.getInputText());
        //cb.setSelected(true);
        //filterClassesList.add(cb);
        //filterClassesList.repaint();

    }//GEN-LAST:event_formattersAddButtonActionPerformed

    private void formattersRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formattersRemoveButtonActionPerformed
        int index = formattersList.getSelectedIndex();
        if (index < 0) return ;
        ((DefaultListModel) formattersList.getModel()).remove(index);
    }//GEN-LAST:event_formattersRemoveButtonActionPerformed

    private void formattersMoveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formattersMoveUpButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_formattersMoveUpButtonActionPerformed

    private void formattersMoveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formattersMoveDownButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_formattersMoveDownButtonActionPerformed

    private void variableAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableAddButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_variableAddButtonActionPerformed

    private void variableRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableRemoveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_variableRemoveButtonActionPerformed

    private void variableMoveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableMoveUpButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_variableMoveUpButtonActionPerformed

    private void variableMoveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableMoveDownButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_variableMoveDownButtonActionPerformed

    private void initFormattersList() {
        formattersList.setCellRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                VariablesFormatter vf = (VariablesFormatter) value;
                JCheckBox cb = checkBoxComponents.get(vf);
                if (cb == null) {
                    cb = new JCheckBox(vf.getName(), vf.isEnabled());
                    checkBoxComponents.put(vf, cb);
                }
                cb.setEnabled(list.isEnabled());
                cb.setFont(list.getFont());
                cb.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                cb.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                return cb;
            }
        });
        formattersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                JList list = (JList) event.getSource();
                // Get index of item clicked
                int index = list.locationToIndex(event.getPoint());
                if (index < 0) return ;
                int height = list.getUI().getCellBounds(formattersList, index, index).height;
                Point cellLocation = list.getUI().indexToLocation(formattersList, index);
                int x = event.getPoint().x - cellLocation.x;
                if (x >= 0 && x <= height) {
                    VariablesFormatter vf = (VariablesFormatter) list.getModel().getElementAt(index);
                    // Toggle selected state
                    vf.setEnabled(!vf.isEnabled());
                    JCheckBox cb = checkBoxComponents.get(vf);
                    cb.setSelected(vf.isEnabled());
                    // Repaint cell
                    list.repaint(list.getCellBounds(index, index));
                }
            }
        });
        formattersList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Remember the last selection, store values to the last selected format and load values for the new one.
                int index = formattersList.getSelectedIndex();
                formattersRemoveButton.setEnabled(index >= 0);
                if (selectedVariablesFormatter != null) {
                    storeSelectedFormatter(selectedVariablesFormatter);
                }
                if (index >= 0) {
                    selectedVariablesFormatter = (VariablesFormatter) formattersList.getModel().getElementAt(index);
                } else {
                    selectedVariablesFormatter = null;
                }
                loadSelectedFormatter(selectedVariablesFormatter);
            }
        });
        formattersList.setModel(new DefaultListModel());
        formattersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void loadSelectedFormatter(VariablesFormatter f) {
        if (f == null) {
            formatterNameTextField.setText("");
            formatterClassTypesTextField.setText("");
            formatterClassTypesSubtypesCheckBox.setSelected(false);
            formatValueEditorPane.setText("");
            formatChildrenCodeEditorPane.setText("");
            formatChildrenListTable.setModel(new DefaultTableModel(new String[0][2], tableColumnNames));
            formatChildrenAsCodeRadioButton.setSelected(true);
            formatChildrenAsListRadioButton.setSelected(false);
            childrenExpandExpressionTextField.setText("");
            setEntryComponentsEnabled(false);
        } else {
            setEntryComponentsEnabled(true);
            formatterNameTextField.setText(f.getName());
            formatterClassTypesTextField.setText(f.getClassTypesCommaSeparated());
            formatterClassTypesSubtypesCheckBox.setSelected(f.isIncludeSubTypes());
            formatValueEditorPane.setText(f.getValueFormatCode());
            formatChildrenCodeEditorPane.setText(f.getChildrenFormatCode());
            Map<String, String> childrenVariables = f.getChildrenVariables();
            int n = childrenVariables.size();
            Iterator<Map.Entry<String, String>> childrenVariablesEntries = childrenVariables.entrySet().iterator();
            String[][] tableData = new String[n][2];
            for (int i = 0; i < n; i++) {
                Map.Entry<String, String> e = childrenVariablesEntries.next();
                tableData[i][0] = e.getKey();
                tableData[i][1] = e.getValue();
            }
            DefaultTableModel childrenVarsModel = new DefaultTableModel(tableData, tableColumnNames) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return String.class;
                }
            };
            formatChildrenListTable.setModel(childrenVarsModel);
            formatChildrenAsCodeRadioButton.setSelected(!f.isUseChildrenVariables());
            formatChildrenAsListRadioButton.setSelected(f.isUseChildrenVariables());
            childrenExpandExpressionTextField.setText(f.getChildrenExpandTestCode());
        }
    }

    private void setEntryComponentsEnabled(boolean enabled) {
        formatterNameTextField.setEnabled(enabled);
        formatterClassTypesTextField.setEnabled(enabled);
        formatterClassTypesSubtypesCheckBox.setEnabled(enabled);
        formatValueEditorPane.setEnabled(enabled);
        formatChildrenCodeEditorPane.setEnabled(enabled);
        formatChildrenListTable.setEnabled(enabled);
        formatChildrenAsCodeRadioButton.setEnabled(enabled);
        formatChildrenAsListRadioButton.setEnabled(enabled);
        childrenExpandExpressionTextField.setEnabled(enabled);
    }

    private void storeSelectedFormatter(VariablesFormatter f) {
        f.setName(formatterNameTextField.getText());
        f.setClassTypes(formatterClassTypesTextField.getText());
        f.setIncludeSubTypes(formatterClassTypesSubtypesCheckBox.isSelected());
        f.setValueFormatCode(formatValueEditorPane.getText());
        f.setChildrenFormatCode(formatChildrenCodeEditorPane.getText());
        TableModel tableModel = formatChildrenListTable.getModel();
        f.getChildrenVariables().clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            f.addChildrenVariable((String) tableModel.getValueAt(i, 0), (String) tableModel.getValueAt(i, 1));
        }
        f.setUseChildrenVariables(formatChildrenAsListRadioButton.isSelected());
        f.setChildrenExpandTestCode(childrenExpandExpressionTextField.getText());
    }
    
    @Override
    void load() {
        Properties p = Properties.getDefault().getProperties("debugger.options.JPDA");
        VariablesFormatter[] formatters = (VariablesFormatter[]) p.getArray("VariableFormatters", null);
        DefaultListModel filterClassesModel = (DefaultListModel) formattersList.getModel();
        filterClassesModel.clear();
        if (formatters != null) {
            for (int i = 0; i < formatters.length; i++) {
                filterClassesModel.addElement(formatters[i]);
            }
            if (formatters.length > 0) {
                formattersList.setSelectedValue(formatters[0], true);
            }
        }
    }

    @Override
    void store() {
        Properties p = Properties.getDefault().getProperties("debugger.options.JPDA");
        ListModel formattersModel = formattersList.getModel();
        VariablesFormatter[] formatters = new VariablesFormatter[formattersModel.getSize()];
        for (int i = 0; i < formatters.length; i++) {
            VariablesFormatter vf = (VariablesFormatter) formattersModel.getElementAt(i);
            formatters[i] = vf;
        }
        p.setArray("VariableFormatters", formatters);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel childrenExpandExpressionLabel;
    private javax.swing.JTextField childrenExpandExpressionTextField;
    private javax.swing.JRadioButton formatChildrenAsCodeRadioButton;
    private javax.swing.JRadioButton formatChildrenAsListRadioButton;
    private javax.swing.JEditorPane formatChildrenCodeEditorPane;
    private javax.swing.JScrollPane formatChildrenCodeScrollPane;
    private javax.swing.JLabel formatChildrenLabel;
    private javax.swing.JScrollPane formatChildrenListScrollPane;
    private javax.swing.JTable formatChildrenListTable;
    private javax.swing.JEditorPane formatValueEditorPane;
    private javax.swing.JLabel formatValueLabel;
    private javax.swing.JScrollPane formatValueScrollPane;
    private javax.swing.JLabel formatterClassTypesLabel;
    private javax.swing.JCheckBox formatterClassTypesSubtypesCheckBox;
    private javax.swing.JTextField formatterClassTypesTextField;
    private javax.swing.JLabel formatterNameLabel;
    private javax.swing.JTextField formatterNameTextField;
    private javax.swing.JButton formattersAddButton;
    private javax.swing.JList formattersList;
    private javax.swing.JButton formattersMoveDownButton;
    private javax.swing.JButton formattersMoveUpButton;
    private javax.swing.JButton formattersRemoveButton;
    private javax.swing.JScrollPane formattersScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton variableAddButton;
    private javax.swing.JButton variableMoveDownButton;
    private javax.swing.JButton variableMoveUpButton;
    private javax.swing.JButton variableRemoveButton;
    // End of variables declaration//GEN-END:variables
    private final Map<VariablesFormatter, JCheckBox> checkBoxComponents = new WeakHashMap<VariablesFormatter, JCheckBox>();
    private VariablesFormatter selectedVariablesFormatter;
    private final String[] tableColumnNames = new String[] {
        NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenListTable.Name"),
        NbBundle.getMessage(CategoryPanelFormatters.class, "CategoryPanelFormatters.formatChildrenListTable.Value")
    };

}
