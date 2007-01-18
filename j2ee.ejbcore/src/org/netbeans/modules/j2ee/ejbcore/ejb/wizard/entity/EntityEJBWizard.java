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

package org.netbeans.modules.j2ee.ejbcore.ejb.wizard.entity;

import java.io.IOException;
import org.netbeans.modules.j2ee.ejbcore.api.codegeneration.EntityGenerator;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.j2ee.common.Util;
import org.netbeans.modules.j2ee.ejbcore.Utils;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
/**
 *
 * @author Chris Webster
 * @author Martin Adamek
 */
public final class EntityEJBWizard implements WizardDescriptor.InstantiatingIterator {

    private WizardDescriptor.Panel[] panels;
    private int index = 0;
    private EntityEJBWizardDescriptor ejbPanel;
    private WizardDescriptor wiz;

    public static EntityEJBWizard create () {
        return new EntityEJBWizard ();
    }

    public String name () {
        return NbBundle.getMessage (EntityEJBWizard.class, "LBL_EntityEJBWizardTitle");
    }

    public void uninitialize(WizardDescriptor wiz) {
    }

    public void initialize(WizardDescriptor wizardDescriptor) {
        wiz = wizardDescriptor;
        Project project = Templates.getProject(wiz);
        SourceGroup[] sourceGroups = Util.getJavaSourceGroups(project);
        ejbPanel = new EntityEJBWizardDescriptor();
        WizardDescriptor.Panel wizardPanel = JavaTemplates.createPackageChooser(project,sourceGroups, ejbPanel, true);

        JComponent jComponent = (JComponent) wizardPanel.getComponent();
        Util.changeLabelInComponent(jComponent, NbBundle.getMessage(EntityEJBWizard.class, "LBL_JavaTargetChooserPanelGUI_ClassName_Label"), NbBundle.getMessage(EntityEJBWizard.class, "LBL_EJB_Name") );
        Util.hideLabelAndLabelFor(jComponent, NbBundle.getMessage(EntityEJBWizard.class, "LBL_JavaTargetChooserPanelGUI_CreatedFile_Label"));
        panels = new WizardDescriptor.Panel[] {wizardPanel};
        Utils.mergeSteps(wiz, panels, null);

    }

    public Set instantiate () {
        boolean isCMP = ejbPanel.isCMP();
        EntityGenerator entityGenerator = EntityGenerator.create(
                Templates.getTargetName(wiz), 
                Templates.getTargetFolder(wiz), 
                ejbPanel.hasRemote(), 
                ejbPanel.hasLocal(), 
                isCMP, 
                ejbPanel.getPrimaryKeyClassName()
                );
        FileObject result = null;
        try {
            result = entityGenerator.generate();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return result == null ? Collections.<FileObject>emptySet() : Collections.singleton(result);
    }

    public void addChangeListener(ChangeListener listener) {
    }

    public void removeChangeListener(ChangeListener listener) {
    }

    public boolean hasPrevious () {
        return index > 0;
    }

    public boolean hasNext () {
    return index < panels.length - 1;
    }

    public void nextPanel () {
        if (! hasNext ()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel () {
        if (! hasPrevious ()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }
}

