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
package org.netbeans.modules.mobility.svgcore.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.mobility.svgcore.api.snippets.SVGSnippetsProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

final class SvgcorePanel extends javax.swing.JPanel {

    private final SvgcoreOptionsPanelController controller;
    private String providerName;

    SvgcorePanel(SvgcoreOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
        jComboBox1.setModel(new SnippetsComboModel());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pathTextField = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        snippetsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 8));

        editorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SvgcorePanel.class, "LBL_EditorFrame"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SvgcorePanel.class, "LBL_AnimatorPath")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonBrowse, org.openide.util.NbBundle.getMessage(SvgcorePanel.class, "LBL_ButtonChange")); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout editorPanelLayout = new org.jdesktop.layout.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pathTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonBrowse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(editorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(pathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonBrowse))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        snippetsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SvgcorePanel.class, "LBL_SVGFrame"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SvgcorePanel.class, "LBL_SnippetsName")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(136, 16));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout snippetsPanelLayout = new org.jdesktop.layout.GroupLayout(snippetsPanel);
        snippetsPanel.setLayout(snippetsPanelLayout);
        snippetsPanelLayout.setHorizontalGroup(
            snippetsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(snippetsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBox1, 0, 473, Short.MAX_VALUE)
                .addContainerGap())
        );
        snippetsPanelLayout.setVerticalGroup(
            snippetsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(snippetsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(snippetsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(snippetsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(editorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(snippetsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(39, 39, 39))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        JFileChooser chooser = new JFileChooser(pathTextField.getText());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int r = chooser.showDialog(
                SwingUtilities.getWindowAncestor(this),
                org.openide.util.NbBundle.getMessage(SvgcorePanel.class, "LBL_Select"));
        if (r == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.isFile()) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        NbBundle.getMessage(SvgcorePanel.class, "ERROR_NotFile", file),
                        NotifyDescriptor.Message.WARNING_MESSAGE));
                return;
            }
            pathTextField.setText(file.getAbsoluteFile().toString());
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    void load() {
        pathTextField.setText(SvgcoreSettings.getDefault().getExternalEditorPath());
        providerName = SvgcoreSettings.getDefault().getCurrentSnippet();
    }

    void store() {
        SvgcoreSettings.getDefault().setExternalEditorPath(pathTextField.getText());
        SvgcoreSettings.getDefault().setCurrentSnippet(providerName);
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel editorPanel;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField pathTextField;
    private javax.swing.JPanel snippetsPanel;
    // End of variables declaration//GEN-END:variables

    private class SnippetsComboModel implements ComboBoxModel {

        private List<String> snippetsNameList;

        public SnippetsComboModel() {
            Collection snippets = getListSnippets();
            snippetsNameList = new ArrayList(snippets.size());
            for (SVGSnippetsProvider provider : getListSnippets()) {
                snippetsNameList.add(provider.getName());
                if (providerName != null && providerName.equals(provider.getName())) {
                    providerName = provider.getName();
                }
            }
        }

        public void setSelectedItem(Object anItem) {
            providerName = getName((String) anItem);
        }

        public Object getSelectedItem() {
            return getDisplyName(providerName);
        }

        public int getSize() {
            return snippetsNameList.size();
        }

        public Object getElementAt(int index) {
            return getDisplyName(snippetsNameList.get(index));
        }

        public void addListDataListener(ListDataListener l) {
        }

        public void removeListDataListener(ListDataListener l) {
        }
    }

    private static final Collection<? extends SVGSnippetsProvider> getListSnippets() {
        Collection<? extends SVGSnippetsProvider> snippetCollection = Lookup.getDefault().lookupAll(SVGSnippetsProvider.class);
        return snippetCollection;
    }

    private static final String getDisplyName(String providerName) {
        for (SVGSnippetsProvider provider : getListSnippets()) {
            if (providerName != null && providerName.equals(provider.getName())) {
                return provider.getDisplayName();
            }
        }
        return null;
    }

    private static final String getName(String displayName) {
        for (SVGSnippetsProvider provider : getListSnippets()) {
            if (displayName != null && displayName.equals(provider.getDisplayName())) {
                return provider.getName();
            }
        }
        return null;
    }
}
