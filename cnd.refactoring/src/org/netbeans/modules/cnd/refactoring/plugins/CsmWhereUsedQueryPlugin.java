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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.cnd.refactoring.plugins;

import java.util.Collection;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmIncludeHierarchyResolver;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceRepository;
import org.netbeans.modules.cnd.refactoring.api.WhereUsedQueryConstants;
import org.netbeans.modules.cnd.refactoring.elements.CsmRefactoringElementImpl;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.ProgressEvent;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.openide.util.NbBundle;

/**
 * Actual implementation of Find Usages query search for C/C++
 * 
 * @todo Perform index lookups to determine the set of files to be checked!
 * 
 * @author Vladimir Voskresensky
 */
public class CsmWhereUsedQueryPlugin extends CsmRefactoringPlugin {
    private WhereUsedQuery refactoring;
//    private RubyElementCtx searchHandle;
//    private Set<IndexedClass> subclasses;
    private final CsmObject startReferenceObject;
    private String targetName;
    
    /** Creates a new instance of WhereUsedQuery */
    public CsmWhereUsedQueryPlugin(WhereUsedQuery refactoring) {
        this.refactoring = refactoring;
//        this.searchHandle = refactoring.getRefactoringSource().lookup(RubyElementCtx.class);
        startReferenceObject = refactoring.getRefactoringSource().lookup(CsmObject.class);
        targetName = "";//searchHandle.getSimpleName();
    }
    
//    protected Source getRubySource(Phase p) {
//        switch (p) {
//        default: 
//            return RetoucheUtils.getSource(searchHandle.getFileObject());
//        }
//    }
//    
//    protected Problem preCheck(CompilationController info) {
////        Problem p = isElementAvail(getSearchHandle(), refactoring.getContext().lookup(CompilationInfo.class));
////        if (p != null)
////            return p;
//        
////        if (!((jmiObject instanceof Feature) || (jmiObject instanceof Variable) || (jmiObject instanceof JavaPackage) || (jmiObject instanceof TypeParameter)) ) {
////            return new Problem(true, NbBundle.getMessage(WhereUsedQuery.class, "ERR_WhereUsedWrongType"));
////        }
//        
//        return null;
//    }
    
//    private Set<FileObject> getRelevantFiles(final RubyElementCtx tph) {
//        final ClasspathInfo cpInfo = getClasspathInfo(refactoring);
//        //final ClassIndex idx = cpInfo.getClassIndex();
//        final Set<FileObject> set = new HashSet<FileObject>();
//                
//        final FileObject file = tph.getFileObject();
//        Source source;
//        if (file!=null) {
//           set.add(file);
//            source = RetoucheUtils.createSource(cpInfo, tph.getFileObject());
//        } else {
//            source = Source.create(cpInfo);
//        }
//        //XXX: This is slow!
//        CancellableTask<CompilationController> task = new CancellableTask<CompilationController>() {
//            public void cancel() {
//            }
//            
//            public void run(CompilationController info) throws Exception {
//                info.toPhase(org.netbeans.api.retouche.source.Phase.RESOLVED);
//                //System.out.println("TODO - compute a full set of files to be checked... for now just lamely using the project files");
//                //set.add(info.getFileObject());
//                // (This currently doesn't need to run in a compilation controller since I'm not using parse results at all...)
//
//                
//                if (isFindSubclasses() || isFindDirectSubclassesOnly()) {
//                    // No need to do any parsing, we'll be using the index to find these files!
//                    set.add(info.getFileObject());
//
//                    String name = tph.getName();
//                
//                    // Find overrides of the class
//                    RubyIndex index = RubyIndex.get(info.getIndex());
//                    String fqn = AstUtilities.getFqnName(tph.getPath());
//                    Set<IndexedClass> classes = index.getSubClasses(null, fqn, name, isFindDirectSubclassesOnly());
//
//                    if (classes.size() > 0) {
//                        subclasses = classes;
//                        // For now just parse this particular file!
//                        set.add(info.getFileObject());
//                        return;
//                    }
//                }
//                
//                if (tph.getKind() == ElementKind.VARIABLE || tph.getKind() == ElementKind.PARAMETER) {
//                    // For local variables, only look in the current file!
//                    set.add(info.getFileObject());
//                }  else {
//                    set.addAll(RetoucheUtils.getRubyFilesInProject(info.getFileObject()));
//                }
//            }
//        };
//        try {
//            source.runUserActionTask(task, true);
//        } catch (IOException ioe) {
//            throw (RuntimeException) new RuntimeException().initCause(ioe);
//        }
//        return set;
//    }
    
