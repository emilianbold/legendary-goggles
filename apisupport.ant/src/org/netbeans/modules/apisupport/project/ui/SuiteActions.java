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

package org.netbeans.modules.apisupport.project.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.modules.apisupport.project.api.BrandingUtils;
import org.netbeans.modules.apisupport.project.api.Util;
import org.netbeans.modules.apisupport.project.spi.BrandingModel;
import org.netbeans.modules.apisupport.project.spi.ExecProject;
import org.netbeans.modules.apisupport.project.suite.SuiteBrandingModel;
import org.netbeans.modules.apisupport.project.suite.SuiteProject;
import static org.netbeans.modules.apisupport.project.ui.Bundle.*;
import org.netbeans.modules.apisupport.project.ui.customizer.SuiteCustomizer;
import org.netbeans.modules.apisupport.project.ui.customizer.SuiteProperties;
import org.netbeans.modules.apisupport.project.ui.customizer.SuiteUtils;
import org.netbeans.modules.apisupport.project.universe.HarnessVersion;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Task;
import org.openide.util.actions.Presenter;

/**
 * Defines actions available on a suite.
 * @author Jesse Glick
 */
public final class SuiteActions implements ActionProvider, ExecProject {

    private static final String COMMAND_BRANDING = "branding";
    private static final String COMMAND_BUILD_JNLP = "build-jnlp";
    private static final String COMMAND_BUILD_MAC = "build-mac";
    private static final String COMMAND_BUILD_OSGI = "build-osgi";
    private static final String COMMAND_BUILD_OSGI_OBR = "build-osgi-obr";
    private static final String COMMAND_BUILD_ZIP = "build-zip";
    private static final String COMMAND_DEBUG_JNLP = "debug-jnlp";
    private static final String COMMAND_DEBUG_OSGI = "debug-osgi";
    private static final String COMMAND_NBMS = "nbms";
    private static final String COMMAND_PROFILE = "profile";
    private static final String COMMAND_PROFILE_OSGI = "profile-osgi";
    private static final String COMMAND_RUN_JNLP = "run-jnlp";
    private static final String COMMAND_RUN_OSGI = "run-osgi";
    private static final String SUITE_ACTIONS_TYPE = "org-netbeans-modules-apisupport-project-suite";
    private static final String SUITE_ACTIONS_PATH = "Projects/" + SUITE_ACTIONS_TYPE + "/Actions";
    private static final String SUITE_PACKAGE_ACTIONS_TYPE = SUITE_ACTIONS_TYPE + "-package";
    private static final String SUITE_PACKAGE_ACTIONS_PATH = "Projects/" + SUITE_PACKAGE_ACTIONS_TYPE + "/Actions";
    private static final String SUITE_JNLP_ACTIONS_TYPE = SUITE_ACTIONS_TYPE + "-jnlp";
    private static final String SUITE_JNLP_ACTIONS_PATH = "Projects/" + SUITE_JNLP_ACTIONS_TYPE + "/Actions";
    private static final String SUITE_OSGI_ACTIONS_TYPE = SUITE_ACTIONS_TYPE + "-osgi";
    private static final String SUITE_OSGI_ACTIONS_PATH = "Projects/" + SUITE_OSGI_ACTIONS_TYPE + "/Actions";

    @Override
    public Task execute(String... args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String r : args) {
            sb.append(r).append(' ');
        }
        Properties p = new Properties();
        p.setProperty("run.args", sb.substring(0, sb.length() - 1));

