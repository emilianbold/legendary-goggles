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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.cnd.discovery.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.netbeans.modules.cnd.discovery.wizard.api.DiscoveryDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
public class SelectModePanel extends javax.swing.JPanel {
    private boolean first = true;
    private boolean lastApplicable;
    private SelectModeWizard wizard;
    
    /** Creates new form SelectModePanel */
    public SelectModePanel(SelectModeWizard wizard) {
        this.wizard = wizard;
        initComponents();
        addListeners();
    }

    private void addListeners(){
        simpleMode.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInstruction();
            }
        });
        advancedMode.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInstruction();
            }
        });
        updateInstruction();
    }
    
    private void updateInstruction(){
        if (simpleMode.isSelected()){
            instructionsTextArea.setText(getString("SelectModeSimpleInstructionText")); // NOI18N
        } else {
            instructionsTextArea.setText(getString("SelectModeAdvancedInstructionText")); // NOI18N
        }
        wizard.stateChanged(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        modeLabel = new javax.swing.JLabel();
        simpleMode = new javax.swing.JRadioButton();
        advancedMode = new javax.swing.JRadioButton();
        instructionPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/discovery/wizard/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(modeLabel, bundle.getString("SelectModeLabelText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(modeLabel, gridBagConstraints);

        buttonGroup1.add(simpleMode);
        simpleMode.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(simpleMode, bundle.getString("SimpleModeButtonText")); // NOI18N
        simpleMode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        simpleMode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        add(simpleMode, gridBagConstraints);

        buttonGroup1.add(advancedMode);
        org.openide.awt.Mnemonics.setLocalizedText(advancedMode, bundle.getString("AdvancedModeButtonText")); // NOI18N
        advancedMode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        advancedMode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 0);
        add(advancedMode, gridBagConstraints);

        instructionPanel.setLayout(new java.awt.BorderLayout());

        instructionsTextArea.setBackground(instructionPanel.getBackground());
        instructionsTextArea.setEditable(false);
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setWrapStyleWord(true);
        instructionsTextArea.setOpaque(false);
        instructionPanel.add(instructionsTextArea, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(instructionPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    void read(final DiscoveryDescriptor wizardDescriptor) {
        updateControls();
    }
    
    void updateControls(){
        if (!first) {
            if (lastApplicable){
                advancedMode.setEnabled(true);
                simpleMode.setEnabled(true);
                if (first) {
                    simpleMode.setSelected(true);
                }
            } else {
                advancedMode.setEnabled(true);
                simpleMode.setEnabled(true);
                advancedMode.setSelected(true);
            }
        } else {
            advancedMode.setEnabled(true);
            simpleMode.setEnabled(true);
            simpleMode.setSelected(true);
        }
        updateInstruction();
    }

    void enableControls(boolean enable){
        advancedMode.setEnabled(enable);
        simpleMode.setEnabled(enable);
    }

    boolean isApplicable(final DiscoveryDescriptor wizardDescriptor){
        if (first && simpleMode.isSelected()) {
            lastApplicable = new DiscoveryExtension().isApplicable(wizardDescriptor, false).isApplicable();
            if (!lastApplicable) {
                wizardDescriptor.setSimpleMode(false);
            }
            first = false;
        }
        return lastApplicable;
    }
    
    void store(DiscoveryDescriptor wizardDescriptor) {
        wizardDescriptor.setSimpleMode(simpleMode.isSelected());
    }
    
    boolean valid(DiscoveryDescriptor wizardDescriptor) {
        if (first) {
            return true;
        }
        if (simpleMode.isSelected()){
            if (!lastApplicable){
                String selectedExecutable = wizardDescriptor.getBuildResult();
                if (selectedExecutable == null || selectedExecutable.length()==0) {
                    wizardDescriptor.setMessage(getString("SimpleMode.Error.NoOutputResult")); // NOI18N
                    return false;
                }
                File file = new File(selectedExecutable);
                if (!file.exists()) {
                    wizardDescriptor.setMessage(getString("SimpleMode.Error.OutputResultNotExist")); // NOI18N
                    return false;
                }
                wizardDescriptor.setMessage(getString("SimpleMode.Error.NoDebugOutputResult")); // NOI18N
                return false;
            }
        }
        return true;
    }
    
    private String getString(String key) {
        return NbBundle.getBundle(SelectModePanel.class).getString(key);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton advancedMode;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JRadioButton simpleMode;
    // End of variables declaration//GEN-END:variables
    
}
