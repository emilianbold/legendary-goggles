/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.java.hints.errors;

import java.util.prefs.Preferences;

/**
 *
 * @author Jan Lahoda
 */
public class SurroundWithTryCatchLog extends javax.swing.JPanel {

    private Preferences p;

    /** Creates new form SurroundWithTryCatchLog */
    public SurroundWithTryCatchLog(Preferences p) {
        initComponents();
        this.p = p;
        exceptions.setSelected(ErrorFixesFakeHint.isUseExceptions());
        logger.setSelected(ErrorFixesFakeHint.isUseLogger());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exceptions = new javax.swing.JCheckBox();
        logger = new javax.swing.JCheckBox();
        printStackTrace = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        exceptions.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(exceptions, org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.exceptions.text")); // NOI18N
        exceptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exceptionsActionPerformed(evt);
            }
        });

        logger.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(logger, org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.logger.text")); // NOI18N
        logger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loggerActionPerformed(evt);
            }
        });

        printStackTrace.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(printStackTrace, org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.printStackTrace.text")); // NOI18N
        printStackTrace.setEnabled(false);

        jLabel1.setText(org.openide.util.NbBundle.getBundle(SurroundWithTryCatchLog.class).getString("SurroundWithTryCatchLog.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(logger)
                            .add(exceptions)
                            .add(printStackTrace))))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(exceptions)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(logger)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(printStackTrace)
                .addContainerGap(188, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void loggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loggerActionPerformed
    ErrorFixesFakeHint.setUseLogger(p, logger.isSelected());
}//GEN-LAST:event_loggerActionPerformed

private void exceptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exceptionsActionPerformed
    ErrorFixesFakeHint.setUseExceptions(p, exceptions.isSelected());
}//GEN-LAST:event_exceptionsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox exceptions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JCheckBox logger;
    private javax.swing.JCheckBox printStackTrace;
    // End of variables declaration//GEN-END:variables

}
