/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.php.editor.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQuery.Index;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.NameKind.Exact;
import org.netbeans.modules.php.editor.api.PhpElementKind;
import org.netbeans.modules.php.editor.api.elements.FieldElement;
import org.netbeans.modules.php.editor.api.elements.MethodElement;
import org.netbeans.modules.php.editor.api.elements.TypeConstantElement;
import org.netbeans.modules.php.editor.api.elements.TypeElement;
import org.netbeans.modules.php.editor.model.ClassScope;
import org.netbeans.modules.php.editor.model.ClassConstantElement;
import org.netbeans.modules.php.editor.model.ConstantElement;
import org.netbeans.modules.php.editor.model.FunctionScope;
import org.netbeans.modules.php.editor.model.IncludeElement;
import org.netbeans.modules.php.editor.model.IndexScope;
import org.netbeans.modules.php.editor.model.InterfaceScope;
import org.netbeans.modules.php.editor.model.MethodScope;
import org.netbeans.modules.php.editor.model.ModelElement;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.Occurence;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.api.elements.ElementFilter;
import org.netbeans.modules.php.editor.api.elements.FunctionElement;
import org.netbeans.modules.php.editor.api.elements.PhpElement;
import org.netbeans.modules.php.editor.model.ClassMemberElement;
import org.netbeans.modules.php.editor.model.FileScope;
import org.netbeans.modules.php.editor.model.NamespaceScope;
import org.netbeans.modules.php.editor.model.Occurence.Accuracy;
import org.netbeans.modules.php.editor.model.Scope;
import org.netbeans.modules.php.editor.model.TypeScope;
import org.netbeans.modules.php.editor.model.VariableName;
import org.netbeans.modules.php.editor.model.VariableScope;
import org.netbeans.modules.php.editor.model.nodes.ASTNodeInfo;
import org.netbeans.modules.php.editor.model.nodes.ASTNodeInfo.Kind;
import org.netbeans.modules.php.editor.model.nodes.ClassConstantDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.ClassDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.ConstantDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.FunctionDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.IncludeInfo;
import org.netbeans.modules.php.editor.model.nodes.InterfaceDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.MagicMethodDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.MethodDeclarationInfo;
import org.netbeans.modules.php.editor.model.nodes.PhpDocTypeTagInfo;
import org.netbeans.modules.php.editor.model.nodes.SingleFieldDeclarationInfo;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.ClassInstanceCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ClassName;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.GotoLabel;
import org.netbeans.modules.php.editor.parser.astnodes.GotoStatement;
import org.netbeans.modules.php.editor.parser.astnodes.Identifier;
import org.netbeans.modules.php.editor.parser.astnodes.Include;
import org.netbeans.modules.php.editor.parser.astnodes.InterfaceDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceName;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocTypeTag;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.StaticConstantAccess;
import org.netbeans.modules.php.editor.parser.astnodes.StaticDispatch;
import org.netbeans.modules.php.editor.parser.astnodes.StaticFieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.StaticMethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.openide.util.Union2;

/**
 * @author Radek Matous
 */
class OccurenceBuilder {

    private Map<ASTNodeInfo<Scalar>, ConstantElement> constDeclarations;
    private Map<ConstantDeclarationInfo, ConstantElement> constDeclarations53;
    private Map<ASTNodeInfo<Scalar>, Scope> constInvocations;
    private Map<ASTNodeInfo<Expression>, Scope> nsConstInvocations;
    private Map<ASTNodeInfo<FunctionDeclaration>, FunctionScope> fncDeclarations;
    private Map<ASTNodeInfo<MethodDeclaration>, MethodScope> methodDeclarations;
    private Map<MagicMethodDeclarationInfo, MethodScope> magicMethodDeclarations;
    private Map<ASTNodeInfo<MethodInvocation>, Scope> methodInvocations;
    private Map<ASTNodeInfo<Identifier>, ClassConstantElement> classConstantDeclarations;
    private Map<ASTNodeInfo<FunctionInvocation>, Scope> fncInvocations;
    private Map<ASTNodeInfo<StaticMethodInvocation>, Scope> staticMethodInvocations;
    private Map<ASTNodeInfo<StaticFieldAccess>, Scope> staticFieldInvocations;
    private Map<ASTNodeInfo<StaticConstantAccess>, Scope> staticConstantInvocations;
    private Map<ClassDeclarationInfo, ClassScope> clasDeclarations;
    private Map<InterfaceDeclarationInfo, InterfaceScope> ifaceDeclarations;
    private HashMap<PhpDocTypeTagInfo, Scope> docTags;
    private Map<ASTNodeInfo<ClassName>, Scope> clasNames;
    private Map<ASTNodeInfo<ClassInstanceCreation>, Scope> clasInstanceCreations;
    private Map<ASTNodeInfo<Expression>, Scope> clasIDs;
    private Map<ASTNodeInfo<Expression>, Scope> ifaceIDs;
    private Map<ASTNodeInfo<Variable>, Scope> variables;
    private HashMap<IncludeInfo, IncludeElement> includes;
    private HashMap<SingleFieldDeclarationInfo, FieldElementImpl> fldDeclarations;
    private HashMap<ASTNodeInfo<FieldAccess>, Scope> fieldInvocations;
    private volatile ElementInfo elementInfo;
    private Map<ASTNodeInfo<GotoLabel>, Scope> gotoLabel;
    private Map<ASTNodeInfo<GotoStatement>, Scope> gotoStatement;

    private final List<Occurence> cachedOccurences;

    OccurenceBuilder() {
        this(-1);
    }

    OccurenceBuilder(int offset) {
        this.constInvocations = new HashMap<ASTNodeInfo<Scalar>, Scope>();
        this.nsConstInvocations = new HashMap<ASTNodeInfo<Expression>, Scope>();
        this.constDeclarations = new HashMap<ASTNodeInfo<Scalar>, ConstantElement>();
        this.constDeclarations53 = new HashMap<ConstantDeclarationInfo, ConstantElement>();
        this.includes = new HashMap<IncludeInfo, IncludeElement>();
        this.fncInvocations = new HashMap<ASTNodeInfo<FunctionInvocation>, Scope>();
        this.fncDeclarations = new HashMap<ASTNodeInfo<FunctionDeclaration>, FunctionScope>();
        this.staticMethodInvocations = new HashMap<ASTNodeInfo<StaticMethodInvocation>, Scope>();
        this.methodDeclarations = new HashMap<ASTNodeInfo<MethodDeclaration>, MethodScope>();
        this.magicMethodDeclarations = new HashMap<MagicMethodDeclarationInfo, MethodScope>();
        this.methodInvocations = new HashMap<ASTNodeInfo<MethodInvocation>, Scope>();
        this.fieldInvocations = new HashMap<ASTNodeInfo<FieldAccess>, Scope>();
        this.staticFieldInvocations = new HashMap<ASTNodeInfo<StaticFieldAccess>, Scope>();
        this.staticConstantInvocations = new HashMap<ASTNodeInfo<StaticConstantAccess>, Scope>();
        this.clasDeclarations = new HashMap<ClassDeclarationInfo, ClassScope>();
        this.ifaceDeclarations = new HashMap<InterfaceDeclarationInfo, InterfaceScope>();
        this.clasNames = new HashMap<ASTNodeInfo<ClassName>, Scope>();
        this.clasInstanceCreations = new HashMap<ASTNodeInfo<ClassInstanceCreation>, Scope>();
        this.clasIDs = new HashMap<ASTNodeInfo<Expression>, Scope>();
        this.ifaceIDs = new HashMap<ASTNodeInfo<Expression>, Scope>();
        this.classConstantDeclarations = new HashMap<ASTNodeInfo<Identifier>, ClassConstantElement>();
        this.variables = new HashMap<ASTNodeInfo<Variable>, Scope>();
        this.fldDeclarations = new HashMap<SingleFieldDeclarationInfo, FieldElementImpl>();
        this.docTags = new HashMap<PhpDocTypeTagInfo, Scope>();
        this.gotoStatement = new HashMap<ASTNodeInfo<GotoStatement>, Scope>();
        this.gotoLabel = new HashMap<ASTNodeInfo<GotoLabel>, Scope>();

        this.cachedOccurences = new ArrayList<Occurence>();
    }

    void prepare(GotoStatement statement, ScopeImpl scope) {
        if (canBePrepared(statement, scope)) {
            ASTNodeInfo<GotoStatement> node = ASTNodeInfo.create(statement);
            gotoStatement.put(node, scope);

        }
    }

    void prepare(GotoLabel label, ScopeImpl scope) {
        if (canBePrepared(label, scope)) {
            ASTNodeInfo<GotoLabel> node = ASTNodeInfo.create(label);
            gotoLabel.put(node, scope);

        }
    }

    void prepare(FieldAccess fieldAccess, Scope scope) {
        if (canBePrepared(fieldAccess, scope)) {
            ASTNodeInfo<FieldAccess> node = ASTNodeInfo.create(fieldAccess);
            fieldInvocations.put(node, scope);

        }
    }

    void prepare(Include incl, IncludeElementImpl inclImpl) {
        if (canBePrepared(incl, inclImpl)) {
            IncludeInfo node = IncludeInfo.create(incl);
            includes.put(node, inclImpl);

        }
    }

    void prepare(MethodInvocation methodInvocation, Scope scope) {
        if (canBePrepared(methodInvocation, scope)) {
            ASTNodeInfo<MethodInvocation> node = ASTNodeInfo.create(methodInvocation);
            methodInvocations.put(node, scope);

        }
    }

    void prepare(SingleFieldDeclarationInfo info, FieldElementImpl fei) {
        SingleFieldDeclaration node = info.getOriginalNode();
        if (canBePrepared(node, fei)) {
            fldDeclarations.put(info, fei);
        }
    }

    void prepare(Variable variable, Scope scope) {
        if (canBePrepared(variable, scope)) {
            ASTNodeInfo<Variable> node = ASTNodeInfo.create(variable);
            variables.put(node, scope);

        }
    }

