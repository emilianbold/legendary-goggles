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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.smarty.ui.customizer;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.smarty.ui.options.SmartyOptionsPanelController;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

public class SmartyCustomizerPanel extends JPanel {

    private static final long serialVersionUID = 173459120857644L;

    public SmartyCustomizerPanel() {
        initComponents();
        init();
    }

    private void init() {
        enabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setFieldsEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        setFieldsEnabled(enabledCheckBox.isSelected());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        customOpenDelimiterLabel = new JLabel();
        customOpenDelimiterTextField = new JTextField();
        customDelimitersLabel = new JLabel();
        optionsLabel = new JLabel();
        customCloseDelimiterLabel = new JLabel();
        customCloseDelimiterTextField = new JTextField();
        enabledCheckBox = new JCheckBox();
        enabledInfoLabel = new JLabel();

        Mnemonics.setLocalizedText(customOpenDelimiterLabel, NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.customOpenDelimiterLabel.text")); // NOI18N

        customOpenDelimiterTextField.setText(NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.customOpenDelimiterTextField.text")); // NOI18N

        customDelimitersLabel.setForeground(UIManager.getColor("Label.foreground"));
        Mnemonics.setLocalizedText(customDelimitersLabel, NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.customDelimitersLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(optionsLabel, NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.optionsLabel.text")); // NOI18N
        optionsLabel.setToolTipText(NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.optionsLabel.toolTipText")); // NOI18N
        optionsLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                optionsLabelMouseEntered(evt);
            }
            public void mousePressed(MouseEvent evt) {
                optionsLabelMousePressed(evt);
            }
        });

        Mnemonics.setLocalizedText(customCloseDelimiterLabel, NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.customCloseDelimiterLabel.text")); // NOI18N

        customCloseDelimiterTextField.setText(NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.customCloseDelimiterTextField.text")); // NOI18N

        Mnemonics.setLocalizedText(enabledCheckBox, NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.enabledCheckBox.text")); // NOI18N

        Mnemonics.setLocalizedText(enabledInfoLabel, NbBundle.getMessage(SmartyCustomizerPanel.class, "SmartyCustomizerPanel.enabledInfoLabel.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(customDelimitersLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addComponent(optionsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(enabledInfoLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(customCloseDelimiterLabel)
                            .addComponent(customOpenDelimiterLabel))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(customOpenDelimiterTextField, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addComponent(customCloseDelimiterTextField, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                    .addComponent(enabledCheckBox))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {customCloseDelimiterTextField, customOpenDelimiterTextField});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(optionsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(enabledCheckBox)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(enabledInfoLabel)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(customOpenDelimiterLabel)
                            .addComponent(customOpenDelimiterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(customCloseDelimiterLabel)
                            .addComponent(customCloseDelimiterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(customDelimitersLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(153, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void optionsLabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_optionsLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_optionsLabelMouseEntered

    private void optionsLabelMousePressed(MouseEvent evt) {//GEN-FIRST:event_optionsLabelMousePressed
        UiUtils.showOptions(SmartyOptionsPanelController.OPTIONS_SUB_PATH);
}//GEN-LAST:event_optionsLabelMousePressed

    public String getCustomCloseDelimiterTextField() {
        return customCloseDelimiterTextField.getText();
    }

    public void setCustomCloseDelimiterText(String customCloseDelimiterText) {
        this.customCloseDelimiterTextField.setText(customCloseDelimiterText);
    }

    public String getCustomOpenDelimiterTextField() {
        return customOpenDelimiterTextField.getText();
    }

    public void setCustomOpenDelimiterText(String customOpenDelimiterText) {
        this.customOpenDelimiterTextField.setText(customOpenDelimiterText);
    }

    final void setFieldsEnabled(boolean enabled) {
        customOpenDelimiterTextField.setEnabled(enabled);
        customCloseDelimiterTextField.setEnabled(enabled);
    }

    public boolean isSupportEnabled() {
        return enabledCheckBox.isSelected();
    }

    public void setSupportEnabled(boolean enabled) {
        enabledCheckBox.setSelected(enabled);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel customCloseDelimiterLabel;
    private JTextField customCloseDelimiterTextField;
    private JLabel customDelimitersLabel;
    private JLabel customOpenDelimiterLabel;
    private JTextField customOpenDelimiterTextField;
    private JCheckBox enabledCheckBox;
    private JLabel enabledInfoLabel;
    private JLabel optionsLabel;
    // End of variables declaration//GEN-END:variables
}
