/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cordova.options;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.cordova.platforms.android.AndroidPlatform;
import org.netbeans.modules.cordova.CordovaPlatform;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;

final class MobilePlatformsPanel extends javax.swing.JPanel {

    private final MobilePlatformsOptionsPanelController controller;
    private final DocumentListener documentL;

    MobilePlatformsPanel(MobilePlatformsOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        documentL = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                MobilePlatformsPanel.this.controller.changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                MobilePlatformsPanel.this.controller.changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                MobilePlatformsPanel.this.controller.changed();
            }
        };

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        androidPanel = new javax.swing.JPanel();
        androidSdkLabel = new javax.swing.JLabel();
        androidSdkField = new javax.swing.JTextField();
        androidSdkBrowse = new javax.swing.JButton();
        androidSdkDownload = new javax.swing.JLabel();
        cordovaPanel = new javax.swing.JPanel();
        cordovaSdkLabel = new javax.swing.JLabel();
        cordovaSdkField = new javax.swing.JTextField();
        cordovaSdkBrowse = new javax.swing.JButton();
        cordovaSdkDownload = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(androidSdkLabel, org.openide.util.NbBundle.getMessage(MobilePlatformsPanel.class, "MobilePlatformsPanel.androidSdkLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(androidSdkBrowse, org.openide.util.NbBundle.getMessage(MobilePlatformsPanel.class, "MobilePlatformsPanel.androidSdkBrowse.text")); // NOI18N
        androidSdkBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                androidSdkBrowseActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(androidSdkDownload, org.openide.util.NbBundle.getMessage(MobilePlatformsPanel.class, "MobilePlatformsPanel.androidSdkDownload.text")); // NOI18N
        androidSdkDownload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                androidSdkDownloadMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout androidPanelLayout = new javax.swing.GroupLayout(androidPanel);
        androidPanel.setLayout(androidPanelLayout);
        androidPanelLayout.setHorizontalGroup(
            androidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(androidPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(androidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(androidPanelLayout.createSequentialGroup()
                        .addComponent(androidSdkLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(androidSdkField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(androidSdkBrowse))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, androidPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(androidSdkDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        androidPanelLayout.setVerticalGroup(
            androidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(androidPanelLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(androidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(androidSdkLabel)
                    .addComponent(androidSdkField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(androidSdkBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(androidSdkDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        org.openide.awt.Mnemonics.setLocalizedText(cordovaSdkLabel, org.openide.util.NbBundle.getMessage(MobilePlatformsPanel.class, "MobilePlatformsPanel.cordovaSdkLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cordovaSdkBrowse, org.openide.util.NbBundle.getMessage(MobilePlatformsPanel.class, "MobilePlatformsPanel.cordovaSdkBrowse.text")); // NOI18N
        cordovaSdkBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cordovaSdkBrowseActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cordovaSdkDownload, org.openide.util.NbBundle.getMessage(MobilePlatformsPanel.class, "MobilePlatformsPanel.cordovaSdkDownload.text")); // NOI18N
        cordovaSdkDownload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cordovaSdkDownloadMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout cordovaPanelLayout = new javax.swing.GroupLayout(cordovaPanel);
        cordovaPanel.setLayout(cordovaPanelLayout);
        cordovaPanelLayout.setHorizontalGroup(
            cordovaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cordovaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cordovaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cordovaPanelLayout.createSequentialGroup()
                        .addComponent(cordovaSdkLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cordovaSdkField, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cordovaSdkBrowse))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cordovaPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cordovaSdkDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        cordovaPanelLayout.setVerticalGroup(
            cordovaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cordovaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cordovaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cordovaSdkLabel)
                    .addComponent(cordovaSdkField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cordovaSdkBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cordovaSdkDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cordovaPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(androidPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(cordovaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(androidPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void androidSdkBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_androidSdkBrowseActionPerformed
        File sdkDir = new FileChooserBuilder(MobilePlatformsPanel.class).setDirectoriesOnly(true).setTitle("Choose Android SDK directory").showOpenDialog();
        if (sdkDir != null) {
            androidSdkField.setText(sdkDir.getAbsolutePath());
        }
    }//GEN-LAST:event_androidSdkBrowseActionPerformed

    private void androidSdkDownloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_androidSdkDownloadMouseClicked
        try {
            URLDisplayer.getDefault().showURL(new URL("http://developer.android.com/sdk/index.html"));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_androidSdkDownloadMouseClicked

    private void cordovaSdkBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cordovaSdkBrowseActionPerformed
        File sdkDir = new FileChooserBuilder(MobilePlatformsPanel.class).setDirectoriesOnly(true).setTitle("Choose Cordova SDK directory").showOpenDialog();
        if (sdkDir != null) {
            cordovaSdkField.setText(sdkDir.getAbsolutePath());
        }
    }//GEN-LAST:event_cordovaSdkBrowseActionPerformed

    private void cordovaSdkDownloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cordovaSdkDownloadMouseClicked
        try {
            URLDisplayer.getDefault().showURL(new URL("http://phonegap.com/download"));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_cordovaSdkDownloadMouseClicked

    void load() {
        if (documentL != null) {
            androidSdkField.getDocument().removeDocumentListener(documentL);
            cordovaSdkField.getDocument().removeDocumentListener(documentL);
        }
        androidSdkField.setText(AndroidPlatform.getDefault().getSdkLocation());
        cordovaSdkField.setText(CordovaPlatform.getDefault().getSdkLocation());
        
        androidSdkField.getDocument().addDocumentListener(documentL);
        cordovaSdkField.getDocument().addDocumentListener(documentL);
    }

    void store() {
        AndroidPlatform.getDefault().setSdkLocation(androidSdkField.getText());
        CordovaPlatform.getDefault().setSdkLocation(cordovaSdkField.getText());
    }

    boolean valid() {
        File androidLoc = new File(androidSdkField.getText());
        File androidTools = new File(androidLoc, "tools");
        boolean adroidValid = androidLoc.exists() && androidLoc.isDirectory()
                               && androidTools.exists() && androidTools.isDirectory();

        File cordovaLoc = new File(cordovaSdkField.getText());
        File cordovaAndroid = new File(cordovaLoc, "lib/android");
        File cordovaIOS = new File(cordovaLoc, "lib/ios");
        boolean cordovaValid = cordovaLoc.exists() && cordovaLoc.isDirectory()
                               && cordovaAndroid.exists() && cordovaAndroid.isDirectory()
                               && cordovaIOS.exists() && cordovaIOS.isDirectory() ;
        
        return adroidValid && cordovaValid;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel androidPanel;
    private javax.swing.JButton androidSdkBrowse;
    private javax.swing.JLabel androidSdkDownload;
    private javax.swing.JTextField androidSdkField;
    private javax.swing.JLabel androidSdkLabel;
    private javax.swing.JPanel cordovaPanel;
    private javax.swing.JButton cordovaSdkBrowse;
    private javax.swing.JLabel cordovaSdkDownload;
    private javax.swing.JTextField cordovaSdkField;
    private javax.swing.JLabel cordovaSdkLabel;
    // End of variables declaration//GEN-END:variables
}