    void prepare(FunctionInvocation functionInvocation, Scope scope) {
        if (canBePrepared(functionInvocation, scope)) {
            ASTNodeInfo<FunctionInvocation> node = ASTNodeInfo.create(functionInvocation);
            this.fncInvocations.put(node, scope);

        }
    }

    void prepare(StaticMethodInvocation staticMethodInvocation, Scope scope) {
        if (canBePrepared(staticMethodInvocation, scope)) {
            ASTNodeInfo<StaticMethodInvocation> node = ASTNodeInfo.create(staticMethodInvocation);
            this.staticMethodInvocations.put(node, scope);

        }
    }

    void prepare(StaticFieldAccess staticFieldAccess, Scope scope) {
        if (canBePrepared(staticFieldAccess, scope)) {
            ASTNodeInfo<StaticFieldAccess> node = ASTNodeInfo.create(staticFieldAccess);
            staticFieldInvocations.put(node, scope);

        }
    }

    void prepare(StaticConstantAccess staticConstantAccess, Scope scope) {
        if (canBePrepared(staticConstantAccess, scope)) {
            ASTNodeInfo<StaticConstantAccess> node = ASTNodeInfo.create(staticConstantAccess);
            staticConstantInvocations.put(node, scope);

        }
    }

    void prepare(ClassName clsName, Scope scope) {
        if (canBePrepared(clsName, scope)) {
            ASTNodeInfo<ClassName> node = ASTNodeInfo.create(clsName);
            clasNames.put(node, scope);

        }
    }

