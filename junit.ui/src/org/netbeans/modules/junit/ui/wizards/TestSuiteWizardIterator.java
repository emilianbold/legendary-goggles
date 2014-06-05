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
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2008 Sun
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

package org.netbeans.modules.junit.ui.wizards;

import java.awt.Component;
import org.netbeans.modules.junit.api.JUnitUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.SourceGroupModifier;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
//import org.netbeans.modules.junit.DefaultPlugin;
import org.netbeans.modules.java.testrunner.GuiUtils;
import org.netbeans.modules.junit.api.JUnitSettings;
import org.netbeans.modules.junit.api.JUnitTestUtil;
import org.netbeans.modules.junit.plugin.JUnitPlugin;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 */
@TemplateRegistrations(value = {
    @TemplateRegistration(id = "TestSuite.java", folder = "UnitTests", position = 400, scriptEngine = "freemarker",
            displayName = "org.netbeans.modules.junit.ui.Bundle#Templates/UnitTests/TestSuite.java",
            iconBase = "org/netbeans/modules/junit/ui/resources/JUnitLogo.png",
            description = "/org/netbeans/modules/junit/ui/resources/TestSuite.html", category = "junit"),
    @TemplateRegistration(folder = "UnitTests", position = 600, scriptEngine = "freemarker",
            content = "/org/netbeans/modules/junit/ui/resources/JUnit3Suite.java.template",
            displayName = "org.netbeans.modules.junit.ui.Bundle#Templates/UnitTests/JUnit3Suite.java",
            iconBase = "org/netbeans/modules/junit/ui/resources/JUnitLogo.png",
            description = "/org/netbeans/modules/junit/ui/resources/TestSuite.html", category = "invisible"),
    @TemplateRegistration(folder = "UnitTests", position = 900, scriptEngine = "freemarker",
            content = "/org/netbeans/modules/junit/ui/resources/JUnit4Suite.java.template",
            displayName = "org.netbeans.modules.junit.ui.Bundle#Templates/UnitTests/JUnit4Suite.java",
            iconBase = "org/netbeans/modules/junit/ui/resources/JUnitLogo.png",
            description = "/org/netbeans/modules/junit/ui/resources/TestSuite.html", category = "invisible")
})
public class TestSuiteWizardIterator implements TemplateWizard.InstantiatingIterator<WizardDescriptor> {
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wizard;
    /** panel for choosing name and target location of the test class */
    private WizardDescriptor.Panel<WizardDescriptor> targetPanel;
    private Project lastSelectedProject = null;
    private WizardDescriptor.Panel optionsPanel;
    private SourceGroup[] testSrcGroups;

    public TestSuiteWizardIterator() { }

    @Override
    public String name() {
        return ""; // NOI18N
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    @Override public final void addChangeListener(ChangeListener l) { }

    @Override public final void removeChangeListener(ChangeListener l) { }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        index = 0;
        panels = createPanels(wizard);
        loadSettings(wizard);
        // Make sure list of steps is accurate.
        String[] beforeSteps = null;
        Object prop = wizard.getProperty(WizardDescriptor.PROP_CONTENT_DATA); // NOI18N
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        String[] steps = createSteps(beforeSteps, panels);
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                // Step name (actually the whole list for reference).
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
            }
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        this.wizard = null;
        
