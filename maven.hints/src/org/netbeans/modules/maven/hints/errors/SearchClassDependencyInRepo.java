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
 * Contributor(s): theanuradha@netbeans.org
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.hints.errors;

import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.Name;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spi.ErrorRule.Data;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import static org.netbeans.modules.maven.hints.errors.Bundle.*;
import org.netbeans.modules.maven.hints.ui.SearchDependencyUI;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.indexer.api.RepositoryQueries;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.EnhancedFix;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Anuradha G
 */
public class SearchClassDependencyInRepo implements ErrorRule<Void> {

    private final AtomicBoolean cancel = new AtomicBoolean(false);

    public SearchClassDependencyInRepo() {
    }

    @Override
    public Set<String> getCodes() {
        return new HashSet<String>(Arrays.asList(
                "compiler.err.cant.resolve",//NOI18N
                "compiler.err.cant.resolve.location",//NOI18N
                "compiler.err.doesnt.exist",//NOI18N
                "compiler.err.not.stmt"));//NOI18N

    }

    @Override
    public List<Fix> run(final CompilationInfo info, String diagnosticKey,
            final int offset, TreePath treePath, Data<Void> data) {
        cancel.set(false);
        if (!SearchClassDependencyHint.isHintEnabled()) {
            return Collections.emptyList();
        }
        //copyed from ImportClass
        int errorPosition = offset + 1; //TODO: +1 required to work OK, rethink

        if (errorPosition == (-1)) {

            return Collections.<Fix>emptyList();
        }
        //copyed from ImportClass-end
        FileObject fileObject = info.getFileObject();
        Project project = FileOwnerQuery.getOwner(fileObject);
        if (project == null) {
            return Collections.emptyList();
        }
        NbMavenProject mavProj = project.getLookup().lookup(NbMavenProject.class);
        if (mavProj == null) {
            return Collections.emptyList();
        }


        //copyed from ImportClass
        TreePath path = info.getTreeUtilities().pathFor(errorPosition);
        if (path.getParentPath() == null) {
            return Collections.emptyList();
        }

        Tree leaf = path.getParentPath().getLeaf();

        switch (leaf.getKind()) {
            case METHOD_INVOCATION: {
                MethodInvocationTree mit = (MethodInvocationTree) leaf;

                if (!mit.getTypeArguments().contains(path.getLeaf())) {
                    return Collections.<Fix>emptyList();
                }
            }
            //genaric handling

            case PARAMETERIZED_TYPE:
                 {
                    leaf = path.getParentPath().getParentPath().getLeaf();
                }
                break;
            case ARRAY_TYPE:
                 {
                    leaf = path.getParentPath().getParentPath().getLeaf();
                }
                break;
        }
        switch (leaf.getKind()) {
            case VARIABLE:
                 {
                    Name typeName = null;
                    VariableTree variableTree = (VariableTree) leaf;
                    if (variableTree.getType() != null) {
                        switch (variableTree.getType().getKind()) {
                            case IDENTIFIER:
                                 {
                                    typeName = ((IdentifierTree) variableTree.getType()).getName();
                                }
                                break;
                            case PARAMETERIZED_TYPE:
                                 {
                                    ParameterizedTypeTree ptt = ((ParameterizedTypeTree) variableTree.getType());
                                    if (ptt.getType() != null && ptt.getType().getKind() == Kind.IDENTIFIER) {
                                        typeName = ((IdentifierTree) ptt.getType()).getName();
                                    }
                                }
                                break;
                            case ARRAY_TYPE:
                                 {
                                    ArrayTypeTree ptt = ((ArrayTypeTree) variableTree.getType());
                                    if (ptt.getType() != null && ptt.getType().getKind() == Kind.IDENTIFIER) {
                                        typeName = ((IdentifierTree) ptt.getType()).getName();
                                    }
                                }
                                break;

                        }
                    }

                    ExpressionTree initializer = variableTree.getInitializer();
                    if (typeName != null && initializer != null) {

                        Name itName = null;
                        switch (initializer.getKind()) {
                            case NEW_CLASS:
                                 {
                                    ExpressionTree identifier;
                                    NewClassTree classTree = (NewClassTree) initializer;
                                    identifier = classTree.getIdentifier();

                                    if (identifier != null) {

                                        switch (identifier.getKind()) {
                                            case IDENTIFIER:
                                                itName = ((IdentifierTree) identifier).getName();
                                                break;
                                            case PARAMETERIZED_TYPE:
                                                 {

                                                    ParameterizedTypeTree ptt = ((ParameterizedTypeTree) identifier);
                                                    if (ptt.getType() != null && ptt.getType().getKind() == Kind.IDENTIFIER) {
                                                        itName = ((IdentifierTree) ptt.getType()).getName();
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }
                                break;
                            case NEW_ARRAY:
                                 {
                                    NewArrayTree arrayTree = (NewArrayTree) initializer;
                                    Tree type = arrayTree.getType();
                                    if (type != null) {
                                        if (type.getKind().equals(Kind.IDENTIFIER)) {
                                            itName = ((IdentifierTree) type).getName();
                                        }
                                    }
                                }
                                break;
                        }

                        if (typeName.equals(itName)) {
                            return Collections.<Fix>emptyList();
                        }
                    }
                }
                break;

        }

        String simpleOrQualifiedName = null;

        // XXX somewhat crude; is there a simpler way?
        TreePath p = path;
        while (p != null) {
            TreePath parent = p.getParentPath();
            if (parent == null) {
                break;
            }
            Kind parentKind = parent.getLeaf().getKind();
            if (parentKind == Kind.IMPORT) {
                simpleOrQualifiedName = p.getLeaf().toString();
                break;
            } else if (parentKind == Kind.MEMBER_SELECT || parentKind == Kind.IDENTIFIER) {
                p = parent;
            } else {
                break;
            }
        }

        if (simpleOrQualifiedName == null) {
            try {
                Token<?> ident = findUnresolvedElementToken(info, offset);
                if (ident == null) {
                    return Collections.<Fix>emptyList();
                }
                simpleOrQualifiedName = ident.text().toString();
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
                return Collections.<Fix>emptyList();
            }
        }

        //copyed from ImportClass-end
        if (cancel.get()) {
            return Collections.<Fix>emptyList();
        }
        boolean isTestSource = false;
        
        //#212331 star static imports need to be stripped of the .* part. 
        if (simpleOrQualifiedName.endsWith(".*")) {
            simpleOrQualifiedName = simpleOrQualifiedName.substring(0, simpleOrQualifiedName.length() - ".*".length());
        }

        MavenProject mp = mavProj.getMavenProject();
        String testSourceDirectory = mp.getBuild().getTestSourceDirectory();
        if (testSourceDirectory != null) {
            File testdir = new File(testSourceDirectory);
            FileObject fo = FileUtil.toFileObject(testdir);
            if (fo != null) {
                isTestSource = FileUtil.isParentOf(fo, fileObject);
            }
        }

        List<Fix> fixes = new ArrayList<Fix>();
        if (SearchClassDependencyHint.isSearchDialog()) {

            fixes.add(new MavenSearchFix(project, simpleOrQualifiedName, isTestSource));
        } else {
            //mkleint: this option is has rather serious performance impact.
            // we need to work on performance before we enable it..
            // the result() version's impact is better, always just searching matters, never indexing.
            Collection<NBVersionInfo> findVersionsByClass = filter(mavProj,
                    RepositoryQueries.findVersionsByClassResult(simpleOrQualifiedName, RepositoryPreferences.getInstance().getRepositoryInfos()).getResults(), isTestSource);



            for (NBVersionInfo nbvi : findVersionsByClass) {
                fixes.add(new MavenFixImport(project, nbvi, isTestSource));
            }
        }

        return fixes;
    }

    private Collection<NBVersionInfo> filter(NbMavenProject mavProj, List<NBVersionInfo> nbvis, boolean test) {


        Map<String, NBVersionInfo> items = new HashMap<String, NBVersionInfo>();
        //check dependency already added
        List<Dependency> dependencies = new ArrayList<Dependency>();
        MavenProject prj = mavProj.getMavenProject();
        if (test) {
            dependencies.addAll(prj.getTestDependencies());
        } else {
            dependencies.addAll(prj.getDependencies());
        }

        for (NBVersionInfo info : nbvis) {
            String key = info.getGroupId() + ":" + info.getArtifactId();

            boolean b = items.containsKey(key);
            if (!b) {
                items.put(key, info);
            }
            for (Dependency dependency : dependencies) {
                //check group id and ArtifactId and Scope even
                if (dependency.getGroupId() != null && dependency.getGroupId().equals(info.getGroupId())) {
                    if (dependency.getArtifactId() != null && dependency.getArtifactId().equals(info.getArtifactId())) {
                        if (!test && dependency.getScope() != null && ("compile".equals(dependency.getScope()))) {//NOI18N

                            return Collections.emptyList();
                        }
                    }
                }
            }

        }
        List<NBVersionInfo> filterd = new ArrayList<NBVersionInfo>(items.values());

        return filterd;

    }
    //copyed from ImportClass

    private static Token findUnresolvedElementToken(CompilationInfo info, int offset) throws IOException {
        TokenHierarchy<?> th = info.getTokenHierarchy();
        TokenSequence<JavaTokenId> ts = th.tokenSequence(JavaTokenId.language());

        if (ts == null) {
            return null;
        }

        ts.move(offset);
        if (ts.moveNext()) {
            Token t = ts.token();

            if (t.id() == JavaTokenId.DOT) {
                ts.moveNext();
                t = ts.token();
            } else {
                if (t.id() == JavaTokenId.LT) {
                    ts.moveNext();
                    t = ts.token();
                } else {
                    if (t.id() == JavaTokenId.NEW) {
                        boolean cont = ts.moveNext();

                        while (cont && ts.token().id() == JavaTokenId.WHITESPACE) {
                            cont = ts.moveNext();
                        }

                        if (!cont) {
                            return null;
                        }
                        t = ts.token();
                    }
                }
            }

            if (t.id() == JavaTokenId.IDENTIFIER) {
                return ts.offsetToken();
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return "MAVEN_MISSING_CLASS";//NOI18N

    }

    @Override
    @Messages("LBL_Class_Search_DisplayName=Add Class Dependency From Maven Repository")
    public String getDisplayName() {
        return LBL_Class_Search_DisplayName();
    }

    @Override
    public void cancel() {
        //cancel task
        cancel.set(true);
    }

    static final class MavenFixImport implements EnhancedFix {

        private final Project mavProj;
        private final NBVersionInfo nbvi;
        private final boolean test;

        public MavenFixImport(Project mavProj, NBVersionInfo nbvi, boolean test) {
            this.mavProj = mavProj;
            this.nbvi = nbvi;
            this.test = test;
        }

        @Override
        public CharSequence getSortText() {
            return getText();
        }

        @Override
        @Messages({
            "# {0} - maven coordinates",
            "LBL_Class_Search_Fix=Add Maven Dependency # {0}"})
        public String getText() {
            return LBL_Class_Search_Fix(nbvi.getGroupId() + " : " + nbvi.getArtifactId() + " : " + nbvi.getVersion());

        }

        @Override
        public ChangeInfo implement() throws Exception {
            ModelUtils.addDependency(mavProj.getProjectDirectory().getFileObject("pom.xml"), nbvi.getGroupId(), nbvi.getArtifactId(),
                    nbvi.getVersion(), nbvi.getType(), test ? "test" : null, null, true);//NOI18N

            RequestProcessor.getDefault().post(new Runnable() {

                @Override
                public void run() {
                    mavProj.getLookup().lookup(NbMavenProject.class).triggerDependencyDownload();
                }
            });
            return null;
        }
    }

    static final class MavenSearchFix implements EnhancedFix {

        private final Project mavProj;
        private final String clazz;
        private final boolean test;

        public MavenSearchFix(Project mavProj, String clazz, boolean test) {
            this.mavProj = mavProj;
            this.clazz = clazz;
            this.test = test;
        }

        @Override
        public CharSequence getSortText() {
            return getText();
        }

        @Override
        @Messages({
            "# {0} - classname",
            "LBL_Class_Search_ALL_Fix=Search Dependency at Maven Repositories for {0}"})
        public String getText() {
            return LBL_Class_Search_ALL_Fix(clazz);

        }

        @Override
        @Messages("LBL_Search_Repo=Search In Maven Repositories ")
        public ChangeInfo implement() throws Exception {
            Runnable r = new Runnable() {
                public void run() {
                    NBVersionInfo nbvi = null;
                    SearchDependencyUI dependencyUI = new SearchDependencyUI(clazz, mavProj);
                    
                    DialogDescriptor dd = new DialogDescriptor(dependencyUI,
                            LBL_Search_Repo());
                    dd.setClosingOptions(new Object[]{
                        dependencyUI.getAddButton(),
                        DialogDescriptor.CANCEL_OPTION
                    });
                    dd.setOptions(new Object[]{
                        dependencyUI.getAddButton(),
                        DialogDescriptor.CANCEL_OPTION
                    });
                    Object ret = DialogDisplayer.getDefault().notify(dd);
                    if (dependencyUI.getAddButton() == ret) {
                        nbvi = dependencyUI.getSelectedVersion();
                    }
                    
                    if (nbvi != null) {
                        ModelUtils.addDependency(mavProj.getProjectDirectory().getFileObject("pom.xml"), nbvi.getGroupId(), nbvi.getArtifactId(),
                                nbvi.getVersion(), nbvi.getType(), test ? "test" : null, null, true);//NOI18N

                        RequestProcessor.getDefault().post(new Runnable() {
                            
                            @Override
                            public void run() {
                                mavProj.getLookup().lookup(NbMavenProject.class).triggerDependencyDownload();
                            }
                        });
                    }
                }
            };
            if(EventQueue.isDispatchThread()) {
                r.run();
            } else {
                EventQueue.invokeLater(r);
            }
            return null;
        }
    }
}
