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

package org.netbeans.modules.j2ee.earproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.j2ee.earproject.ui.customizer.EarProjectProperties;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOperationImplementation;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * @author Jan Lahoda
 */
public class EarProjectOperations implements DeleteOperationImplementation, CopyOperationImplementation, MoveOperationImplementation {
    
    private final EarProject project;
    //RELY: Valid only on original project after the notifyMoving or notifyCopying was called
    private String libraryPath;
    //RELY: Valid only on original project after the notifyMoving or notifyCopying was called
    private File libraryFile;
    //RELY: Valid only on original project after the notifyMoving or notifyCopying was called
    private boolean libraryWithinProject;
    //RELY: Valid only on original project after the notifyMoving or notifyCopying was called
    private String absolutesRelPath;    
    
    public EarProjectOperations(EarProject project) {
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
        addFile(projectDirectory, "src", files); // NOI18N
        addFile(projectDirectory, ".cvsignore", files); // NOI18N
        
        return files;
    }
    
    public List<FileObject> getDataFiles() {
        // add libraries folder if it is within project:
        AntProjectHelper helper = project.getAntProjectHelper();
        if (helper.getLibrariesLocation() != null) {
            File f = helper.resolveFile(helper.getLibrariesLocation());
            if (f != null && f.exists()) {
                FileObject libFolder = FileUtil.toFileObject(f).getParent();
                if (FileUtil.isParentOf(project.getProjectDirectory(), libFolder)) {
                    return Collections.<FileObject>singletonList(libFolder);
                }
            }
        }
        return Collections.emptyList();
    }
    
    public void notifyDeleting() throws IOException {
        EarActionProvider ap = project.getLookup().lookup(EarActionProvider.class);
        
        assert ap != null;
        
        Lookup context = Lookups.fixed(new Object[0]);
        Properties p = new Properties();
        String[] targetNames = ap.getTargetNames(ActionProvider.COMMAND_CLEAN, context, p);
        FileObject buildXML = project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
        
        assert targetNames != null;
        assert targetNames.length > 0;
        
        ActionUtils.runTarget(buildXML, targetNames, p).waitFinished();
    }
    
    public void notifyDeleted() throws IOException {
        project.getAntProjectHelper().notifyDeleted();
    }
   
    public void notifyCopying() {
        rememberLibraryLocation();
    }
    
    public void notifyCopied(Project original, File originalPath, String nueName) {
        if (original == null) {
            //do nothing for the original project.
            return ;
        }
        
        EarProjectOperations origOperations = original.getLookup().lookup(EarProjectOperations.class);
        fixLibraryLocation(origOperations);
        
        project.setName(nueName);
    }
    
    public void notifyMoving() throws IOException {
        rememberLibraryLocation();
        notifyDeleting();
    }
    
    public void notifyMoved(Project original, File originalPath, final String newName) {
        if (original == null) {
            project.getAntProjectHelper().notifyDeleted();
            return ;
        }
        
        EarProjectOperations origOperations = original.getLookup().lookup(EarProjectOperations.class);
        fixLibraryLocation(origOperations);

        final String oldProjectName = project.getLookup().lookup(ProjectInformation.class).getName();

        project.setName(newName);
	
        ProjectManager.mutex().writeAccess(new Runnable() {
            public void run() {
		AntProjectHelper helper = project.getAntProjectHelper();
		EditableProperties projectProps = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);

		String earName = projectProps.get(EarProjectProperties.JAR_NAME);
		String oldName = earName.substring(0, earName.length() - 4);
		if (earName.endsWith(".ear") && oldName.equals(oldProjectName)) { //NOI18N
                    projectProps.put(EarProjectProperties.JAR_NAME, newName + ".ear"); //NOI18N
                }

		helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProps);
            }
        });

    }
    
    private void fixLibraryLocation(EarProjectOperations original) throws IllegalArgumentException {
        String libPath = original.libraryPath;
        if (libPath != null) {
            if (!new File(libPath).isAbsolute()) {
                //relative path to libraries
                if (!original.libraryWithinProject) {
                    File file = original.libraryFile;
                    if (file == null) {
                        // could happen in some rare cases, but in that case the original project was already broken, don't fix.
                        return;
                    }
                    String relativized = PropertyUtils.relativizeFile(FileUtil.toFile(project.getProjectDirectory()), file);
                    if (relativized != null) {
                        project.getAntProjectHelper().setLibrariesLocation(relativized);
                    } else {
                        //cannot relativize, use absolute path
                        project.getAntProjectHelper().setLibrariesLocation(file.getAbsolutePath());
                    }
                } else {
                    //got copied over to new location.. the relative path is the same..
                }
            } else {

                //absolute path to libraries..
                if (original.libraryWithinProject) {
                    if (original.absolutesRelPath != null) {
                        project.getAntProjectHelper().setLibrariesLocation(PropertyUtils.resolveFile(FileUtil.toFile(project.getProjectDirectory()), original.absolutesRelPath).getAbsolutePath());
                    }
                } else {
                    // absolute path to an external folder stays the same.
                }
            }
        }
    }
    
    
    private void rememberLibraryLocation() {
     libraryWithinProject = false;
        absolutesRelPath = null;
        libraryPath = project.getAntProjectHelper().getLibrariesLocation();
        if (libraryPath != null) {
            File prjRoot = FileUtil.toFile(project.getProjectDirectory());
            libraryFile = PropertyUtils.resolveFile(prjRoot, libraryPath);
            if (FileOwnerQuery.getOwner(libraryFile.toURI()) == project && 
                    libraryFile.getAbsolutePath().startsWith(prjRoot.getAbsolutePath())) {
                //do not update the relative path if within the project..
                libraryWithinProject = true;
                FileObject fo = FileUtil.toFileObject(libraryFile);
                if (new File(libraryPath).isAbsolute() && fo != null) {
                    // if absolte path within project, it will get moved/copied..
                    absolutesRelPath = FileUtil.getRelativePath(project.getProjectDirectory(), fo);
                }
            }
        }
    }
    
        
}
