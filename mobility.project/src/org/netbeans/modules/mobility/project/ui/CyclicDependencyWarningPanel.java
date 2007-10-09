/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

/*
 * CyclicDepenedcyWarningPanel.java
 *
 * Created on 20. kveten 2004, 11:48
 */
package org.netbeans.modules.mobility.project.ui;

import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author  Adam
 */
public class CyclicDependencyWarningPanel extends JPanel implements Runnable {
    
    private static CyclicDependencyWarningPanel panel;
    private final DefaultListModel model;
    
    protected Dialog d = null;
    protected final CloseListener listener;
    protected static boolean dontShow = false;
    
    public static void showWarning(final String projectName) {
        synchronized (CyclicDependencyWarningPanel.class) {
            if (dontShow) return;
            if (panel == null) panel = new CyclicDependencyWarningPanel();
            if (!panel.model.contains(projectName)) panel.model.addElement(projectName);
            SwingUtilities.invokeLater(panel);
        }
    }
    
    /** Creates new form CyclicDepenedcyWarningPanel */
    private CyclicDependencyWarningPanel() {
        model = new DefaultListModel();
        listener = new CloseListener();
        initComponents();
        initAccessibility();
        jList1.setModel(model);
    }
    
    public void run() {
        synchronized (CyclicDependencyWarningPanel.class) {
            if (d == null) {
                d = DialogDisplayer.getDefault().createDialog(new DialogDescriptor(this, NbBundle.getMessage(CyclicDependencyWarningPanel.class, "Title_CycDep"), false, //NOI18N
                        new Object[] {NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION,
                        DialogDescriptor.DEFAULT_ALIGN, new HelpCtx(CyclicDependencyWarningPanel.class), null));
                d.addWindowListener(listener);
            }
            d.setVisible(true);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jCheckBox1 = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(CyclicDependencyWarningPanel.class, "LBL_CyclicDependencies")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel2, gridBagConstraints);

        jLabel1.setLabelFor(jList1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(CyclicDependencyWarningPanel.class, "LBL_CycDeps_Projects")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel1, gridBagConstraints);

        jList1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, NbBundle.getMessage(CyclicDependencyWarningPanel.class, "LBL_CycDeps_DoNotAgain")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jCheckBox1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void initAccessibility() {
        getAccessibleContext().setAccessibleName((NbBundle.getMessage(CyclicDependencyWarningPanel.class, "ACSN_CycDep")));
        getAccessibleContext().setAccessibleDescription((NbBundle.getMessage(CyclicDependencyWarningPanel.class, "ACSD_CycDep")));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    
    private class CloseListener extends WindowAdapter {
        private CloseListener()
        {
            //Just to avoid creation of accessor class
        }
        
        @SuppressWarnings("synthetic-access")
		public void windowClosed(@SuppressWarnings("unused")
		final WindowEvent e) {
            synchronized (CyclicDependencyWarningPanel.class) {
                dontShow = jCheckBox1.isSelected();
                d.removeWindowListener(listener);
                d = null;
            }
        }
    }
}
