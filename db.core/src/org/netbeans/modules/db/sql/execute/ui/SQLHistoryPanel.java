/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

/*
 * SQLHistoryDlg2.java
 *
 * Created on Jun 5, 2008, 4:55:52 PM
 */
package org.netbeans.modules.db.sql.execute.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.db.sql.history.SQLHistory;
import org.netbeans.modules.db.sql.history.SQLHistoryModel;
import org.netbeans.modules.db.sql.history.SQLHistoryModelImpl;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author John Baker
 */
public class SQLHistoryPanel extends javax.swing.JPanel {

    public static final Logger LOGGER = Logger.getLogger(SQLHistoryPanel.class.getName());
    private static Object[][] data;
    private static Object[][] parsedData;
    private Object[] comboData;
    private SQLHistoryView view;
    private JEditorPane editorPane;
    private static String[] sqlToolTipText;


    /** Creates new form SQLHistoryDlg2 */
    public SQLHistoryPanel(JEditorPane editorPane) {
        this.editorPane = editorPane;
        this.view = new SQLHistoryView(new SQLHistoryModelImpl());
        initSQLHistoryTableData(view);
        initComponents();
        connectionComboBox.addActionListener((HistoryTableModel) sqlHistoryTable.getModel());
        searchTextField.getDocument().addDocumentListener((HistoryTableModel) sqlHistoryTable.getModel());
        sqlHistoryTable.getColumnModel().getColumn(0).setHeaderValue(NbBundle.getMessage(SQLHistoryPanel.class, "LBL_SQLTableTitle"));
        sqlHistoryTable.getColumnModel().getColumn(1).setHeaderValue(NbBundle.getMessage(SQLHistoryPanel.class, "LBL_DateTableTitle"));
        // Initialize data for the Connection combo box  
        this.view.updateUrl();
        inputWarningLabel.setVisible(false);
    }

    private void initSQLHistoryTableData(SQLHistoryView localSQLView) {
            // Initialize sql column data          
            List<String> sqlList = view.getSQLList();
            List<String> dateList = view.getDateList();
            sqlToolTipText = new String[sqlList.size()];
            parsedData = new Object[sqlList.size()][2];
            data = new Object[sqlList.size()][2];
            int row = 0;
            int maxLength; 
            int length;
            for (String sql : sqlList) {
                length = sql.trim().length();
                maxLength = length > 50 ? 50 : length;
                data[row][0] = sql.trim().substring(0, maxLength);
                parsedData[row][0] = sql;
                sqlToolTipText[row] = sql.trim();
                row++;
            }
            // Initialize data
            row = 0;
            for (String date : dateList) {
                data[row][1] = date;
                parsedData[row][1] = date;
                row++;
            }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        connectionComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        insertSQLButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        sqlHistoryTable = new javax.swing.JTable();
        sqlLimitLabel = new javax.swing.JLabel();
        sqlLimitTextField = new javax.swing.JTextField();
        sqlLimitButton = new javax.swing.JButton();
        inputWarningLabel = new javax.swing.JLabel();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "LBL_Connection")); // NOI18N

        connectionComboBox.setActionCommand(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.connectionComboBox.actionCommand")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "LBL_Match")); // NOI18N

        searchTextField.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.searchTextField.text")); // NOI18N
        searchTextField.setMinimumSize(new java.awt.Dimension(20, 22));

        insertSQLButton.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "LBL_Insert")); // NOI18N
        insertSQLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSQLButtonActionPerformed(evt);
            }
        });

        sqlHistoryTable.setModel(new HistoryTableModel());
        sqlHistoryTable.setCellSelectionEnabled(true);
        sqlHistoryTable.setGridColor(java.awt.Color.lightGray);
        sqlHistoryTable.setSelectionBackground(java.awt.Color.lightGray);
        jScrollPane1.setViewportView(sqlHistoryTable);
        sqlHistoryTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.sqlHistoryTable.columnModel.title0")); // NOI18N
        sqlHistoryTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.sqlHistoryTable.columnModel.title1")); // NOI18N

        sqlLimitLabel.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.sqlLimitLabel.text")); // NOI18N

        sqlLimitTextField.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.sqlLimitTextField.text")); // NOI18N
        sqlLimitTextField.setMinimumSize(new java.awt.Dimension(18, 22));

        sqlLimitButton.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.sqlLimitButton.text")); // NOI18N
        sqlLimitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sqlLimitButtonActionPerformed(evt);
            }
        });

        inputWarningLabel.setForeground(java.awt.Color.red);
        inputWarningLabel.setText(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.inputWarningLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(connectionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(searchTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(insertSQLButton))
                    .add(layout.createSequentialGroup()
                        .add(sqlLimitLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(sqlLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(103, 103, 103)
                                .add(inputWarningLabel))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(sqlLimitButton)))
                        .addContainerGap(301, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(connectionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(searchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(insertSQLButton)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sqlLimitLabel)
                    .add(inputWarningLabel)
                    .add(sqlLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sqlLimitButton)))
        );

        insertSQLButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "ACSD_Search")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "ACSD_History")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "ACSD_History")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void insertSQLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSQLButtonActionPerformed
    int rowSelected = sqlHistoryTable.getSelectedRow();
    try {
        // Make sure to insert the entire SQL, not just what appears in the Table
        List<SQLHistory> sqlHistoryList = view.getSQLHistoryList();
        int i = 0;
        String sqlToInsert = ""; // NOI18N
        for (SQLHistory sqlHistory : sqlHistoryList) {
            if (rowSelected == i) {
                sqlToInsert = sqlHistory.getSql().trim();
            }
        }
        new InsertSQLUtility().insert(sqlToInsert, editorPane);
    } catch (BadLocationException ex) {
        Exceptions.printStackTrace(ex);
    }

}//GEN-LAST:event_insertSQLButtonActionPerformed

