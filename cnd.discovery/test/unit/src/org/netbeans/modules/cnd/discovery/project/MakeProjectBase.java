/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.discovery.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.junit.MockServices;
import org.netbeans.modules.cnd.api.execution.ExecutionListener;
import org.netbeans.modules.cnd.api.execution.NativeExecutor;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmInclude;
import org.netbeans.modules.cnd.api.model.CsmModel;
import org.netbeans.modules.cnd.api.model.CsmModelAccessor;
import org.netbeans.modules.cnd.api.model.CsmProject;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.discovery.projectimport.ImportProject;
import org.netbeans.modules.cnd.makeproject.MakeProjectType;
import org.netbeans.modules.cnd.modelimpl.csm.core.ModelImpl;
import org.netbeans.modules.cnd.modelimpl.repository.RepositoryUtils;
import org.netbeans.modules.cnd.test.BaseTestCase;
import org.netbeans.modules.cnd.test.CndCoreTestUtils;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author Alexander Simon
 */
public abstract class MakeProjectBase extends BaseTestCase {
    private static final boolean OPTIMIZE_NATIVE_EXECUTIONS =true;
    private static final boolean TRACE = true;

    public MakeProjectBase(String name) {
        super(name);
        if (TRACE) {
            System.setProperty("cnd.discovery.trace.projectimport", "true"); // NOI18N
        }
        //System.setProperty("org.netbeans.modules.cnd.makeproject.api.runprofiles", "true"); // NOI18N
        //System.setProperty("cnd.mode.unittest", "true");
        System.setProperty("cnd.make.project.creation.skip.notify.header.extension", "true");
        //Logger.getLogger("org.netbeans.modules.editor.settings.storage.Utils").setLevel(Level.SEVERE);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //MockServices.setServices(MakeProjectType.class);
        startupModel();
    }

    @Override
    protected List<Class> getServises() {
        List<Class> list = new ArrayList<Class>();
        list.add(MakeProjectType.class);
        list.addAll(super.getServises());
        return list;
    }

    private void startupModel() {
        ModelImpl model = (ModelImpl) CsmModelAccessor.getModel();
        model.startup();
        RepositoryUtils.cleanCashes();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        shutdownModel();
    }

    private final void shutdownModel() {
        ModelImpl model = (ModelImpl) CsmModelAccessor.getModel();
        waitModelTasks(model);
        model.shutdown();
        waitModelTasks(model);
        RepositoryUtils.cleanCashes();
        RepositoryUtils.debugClear();
    }

    private void waitModelTasks(ModelImpl model) {
        Cancellable task = model.enqueueModelTask(new Runnable() {
            public void run() {
            }
        }, "wait finished other tasks"); //NOI18N
        if (task instanceof Task) {
            ((Task) task).waitFinished();
        }
    }