    //@Override
    public Problem prepare(final RefactoringElementsBag elements) {
        CsmObject referencedObject = refactoring.getRefactoringSource().lookup(CsmObject.class);
        if (referencedObject == null) {
            return null;
        }
        Collection<CsmFile> files = getRelevantFiles(startReferenceObject, referencedObject);
        fireProgressListenerStart(ProgressEvent.START, files.size());
        processQuery(referencedObject, elements, files);
//        processFiles(a, new FindTask(elements));
        fireProgressListenerStop();
        return null;
    }
    //    //@Override
//    protected Problem fastCheckParameters(CompilationController info) {
//        if (searchHandle.getKind() == ElementKind.METHOD) {
//            return checkParametersForMethod(isFindOverridingMethods(), isFindUsages());
//        } 
//        return null;
//    }
//    
//    //@Override
//    protected Problem checkParameters(CompilationController info) {
//        return null;
//    }
    
    private Problem checkParametersForMethod(boolean overriders, boolean usages) {
        if (!(usages || overriders)) {
            return new Problem(true, NbBundle.getMessage(CsmWhereUsedQueryPlugin.class, "MSG_NothingToFind"));
        } else
            return null;
    }
        
    private boolean isFindSubclasses() {
        return refactoring.getBooleanValue(WhereUsedQueryConstants.FIND_SUBCLASSES);
    }
    private boolean isFindUsages() {
        return refactoring.getBooleanValue(WhereUsedQuery.FIND_REFERENCES);
    }
    private boolean isFindDirectSubclassesOnly() {
        return refactoring.getBooleanValue(WhereUsedQueryConstants.FIND_DIRECT_SUBCLASSES);
    }
    
    private boolean isFindOverridingMethods() {
        return refactoring.getBooleanValue(WhereUsedQueryConstants.FIND_OVERRIDING_METHODS);
    }

    private boolean isSearchFromBaseClass() {
        return refactoring.getBooleanValue(WhereUsedQueryConstants.SEARCH_FROM_BASECLASS);
    }

    private boolean isSearchInComments() {
        return refactoring.getBooleanValue(WhereUsedQuery.SEARCH_IN_COMMENTS);
    }