private void sqlLimitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sqlLimitButtonActionPerformed
// TODO add your handling code here:
    String limit = sqlLimitTextField.getText();
    int iLimit = 0;
    try {
        iLimit = Integer.parseInt(limit);
        NbPreferences.forModule(SQLHistoryPanel.class).put("SQL_STATEMENTS_SAVED_FOR_HISTORY", Integer.toString(iLimit));  // NOI18N               
    } catch (NumberFormatException ne) {
        inputWarningLabel.setVisible(true);
        inputWarningLabel.setText(NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.inputWarningLabel.text"));
        sqlLimitTextField.setText("10000");  // reset user's input
    }
    
    if (iLimit < 0 && iLimit > 10000) {
        sqlLimitButton.setEnabled(true);
        inputWarningLabel.setVisible(true);
        inputWarningLabel.setText(NbBundle.getMessage(SQLHistoryPanel.class, "SQLHistoryPanel.sqlLimitLabel.text"));
    }

}//GEN-LAST:event_sqlLimitButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox connectionComboBox;
    private javax.swing.JLabel inputWarningLabel;
    private javax.swing.JButton insertSQLButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTable sqlHistoryTable;
    private javax.swing.JButton sqlLimitButton;
    private javax.swing.JLabel sqlLimitLabel;
    private javax.swing.JTextField sqlLimitTextField;
    // End of variables declaration//GEN-END:variables
    private class SQLHistoryView {
        SQLHistoryModel model;
        List<SQLHistory> sqlHistoryList;

        public SQLHistoryView(SQLHistoryModel model) {
            this.model = model;
            this.sqlHistoryList = model.getSQLHistoryList();
        }

        public List<SQLHistory> getSQLHistoryList() {
            return sqlHistoryList;
        }

        public List<String> getUrlList() {
            List<String> urlList = new ArrayList<String>();
            for (SQLHistory sqlHistory : sqlHistoryList) {
                String url = sqlHistory.getUrl();
                if (!urlList.contains(url)) {
                    urlList.add(url);
                }
            }
            return urlList;
        }

        public List<String> getSQLList() {
            List<String> sqlList = new ArrayList<String>();

            for (SQLHistory sqlHistory : sqlHistoryList) {
                sqlList.add(sqlHistory.getSql());  // NOI18N
            }
            return sqlList;
        }

        public List<String> getDateList() {
            List<String> dateList = new ArrayList<String>();

            for (SQLHistory sqlHistory : sqlHistoryList) {
                dateList.add(DateFormat.getInstance().format(sqlHistory.getDate()));  // NOI18N
            }
            return dateList;
        }

        public void updateUrl() {
            // Initialize combo box data
            connectionComboBox.addItem(NbBundle.getMessage(SQLHistoryPanel.class, "LBL_URLComboBoxAllConnectionsItem"));
            List<String> urlList = getUrlList();
            for (String url : urlList) {
                Object item = new Object();
                item = url;
                connectionComboBox.addItem(item);
            }
        }

        public void setFilter() {
            // unused
        }
    }

    private final class UrlComboBoxModel implements ComboBoxModel, ActionListener {

        public void setSelectedItem(Object item) {
            connectionComboBox.setSelectedItem(item);
        }

        public Object getSelectedItem() {
            return (String) connectionComboBox.getSelectedItem();
        }

        public int getSize() {
            return comboData.length;
        }

        public Object getElementAt(int index) {
            return comboData[index];
        }

        public void addListDataListener(ListDataListener arg0) {
        }

        public void removeListDataListener(ListDataListener arg0) {
        }

        public void actionPerformed(ActionEvent arg0) {
        }
    }

    private final class HistoryTableModel extends DefaultTableModel implements ActionListener, DocumentListener {
        public int getRowCount() {
            return data.length;
        }

        public int getColumnCount() {
            return 2;
        }

        public Class<?> getColumnClass(int c) {
            Object value = getValueAt(0, c);
            if (value == null) {
                return String.class;
            } else {
                return getValueAt(0, c).getClass();
            }
        }

        public boolean isCellEditable(int arg0, int arg1) {
            return false;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void setValueAt(Object value, int row, int col) {
            adjustColumnPreferredWidths(sqlHistoryTable);
            fireTableCellUpdated(row, col);
        }

        public void addTableModelListener(TableModelListener arg0) {
            // not used
        }

        public void removeTableModelListener(TableModelListener arg0) {
            // not used
        }

        public void adjustColumnPreferredWidths(JTable table) {
            // Get max width for cells in column and make that the preferred width
            TableColumnModel columnModel = table.getColumnModel();
            for (int col = 0; col < table.getColumnCount(); col++) {

                int maxwidth = 0;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer rend =
                            table.getCellRenderer(row, col);
                    Object value = table.getValueAt(row, col);
                    Component comp =
                            rend.getTableCellRendererComponent(table,
                            value,
                            false,
                            false,
                            row,
                            col);
                    maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
                }
                TableColumn column = columnModel.getColumn(col);
                column.setPreferredWidth(maxwidth);
            }
        }

        public void actionPerformed(ActionEvent evt) {
            String url = ((javax.swing.JComboBox) evt.getSource()).getSelectedItem().toString();
            List<SQLHistory> sqlHistoryList = view.getSQLHistoryList();
            List<String> sqlList = new ArrayList<String>();
            List<String> dateList = new ArrayList<String>();
            connectionComboBox.setToolTipText(url);
            int i = 0;
            int length;
            int maxLength;
            for (SQLHistory sqlHistory : sqlHistoryList) {
                if (url.equals(NbBundle.getMessage(SQLHistoryPanel.class, "LBL_URLComboBoxAllConnectionsItem"))) {
                    length = sqlHistory.getSql().trim().length();
                    maxLength = length > 50 ? 50 : length;
                    sqlList.add(sqlHistory.getSql().trim().substring(0, maxLength));
                    dateList.add(DateFormat.getInstance().format(sqlHistory.getDate()));
                    sqlToolTipText[i++] = sqlHistory.getSql().trim();
                } else if (url.equals(sqlHistory.getUrl())) {
                    length = sqlHistory.getSql().trim().length();
                    maxLength = length > 50 ? 50 : length;
                    sqlList.add(sqlHistory.getSql().trim().substring(0, maxLength));
                    dateList.add(DateFormat.getInstance().format(sqlHistory.getDate()));
                    sqlToolTipText[i++] = sqlHistory.getSql().trim();                   
                } 
            }

                // Initialize sql column data
                data = null;
                data = new Object[sqlList.size()][2];
                int row = 0;
                for (String sql : sqlList) {
                    length = sql.trim().length();
                    maxLength = length > 50 ? 50 : length;
                    data[row][0] = sql.trim().substring(0, maxLength);
                    sqlToolTipText[row] = sql.trim();
                    row++;
                }
                // Initialize date column data
                row = 0;
                for (String date : dateList) {
                    data[row++][1] = date;
                }
        
                sqlHistoryTable.setDefaultRenderer(String.class, new SQLHistoryTableRenderer());                                
                sqlHistoryTable.repaint();
                sqlHistoryTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                sqlList = null;
                dateList = null;
        }

        public void insertUpdate(DocumentEvent evt) {
            // Read the contents
            try {
                String matchText = read(evt.getDocument());
                List<String> sqlList = new ArrayList<String>();
                List<String> dateList = new ArrayList<String>(); 
                sqlList = view.getSQLList();
                dateList = view.getDateList();
                data = new Object[sqlList.size()][2];
                int row = 0;
                int length;
                int maxLength;
                Iterator dateIterator = dateList.iterator();
                for (String sql : sqlList) {
                    if (sql.trim().indexOf(matchText) != -1) {
                        length = sql.trim().length();
                        maxLength = length > 50 ? 50 : length;
                        data[row][0] = sql.trim().substring(0, maxLength);
                        data[row][1] = dateIterator.next();
                        sqlToolTipText[row] = sql.trim();
                        row++;
                    } else {
                        cleanTable();
                    }
                }
                // Refresh the table
                sqlHistoryTable.repaint();
            } catch (InterruptedException e) {
                Exceptions.printStackTrace(e);
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }

        public void removeUpdate(DocumentEvent evt) {
             // Read the contents
            try {
                String matchText = read(evt.getDocument());
                List<String> sqlList = new ArrayList<String>();
                List<String> dateList = new ArrayList<String>();                                
                sqlList = view.getSQLList();               
                dateList = view.getDateList();
                data = new Object[sqlList.size()][2];
                int row = 0;
                int length;
                int maxLength;                                
                Iterator dateIterator = dateList.iterator();
                for (String sql : sqlList) {
                    if (sql.trim().indexOf(matchText) != -1) {
                        length = sql.trim().length();
                        maxLength = length > 50 ? 50 : length;
                        data[row][0] = sql.trim().substring(0, maxLength);
                        data[row][1] = dateIterator.next();
                        sqlToolTipText[row] = sql.trim();
                        row++;
                    } else {
                        cleanTable();
                    }
                }
                // Refresh the table                     
                sqlHistoryTable.repaint();
            } catch (InterruptedException e) {
                Exceptions.printStackTrace(e);
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }

        public void changedUpdate(DocumentEvent arg0) {
            // unused
        }

        private void cleanTable() {
            List<SQLHistory> sqlHistoryList = view.getSQLHistoryList();
            data = null;                         
            data = new Object[sqlHistoryList.size()][2];
            sqlHistoryTable.repaint();
        }
    }

    public static String read(Document doc) throws InterruptedException, Exception {
        Renderer r = new Renderer(doc);
        doc.render(r);

        synchronized (r) {
            while (!r.done) {
                r.wait();
                if (r.err != null) {
                    throw new Exception(r.err);
                }
            }
        }
        return r.result;
    }

    private static class SQLHistoryTableRenderer extends JLabel implements TableCellRenderer {

        public SQLHistoryTableRenderer() {
            super();
        }

        public Component getTableCellRendererComponent(JTable table, Object sqlHistory, boolean isSelected, boolean hasFocus, int nRow, int nCol) {
            Object tableData = data[nRow][nCol];
            Object toolTipData = parsedData[nRow][nCol];
            if (null != tableData && null != toolTipData) {
                setToolTipText("<html>" + parsedData[nRow][nCol].toString() + "</html>");
                setText(data[nRow][nCol].toString());
                table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
            return this;
        }
    }

    private static class Renderer implements Runnable {
        Document doc;
        String result;
        Throwable err;
        boolean done;

        Renderer(Document doc) {
            this.doc = doc;
        }

        public synchronized void run() {
            try {
                result = doc.getText(0, doc.getLength());
            } catch (Throwable e) {
                err = e;
                Exceptions.printStackTrace(e);
            }
            done = true;
            notify();
        }
    }

    private class InsertSQLUtility {

        public InsertSQLUtility() {
        }

        public void insert(String s, JEditorPane target)
                throws BadLocationException {
            insert(s, target, false);
        }

        public void insert(String s, JEditorPane target, boolean reformat)
                throws BadLocationException {

            if (s == null) {
                s = "";  // NOI18N
            }

            Document doc = target.getDocument();
            if (doc == null) {
                return;
            }

            if (doc instanceof BaseDocument) {
                ((BaseDocument) doc).atomicLock();
            }

            int start = insert(s, target, doc);
//            // format the inserted text
//            if (reformat && start >= 0 && doc instanceof BaseDocument) {  
//                int end = start + s.length();
//                Formatter f = ((BaseDocument) doc).getFormatter();
//                f.reformat((BaseDocument) doc, start, end);
//            }

            if (doc instanceof BaseDocument) {
                ((BaseDocument) doc).atomicUnlock();
            }

        }

        private int insert(String s, JEditorPane target, Document doc)
                throws BadLocationException {

            int start = -1;
            try {
                Caret caret = target.getCaret();
                int p0 = Math.min(caret.getDot(), caret.getMark());
                int p1 = Math.max(caret.getDot(), caret.getMark());
                doc.remove(p0, p1 - p0);
                start = caret.getDot();
                doc.insertString(start, s, null);
            } catch (BadLocationException ble) {
                LOGGER.log(Level.WARNING, org.openide.util.NbBundle.getMessage(SQLHistoryPanel.class, "LBL_InsertAtLocationError") + ble);
            }
            return start;
        }
    }
}