    void prepare(Kind kind, Expression node, Scope scope) {
        ASTNodeInfo<Expression> nodeInfo = null;
        if (node instanceof Identifier) {
            nodeInfo = ASTNodeInfo.create(kind, (Identifier) node);
        } else if (node instanceof NamespaceName) {
            nodeInfo = ASTNodeInfo.create(kind, (NamespaceName) node);
        }
         if (nodeInfo != null && canBePrepared(node, scope)) {
            switch (nodeInfo.getKind()) {
                case CLASS:
                    clasIDs.put(nodeInfo, scope);
                    break;
                case IFACE:
                    ifaceIDs.put(nodeInfo, scope);
                    break;
                case CONSTANT:
                    if (node instanceof NamespaceName) {
                        nsConstInvocations.put(nodeInfo, scope);
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    void prepare(ClassInstanceCreation node, Scope scope) {
        ASTNodeInfo<ClassInstanceCreation> nodeInfo = ASTNodeInfo.create(node);
        if (canBePrepared(node, scope)) {
            clasInstanceCreations.put(nodeInfo, scope);
        }
    }

    void prepare(Kind kind, Scalar scalar, Scope scope) {
        ASTNodeInfo<Scalar> nodeInfo = ASTNodeInfo.create(kind, scalar);
        if (canBePrepared(scalar, scope)) {
            constInvocations.put(nodeInfo, scope);
        }
    }

    void prepare(ASTNodeInfo<Scalar> nodeInfo, ConstantElement constantElement) {
        Scalar scalar = nodeInfo.getOriginalNode();
        if (canBePrepared(scalar, constantElement)) {
            constDeclarations.put(nodeInfo, constantElement);
        }
    }

    void prepare(ConstantDeclarationInfo constantNodeInfo, ConstantElement scope) {
        if (constantNodeInfo != null && canBePrepared(constantNodeInfo.getOriginalNode(), scope)) {
            constDeclarations53.put(constantNodeInfo, scope);
        }
    }

    void prepare(PHPDocTypeTag pHPDocTag, Scope scope) {
        if (canBePrepared(pHPDocTag, scope)) {
            List<? extends PhpDocTypeTagInfo> infos = PhpDocTypeTagInfo.create(pHPDocTag, scope);
            for (PhpDocTypeTagInfo typeTagInfo : infos) {
                docTags.put(typeTagInfo, scope);
            }
        }
    }

    void prepare(ClassDeclaration classDeclaration, ClassScope scope) {
        if (canBePrepared(classDeclaration, scope)) {
            ClassDeclarationInfo node = ClassDeclarationInfo.create(classDeclaration);
            clasDeclarations.put(node, scope);
            prepare(Kind.CLASS, classDeclaration.getSuperClass(), scope);
            List<Expression> interfaes = classDeclaration.getInterfaes();
            for (Expression iface : interfaes) {
                prepare(Kind.IFACE, iface, scope);
            }

        }
    }

    void prepare(InterfaceDeclaration interfaceDeclaration, InterfaceScope scope) {
        if (canBePrepared(interfaceDeclaration, scope)) {
            InterfaceDeclarationInfo node = InterfaceDeclarationInfo.create(interfaceDeclaration);
            ifaceDeclarations.put(node, scope);
            List<Expression> interfaes = interfaceDeclaration.getInterfaes();
            for (Expression iface : interfaes) {
                prepare(Kind.IFACE, iface, scope);
            }

        }
    }

    void prepare(FunctionDeclaration functionDeclaration, FunctionScope scope) {
        if (canBePrepared(functionDeclaration, scope)) {
            FunctionDeclarationInfo node = FunctionDeclarationInfo.create(functionDeclaration);
            fncDeclarations.put(node, scope);

        }
    }

    void prepare(MethodDeclaration methodDeclaration, MethodScope scope) {
        if (canBePrepared(methodDeclaration, scope)) {
            MethodDeclarationInfo node = MethodDeclarationInfo.create(methodDeclaration, scope.getTypeScope());
            methodDeclarations.put(node, scope);

        }
    }

    void prepare(final MagicMethodDeclarationInfo node, MethodScope scope) {
        if (canBePrepared(node.getOriginalNode(), scope)) {
            if (node.getKind().equals(Kind.METHOD)) {
                magicMethodDeclarations.put(node, scope);
            }
        }
    }

    void prepare(ClassConstantDeclarationInfo constantNodeInfo, ClassConstantElement scope) {
        if (constantNodeInfo != null && canBePrepared(constantNodeInfo.getOriginalNode(), scope)) {
            classConstantDeclarations.put(constantNodeInfo, scope);
        }
    }

    /***
     *
     * @param offset
     * @return true if ElementInfo was set and even more represents different element than the previous one and
     * thus makes sense to recompute occurences. If false is returned then makes no sense to recompute occurences
     */
    private boolean setElementInfo(final int offset) {
        elementInfo = null;
        for (Entry<ASTNodeInfo<GotoStatement>, Scope> entry : gotoStatement.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<GotoLabel>, Scope> entry : gotoLabel.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<FieldAccess>, Scope> entry : fieldInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<IncludeInfo, IncludeElement> entry : includes.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), ModelUtils.getNamespaceScope(entry.getValue())), offset);
        }

        for (Entry<ASTNodeInfo<FieldAccess>, Scope> entry : fieldInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<MethodInvocation>, Scope> entry : methodInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<SingleFieldDeclarationInfo, FieldElementImpl> entry : fldDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<Variable>, Scope> entry : variables.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<FunctionInvocation>, Scope> entry : fncInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<StaticMethodInvocation>, Scope> entry : staticMethodInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<StaticFieldAccess>, Scope> entry : staticFieldInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<StaticConstantAccess>, Scope> entry : staticConstantInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<ClassName>, Scope> entry : clasNames.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<ClassInstanceCreation>, Scope> entry : clasInstanceCreations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<Expression>, Scope> entry : clasIDs.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<Expression>, Scope> entry : ifaceIDs.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<Scalar>, Scope> entry : constInvocations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<Scalar>, ConstantElement> entry : constDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ConstantDeclarationInfo, ConstantElement> entry : constDeclarations53.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<PhpDocTypeTagInfo, Scope> entry : docTags.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ClassDeclarationInfo, ClassScope> entry : clasDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<InterfaceDeclarationInfo, InterfaceScope> entry : ifaceDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<FunctionDeclaration>, FunctionScope> entry : fncDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<MethodDeclaration>, MethodScope> entry : methodDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<MagicMethodDeclarationInfo, MethodScope> entry : magicMethodDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        for (Entry<ASTNodeInfo<Identifier>, ClassConstantElement> entry : classConstantDeclarations.entrySet()) {
            setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
        }

        if (elementInfo == null) {
            for (Entry<ASTNodeInfo<Expression>, Scope> entry : nsConstInvocations.entrySet()) {
                setOffsetElementInfo(new ElementInfo(entry.getKey(), entry.getValue()), offset);
            }
        }
        return elementInfo != null ;
    }

    private boolean setElementInfo(final ModelElement element) {
        elementInfo = new ElementInfo(element);
        return true;
    }

    private void build(FileScopeImpl fileScope) {
//        List<Occurence> retval = new ArrayList<Occurence>();
        ASTNodeInfo.Kind kind = elementInfo != null ? elementInfo.getKind() : null;
        if (elementInfo != null && kind != null) {
            final IndexScope indexScope = ModelUtils.getIndexScope(fileScope);
            final Index index = indexScope.getIndex();
            cachedOccurences.clear();

            switch (kind) {
                case GOTO:
                    buildGotoLabels(elementInfo, fileScope, cachedOccurences);
                    buildGotoStatements(elementInfo, fileScope, cachedOccurences);
                    break;
                case FUNCTION:
                    final Set<FunctionElement> functions = index.getFunctions(NameKind.exact(elementInfo.getQualifiedName()));
                    elementInfo.setDeclarations(functions);
                    buildFunctionInvocations(elementInfo, fileScope, cachedOccurences);
                    buildFunctionDeclarations(elementInfo, fileScope, cachedOccurences);
                    break;
                case VARIABLE:
                    buildVariables(elementInfo, fileScope, cachedOccurences);
                    buildDocTagsForVars(elementInfo, fileScope, cachedOccurences);
                    break;
                case STATIC_METHOD:
                    buildStaticMethods(index, fileScope, cachedOccurences);
                    break;
                case FIELD:
                    buildFields(index, fileScope, cachedOccurences);
                    break;
                case STATIC_FIELD:
                    buildStaticFields(index, fileScope, cachedOccurences);
                    break;
                case CONSTANT:
                    Set<org.netbeans.modules.php.editor.api.elements.ConstantElement> constants =
                            index.getConstants(NameKind.exact(elementInfo.getQualifiedName()));

                    elementInfo.setDeclarations(constants);
                    cachedOccurences.clear();
                    buildConstantInvocations(elementInfo, fileScope, cachedOccurences);
                    buildConstantDeclarations(elementInfo, fileScope, cachedOccurences);
                    break;
                case CLASS_CONSTANT:
                case STATIC_CLASS_CONSTANT:
                    buildTypeConstants(index, fileScope, cachedOccurences);
                    break;
                case IFACE:
                case CLASS_INSTANCE_CREATION:
                case CLASS:
                    final QualifiedName qualifiedName = elementInfo.getQualifiedName();
                    final Set<TypeElement> types = index.getTypes(NameKind.exact(qualifiedName));
                    if (elementInfo.setDeclarations(types)) {
                        buildClassInstanceCreation(elementInfo, fileScope, cachedOccurences);
                        buildClassNames(elementInfo, fileScope, cachedOccurences);
                        buildClassIDs(elementInfo, fileScope, cachedOccurences);
                        buildClassDeclarations(elementInfo, fileScope, cachedOccurences);
                        buildDocTagsForClasses(elementInfo, fileScope, cachedOccurences);
                        buildInterfaceIDs(elementInfo, fileScope, cachedOccurences);
                        buildInterfaceDeclarations(elementInfo, fileScope, cachedOccurences);
                        buildClassInstanceCreation(elementInfo, fileScope, cachedOccurences);
                    }
                    break;
                case METHOD:
                    buildMethods(index, fileScope, cachedOccurences);
                    if (elementInfo.getModelElemnt() != null) {
                        //168149  -  Searching for usages of the __construct method
                        Scope scope = elementInfo.getScope();
                        if (scope instanceof MethodScope && MethodElement.CONSTRUCTOR_NAME.equalsIgnoreCase(elementInfo.getName())) {
                            buildMethods(index, fileScope, cachedOccurences);
                            setElementInfo((TypeScope)scope.getInScope());
                            if (elementInfo.setDeclarations(index.getTypes(NameKind.exact(elementInfo.getQualifiedName())))) {
                                buildClassInstanceCreation(elementInfo, fileScope, cachedOccurences);
                            }
                        }
                    }
                    break;
                case INCLUDE:
                    buildIncludes(elementInfo, fileScope, cachedOccurences);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        //return retval;
    }

    private void buildStaticFields(final Index index, FileScopeImpl fileScope, final List<Occurence> occurences) {
        final Exact fieldName = NameKind.exact(elementInfo.getName());
        QualifiedName clzName = elementInfo.getTypeQualifiedName();
        final Set<FieldElement> fields = new HashSet<FieldElement>();
        Scope scope = elementInfo.getScope().getInScope();
        if (clzName.getKind().isUnqualified() && scope instanceof TypeScope) {
            if (clzName.getName().equalsIgnoreCase("self")) {
                clzName = ((TypeScope) scope).getFullyQualifiedName();
            } else if (clzName.getName().equalsIgnoreCase("parent") && scope instanceof ClassScope) {
                clzName = ((ClassScope) scope).getSuperClassName();
            }
        }
        if (clzName != null && clzName.toString().length() > 0) {
            for (TypeElement typeElement : index.getTypes(NameKind.exact(clzName))) {
                fields.addAll(ElementFilter.forName(fieldName).filter(index.getAlllFields(typeElement)));
            }
        }
        if (elementInfo.setDeclarations(fields)) {
            occurences.clear();
            buildStaticFieldInvocations(elementInfo, fileScope, occurences);
            buildFieldDeclarations(elementInfo, fileScope, occurences);
            buildDocTagsForFields(elementInfo, fileScope, occurences);
        }
    }

    private void buildFields(final Index index, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<TypeElement> types = new HashSet<TypeElement>();
        final Exact fieldName = NameKind.exact(elementInfo.getName());
        Set<FieldElement> fields = new HashSet<FieldElement>();
        final Scope scope = elementInfo.getScope();
        final ASTNodeInfo nodeInfo = elementInfo.getNodeInfo();
        if (fields.isEmpty()/* && types.isEmpty()*/) {
            String fldName = elementInfo.getName();
            fields = index.getFields(NameKind.exact(fldName.startsWith("$") ? fldName.substring(1) : fldName));
        }

        Occurence.Accuracy accuracy = Accuracy.NO;
        if (fields.size() == 1) {
            accuracy = (elementInfo.setDeclarations(fields)) ? Accuracy.UNIQUE : null;
            elementInfo.setDeclarations(fields);
        } else {
            if (nodeInfo != null) {
                if (scope instanceof VariableScope) {
                    ASTNode originalNode = nodeInfo.getOriginalNode();
                    if (originalNode instanceof VariableBase) {
                        types.addAll(getClassName((VariableScope) scope, (VariableBase) originalNode));
                    }
                }
            }
            if (scope instanceof FieldElementImpl) {
                final Scope inScope = ((FieldElementImpl) scope).getInScope();
                types.add((TypeElement) inScope);
            }

            if (types.size() > 0) {
                if (!fields.isEmpty()) {
                    fields = new HashSet<FieldElement>();
                    for (TypeElement typeElement : types) {
                        fields.addAll(ElementFilter.forName(fieldName).filter(index.getAlllFields(typeElement)));
                    }
                }

                if (fields.isEmpty() && types.size() == 1) {
                    accuracy = (elementInfo.setDeclarations(types)) ? Accuracy.EXACT_TYPE : null;
                    elementInfo.setDeclarations(types);
                } else if (fields.isEmpty() && types.size() > 1) {
                    accuracy = Accuracy.MORE_TYPES;
                    elementInfo.setDeclarations(types);
                } else if (fields.size() == 1) {
                    accuracy = Accuracy.EXACT;
                    elementInfo.setDeclarations(fields);
                } else if (!fields.isEmpty() && !types.isEmpty()) {
                    accuracy = Accuracy.MORE_MEMBERS;
                    elementInfo.setDeclarations(fields);
                }
            } else if (!fields.isEmpty()) {
                accuracy = Accuracy.MORE;
                elementInfo.setDeclarations(fields);
            }
        }
        if (accuracy != null) {
            occurences.clear();
            if (EnumSet.<Occurence.Accuracy>of(Accuracy.EXACT, Accuracy.EXACT_TYPE, Accuracy.UNIQUE, Accuracy.EXACT_TYPE, Accuracy.MORE_MEMBERS).contains(accuracy)) {
                buildFieldInvocations(elementInfo, fileScope, accuracy, occurences);
                buildFieldDeclarations(elementInfo, fileScope, occurences);
                buildDocTagsForFields(elementInfo, fileScope, occurences);
            } else if (!accuracy.equals(Accuracy.NO)) {
                //not compute other occurences
                OccurenceImpl occurence2 = new OccurenceImpl(elementInfo.getDeclarations(), nodeInfo.getRange());
                occurence2.setAccuracy(accuracy);
                occurences.add(occurence2);
            }
        }
    }

    private void buildMethods(final Index index, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<TypeElement> types = new HashSet<TypeElement>();
        final Exact methodName = NameKind.exact(elementInfo.getName());
        Set<MethodElement> methods = new HashSet<MethodElement>();
        final Scope scope = elementInfo.getScope();
        final ASTNodeInfo nodeInfo = elementInfo.getNodeInfo();
        if (methods.isEmpty()/* && types.isEmpty()*/) {
            String mthd = elementInfo.getName();
            methods = index.getMethods(NameKind.exact(mthd));
        }

        Occurence.Accuracy accuracy = Accuracy.NO;
        if (methods.size() == 1) {
            accuracy = (elementInfo.setDeclarations(methods)) ? Accuracy.UNIQUE : null;
            elementInfo.setDeclarations(methods);
        } else {
            if (nodeInfo != null) {
                if (scope instanceof VariableScope) {
                    ASTNode originalNode = nodeInfo.getOriginalNode();
                    if (originalNode instanceof VariableBase) {
                        types.addAll(getClassName((VariableScope) scope, (VariableBase) originalNode));
                    }
                }
            }
            if (scope instanceof MethodScopeImpl) {
                final Scope inScope =  scope.getInScope();
                types.add((TypeElement) inScope);
            }

            if (types.size() > 0) {
                if (!methods.isEmpty()) {
                    methods = new HashSet<MethodElement>();
                    for (TypeElement typeElement : types) {
                        methods.addAll(ElementFilter.forName(methodName).filter(index.getAllMethods(typeElement)));
                    }
                }

                if (methods.isEmpty() && types.size() == 1) {
                    accuracy = (elementInfo.setDeclarations(types)) ? Accuracy.EXACT_TYPE : null;
                    elementInfo.setDeclarations(types);
                } else if (methods.isEmpty() && types.size() > 1) {
                    accuracy = Accuracy.MORE_TYPES;
                    elementInfo.setDeclarations(types);
                } else if (methods.size() == 1) {
                    accuracy = Accuracy.EXACT;
                    elementInfo.setDeclarations(methods);
                } else if (!methods.isEmpty() && !types.isEmpty()) {
                    accuracy = Accuracy.MORE_MEMBERS;
                    elementInfo.setDeclarations(methods);
                }
            } else if (!methods.isEmpty()) {
                accuracy = Accuracy.MORE;
                elementInfo.setDeclarations(methods);
            }
        }
        if (accuracy != null) {
            occurences.clear();
            if (EnumSet.<Occurence.Accuracy>of(Accuracy.EXACT, Accuracy.EXACT_TYPE, Accuracy.UNIQUE, Accuracy.EXACT_TYPE, Accuracy.MORE_MEMBERS).contains(accuracy)) {
                buildMethodInvocations(elementInfo, fileScope, accuracy, cachedOccurences);
                buildMethodDeclarations(elementInfo, fileScope, cachedOccurences);
                buildMagicMethodDeclarations(elementInfo, fileScope, cachedOccurences);
            } else if (!accuracy.equals(Accuracy.NO)) {
                //not compute other occurences
                OccurenceImpl occurence2 = new OccurenceImpl(elementInfo.getDeclarations(), nodeInfo.getRange());
                occurence2.setAccuracy(accuracy);
                occurences.add(occurence2);
            }
        }
    }

    private void buildTypeConstants(final Index index, FileScopeImpl fileScope, final List<Occurence> occurences) {
        final Exact methodName = NameKind.exact(elementInfo.getName());
        QualifiedName clzName = elementInfo.getTypeQualifiedName();
        final Set<TypeConstantElement> constants = new HashSet<TypeConstantElement>();
        Scope scope = elementInfo.getScope() instanceof TypeScope ? elementInfo.getScope() : elementInfo.getScope().getInScope();
        if (clzName.getKind().isUnqualified() && scope instanceof TypeScope) {
            if (clzName.getName().equalsIgnoreCase("self")) {//NOI18N
                clzName = ((TypeScope) scope).getFullyQualifiedName();
            } else if (clzName.getName().equalsIgnoreCase("parent") && scope instanceof ClassScope) {//NOI18N
                clzName = ((ClassScope) scope).getSuperClassName();
            }
        }
        for (TypeElement typeElement : index.getTypes(NameKind.exact(clzName))) {
            constants.addAll(ElementFilter.forName(methodName).filter(index.getAllTypeConstants(typeElement)));
        }
        if (elementInfo.setDeclarations(constants)) {
            buildStaticConstantInvocations(elementInfo, fileScope, cachedOccurences);
            buildStaticConstantDeclarations(elementInfo, fileScope, cachedOccurences);
        }
    }

    private void buildStaticMethods(final Index index, FileScopeImpl fileScope, final List<Occurence> occurences) {
        final Exact methodName = NameKind.exact(elementInfo.getName());
        QualifiedName clzName = elementInfo.getTypeQualifiedName();
        final Set<MethodElement> methods = new HashSet<MethodElement>();
        Scope scope = elementInfo.getScope().getInScope();
        if (clzName.getKind().isUnqualified() && scope instanceof TypeScope) {
            if (clzName.getName().equalsIgnoreCase("self")) {//NOI18N
                clzName = ((TypeScope) scope).getFullyQualifiedName();
            } else if (clzName.getName().equalsIgnoreCase("parent") && scope instanceof ClassScope) {//NOI18N
                clzName = ((ClassScope) scope).getSuperClassName();
            }
        }
        for (TypeElement typeElement : index.getTypes(NameKind.exact(clzName))) {
            methods.addAll(ElementFilter.forName(methodName).filter(index.getAllMethods(typeElement)));
        }
        if (elementInfo.setDeclarations(methods)) {
            occurences.clear();
            buildStaticMethodInvocations(elementInfo, fileScope, occurences);
            buildMethodDeclarations(elementInfo, fileScope, occurences);
        }
    }

    private void buildMagicMethodDeclarations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        for (Entry<MagicMethodDeclarationInfo, MethodScope> entry : magicMethodDeclarations.entrySet()) {
            MagicMethodDeclarationInfo nodeInfo = entry.getKey();
            if (isNameEquality(nodeCtxInfo, nodeInfo, entry.getValue())) {
                occurences.add(new OccurenceImpl(entry.getValue(), nodeInfo.getRange()));
            }
        }
    }

    private void buildMethodInvocations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, Occurence.Accuracy accuracy, final List<Occurence> occurences) {
        final Set<? extends PhpElement> declarations = nodeCtxInfo.getDeclarations();
        Map<QualifiedName, TypeElement> notMatchingTypeNames = new HashMap<QualifiedName, TypeElement>();
        Map<QualifiedName, TypeElement> matchingTypeNames = new HashMap<QualifiedName, TypeElement>();
        for (PhpElement phpElement : declarations) {
            if (phpElement instanceof MethodElement) {
                final TypeElement type = ((MethodElement) phpElement).getType();
                matchingTypeNames.put(type.getFullyQualifiedName(), type);
            } else if (phpElement instanceof TypeElement) {
                final TypeElement type = (TypeElement) phpElement;
                matchingTypeNames.put(type.getFullyQualifiedName(), type);
            }
        }
        if (matchingTypeNames.size() > 0) {
            final Exact name = NameKind.exact(nodeCtxInfo.getQualifiedName());
            final ElementFilter nameFilter = ElementFilter.forName(name);
            for (Entry<ASTNodeInfo<MethodInvocation>, Scope> entry : methodInvocations.entrySet()) {
                ASTNodeInfo<MethodInvocation> nodeInfo = entry.getKey();
                if (name.matchesName(PhpElementKind.METHOD, nodeInfo.getQualifiedName())) {
                    final HashSet<TypeScope> types = new HashSet<TypeScope>(getClassName((VariableScope) entry.getValue(), nodeInfo.getOriginalNode()));
                    if (!createTypeFilter(matchingTypeNames.values(), false).filter(types).isEmpty()) {
                        final OccurenceImpl occurence = new OccurenceImpl(declarations, nodeInfo.getRange());
                        occurence.setAccuracy(accuracy);
                        occurences.add(occurence);
                    } else {
                        final IndexScope indexScope = ModelUtils.getIndexScope(fileScope);
                        final Index index = indexScope.getIndex();
                        for (TypeScope typeScope : types) {
                            if (createTypeFilter(notMatchingTypeNames.values(), false).filter(typeScope).isEmpty()) {
                                final ElementFilter typeFilter = createTypeFilter(matchingTypeNames.values(), true);
                                final Set<MethodElement> methods = typeFilter.filter(
                                        nameFilter.filter(index.getAllMethods(typeScope)));
                                if (!methods.isEmpty()) {
                                    matchingTypeNames.put(typeScope.getFullyQualifiedName(), typeScope);
                                    final OccurenceImpl occurence = new OccurenceImpl(declarations, nodeInfo.getRange());
                                    occurence.setAccuracy(accuracy);
                                    occurences.add(occurence);
                                    break;
                                } else {
                                    notMatchingTypeNames.put(typeScope.getFullyQualifiedName(), typeScope);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildIncludes(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        String idName = nodeCtxInfo.getName();
        for (Entry<IncludeInfo, IncludeElement> entry : includes.entrySet()) {
            IncludeInfo nodeInfo = entry.getKey();
            if (idName.equalsIgnoreCase(nodeInfo.getName())) {
                occurences.add(new OccurenceImpl(entry.getValue(), nodeInfo.getRange()));
            }
        }
    }


    private void buildConstantInvocations(final ElementInfo nodeCtxInfo, final FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<Scalar>, Scope> entry : constInvocations.entrySet()) {
                ASTNodeInfo<Scalar> nodeInfo = entry.getKey();
                if (nodeInfo.getName().length() > 0 && NameKind.exact(nodeInfo.getName()).matchesName(phpElement)) {
                    occurences.add(new OccurenceImpl(ElementFilter.forFiles(fileScope.getFileObject()).prefer(elements), nodeInfo.getRange()));
                }
            }

            for (Entry<ASTNodeInfo<Expression>, Scope> entry : nsConstInvocations.entrySet()) {
                ASTNodeInfo<Expression> nodeInfo = entry.getKey();
                Expression originalNode = nodeInfo.getOriginalNode();
                if (originalNode instanceof NamespaceName) {
                    NamespaceName namespaceName = (NamespaceName) originalNode;
                    final QualifiedName qualifiedName = QualifiedName.create(namespaceName);
                    if (NameKind.exact(qualifiedName).matchesName(phpElement)) {
                        if (qualifiedName.getKind().isUnqualified()) {
                            occurences.add(new OccurenceImpl(ElementFilter.forFiles(fileScope.getFileObject()).prefer(elements), nodeInfo.getRange()));
                        } else {
                            occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                        }
                    }
                }
            }

        }


    }

    private void buildConstantDeclarations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        String idName = nodeCtxInfo.getName();
        for (Entry<ASTNodeInfo<Scalar>, ConstantElement> entry : constDeclarations.entrySet()) {
            ASTNodeInfo<Scalar> nodeInfo = entry.getKey();
            if (idName.equalsIgnoreCase(nodeInfo.getName())) {
                occurences.add(new OccurenceImpl(entry.getValue(), nodeInfo.getRange()));
            }
        }
        for (Entry<ConstantDeclarationInfo, ConstantElement> entry : constDeclarations53.entrySet()) {
            ClassConstantDeclarationInfo nodeInfo = entry.getKey();
            if (idName.equalsIgnoreCase(nodeInfo.getName())) {
                occurences.add(new OccurenceImpl(entry.getValue(), nodeInfo.getRange()));
            }
        }
    }

    private void buildStaticConstantDeclarations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            if (phpElement instanceof TypeConstantElement) {
                TypeConstantElement constantElement = (TypeConstantElement) phpElement;
                TypeElement typeElement = constantElement.getType();
                Exact typeName = NameKind.exact(typeElement.getFullyQualifiedName());
                Exact constName = NameKind.exact(constantElement.getName());
                for (Entry<ASTNodeInfo<Identifier>, ClassConstantElement> entry : classConstantDeclarations.entrySet()) {
                    ASTNodeInfo<Identifier> nodeInfo = entry.getKey();
                    TypeScope typeScope = (TypeScope) entry.getValue().getInScope();
                    if (typeName.matchesName(typeScope)) {
                        if (constName.matchesName(PhpElementKind.TYPE_CONSTANT, nodeInfo.getName())) {
                            occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                        }
                    }
                }
            }
        }

    }

    private static ElementFilter createTypeFilter(Collection<TypeElement> types, boolean forTypeMembers) {
        List<ElementFilter> typeFilters = new ArrayList<ElementFilter>();
        for (TypeElement typeElement : types) {
            typeFilters.add(forTypeMembers ? ElementFilter.forMembersOfType(typeElement) : ElementFilter.forEqualTypes(typeElement));
        }
        return ElementFilter.anyOf(typeFilters);
    }

    private void buildFieldInvocations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, Occurence.Accuracy accuracy, final List<Occurence> occurences) {
        final Set<? extends PhpElement> declarations = nodeCtxInfo.getDeclarations();
        Map<QualifiedName, TypeElement> notMatchingTypeNames = new HashMap<QualifiedName, TypeElement>();
        Map<QualifiedName, TypeElement> matchingTypeNames = new HashMap<QualifiedName, TypeElement>();
        for (PhpElement phpElement : declarations) {
            if (phpElement instanceof FieldElement) {
                final TypeElement type = ((FieldElement) phpElement).getType();
                matchingTypeNames.put(type.getFullyQualifiedName(), type);
            } else if (phpElement instanceof TypeElement) {
                final TypeElement type = (TypeElement) phpElement;
                matchingTypeNames.put(type.getFullyQualifiedName(), type);
            }
        }
        if (matchingTypeNames.size() > 0) {
            final Exact name = NameKind.exact(nodeCtxInfo.getQualifiedName());
            final ElementFilter nameFilter = ElementFilter.forName(name);
            for (Entry<ASTNodeInfo<FieldAccess>, Scope> entry : fieldInvocations.entrySet()) {
                ASTNodeInfo<FieldAccess> nodeInfo = entry.getKey();
                if (name.matchesName(PhpElementKind.FIELD, nodeInfo.getName())) {
                    final HashSet<TypeScope> types = new HashSet<TypeScope>(getClassName((VariableScope) entry.getValue(), nodeInfo.getOriginalNode()));
                    if (!createTypeFilter(matchingTypeNames.values(), false).filter(types).isEmpty()) {
                        final OccurenceImpl occurence = new OccurenceImpl(declarations, nodeInfo.getRange());
                        occurence.setAccuracy(accuracy);
                        occurences.add(occurence);
                    } else {
                        final IndexScope indexScope = ModelUtils.getIndexScope(fileScope);
                        final Index index = indexScope.getIndex();
                        for (TypeScope typeScope : types) {
                            if (createTypeFilter(notMatchingTypeNames.values(), false).filter(typeScope).isEmpty()) {
                                final ElementFilter typeFilter = createTypeFilter(matchingTypeNames.values(), true);
                                final Set<FieldElement> fields = typeFilter.filter(
                                        nameFilter.filter(index.getAlllFields(typeScope)));
                                if (!fields.isEmpty()) {
                                    matchingTypeNames.put(typeScope.getFullyQualifiedName(), typeScope);
                                    final OccurenceImpl occurence = new OccurenceImpl(declarations, nodeInfo.getRange());
                                    occurence.setAccuracy(accuracy);
                                    occurences.add(occurence);
                                    break;
                                } else {
                                    notMatchingTypeNames.put(typeScope.getFullyQualifiedName(), typeScope);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildMethodDeclarations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            if (phpElement instanceof MethodElement) {
                MethodElement method = (MethodElement) phpElement;
                TypeElement typeElement = method.getType();
                Exact typeName = NameKind.exact(typeElement.getFullyQualifiedName());
                Exact methodName = NameKind.exact(method.getName());
                for (Entry<ASTNodeInfo<MethodDeclaration>, MethodScope> entry : methodDeclarations.entrySet()) {
                    ASTNodeInfo<MethodDeclaration> nodeInfo = entry.getKey();
                    TypeScope typeScope = (TypeScope) entry.getValue().getInScope();
                    if (typeName.matchesName(typeScope)) {
                        if (methodName.matchesName(PhpElementKind.METHOD, nodeInfo.getName())) {
                            occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                        }
                    }
                }
            }
        }
    }

    private void buildStaticFieldInvocations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Collection<QualifiedName> matchingTypeNames = new HashSet<QualifiedName>();
        Collection<QualifiedName> notMatchingTypeNames = new HashSet<QualifiedName>();
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();

        for (PhpElement phpElement : elements) {
            if (phpElement instanceof FieldElement) {
                FieldElement fieldElement = (FieldElement) phpElement;
                matchingTypeNames.add(fieldElement.getType().getFullyQualifiedName());
                matchingTypeNames.add(nodeCtxInfo.getTypeQualifiedName());
                Exact fieldName = NameKind.exact(phpElement.getName());
                for (Entry<ASTNodeInfo<StaticFieldAccess>, Scope> entry : staticFieldInvocations.entrySet()) {
                    ASTNodeInfo<StaticFieldAccess> nodeInfo = entry.getKey();
                    QualifiedName clzName = QualifiedName.create(nodeInfo.getOriginalNode().getClassName());
                    final Scope scope = entry.getValue().getInScope();
                    if (clzName.getKind().isUnqualified() && scope instanceof TypeScope) {
                        if (clzName.getName().equalsIgnoreCase("self")) {
                            clzName = ((TypeScope) scope).getFullyQualifiedName();
                        } else if (clzName.getName().equalsIgnoreCase("parent") && scope instanceof ClassScope) {
                            clzName = ((ClassScope) scope).getSuperClassName();
                        }
                    }
                    if (clzName != null) {
                        if (fieldName.matchesName(PhpElementKind.FIELD, nodeInfo.getName())) {
                            final Exact typeName = NameKind.exact(clzName);
                            boolean isTheSame = false;
                            //matches with other matching names
                            for (QualifiedName matchingName : matchingTypeNames) {
                                if (typeName.matchesName(PhpElementKind.CLASS, matchingName)) {
                                    isTheSame = true;
                                    break;
                                }
                            }
                            //if not then query to index
                            if (!isTheSame) {
                                boolean skipIt = false;
                                for (QualifiedName notMatchingName : notMatchingTypeNames) {
                                    if (typeName.matchesName(PhpElementKind.CLASS, notMatchingName)) {
                                        skipIt = true;
                                        break;
                                    }
                                }
                                if (skipIt) {
                                    continue;
                                }
                                final IndexScope indexScope = ModelUtils.getIndexScope(fileScope);
                                final Index index = indexScope.getIndex();
                                final ElementFilter forTheSameType = ElementFilter.forMembersOfType(fieldElement.getType());
                                final Set<FieldElement> expectedFields = forTheSameType.filter(index.getAlllFields(NameKind.exact(clzName), fieldName));
                                isTheSame = !expectedFields.isEmpty();
                            }
                            if (isTheSame) {
                                //add into matching names
                                matchingTypeNames.add(clzName);
                                occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                            } else {
                                notMatchingTypeNames.add(clzName);
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildStaticMethodInvocations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Collection<QualifiedName> matchingTypeNames = new HashSet<QualifiedName>();
        Collection<QualifiedName> notMatchingTypeNames = new HashSet<QualifiedName>();
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            if (phpElement instanceof MethodElement) {
                MethodElement methodElement = (MethodElement) phpElement;
                matchingTypeNames.add(methodElement.getType().getFullyQualifiedName());
                matchingTypeNames.add(nodeCtxInfo.getTypeQualifiedName());
                Exact methodName = NameKind.exact(phpElement.getName());
                for (Entry<ASTNodeInfo<StaticMethodInvocation>, Scope> entry : staticMethodInvocations.entrySet()) {
                    ASTNodeInfo<StaticMethodInvocation> nodeInfo = entry.getKey();
                    QualifiedName clzName = QualifiedName.create(nodeInfo.getOriginalNode().getClassName());
                    final Scope scope = entry.getValue().getInScope();
                    if (clzName.getKind().isUnqualified() && scope instanceof TypeScope) {
                        if (clzName.getName().equalsIgnoreCase("self")) {
                            clzName = ((TypeScope) scope).getFullyQualifiedName();
                        } else if (clzName.getName().equalsIgnoreCase("parent") && scope instanceof ClassScope) {
                            clzName = ((ClassScope) scope).getSuperClassName();
                        }
                    }
                    if (clzName != null) {
                        if (methodName.matchesName(PhpElementKind.METHOD, nodeInfo.getName())) {
                            final Exact typeName = NameKind.exact(clzName);
                            boolean isTheSame = false;
                            //matches with other matching names
                            for (QualifiedName matchingName : matchingTypeNames) {
                                if (typeName.matchesName(PhpElementKind.CLASS, matchingName)) {
                                    isTheSame = true;
                                    break;
                                }
                            }
                            //if not then query to index
                            if (!isTheSame) {
                                boolean skipIt = false;
                                for (QualifiedName notMatchingName : notMatchingTypeNames) {
                                    if (typeName.matchesName(PhpElementKind.CLASS, notMatchingName)) {
                                        skipIt = true;
                                        break;
                                    }
                                }
                                if (skipIt) {
                                    continue;
                                }
                                final IndexScope indexScope = ModelUtils.getIndexScope(fileScope);
                                final Index index = indexScope.getIndex();
                                final ElementFilter forTheSameType = ElementFilter.forMembersOfType(methodElement.getType());
                                final Set<MethodElement> expectedMethods = forTheSameType.filter(index.getAllMethods(NameKind.exact(clzName), methodName));
                                isTheSame = !expectedMethods.isEmpty();
                            }
                            if (isTheSame) {
                                //add into matching names
                                matchingTypeNames.add(clzName);
                                occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                            } else {
                                notMatchingTypeNames.add(clzName);
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildStaticConstantInvocations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Collection<QualifiedName> matchingTypeNames = new HashSet<QualifiedName>();
        Collection<QualifiedName> notMatchingTypeNames = new HashSet<QualifiedName>();
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            if (phpElement instanceof TypeConstantElement) {
                TypeConstantElement constantElement = (TypeConstantElement) phpElement;
                matchingTypeNames.add(constantElement.getType().getFullyQualifiedName());
                matchingTypeNames.add(nodeCtxInfo.getTypeQualifiedName());
                Exact constantName = NameKind.exact(phpElement.getName());
                for (Entry<ASTNodeInfo<StaticConstantAccess>, Scope> entry : staticConstantInvocations.entrySet()) {
                    ASTNodeInfo<StaticConstantAccess> nodeInfo = entry.getKey();
                    QualifiedName clzName = QualifiedName.create(nodeInfo.getOriginalNode().getClassName());
                    final Scope scope = entry.getValue() instanceof TypeScope ? entry.getValue() : entry.getValue().getInScope();
                    if (clzName.getKind().isUnqualified() && scope instanceof TypeScope) {
                        if (clzName.getName().equalsIgnoreCase("self")) {
                            clzName = ((TypeScope) scope).getFullyQualifiedName();
                        } else if (clzName.getName().equalsIgnoreCase("parent") && scope instanceof ClassScope) {
                            clzName = ((ClassScope) scope).getSuperClassName();
                        }
                    }
                    if (clzName != null) {
                        if (constantName.matchesName(PhpElementKind.TYPE_CONSTANT, nodeInfo.getName())) {
                            final Exact typeName = NameKind.exact(clzName);
                            boolean isTheSame = false;
                            //matches with other matching names
                            for (QualifiedName matchingName : matchingTypeNames) {
                                if (typeName.matchesName(PhpElementKind.CLASS, matchingName)) {
                                    isTheSame = true;
                                    break;
                                }
                            }
                            //if not then query to index
                            if (!isTheSame) {
                                boolean skipIt = false;
                                for (QualifiedName notMatchingName : notMatchingTypeNames) {
                                    if (typeName.matchesName(PhpElementKind.CLASS, notMatchingName)) {
                                        skipIt = true;
                                        break;
                                    }
                                }
                                if (skipIt) {
                                    continue;
                                }
                                final IndexScope indexScope = ModelUtils.getIndexScope(fileScope);
                                final Index index = indexScope.getIndex();
                                final ElementFilter forTheSameType = ElementFilter.forMembersOfType(constantElement.getType());
                                final Set<TypeConstantElement> expectedConstants = forTheSameType.filter(index.getAllTypeConstants(NameKind.exact(clzName), constantName));
                                isTheSame = !expectedConstants.isEmpty();
                            }
                            if (isTheSame) {
                                //add into matching names
                                matchingTypeNames.add(clzName);
                                occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                            } else {
                                notMatchingTypeNames.add(clzName);
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildDocTagsForClasses(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<PhpDocTypeTagInfo, Scope> entry : docTags.entrySet()) {
                PhpDocTypeTagInfo nodeInfo = entry.getKey();
                final QualifiedName qualifiedName = nodeInfo.getQualifiedName();
                final String name = nodeInfo.getName();
                if (!name.isEmpty() && NameKind.exact(name).matchesName(PhpElementKind.CLASS, phpElement.getName()) &&
                        NameKind.exact(qualifiedName).matchesName(phpElement)) {
                    if (fileScope.getFileObject() == phpElement.getFileObject()) {
                        if (qualifiedName.getKind().isUnqualified()) {
                            occurences.add(new OccurenceImpl(ElementFilter.forFiles(fileScope.getFileObject()).prefer(elements), nodeInfo.getRange()));
                        } else {
                            occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                        }
                    }
                }
            }
        }
    }

    private void buildClassInstanceCreation(ElementInfo query, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = query.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<ClassInstanceCreation>, Scope> entry : clasInstanceCreations.entrySet()) {
                ASTNodeInfo<ClassInstanceCreation> nodeInfo = entry.getKey();
                final QualifiedName qualifiedName = nodeInfo.getQualifiedName();
                Set<? extends PhpElement> contextTypes = elements;
                if (NameKind.exact(qualifiedName).matchesName(phpElement)) {
                    if (qualifiedName.getKind().isUnqualified()) {
                        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(fileScope, nodeInfo.getRange().getStart());
                        if (namespaceScope != null) {
                            Set<QualifiedName> allNames = new HashSet<QualifiedName>();
                            for (QualifiedName qn : QualifiedName.getComposedNames(qualifiedName, namespaceScope)) {
                                if (!qn.getKind().isUnqualified() && !qn.isDefaultNamespace()) {
                                    allNames.add(qn.toNamespaceName());
                                }
                            }
                            ElementFilter forTypesFromNamespace = ElementFilter.forTypesFromNamespaces(allNames);
                            contextTypes = forTypesFromNamespace.filter(elements);
                            if (contextTypes.isEmpty()) {
                                contextTypes = elements;
                            } else if (!contextTypes.contains(phpElement)) {
                                continue;
                            }
                        }
                    }

                    if (!qualifiedName.getKind().isUnqualified()) {
                        contextTypes = Collections.singleton(phpElement);
                    }
                    final OccurenceImpl occurenceImpl = new OccurenceImpl(
                            ElementFilter.forFiles(fileScope.getFileObject()).prefer(contextTypes), nodeInfo.getRange()) {

                        @Override
                        public Collection<? extends PhpElement> gotoDeclarations() {
                            ElementQuery elementQuery = declaration.getElementQuery();
                            if (declaration instanceof TypeElement && elementQuery != null && elementQuery.getQueryScope().isIndexScope()) {
                                ElementQuery.Index index = (ElementQuery.Index) elementQuery;
                                Set<MethodElement> declaredMethods = 
                                        ElementFilter.forName(NameKind.exact(MethodElement.CONSTRUCTOR_NAME)).filter(index.getDeclaredMethods((TypeElement) declaration));
                                if (!declaredMethods.isEmpty()) {
                                    return declaredMethods;
                                }
                            }

                            return super.gotoDeclarations();
                        }
                    };
                    occurences.add(occurenceImpl);

                }
            }
        }
    }

    private void buildClassNames(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<ClassName>, Scope> entry : clasNames.entrySet()) {
                ASTNodeInfo<ClassName> nodeInfo = entry.getKey();
                final QualifiedName qualifiedName = nodeInfo.getQualifiedName();
                if (NameKind.exact(qualifiedName).matchesName(phpElement)) {
                    if (qualifiedName.getKind().isUnqualified()) {
                        occurences.add(new OccurenceImpl(elements, nodeInfo.getRange()));
                    } else {
                        occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                    }
                }
            }
        }
    }

    private void buildInterfaceIDs(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<Expression>, Scope> entry : ifaceIDs.entrySet()) {
                ASTNodeInfo<Expression> nodeInfo = entry.getKey();
                Set<? extends PhpElement> contextTypes = elements;
                final QualifiedName qualifiedName = nodeInfo.getQualifiedName();
                if (NameKind.exact(qualifiedName).matchesName(phpElement)) {
                    if (qualifiedName.getKind().isUnqualified()) {
                        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(fileScope, nodeInfo.getRange().getStart());
                        if (namespaceScope != null) {
                            Set<QualifiedName> allNames = new HashSet<QualifiedName>();
                            for (QualifiedName qn : QualifiedName.getComposedNames(qualifiedName, namespaceScope)) {
                                if (!qn.getKind().isUnqualified() && !qn.isDefaultNamespace()) {
                                    allNames.add(qn.toNamespaceName());
                                }
                            }
                            ElementFilter forTypesFromNamespace = ElementFilter.forTypesFromNamespaces(allNames);
                            contextTypes = forTypesFromNamespace.filter(elements);
                            if (contextTypes.isEmpty()) {
                                contextTypes = elements;
                            } else if (!contextTypes.contains(phpElement)) {
                                continue;
                            }
                        }
                    }

                    if (qualifiedName.getKind().isUnqualified()) {
                        occurences.add(new OccurenceImpl(ElementFilter.forFiles(fileScope.getFileObject()).prefer(contextTypes), nodeInfo.getRange()));
                    } else {
                        occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                    }
                }
            }
        }
    }

    private void buildClassIDs(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<Expression>, Scope> entry : clasIDs.entrySet()) {
                ASTNodeInfo<Expression> nodeInfo = entry.getKey();
                final QualifiedName qualifiedName = nodeInfo.getQualifiedName();
                Set<? extends PhpElement> contextTypes = elements;
                if (NameKind.exact(qualifiedName).matchesName(phpElement)) {
                    if (qualifiedName.getKind().isUnqualified()) {
                        NamespaceScope namespaceScope = ModelUtils.getNamespaceScope(fileScope, nodeInfo.getRange().getStart());
                        if (namespaceScope != null) {
                            Set<QualifiedName> allNames = new HashSet<QualifiedName>();
                            for (QualifiedName qn : QualifiedName.getComposedNames(qualifiedName, namespaceScope)) {
                                if (!qn.getKind().isUnqualified() && !qn.isDefaultNamespace()) {
                                    allNames.add(qn.toNamespaceName());
                                }
                            }
                            ElementFilter forTypesFromNamespace = ElementFilter.forTypesFromNamespaces(allNames);
                            contextTypes = forTypesFromNamespace.filter(elements);
                            if (contextTypes.isEmpty()) {
                                contextTypes = elements;
                            } else if (!contextTypes.contains(phpElement)) {
                                continue;
                            }
                        }
                    }

                    if (qualifiedName.getKind().isUnqualified()) {
                        occurences.add(new OccurenceImpl(ElementFilter.forFiles(fileScope.getFileObject()).prefer(contextTypes),
                                nodeInfo.getRange()));
                    } else {
                        occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                    }
                }
            }
        }
    }

    private void buildInterfaceDeclarations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<InterfaceDeclarationInfo, InterfaceScope> entry : ifaceDeclarations.entrySet()) {
                InterfaceDeclarationInfo nodeInfo = entry.getKey();
                if (NameKind.exact(nodeInfo.getQualifiedName()).matchesName(phpElement) && 
                        nodeInfo.getRange().containsInclusive(phpElement.getOffset())) {
                    if (fileScope.getFileObject() == phpElement.getFileObject()) {
                        occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                    }
                }
            }
        }
    }

    private void buildClassDeclarations(ElementInfo query, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = query.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ClassDeclarationInfo, ClassScope> entry : clasDeclarations.entrySet()) {
                ClassDeclarationInfo nodeInfo = entry.getKey();
                if (NameKind.exact(nodeInfo.getQualifiedName()).matchesName(phpElement) &&
                        nodeInfo.getRange().containsInclusive(phpElement.getOffset())) {
                    if (fileScope.getFileObject() == phpElement.getFileObject()) {
                        occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                    }
                }
            }
        }
    }

    private void buildFunctionDeclarations(final ElementInfo nodeCtxInfo, final FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<FunctionDeclaration>, FunctionScope> entry : fncDeclarations.entrySet()) {
                ASTNodeInfo<FunctionDeclaration> nodeInfo = entry.getKey();
                if (NameKind.exact(nodeInfo.getQualifiedName()).matchesName(phpElement)) {
                    occurences.add(new OccurenceImpl(entry.getValue(), nodeInfo.getRange()));
                }
            }
        }
    }

    private void buildFunctionInvocations(final ElementInfo nodeCtxInfo, final FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            for (Entry<ASTNodeInfo<FunctionInvocation>, Scope> entry : fncInvocations.entrySet()) {
                ASTNodeInfo<FunctionInvocation> nodeInfo = entry.getKey();
                final QualifiedName qualifiedName = nodeInfo.getQualifiedName();
                if (NameKind.exact(qualifiedName).matchesName(phpElement)) {
                    if (qualifiedName.getKind().isUnqualified()) {
                        occurences.add(new OccurenceImpl(ElementFilter.forFiles(fileScope.getFileObject()).prefer(elements), nodeInfo.getRange()));
                    } else {
                        occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                    }
                }
            }
        }
    }


    private void buildFieldDeclarations(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            if (phpElement instanceof FieldElement) {
                FieldElement field = (FieldElement) phpElement;
                TypeElement typeElement = field.getType();
                Exact typeName = NameKind.exact(typeElement.getFullyQualifiedName());
                Exact fieldName = NameKind.exact(field.getName());
                for (Entry<SingleFieldDeclarationInfo, FieldElementImpl> entry : fldDeclarations.entrySet()) {
                    SingleFieldDeclarationInfo nodeInfo = entry.getKey();
                    TypeScope typeScope = (TypeScope) entry.getValue().getInScope();
                    if (typeName.matchesName(typeScope)) {
                        if (fieldName.matchesName(PhpElementKind.FIELD, nodeInfo.getName())) {
                            occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                        }
                    }
                }
            }
        }
    }

    private void buildDocTagsForFields(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        Collection<QualifiedName> matchingTypeNames = new HashSet<QualifiedName>();
        Set<? extends PhpElement> elements = nodeCtxInfo.getDeclarations();
        for (PhpElement phpElement : elements) {
            if (phpElement instanceof FieldElement) {
                FieldElement fieldElement = (FieldElement) phpElement;
                TypeElement typeElement = fieldElement.getType();
                Exact typeName = NameKind.exact(typeElement.getFullyQualifiedName());
                matchingTypeNames.add(typeElement.getFullyQualifiedName());
                matchingTypeNames.add(nodeCtxInfo.getTypeQualifiedName());
                Exact fieldName = NameKind.exact(phpElement.getName());
                for (Entry<PhpDocTypeTagInfo, Scope> entry : docTags.entrySet()) {
                    PhpDocTypeTagInfo nodeInfo = entry.getKey();
                    Scope scope = entry.getValue();
                    if (Kind.FIELD.equals(nodeInfo.getKind()) && scope instanceof ClassScope) {
                        if (typeName.matchesName(((ClassScope) scope))) {
                            if (fieldName.matchesName(PhpElementKind.FIELD, nodeInfo.getName())) {
                                occurences.add(new OccurenceImpl(phpElement, nodeInfo.getRange()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildGotoStatements(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        buildGoto(nodeCtxInfo, gotoStatement, fileScope, occurences);
    }

    private void buildGotoLabels(final ElementInfo nodeCtxInfo, final FileScopeImpl fileScope, final List<Occurence> occurences) {
        buildGoto(nodeCtxInfo, gotoLabel, fileScope, occurences);
    }

    private <T extends ASTNode> void buildGoto(final ElementInfo nodeCtxInfo, final Map<ASTNodeInfo<T>, Scope> entries, FileScopeImpl fileScope, final List<Occurence> occurences) {
        String currentName = nodeCtxInfo.getName();
        Scope currentScope = nodeCtxInfo.getScope();
        for (Entry<ASTNodeInfo<T>, Scope> entry : entries.entrySet()) {
            ASTNodeInfo<T> nodeInfo = entry.getKey();
            String name = nodeInfo.getName();
            Scope scope = entry.getValue();
            if (currentName.equalsIgnoreCase(name) && currentScope == scope) {
                occurences.add(new OccurenceImpl(entry.getValue(), nodeInfo.getRange()) {
                    @Override
                    public Collection<? extends PhpElement> gotoDeclarations() {
                        return Collections.emptyList();
                    }
                });
            }
        }
    }

    private void buildDocTagsForVars(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        final Scope ctxScope = nodeCtxInfo.getScope() instanceof VariableName ? nodeCtxInfo.getScope().getInScope() : nodeCtxInfo.getScope();
        if (!(ctxScope instanceof VariableScope)) {
            return;
        }
        final VariableScope ctxVarScope = (VariableScope) ctxScope;
        final ElementFilter nameFilter = ElementFilter.forName(NameKind.exact(nodeCtxInfo.getName()));
        final Set<VariableName> vars = ctxVarScope != null ? nameFilter.filter(new HashSet<VariableName>(ctxVarScope.getDeclaredVariables()))
                : Collections.<VariableName>emptySet();
        final VariableName var = (vars.size() == 1) ? vars.iterator().next() : null;
        for (Entry<PhpDocTypeTagInfo, Scope> entry : docTags.entrySet()) {
            PhpDocTypeTagInfo nodeInfo = entry.getKey();
            Scope scope = entry.getValue();
            if (Kind.VARIABLE.equals(nodeInfo.getKind()) && scope instanceof VariableScope
                    && NameKind.exact(nodeInfo.getName()).matchesName(PhpElementKind.VARIABLE, nodeCtxInfo.getName())) {
                if (!var.isGloballyVisible()) {
                    Scope nextScope = entry.getValue();
                    if (ctxVarScope.equals(nextScope)) {
                        occurences.add(new OccurenceImpl(var, nodeInfo.getRange()));
                    }
                } else {
                    Scope nextScope = entry.getValue();
                    if (nextScope instanceof VariableScope) {
                        final Set<VariableName> nextVars = nameFilter.filter(new HashSet<VariableName>(((VariableScope) nextScope).getDeclaredVariables()));
                        final VariableName nextVar = (nextVars.size() == 1) ? nextVars.iterator().next() : null;
                        if (nextVar != null && nextVar.isGloballyVisible()) {
                            occurences.add(new OccurenceImpl(var, nodeInfo.getRange()));
                        }
                    }
                }
            }
        }
    }

    private void buildVariables(ElementInfo nodeCtxInfo, FileScopeImpl fileScope, final List<Occurence> occurences) {
        final Scope ctxScope = nodeCtxInfo.getScope() instanceof VariableName ? nodeCtxInfo.getScope().getInScope() : nodeCtxInfo.getScope();
        if (!(ctxScope instanceof VariableScope)) {
            return;
        }
        final VariableScope ctxVarScope = (VariableScope) ctxScope;
        final ElementFilter nameFilter = ElementFilter.forName(NameKind.exact(nodeCtxInfo.getName()));
        final Set<VariableName> vars = ctxVarScope != null ? nameFilter.filter(new HashSet<VariableName>(ctxVarScope.getDeclaredVariables())) :
            Collections.<VariableName>emptySet();
        final VariableName var =  (vars.size() == 1) ? vars.iterator().next() : null;
        if (var != null) {
            for (Entry<ASTNodeInfo<Variable>, Scope> entry : variables.entrySet()) {
                ASTNodeInfo<Variable> nodeInfo = entry.getKey();
                if (NameKind.exact(nodeInfo.getName()).matchesName(PhpElementKind.VARIABLE, nodeCtxInfo.getName())) {
                    if (!var.isGloballyVisible()) {
                        Scope nextScope = entry.getValue();
                        if (var.representsThis() && nextScope.getInScope() instanceof TypeScope) {
                            final Scope inScope = ctxVarScope instanceof MethodScope ? ctxVarScope.getInScope() : ctxVarScope;
                            if (nextScope.getInScope().equals(inScope)) {
                                occurences.add(new OccurenceImpl(var, nodeInfo.getRange()));
                            }
                        } else {
                            if (ctxVarScope.equals(nextScope)) {
                                occurences.add(new OccurenceImpl(var, nodeInfo.getRange()));
                            }
                        }
                    } else {
                        Scope nextScope = entry.getValue();
                        if (nextScope instanceof VariableScope) {
                            final Set<VariableName> nextVars = nameFilter.filter(new HashSet<VariableName>(((VariableScope) nextScope).getDeclaredVariables()));
                            final VariableName nextVar =  (nextVars.size() == 1) ? nextVars.iterator().next() : null;
                            if (nextVar != null && nextVar.isGloballyVisible()) {
                                occurences.add(new OccurenceImpl(var, nodeInfo.getRange()));
                            }
                        }
                    }
                }
            }
        }
    }

    Occurence build(FileScopeImpl fileScope, final int offset) {
        Occurence retval = findOccurenceByOffset(offset);
        if (retval == null && setElementInfo(offset)) {
            build(fileScope);
            retval = findOccurenceByOffset(offset);
        } 
        return retval;
    }

    List<Occurence> build(FileScopeImpl fileScope, final ModelElement element) {
        if (setElementInfo(element)) {
            build(fileScope);
        }
        return cachedOccurences;
    }

    private Occurence findOccurenceByOffset(final int offset) {
        Occurence retval = null;
        for (Occurence occ : cachedOccurences) {
            assert occ != null;
            if (occ.getOccurenceRange().containsInclusive(offset)) {
                retval = occ;
            }
        }
        return retval;
    }

    private boolean canBePrepared(ASTNode node, ModelElement scope) {
        return scope != null && node != null;
    }

    private void setOffsetElementInfo(ElementInfo nextElementInfo, final int offset) {
        if (nextElementInfo != null && offset >= 0) {
            if (nextElementInfo.getName() != null && nextElementInfo.getName().trim().length() > 0) {
                OffsetRange range = nextElementInfo.getRange();
                if (range.containsInclusive(offset)) {
                    elementInfo = nextElementInfo;
                }
            }
        }
    }

    private static Collection<? extends TypeScope> getClassName(VariableScope scp, VariableBase varBase) {
        String vartype = VariousUtils.extractTypeFroVariableBase(varBase,
                Collections.<String, AssignmentImpl>emptyMap());
        return VariousUtils.getType(scp, vartype, varBase.getStartOffset(), true);
    }


    private static boolean isNameEquality(ElementInfo query, ASTNodeInfo node, ModelElement nodeScope) {
        String idName = query.getName();
        if (idName.equalsIgnoreCase(node.getName())) {
            QualifiedName queryQN = query.getQualifiedName();
            QualifiedName nodeQN = node.getQualifiedName();
            if (queryQN.equals(nodeQN)) {
                return true;
            }
            final Collection<QualifiedName> queryComposedNames = QualifiedName.getComposedNames(queryQN, query.getNamespaceScope());
            final Collection<QualifiedName> nodeQomposedNames = QualifiedName.getComposedNames(nodeQN, ModelUtils.getNamespaceScope(nodeScope));
            queryComposedNames.retainAll(nodeQomposedNames);
            return !queryComposedNames.isEmpty();
        }
        return false;
    }

    private class OccurenceImpl implements Occurence {

        private final OffsetRange occurenceRange;
        final PhpElement declaration;
        private Collection<? extends PhpElement> allDeclarations;
        private Collection<? extends PhpElement>  gotoDeclarations;
        private Accuracy accuracy = Accuracy.EXACT;

        public OccurenceImpl (Collection<? extends PhpElement> allDeclarations, OffsetRange occurenceRange) {
            this(allDeclarations, ModelUtils.getFirst(allDeclarations), occurenceRange);
        }

        public OccurenceImpl (PhpElement declaration, OffsetRange occurenceRange) {
            this(Collections.<PhpElement>singleton(declaration), occurenceRange);
        }

        private OccurenceImpl (Collection<? extends PhpElement> allDeclarations, PhpElement declaration, OffsetRange occurenceRange) {
            if ((declaration instanceof MethodScope) && ((MethodScope) declaration).isConstructor()) {
                ModelElement modelElement = (ModelElement) declaration;
                this.declaration = modelElement.getInScope();
            } else {
                this.allDeclarations = allDeclarations;
                this.declaration = declaration;
            }
            assert declaration != null;
            this.occurenceRange = occurenceRange;
        }

        @Override
        public PhpElementKind getKind() {
            return declaration.getPhpElementKind();
        }

        @Override
        public OffsetRange getOccurenceRange() {
            return occurenceRange;
        }

        @Override
        public Accuracy degreeOfAccuracy() {
            return accuracy;
        }

        @Override
        public Collection<? extends PhpElement>  gotoDeclarations() {
            return gotoDeclarations == null ? allDeclarations : gotoDeclarations;
        }

        public void setAccuracy(Accuracy accuracy) {
            this.accuracy = accuracy;
        }

        @Override
        public Collection<? extends PhpElement> getAllDeclarations() {
            return this.allDeclarations;
        }

        @Override
        public Collection<Occurence> getAllOccurences() {
            return cachedOccurences;
        }
    }

    private static class ElementInfo {

        private Scope scope;
        private Union2<ASTNodeInfo, ModelElement> element;
        public Set<? extends PhpElement> declarations = Collections.emptySet();

        public ElementInfo(ModelElement element) {
            this.element = Union2.createSecond(element);
            if (element instanceof Scope) {
                this.scope = (Scope) element;
            } else {
                this.scope = element.getInScope();
            }
        }

        public ElementInfo(ASTNodeInfo nodeInfo, ModelElement element) {
            this.element = Union2.createFirst(nodeInfo);
            if (element instanceof Scope) {
                this.scope = (Scope) element;
            } else {
                this.scope = element.getInScope();
            }
        }

        /**
         * @return the scope
         */
        public Scope getScope() {
            return scope;
        }

        public FileScope getFileScope() {
            return ModelUtils.getFileScope(scope);
        }

        public NamespaceScope getNamespaceScope() {
            return ModelUtils.getNamespaceScope(scope);
        }

        public QualifiedName getTypeQualifiedName() {
            ASTNodeInfo nodeInfo = getNodeInfo();
            QualifiedName qualifiedName = null;
            if (nodeInfo != null) {
                ASTNode originalNode = nodeInfo.getOriginalNode();
                if (nodeInfo instanceof ClassConstantDeclarationInfo && originalNode instanceof Identifier) {
                    if (getScope() instanceof TypeScope) {
                        return ((TypeScope) getScope()).getFullyQualifiedName();
                    }
                }
                if (originalNode instanceof StaticDispatch) {
                    qualifiedName = ASTNodeInfo.toQualifiedName(originalNode, true);
                } else {
                    if (getScope().getInScope() instanceof TypeScope) {
                        if (originalNode instanceof MethodDeclaration
                                || originalNode instanceof SingleFieldDeclaration) {
                            return ((TypeScope) getScope().getInScope()).getFullyQualifiedName();
                        }
                    }
                    qualifiedName = nodeInfo.getQualifiedName();
                }
            } else {
                ModelElement modelElemnt = getModelElemnt();
                final QualifiedName namespaceName = modelElemnt.getNamespaceName();
                Scope inScope = modelElemnt.getInScope();
                if (inScope instanceof TypeScope) {
                    qualifiedName = namespaceName.append(inScope.getName());
                } else {
                    qualifiedName = namespaceName.append(modelElemnt.getName());
                }
            }
            return qualifiedName;
        }

        public QualifiedName getQualifiedName() {
            ASTNodeInfo nodeInfo = getNodeInfo();
            QualifiedName qualifiedName = null;
            if (nodeInfo != null) {
                qualifiedName = nodeInfo.getQualifiedName();
            } else {
                ModelElement modelElemnt = getModelElemnt();
                if (modelElemnt instanceof ClassMemberElement) {
                    qualifiedName = QualifiedName.createUnqualifiedName(modelElemnt.getName());
                } else {
                    final QualifiedName namespaceName = modelElemnt.getNamespaceName();
                    qualifiedName = namespaceName.append(modelElemnt.getName());
                }
            }
            return qualifiedName;
        }

        public Collection<QualifiedName> getComposedNames() {
            return QualifiedName.getComposedNames(getQualifiedName(), getNamespaceScope());
        }

        public String getName() {
            ASTNodeInfo nodeInfo = getNodeInfo();
            if (nodeInfo != null) {
                return nodeInfo.getName();
            }
            return getModelElemnt().getName();
        }

        public ASTNodeInfo.Kind getKind() {
            ASTNodeInfo nodeInfo = getNodeInfo();
            if (nodeInfo != null) {
                return nodeInfo.getKind();
            }
            ASTNodeInfo.Kind kind = null;
            ModelElement modelElemnt = getModelElemnt();
            switch (modelElemnt.getPhpElementKind()) {
                case CLASS:
                    kind = Kind.CLASS;
                    break;
                case TYPE_CONSTANT:
                    kind = Kind.CLASS_CONSTANT;
                    break;
                case CONSTANT:
                    kind = Kind.CONSTANT;
                    break;
                case FIELD:
                    kind = modelElemnt.getPhpModifiers().isStatic() ? Kind.STATIC_FIELD : Kind.FIELD;
                    break;
                case FUNCTION:
                    kind = Kind.FUNCTION;
                    break;
                case IFACE:
                    kind = Kind.IFACE;
                    break;
                case INCLUDE:
                    kind = Kind.INCLUDE;
                    break;
                case METHOD:
                    boolean isStatic = modelElemnt.getPhpModifiers().isStatic();
                    kind = isStatic ? Kind.STATIC_METHOD : Kind.METHOD;
                    break;
                case VARIABLE:
                    kind = Kind.VARIABLE;
                    break;
            }
            assert kind != null;
            return kind;
        }

        public OffsetRange getRange() {
            ASTNodeInfo nodeInfo = getNodeInfo();
            if (nodeInfo != null) {
                return nodeInfo.getRange();
            }
            return getModelElemnt().getNameRange();
        }

        public Union2<ASTNodeInfo, ModelElement> getRawElement() {
            return element;
        }

        public ASTNodeInfo getNodeInfo() {
            return element.hasFirst() ? element.first() : null;
        }

        private ModelElement getModelElemnt() {
            return element.hasSecond() ? element.second() : null;
        }

        /**
         * @return the declarations
         */
        public Set<? extends PhpElement> getDeclarations() {
            return declarations;
        }

        /**
         * @param declarations the declarations to set
         */
        public boolean setDeclarations(Set<? extends PhpElement> declarations) {
            this.declarations = declarations;
            return this.declarations != null && !this.declarations.isEmpty();
        }
    }
}
