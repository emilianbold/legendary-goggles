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

package org.netbeans.modules.java.j2seproject.ui.wizards;


import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.openide.filesystems.FileUtil;


/**
 * List of source/test roots
 * @author tzezula
 */
public final class FolderList extends javax.swing.JPanel {

    public static final String PROP_LAST_CUR_DIR = "lastCurrentDirectory";   //NOI18N
    public static final String PROP_FILES = "files";    //NOI18N

    private File lastCurrentDir;
    private String fcMessage;

    /** Creates new form FolderList */
    public FolderList (String label, char mnemonic, String accessibleDesc, String fcMessage,
                       char addButtonMnemonic, String addButtonAccessibleDesc,
                       char removeButtonMnemonic,String removeButtonAccessibleDesc) {
        this.fcMessage = fcMessage;
        initComponents();
        this.jLabel1.setText(label);
        this.jLabel1.setDisplayedMnemonic(mnemonic);
        this.roots.getAccessibleContext().setAccessibleName(accessibleDesc);
        this.roots.setCellRenderer(new Renderer());
        this.roots.setModel (new DefaultListModel());
        this.roots.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removeButton.setEnabled(roots.getSelectedIndices().length != 0);
                }
            }
        });
        this.addButton.getAccessibleContext().setAccessibleDescription(addButtonAccessibleDesc);
        this.addButton.setMnemonic (addButtonMnemonic);        
        this.removeButton.getAccessibleContext().setAccessibleDescription(removeButtonAccessibleDesc);
        this.removeButton.setMnemonic (removeButtonMnemonic);
        this.removeButton.setEnabled(false);
    }

    public File getLastCurrentDirectory () {
        return this.lastCurrentDir;
    }

    public void setLastCurrentDirectory (File dir) {
        File oldValue = this.lastCurrentDir;
        this.lastCurrentDir = dir;
        this.firePropertyChange(PROP_LAST_CUR_DIR, oldValue,this.lastCurrentDir);
    }

    public File[] getFiles () {
        Object[] files = ((DefaultListModel)this.roots.getModel()).toArray();
        File[] result = new File[files.length];
        System.arraycopy(files, 0, result, 0, files.length);
        return result;
    }

    public void setFiles (File[] files) {
        DefaultListModel model = ((DefaultListModel)this.roots.getModel());
        model.clear();
        for (int i=0; i<files.length; i++) {
            model.addElement (files[i]);
        }
        if (files.length>0) {
            this.roots.setSelectedIndex(0);
        }
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
        roots = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setViewportView(roots);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jScrollPane1, gridBagConstraints);

        addButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/wizards/Bundle").getString("CTL_AddFolder"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(addButton, gridBagConstraints);

        removeButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/wizards/Bundle").getString("CTL_RemoveFolder"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(removeButton, gridBagConstraints);

    }//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Object[] selection = this.roots.getSelectedValues ();
        for (int i=0; i<selection.length; i++) {
            ((DefaultListModel)this.roots.getModel()).removeElement (selection[i]);
        }
        this.firePropertyChange(PROP_FILES, null, null);
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(this.fcMessage);
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        File cd = getLastCurrentDirectory();
        if (cd != null && cd.isDirectory()) {
            chooser.setCurrentDirectory(cd);
        }
        if (chooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            int[] indecesToSelect = new int[files.length];
            DefaultListModel model = (DefaultListModel)this.roots.getModel();
            for (int i=0, index=model.size(); i<files.length; i++, index++) {
                model.addElement (FileUtil.normalizeFile(files[i]));
                indecesToSelect[i] = index;
            }
            this.roots.setSelectedIndices(indecesToSelect);
            this.firePropertyChange(PROP_FILES, null, null);
            this.setLastCurrentDirectory(chooser.getCurrentDirectory());
        }
    }//GEN-LAST:event_addButtonActionPerformed
    
    
    private static class Renderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            File f = (File) value;            
            return super.getListCellRendererComponent(list, f.getAbsolutePath(), index, isSelected, cellHasFocus);
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JList roots;
    // End of variables declaration//GEN-END:variables
    
}
