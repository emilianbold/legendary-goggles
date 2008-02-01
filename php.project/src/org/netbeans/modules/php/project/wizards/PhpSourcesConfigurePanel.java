/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.php.project.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 * @author ads
 *
 */
final class PhpSourcesConfigurePanel implements Panel, WizardDescriptor.FinishablePanel {
    
    public static final String DEFAULT_SOURCE_ROOT = "DefaultSourceRootDirectory"; // NOI18N
    public static final String DEFAULT_PHP_VERSION = "DefaultPhpVersion"; // NOI18N
    
    
    PhpSourcesConfigurePanel(NewPhpProjectWizardIterator.WizardType type) {
        myType = type;
        init( true );
    }
    
    public PhpSourcesConfigurePanel( String title, String defaultPhpVersion , String defaultSourceRoot, 
            boolean full, NewPhpProjectWizardIterator.WizardType type  ) 
    {
        myTitle = title;
        myDefaultPhpVersion = defaultPhpVersion;
        myDefaultSourceRoot = defaultSourceRoot;
        myType = type;
        init( full );
    }
    
    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.FinishablePanel#isFinishPanel()
     */
    public boolean isFinishPanel() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener( ChangeListener listener ) {
        synchronized ( myListeners ) {
            myListeners.add(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener( ChangeListener listener ) {
        synchronized ( myListeners ) {
            myListeners.remove(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getComponent()
     */
    public Component getComponent() {
        return myComponent;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getHelp()
     */
    public HelpCtx getHelp() {
        return new HelpCtx( PhpSourcesConfigurePanel.class  );
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#isValid()
     */
    public boolean isValid() {
        return getVisualPanel().dataIsValid( getDescriptor() );
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#readSettings(java.lang.Object)
     */
    public void readSettings( Object settings ) {
        myDescriptor = (WizardDescriptor) settings;
        getVisualPanel().read ( getDescriptor() );
        
        /*
         * Copied from Make project configuration panel
         */
        // XXX hack, TemplateWizard in final setTemplateImpl() forces new wizard's title
        // this name is used in NewProjectWizard to modify the title
        Object substitute = getVisualPanel().getClientProperty(
                PhpConfigureProjectVisual.NEW_PROJECT_WIZARD_TITLE);
        if (substitute != null) {
            getDescriptor().putProperty(
                    PhpConfigureProjectVisual.NEW_PROJECT_WIZARD_TITLE, substitute);
        }
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#storeSettings(java.lang.Object)
     */
    public void storeSettings( Object settings ) {
        WizardDescriptor descriptor = (WizardDescriptor) settings;
        getVisualPanel().store(descriptor);
        ((WizardDescriptor) descriptor).putProperty(
                PhpConfigureProjectVisual.NEW_PROJECT_WIZARD_TITLE, null); 
    }
    
    public String getDefaultSourceRoot() {
        if ( myDefaultSourceRoot == null ) {
            return  NbBundle.getBundle(NewPhpProjectWizardIterator.class).
                getString( DEFAULT_SOURCE_ROOT );
        }
        return myDefaultSourceRoot;
    }
    
    public String getDefaultPhpVersion() {
        if ( myDefaultPhpVersion == null ) {
            return  NbBundle.getBundle(NewPhpProjectWizardIterator.class).
                getString( DEFAULT_PHP_VERSION );
        }
        return myDefaultPhpVersion;
    }
    
    public static File getCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return null;
        }
    }

    String getTitle() {
        return myTitle;
    }
    
    boolean isFull() {
        return isFull;
    }
    
    final void fireChangeEvent() {
        ChangeListener[] listeners;
        synchronized ( myListeners) {
            listeners = myListeners.toArray( 
                    new ChangeListener[ myListeners.size() ] );
        }
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }
    
    private PhpSourcePanelVisual getVisualPanel() {
        return myComponent;
    }
    
    private WizardDescriptor getDescriptor() {
        return myDescriptor;
    }
    
    private void init( boolean full ) {
        isFull = full;
        myComponent = new PhpSourcePanelVisual( this );
        
        
    }

    public NewPhpProjectWizardIterator.WizardType getWizardType(){
        return myType;
    }

    private PhpSourcePanelVisual myComponent;
    
    private final Collection<ChangeListener> myListeners = 
        new LinkedList<ChangeListener>();
    
    private WizardDescriptor myDescriptor;
    
    private String myTitle;
    
    private boolean isFull;
    
    private String myDefaultSourceRoot;
    private String myDefaultPhpVersion;

    private NewPhpProjectWizardIterator.WizardType myType;

}
