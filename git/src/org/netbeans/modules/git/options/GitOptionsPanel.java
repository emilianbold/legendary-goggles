/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.git.options;


import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.git.GitModuleConfig;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

@OptionsPanelController.Keywords(keywords={"git", "#GitOptionsPanel.kw1", "#GitOptionsPanel.kw2", "#GitOptionsPanel.kw3", "#GitOptionsPanel.kw4"},
        location=OptionsDisplayer.ADVANCED, tabTitle="#CTL_OptionsPanel.title")
@NbBundle.Messages("CTL_OptionsPanel.title=Versioning")
final class GitOptionsPanel extends javax.swing.JPanel {
    
    private final GitOptionsPanelController controller;
    
    GitOptionsPanel(GitOptionsPanelController controller) {
        this.controller = controller;        
        initComponents();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();        
    }

    @Override
    public void removeNotify() {        
        super.removeNotify();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbOpenOutputWindow = new javax.swing.JCheckBox();
        excludeNewFiles = new javax.swing.JCheckBox();
        cbIgnoreNotSharableFiles = new javax.swing.JCheckBox();

        cbOpenOutputWindow.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbOpenOutputWindow, org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.cbOpenOutputWindow.text")); // NOI18N
        cbOpenOutputWindow.setToolTipText(org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "ACSD_cbOpenOutputWindow")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(excludeNewFiles, org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.excludeNewFiles.text")); // NOI18N
        excludeNewFiles.setToolTipText(org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.excludeNewFiles.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(signOffCheckBox, org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.signOffCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbIgnoreNotSharableFiles, org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.cbIgnoreNotSharableFiles.text")); // NOI18N
        cbIgnoreNotSharableFiles.setToolTipText(org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.cbIgnoreNotSharableFiles.toolTipText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(signOffCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(excludeNewFiles)
                            .addComponent(cbOpenOutputWindow)
                            .addComponent(cbIgnoreNotSharableFiles))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbOpenOutputWindow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(excludeNewFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(signOffCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbIgnoreNotSharableFiles)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cbOpenOutputWindow.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "ACSD_cbOpenOutputWindow")); // NOI18N
        excludeNewFiles.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.excludeNewFiles.text")); // NOI18N
        excludeNewFiles.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GitOptionsPanel.class, "GitOptionsPanel.excludeNewFiles.toolTipText")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    void load() {        
        cbOpenOutputWindow.setSelected(GitModuleConfig.getDefault().getAutoOpenOutput());        
        excludeNewFiles.setSelected(GitModuleConfig.getDefault().getExludeNewFiles());
        signOffCheckBox.setSelected(GitModuleConfig.getDefault().getSignOff());
        cbIgnoreNotSharableFiles.setSelected(GitModuleConfig.getDefault().getAutoIgnoreFiles());
    }
    
    void store() {
        GitModuleConfig.getDefault().setAutoOpenOutput(cbOpenOutputWindow.isSelected());
        GitModuleConfig.getDefault().setExcludeNewFiles(excludeNewFiles.isSelected());
        GitModuleConfig.getDefault().setSignOff(signOffCheckBox.isSelected());
        GitModuleConfig.getDefault().setAutoIgnoreFiles(cbIgnoreNotSharableFiles.isSelected());
    }
    
    boolean valid() {
        return true;
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbIgnoreNotSharableFiles;
    private javax.swing.JCheckBox cbOpenOutputWindow;
    private javax.swing.JCheckBox excludeNewFiles;
    final javax.swing.JCheckBox signOffCheckBox = new javax.swing.JCheckBox();
    // End of variables declaration//GEN-END:variables
    
}
