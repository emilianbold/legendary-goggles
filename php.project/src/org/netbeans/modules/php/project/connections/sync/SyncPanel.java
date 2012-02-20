/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.connections.sync;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Panel for remote synchronization.
 */
public final class SyncPanel extends JPanel {

    private static final long serialVersionUID = 1674646546545121L;

    // XXX
    @StaticResource
    private static final String INFO_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/info_icon.png"; // NOI18N
    @StaticResource
    private static final String RESET_ICON_PATH = "org/netbeans/modules/php/project/ui/resources/info_icon.png"; // NOI18N

    static final TableCellRenderer DEFAULT_TABLE_CELL_RENDERER = new DefaultTableCellRenderer();
    static final TableCellRenderer ERROR_TABLE_CELL_RENDERER = new DefaultTableCellRenderer();

    final List<SyncItem> items;
    final FileTableModel tableModel;

    private final String projectName;
    private final String remoteConfigurationName;

    // @GuardedBy(AWT)
    private DialogDescriptor descriptor = null;
    // @GuardedBy(AWT)
    private NotificationLineSupport notificationLineSupport = null;


    SyncPanel(String projectName, String remoteConfigurationName, List<SyncItem> items) {
        assert SwingUtilities.isEventDispatchThread();
        assert items != null;

        this.projectName = projectName;
        this.remoteConfigurationName = remoteConfigurationName;
        this.items = items;
        tableModel = new FileTableModel(items);

        initComponents();
        initTable();
        initOperationButtons();
        initInfos();
    }

    @NbBundle.Messages({
        "# 0 - project name",
        "# 1 - remote configuration name",
        "SyncPanel.title=Remote Synchronization for {0}: {1}",
    })
    public boolean open(boolean firstRun) {
        assert SwingUtilities.isEventDispatchThread();
        descriptor = new DialogDescriptor(
                this,
                Bundle.SyncPanel_title(projectName, remoteConfigurationName),
                true,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_OPTION,
                null);
        notificationLineSupport = descriptor.createNotificationLineSupport();
        validateItems();
        updateSyncInfo();
        firstRunInfoLabel.setVisible(firstRun);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        try {
            dialog.setVisible(true);
        } finally {
            dialog.dispose();
        }
        return descriptor.getValue() == NotifyDescriptor.OK_OPTION;
    }

