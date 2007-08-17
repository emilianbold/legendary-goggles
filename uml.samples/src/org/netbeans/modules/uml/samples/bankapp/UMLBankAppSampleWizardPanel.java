/*
 * Copyright (c) 2007, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Sun Microsystems, Inc. nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.netbeans.modules.uml.samples.bankapp;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class UMLBankAppSampleWizardPanel implements WizardDescriptor.Panel,
    WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel
{
    
    private WizardDescriptor wizardDescriptor;
    private UMLBankAppSamplePanelVisual component;
    
    /** Creates a new instance of templateWizardPanel */
    public UMLBankAppSampleWizardPanel()
    {
    }
    
    public Component getComponent()
    {
        if (component == null)
        {
            component = new UMLBankAppSamplePanelVisual(this);
            component.setName(NbBundle.getMessage(UMLBankAppSampleWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }
    
    public HelpCtx getHelp()
    {
        return new HelpCtx(UMLBankAppSampleWizardPanel.class);
    }
    
    public boolean isValid()
    {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);
    public final void addChangeListener(ChangeListener l)
    {
        synchronized (listeners)
        {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l)
    {
        synchronized (listeners)
        {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent()
    {
        Iterator it;
        synchronized (listeners)
        {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext())
        {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    public void readSettings(Object settings)
    {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);
    }
    
    public void storeSettings(Object settings)
    {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
    }
    
    public boolean isFinishPanel()
    {
        return true;
    }
    
    public void validate() throws WizardValidationException
    {
        getComponent();
        component.validate(wizardDescriptor);
    }
    
}
