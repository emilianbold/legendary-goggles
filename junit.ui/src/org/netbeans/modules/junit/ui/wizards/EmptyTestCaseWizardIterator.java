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
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2007 Sun
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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.SourceGroupModifier;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.modules.java.testrunner.GuiUtils;
import org.netbeans.modules.junit.api.JUnitSettings;
import org.netbeans.modules.junit.api.JUnitTestUtil;
import org.netbeans.modules.junit.plugin.JUnitPlugin;
import org.netbeans.modules.gsf.testrunner.plugin.CommonPlugin.CreateTestParam;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 * @author  Marian Petras
 */
@TemplateRegistrations(value = {
    @TemplateRegistration(id = "EmptyJUnitTest.java", folder = "UnitTests", position = 100, scriptEngine = "freemarker",
            displayName = "org.netbeans.modules.junit.ui.Bundle#Templates/UnitTests/EmptyJUnitTest.java",
            iconBase = "org/netbeans/modules/junit/ui/resources/JUnitLogo.png",
            description = "/org/netbeans/modules/junit/ui/resources/EmptyJUnitTest.html", category = "junit"),
    @TemplateRegistration(folder = "UnitTests", position = 200, scriptEngine = "freemarker",
            content = "../resources/JUnit3TestClass.java.template",
            displayName = "org.netbeans.modules.junit.ui.Bundle#Templates/UnitTests/JUnit3TestClass.java",
            iconBase = "org/netbeans/modules/junit/ui/resources/JUnitLogo.png",
            description = "/org/netbeans/modules/junit/ui/resources/EmptyJUnitTest.html", category = "invisible"),
    @TemplateRegistration(folder = "UnitTests", position = 700, scriptEngine = "freemarker",
            content = "../resources/JUnit4TestClass.java.template",
            displayName = "org.netbeans.modules.junit.ui.Bundle#Templates/UnitTests/JUnit4TestClass.java",
            iconBase = "org/netbeans/modules/junit/ui/resources/JUnitLogo.png",
            description = "/org/netbeans/modules/junit/ui/resources/EmptyJUnitTest.html", category = "invisible")
})
@SuppressWarnings("serial")
public class EmptyTestCaseWizardIterator implements TemplateWizard.InstantiatingIterator<WizardDescriptor> {

    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wizard;
    /** panel for choosing name and target location of the test class */
    private WizardDescriptor.Panel<WizardDescriptor> targetPanel;
    private Project lastSelectedProject = null;
    private WizardDescriptor.Panel optionsPanel;

    public EmptyTestCaseWizardIterator() { }

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
    }

    @Override
    public Set<DataObject> instantiate() throws IOException {
        saveSettings(wizard);
        
        /* collect and build necessary data: */
        String name = Templates.getTargetName(wizard);
        FileObject targetFolder = Templates.getTargetFolder(wizard);
        
        Map<CreateTestParam, Object> params
                = JUnitTestUtil.getSettingsMap(false);
        params.put(CreateTestParam.CLASS_NAME,
                   Templates.getTargetName(wizard));
                
        /* create the test class: */
        final JUnitPlugin plugin = JUnitTestUtil.getPluginForProject(
                                                Templates.getProject(wizard));
        
        if (!JUnitTestUtil.createTestActionCalled(
                                            plugin,
                                            new FileObject[] {targetFolder})) {
            return null;
        }

        /*
         * The JUnitPlugin instance must be initialized _before_ field
         * JUnitPluginTrampoline.DEFAULT gets accessed.
         * See issue #74744.
         */
        final FileObject[] testFileObjects
                = JUnitTestUtil.createTests(
                     plugin,
                     null,
                     targetFolder,
                     params);
        
        if (testFileObjects == null) {
            throw new IOException();
        }
        
        DataObject testDataObject;
        try {
            testDataObject = DataObject.find(testFileObjects[0]);
        } catch (DataObjectNotFoundException ex) {
            throw new IOException();
        }
        
        return Collections.singleton(testDataObject);
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

    @NbBundle.Messages("MSG_WizardInitializationError=There was an error initializing the wizard.")
    private WizardDescriptor.Panel<WizardDescriptor> getTargetPanel() {
	if(wizard == null) {
	    targetPanel = new StepProblemMessage(null, Bundle.MSG_WizardInitializationError());
	    return targetPanel;
	}
        final Project project = Templates.getProject(wizard);
        if (targetPanel == null || project != lastSelectedProject) {
            Collection<SourceGroup> sourceGroups = JUnitUtils.getTestTargets(project, true);
            if (sourceGroups.isEmpty()) {
                if (SourceGroupModifier.createSourceGroup(project, JavaProjectConstants.SOURCES_TYPE_JAVA, JavaProjectConstants.SOURCES_HINT_TEST) != null) {
                    sourceGroups = JUnitUtils.getTestTargets(project, true);
                }
            }
            if (sourceGroups.isEmpty()) {
                targetPanel = new StepProblemMessage(
                        project,
                        NbBundle.getMessage(EmptyTestCaseWizardIterator.class,
                                            "MSG_NoTestSourceGroup"));  //NOI18N
            } else {
                SourceGroup[] testSrcGroups;
                sourceGroups.toArray(testSrcGroups = new SourceGroup[sourceGroups.size()]);
                if (optionsPanel == null) {
                    optionsPanel = new EmptyTestStepLocation();
                }
                targetPanel = JavaTemplates.createPackageChooser(project, testSrcGroups, new EmptyTestStepLocation());
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
