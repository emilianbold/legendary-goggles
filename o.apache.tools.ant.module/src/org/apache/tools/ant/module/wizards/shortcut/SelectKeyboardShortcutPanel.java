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

package org.apache.tools.ant.module.wizards.shortcut;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

final class SelectKeyboardShortcutPanel extends javax.swing.JPanel implements KeyListener {

    private KeyStroke stroke = null;
    
    private SelectKeyboardShortcutWizardPanel wiz;
    
    /** Create the wizard panel component and set up some basic properties. */
    public SelectKeyboardShortcutPanel (SelectKeyboardShortcutWizardPanel wiz) {
        this.wiz = wiz;
        initComponents ();
	initAccessibility ();
        // Provide a name in the title bar.
        setName (NbBundle.getMessage (SelectKeyboardShortcutPanel.class, "SKSP_LBL_select_shortcut_to_add"));
        testField.addKeyListener (this);
    }

    
    private void initAccessibility () {        
        testField.getAccessibleContext().setAccessibleName(NbBundle.getMessage (SelectKeyboardShortcutPanel.class, "ACSN_LBL_type_here")); 
        testField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage (SelectKeyboardShortcutPanel.class, "ACSD_LBL_type_here")); 
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SelectKeyboardShortcutPanel.class, "SKSP_TEXT_press_any_key_seq"));
    }
    
    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        hintsArea = new javax.swing.JTextArea();
        mainPanel = new javax.swing.JPanel();
        testField = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        hintsArea.setBackground(new java.awt.Color(204, 204, 204));
        hintsArea.setEditable(false);
        hintsArea.setFont(javax.swing.UIManager.getFont ("Label.font"));
        hintsArea.setForeground(new java.awt.Color(102, 102, 153));
        hintsArea.setLineWrap(true);
        hintsArea.setText(NbBundle.getMessage(SelectKeyboardShortcutPanel.class, "SKSP_TEXT_press_any_key_seq"));
        hintsArea.setWrapStyleWord(true);
        hintsArea.setDisabledTextColor(javax.swing.UIManager.getColor ("Label.foreground"));
        hintsArea.setEnabled(false);
        hintsArea.setOpaque(false);
        add(hintsArea, java.awt.BorderLayout.NORTH);

        testField.setColumns(15);
        testField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        testField.setText(NbBundle.getMessage(SelectKeyboardShortcutPanel.class, "SKSP_LBL_type_here"));
        mainPanel.add(testField);

        add(mainPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea hintsArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField testField;
    // End of variables declaration//GEN-END:variables
    
    // KeyListener:

    public void keyPressed (KeyEvent e) {
        // XXX ideally make TAB switch focus, rather than be handled...
        stroke = KeyStroke.getKeyStroke (e.getKeyCode (), e.getModifiers ());
        testField.setText (Utilities.keyToString (stroke));
        wiz.fireChangeEvent ();
        e.consume ();
    }
    public void keyReleased (KeyEvent e) {
        e.consume ();
    }
    public void keyTyped (KeyEvent e) {
        e.consume ();
    }
    
    public static class SelectKeyboardShortcutWizardPanel implements WizardDescriptor.Panel {

        private SelectKeyboardShortcutPanel panel = null;
        private FileObject shortcutsFolder = null;
        
        public Component getComponent () {
            return getPanel(); 
        }
        
        private SelectKeyboardShortcutPanel getPanel() {
            if (panel == null) {
                panel = new SelectKeyboardShortcutPanel(this);
            }
            return panel;
        }

        public HelpCtx getHelp () {
            return HelpCtx.DEFAULT_HELP;
        }

        public boolean isValid () {
            if (shortcutsFolder == null)
                shortcutsFolder = Repository.getDefault ().getDefaultFileSystem ().findResource ("Shortcuts"); // NOI18N
            return (getPanel().stroke != null) &&
                   (shortcutsFolder.getFileObject(Utilities.keyToString(getPanel().stroke), "instance") == null) && // NOI18N
                   (shortcutsFolder.getFileObject(Utilities.keyToString(getPanel().stroke), "xml") == null); // NOI18N
        }

        private final Set listeners = new HashSet (1); // Set<ChangeListener>
        public final void addChangeListener (ChangeListener l) {
            synchronized (listeners) {
                listeners.add (l);
            }
        }
        public final void removeChangeListener (ChangeListener l) {
            synchronized (listeners) {
                listeners.remove (l);
            }
        }
        protected final void fireChangeEvent () {
            Iterator it;
            synchronized (listeners) {
                it = new HashSet (listeners).iterator ();
            }
            ChangeEvent ev = new ChangeEvent (this);
            while (it.hasNext ()) {
                ((ChangeListener) it.next ()).stateChanged (ev);
            }
        }

        public void readSettings (Object settings) {
            // XXX later...
        }
        public void storeSettings (Object settings) {
            WizardDescriptor wiz = (WizardDescriptor) settings;
            wiz.putProperty(ShortcutWizard.PROP_STROKE, getPanel().stroke);
        }
    }
    
}
