/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

/*
 * AXIDocumentImpl.java
 *
 * Created on May 10, 2006, 1:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.xml.axi.impl;

import java.util.HashMap;
import org.netbeans.modules.xml.axi.AXIComponent;
import org.netbeans.modules.xml.axi.AXIDocument;
import org.netbeans.modules.xml.axi.AXIModel;
import org.netbeans.modules.xml.schema.model.SchemaComponent;

/**
 * AXIDocument implementation.
 *
 * @author Samaresh (Samaresh.Panda@Sun.Com)
 */
public final class AXIDocumentImpl extends AXIDocument {
    
    /**
     * Creates a new instance of AXIDocumentImpl
     */
    public AXIDocumentImpl(AXIModel model, SchemaComponent schemaComponent) {
        super(model, schemaComponent);
    }
    
    public AXIComponent findChild(SchemaComponent child) {
        //first try from cache, if not found, lookup children
        AXIComponent axiChild = globalChildrenCache.get(child);
        if(axiChild != null)
            return axiChild;
        for(AXIComponent c : getChildren()) {
            if(c.getPeer() == child) {
                addToCache(c);
                return c;
            }
        }
        return null;        
    }

    public void addToCache(AXIComponent child) {
        if(child.getPeer() == null)
            return;
        globalChildrenCache.put(child.getPeer(), child);
    }
    
    public void removeFromCache(AXIComponent child) {
        globalChildrenCache.remove(child.getPeer());
    }
    
    private HashMap<SchemaComponent, AXIComponent> globalChildrenCache =
            new HashMap<SchemaComponent, AXIComponent>();
}
