/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2002-2003 Sun
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
package org.netbeans.modules.fcb.facebookmodule;

import facebook.socialnetworkingservice.facebookresponse.User;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.saas.facebook.FacebookSocialNetworkingService;
import org.netbeans.saas.facebook.FacebookSocialNetworkingServiceAuthenticator;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author lukas
 */
public class UserPanel extends javax.swing.JPanel {

    private URL photoUrl;
    private User u;

    /** Creates new form UserPanel */
    public UserPanel(User u) {
        this.u = u;
        try {
            photoUrl = new URL(u.getPic().getValue());
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserPanel.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        }
        initComponents();
        jLabel1.setToolTipText(u.getFirstName());
        String currentStatus = u.getStatus().getValue().getMessage().trim();
        setStatus(currentStatus);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setBackground(java.awt.Color.white);
        setPreferredSize(new java.awt.Dimension(315, 239));

        jLabel1.setIcon(new ImageIcon(photoUrl));
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setFocusable(false);
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 300));

        jTextArea1.setFont(new java.awt.Font("Arial", 0, 14));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setTabSize(4);
        jTextArea1.setMargin(new java.awt.Insets(5, 8, 5, 8));
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText(org.openide.util.NbBundle.getMessage(UserPanel.class, "UserPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText(org.openide.util.NbBundle.getMessage(UserPanel.class, "UserPanel.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextPane1.setBorder(null);
        jTextPane1.setContentType(org.openide.util.NbBundle.getMessage(UserPanel.class, "UserPanel.jTextPane1.contentType")); // NOI18N
        jTextPane1.setEditable(false);
        jScrollPane2.setViewportView(jTextPane1);

        jLabel2.setBackground(java.awt.Color.white);
        jLabel2.setFont(new java.awt.Font("Arial", 0, 10));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(UserPanel.class, "TTIP_newStatus")); // NOI18N
        jLabel2.setFocusable(false);

        jLabel3.setBackground(java.awt.Color.white);
        jLabel3.setFont(new java.awt.Font("Arial", 1, 10));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(UserPanel.class, "UserPanel.jLabel3.text")); // NOI18N
        jLabel3.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(UserPanel.class, "TTIP_newStatus")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        final ProgressHandle handle = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(UserPanel.class, "MSG_statusUpdate"));
        class X implements Runnable {

            void init() {
                handle.start();
                jButton1.setEnabled(false);
                jButton3.setEnabled(false);
                jTextArea1.setEnabled(false);
                RequestProcessor.getDefault().post(this);
            }

            @Override
            public void run() {
                if (EventQueue.isDispatchThread()) {
                    finish();
                } else {
                    handle.progress(NbBundle.getMessage(UserPanel.class, "MSG_connecting"));
                    try {
                        FacebookSocialNetworkingService.updateStatus(jTextArea1.getText());
                    } catch (Exception ioe) {
                        if (!(ioe instanceof IOException)) {
                            throw new RuntimeException(ioe);
                        }
                    }
                    EventQueue.invokeLater(this);
                }
            }

            void finish() {
                jButton1.setEnabled(true);
                jButton3.setEnabled(true);
                setStatus(jTextArea1.getText().trim());
                jTextArea1.setText("");
                jTextArea1.setEnabled(true);
                handle.finish();
                StatusDisplayer.getDefault().setStatusText(
                        NbBundle.getMessage(UserPanel.class, "MSG_statusUpdate_done"));
            }
        }
        new X().init();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        final ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(UserPanel.class, "MSG_fcbLogout"));
        class X implements Runnable {

            void init() {
                handle.start();
                jButton1.setEnabled(false);
                jButton3.setEnabled(false);
                jTextArea1.setEnabled(false);
                RequestProcessor.getDefault().post(this);
            }

            @Override
            public void run() {
                if (EventQueue.isDispatchThread()) {
                    finish();
                } else {
                    handle.progress(NbBundle.getMessage(UserPanel.class, "MSG_connecting"));
                    try {
                        FacebookSocialNetworkingServiceAuthenticator.logout();
                    } catch (Exception ioe) {
                        if (!(ioe instanceof IOException)) {
                            throw new RuntimeException(ioe);
                        }
                    }
                    EventQueue.invokeLater(this);
                }
            }

            void finish() {
                jButton1.setEnabled(true);
                jButton3.setEnabled(true);
                jTextArea1.setEnabled(true);
                FcbTopComponent.openLoginPanel();
                handle.finish();
                StatusDisplayer.getDefault().setStatusText(
                        NbBundle.getMessage(UserPanel.class, "MSG_loggedOut"));
            }
        }
        new X().init();
    }//GEN-LAST:event_jButton3ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

    private void setStatus(String text) {
        if (text.length() > 0) {
            jTextPane1.setText(NbBundle.getMessage(
                    UserPanel.class, "TXT_status", u.getName(), text));
        } else {
            jTextPane1.setText(NbBundle.getMessage(
                    UserPanel.class, "TXT_statusEmpty", u.getName()));
        }
    }

}
