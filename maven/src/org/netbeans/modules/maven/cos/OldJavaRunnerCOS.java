/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.cos;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.netbeans.api.extexecution.startup.StartupExtender;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.runner.JavaRunner;
import org.netbeans.api.java.source.ui.ScanDialog;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.classpath.ProjectSourcesClassPathProvider;
import org.netbeans.modules.maven.api.execute.ActiveJ2SEPlatformProvider;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.modules.maven.api.execute.RunUtils;
import org.netbeans.modules.maven.classpath.AbstractProjectClassPathImpl;
import org.netbeans.modules.maven.classpath.RuntimeClassPathImpl;
import org.netbeans.modules.maven.classpath.TestRuntimeClassPathImpl;
import org.netbeans.modules.maven.customizer.RunJarPanel;
import org.netbeans.modules.maven.execute.DefaultReplaceTokenProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SingleMethod;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author mkleint
 */
public class OldJavaRunnerCOS {
    private static final Logger LOG = Logger.getLogger(OldJavaRunnerCOS.class.getName());
    private static final String STARTUP_ARGS_KEY = "run.jvmargs.ide"; // NOI18N


    static  boolean deprecatedJavaRunnerApproachTest(final RunConfig config, String actionName) {
        String test = config.getProperties().get("test");
        final Map<String, Object> params = new HashMap<String, Object>();
        FileObject selected = config.getSelectedFileObject();
        ProjectSourcesClassPathProvider cpp = config.getProject().getLookup().lookup(ProjectSourcesClassPathProvider.class);
        ClassPath srcs = cpp.getProjectSourcesClassPath(ClassPath.SOURCE);
        ClassPath[] cps = cpp.getProjectClassPaths(ClassPath.SOURCE);
        ClassPath testcp = ClassPathSupport.createProxyClassPath(cps);
        String path;
        if (selected != null) {
            path = srcs.getResourceName(selected);
            if (path != null) {
                String nameExt = selected.getNameExt().replace(".java", "Test.java");
                path = path.replace(selected.getNameExt(), nameExt);
                FileObject testFo = testcp.findResource(path);
                if (testFo != null) {
                    selected = testFo;
                } else {
                    //#160776 only files on source classpath pass through
                    return true;
                }
            } else {
                path = testcp.getResourceName(selected);
                if (path == null) {
                    //#160776 only files on source classpath pass through
                    return true;
                }
            }
        } else {
            test = test + ".java";
            selected = testcp.findResource(test);
            if (selected == null) {
                List<FileObject> mainSourceRoots = Arrays.asList(srcs.getRoots());
                TOP:
                for (FileObject root : testcp.getRoots()) {
                    if (mainSourceRoots.contains(root)) {
                        continue;
                    }
                    Enumeration<? extends FileObject> fos = root.getData(true);
                    while (fos.hasMoreElements()) {
                        FileObject fo = fos.nextElement();
                        if (fo.getNameExt().equals(test)) {
                            selected = fo;
                            break TOP;
                        }
                    }
                }
            }
        }
        if (selected == null) {
            return true;
        }
        params.put(JavaRunner.PROP_EXECUTE_FILE, selected);
        params.put(JavaRunner.PROP_PLATFORM, config.getProject().getLookup().lookup(ActiveJ2SEPlatformProvider.class).getJavaPlatform());
        List<String> jvmProps = new ArrayList<String>();
        Set<String> jvmPropNames = new HashSet<String>();
        params.put(JavaRunner.PROP_PROJECT_NAME, config.getExecutionName() + "/CoS");
        String dir = PluginPropertyUtils.getPluginProperty(config.getMavenProject(), Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_SUREFIRE, "basedir", "test", "basedir");
        jvmPropNames.add("basedir");
        if (dir != null) {
            params.put(JavaRunner.PROP_WORK_DIR, dir);
            jvmProps.add("-Dbasedir=\"" + dir + "\"");
        } else {
            params.put(JavaRunner.PROP_WORK_DIR, config.getExecutionDirectory());
            jvmProps.add("-Dbasedir=\"" + config.getExecutionDirectory().getAbsolutePath() + "\"");
        }
        Properties sysProps = PluginPropertyUtils.getPluginPropertyParameter(config.getMavenProject(), Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_SUREFIRE, "systemProperties", "test");
        if (sysProps != null) {
            for (Map.Entry key : sysProps.entrySet()) {
                jvmProps.add("-D" + key.getKey() + "=" + key.getValue());
                jvmPropNames.add((String) key.getKey());
            }
        }
        for (Map.Entry entry : config.getProperties().entrySet()) {
            if ("maven.surefire.debug".equals(entry.getKey())) {
                //NOI18N
                continue;
            }
            if (Constants.ACTION_PROPERTY_JPDALISTEN.equals(entry.getKey())) {
                continue;
            }
            if ("jpda.stopclass".equals(entry.getKey())) {
                //NOI18N
                continue;
            }
            if (DefaultReplaceTokenProvider.METHOD_NAME.equals(entry.getKey())) {
                params.put("methodname", entry.getValue());
                actionName = ActionProvider.COMMAND_TEST_SINGLE.equals(actionName) ? SingleMethod.COMMAND_RUN_SINGLE_METHOD : SingleMethod.COMMAND_DEBUG_SINGLE_METHOD;
                continue;
            }
            if (!jvmPropNames.contains((String) entry.getKey())) {
                jvmProps.add("-D" + entry.getKey() + "=" + entry.getValue());
                jvmPropNames.add((String) entry.getKey());
            }
        }
        String argLine = PluginPropertyUtils.getPluginProperty(config.getMavenProject(), Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_SUREFIRE, "argLine", "test", "argLine");
        if (argLine != null) {
            try {
                String[] arr = CommandLineUtils.translateCommandline(argLine);
                jvmProps.addAll(Arrays.asList(arr));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            argLine = config.getProperties().get("argLine");
            if (argLine != null) {
                try {
                    jvmProps.addAll(CosChecker.extractDebugJVMOptions(argLine));
                } catch (CommandLineException cli) {
                    LOG.log(Level.INFO, "error parsing argLine property:" + argLine, cli);
                    if (ActionProvider.COMMAND_DEBUG_TEST_SINGLE.equals(actionName)) {
                        NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Error parsing argLine property, arguments will not be passed to internal execution. Error: " + cli.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notifyLater(msg);
                    }
                } catch (Exception ex) {
                    LOG.log(Level.INFO, "error extracting debug params from argLine property:" + argLine, ex);
                }
            }
        }
        String[] additionals = PluginPropertyUtils.getPluginPropertyList(config.getMavenProject(), Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_SUREFIRE, "additionalClasspathElements", "additionalClasspathElement", "test");
        ClassPath cp = createRuntimeClassPath(config.getMavenProject(), true);
        if (additionals != null) {
            List<URL> roots = new ArrayList<URL>();
            File base = FileUtil.toFile(config.getProject().getProjectDirectory());
            for (String add : additionals) {
                File root = FileUtilities.resolveFilePath(base, add);
                if (root != null) {
                    try {
                        URL url = Utilities.toURI(root).toURL();
                        if (FileUtil.isArchiveFile(url)) {
                            url = FileUtil.getArchiveRoot(url);
                        }
                        roots.add(url);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(CosChecker.class.getName()).log(Level.INFO, "Cannot convert ''{0}'' to URL", add);
                    }
                } else {
                    Logger.getLogger(CosChecker.class.getName()).log(Level.INFO, "Cannot convert ''{0}'' to URL.", add);
                }
            }
            ClassPath addCp = ClassPathSupport.createClassPath(roots.toArray(new URL[roots.size()]));
            cp = ClassPathSupport.createProxyClassPath(cp, addCp);
        }
        params.put(JavaRunner.PROP_EXECUTE_CLASSPATH, cp);
        params.put(JavaRunner.PROP_RUN_JVMARGS, jvmProps);
        params.put("maven.disableSources", Boolean.TRUE);
        final String action2Quick = CosChecker.action2Quick(actionName);
        boolean supported = JavaRunner.isSupported(action2Quick, params);
        if (supported) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        ScanDialog.runWhenScanFinished(new Runnable() {
                            @Override
                            public void run() {
                                if (SwingUtilities.isEventDispatchThread()) {
                                    CosChecker.RP.post(this);
                                    return;
                                }
                                try {
                                    collectStartupArgs(config, params);
                                    final ExecutorTask tsk = JavaRunner.execute(action2Quick, params);
                                    CosChecker.warnCoSInOutput(tsk, config);
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                } catch (UnsupportedOperationException ex) {
                                    Exceptions.printStackTrace(ex);
                                } finally {
                                    CosChecker.touchCoSTimeStamp(config, true);
                                    if (RunUtils.hasApplicationCompileOnSaveEnabled(config)) {
                                        CosChecker.touchCoSTimeStamp(config, false);
                                    } else {
                                        CosChecker.deleteCoSTimeStamp(config, false);
                                    }
                                }
                            }
                        }, config.getTaskDisplayName());
                    }
                });
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
        return true;
    }

    static boolean deprecatedJavaRunnerApproach(final RunConfig config, String actionName) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(JavaRunner.PROP_PROJECT_NAME, config.getExecutionName() + "/CoS");
        String proppath = config.getProperties().get("exec.workingdir");
        if (proppath != null) {
            params.put(JavaRunner.PROP_WORK_DIR, FileUtil.normalizeFile(new File(proppath)));
        } else {
            params.put(JavaRunner.PROP_WORK_DIR, config.getExecutionDirectory());
        }
        if (CosChecker.RUN_MAIN.equals(actionName) || CosChecker.DEBUG_MAIN.equals(actionName) || CosChecker.PROFILE_MAIN.equals(actionName)) {
            FileObject selected = config.getSelectedFileObject();
            ClassPath srcs = config.getProject().getLookup().lookup(ProjectSourcesClassPathProvider.class).getProjectSourcesClassPath(ClassPath.SOURCE);
            String path = srcs.getResourceName(selected);
            if (path == null) {
                //#160776 only files on source classpath pass through
                return true;
            }
            params.put(JavaRunner.PROP_EXECUTE_FILE, selected);
        } else {
            params.put(JavaRunner.PROP_EXECUTE_CLASSPATH, createRuntimeClassPath(config.getMavenProject(), false));
        }
        String exargs = config.getProperties().get("exec.args");
        if (exargs != null) {
            String[] args = RunJarPanel.splitAll(exargs);
            if (params.get(JavaRunner.PROP_EXECUTE_FILE) == null) {
                params.put(JavaRunner.PROP_CLASSNAME, args[1]);
            }
            String[] appargs = args[2].split(" ");
            params.put(JavaRunner.PROP_APPLICATION_ARGS, Arrays.asList(appargs));
            try {
                params.put(JavaRunner.PROP_RUN_JVMARGS, CosChecker.extractDebugJVMOptions(args[0]));
            } catch (CommandLineException cli) {
                LOG.log(Level.INFO, "error parsing exec.args property:" + args[0], cli);
                if (CosChecker.DEBUG_MAIN.equals(actionName) || ActionProvider.COMMAND_DEBUG.equals(actionName)) {
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Error parsing exec.args property, arguments will not be passed to internal execution. Error: " + cli.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notifyLater(msg);
                }
            } catch (Exception ex) {
                LOG.log(Level.INFO, "error extracting debug params from exec.args property:" + args[0], ex);
            }
        }
        params.put(JavaRunner.PROP_PLATFORM, config.getProject().getLookup().lookup(ActiveJ2SEPlatformProvider.class).getJavaPlatform());
        params.put("maven.disableSources", Boolean.TRUE);
        if (params.get(JavaRunner.PROP_EXECUTE_FILE) != null || params.get(JavaRunner.PROP_CLASSNAME) != null) {
            final String action2Quick = CosChecker.action2Quick(actionName);
            boolean supported = JavaRunner.isSupported(action2Quick, params);
            if (supported) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ScanDialog.runWhenScanFinished(new Runnable() {
                                @Override
                                public void run() {
                                    if (SwingUtilities.isEventDispatchThread()) {
                                        CosChecker.RP.post(this);
                                        return;
                                    }
                                    try {
                                        collectStartupArgs(config, params);
                                        ExecutorTask tsk = JavaRunner.execute(action2Quick, params);
                                        CosChecker.warnCoSInOutput(tsk, config);
                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } catch (UnsupportedOperationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        if (RunUtils.hasApplicationCompileOnSaveEnabled(config)) {
                                            CosChecker.touchCoSTimeStamp(config, false);
                                        }
                                    }
                                }
                            }, config.getTaskDisplayName());
                        }
                    });
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InvocationTargetException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return false;
            }
        } else {
            //TODO what to do now? skip?
        }
        return true;
    }

    
    //create a special runtime classpath here as the resolved mavenproject in execution
    // can be different from the one in loaded project
    private static  ClassPath createRuntimeClassPath(MavenProject prj, boolean test) {
        List<URI> roots;
        if (test) {
            roots = TestRuntimeClassPathImpl.createPath(prj);
        } else {
            roots = RuntimeClassPathImpl.createPath(prj);
        }
        return ClassPathSupport.createClassPath(AbstractProjectClassPathImpl.getPath(roots.toArray(new URI[0]), null));
    }    
    
    
   private static void collectStartupArgs(RunConfig config, Map<String, Object> params) {
        String actionName = config.getActionName();
        StartupExtender.StartMode mode;
        
        if (ActionProvider.COMMAND_RUN.equals(actionName) || CosChecker.RUN_MAIN.equals(actionName)) {
            mode = StartupExtender.StartMode.NORMAL;
        } else if (ActionProvider.COMMAND_DEBUG.equals(actionName) || CosChecker.DEBUG_MAIN.equals(actionName)) {
            mode = StartupExtender.StartMode.DEBUG;
        } else if (ActionProvider.COMMAND_PROFILE.equals(actionName) || ActionProvider.COMMAND_PROFILE_SINGLE.equals(actionName) || CosChecker.PROFILE_MAIN.equals(actionName)) {
            mode = StartupExtender.StartMode.PROFILE;
        } else if (ActionProvider.COMMAND_PROFILE_TEST_SINGLE.equals(actionName)) {
            mode = StartupExtender.StartMode.TEST_PROFILE;
        } else {
            // XXX could also set argLine for COMMAND_TEST and relatives (StartMode.TEST_*); need not be specific to TYPE_JAR
            return;
        }

        InstanceContent ic = new InstanceContent();
        Project p = config.getProject();
        if (p != null) {
            ic.add(p);
            ActiveJ2SEPlatformProvider pp = p.getLookup().lookup(ActiveJ2SEPlatformProvider.class);
            if (pp != null) {
                ic.add(pp.getJavaPlatform());
            }
        }
        Set<String> args = new HashSet<String>();

        for (StartupExtender group : StartupExtender.getExtenders(new AbstractLookup(ic), mode)) {
            args.addAll(group.getArguments());
        }
        
        if (!args.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for(String arg : args) {
                sb.append(arg).append(' ');
            }
            params.put(STARTUP_ARGS_KEY, sb.toString());
        }
    }    
}
