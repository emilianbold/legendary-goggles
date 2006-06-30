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


package org.netbeans.modules.image;


import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle;


/**
 * Action which zooms in of an image.
 *
 * @author  Lukas Tadial
 */
public class ZoomInAction extends CallableSystemAction {

    /** Generated serial version UID. */
    static final long serialVersionUID = -8705899978543961455L;


    /** Perform action. */
    public void performAction() {
        TopComponent curComponent = TopComponent.getRegistry().getActivated();
        if(curComponent instanceof ImageViewer)
            ((ImageViewer) curComponent).zoomIn();
    }
    
    /** Gets action name. Implements superclass abstract method. */
    public String getName() {
        return NbBundle.getBundle(ZoomInAction.class).getString("LBL_ZoomIn");
    }
    
    /** Gets action help context. Implemenets superclass abstract method.*/
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    /** Overrides superclass method. */
    public boolean isEnabled() {
        return true;
    }
    
    /** Gets icon resource. Overrides superclass method. */
    protected String iconResource() {
        return "org/netbeans/modules/image/zoomIn.gif"; // NOI18N
    }
}