        return ActionUtils.runTarget(findBuildXml(project), new String[]{"run"}, p);
    }
    
    static Action[] getProjectActions(SuiteProject project) {
        return CommonProjectActions.forType(SUITE_ACTIONS_TYPE);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.package")
    @ActionRegistration(displayName="#SUITE_ACTION_package_menu", lazy=false)
    @ActionReference(path=SUITE_ACTIONS_PATH, position=600)
    @Messages("SUITE_ACTION_package_menu=Package as")
    public static Action packageMenu() {
        return new SubMenuAction(SUITE_ACTION_package_menu(), Arrays.asList(CommonProjectActions.forType(SUITE_PACKAGE_ACTIONS_TYPE)));
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.buildZip")
    @ActionRegistration(displayName="#SUITE_ACTION_zip", lazy=false)
    @ActionReference(path=SUITE_PACKAGE_ACTIONS_PATH, position=100)
    @Messages("SUITE_ACTION_zip=ZIP Distribution")
    public static Action buildZip() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_BUILD_ZIP, SUITE_ACTION_zip(), null);
    }
    
    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.nbms")
    @ActionRegistration(displayName="#SUITE_ACTION_nbms", lazy=false)
    @ActionReference(path=SUITE_PACKAGE_ACTIONS_PATH, position=200)
    @Messages("SUITE_ACTION_nbms=NBMs")
    public static Action nbms() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_NBMS, SUITE_ACTION_nbms(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.buildMac")
    @ActionRegistration(displayName="#SUITE_ACTION_mac", lazy=false)
    @ActionReference(path=SUITE_PACKAGE_ACTIONS_PATH, position=300)
    @Messages("SUITE_ACTION_mac=Mac OS X Application")
    public static Action buildMac() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_BUILD_MAC, SUITE_ACTION_mac(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.jnlpMenu")
    @ActionRegistration(displayName="#SUITE_ACTION_jnlp_menu", lazy=false)
    @ActionReference(path=SUITE_ACTIONS_PATH, position=1300)
    @Messages("SUITE_ACTION_jnlp_menu=JNLP")
    public static Action jnlpMenu() {
        return new SubMenuAction(SUITE_ACTION_jnlp_menu(), Arrays.asList(CommonProjectActions.forType(SUITE_JNLP_ACTIONS_TYPE)));
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.buildJnlp")
    @ActionRegistration(displayName="#SUITE_ACTION_build_jnlp", lazy=false)
    @ActionReference(path=SUITE_JNLP_ACTIONS_PATH, position=100)
    @Messages("SUITE_ACTION_build_jnlp=Build")
    public static Action buildJnlp() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_BUILD_JNLP, SUITE_ACTION_build_jnlp(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.runJnlp")
    @ActionRegistration(displayName="#SUITE_ACTION_run_jnlp", lazy=false)
    @ActionReference(path=SUITE_JNLP_ACTIONS_PATH, position=200)
    @Messages("SUITE_ACTION_run_jnlp=Run")
    public static Action runJnlp() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_RUN_JNLP, SUITE_ACTION_run_jnlp(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.debugJnlp")
    @ActionRegistration(displayName="#SUITE_ACTION_debug_jnlp", lazy=false)
    @ActionReference(path=SUITE_JNLP_ACTIONS_PATH, position=300)
    @Messages("SUITE_ACTION_debug_jnlp=Debug")
    public static Action debugJnlp() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_DEBUG_JNLP, SUITE_ACTION_debug_jnlp(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.osgiMenu")
    @ActionRegistration(displayName="#SUITE_ACTION_osgi_menu", lazy=false)
    @ActionReference(path=SUITE_ACTIONS_PATH, position=1400)
    @Messages("SUITE_ACTION_osgi_menu=OSGi")
    public static Action osgiMenu() {
        return new SubMenuAction(SUITE_ACTION_osgi_menu(), Arrays.asList(CommonProjectActions.forType(SUITE_OSGI_ACTIONS_TYPE)));
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.buildOsgi")
    @ActionRegistration(displayName="#SUITE_ACTION_build_osgi", lazy=false)
    @ActionReference(path=SUITE_OSGI_ACTIONS_PATH, position=100)
    @Messages("SUITE_ACTION_build_osgi=Build Bundles")
    public static Action buildOsgi() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_BUILD_OSGI, SUITE_ACTION_build_osgi(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.buildOsgiObr")
    @ActionRegistration(displayName="#SUITE_ACTION_build_osgi_obr", lazy=false)
    @ActionReference(path=SUITE_OSGI_ACTIONS_PATH, position=200)
    @Messages("SUITE_ACTION_build_osgi_obr=Build Bundle Repository")
    public static Action buildOsgiObr() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_BUILD_OSGI_OBR, SUITE_ACTION_build_osgi_obr(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.runOsgi")
    @ActionRegistration(displayName="#SUITE_ACTION_run_osgi", lazy=false)
    @ActionReference(path=SUITE_OSGI_ACTIONS_PATH, position=300)
    @Messages("SUITE_ACTION_run_osgi=Run in Felix")
    public static Action runOsgi() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_RUN_OSGI, SUITE_ACTION_run_osgi(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.debugOsgi")
    @ActionRegistration(displayName="#SUITE_ACTION_debug_osgi", lazy=false)
    @ActionReference(path=SUITE_OSGI_ACTIONS_PATH, position=400)
    @Messages("SUITE_ACTION_debug_osgi=Debug in Felix")
    public static Action debugOsgi() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_DEBUG_OSGI, SUITE_ACTION_debug_osgi(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.profileOsgi")
    @ActionRegistration(displayName="#SUITE_ACTION_profile_osgi", lazy=false)
    @ActionReference(path=SUITE_OSGI_ACTIONS_PATH, position=500)
    @Messages("SUITE_ACTION_profile_osgi=Profile in Felix")
    public static Action profileOsgi() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_PROFILE_OSGI, SUITE_ACTION_profile_osgi(), null);
    }

    @ActionID(category="Project", id="org.netbeans.modules.apisupport.project.suite.branding")
    @ActionRegistration(displayName="#SUITE_ACTION_branding", lazy=false)
    @ActionReference(path=SUITE_ACTIONS_PATH, position=2700)
    @Messages("SUITE_ACTION_branding=Branding...")
    public static Action branding() {
        return ProjectSensitiveActions.projectCommandAction(COMMAND_BRANDING, SUITE_ACTION_branding(), null);
    }

    // XXX shouldn't this be a factory method in o.o.awt.Actions or similar?
    private static class SubMenuAction extends AbstractAction implements ContextAwareAction {
        private final String label;
        private final List<? extends Action> entries;
        SubMenuAction(String label, List<? extends Action> entries) {
            this.label = label;
            this.entries = entries;
        }
        public @Override Action createContextAwareInstance(final Lookup ctx) {
            class A extends AbstractAction implements Presenter.Popup {
                public @Override void actionPerformed(ActionEvent e) {assert false;}
                public @Override JMenuItem getPopupPresenter() {
                    class Menu extends JMenu implements DynamicMenuContent {
                        Menu() {super(label);}
                        public @Override JComponent[] getMenuPresenters() {return new JComponent[] {this};}
                        public @Override JComponent[] synchMenuPresenters(JComponent[] items) {return getMenuPresenters();}
                    }
                    JMenu m = new Menu();
                    for (Action entry : entries) {
                        if (entry instanceof ContextAwareAction) {
                            entry = ((ContextAwareAction) entry).createContextAwareInstance(ctx);
                        }
                        m.add(entry);
                    }
                    return m;
                }
            }
            return new A();
        }
        public @Override void actionPerformed(ActionEvent e) {assert false;}
    }

    private static final Map<String,HarnessVersion> MINIMUM_HARNESS_VERSION = new HashMap<String,HarnessVersion>();
    static {
        MINIMUM_HARNESS_VERSION.put(ActionProvider.COMMAND_TEST, HarnessVersion.V61);
        MINIMUM_HARNESS_VERSION.put(COMMAND_NBMS, HarnessVersion.V50u1);
        MINIMUM_HARNESS_VERSION.put(COMMAND_BUILD_MAC, HarnessVersion.V55u1);
        MINIMUM_HARNESS_VERSION.put(COMMAND_BUILD_OSGI, HarnessVersion.V69);
        MINIMUM_HARNESS_VERSION.put(COMMAND_BUILD_OSGI_OBR, HarnessVersion.V69);
        MINIMUM_HARNESS_VERSION.put(COMMAND_RUN_OSGI, HarnessVersion.V69);
        MINIMUM_HARNESS_VERSION.put(COMMAND_DEBUG_OSGI, HarnessVersion.V69);
        MINIMUM_HARNESS_VERSION.put(COMMAND_PROFILE_OSGI, HarnessVersion.V69);
    }
    
    private final SuiteProject project;
    
    public SuiteActions(SuiteProject project) {
        this.project = project;
    }
    
    public String[] getSupportedActions() {
        List<String> actions = new ArrayList<String>(Arrays.asList(
            ActionProvider.COMMAND_BUILD,
            ActionProvider.COMMAND_CLEAN,
            ActionProvider.COMMAND_REBUILD,
            ActionProvider.COMMAND_RUN,
            ActionProvider.COMMAND_DEBUG,
            ActionProvider.COMMAND_TEST,
            COMMAND_BUILD_ZIP,
            COMMAND_BUILD_JNLP,
            COMMAND_RUN_JNLP,
            COMMAND_DEBUG_JNLP,
            COMMAND_BUILD_OSGI,
            COMMAND_BUILD_OSGI_OBR,
            COMMAND_RUN_OSGI,
            COMMAND_DEBUG_OSGI,
            COMMAND_PROFILE_OSGI,
            COMMAND_BUILD_MAC,
            COMMAND_NBMS,
            COMMAND_PROFILE,
            COMMAND_BRANDING,
            ActionProvider.COMMAND_RENAME,
            ActionProvider.COMMAND_MOVE,
            ActionProvider.COMMAND_DELETE
        ));
        return actions.toArray(new String[actions.size()]);
    }
    
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        if (COMMAND_BRANDING.equals(command)) {
            boolean enabled = false;
            EditableProperties properties = project.getHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
            if( null != properties ) {
                String brandingToken = properties.get(SuiteBrandingModel.BRANDING_TOKEN_PROPERTY);
                enabled = null != brandingToken;
            }
            return enabled;
        } else if (ActionProvider.COMMAND_DELETE.equals(command) ||
                ActionProvider.COMMAND_RENAME.equals(command) ||
                ActionProvider.COMMAND_MOVE.equals(command)) {
            return true;
        } else if (Arrays.asList(getSupportedActions()).contains(command)) {
            HarnessVersion min = MINIMUM_HARNESS_VERSION.get(command);
            if (min != null) {
                NbPlatform plaf = project.getPlatform(true);
                if (plaf == null || plaf.getHarnessVersion().compareTo(min) < 0) {
                    return false;
                }
            }
            return findBuildXml(project) != null;
        } else {
            throw new IllegalArgumentException(command);
        }
    }
    
    @Messages("Title_BrandingEditor={0} - Branding")
    @Override public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        if (ActionProvider.COMMAND_DELETE.equals(command)) {
            if (SuiteOperations.canRun(project)) {
                DefaultProjectOperations.performDefaultDeleteOperation(project);
            }
        } else if (ActionProvider.COMMAND_RENAME.equals(command)) {
            if (SuiteOperations.canRun(project)) {
                DefaultProjectOperations.performDefaultRenameOperation(project, null);
            }
        } else if (ActionProvider.COMMAND_MOVE.equals(command)) {
            if (SuiteOperations.canRun(project)) {
                DefaultProjectOperations.performDefaultMoveOperation(project);
            }
        } else if (COMMAND_BRANDING.equals(command)) {
            SuiteProperties properties = new SuiteProperties(project, project.getHelper(), project.getEvaluator(), SuiteUtils.getSubProjects(project));
            BrandingModel model = properties.getBrandingModel();
            BrandingUtils.openBrandingEditor(Title_BrandingEditor(properties.getProjectDisplayName()), project, model);
        } else {
            NbPlatform plaf = project.getPlatform(false);
            if (plaf != null) {
                HarnessVersion v = plaf.getHarnessVersion();
                if (v != HarnessVersion.UNKNOWN) {
                    for (Project p : project.getLookup().lookup(SubprojectProvider.class).getSubprojects()) {
                        if (v.compareTo(((NbModuleProject) p).getMinimumHarnessVersion()) < 0) {
                            ModuleActions.promptForNewerHarness();
                            return;
                        }
                    }
                }
            }
            ExecutorTask task = null;
            ActionEvent ev;
            try {
                task = invokeActionImpl(command, context);
            } catch (IOException e) {
                Util.err.notify(e);
            }
            if (
                task != null &&
                (ev = context.lookup(ActionEvent.class)) != null &&
                "waitFinished".equals(ev.getActionCommand()) // NOI18N
            ) {
                task.waitFinished();
                if (ev.getSource() instanceof ExecutorTask[]) {
                    ExecutorTask[] arr = (ExecutorTask[])ev.getSource();
                    if (arr.length > 0) {
                        arr[0] = task;
                    }
                }
            }
        }
    }
    
    /** Used from tests to start the build script and get task that allows to track its progress.
     * @return null or task that was started
     */
    public ExecutorTask invokeActionImpl(String command, Lookup context) throws IllegalArgumentException, IOException {
        String[] targetNames;
        Properties p = new Properties();

        if (command.equals(ActionProvider.COMMAND_BUILD)) {
            targetNames = new String[] {"build"}; // NOI18N
        } else if (command.equals(ActionProvider.COMMAND_CLEAN)) {
            targetNames = new String[] {"clean"}; // NOI18N
        } else if (command.equals(ActionProvider.COMMAND_REBUILD)) {
            targetNames = new String[] {"clean", "build"}; // NOI18N
        } else if (command.equals(ActionProvider.COMMAND_TEST)) {
            targetNames = new String[] {"test"}; // NOI18N
        } else if (command.equals(COMMAND_BUILD_ZIP)) {
            if (promptForAppName(PROMPT_FOR_APP_NAME_MODE_ZIP)) { // #65006
                return null;
            }
            targetNames = new String[] {COMMAND_BUILD_ZIP};
        } else if (command.equals(COMMAND_BUILD_JNLP)) {
            if (promptForAppName(PROMPT_FOR_APP_NAME_MODE_JNLP)) {
                return null;
            }
            targetNames = new String[] {COMMAND_BUILD_JNLP};
        } else if (command.equals(COMMAND_RUN_JNLP)) {
            if (promptForAppName(PROMPT_FOR_APP_NAME_MODE_JNLP)) {
                return null;
            }
            targetNames = new String[] {COMMAND_RUN_JNLP};
        } else if (command.equals(COMMAND_DEBUG_JNLP)) {
            if (promptForAppName(PROMPT_FOR_APP_NAME_MODE_JNLP)) {
                return null;
            }
            targetNames = new String[] {COMMAND_DEBUG_JNLP};
        } else {
            targetNames = new String[] {command};
        }

        ModuleActions.setRunArgsIde(project, command, p, project.getTestUserDirLockFile());
        
        return ActionUtils.runTarget(findBuildXml(project), targetNames, p);
    }
    
    private static FileObject findBuildXml(SuiteProject project) {
        return project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
    }
    
    private static final int PROMPT_FOR_APP_NAME_MODE_JNLP = 0;
    private static final int PROMPT_FOR_APP_NAME_MODE_ZIP = 1;
    /** @return true if the dialog is shown */
    @Messages({
        "ERR_app_name_jnlp=<html>The JNLP application cannot be built because this suite is not yet set up as a standalone application.<br>At least a branding name must be configured before building or running in JNLP mode.",
        "ERR_app_name_zip=<html>The ZIP file cannot be built because this suite is not yet set up as a standalone application.<br>At least a branding name must be configured before building an application ZIP.",
        "TITLE_app_name=Not Standalone Application",
        "LBL_configure_app_name=Configure Application...",
        "ACSD_configure_app_name=Configures Application"
    })
    private boolean promptForAppName(int mode) {
        String name = project.getEvaluator().getProperty("app.name"); // NOI18N
        if (name != null) {
            return false;
        }
        
        // #61372: warn the user, rather than disabling the action.
        String msg;
        switch (mode) {
            case PROMPT_FOR_APP_NAME_MODE_JNLP:
                msg = ERR_app_name_jnlp();
                break;
            case PROMPT_FOR_APP_NAME_MODE_ZIP:
                msg = ERR_app_name_zip();
                break;
            default:
                throw new AssertionError(mode);
        }
        if (ApisupportAntUIUtils.showAcceptCancelDialog(
                TITLE_app_name(),
                msg,
                LBL_configure_app_name(),
                ACSD_configure_app_name(),
                null,
                NotifyDescriptor.WARNING_MESSAGE)) {
            SuiteCustomizer cpi = project.getLookup().lookup(SuiteCustomizer.class);
            cpi.showCustomizer(SuiteCustomizer.APPLICATION, SuiteCustomizer.APPLICATION_CREATE_STANDALONE_APPLICATION);
        }
        return true;
    }
    
}
