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

package org.netbeans.modules.apisupport.project.ui.wizard.updatecenter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.apisupport.project.CreatedModifiedFiles;
import org.netbeans.modules.apisupport.project.layers.LayerUtils;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.openide.WizardDescriptor;

/**
 * Wizard for creating new update catalog.
 *
 * @author Jiri Rechtacek
 */
final class NewUpdateCenterIterator extends BasicWizardIterator {

    private DataModel data;
    
    public static NewUpdateCenterIterator createIterator() {
        return new NewUpdateCenterIterator();
    }
    
    public Set instantiate() throws IOException {
        CreatedModifiedFiles cmf = data.refreshCreatedModifiedFiles();
        cmf.run();
        return Collections.singleton (LayerUtils.layerForProject (data.getProject ()).getLayerFile ());
    }

    protected BasicWizardIterator.Panel[] createPanels (WizardDescriptor wiz) {
        data = new DataModel (wiz);
        return new BasicWizardIterator.Panel[] {
            new UpdateCenterRegistrationPanel (wiz, data)
        };
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        super.uninitialize(wiz);
        data = null;
    }
        
}
