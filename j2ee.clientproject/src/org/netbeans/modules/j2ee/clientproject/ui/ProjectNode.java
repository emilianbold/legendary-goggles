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

package org.netbeans.modules.j2ee.clientproject.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.j2ee.clientproject.classpath.ClassPathSupport;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.HelpCtx;
import org.openide.util.lookup.Lookups;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.NodeAction;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.modules.j2ee.clientproject.UpdateHelper;
import org.netbeans.modules.j2ee.clientproject.ui.customizer.AppClientProjectProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.util.Exceptions;



/**
 * ProjectNode represents a dependent project under the Libraries Node.
 * It is a leaf node with the following actions: {@link OpenProjectAction},
 * {@link ShowJavadocAction} and {@link RemoveClassPathRootAction}
 * @author Tomas Zezula
 */
class ProjectNode extends AbstractNode {

    private static final String PROJECT_ICON = "org/netbeans/modules/j2ee/clientproject/ui/resources/projectDependencies.gif";    //NOI18N
    private static final Component CONVERTOR_COMPONENT = new Panel();

    private final AntArtifact antArtifact;
    private final URI artifactLocation;
    private Image cachedIcon;

    ProjectNode (AntArtifact antArtifact, URI artifactLocation, UpdateHelper helper, PropertyEvaluator eval, ReferenceHelper refHelper, String classPathId, String entryId, String includedLibrariesElement) {
        super (Children.LEAF, createLookup(antArtifact, artifactLocation, helper, eval, refHelper, classPathId, entryId, includedLibrariesElement));
        this.antArtifact = antArtifact;
        this.artifactLocation = artifactLocation;
    }

    @Override
    public String getDisplayName () {        
        ProjectInformation info = getProjectInformation();        
        if (info != null) {
            return MessageFormat.format(NbBundle.getMessage(ProjectNode.class,"TXT_ProjectArtifactFormat"),
                    new Object[] {info.getDisplayName(), artifactLocation.toString()});
        }
        else {
            return NbBundle.getMessage (ProjectNode.class,"TXT_UnknownProjectName");
        }
    }

    @Override
    public String getName () {
        return this.getDisplayName();
    }

