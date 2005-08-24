/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbcore.patterns;

import org.netbeans.modules.j2ee.api.ejbjar.EnterpriseReferenceContainer;
import org.openide.*;
import org.openide.util.*;

import java.io.*;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.ejbcore.api.codegeneration.EjbGenerationUtil;
import org.netbeans.modules.j2ee.ejbcore.Utils;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;

import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;


/** 
 *
 * @author Chris Webster
 * @author Martin Adamek
 */
public final class ServiceLocatorWizard implements WizardDescriptor.InstantiatingIterator {
    private WizardDescriptor.Panel[] panels;
    private int index = 0;
    private WizardDescriptor wiz;
    
    private static final String [] STEPS =
                                   new String [] { 
                                       NbBundle.getMessage (ServiceLocatorWizard.class, 
					     "LBL_SpecifyName")
                                   };
                               
    public String name () {
	return NbBundle.getMessage (ServiceLocatorWizard.class, 
			 	    "LBL_MessageServiceLocatorWizardTitle");
    }

    public void uninitialize(WizardDescriptor wiz) {
    }
    
    public void initialize(WizardDescriptor wizardDescriptor) {
        wiz = wizardDescriptor;
        Project project = Templates.getProject(wiz);
        Sources sources = (Sources) project.getLookup().lookup(Sources.class);
        SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        panels = new WizardDescriptor.Panel[] {JavaTemplates.createPackageChooser(project,sourceGroups)};
        Utils.mergeSteps(wiz, panels, STEPS);
    }
    
    public Set instantiate () throws IOException {
        FileObject pkg = Templates.getTargetFolder(wiz);
        String clsName = Templates.getTargetName(wiz);
        Project project = Templates.getProject(wiz);
        DataFolder df = DataFolder.findFolder(pkg);
        FileObject template = Templates.getTemplate(wiz);
        DataObject dTemplate = DataObject.find( template );                
        DataObject dobj = dTemplate.createFromTemplate( df, clsName);
        String pkgName = EjbGenerationUtil.getSelectedPackageName(pkg, project);
        String fullName = (pkgName.length()>0?pkgName+'.':"")+clsName;
        EnterpriseReferenceContainer erc = (EnterpriseReferenceContainer)
                project.getLookup().lookup(EnterpriseReferenceContainer.class);
        if (erc != null) {
            erc.setServiceLocatorName(fullName);
        }
        FileObject createdFile = dobj.getPrimaryFile();
        
        return Collections.singleton(createdFile); 
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener l) {
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener l) {
    }
    
    public boolean hasPrevious () {
        return index > 0;
    }
    
    public boolean hasNext () {
	return index < panels.length - 1;
    }
    
    public void nextPanel () {
        if (! hasNext ()) {
            throw new NoSuchElementException ();
        }
        index++;
    }
    
    public void previousPanel () {
        if (! hasPrevious ()) {
            throw new NoSuchElementException ();
        }
        index--;
    }
    
    public WizardDescriptor.Panel current () {
        return panels[index];
    }
    
}

