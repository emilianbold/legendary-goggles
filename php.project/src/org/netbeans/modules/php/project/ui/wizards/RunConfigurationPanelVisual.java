/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.wizards;

import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.netbeans.modules.php.project.connections.ConfigManager;
import org.netbeans.modules.php.project.ui.SourcesFolderProvider;
import org.netbeans.modules.php.project.ui.customizer.RunAsPanel;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik
 */
public class RunConfigurationPanelVisual extends JPanel {
    private static final long serialVersionUID = -2998474632476302L;
    private static final int STEP_INDEX = 1;

    private final RunAsPanel.InsidePanel[] insidePanels;

    public RunConfigurationPanelVisual(RunConfigurationPanel wizardPanel, SourcesFolderProvider sourcesFolderProvider,
            ConfigManager configManager, RunAsPanel.InsidePanel[] insidePanels) {
        setName(wizardPanel.getSteps()[STEP_INDEX]);
        putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, STEP_INDEX);
        // Step name (actually the whole list for reference).
        putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, wizardPanel.getSteps());

        this.insidePanels = insidePanels.clone();
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

        infoLabel1 = new JLabel();
        infoLabel2 = new JLabel();
        runAsPanel = new RunAsPanel(insidePanels);

        Mnemonics.setLocalizedText(infoLabel1, NbBundle.getMessage(RunConfigurationPanelVisual.class, "LBL_RunConfigurationInfo1")); // NOI18N

        Mnemonics.setLocalizedText(infoLabel2, NbBundle.getMessage(RunConfigurationPanelVisual.class, "LBL_RunConfigurationInfo2")); // NOI18N

        runAsPanel.setLayout(new CardLayout());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(runAsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(infoLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(infoLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(infoLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(runAsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        infoLabel1.getAccessibleContext().setAccessibleName(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.infoLabel1.AccessibleContext.accessibleName")); // NOI18N
        infoLabel1.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.infoLabel1.AccessibleContext.accessibleDescription")); // NOI18N
        infoLabel2.getAccessibleContext().setAccessibleName(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.infoLabel2.AccessibleContext.accessibleName")); // NOI18N
        infoLabel2.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.infoLabel2.AccessibleContext.accessibleDescription")); // NOI18N
        runAsPanel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.runAsPanel.AccessibleContext.accessibleName")); // NOI18N
        runAsPanel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.runAsPanel.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(RunConfigurationPanelVisual.class, "RunConfigurationPanelVisual.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel infoLabel1;
    private JLabel infoLabel2;
    private JPanel runAsPanel;
    // End of variables declaration//GEN-END:variables
}