    @Override
    public Image getIcon(int type) {
        if (cachedIcon == null) {
            ProjectInformation info = getProjectInformation();
            if (info != null) {
                Icon icon = info.getIcon();
                //XXX: There should be an API for Icon -> Image conversion,
                //issue: http://www.netbeans.org/issues/show_bug.cgi?id=52562
                if (icon instanceof ImageIcon) {
                    cachedIcon = ((ImageIcon)icon).getImage();
                }
                else {
                    int height = icon.getIconHeight();
                    int width = icon.getIconWidth();
                    cachedIcon = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
                    icon.paintIcon( CONVERTOR_COMPONENT, cachedIcon.getGraphics(), 0, 0 );
                }
            }
            else {
                cachedIcon = Utilities.loadImage(PROJECT_ICON);
            }
        }
        return cachedIcon;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return this.getIcon(type);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get (OpenProjectAction.class),
            SystemAction.get (ShowJavadocAction.class),
            SystemAction.get (RemoveClassPathRootAction.class),
        };
    }

    @Override
    public Action getPreferredAction () {
        return getActions(false)[0];
    }
    
    private ProjectInformation getProjectInformation () {
        Project p = this.antArtifact.getProject();
        if (p != null) {
            return ProjectUtils.getInformation(p);
        }
        return null;
    }
    
    private static Lookup createLookup (AntArtifact antArtifact, URI artifactLocation, 
            UpdateHelper helper, PropertyEvaluator eval, ReferenceHelper refHelper, 
            String classPathId, String entryId, String includedLibrariesElement) {
        Project p = antArtifact.getProject();
        Object[] content;
        if (p == null) {
            content = new Object[1];
        }
        else {
            content = new Object[3];
            content[1] = new JavadocProvider(antArtifact, artifactLocation);
            content[2] = p;
        }
        content[0] = new Removable(helper, eval, refHelper, classPathId, entryId, includedLibrariesElement);        
        Lookup lkp = Lookups.fixed(content);
        return lkp;
    }    

    private static class JavadocProvider implements ShowJavadocAction.JavadocProvider {

        private final AntArtifact antArtifact;
        private final URI artifactLocation;

        JavadocProvider (AntArtifact antArtifact, URI artifactLocation) {
            this.antArtifact = antArtifact;
            this.artifactLocation = artifactLocation;
        }


        public boolean hasJavadoc() {
            return findJavadoc().size() > 0;
        }

        public void showJavadoc() {
            Set<URL> us = findJavadoc();
            URL[] urls = us.toArray(new URL[us.size()]);
            URL pageURL = ShowJavadocAction.findJavadoc("overview-summary.html",urls); // NOI18N
            if (pageURL == null) {
                pageURL = ShowJavadocAction.findJavadoc("index.html",urls); // NOI18N
            }
            ProjectInformation info = null;
            Project p = this.antArtifact.getProject ();
            if (p != null) {
                info = ProjectUtils.getInformation(p);
            }
            ShowJavadocAction.showJavaDoc (pageURL, info == null ?
                NbBundle.getMessage (ProjectNode.class,"TXT_UnknownProjectName") : info.getDisplayName());
        }
        
        private Set<URL> findJavadoc() {            
            File scriptLocation = this.antArtifact.getScriptLocation();            
            Set<URL> urls = new HashSet<URL>();
            try {
                URL artifactURL = scriptLocation.toURI().resolve(this.artifactLocation).normalize().toURL();
                if (FileUtil.isArchiveFile(artifactURL)) {
                    artifactURL = FileUtil.getArchiveRoot(artifactURL);
                }
                urls.addAll(Arrays.asList(JavadocForBinaryQuery.findJavadoc(artifactURL).getRoots()));                
            } catch (MalformedURLException mue) {
                Exceptions.printStackTrace(mue);                
            }                                    
            return urls;
        }

    }

    private static class OpenProjectAction extends NodeAction {
        private static final long serialVersionUID = 1L;

        protected void performAction(Node[] activatedNodes) {
            Project[] projects = new Project[activatedNodes.length];
            for (int i=0; i<projects.length;i++) {
                projects[i] = activatedNodes[i].getLookup().lookup(Project.class);
            }
            OpenProjects.getDefault().open(projects, false);
        }

        protected boolean enable(Node[] activatedNodes) {
            final Collection/*<Project>*/ openedProjects =Arrays.asList(OpenProjects.getDefault().getOpenProjects());
            for (int i=0; i<activatedNodes.length; i++) {
                Project p;
                if ((p = activatedNodes[i].getLookup().lookup(Project.class)) == null) {
                    return false;
                }
                if (openedProjects.contains(p)) {
                    return false;
                }
            }
            return true;
        }

        public String getName() {
            return NbBundle.getMessage (ProjectNode.class,"CTL_OpenProject");
        }

        public HelpCtx getHelpCtx() {
            return new HelpCtx (OpenProjectAction.class);
        }

        @Override
        protected boolean asynchronous() {
            return false;
        }
    }

    private static class Removable implements RemoveClassPathRootAction.Removable {

        private final UpdateHelper helper;
        private final PropertyEvaluator eval;
        private final ReferenceHelper refHelper;
        private final String classPathId;
        private final String entryId;
        private final String includedLibrariesElement;
        private final ClassPathSupport cs;

        Removable (UpdateHelper helper, PropertyEvaluator eval, ReferenceHelper refHelper, String classPathId, String entryId, String includedLibrariesElement) {
            this.helper = helper;
            this.eval = eval;
            this.refHelper = refHelper;
            this.classPathId = classPathId;
            this.entryId = entryId;
            this.includedLibrariesElement = includedLibrariesElement;
            
            this.cs = new ClassPathSupport( eval, refHelper, helper.getAntProjectHelper(), 
                                            AppClientProjectProperties.WELL_KNOWN_PATHS, 
                                            AppClientProjectProperties.LIBRARY_PREFIX, 
                                            AppClientProjectProperties.LIBRARY_SUFFIX, 
                                            AppClientProjectProperties.ANT_ARTIFACT_PREFIX );        
        }

        public boolean canRemove () {
            //Allow to remove only entries from PROJECT_PROPERTIES, same behaviour as the project customizer
            EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
            return props.getProperty (classPathId) != null;
        }

        public Project remove() {
            // The caller has write access to ProjectManager
            // and ensures the project will be saved.
            
            boolean removed = false;
            EditableProperties props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
            String raw = props.getProperty (classPathId);
            List resources = cs.itemsList(raw, includedLibrariesElement);
            for (Iterator i = resources.iterator(); i.hasNext();) {
                ClassPathSupport.Item item = (ClassPathSupport.Item)i.next();
                if (entryId.equals(AppClientProjectProperties.getAntPropertyName(item.getReference()))) {
                    i.remove();
                    removed = true;
                }
            }
            if (removed) {
                String[] itemRefs = cs.encodeToStrings(resources.iterator(), includedLibrariesElement);
                props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);    //Reread the properties, PathParser changes them
                props.setProperty(classPathId, itemRefs);
                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);

                String ref = "${" + entryId + "}"; //NOI18N
                if (!RemoveClassPathRootAction.isReferenced (new EditableProperties[] {
                        props,
                        helper.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH)}, ref)) {
                    refHelper.destroyReference (ref);
                }

                return FileOwnerQuery.getOwner(helper.getAntProjectHelper().getProjectDirectory());
            } else {
                return null;
            }
        }
    }
}
