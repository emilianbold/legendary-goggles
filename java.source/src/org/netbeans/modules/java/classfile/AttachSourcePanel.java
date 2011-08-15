/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.classfile;

import java.net.URL;
import javax.swing.SwingUtilities;
import org.netbeans.api.actions.Openable;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.queries.SourceJavadocAttacher;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;


public class AttachSourcePanel extends javax.swing.JPanel {

    private final URL root;
    private final URL file;
    private final String binaryName;

    public AttachSourcePanel(
            @NonNull final URL root,
            @NonNull final URL file,
            @NonNull final String binaryName) {
        assert root != null;
        assert file != null;
        assert binaryName != null;
        this.root = root;
        this.file = file;
        this.binaryName = binaryName;
        initComponents();
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
        jButton1 = new javax.swing.JButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AttachSourcePanel.class, "AttachSourcePanel.jLabel1.text")); // NOI18N

        jButton1.setText(org.openide.util.NbBundle.getMessage(AttachSourcePanel.class, "AttachSourcePanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachSources(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                .addGap(22, 22, 22)
                .addComponent(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void attachSources(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachSources
        jButton1.setEnabled(false);
        SourceJavadocAttacher.attachSources(root, new SourceJavadocAttacher.AttachmentListener() {
            @Override
            public void attachmentSucceeded() {
                boolean success = false;
                final FileObject rootFo = URLMapper.findFileObject(root);
                final FileObject fileFo = URLMapper.findFileObject(file);
                if (rootFo != null && fileFo != null) {
                    final FileObject[] fos = SourceForBinaryQuery.findSourceRoots(root).getRoots();
                    if (fos.length > 0) {
                        final ClassPath cp = ClassPathSupport.createClassPath(fos);
                        final FileObject newFileFo = cp.findResource(binaryName + ".java"); //NOI18N
                        if (newFileFo != null) {
                            try {
                                final EditorCookie ec = DataObject.find(fileFo).getLookup().lookup(EditorCookie.class);
                                final Openable open = DataObject.find(newFileFo).getLookup().lookup(Openable.class);
                                if (ec != null && open != null) {
                                    ec.close();
                                    open.open();
                                    success = true;
                                }
                            } catch (DataObjectNotFoundException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
                if (!success) {
                    enableAttach();
                }
            }

            @Override
            public void attachmentFailed() {
                enableAttach();
            }

            private void enableAttach() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jButton1.setEnabled(true);
                    }
                });
            }
        });
}//GEN-LAST:event_attachSources

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
