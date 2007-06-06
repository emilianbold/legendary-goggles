/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.timers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import org.netbeans.insane.live.LiveReferences;
import org.openide.DialogDescriptor;
import org.openide.explorer.view.NodeRenderer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.DialogDisplayer;

/**
 *
 * @author Jan Lahoda
 */
public class TimeComponentPanel extends javax.swing.JPanel implements PropertyChangeListener {
    
    /** Creates new form TimeComponentPanel */
    public TimeComponentPanel() {
        initComponents();
        times.addMouseListener(new PopupAdapter());
        jList1.addMouseListener(new ListPopupAdapter());
        key2RowNumber = new HashMap<String, Integer>();
        TimesCollectorPeer.getDefault().addPropertyChangeListener(this);
        fillIn();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        times = new javax.swing.JTable();
        
        setLayout(new java.awt.GridBagLayout());
        
        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jList1.setModel(new DefaultListModel());
        jList1.setCellRenderer(new FileObjectListRenderer());
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        
        jScrollPane1.setViewportView(jList1);
        
        jSplitPane1.setLeftComponent(jScrollPane1);
        
        times.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
            
        },
                new String [] {
            "Name", "Time"
        }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };
            
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(times);
        
        jSplitPane1.setRightComponent(jScrollPane2);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);
        
    }
// </editor-fold>//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
// TODO add your handling code here:
        fillTimeTable();
    }//GEN-LAST:event_jList1ValueChanged
    
    
// Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable times;
// End of variables declaration//GEN-END:variables
    
    private Map<String, Integer> key2RowNumber;
    
    private void fillTimeTable() {
        Reference ref = (Reference)jList1.getSelectedValue();
        Object fo = ref == null ? null : ref.get();
        
        // clear the table
        DefaultTableModel model = (DefaultTableModel) times.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        key2RowNumber.clear();
        
        if (fo == null) return;
        
        Collection<String> keys = TimesCollectorPeer.getDefault().getKeysForFile(fo);
        synchronized(keys) {
            for (String key : keys) {
                changeRow(fo, key);
            }
        }
    }

    private TimesCollectorPeer.Description getDescForRow(Object fo, int row) {
        Collection<String> keys = TimesCollectorPeer.getDefault().getKeysForFile(fo);
        synchronized (keys) {
            Iterator<String> it = keys.iterator();
            String key = null;
            for (int i= 0; i<=row; i++) {
                assert (it.hasNext());
                key = it.next();
            }
            return TimesCollectorPeer.getDefault().getDescription(fo, key);
        }
    }

    
    private void changeRow(Object fo, String key) {
        Integer row = key2RowNumber.get(key);
        DefaultTableModel model = (DefaultTableModel) times.getModel();
        
        if (row != null) {
            model.removeRow(row);
        }
        
        TimesCollectorPeer.Description desc = TimesCollectorPeer.getDefault().getDescription(fo, key);
        
        if (desc == null) {
            return ;
        }
        
        if (row == null) {
            key2RowNumber.put(key, row = model.getRowCount());
        }
        
        model.insertRow(row, new Object[] {desc.getMessage(), desc.getTime()});
    }
    
    private void fillIn() {
        DefaultListModel model = (DefaultListModel) jList1.getModel();

        model.removeAllElements();

        for (Object f : TimesCollectorPeer.getDefault().getFiles()) {
            model.addElement(new WeakReference(f));
        }
    }

    public void propertyChange(final PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if ("fos".equals(evt.getPropertyName())) {
                    DefaultListModel model = (DefaultListModel) jList1.getModel();
                    
                    if (evt.getNewValue() != null) {
                        model.addElement(new WeakReference(evt.getNewValue()));
                    } else {
                        fillIn();
                    }
                }
                
                if ("PROP".equals(evt.getPropertyName())) {
                    Object fo  = evt.getOldValue();
                    String     key = (String) evt.getNewValue();
                    Reference ref = (Reference)jList1.getSelectedValue();
                    if (fo == null || (ref != null && ref.get() == fo)) {
                        changeRow(fo, key);
                    }
                }
                
                if ("selected".equals(evt.getPropertyName())) {
                    Object fo = evt.getNewValue();
                    ListModel dm = jList1.getModel();
                    for(int i=0; i < dm.getSize(); i++) {
                        if(fo.equals(((WeakReference)dm.getElementAt(i)).get())) {
                            jList1.setSelectedIndex(i);
                            jList1.ensureIndexIsVisible(i);
                            repaint();
                            break;
                        }
                    }
                }
            }
        });
    }


    private static void dumpRoots(Collection objs) {
        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout());
        JProgressBar bar = new JProgressBar();
        inner.add(new JLabel("Computing object reachability"), BorderLayout.CENTER);
        inner.add(bar, BorderLayout.SOUTH);
        Dialog d = DialogDisplayer.getDefault().createDialog(new DialogDescriptor(
                inner, "Please wait"));
        d.pack();
        d.setModal(false);
        d.setVisible(true);
        
        String report = getRoots(objs, bar, inner);

        inner.removeAll();
        JScrollPane pane = new JScrollPane();
        JTextArea editor = new JTextArea(report);
        editor.setColumns(80);
        editor.setEditable(false);
        pane.setViewportView(editor);
        inner.add(pane, BorderLayout.CENTER);
        d.setSize(Math.min(600, editor.getPreferredSize().width+30), Math.min(400, editor.getPreferredSize().height + 70));
        d.invalidate();
        d.validate();
        d.repaint();
    }
    
    private static class FileObjectListRenderer extends DefaultListCellRenderer {
        private NodeRenderer r;
        
        private FileObjectListRenderer() {
            r = new NodeRenderer();
        }
        
        public Component getListCellRendererComponent(JList list, Object wr, int index, boolean isSelected, boolean cellHasFocus) {
            Object value = ((WeakReference)wr).get();
            
            if (value instanceof FileObject) {
                try {
                    FileObject fo = (FileObject) value;
                    DataObject od = DataObject.find(fo);
                    Node       node = od.getNodeDelegate();
                    
                    return r.getListCellRendererComponent(list, node, index, isSelected, cellHasFocus);
                } catch (IOException e) {
                    FileObject fo = (FileObject) value;
                    value = "FO: " + fo;
                }
            }
            
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        
    }
    
    class PopupAdapter extends org.openide.awt.MouseUtils.PopupMouseAdapter {
        PopupAdapter() {
        }

        protected void showPopup(MouseEvent e) {
            int selRow = times.rowAtPoint(e.getPoint());

            if (!times.isRowSelected(selRow)) {
                // This will set ExplorerManager selection as well.
                // If selRow == -1 the selection will be cleared.
                times.getSelectionModel().setSelectionInterval(selRow, selRow);
            }

            if (selRow != -1) {
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getX(), e.getY(), TimeComponentPanel.this);
                createPopup((int) p.getX(), (int) p.getY(), selRow);
            }
        }
        
        void createPopup(int x, int y, int row) {
            Object fo = jList1.getSelectedValue();
            if (fo instanceof WeakReference) fo = ((WeakReference)fo).get();
            if (fo == null) return;
            
            TimesCollectorPeer.Description desc = getDescForRow(fo, row);
            if (!(desc instanceof TimesCollectorPeer.ObjectCountDescripton)) return;
            
            final TimesCollectorPeer.ObjectCountDescripton oc = (TimesCollectorPeer.ObjectCountDescripton) desc;
            JPopupMenu popup = new JPopupMenu();
            popup.add(new AbstractAction("Find refs") {

                public void actionPerformed(ActionEvent arg0) {
                    dumpRoots(oc.getInstances());
                }
            });
            popup.show(TimeComponentPanel.this, x, y);
        }
    }

    class ListPopupAdapter extends org.openide.awt.MouseUtils.PopupMouseAdapter {
        ListPopupAdapter() {
        }

        protected void showPopup(MouseEvent e) {
            int selRow = jList1.locationToIndex(e.getPoint());

            if (!jList1.isSelectedIndex(selRow)) {
                // If selRow == -1 the selection will be cleared.
                jList1.getSelectionModel().setSelectionInterval(selRow, selRow);
            }

            if (selRow != -1) {
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getX(), e.getY(), TimeComponentPanel.this);
                createPopup((int) p.getX(), (int) p.getY(), selRow);
            }
        }
        
        void createPopup(int x, int y, int row) {
            final WeakReference wr = (WeakReference)jList1.getSelectedValue();
            if (! (wr.get() instanceof FileObject)) return;

            JPopupMenu popup = new JPopupMenu();
            popup.add(new AbstractAction("Find refs") {

                public void actionPerformed(ActionEvent arg0) {
                    try {
                        FileObject f = (FileObject)wr.get();
                        if (f == null) return;
                        DataObject dobj = DataObject.find(f);
                        f = null;
                        // hack - DO.find, we'not really interrested in
                        // the FileObject reachability
                        // This will go away for general key type
                        dumpRoots(Collections.singleton(dobj));
                    }
                    catch (DataObjectNotFoundException ex) {
                        // OK, the DO is not ready, FO invalid or whatever.
                        // Trace the invalid FO instead 
                        dumpRoots(Collections.singleton(wr.get()));
                    }
                }
            });
            popup.show(TimeComponentPanel.this, x, y);
        }
    }


    private static String getRoots(Collection objects, JProgressBar bar, final JPanel inner) {
        // scanning intentionally blocks AWT, force repaints
        bar.getModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                inner.paintImmediately(inner.getBounds());
            }
        });
        Map/*<Object,Path>*/ traces = LiveReferences.fromRoots(objects, null, bar.getModel());
        StringBuffer sb = new StringBuffer();
        
        for (Object inst : traces.keySet()) {
            sb.append(inst);
            sb.append(":\n");
            sb.append(traces.get(inst));
            sb.append("\n\n");
        }
  
        return sb.toString();
    }
}
