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

package org.netbeans.modules.websvc.spi.client;

import org.netbeans.modules.websvc.api.client.WebServicesClientView;
import org.netbeans.modules.websvc.client.WebServicesClientViewAccessor;

/**
 * Most general way to create {@link WebServicesView} instances.
 * You are not permitted to create them directly; instead you implement
 * {@link WebServicesViewImpl} and use this factory.
 *
 * @author Peter Williams
 */
public final class WebServicesClientViewFactory {

    private WebServicesClientViewFactory() {
    }

    public static WebServicesClientView createWebServicesClientView(WebServicesClientViewImpl spiWebServicesClientView) {
        return WebServicesClientViewAccessor.DEFAULT.createWebServicesClientView(spiWebServicesClientView);
    }
	
}
