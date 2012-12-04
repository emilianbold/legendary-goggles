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

package org.netbeans.modules.groovy.support.wizard;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.modules.groovy.support.api.GroovySources;
import org.netbeans.modules.groovy.support.spi.GroovyExtender;
import org.netbeans.modules.groovy.support.wizard.impl.AntProjectTypeStrategy;
import org.netbeans.modules.groovy.support.wizard.impl.MavenProjectTypeStrategy;
import org.netbeans.modules.gsf.testrunner.api.SelfResizingPanel;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 * General wizard iterator implementation for Groovy JUnit Test.
 *
 * This class has been created because we need to hook to some of the WizardIterator
 * methods (e.g. we have to create test source root if it doesn't exist when creating
 * new Groovy JUnit test, we have to add JUnit and Groovy library dependencies etc.).
 *
 * The implementation depends on the actual project type, because the actions are
 * different for Ant and Maven projects (e.g. in Maven we need to add dependency
 * to pom.xml file, but in Ant we just add library to the classpath, etc.).
 *
 * @see ProjectTypeStrategy
 * @see AntProjectTypeStrategy
 * @see MavenProjectTypeStrategy
 *
 * @author Martin Janicek
 */
@TemplateRegistrations(value = {
    @TemplateRegistration(
        folder = "Groovy",
        position = 121,
        content = "/org/netbeans/modules/groovy/support/resources/GroovyJUnitTest.groovy",
        scriptEngine = "freemarker",
        displayName = "Groovy JUnit Test",
        iconBase = "org/netbeans/modules/groovy/support/resources/GroovyFile16x16.png",
        description = "/org/netbeans/modules/groovy/support/resources/GroovyJUnitTest.html",
        category = {"groovy", "java-main-class"}),

    @TemplateRegistration(
        folder = "Groovy",
        position = 131,
        content = "/org/netbeans/modules/groovy/support/resources/GroovyJUnit3Test.groovy",
        scriptEngine = "freemarker",
        iconBase = "org/netbeans/modules/groovy/support/resources/GroovyFile16x16.png",
        description = "/org/netbeans/modules/groovy/support/resources/GroovyJUnitTest.html",
        category = "invisible"),

    @TemplateRegistration(
        folder = "Groovy",
        position = 141,
        content = "/org/netbeans/modules/groovy/support/resources/GroovyJUnit4Test.groovy",
        scriptEngine = "freemarker",
        iconBase = "org/netbeans/modules/groovy/support/resources/GroovyFile16x16.png",
        description = "/org/netbeans/modules/groovy/support/resources/GroovyJUnitTest.html",
        category = "invisible")
})
public final class GroovyJUnitTestWizardIterator extends GroovyFileWizardIterator {

    private static final Logger LOGGER = Logger.getLogger(GroovyJUnitTestWizardIterator.class.getName());
    private static final ResourceBundle BUNDLE = NbBundle.getBundle(GroovyJUnitTestWizardIterator.class);


    private GroovyJUnitTestWizardIterator() {
    }

    @Override
    protected List<SourceGroup> getOrderedSourcesGroups(WizardDescriptor wizardDescriptor, List<SourceGroup> groups) {
        LOGGER.finest("getOrderedSourcesGroups(...) method entered");
        LOGGER.finest("Source groups at the beginning of the method: \n");
        printSourceGroups(groups);

        if (!strategy.existsGroovyTestFolder(groups)) {
            LOGGER.finest("Groovy test folder doesn't exist");
            LOGGER.finest("\nProject structure before the strategy.createGroovyTestFolder() call: \n");
            printProjectStructure();
            strategy.createGroovyTestFolder();

            LOGGER.finest("\nProject structure after the strategy.createGroovyTestFolder() call: \n");
            printProjectStructure();

            // Retrieve the source groups again, but now with a newly created /test/groovy folder
            groups = GroovySources.getGroovySourceGroups(ProjectUtils.getSources(project));

            LOGGER.finest("\nSource groups after the test folder creation: \n");
            printSourceGroups(groups);
        }
        final List<SourceGroup> testSourceGroups = strategy.getOnlyTestSourceGroups(groups);

        LOGGER.finest("\nTest source groups only: \n");
        printSourceGroups(testSourceGroups);

        if (!testSourceGroups.isEmpty()) {
            return testSourceGroups;
        } else {
            return groups;
        }
    }

    private void printProjectStructure() {
        for (FileObject child : project.getProjectDirectory().getChildren()) {
            LOGGER.log(Level.FINEST, "Child name: {0}", child.getName());
        }
    }

    private void printSourceGroups(List<SourceGroup> groups) {
        for (SourceGroup group : groups) {
            LOGGER.log(Level.FINEST, "Group name: {0}, Group root: {1}", new Object[] {group.getDisplayName(), group.getRootFolder()});
        }
    }

