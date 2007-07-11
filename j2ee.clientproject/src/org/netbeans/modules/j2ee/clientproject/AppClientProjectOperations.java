/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.clientproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.j2ee.clientproject.classpath.AppClientProjectClassPathExtender;
import org.netbeans.modules.j2ee.clientproject.ui.customizer.AppClientProjectProperties;
import org.netbeans.modules.j2ee.dd.api.client.AppClient;
import org.netbeans.modules.j2ee.dd.api.client.DDProvider;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOperationImplementation;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class AppClientProjectOperations implements DeleteOperationImplementation, CopyOperationImplementation, MoveOperationImplementation {
    
    private final AppClientProject project;
    
    public AppClientProjectOperations(AppClientProject project) {
        this.project = project;
    }
    
    private static void addFile(FileObject projectDirectory, String fileName, List<FileObject> result) {
        FileObject file = projectDirectory.getFileObject(fileName);
        
        if (file != null) {
            result.add(file);
        }
    }
    
    public List<FileObject> getMetadataFiles() {
        FileObject projectDirectory = project.getProjectDirectory();
        List<FileObject> files = new ArrayList<FileObject>();
        
        addFile(projectDirectory, "nbproject", files); // NOI18N
        addFile(projectDirectory, "build.xml", files); // NOI18N
        addFile(projectDirectory, "catalog.xml", files); //NOI18N
        
        return files;
    }
    
    public List<FileObject> getDataFiles() {
        List<FileObject> files = new ArrayList<FileObject>();
        
        FileObject metaInf = project.getCarModule().getMetaInf();
        if (metaInf != null) {
            files.add(metaInf);
        }
        
        SourceRoots src = project.getSourceRoots();
        FileObject[] srcRoots = src.getRoots();
        
        for (int cntr = 0; cntr < srcRoots.length; cntr++) {
            files.add(srcRoots[cntr]);
        }
        
        PropertyEvaluator evaluator = project.evaluator();
        String prop = evaluator.getProperty(AppClientProjectProperties.SOURCE_ROOT);
        if (prop != null) {
            FileObject projectDirectory = project.getProjectDirectory();
            FileObject srcDir = project.getAntProjectHelper().resolveFileObject(prop);
            if (projectDirectory != srcDir && !files.contains(srcDir)) {
                files.add(srcDir);
            }
        }
        
        SourceRoots test = project.getTestSourceRoots();
        FileObject[] testRoots = test.getRoots();
        
        for (int cntr = 0; cntr < testRoots.length; cntr++) {
            files.add(testRoots[cntr]);
        }
        
        File resourceDir = project.getCarModule().getResourceDirectory();
        if (resourceDir != null) {
            FileObject resourceFO = FileUtil.toFileObject(resourceDir);
            if (resourceFO != null) {
                files.add(resourceFO);
            }
        }
        
        return files;
    }
    
    public void notifyDeleting() throws IOException {
        AppClientActionProvider ap = project.getLookup().lookup(AppClientActionProvider.class);
        
        assert ap != null;
        
        Lookup context = Lookups.fixed(new Object[0]);
        Properties p = new Properties();
        String[] targetNames = ap.getTargetNames(ActionProvider.COMMAND_CLEAN, context, p);
        FileObject buildXML = project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
        
        assert targetNames != null;
        assert targetNames.length > 0;
        
        ActionUtils.runTarget(buildXML, targetNames, p).waitFinished();
        
        AppClientProjectClassPathExtender extender = project.getLookup().lookup(AppClientProjectClassPathExtender.class);
        extender.notifyDeleting();
    }
    
    public void notifyDeleted() throws IOException {
        project.getAntProjectHelper().notifyDeleted();
    }
    
    public void notifyCopying() {
        //nothing.
    }
    
    public void notifyCopied(Project original, File originalPath, String nueName) {
        if (original == null) {
            //do nothing for the original project.
            return ;
        }
        
        project.getReferenceHelper().fixReferences(originalPath);
        fixOtherReferences(originalPath);
        
        project.setName(nueName);
    }
    
    public void notifyMoving() throws IOException {
        if (!this.project.getUpdateHelper().requestSave()) {
            throw new IOException(NbBundle.getMessage(AppClientProjectOperations.class,
                    "MSG_OldProjectMetadata")); // NOI18N
        }
        notifyDeleting();
    }
    
    public void notifyMoved(Project original, File originalPath, final String newName) {
        if (original == null) {
            project.getAntProjectHelper().notifyDeleted();
            return ;
        }
        
        final String oldProjectName = project.getName();
        
        project.setName(newName);
        project.getReferenceHelper().fixReferences(originalPath);
        fixOtherReferences(originalPath);
        
        ProjectManager.mutex().writeAccess(new Runnable() {
            public void run() {
                AntProjectHelper helper = project.getAntProjectHelper();
                EditableProperties projectProps = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                
                String jarName = projectProps.get(AppClientProjectProperties.JAR_NAME);
                String oldName = jarName.substring(0, jarName.length() - 4);
                if (jarName.endsWith(".jar") && oldName.equals(oldProjectName)) { //NOI18N
                    projectProps.put(AppClientProjectProperties.JAR_NAME, newName + ".jar"); //NOI18N
                }
                
                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProps);
                FileObject ddFO = project.getCarModule().getDeploymentDescriptor();
                if (ddFO != null) {
                    try {
                        AppClient appClient = DDProvider.getDefault().getDDRoot(ddFO);
                        appClient.setDisplayName(newName);
                        appClient.write(ddFO);
                    } catch (IOException ioe) {
                        Logger.getLogger("global").log(Level.WARNING, null, ioe);
                    }
                }
            }
        });
    }
    
    private void fixOtherReferences(final File originalPath) {
        final String property = AppClientProjectProperties.META_INF;
        final File projectDir = FileUtil.toFile(project.getProjectDirectory());
        
        ProjectManager.mutex().writeAccess(new Runnable() {
            public void run() {
                EditableProperties props = project.getAntProjectHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                String path = props.getProperty(property);
                if (path == null) {
                    return;
                }
                
                if (path.startsWith(originalPath.getAbsolutePath())) {
                    String relative = PropertyUtils.relativizeFile(originalPath, new File(path));
                    String fixedPath = new File(projectDir, relative).getAbsolutePath();
                    props.setProperty(property, fixedPath);
                    project.getAntProjectHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                }
            }
        });
    }
}
