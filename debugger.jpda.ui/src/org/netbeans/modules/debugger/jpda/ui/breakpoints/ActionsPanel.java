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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.debugger.jpda.ui.breakpoints;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.netbeans.api.debugger.jpda.JPDABreakpoint;

/**
 * @author  jj97931
 */
public class ActionsPanel extends javax.swing.JPanel {

    private JPDABreakpoint  breakpoint;

    /** Creates new form LineBreakpointPanel */
    public ActionsPanel (JPDABreakpoint b) {
        breakpoint = b;
        initComponents ();

        cbSuspend.addItem (java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("LBL_CB_Actions_Panel_Suspend_None"));
        cbSuspend.addItem (java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("LBL_CB_Actions_Panel_Suspend_Current"));
        cbSuspend.addItem (java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("LBL_CB_Actions_Panel_Suspend_All"));
        switch (b.getSuspend ()) {
            case JPDABreakpoint.SUSPEND_NONE:
                cbSuspend.setSelectedIndex (0);
                break;
            case JPDABreakpoint.SUSPEND_EVENT_THREAD:
                cbSuspend.setSelectedIndex (1);
                break;
            case JPDABreakpoint.SUSPEND_ALL:
                cbSuspend.setSelectedIndex (2);
                break;
        }
        if (b.getPrintText () != null)
            tfPrintText.setText (b.getPrintText ());
        tfPrintText.setPreferredSize(new Dimension(
                30*tfPrintText.getFontMetrics(tfPrintText.getFont()).charWidth('W'),
                tfPrintText.getPreferredSize().height));
        tfPrintText.setCaretPosition(0);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tfPrintText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cbSuspend = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle"); // NOI18N
        setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("L_Actions_Panel_BorderTitle"))); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        tfPrintText.setToolTipText(bundle.getString("TTT_TF_Actions_Panel_Print_Text")); // NOI18N
        tfPrintText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPrintTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(tfPrintText, gridBagConstraints);
        tfPrintText.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TF_Actions_Panel_Print_Text")); // NOI18N

        jLabel1.setLabelFor(cbSuspend);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, bundle.getString("L_Actions_Panel_Suspend")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_L_Actions_Panel_Suspend")); // NOI18N

        cbSuspend.setToolTipText(bundle.getString("TTT_CB_Actions_Panel_Suspend")); // NOI18N
        cbSuspend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSuspendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(cbSuspend, gridBagConstraints);
        cbSuspend.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_CB_Actions_Panel_Suspend")); // NOI18N

        jLabel2.setLabelFor(tfPrintText);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, bundle.getString("L_Actions_Panel_Print_Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void tfPrintTextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tfPrintTextActionPerformed
    {//GEN-HEADEREND:event_tfPrintTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPrintTextActionPerformed

    private void cbSuspendActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbSuspendActionPerformed
    {//GEN-HEADEREND:event_cbSuspendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbSuspendActionPerformed
    
    /**
     * Called when "Ok" button is pressed.
     */
    public void ok () {
        String printText = tfPrintText.getText ();
        if (printText.trim ().length () > 0)
            breakpoint.setPrintText (printText.trim ());
        else
            breakpoint.setPrintText (null);
        
        switch (cbSuspend.getSelectedIndex ()) {
            case 0:
                breakpoint.setSuspend (JPDABreakpoint.SUSPEND_NONE);
                break;
            case 1:
                breakpoint.setSuspend (JPDABreakpoint.SUSPEND_EVENT_THREAD);
                break;
            case 2:
                breakpoint.setSuspend (JPDABreakpoint.SUSPEND_ALL);
                break;
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbSuspend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField tfPrintText;
    // End of variables declaration//GEN-END:variables
    
}
