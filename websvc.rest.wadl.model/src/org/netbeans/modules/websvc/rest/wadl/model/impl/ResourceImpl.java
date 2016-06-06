/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008, 2016 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2008.11.07 at 12:36:44 PM PST
//


package org.netbeans.modules.websvc.rest.wadl.model.impl;

import java.util.Collection;
import org.netbeans.modules.websvc.rest.wadl.model.*;
import org.w3c.dom.Element;

public class ResourceImpl extends ResourceTypeImpl implements Resource {

    /** Creates a new instance of OperationImpl */
    public ResourceImpl(WadlModel model, Element e) {
        super(model, e);
    }

    public ResourceImpl(WadlModel model){
        this(model, createNewElement(WadlQNames.RESOURCE.getQName(), model));
    }

    public Collection<Resource> getResource() {
        return getChildren(Resource.class);
    }

    public void addResource(Resource resource) {
        addAfter(RESOURCE_PROPERTY, resource, TypeCollection.FOR_RESOURCE.types());
    }

    public void removeResource(Resource resource) {
        removeChild(RESOURCE_PROPERTY, resource);
    }

    public String getType() {
        return getAttribute(WadlAttribute.TYPE);
    }

    public void setType(String base) {
        setAttribute(TYPE_PROPERTY, WadlAttribute.TYPE, base);
    }

    public String getQueryType() {
        return getAttribute(WadlAttribute.QUERY_TYPE);
    }

    public void setQueryType(String base) {
        setAttribute(QUERY_TYPE_PROPERTY, WadlAttribute.QUERY_TYPE, base);
    }

    public String getPath() {
        return getAttribute(WadlAttribute.PATH);
    }

    public void setPath(String base) {
        setAttribute(PATH_PROPERTY, WadlAttribute.PATH, base);
    }

}
