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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.modelimpl.impl.services;

import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmClassifier;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmNamespace;
import org.netbeans.modules.cnd.api.model.CsmNamespaceDefinition;
import org.netbeans.modules.cnd.api.model.CsmScope;
import org.netbeans.modules.cnd.api.model.CsmScopeElement;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.modelimpl.csm.core.Resolver;
import org.netbeans.modules.cnd.spi.model.CsmBaseUtilitiesProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Vladimir Voskresensky
 */
@ServiceProvider(service = CsmBaseUtilitiesProvider.class, position = 1000)
public class BaseUtilitiesProviderImpl extends CsmBaseUtilitiesProvider {
    private static final BaseUtilitiesProviderImpl IMPL = new BaseUtilitiesProviderImpl();
    /**for lookup*/
    public BaseUtilitiesProviderImpl() {}
    
    @Override
    public CsmFunction getFunctionDeclaration(CsmFunction fun) {
        return IMPL.getFunctionDeclaration(fun, null);
    }

    @Override
    public CsmNamespace getFunctionNamespace(CsmFunction fun) {
        return IMPL.getFunctionNamespace(fun, null);
    }
    
    @Override
    public CsmNamespace getClassNamespace(CsmClassifier cls) {
        return IMPL.getClassNamespace(cls, null);
    }
    
    public CsmFunction getFunctionDeclaration(CsmFunction fun, Resolver parent) {
        assert (fun != null) : "must be not null";
        CsmFunction funDecl = fun;
        if (CsmKindUtilities.isFunctionDefinition(funDecl)) {
            if (funDecl instanceof Resolver.SafeFunctionDeclarationProvider) {
                funDecl = ((Resolver.SafeFunctionDeclarationProvider) funDecl).getFunctionDeclaration(parent);
            } else {
                funDecl = ((CsmFunctionDefinition) funDecl).getDeclaration();
            }
        }
        return funDecl;
    }
    
    public CsmNamespace getFunctionNamespace(CsmFunction fun, Resolver parent) {
        CsmFunction decl = getFunctionDeclaration(fun, parent);
        fun = decl != null ? decl : fun;
        if (fun != null) {
            CsmScope scope = fun.getScope();
            if (CsmKindUtilities.isNamespaceDefinition(scope)) {
                CsmNamespace ns = ((CsmNamespaceDefinition) scope).getNamespace();
                return ns;
            } else if (CsmKindUtilities.isNamespace(scope)) {
                CsmNamespace ns = (CsmNamespace) scope;
                return ns;
            } else if (CsmKindUtilities.isClass(scope)) {
                return getClassNamespace((CsmClass) scope, parent);
            }
        }
        return null;
    }
    
    public CsmNamespace getClassNamespace(CsmClassifier cls, Resolver parent) {
        CsmScope scope = cls.getScope();
        while (scope != null) {
            if (CsmKindUtilities.isNamespace(scope)) {
                return (CsmNamespace) scope;
            }
            if (CsmKindUtilities.isScopeElement(scope)) {
                scope = ((CsmScopeElement) scope).getScope();
            } else {
                break;
            }
        }
        return null;
    }
    
    public static BaseUtilitiesProviderImpl getImpl() {
        return IMPL;
    }
    
    
}