    private void processQuery(final CsmObject csmObject, 
            final RefactoringElementsBag elements,
            final Collection<CsmFile> files) {
        if (isFindUsages()) {
            if (CsmKindUtilities.isFile(csmObject)) {
                Collection<CsmReference> refs = CsmIncludeHierarchyResolver.getDefault().getIncludes((CsmFile)csmObject);
                for (CsmReference csmReference : refs) {
                    elements.add(refactoring, CsmRefactoringElementImpl.create(csmReference));
                }      
            } else {
                CsmReferenceRepository xRef = CsmReferenceRepository.getDefault();
                for (CsmFile file : files) {
                    if (cancelRequest) {
                        break;
                    }
                    Collection<CsmReference> refs = xRef.getReferences(csmObject, file, true);
                    for (CsmReference csmReference : refs) {
                        elements.add(refactoring, CsmRefactoringElementImpl.create(csmReference));
                    }      
                    fireProgressListenerStep();
                }
            }
        }
    }
    
//    private class FindTask implements CancellableTask<WorkingCopy> {
//
//        private RefactoringElementsBag elements;
//        private volatile boolean cancelled;
//
//        public FindTask(RefactoringElementsBag elements) {
//            super();
//            this.elements = elements;
//        }
//
//        public void cancel() {
//            cancelled=true;
//        }
//
//        public void run(WorkingCopy compiler) throws IOException {
//            if (cancelled)
//                return ;
//            compiler.toPhase(org.netbeans.api.retouche.source.Phase.RESOLVED);
//
//            Error error = null;
//            
//            RubyElementCtx searchCtx = searchHandle;
//            
//            Node root = AstUtilities.getRoot(compiler);
//            
//            if (root == null) {
//                //System.out.println("Skipping file " + workingCopy.getFileObject());
//                // See if the document contains references to this symbol and if so, put a warning in
//                if (compiler.getText().indexOf(targetName) != -1) {
//                    int start = 0;
//                    int end = 0;
//                    String desc = "Parse error in file which contains " + targetName + " reference - skipping it"; 
//                    List<Error> errors = compiler.getDiagnostics();
//                    if (errors.size() > 0) {
//                        for (Error e : errors) {
//                            if (e.getSeverity() == Severity.ERROR) {
//                                error = e;
//                                break;
//                            }
//                        }
//                        if (error == null) {
//                            error = errors.get(0);
//                        }
//                        
//                        String errorMsg = error.getDisplayName();
//                        
//                        if (errorMsg.length() > 80) {
//                            errorMsg = errorMsg.substring(0, 77) + "..."; // NOI18N
//                        }
//
//                        desc = desc + "; " + errorMsg;
//                        start = end = error.getStartPosition().getOffset();
//                        if (compiler.getEmbeddingModel() != null) {
//                            start = compiler.getEmbeddingModel().generatedToSourcePos(compiler.getFileObject(), start);
//                            if (start == -1) {
//                                start = 0; // Just point to top of the file
//                            }
//                            end = start;
//                        }
//                    }
//                    
//                    Set<Modifier> modifiers = Collections.emptySet();
//                    Icon icon = UiUtils.getElementIcon(ElementKind.ERROR, modifiers);
//                    OffsetRange range = new OffsetRange(start, end);
//                    WhereUsedElement element = WhereUsedElement.create(compiler, targetName, desc, range, icon); 
//                    elements.add(refactoring, element);
//                }
//            }
//
//            if (error == null && isSearchInComments()) {
//                Document doc = RetoucheUtils.getDocument(compiler, compiler.getFileObject());
//                if (doc != null) {
//                    //force open
//                    TokenHierarchy<Document> th = TokenHierarchy.get(doc);
//                    TokenSequence<?extends TokenId> ts = th.tokenSequence();
//
//                    ts.move(0);
//
//                    searchTokenSequence(compiler, ts);
//                }
//            }
//            
//            if (root == null) {
//                // TODO - warn that this file isn't compileable and is skipped?
//                fireProgressListenerStep();
//                return;
//            }
//            
//            Element element = AstElement.create(root);
//            Node node = searchCtx.getNode();
//            RubyElementCtx fileCtx = new RubyElementCtx(root, node, element, compiler.getFileObject(), compiler);
//
//            // If it's a local search, use a simpler search routine
//            // TODO: ArgumentNode - look to see if we're in a parameter list, and if so its a localvar
//            // (if not, it's a method)
//            
//            if (isFindSubclasses() || isFindDirectSubclassesOnly()) {
//                // I'm only looking for the specific classes
//                assert subclasses != null;
//                // Look in these files for the given classes
//                //findSubClass(root);
//                for (IndexedClass clz : subclasses) {
//                    RubyElementCtx matchCtx = new RubyElementCtx(clz, compiler);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                }
//            } else if (isFindUsages()) {
//                Node method = null;
//                if (node instanceof ArgumentNode) {
//                    AstPath path = searchCtx.getPath();
//                    assert path.leaf() == node;
//                    Node parent = path.leafParent();
//
//                    if (!(parent instanceof MethodDefNode)) {
//                        method = AstUtilities.findLocalScope(node, path);
//                    }
//                } else if (node instanceof LocalVarNode || node instanceof LocalAsgnNode || node instanceof DAsgnNode || 
//                        node instanceof DVarNode) {
//                    // A local variable read or a parameter read, or an assignment to one of these
//                    AstPath path = searchCtx.getPath();
//                    method = AstUtilities.findLocalScope(node, path);
//                }
//
//                if (method != null) {
//                    findLocal(searchCtx, fileCtx, method, targetName);
//                } else {
//                    // Full AST search
//                    // TODO: If it's a local variable, parameter or dynamic variable, limit search to the current scope!
//                    AstPath path = new AstPath();
//                    path.descend(root);
//                    find(path, searchCtx, fileCtx, root, targetName, Character.isUpperCase(targetName.charAt(0)));
//                    path.ascend();
//                }
//            
//                // TODO: Comment search
//                // TODO: ClassSearch: If looking for subtypes only, do something special here...
//               // (in fact, I should be able to ONLY use the index, correct?)
//                
//                // TODO
//                
//            } else if (isFindOverridingMethods()) {
//                // TODO
//                
//            } else if (isSearchFromBaseClass()) {
//                // TODO
//            }
//            fireProgressListenerStep();
//        }
//
//        private void searchTokenSequence(CompilationInfo info, TokenSequence<? extends TokenId> ts) {
//            if (ts.moveNext()) {
//                do {
//                    Token<?extends TokenId> token = ts.token();
//                    TokenId id = token.id();
//
//                    String primaryCategory = id.primaryCategory();
//                    if ("comment".equals(primaryCategory) || "block-comment".equals(primaryCategory)) { // NOI18N
//                        // search this comment
//                        String text = token.text().toString();
//                        int index = text.indexOf(targetName);
//                        if (index != -1) {
//                            // TODO make sure it's its own word. Technically I could
//                            // look at identifier chars like "_" here but since they are
//                            // used for other purposes in comments, consider letters
//                            // and numbers as enough
//                            if ((index == 0 || !Character.isLetterOrDigit(text.charAt(index-1))) &&
//                                    (index+targetName.length() >= text.length() || 
//                                    !Character.isLetterOrDigit(text.charAt(index+targetName.length())))) {
//                                int start = ts.offset() + index;
//                                int end = start + targetName.length();
//                                
//                                // TODO - get a comment-reference icon? For now, just use the icon type
//                                // of the search target
//                                Set<Modifier> modifiers = Collections.emptySet();
//                                if (searchHandle.getElement() != null) {
//                                    modifiers = searchHandle.getElement().getModifiers();
//                                }
//                                Icon icon = UiUtils.getElementIcon(searchHandle.getKind(), modifiers);
//                                OffsetRange range = new OffsetRange(start, end);
//                                WhereUsedElement element = WhereUsedElement.create(info, targetName, range, icon); 
//                                elements.add(refactoring, element);
//                            }
//                        }
//                    } else {
//                        TokenSequence<? extends TokenId> embedded = ts.embedded();
//                        if (embedded != null) {
//                            searchTokenSequence(info, embedded);
//                        }                                    
//                    }
//                } while (ts.moveNext());
//            }
//        }
//
//        /**
//         * @todo P1: This is matching method names on classes that have nothing to do with the class we're searching for
//         *   - I've gotta filter fields, methods etc. that are not in the current class
//         *  (but I also have to search for methods that are OVERRIDING the class... so I've gotta work a little harder!)
//         * @todo Arity matching on the methods to preclude methods that aren't overriding or aliasing!
//         */
//        private void find(AstPath path, RubyElementCtx searchCtx, RubyElementCtx fileCtx, Node node, String name, boolean upperCase) {
//            /*if (node instanceof ArgumentNode) {
//                if (((ArgumentNode)node).getName().equals(name)) {
//                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                }
//            } else*/ if (node instanceof AliasNode) {
//                AliasNode an = (AliasNode)node;
//                if (an.getNewName().equals(name) || an.getOldName().equals(name)) {
//                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                }
//            } else if (!upperCase) {
//                // Local variables - I can be smarter about context searches here!
//                
//                // Methods, attributes, etc.
//                // TODO - be more discriminating on the filetype
//                if (node instanceof MethodDefNode) {
//                    if (((MethodDefNode)node).getName().equals(name)) {
//                                                
//                        boolean skip = false;
//
//                        // Check that we're in a class or module we're interested in
//                        String fqn = AstUtilities.getFqnName(path);
//                        if (fqn == null || fqn.length() == 0) {
//                            fqn = RubyIndex.OBJECT;
//                        }
//                        
//                        if (!fqn.equals(searchCtx.getDefClass())) {
//                            // XXX THE ABOVE IS NOT RIGHT - I shouldn't
//                            // use equals on the class names, I should use the
//                            // index and see if one derives from includes the other
////                            skip = true;
//                        }
//
//                        // Check arity
//                        if (!skip && AstUtilities.isCall(searchCtx.getNode())) {
//                            // The reference is a call and this is a definition; see if
//                            // this looks like a match
//                            // TODO - enforce that this method is also in the desired
//                            // target class!!!
//                            if (!AstUtilities.isCallFor(searchCtx.getNode(), searchCtx.getArity(), node)) {
//                                skip = true;
//                            }
//                        } else {
//                            // The search handle is a method def, as is this, with the same name.
//                            // Now I need to go and see if this is an override (e.g. compatible
//                            // arglist...)
//                            // XXX TODO
//                        }
//                        
//                        if (!skip) {
//                            node = AstUtilities.getDefNameNode((MethodDefNode)node);
//                            // Found a method match
//                            // TODO - check arity - see OccurrencesFinder
//                            RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                            elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                        }
//                    }
//                } else if (AstUtilities.isCall(node)) {
//                     if (((INameNode)node).getName().equals(name)) {
//                         // TODO - if it's a call without a lhs (e.g. Call.LOCAL),
//                         // make sure that we're referring to the same method call
//                        // Found a method call match
//                        // TODO - make a node on the same line
//                        // TODO - check arity - see OccurrencesFinder
//                        RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                        elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                     }
//                } else if (AstUtilities.isAttr(node)) {
//                    SymbolNode[] symbols = AstUtilities.getAttrSymbols(node);
//                    for (SymbolNode symbol : symbols) {
//                        if (symbol.getName().equals(name)) {
//                            RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                            elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                        }
//                    }
//                } else if (node instanceof SymbolNode) {
//                    if (((SymbolNode)node).getName().equals(name)) {
//                        RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                        elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                    }
//                } else if (node instanceof GlobalVarNode || node instanceof GlobalAsgnNode ||
//                        node instanceof InstVarNode || node instanceof InstAsgnNode ||
//                        node instanceof ClassVarAsgnNode || node instanceof ClassVarDeclNode || node instanceof ClassVarNode) {
//                    if (((INameNode)node).getName().equals(name)) {
//                        RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                        elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                    }
//                }
//            } else {
//                // Classes, modules, constants, etc.
//                if (node instanceof Colon2Node) {
//                    Colon2Node c2n = (Colon2Node)node;
//                    if (c2n.getName().equals(name)) {
//                        RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                        elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                    }
//                    
//                } else if (node instanceof ConstNode || node instanceof ConstDeclNode) {
//                    if (((INameNode)node).getName().equals(name)) {
//                        RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                        elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                    }
//                }
//            }
//
//            @SuppressWarnings("unchecked")
//            List<Node> list = node.childNodes();
//
//            for (Node child : list) {
//                path.descend(child);
//                find(path, searchCtx, fileCtx, child, name, upperCase);
//                path.ascend();
//            }
//        }
//        
//        /** Search for local variables in local scope */
//        private void findLocal(RubyElementCtx searchCtx, RubyElementCtx fileCtx, Node node, String name) {
//            if (node instanceof ArgumentNode) {
//                // TODO - check parent and make sure it's not a method of the same name?
//                // e.g. if I have "def foo(foo)" and I'm searching for "foo" (the parameter),
//                // I don't want to pick up the ArgumentNode under def foo that corresponds to the
//                // "foo" method name!
//                if (((ArgumentNode)node).getName().equals(name)) {
//                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                }
//// I don't have alias nodes within a method, do I?                
////            } else if (node instanceof AliasNode) { 
////                AliasNode an = (AliasNode)node;
////                if (an.getNewName().equals(name) || an.getOldName().equals(name)) {
////                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
////                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
////                }
//            } else if (node instanceof LocalVarNode || node instanceof LocalAsgnNode) {
//                if (((INameNode)node).getName().equals(name)) {
//                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                }
//            } else if (node instanceof DVarNode || node instanceof DAsgnNode) {
//                 if (((INameNode)node).getName().equals(name)) {
//                    // Found a method call match
//                    // TODO - make a node on the same line
//                    // TODO - check arity - see OccurrencesFinder
//                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                 }                 
//            } else if (node instanceof SymbolNode) {
//                // XXX Can I have symbols to local variables? Try it!!!
//                if (((SymbolNode)node).getName().equals(name)) {
//                    RubyElementCtx matchCtx = new RubyElementCtx(fileCtx, node);
//                    elements.add(refactoring, WhereUsedElement.create(matchCtx));
//                }
//            }
//
//            @SuppressWarnings("unchecked")
//            List<Node> list = node.childNodes();
//
//            for (Node child : list) {
//                findLocal(searchCtx, fileCtx, child, name);
//            }
//        }
//
////        private void findSubClass(Node node) {
////            @SuppressWarnings("unchecked")
////            List<Node> list = node.childNodes();
////
////            for (Node child : list) {
////                findSubClass(child);
////            }
////        }
//    }
}
