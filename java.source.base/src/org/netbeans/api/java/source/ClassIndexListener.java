/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.api.java.source;

import java.util.EventListener;

/**
 * Listener for changes in {@link ClassIndex}.
* <P>
* When attached to a {ClassIndex} it listens for addition,
* removal and modification of declared types.
* <P>
*
* @see ClassIndex#addClassIndexListener
 * @author Tomas Zezula
 */
public interface ClassIndexListener extends EventListener {
    
    /**
     * Called when the new declared types are added
     * into the {@link ClassIndex}
     * @param event specifying the added types
     */
    public void typesAdded (TypesEvent event);
    
    /**
     * Called when declared types are removed
     * from the {@link ClassIndex}
     * @param event specifying the removed types
     */
    public void typesRemoved (TypesEvent event);
        
    /**
     * Called when some declared types are changed.
     * @param event specifying the changed types
     */
    public void typesChanged (TypesEvent event);
    
    /**
     * Called when new roots are added
     * into the {@link ClassPath} for which the {@link ClassIndex}
     * was created.
     * @param event specifying the added roots
     */
    public void rootsAdded (RootsEvent event);
    
    /**
     * Called when root are removed
     * from the {@link ClassPath} for which the {@link ClassIndex}
     * was created.
     * @param event specifying the removed roots
     */
    public void rootsRemoved (RootsEvent event);
    
}
