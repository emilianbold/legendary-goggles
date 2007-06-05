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
package org.netbeans.modules.visual.action;

import org.netbeans.api.visual.action.ConnectDecorator;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;

/**
 *
 * @author alex
 */
public class ExtendedConnectAction extends ConnectAction {

    private long modifiers;
    private boolean macLocking;

    public ExtendedConnectAction(ConnectDecorator decorator, Widget interractionLayer, ConnectProvider provider, long modifiers) {
        super(decorator, interractionLayer, provider);
        this.modifiers = modifiers;
    }

    protected boolean isLocked () {
        return super.isLocked ()  ||  macLocking;
    }

    public WidgetAction.State mousePressed(Widget widget, WidgetAction.WidgetMouseEvent event) {
        if (macLocking)
            return State.createLocked (widget, this);
        if ((event.getModifiers () & modifiers) != 0) {
            if ((Utilities.getOperatingSystem () & Utilities.OS_MAC) != 0)
                macLocking = true;
            return super.mousePressed(widget,event);
        }
        return State.REJECTED;
    }

    public WidgetAction.State mouseReleased(Widget widget, WidgetAction.WidgetMouseEvent event) {
        macLocking = false;
        if (isLocked ())
            return super.mouseReleased(widget,event);
        else
            return State.REJECTED;
    }

    public State mouseMoved (Widget widget, WidgetMouseEvent event) {
        if (macLocking)
            return super.mouseDragged (widget, event);
        return super.mouseMoved (widget, event);
    }
}
