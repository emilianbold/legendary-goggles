/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.tools.generator;

import java.text.MessageFormat;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;

import org.openide.*;
import org.openide.filesystems.*;
import org.openide.util.*;

import org.netbeans.modules.xml.tools.lib.GuiUtil;

public class SelectFileDialog extends JPanel {

    /** Serial Version UID */
    private static final long serialVersionUID = 4699298946223454165L;


    private Util.NameCheck check;

    
    //
    // init
    //

    public SelectFileDialog (FileObject folder, String name, String ext) {

        this (folder, name, ext, Util.JAVA_CHECK);
    }

    public SelectFileDialog (FileObject folder, String name, String ext, Util.NameCheck check) {
        this.folder = folder;
        this.name = name;
        this.ext = ext;
        this.check = check;
        this.selectDD = new DialogDescriptor
                        (this, Util.getString ("PROP_fileNameTitle") + " *." + ext, true, // NOI18N
                         DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
                         DialogDescriptor.BOTTOM_ALIGN, null, null);

        initComponents ();
        fileLabel.setDisplayedMnemonic(Util.getChar("PROP_fileName_mne")); // NOI18N
        ownInitComponents ();
        
        initAccessibility();
    }

    private void ownInitComponents () {
        fileField.setText (name);        
        updateDialogUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        fileLabel = new javax.swing.JLabel();
        fileField = new javax.swing.JTextField();
        fillPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        fileLabel.setText(java.util.ResourceBundle.getBundle("tools/src/org/netbeans/modules/xml/tools/generator/Bundle").getString("PROP_fileName"));
        fileLabel.setLabelFor(fileField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(fileLabel, gridBagConstraints);

        fileField.setColumns(20);
        fileField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileFieldActionPerformed(evt);
            }
        });

        fileField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fileFieldKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints.weightx = 1.0;
        add(fileField, gridBagConstraints);

        fillPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        fillPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(fillPanel, gridBagConstraints);

    }//GEN-END:initComponents

  private void fileFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fileFieldKeyReleased
        updateDialogUI();
  }//GEN-LAST:event_fileFieldKeyReleased

    private void fileFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileFieldActionPerformed
        updateDialogUI();
    }//GEN-LAST:event_fileFieldActionPerformed

    // check for errors and signalize them
    private void updateDialogUI() {

        if (!!! check.checkName(fileField.getText())) {
            fileField.setForeground(Color.red);        
            enableOkOption (false);
        } else {
            fileField.setForeground(Color.black);
            enableOkOption (true);
        }
    }

    private void enableOkOption(boolean enable) {
/* NullPointerEx
        Object[] options = selectDD.getOptions();

        for (int i = 0; i<options.length; i++) {
            Util.debug("   " + options[i]);
            if (options[i].equals(DialogDescriptor.OK_OPTION)) {
                Util.debug("++++");                        
            }
        }
*/        
    }

    public FileObject getFileObject () throws IOException {
        TopManager.getDefault().createDialog (selectDD).show();
        if (selectDD.getValue() != DialogDescriptor.OK_OPTION) {
            throw new UserCancelException();
        }
        String name = fileField.getText();
        
        FileObject newFO = folder.getFileObject (name, ext);
        if (newFO != null) {
            if (!!! GuiUtil.confirmAction (MessageFormat.format (Util.getString ("PROP_replaceMsg"),
                                                                new String [] { name, ext })) ) {
                throw new UserCancelException();
            }
        } else {
            newFO = folder.createData (name, ext);
        }
        return newFO;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileField;
    private javax.swing.JPanel fillPanel;
    // End of variables declaration//GEN-END:variables

    DialogDescriptor selectDD;
    FileObject folder;
    String name;
    String ext;    
    
    
    /** Initialize accesibility
     */
    public void initAccessibility(){

        java.util.ResourceBundle b = org.openide.util.NbBundle.getBundle(this.getClass());
 
        fileField.getAccessibleContext().setAccessibleDescription(b.getString("ACSD_fileField"));        
    }
    
}
