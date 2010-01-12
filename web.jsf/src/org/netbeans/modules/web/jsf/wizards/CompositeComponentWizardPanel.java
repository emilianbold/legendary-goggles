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
package org.netbeans.modules.web.jsf.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.jsf.JSFUtils;
import org.netbeans.modules.web.wizards.Utilities;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.TemplateWizard;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author alexeybutenko
 */
public class CompositeComponentWizardPanel implements WizardDescriptor.Panel, ChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CompositeComponentVisualPanel component;
    private String text;
    private TemplateWizard wizard;
    Project project;
    SourceGroup[] folders;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private static final String RESOURCES_FOLDER = "resources"; //NOI18N
    //TODO how to add [,] to the regular expression?
    private static final Pattern INVALID_FILENAME_CHARACTERS = Pattern.compile("[`~!@#$%^&*()=+\\|{};:'\",<>/?]"); // NOI18N
    private static final Pattern INVALID_FOLDERNAME_CHARACTERS = Pattern.compile("[`~!@#$%^&*()=+|{};:'\",<>?]"); // NOI18N

    public CompositeComponentWizardPanel(TemplateWizard wizard, SourceGroup[] folders, String selectedText) {
	this.wizard = wizard;
	text = selectedText;
	this.folders = folders;
	project = Templates.getProject(wizard);
    }

    //we need to run it in AWT thread because of the editor initialization
    public Component getComponent() {
	if (SwingUtilities.isEventDispatchThread()) {
	    return _getComponent();
	} else {
	    final AtomicReference<Component> ref = new AtomicReference<Component>();
	    try {
		SwingUtilities.invokeAndWait(new Runnable() {
		    public void run() {
			ref.set(_getComponent());
		    }
		});
	    } catch (InterruptedException ex) {
		Exceptions.printStackTrace(ex);
	    } catch (InvocationTargetException ex) {
		Exceptions.printStackTrace(ex);
	    }
	    return ref.get();
	}
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component _getComponent() {
	if (component == null) {
	    component = new CompositeComponentVisualPanel(project, folders, text);
	    component.addChangeListener(this);
	}
	return component;
    }

    public HelpCtx getHelp() {
	// Show no Help button for this panel:
	return HelpCtx.DEFAULT_HELP;
	// If you have context help:
	// return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {

    	String errorMessage = null;
	WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
	if (!Utilities.isJavaEE6(wizard) && !(JSFUtils.isJavaEE5((TemplateWizard) wizard) && JSFUtils.isJSF20(webModule))) {
	    errorMessage = NbBundle.getMessage(CompositeComponentWizardPanel.class, "ERR_Not_JSF20");//NOI18N
	}
	if (component == null || component.getTargetName() == null || component.getTargetGroup() == null) {
	    return false;
	}

	if (component.getTargetFolder() == null || !component.getTargetFolder().startsWith(RESOURCES_FOLDER)) {
	    errorMessage = NbBundle.getMessage(CompositeComponentWizardPanel.class, "ERR_No_resources_folder");//NOI18N
	} else if (component.getTargetFolder().equals(RESOURCES_FOLDER) || component.getTargetFolder().equals(RESOURCES_FOLDER + File.separatorChar)) {
	    errorMessage = NbBundle.getMessage(CompositeComponentWizardPanel.class, "ERR_No_component_folder");//NOI18N
	}

	String filename = component.getTargetName();
	if ("".equals(filename) || INVALID_FILENAME_CHARACTERS.matcher(filename).find()) {
	    errorMessage = NbBundle.getMessage(CompositeComponentWizardPanel.class, "ERR_Wrong_Filename");//NOI18N
	}

	String folderName = component.getTargetFolder();
	if (INVALID_FOLDERNAME_CHARACTERS.matcher(folderName).find()) {
	    errorMessage = NbBundle.getMessage(CompositeComponentWizardPanel.class, "ERR_Wrong_Foldername");//NOI18N
	}

	if (webModule != null && webModule.getDocumentBase() != null) {
	    String expectedExtension = Templates.getTemplate(wizard).getExt();
	    expectedExtension = expectedExtension.length() == 0 ? "" : "." + expectedExtension;   //NOI18N
	    FileObject targetFile = webModule.getDocumentBase().getFileObject(folderName + "/" + filename + expectedExtension);   //NOI18N
	    if (targetFile != null) {
		errorMessage = filename + expectedExtension + " already exist"; //NOI18N
	    }
	}

	//check the selection context
	if (Boolean.TRUE.equals((Boolean) wizard.getProperty("incorrectActionContext"))) {//NOI18N
	    //we can still finish the wizard
	    wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, NbBundle.getMessage(CompositeComponentVisualPanel.class, "MSG_Invalid_Selection"));
	    return true;
	}

	//current prefix in the panel
	String prefix = component.getPrefix();
	if(prefix.length() == 0) {
	    errorMessage = NbBundle.getMessage(CompositeComponentWizardPanel.class, "MSG_Library_Prefix_Empty");//NOI18N
	} else {
	    //there's some prefix
	    //check for used prefixes
	    //get declared libraries map //namespace2prefix map
	    Map<String, String> declaredPrefixes = (Map<String, String>)wizard.getProperty("declaredPrefixes");
	    //compute namespace of the library according to the folder
	    String ccLibNamespace = component.getCompositeComponentURI();
	    //warning if the current library namespace is not already declared, but the prefix is used for
	    //another library
	    if(!prefix.equals(declaredPrefixes.get(ccLibNamespace)) && declaredPrefixes.values().contains(prefix)) {
		//the selected prefix is already in use, show warning, but let the user finish the wizard
		wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, NbBundle.getMessage(CompositeComponentVisualPanel.class, "MSG_Already_Used_Prefix", component.getPrefix()));//NOI18N
		return true;
	    }
	}
	
	wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, errorMessage);
	
	return errorMessage == null;
    }

    public void addChangeListener(ChangeListener l) {
	changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
	changeSupport.removeChangeListener(l);
    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
	if (settings instanceof TemplateWizard) {
	    this.wizard = (TemplateWizard) settings;
	    this.project = Templates.getProject(wizard);
	    if (component == null) {
		getComponent();
	    }
	    if (component != null) {

		FileObject preselectedTarget = Templates.getTargetFolder(wizard);
		if (preselectedTarget == null) {
		    preselectedTarget = project.getProjectDirectory();
		}
		// Try to preserve the already entered target name
		String targetName = Templates.getTargetName(wizard);
		// Init values
		component.initValues(Templates.getTemplate(wizard), preselectedTarget, targetName);
	    }
	    Object substitute = component.getClientProperty("NewFileWizard_Title"); // NOI18N
	    if (substitute != null) {
		wizard.putProperty("NewFileWizard_Title", substitute); // NOI18N
	    }
//	    wizard.putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{ // NOI18N
//			//                NbBundle.getBundle (CompositeComponentWizardPanel.class).getString ("LBL_TemplatesPanel_Name"), // NOI18N
//			NbBundle.getBundle(CompositeComponentWizardPanel.class).getString("LBL_SimpleTargetChooserPanel_Name")}); // NOI18N
	}
    }

    public void storeSettings(Object settings) {
	if (settings instanceof TemplateWizard) {
	    TemplateWizard wizard = (TemplateWizard) settings;

	    if (WizardDescriptor.PREVIOUS_OPTION.equals(wizard.getValue())) {
		return;
	    }
	    if (!wizard.getValue().equals(WizardDescriptor.CANCEL_OPTION) && isValid()) {

		FileObject template = Templates.getTemplate(wizard);

		String name = component.getTargetName();
		if (name.indexOf('/') > 0) { // NOI18N
		    name = name.substring(name.lastIndexOf('/') + 1);
		}

		Templates.setTargetFolder(wizard, getTargetFolderFromGUI());
		Templates.setTargetName(wizard, name);
	    }
	    wizard.putProperty("NewFileWizard_Title", null); // NOI18N
	    wizard.putProperty("selectedPrefix", component.getPrefix()); //NOI18N
	}
    }

    private FileObject getTargetFolderFromGUI() {
	FileObject rootFolder = component.getTargetGroup().getRootFolder();
	String folderName = component.getTargetFolder();
	String newObject = component.getTargetName();

	if (newObject.indexOf('/') > 0) { // NOI18N
	    String path = newObject.substring(0, newObject.lastIndexOf('/')); // NOI18N
	    folderName = folderName == null || "".equals(folderName) ? path : folderName + '/' + path; // NOI18N
	}

	FileObject targetFolder;
	if (folderName == null) {
	    targetFolder = rootFolder;
	} else {
	    targetFolder = rootFolder.getFileObject(folderName);
	}

	if (targetFolder == null) {
	    // XXX add deletion of the file in uninitalize ow the wizard
	    try {
		targetFolder = FileUtil.createFolder(rootFolder, folderName);
	    } catch (IOException ioe) {
		// Can't create the folder
		throw new IllegalArgumentException(ioe); // ioe already annotated
	    }
	}

	return targetFolder;
    }

    public void stateChanged(ChangeEvent e) {
	changeSupport.fireChange();
    }
}

