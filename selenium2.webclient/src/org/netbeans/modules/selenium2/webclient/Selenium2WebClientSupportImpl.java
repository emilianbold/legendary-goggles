/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium2.webclient;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.selenium2.spi.Selenium2SupportImpl;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.selenium2.webclient.api.SeleniumTestingProviders;
import org.netbeans.modules.selenium2.webclient.api.SeleniumTestingProvider;
import org.netbeans.modules.selenium2.webclient.api.Utilities;
import org.netbeans.modules.web.clientproject.api.ProjectDirectoriesProvider;
import org.netbeans.modules.web.clientproject.api.WebClientProjectConstants;
import org.netbeans.modules.web.common.api.UsageLogger;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Theofanis Oikonomou
 */
@ServiceProvider(service = Selenium2SupportImpl.class)
public class Selenium2WebClientSupportImpl extends Selenium2SupportImpl {
    
    static final Logger LOGGER = Logger.getLogger(Selenium2WebClientSupportImpl.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(Selenium2WebClientSupportImpl.class.getName(), 1);
    private WizardDescriptor wizard;
    
    /** Logger of selenium testing provider for HTML 5 project type usage. */
    private static final UsageLogger USAGE_LOGGER = new UsageLogger.Builder("org.netbeans.ui.metrics.selenium2")  // NOI18N
            .message(Selenium2WebClientSupportImpl.class, "USG_SELENIUM2") // NOI18N
            .firstMessageOnly(true).create();

    @Override
    public boolean isSupportActive(Project p) {
        return p.getLookup().lookup(ProjectDirectoriesProvider.class) != null; // true suggests this is a web.clientproject
    }

    @Override
    public void configureProject(FileObject targetFolder) {
        Project p = FileOwnerQuery.getOwner(targetFolder);
        if (p == null) {
            return;
        }
        FileObject testsFolder = Utilities.getTestsSeleniumFolder(p, true);
        if (testsFolder == null) {
            Utilities.openCustomizer(p, WebClientProjectConstants.CUSTOMIZER_SOURCES_IDENT);
        }
    }

    @Override
    @NbBundle.Messages({
    "# {0} - project",
    "NO_SELENIUM_TESTS_FOLDER=No Selenium Tests Folder set for project: {0}"})
    public WizardDescriptor.Panel createTargetChooserPanel(WizardDescriptor wiz) {
        wizard = wiz;
        Project project = Templates.getProject(wiz);
        Sources sources = ProjectUtils.getSources(project);
        FileObject testsFolder = Utilities.getTestsSeleniumFolder(project, true);
        if (testsFolder == null) {
            wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, Bundle.NO_SELENIUM_TESTS_FOLDER(ProjectUtils.getInformation(project).getDisplayName()));
            return new NoSeleniumTestsFolderSetWizardPanel();
        }
        SourceGroup[] sourceGroups = sources.getSourceGroups(WebClientProjectConstants.SOURCES_TYPE_HTML5_TEST_SELENIUM);
        return Templates.buildSimpleTargetChooser(project, sourceGroups).create();
    }

    @Override
    public String getTemplateID() {
        return Templates.getTemplate(wizard).getPath();
    }

    @Override
    public boolean isSupportEnabled(FileObject[] activatedFOs) {
        if (activatedFOs.length == 0) {
            return false;
        }
        
        Project p = FileOwnerQuery.getOwner(activatedFOs[0]);
        if (p == null) {
            return false;
        }
        if(p.getLookup().lookup(ProjectDirectoriesProvider.class) == null) { // true suggests this is not a web.clientproject
            return false;
        }
        return activatedFOs.length == 1 && activatedFOs[0].equals(p.getProjectDirectory());
    }

    @Override
    public List<Object> getTestSourceRoots(Collection<SourceGroup> createdSourceRoots, FileObject refFileObject) {
//        configureProject(refFileObject);
//        Project p = FileOwnerQuery.getOwner(refFileObject);
//        if (p == null) {
//            return Collections.EMPTY_LIST;
//        }
//        Sources sources = ProjectUtils.getSources(p);
//        SourceGroup[] sourceGroups = sources.getSourceGroups(WebClientProjectConstants.SOURCES_TYPE_HTML5_TEST_SELENIUM);
//        return new ArrayList<Object>(Arrays.asList(sourceGroups));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getSourceAndTestClassNames(FileObject fo, boolean isTestNG, boolean isSelenium) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void runTests(final FileObject[] activatedFOs, boolean isSelenium) {
        RP.post(new Runnable() {
            @Override
            public void run() {
                Project p = FileOwnerQuery.getOwner(activatedFOs[0]);
                if (p == null) {
                    return;
                }
                SeleniumTestingProvider provider = SeleniumTestingProviders.getDefault().getSeleniumTestingProvider(p, true);
                if(provider != null) {
                    provider.runTests(activatedFOs);
                    USAGE_LOGGER.log(provider.getIdentifier());
                }
            }
        });
    }
    
    private class NoSeleniumTestsFolderSetWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

        @Override
        public Component getComponent() {
            return new JPanel();
        }

        @Override
        public HelpCtx getHelp() {
            return HelpCtx.DEFAULT_HELP;
        }

        @Override
        public void readSettings(WizardDescriptor settings) {
        }

        @Override
        public void storeSettings(WizardDescriptor settings) {
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void addChangeListener(ChangeListener l) {
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
        }
    }
    
}
