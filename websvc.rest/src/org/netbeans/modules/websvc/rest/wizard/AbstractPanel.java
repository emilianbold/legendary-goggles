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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.websvc.rest.wizard;

import java.awt.Component;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.FinishablePanel;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

/**
 *
 * @author nam
 */
public abstract class AbstractPanel implements ChangeListener, FinishablePanel, 
    Panel 
{
    private final CopyOnWriteArrayList<ChangeListener> listeners = 
        new CopyOnWriteArrayList<ChangeListener>();
    protected java.lang.String panelName;
    protected org.openide.WizardDescriptor wizardDescriptor;

    public AbstractPanel (String name, WizardDescriptor wizardDescriptor) {
        this.panelName = name;
        this.wizardDescriptor = wizardDescriptor;
    }
    
    @Override
    public abstract java.awt.Component getComponent();

    @Override
    public abstract boolean isFinishPanel();

    public static interface Settings {
        void read(WizardDescriptor wizard);
        void store(WizardDescriptor wizard);
        boolean valid(WizardDescriptor wizard);
        void addChangeListener(ChangeListener l);
    }
    
    @Override
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        ((Settings)getComponent()).read(wizardDescriptor);
    }
    
    @Override
    public void storeSettings(Object settings) {
        ((Settings)getComponent()).store(wizardDescriptor);
    }

    @Override
    public boolean isValid() {
        if (getComponent() instanceof Settings) {
            return ((Settings)getComponent()).valid(wizardDescriptor);
        }
        return false;
    }
    
    @Override
    public final void addChangeListener(ChangeListener l) {
        listeners.add( l );
    }

    protected final void fireChangeEvent(ChangeEvent ev) {
        for ( ChangeListener listener : listeners ){
            listener.stateChanged(ev);
        }
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    public static void clearMessage(WizardDescriptor wizard, MessageType type) {
        setMessage(wizard, type, (String) null);
    }
    
    public static void clearErrorMessage(WizardDescriptor wizard) {
        setMessage(wizard, MessageType.ERROR, (String) null);
    }
    
    public static void clearInfoMessage(WizardDescriptor wizard) {
        setMessage(wizard, MessageType.INFO, (String) null);
    }
    
    public static void setMessage(WizardDescriptor wizard, Throwable t, MessageType type) {
        String message = "";
        if (t != null) {
            message = (t.getLocalizedMessage());
        }
        wizard.putProperty(type.getName(), message);
    }
    
    static void setMessage(WizardDescriptor wizard, MessageType type, String key, String... params) {
        String message = "";
        if (key != null) {
            message = (NbBundle.getMessage(AbstractPanel.class, key, params));
        }
        wizard.putProperty(type.getName(), message);
    }
    
    public static void setMessage(WizardDescriptor wizard, MessageType type, String key) {
        String message = "";
        if (key != null) {
            message = (NbBundle.getMessage(AbstractPanel.class, key));
        }
        wizard.putProperty(type.getName(), message);
    }

    protected void setMessage(MessageType type, java.lang.String key) {
        setMessage(wizardDescriptor, type, key);
    }
    
    public static void setErrorMessage(WizardDescriptor wizard, String key) {
        setMessage(wizard, MessageType.ERROR, key);
    }

    protected void setErrorMessage(java.lang.String key) {
        setMessage(wizardDescriptor, MessageType.ERROR, key);
    }
    
    public static void setInfoMessage(WizardDescriptor wizard, String key) {
        setMessage(wizard, MessageType.INFO, key);
    }

    protected void setInfoMessage(java.lang.String key) {
        setMessage(wizardDescriptor, MessageType.INFO, key);
    }

    @Override
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        Component c = getComponent();
        if (c instanceof Settings) {
            ((Settings)c).valid(wizardDescriptor);
        }
        fireChangeEvent(e);
    }
    
    public String getName() {
        return panelName;
    }
    
    public enum MessageType {
        INFO(WizardDescriptor.PROP_INFO_MESSAGE),
        WARNING(WizardDescriptor.PROP_WARNING_MESSAGE),
        ERROR(WizardDescriptor.PROP_ERROR_MESSAGE);
                
        private String name;
        
        MessageType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
