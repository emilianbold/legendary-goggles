/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.codeception.ui;

import java.awt.EventQueue;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.codeception.commands.Codecept;
import org.netbeans.modules.php.codeception.commands.Codecept.GenerateCommand;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.Pair;

public final class CodeceptionCreateTestPanel extends JPanel {

    private CodeceptionCreateTestPanel(List<GenerateCommand> commands, List<String> suites) {
        assert EventQueue.isDispatchThread();
        assert suites != null;
        assert commands != null;

        initComponents();
        init(commands, suites);
    }

    private void init(List<GenerateCommand> commands, List<String> suites) {
        for (String suite : suites) {
            suitesComboBox.addItem(suite);
        }
        for (GenerateCommand command : commands) {
            generateComboBox.addItem(command);
        }
    }

    @NbBundle.Messages("CodeceptionCreateTestPanel.dialog.title=Create Test")
    @CheckForNull
    public static Pair<GenerateCommand, String> showDialog(List<GenerateCommand> commands, List<String> suites) {
        final List<String> suitesCopy = new CopyOnWriteArrayList<>(suites);
        final List<GenerateCommand> commandsCopy = new CopyOnWriteArrayList<>(commands);
        return Mutex.EVENT.readAccess(new Mutex.Action<Pair<GenerateCommand, String>>() {
            @Override
            public Pair<GenerateCommand, String> run() {
                assert EventQueue.isDispatchThread();
                CodeceptionCreateTestPanel panel = new CodeceptionCreateTestPanel(commandsCopy, suitesCopy);
                NotifyDescriptor descriptor = new NotifyDescriptor(
                        panel,
                        Bundle.CodeceptionCreateTestPanel_dialog_title(),
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.PLAIN_MESSAGE,
                        null,
                        NotifyDescriptor.OK_OPTION);
                if (DialogDisplayer.getDefault().notify(descriptor) != NotifyDescriptor.OK_OPTION) {
                    return null;
                }
                GenerateCommand selectedCommand = panel.getSelectedCommand();
                String selectedSuite = panel.getSelectedSuite();
                if (selectedCommand == null || selectedSuite == null) {
                    return null;
                }
                return Pair.of(panel.getSelectedCommand(), panel.getSelectedSuite());
            }
        });
    }

    public String getSelectedSuite() {
        return (String) suitesComboBox.getSelectedItem();
    }

    public GenerateCommand getSelectedCommand() {
        return (GenerateCommand) generateComboBox.getSelectedItem();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        commandLabel = new JLabel();
        generateComboBox = new JComboBox<Codecept.GenerateCommand>();
        suiteLabel = new JLabel();
        suitesComboBox = new JComboBox<String>();

        commandLabel.setLabelFor(generateComboBox);
        Mnemonics.setLocalizedText(commandLabel, NbBundle.getMessage(CodeceptionCreateTestPanel.class, "CodeceptionCreateTestPanel.commandLabel.text")); // NOI18N

        suiteLabel.setLabelFor(suitesComboBox);
        Mnemonics.setLocalizedText(suiteLabel, NbBundle.getMessage(CodeceptionCreateTestPanel.class, "CodeceptionCreateTestPanel.suiteLabel.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(commandLabel)
                    .addComponent(suiteLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(suitesComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generateComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(commandLabel)
                    .addComponent(generateComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(suiteLabel)
                    .addComponent(suitesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel commandLabel;
    private JComboBox<Codecept.GenerateCommand> generateComboBox;
    private JLabel suiteLabel;
    private JComboBox<String> suitesComboBox;
    // End of variables declaration//GEN-END:variables

}