    @Override
    public Set instantiate(ProgressHandle handle) throws IOException {
        handle.start();
        handle.progress(NbBundle.getMessage(GroovyJUnitTestWizardIterator.class, "LBL_NewGroovyFileWizardIterator_WizardProgress_CreatingFile")); // NOI18N

        JUnit currentJUnit = strategy.findJUnitVersion();
        if (currentJUnit == JUnit.NOT_DECLARED) {
            JUnit jUnitToUse = askUserWhichJUnitToUse();
            if (jUnitToUse == null) {
                return Collections.emptySet();
            }

            strategy.setjUnitVersion(jUnitToUse);
            strategy.addJUnitLibrary(jUnitToUse);
        } else {
            strategy.setjUnitVersion(currentJUnit);
        }

        FileObject template = strategy.findTemplate(wiz);
        FileObject targetFolder = Templates.getTargetFolder(wiz);
        String targetName = Templates.getTargetName(wiz);

        DataFolder dFolder = DataFolder.findFolder(targetFolder);
        DataObject dTemplate = DataObject.find(template);

        String pkgName = getPackageName(targetFolder);
        DataObject dobj;
        if (pkgName == null) {
            dobj = dTemplate.createFromTemplate(dFolder, targetName);
        } else {
            dobj = dTemplate.createFromTemplate(dFolder, targetName, Collections.singletonMap("package", pkgName)); // NOI18N
        }

        FileObject createdFile = dobj.getPrimaryFile();

        GroovyExtender extender = Templates.getProject(wiz).getLookup().lookup(GroovyExtender.class);
        if (extender != null && !extender.isActive()) {
            extender.activate();
        }

        handle.finish();

        return Collections.singleton(createdFile);
    }

    private JUnit askUserWhichJUnitToUse() {
        JRadioButton radioButtonForJUnit3 = new JRadioButton();
        JRadioButton radioButtonForJUnit4 = new JRadioButton();

        Mnemonics.setLocalizedText(radioButtonForJUnit3, BUNDLE.getString("LBL_JUnit3_generator"));                       //NOI18N
        Mnemonics.setLocalizedText(radioButtonForJUnit4, BUNDLE.getString("LBL_JUnit4_generator"));                       //NOI18N

        radioButtonForJUnit3.getAccessibleContext().setAccessibleDescription(BUNDLE.getString("AD_JUnit3_generator"));    //NOI18N
        radioButtonForJUnit4.getAccessibleContext().setAccessibleDescription(BUNDLE.getString("AD_JUnit4_generator"));    //NOI18N

        ButtonGroup group = new ButtonGroup();
        group.add(radioButtonForJUnit3);
        group.add(radioButtonForJUnit4);
        radioButtonForJUnit4.setSelected(true);

        JComponent msg = createMultilineLabel(BUNDLE.getString("MSG_select_junit_version")); //NOI18N
        JPanel choicePanel = new JPanel(new GridLayout(0, 1, 0, 3));
        choicePanel.add(radioButtonForJUnit3);
        choicePanel.add(radioButtonForJUnit4);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.add(msg, BorderLayout.NORTH);
        panel.add(choicePanel, BorderLayout.CENTER);

        JButton button = new JButton();
        Mnemonics.setLocalizedText(button, BUNDLE.getString("LBL_Select"));     //NOI18N
        button.getAccessibleContext().setAccessibleDescription("AD_Select");    //NOI18N
        button.getAccessibleContext().setAccessibleName("AN_Select");           //NOI18N

        Object answer = DialogDisplayer.getDefault().notify(
                new DialogDescriptor(
                        wrapDialogContent(panel),
                        BUNDLE.getString("LBL_title_select_generator"),         //NOI18N
                        true,
                        new Object[] {button, NotifyDescriptor.CANCEL_OPTION},
                        button,
                        DialogDescriptor.DEFAULT_ALIGN,
                        null,
                        (ActionListener) null));

        if (answer == button) {
            if (radioButtonForJUnit3.isSelected()) {
                return JUnit.JUNIT3;
            } else {
                return JUnit.JUNIT4;
            }
        } else {
            return null;
        }
    }

    private JTextComponent createMultilineLabel(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEnabled(false);
        textArea.setOpaque(false);
        textArea.setColumns(25);
        textArea.setDisabledTextColor(new JLabel().getForeground());

        return textArea;
    }

    private static JComponent wrapDialogContent(JComponent comp) {
        JComponent result;

        result = new SelfResizingPanel();
        result.setLayout(new GridLayout());
        result.add(comp);
        result.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        result.getAccessibleContext().setAccessibleDescription(BUNDLE.getString("AD_title_select_generator")); //NOI18N
        return result;
    }
}