    public void performTestProject(String URL, List<String> additionalScripts){
        try {
            final String path = download(URL, additionalScripts);
            final File configure = new File(path+File.separator+"configure");
            final File makeFile = new File(path+File.separator+"Makefile");
            if (!configure.exists()) {
                if (!makeFile.exists()){
                    assertTrue("Cannot find configure or Makefile in folder "+path, false);
                }
            }
            WizardDescriptor wizard = new WizardDescriptor() {
                @Override
                public synchronized Object getProperty(String name) {
                    if ("simpleMode".equals(name)) {
                        return Boolean.TRUE;
                    } else if ("path".equals(name)) {
                        return path;
                    } else if ("configureName".equals(name)) {
                        if (OPTIMIZE_NATIVE_EXECUTIONS && makeFile.exists()) {
                            // optimization on developer computer:
                            // run configure only once
                            return null;
                        } else {
                            return path+"/configure";
                        }
                    } else if ("realFlags".equals(name)) {
                        if (path.indexOf("cmake-")>0) {
                            return "CFLAGS=\"-g3 -gdwarf-2\" CXXFLAGS=\"-g3 -gdwarf-2\" CMAKE_BUILD_TYPE=Debug CMAKE_CXX_FLAGS_DEBUG=\"-g3 -gdwarf-2\" CMAKE_C_FLAGS_DEBUG=\"-g3 -gdwarf-2\"";
                        } else {
                            return "CFLAGS=\"-g3 -gdwarf-2\" CXXFLAGS=\"-g3 -gdwarf-2\"";
                        }
                    } else if ("buildProject".equals(name)) {
                        if (OPTIMIZE_NATIVE_EXECUTIONS && makeFile.exists() && findObjectFiles(path)) {
                            // optimization on developer computer:
                            // make only once
                            return Boolean.FALSE;
                        } else {
                            return Boolean.TRUE;
                        }
                    }
                    return null;
                }
            };
            ImportProject importer = new ImportProject(wizard);
            importer.setUILessMode();
            importer.create();
            OpenProjects.getDefault().open(new Project[]{importer.getProject()}, false);
            while(!importer.isFinished()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            CsmModel model = CsmModelAccessor.getModel();
            Project makeProject = importer.getProject();
            assertTrue("Not found model", model != null);
            assertTrue("Not found make project", makeProject != null);
            NativeProject np = makeProject.getLookup().lookup(NativeProject.class);
            assertTrue("Not found native project", np != null);
            CsmProject csmProject = model.getProject(np);
            assertTrue("Not found model project", csmProject != null);
            csmProject.waitParse();
            perform(csmProject);
            OpenProjects.getDefault().close(new Project[]{makeProject});
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            assertTrue(ex.getMessage(), false);
        }
    }

    private boolean findObjectFiles(String path){
        return findObjectFiles(new File(path));
    }
    
    private boolean findObjectFiles(File file){
        if (file.isDirectory()) {
            for(File f : file.listFiles()){
                if (f.isDirectory()) {
                    boolean b = findObjectFiles(f);
                    if (b) {
                        return true;
                    }
                } else if (f.isFile() && f.getName().endsWith(".o")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void perform(CsmProject csmProject) {
        if (TRACE) {
            System.err.println("Model content:");
        }
        for (CsmFile file : csmProject.getAllFiles()) {
            if (TRACE) {
                System.err.println("\t"+file.getAbsolutePath());
            }
            for(CsmInclude include : file.getIncludes()){
                assertTrue("Not resolved include directive "+include.getIncludeName()+" in file "+file.getAbsolutePath(), include.getIncludeFile() != null);
            }
        }
    }

    private String download(String urlName, List<String> additionalScripts) throws IOException {
        String zipName = urlName.substring(urlName.lastIndexOf('/')+1);
        String tarName = zipName.substring(0, zipName.lastIndexOf('.'));
        String packageName = tarName.substring(0, tarName.lastIndexOf('.'));
        File fileDataPath = CndCoreTestUtils.getDownloadBase();
        String dataPath = fileDataPath.getAbsolutePath();

        String createdFolder = dataPath+"/"+packageName;
        final AtomicBoolean finish = new AtomicBoolean(false);
        ExecutionListener listener = new ExecutionListener() {
            public void executionStarted() {
            }
            public void executionFinished(int rc) {
                finish.set(true);
            }
        };
        NativeExecutor ne = null;
        File fileCreatedFolder = new File(createdFolder);
        if (!fileCreatedFolder.exists()){
            fileCreatedFolder.mkdirs();
        }
        if (fileCreatedFolder.list().length == 0){
            System.err.println(dataPath+"#wget "+urlName);
            ne = new NativeExecutor(dataPath,"wget", urlName, new String[0], "wget", "run", false, false);
            waitExecution(ne, listener, finish);
            System.err.println(dataPath+"#gzip -d "+zipName);
            ne = new NativeExecutor(dataPath,"gzip", "-d "+zipName, new String[0], "gzip", "run", false, false);
            waitExecution(ne, listener, finish);
            System.err.println(dataPath+"#tar xf "+tarName);
            ne = new NativeExecutor(dataPath,"tar", "xf "+tarName, new String[0], "tar", "run", false, false);
            waitExecution(ne, listener, finish);
            if (additionalScripts != null) {
                for(String s: additionalScripts){
                    int i = s.indexOf(' ');
                    String command = s.substring(0,i);
                    String arguments = s.substring(i+1);
                    if (command.startsWith(".")) {
                        command = createdFolder+"/"+command;
                    }
                    System.err.println(createdFolder+"#"+command+" "+arguments);
                    ne = new NativeExecutor(createdFolder, command, arguments, new String[0], command, "run", false, false);
                    waitExecution(ne, listener, finish);
                }
            }
        }
        System.err.println(createdFolder+"#rm -rf nbproject");
        ne = new NativeExecutor(createdFolder, "rm", "-rf nbproject", new String[0], "rm", "run", false, false);
        waitExecution(ne, listener, finish);
        return createdFolder;
    }

    private void waitExecution(NativeExecutor ne, ExecutionListener listener, AtomicBoolean finish){
        finish.set(false);
        ne.addExecutionListener(listener);
        try {
            ne.execute();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        while(!finish.get()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
