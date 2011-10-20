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

package org.netbeans.modules.j2ee.ejbcore.ejb.wizard.dd;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Martin Adamek
 */
public class EjbJarXmlWizardPanel1 implements WizardDescriptor.Panel { 

    // generated by apisupport wizard
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    private final EjbJarXmlVisualPanel1 component = new EjbJarXmlVisualPanel1();
    private final InfoPanel infoPanel = new InfoPanel();
    private WizardDescriptor wizardDescriptor;
    private Project project;
    
    public EjbJarXmlWizardPanel1() {
        component.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                fireChangeEvent();
            }
        });
    }
    
    FileObject getSelectedLocation() {
        return component.getSelectedLocation();
    }
    
    Project getProject() {
        return project;
    }
    
    public Component getComponent() {
        FileObject selectedDDLocation = component.getSelectedLocation();
        if (selectedDDLocation != null) {
            if (selectedDDLocation.isFolder()) {
                return component;
            } else {
                infoPanel.setText(NbBundle.getMessage(EjbJarXmlWizardPanel1.class,"ERR_FileExists"));
                return infoPanel;
            }
        } else {
            infoPanel.setText(NbBundle.getMessage(EjbJarXmlWizardPanel1.class,"ERR_NoValidLocation"));
            return infoPanel;
        }
    }
    
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean isValid() {
        if (component.getSelectedLocation() == null) {
            return false;
        }
        return true;
    }
    
    public final void addChangeListener(ChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
    
    public final void removeChangeListener(ChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    protected final void fireChangeEvent() {
        Iterator<ChangeListener> listenersIterator;
        synchronized (listeners) {
            listenersIterator = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent changeEvent = new ChangeEvent(this);
        while (listenersIterator.hasNext()) {
            listenersIterator.next().stateChanged(changeEvent);
        }
    }
    
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        if (project == null) {
            project = Templates.getProject(wizardDescriptor);
            component.setProject(project);
        }
        String displayName = NbBundle.getMessage(EjbJarXmlWizardPanel1.class, "LBL_DDWizardTitle"); //NOI18N
        wizardDescriptor.putProperty ("NewFileWizard_Title", displayName); // NOI18N
    }
    
    public void storeSettings(Object settings) {}

    private class InfoPanel extends JPanel{
        private JLabel infoText;

        public InfoPanel() {
            infoText = new JLabel();
            add(infoText);
        }

        public void setText(String text){
            infoText.setText(text);
//            revalidate();
        }
    }
}

