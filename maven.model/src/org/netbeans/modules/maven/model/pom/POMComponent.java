/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.model.pom;

import java.util.List;
import javax.xml.namespace.QName;
import org.netbeans.modules.xml.xam.dom.DocumentComponent2;

/**
 * Interface for all the components in the model.
 *
 * @author mkleint
 */
public interface POMComponent extends DocumentComponent2<POMComponent> {
    
    public static final String EXTENSIBILITY_ELEMENT_PROPERTY = "extensibilityElement"; // NOI18N
    
    /**
     * Get the owner model of this component.
     * 
     * @return  the owner model
     */
    @Override
    POMModel getModel();
    
    void accept(POMComponentVisitor visitor);
        
    /**
     * Adds a child extensibility element.
     * 
     * @param ee    a new child extensibility element
     */
    void addExtensibilityElement(POMExtensibilityElement ee);
    
    /**
     * Removes an existing child extensibility element.
     * 
     * @param ee    an existing child extensibility element
     */
    void removeExtensibilityElement(POMExtensibilityElement ee);
    
    /**
     * Gets a list of all child extensibility elements.
     * 
     * @return  a list of all child extensibility elements
     */
    List<POMExtensibilityElement> getExtensibilityElements();
    
    /**
     * Gets a list of child extensibility elements of the given type.
     * 
     * @param type  type of child extensibility elements
     * @return  a list of child extensibility elements of the given type
     */
    <T extends POMExtensibilityElement> List<T> getExtensibilityElements(Class<T> type);
        

    String getChildElementText(QName qname);
    void setChildElementText(String propertyName, String text, QName qname);
    /**
     * find the location in document for the given simple child element
     *
     * @param qname
     * @return position in document or -1 if not present.
     */
    int findChildElementPosition(QName qname);
}