        targetPanel = null;
        lastSelectedProject = null;
        optionsPanel = null;
        testSrcGroups = null;
    }

    @Override
    public Set<DataObject> instantiate() throws IOException {
        saveSettings(wizard);
        
        /* collect and build necessary data: */
        String name = Templates.getTargetName(wizard);
        FileObject targetFolder = Templates.getTargetFolder(wizard);
        FileObject testRootFolder = findTestRootFolder(targetFolder);
        assert testRootFolder != null;
        
        /* create test class(es) for the selected source class: */
        DataObject suite = JUnitUtils.createSuiteTest(testRootFolder, targetFolder, name, JUnitTestUtil.getSettingsMap(true));
        if (suite != null) {
            return Collections.singleton(suite);
        } else {
            throw new IOException();
        }
    }
    
    private WizardDescriptor.Panel[] createPanels(final WizardDescriptor wizardDescriptor) {
        return new WizardDescriptor.Panel[]{getTargetPanel()};
    }

    private String[] createSteps(String[] before, WizardDescriptor.Panel[] panels) {
        assert panels != null;
        // hack to use the steps set before this panel processed
        int diff = 0;
        if (before == null) {
            before = new String[0];
        } else if (before.length > 0) {
            diff = ("...".equals(before[before.length - 1])) ? 1 : 0; // NOI18N
        }
        String[] res = new String[(before.length - diff) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (before.length - diff)) {
                res[i] = before[i];
            } else {
                res[i] = panels[i - before.length + diff].getComponent().getName();
            }
        }
        return res;
    }
    
    private FileObject findTestRootFolder(FileObject targetFolder) {
        for (int i = 0; i < testSrcGroups.length; i++) {
            FileObject rootFolder = testSrcGroups[i].getRootFolder();
            if (rootFolder == targetFolder
                    || FileUtil.isParentOf(rootFolder, targetFolder)) {
                return rootFolder;
            }
        }
        return null;
    }

    /**
     * Returns a panel for choosing name and target location of the test
     * class. If the panel already exists, returns the existing panel,
     * otherwise creates a new panel.
     *
     * @return  existing panel or a newly created panel if it did not exist
     */
    private WizardDescriptor.Panel<WizardDescriptor> getTargetPanel() {
        final Project project = Templates.getProject(wizard);
        if(project == null) { // #176639
            return new StepProblemMessage(project,
                                          NbBundle.getMessage(
                                             this.getClass(),
                                             "MSG_UnsupportedPlugin")); //NOI18N

        }
        if (targetPanel == null || project != lastSelectedProject) {
            JUnitPlugin plugin = JUnitTestUtil.getPluginForProject(project);
            if (JUnitUtils.isInstanceOfDefaultPlugin(plugin)) {
                targetPanel = new StepProblemMessage(
                        project,
                        NbBundle.getMessage(TestSuiteWizardIterator.class,
                                            "MSG_UnsupportedPlugin"));  //NOI18N
            } else {
                Collection<SourceGroup> sourceGroups = JUnitUtils.getTestTargets(project, true);
                if (sourceGroups.isEmpty()) {
                    if (SourceGroupModifier.createSourceGroup(project, JavaProjectConstants.SOURCES_TYPE_JAVA, JavaProjectConstants.SOURCES_HINT_TEST) != null) {
                        sourceGroups = JUnitUtils.getTestTargets(project, true);
                    }
                }

                if (sourceGroups.isEmpty()) {
                    targetPanel = new StepProblemMessage(
                            project,
                            NbBundle.getMessage(TestSuiteWizardIterator.class,
                                              "MSG_NoTestSourceGroup"));//NOI18N
                } else {
                    sourceGroups.toArray(
                          testSrcGroups = new SourceGroup[sourceGroups.size()]);
                    if (optionsPanel == null) {
                        optionsPanel = new TestSuiteStepLocation();
                    }
                    targetPanel = JavaTemplates.createPackageChooser(project,
                                                                  testSrcGroups,
                                                                  optionsPanel);
                }
            }
            lastSelectedProject = project;
        }
        return targetPanel;
    }

    private void loadSettings(WizardDescriptor wizard) {
        JUnitSettings settings = JUnitSettings.getDefault();
        
        wizard.putProperty(GuiUtils.CHK_SETUP, settings.isGenerateSetUp());
        wizard.putProperty(GuiUtils.CHK_TEARDOWN, settings.isGenerateTearDown());
        wizard.putProperty(GuiUtils.CHK_BEFORE_CLASS, settings.isGenerateClassSetUp());
        wizard.putProperty(GuiUtils.CHK_AFTER_CLASS, settings.isGenerateClassTearDown());
        wizard.putProperty(GuiUtils.CHK_HINTS, settings.isBodyComments());
    }

    private void saveSettings(WizardDescriptor wizard) {
        JUnitSettings settings = JUnitSettings.getDefault();
        
        settings.setGenerateSetUp(Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_SETUP)));
        settings.setGenerateTearDown(Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_TEARDOWN)));
        settings.setGenerateClassSetUp(Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_BEFORE_CLASS)));
        settings.setGenerateClassTearDown(Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_AFTER_CLASS)));
        settings.setBodyComments(Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_HINTS)));
    }

}
