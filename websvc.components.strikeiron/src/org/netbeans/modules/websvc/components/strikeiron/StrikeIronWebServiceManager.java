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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.websvc.components.strikeiron;

import javax.swing.Action;
import org.netbeans.modules.websvc.components.strikeiron.actions.FindServiceAction;
import org.netbeans.modules.websvc.manager.api.WebServiceDescriptor;
import org.netbeans.modules.websvc.manager.spi.WebServiceManagerExt;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author nam
 */
public class StrikeIronWebServiceManager implements WebServiceManagerExt {
    public static final String STRIKE_IRON_GROUP = NbBundle.getMessage(StrikeIronWebServiceManager.class, "STRIKE_IRON_GROUP");
    public Action[] getGroupActions(Node node) {
        /*if (node.getName().startsWith(STRIKE_IRON_GROUP)) {
            return new Action[] { SystemAction.get(FindServiceAction.class) };
        } else {*/
            return EMPTY_ACTIONS;
        //}
    }

    public Action[] getMethodActions(Node node) {
        return EMPTY_ACTIONS;
    }

    public Action[] getPortActions(Node node) {
        return EMPTY_ACTIONS;
    }

    public Action[] getWebServiceActions(Node node) {
        return EMPTY_ACTIONS;
    }

    public Action[] getWebServicesRootActions(Node node) {
        return EMPTY_ACTIONS;
    }

    public boolean wsServiceAddedExt(WebServiceDescriptor wsMetadataDesc) {
        return true;
    }

    public boolean wsServiceRemovedExt(WebServiceDescriptor wsMetadataDesc) {
        return true;
    }
    
}