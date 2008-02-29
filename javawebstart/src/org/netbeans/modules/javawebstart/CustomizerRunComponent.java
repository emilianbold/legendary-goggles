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

package org.netbeans.modules.javawebstart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import org.netbeans.modules.java.j2seproject.api.J2SERunConfigProvider;

/**
 * 
 * @author  Milan Kubec
 */
public class CustomizerRunComponent extends javax.swing.JPanel implements ActionListener {
    
    private Map<String,String> runSelectedMap = new HashMap<String,String>();
    private Map<String,String> runUnselectedMap = new HashMap<String,String>();
    
    private J2SERunConfigProvider.ConfigChangeListener listener;
    
    public CustomizerRunComponent() {
        initComponents();
        runCheckBox.addActionListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        runCheckBox = new javax.swing.JCheckBox();
        hintLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(runCheckBox, org.openide.util.NbBundle.getMessage(CustomizerRunComponent.class, "LBL_Run_with_JWS")); // NOI18N
        runCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        runCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(runCheckBox, gridBagConstraints);
        runCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomizerRunComponent.class, "ACSN_Run_With_JWS")); // NOI18N
        runCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRunComponent.class, "ACSD_Run_With_JWS")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(hintLabel, org.openide.util.NbBundle.getMessage(CustomizerRunComponent.class, "HINT_Run_with_JWS")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 22, 0, 0);
        add(hintLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    public void addListener(J2SERunConfigProvider.ConfigChangeListener l) {
        listener = l;
    }
    
    public void setCheckboxEnabled(boolean b) {
        runCheckBox.setEnabled(b);
    }
    
    public void setCheckboxSelected(boolean b) {
        runCheckBox.setSelected(b);
    }
    
    public void setHintVisible(boolean b) {
        hintLabel.setVisible(b);
    }
    
    public void actionPerformed(ActionEvent e) {
        initMaps();
        if (runCheckBox.isSelected()) {
            listener.propertiesChanged(runSelectedMap);
        } else {
            listener.propertiesChanged(runUnselectedMap);
        }
    }
    
    private void initMaps() {
        runUnselectedMap.put("$target.run", null);
        runUnselectedMap.put("$target.debug", null);
        runSelectedMap.put("$target.run", "jws-run");
        runSelectedMap.put("$target.debug", "jws-debug");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hintLabel;
    private javax.swing.JCheckBox runCheckBox;
    // End of variables declaration//GEN-END:variables
    
}
