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

package org.netbeans.modules.web.project.ui.customizer;

import org.netbeans.api.project.libraries.LibrariesCustomizer;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.web.project.WebProjectGenerator;
import org.netbeans.modules.web.project.Utils;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
/**
 *
 * @author  tz97951
 */
public class LibrariesChooser extends javax.swing.JPanel {
    private Collection incompatibleLibs;
    private Collection alreadySelectedLibs;
    private String j2eePlatform;

    /** Creates new form LibrariesChooser */
    public LibrariesChooser(Collection alreadySelectedLibs, String j2eePlatform) {
        initComponents();
        jLabel2.setForeground(Utils.getErrorColor());
        jList1.setPrototypeCellValue("0123456789012345678901234");      //NOI18N
        jList1.setModel(new LibrariesListModel());
        this.j2eePlatform = j2eePlatform;
        incompatibleLibs =
                VisualClasspathSupport.getLibrarySet(WebProjectGenerator.getIncompatibleLibraries(j2eePlatform));
        jList1.setCellRenderer(new LibraryRenderer(incompatibleLibs));
        this.alreadySelectedLibs = alreadySelectedLibs;
    }

    public Library[] getSelectedLibraries () {
        Object[] selected = this.jList1.getSelectedValues();
        Collection libs = new ArrayList();
        for (int i = 0; i < selected.length; i++) {
            final Library lib = (Library) selected[i];
            if(!incompatibleLibs.contains(lib)) {   // incompatible libraries are not added
                libs.add(lib);
            }
        }
        return (Library[]) libs.toArray(new Library[libs.size()]);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        jList1.addListSelectionListener(listener);
    }

    public boolean isValidSelection() {
        Object[] selected = this.jList1.getSelectedValues();
        if(selected.length == 0) {
            return false;
        }
        for (int i = 0; i < selected.length; i++) {
            if(incompatibleLibs.contains(selected[i])) {
                return false;
            }
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        edit = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(350, 250));
        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("AD_LibrariesChooser"));
        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("MNE_InstalledLibraries").charAt(0));
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("CTL_InstalledLibraries"));
        jLabel1.setLabelFor(jList1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 74));
        jList1.addListSelectionListener(formListener);

        jScrollPane1.setViewportView(jList1);
        jList1.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("AD_jScrollPaneLibraries"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        add(jScrollPane1, gridBagConstraints);

        jLabel2.setForeground(javax.swing.UIManager.getColor("nb.errorForeground"));
        jLabel2.setPreferredSize(new java.awt.Dimension(50, 16));
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(jLabel2, gridBagConstraints);

        edit.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("MNE_EditLibraries").charAt(0));
        edit.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("CTL_EditLibraries"));
        edit.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 12);
        add(edit, gridBagConstraints);
        edit.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("AD_jButtonManageLibraries"));

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, javax.swing.event.ListSelectionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == edit) {
                LibrariesChooser.this.editLibraries(evt);
            }
        }

        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            if (evt.getSource() == jList1) {
                LibrariesChooser.this.jList1ValueChanged(evt);
            }
        }
    }//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }
        Object[] selected = this.jList1.getSelectedValues();
        for (int i = 0; i < selected.length; i++) {
            final Library lib = (Library) selected[i];
            if (incompatibleLibs.contains(lib)) {
                jLabel2.setText(NbBundle.getMessage(LibrariesChooser.class, "MSG_IncompatibleLibrary", j2eePlatform));
                return;
            }
        }
        jLabel2.setText("");
    }//GEN-LAST:event_jList1ValueChanged

    private void editLibraries(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLibraries
        LibrariesListModel model = (LibrariesListModel) jList1.getModel ();
        Collection oldLibraries = Arrays.asList(model.getLibraries());
        LibrariesCustomizer.showCustomizer((Library)this.jList1.getSelectedValue());
        List currentLibraries = Arrays.asList(model.getLibraries());
        Collection newLibraries = new ArrayList (currentLibraries);

        newLibraries.removeAll(oldLibraries);
        int indexes[] = new int [newLibraries.size()];

        Iterator it = newLibraries.iterator();
        for (int i=0; it.hasNext();i++) {
            Library lib = (Library) it.next ();
            indexes[i] = currentLibraries.indexOf (lib);
        }
        this.jList1.setSelectedIndices (indexes);
    }//GEN-LAST:event_editLibraries


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton edit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables



    private static final class LibrariesListModel extends AbstractListModel implements PropertyChangeListener {

        private Library[] cache;
        /** No of libs in LibraryManager when last refreshed */
        private int numberOfLibs;

        public LibrariesListModel () {
            LibraryManager manager = LibraryManager.getDefault();
            manager.addPropertyChangeListener((PropertyChangeListener)WeakListeners.create(PropertyChangeListener.class,
                    this, manager));
        }

        public synchronized int getSize() {
            if (this.cache == null) {
                this.cache = this.createLibraries();
            }
            return this.cache.length;
        }

        public synchronized Object getElementAt(int index) {
            if (this.cache == null) {
                this.cache = this.createLibraries();
            }
            if (index >= 0 && index < this.cache.length) {
                return this.cache[index];
            }
            else {
                return null;
            }
        }

        public synchronized void propertyChange(PropertyChangeEvent evt) {
            int oldSize = this.cache == null ? 0 : numberOfLibs;
            this.cache = createLibraries();
            int newSize = numberOfLibs;
            this.fireContentsChanged(this, 0, Math.min(oldSize-1,newSize-1));
            if (oldSize > newSize) {
                this.fireIntervalRemoved(this,newSize,oldSize-1);
            }
            else if (oldSize < newSize) {
                this.fireIntervalAdded(this,oldSize,newSize-1);
            }
        }

        public synchronized Library[] getLibraries () {
            if (this.cache == null) {
                this.cache = this.createLibraries();
            }
            return this.cache;
        }

        private Library[] createLibraries () {
            Library[] libs = LibraryManager.getDefault().getLibraries();
            numberOfLibs = libs.length;
            Arrays.sort(libs, new Comparator () {
                public int compare (Object o1, Object o2) {
                    assert (o1 instanceof Library) && (o2 instanceof Library);
                    String name1 = ((Library)o1).getDisplayName();
                    String name2 = ((Library)o2).getDisplayName();
                    return name1.compareToIgnoreCase(name2);
                }
            });
            return libs;
        }
    }


    private static final class LibraryRenderer extends DefaultListCellRenderer {

        private static final String LIBRARY_ICON = "org/netbeans/modules/web/project/ui/resources/libraries.gif";  //NOI18N
        private Icon cachedIcon;
        private Collection incompatibleLibs;

        public LibraryRenderer(Collection incompatibleLibs) {
            this.incompatibleLibs = incompatibleLibs;
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            String displayName = null;
            if (value instanceof Library) {
                displayName = ((Library) value).getDisplayName();
            }
            super.getListCellRendererComponent(list, displayName, index, isSelected, cellHasFocus);
            if (incompatibleLibs.contains(value)) {
                setEnabled(false);
            }
            setToolTipText(value instanceof Library ? VisualClasspathSupport.getLibraryString((Library) value) : null);
            setIcon(createIcon());
            return this;
        }

        private synchronized Icon createIcon () {
            if (this.cachedIcon == null) {
                Image img = Utilities.loadImage(LIBRARY_ICON);
                this.cachedIcon = new ImageIcon (img);
            }
            return this.cachedIcon;
        }

    }

}
