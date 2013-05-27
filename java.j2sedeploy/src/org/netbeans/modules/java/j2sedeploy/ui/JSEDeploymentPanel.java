/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.j2sedeploy.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.j2sedeploy.J2SEDeployProperties;
import org.netbeans.modules.java.j2sedeploy.api.J2SEDeployConstants;
import org.netbeans.modules.java.j2seproject.api.J2SECategoryExtensionProvider;
import org.openide.util.HelpCtx;

/**
 *
 * @author Petr Somol
 */
public class JSEDeploymentPanel extends javax.swing.JPanel implements HelpCtx.Provider {

    private final J2SEDeployProperties props;
    private Project project;
    private java.util.List<J2SECategoryExtensionProvider> compProviders = new LinkedList<>();
    private int nextExtensionYPos;
    private List<ActionListener> okListener = new ArrayList<>();
    private List<ActionListener> storeListener = new ArrayList<>();
    private List<ActionListener> closeListener = new ArrayList<>();
    
    /**
     * Creates new form JSEDeploymentPanel
     */
    public JSEDeploymentPanel(J2SEDeployProperties props) {
        this.props = props;
        initComponents();
        checkBoxNativePackaging.setSelected(props.getNativeBundlingEnabled());
        this.project = props.getProject();
        for (J2SECategoryExtensionProvider compProvider : project.getLookup().lookupAll(J2SECategoryExtensionProvider.class)) {
            if( compProvider.getCategory() == J2SECategoryExtensionProvider.ExtensibleCategory.DEPLOYMENT ) {
                if( addExtPanel(project,compProvider,nextExtensionYPos) ) {
                    compProviders.add(compProvider);
                    nextExtensionYPos++;
                }
            }
        }
        addPanelFiller(nextExtensionYPos);
    }
    
    @NonNull
    public List<ActionListener> getOKListeners() {
        return okListener;
    }

    @NonNull
    public List<ActionListener> getStoreListeners() {
        return storeListener;
    }

    @NonNull
    public List<ActionListener> getCloseListeners() {
        return closeListener;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        labelNativePackaging = new javax.swing.JLabel();
        checkBoxNativePackaging = new javax.swing.JCheckBox();
        extPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(550, 400));
        setLayout(new java.awt.GridBagLayout());

        mainPanel.setPreferredSize(new java.awt.Dimension(550, 60));
        mainPanel.setLayout(new java.awt.GridBagLayout());

        labelNativePackaging.setLabelFor(checkBoxNativePackaging);
        org.openide.awt.Mnemonics.setLocalizedText(labelNativePackaging, org.openide.util.NbBundle.getMessage(JSEDeploymentPanel.class, "JSEDeploymentPanel.labelNativePackaging.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        mainPanel.add(labelNativePackaging, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(checkBoxNativePackaging, org.openide.util.NbBundle.getMessage(JSEDeploymentPanel.class, "JSEDeploymentPanel.checkBoxNativePackaging.text")); // NOI18N
        checkBoxNativePackaging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxNativePackagingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(7, 15, 0, 0);
        mainPanel.add(checkBoxNativePackaging, gridBagConstraints);
        checkBoxNativePackaging.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JSEDeploymentPanel.class, "AN_JSEDeploymentPanel.checkBoxNativePackaging")); // NOI18N
        checkBoxNativePackaging.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JSEDeploymentPanel.class, "AD_JSEDeploymentPanel.checkBoxNativePackaging")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(mainPanel, gridBagConstraints);

        extPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(extPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxNativePackagingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxNativePackagingActionPerformed
        boolean sel = checkBoxNativePackaging.isSelected();
        props.setNativeBundlingEnabled(sel);
    }//GEN-LAST:event_checkBoxNativePackagingActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxNativePackaging;
    private javax.swing.JPanel extPanel;
    private javax.swing.JLabel labelNativePackaging;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(JSEDeploymentPanel.class);
    }
    
    private boolean addExtPanel(Project p, J2SECategoryExtensionProvider compProvider, int gridY) {
        if (compProvider != null) {
            J2SECategoryExtensionProvider.ConfigChangeListener ccl = new J2SECategoryExtensionProvider.ConfigChangeListener() {
                @Override
                public void propertiesChanged(Map<String, String> updates) {
                }
            };
            JComponent comp = compProvider.createComponent(p, ccl);
            if (comp != null) {
                java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints();
                constraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraints.gridx = 0;
                constraints.gridy = gridY;
                constraints.weightx = 1.0;
                extPanel.add(comp, constraints);
                
                // extract listeners if exist
                Object okObject = comp.getClientProperty(J2SEDeployConstants.PASS_OK_LISTENER);
                if(okObject != null && okObject instanceof ActionListener) {
                    okListener.add((ActionListener)okObject);
                }
                Object storeObject = comp.getClientProperty(J2SEDeployConstants.PASS_STORE_LISTENER);
                if(storeObject != null && storeObject instanceof ActionListener) {
                    storeListener.add((ActionListener)storeObject);
                }
                Object closeObject = comp.getClientProperty(J2SEDeployConstants.PASS_CLOSE_LISTENER);
                if(closeObject != null && closeObject instanceof ActionListener) {
                    closeListener.add((ActionListener)closeObject);
                }
                
                return true;
            }
        }
        return false;
    }

    private void addPanelFiller(int gridY) {
        java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints();
        constraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = gridY;
        //constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        extPanel.add( new Box.Filler(
                new Dimension(), 
                new Dimension(),
                new Dimension(10000,10000) ),
                constraints);
    }

}