    private void initTable() {
        // model
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                validateItems();
                updateSyncInfo();
            }
        });
        itemTable.setModel(tableModel);
        // renderer
        itemTable.setDefaultRenderer(String.class, new StringRenderer());
        itemTable.setDefaultRenderer(SyncItem.Operation.class, new OperationRenderer());
        // columns
        itemTable.getTableHeader().setReorderingAllowed(false);
        TableColumnModel columnModel = itemTable.getColumnModel();
        columnModel.getColumn(0).setMinWidth(10);
        columnModel.getColumn(0).setMaxWidth(10);
        columnModel.getColumn(0).setResizable(false);
        columnModel.getColumn(2).setMinWidth(100);
        columnModel.getColumn(2).setMaxWidth(100);
        columnModel.getColumn(2).setResizable(false);
        // selections
        itemTable.setColumnSelectionAllowed(false);
        itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                int selectedRowCount = itemTable.getSelectedRowCount();
                setEnabledOperationButtons(selectedRowCount > 0);
                setEnabledDiffButton(selectedRowCount);
            }
        });
    }

    @NbBundle.Messages("SyncPanel.resetButton.title=Reset to the original state")
    private void initOperationButtons() {
        // operations
        initOperationButton(noopButton, SyncItem.Operation.NOOP);
        initOperationButton(downloadButton, SyncItem.Operation.DOWNLOAD);
        initOperationButton(uploadButton, SyncItem.Operation.UPLOAD);
        initOperationButton(deleteLocallyButton, SyncItem.Operation.DELETE_LOCALLY);
        initOperationButton(deleteRemotelyButton, SyncItem.Operation.DELETE_REMOTELY);
        // reset
        initResetButton();
    }

    private void initOperationButton(JButton button, SyncItem.Operation operation) {
        // XXX
        //button.setText(null);
        button.setText(operation.name());
        //button.setIcon(operation.getIcon());
        button.setToolTipText(operation.getTitle());
        button.addActionListener(new OperationButtonListener(operation));
    }

    private void initResetButton() {
        // XXX
        //resetButton.setText(null);
        resetButton.setText("RESET"); // NOI18N
        //resetButton.setIcon(ImageUtilities.loadImageIcon(RESET_ICON_PATH, false));
        resetButton.setToolTipText(Bundle.SyncPanel_resetButton_title());
        resetButton.addActionListener(new OperationButtonListener(null));
    }

    private void initInfos() {
        firstRunInfoLabel.setIcon(ImageUtilities.loadImageIcon(INFO_ICON_PATH, false));
        syncInfoLabel.setIcon(ImageUtilities.loadImageIcon(INFO_ICON_PATH, false));
    }

    void setEnabledOperationButtons(boolean enabled) {
        noopButton.setEnabled(enabled);
        downloadButton.setEnabled(enabled);
        uploadButton.setEnabled(enabled);
        deleteLocallyButton.setEnabled(enabled);
        deleteRemotelyButton.setEnabled(enabled);
        resetButton.setEnabled(enabled);
    }

    void setEnabledDiffButton(int selectedRowCount) {
        if (selectedRowCount != 1) {
            diffButton.setEnabled(false);
            return;
        }
        SyncItem syncItem = items.get(itemTable.getSelectedRow());
        diffButton.setEnabled(syncItem.isDiffPossible());
    }

    @NbBundle.Messages({
        "SyncPanel.error.operations=Synchronization not possible. Fix errors first.",
        "SyncPanel.warn.operations=Synchronization possible but warnings should be reviewed first."
    })
    void validateItems() {
        assert SwingUtilities.isEventDispatchThread();
        boolean warn = false;
        for (SyncItem syncItem : items) {
            if (syncItem.hasError()) {
                notificationLineSupport.setErrorMessage(Bundle.SyncPanel_error_operations());
                descriptor.setValid(false);
                return;
            }
            if (syncItem.hasWarning()) {
                warn = true;
            }
        }
        if (warn) {
            notificationLineSupport.setWarningMessage(Bundle.SyncPanel_warn_operations());
        } else {
            notificationLineSupport.clearMessages();
        }
        descriptor.setValid(true);
    }

    @NbBundle.Messages("SyncPanel.info.status=Download: {0} files, upload: {1} files, delete remotely: {2} files, "
            + "delete locally: {3} files, no operation: {4} files, errors: {5} files.")
    void updateSyncInfo() {
        int download = 0;
        int upload = 0;
        int deleteRemotely = 0;
        int deleteLocally = 0;
        int noop = 0;
        int errors = 0;
        for (SyncItem syncItem : items) {
            switch (syncItem.getOperation()) {
                case NOOP:
                    noop++;
                    break;
                case DOWNLOAD:
                case DOWNLOAD_REVIEW:
                    download++;
                    break;
                case UPLOAD:
                case UPLOAD_REVIEW:
                    upload++;
                    break;
                case DELETE_REMOTELY:
                    deleteRemotely++;
                    break;
                case DELETE_LOCALLY:
                    deleteLocally++;
                    break;
                case FILE_CONFLICT:
                case FILE_DIR_COLLISION:
                    errors++;
                    break;
                default:
                    assert false : "Unknown operation: " + syncItem.getOperation();
            }
        }
        syncInfoLabel.setText(Bundle.SyncPanel_info_status(download, upload, deleteRemotely, deleteLocally, noop, errors));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstRunInfoLabel = new JLabel();
        itemScrollPane = new JScrollPane();
        itemTable = new JTable();
        syncInfoLabel = new JLabel();
        diffButton = new JButton();
        noopButton = new JButton();
        downloadButton = new JButton();
        uploadButton = new JButton();
        deleteLocallyButton = new JButton();
        deleteRemotelyButton = new JButton();
        resetButton = new JButton();

        Mnemonics.setLocalizedText(firstRunInfoLabel, NbBundle.getMessage(SyncPanel.class, "SyncPanel.firstRunInfoLabel.text")); // NOI18N
        itemTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        itemTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        itemScrollPane.setViewportView(itemTable);

        Mnemonics.setLocalizedText(syncInfoLabel, "SYNC INFO LABEL"); // NOI18N
        Mnemonics.setLocalizedText(diffButton, NbBundle.getMessage(SyncPanel.class, "SyncPanel.diffButton.text")); // NOI18N
        diffButton.setEnabled(false);

        Mnemonics.setLocalizedText(noopButton, " "); // NOI18N
        noopButton.setEnabled(false);

        Mnemonics.setLocalizedText(downloadButton, " "); // NOI18N
        downloadButton.setEnabled(false);

        Mnemonics.setLocalizedText(uploadButton, " "); // NOI18N
        uploadButton.setEnabled(false);

        Mnemonics.setLocalizedText(deleteLocallyButton, " "); // NOI18N
        deleteLocallyButton.setEnabled(false);

        Mnemonics.setLocalizedText(deleteRemotelyButton, " "); // NOI18N
        deleteRemotelyButton.setEnabled(false);

        Mnemonics.setLocalizedText(resetButton, " "); // NOI18N
        resetButton.setEnabled(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()

                .addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(itemScrollPane).addGroup(layout.createSequentialGroup()

                        .addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                                .addComponent(diffButton)
                                .addGap(18, 18, 18)
                                .addComponent(noopButton)

                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(downloadButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(uploadButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(deleteLocallyButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(deleteRemotelyButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(resetButton)).addComponent(firstRunInfoLabel).addComponent(syncInfoLabel)).addGap(0, 0, Short.MAX_VALUE))).addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {deleteLocallyButton, deleteRemotelyButton, downloadButton, noopButton, resetButton, uploadButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(firstRunInfoLabel)

                .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(itemScrollPane, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(syncInfoLabel).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(diffButton).addComponent(noopButton).addComponent(downloadButton).addComponent(uploadButton).addComponent(deleteLocallyButton).addComponent(deleteRemotelyButton).addComponent(resetButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteLocallyButton;
    private JButton deleteRemotelyButton;
    private JButton diffButton;
    private JButton downloadButton;
    private JLabel firstRunInfoLabel;
    private JScrollPane itemScrollPane;
    private JTable itemTable;
    private JButton noopButton;
    private JButton resetButton;
    private JLabel syncInfoLabel;
    private JButton uploadButton;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private static final class FileTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 16478634354314324L;

        @NbBundle.Messages({
            "SyncPanel.table.column.remote.title=Remote Path",
            "SyncPanel.table.column.local.title=Local Path",
            "SyncPanel.table.column.operation.title=Operation"
        })
        private static final String[] COLUMNS = {
            "", // NOI18N
            Bundle.SyncPanel_table_column_remote_title(),
            Bundle.SyncPanel_table_column_operation_title(),
            Bundle.SyncPanel_table_column_local_title(),
        };

        private final List<SyncItem> items;


        public FileTableModel(List<SyncItem> items) {
            this.items = items;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @NbBundle.Messages({
            "SyncPanel.error.cellValue=!",
            "SyncPanel.warning.cellValue=?"
        })
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SyncItem syncItem = items.get(rowIndex);
            if (columnIndex == 0) {
                if (syncItem.hasError()) {
                    return Bundle.SyncPanel_error_cellValue();
                }
                if (syncItem.hasWarning()) {
                    return Bundle.SyncPanel_warning_cellValue();
                }
                return null;
            } else if (columnIndex == 1) {
                return syncItem.getRemotePath();
            } else if (columnIndex == 2) {
                return syncItem.getOperation();
            } else if (columnIndex == 3) {
                return syncItem.getLocalPath();
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0
                    || columnIndex == 1
                    || columnIndex == 3) {
                return String.class;
            } else if (columnIndex == 2) {
                return SyncItem.Operation.class;
            }
            throw new IllegalStateException("Unknown column index: " + columnIndex);
        }

        public void fireSyncItemChange(int row) {
            fireTableCellUpdated(row, 0);
            fireTableCellUpdated(row, 2);
        }

    }

    private final class StringRenderer implements TableCellRenderer {

        private static final long serialVersionUID = 567654543546954L;


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel rendererComponent;
            String text = (String) value;
            if (column == 0) {
                // error
                rendererComponent = (JLabel) ERROR_TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                rendererComponent.setHorizontalAlignment(SwingConstants.CENTER);
                rendererComponent.setFont(rendererComponent.getFont().deriveFont(Font.BOLD));
                SyncItem syncItem = items.get(row);
                rendererComponent.setForeground(UIManager.getColor(syncItem.hasError() ? "nb.errorForeground" : "nb.warningForeground")); // NOI18N
                rendererComponent.setToolTipText(items.get(row).getMessage());
            } else {
                rendererComponent = (JLabel) DEFAULT_TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // file path
                rendererComponent.setToolTipText(text);
            }
            rendererComponent.setText(text);
            return rendererComponent;
        }

    }

    private final class OperationRenderer implements TableCellRenderer {

        private static final long serialVersionUID = -6786654671313465458L;


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel rendererComponent = (JLabel) DEFAULT_TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            SyncItem.Operation operation = (SyncItem.Operation) value;
            // XXX replace with icon
            rendererComponent.setText(operation.toString());
            rendererComponent.setToolTipText(operation.getTitle());
            return rendererComponent;
        }

    }

    private final class OperationButtonListener implements ActionListener {

        private final SyncItem.Operation operation;


        public OperationButtonListener(SyncItem.Operation operation) {
            this.operation = operation;
        }

        // can be done in background thread if needed
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = itemTable.getSelectedRows();
            assert selectedRows.length > 0;
            for (Integer index : selectedRows) {
                SyncItem syncItem = items.get(index);
                if (operation == null) {
                    syncItem.resetOperation();
                } else {
                    syncItem.setOperation(operation);
                }
                syncItem.validate();
                tableModel.fireSyncItemChange(index);
            }
        }

    }

}
