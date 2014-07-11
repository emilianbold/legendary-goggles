/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.fold;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaParserResultTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.parsing.spi.TaskIndexingMode;
import org.netbeans.spi.editor.fold.FoldInfo;
import org.netbeans.spi.editor.fold.FoldManagerFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author sdedic
 */
@MimeRegistrations({
    @MimeRegistration(mimeType = "text/x-java", service = TaskFactory.class),
    @MimeRegistration(mimeType = "text/x-java", service = FoldManagerFactory.class),
})
public class ResourceStringFoldProvider extends ParsingFoldSupport{
    private List<MessagePattern> messages = new ArrayList<>();

    public ResourceStringFoldProvider() {
        messages.add(
            new MessagePattern(
                Pattern.compile("^" + Pattern.quote("java.util.ResourceBundle") + "$"), 
                Pattern.compile("^getString$"), MessagePattern.BUNDLE_FROM_INSTANCE, 0));

        messages.add(
            new MessagePattern(
                Pattern.compile("^" + Pattern.quote("org.openide.util.NbBundle") + "$"), 
                Pattern.compile("^getMessage$"), 0, 1));

        messages.add(
            new MessagePattern(
                Pattern.compile("^" + Pattern.quote("java.util.ResourceBundle") + "$"), 
                Pattern.compile("^getBundle$"), 0, MessagePattern.GET_BUNDLE_CALL));

        messages.add(
            new MessagePattern(
                Pattern.compile("^" + Pattern.quote("org.openide.util.NbBundle") + "$"), 
                Pattern.compile("^getBundle$"), 0, MessagePattern.GET_BUNDLE_CALL));

        messages.add(
            new MessagePattern(
                Pattern.compile("\\.[^.]+(Bundle|Messages)$"), 
                Pattern.compile("^(getMessage|getString)"), MessagePattern.BUNDLE_FROM_CLASS, 0));

        // total wildcards
        messages.add(
            new MessagePattern(
                Pattern.compile("\\.Bundle$"), 
                Pattern.compile(".*"), MessagePattern.BUNDLE_FROM_CLASS, MessagePattern.KEY_FROM_METHODNAME));
    }
    
    @Override
    protected FoldProcessor createTask(FileObject f) {
        return new Proc(f);
    }
    
    private final class Proc extends FoldProcessor implements ChangeListener {
        ResourceStringLoader loader;
        
        public Proc(FileObject f) {
            super(f, "text/x-java");
        }

        @Override
        protected boolean processResult(Parser.Result result) {
            CompilationInfo info = CompilationInfo.get(result);
            if (info == null) {
                return false;
            }
            CompilationController ctrl = CompilationController.get(result);
            if (ctrl != null) {
                try {
                    ctrl.toPhase(JavaSource.Phase.RESOLVED);
                } catch (IOException ex) {
                    return false;
                }
            }
            if (loader == null) {
                loader = new ResourceStringLoader(this);
            }
            V v = new V(info, getFile(), this);
            v.setDescriptions(messages);
            v.scan(info.getCompilationUnit(), null);
            return true;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            performRefresh();
        }
    }

    @Override
    protected ParserResultTask createParserTask(FileObject file) {
        final ParserResultTask wrapped = super.createParserTask(file);
        return new JavaParserResultTask(JavaSource.Phase.RESOLVED, TaskIndexingMode.ALLOWED_DURING_SCAN) {
            
            @Override
            public void run(Parser.Result result, SchedulerEvent event) {
                wrapped.run(result, event);
            }
            
            @Override
            public int getPriority() {
                return wrapped.getPriority();
            }
            
            @Override
            public Class<? extends Scheduler> getSchedulerClass() {
                return wrapped.getSchedulerClass();
            }
            
            @Override
            public void cancel() {
                wrapped.cancel();
            }
        };
    }
    

    private static class V extends TreePathScanner<Void, Void> {
        private final FileObject                  anchor;
        private final Proc                         proc;
        private final CompilationInfo info;
        private MessagePattern                  messageMethod;
        private Map<Element, String>            variableBundles = new HashMap<>();
        private TypeMirror          resourceBundleType;
        private String              exprBundleName;
        private TreePath            methodOwnerPath;
        private String              methodName;
        
        /**
         * Recognized method calls will be inspected for possible resource bundle folding
         */
        private List<MessagePattern> descriptions = new ArrayList<>();
                
        public V(CompilationInfo info, FileObject anchor, Proc proc) {
            this.proc = proc;
            this.info = info;
            this.anchor = anchor;
            
            Element el = info.getElements().getTypeElement("java.util.ResourceBundle"); // NO18N
            if (el != null) {
                resourceBundleType = el.asType();
            }
        }
        
        public void setDescriptions(List<MessagePattern> descs) {
            this.descriptions = descs;
        }
        
        private boolean isCancelled() {
            return proc.isCancelled();
        }
        
        private void defineFold(String bundle, String key, Tree expr) {
            final ClassPath cp = ClassPath.getClassPath(anchor, ClassPath.SOURCE);
            FileObject bundleFile = cp.findResource(bundle + ".properties");
            SourcePositions spos = info.getTrees().getSourcePositions();
            int start = (int)spos.getStartPosition(info.getCompilationUnit(), expr);
            int end = (int)spos.getEndPosition(info.getCompilationUnit(), expr);
            
            if (start == -1 || end == -1) {
                return;
            }
            if (bundleFile == null) {
                return;
            }
            
            String message = proc.loader.getMessage(bundleFile, key);
            if (message == null) {
                return;
            }
            int newline = message.indexOf('\n');
            if (newline >= 0) {
                message =  message.substring(0, newline);
                
            }
            message = message.replaceAll("\\.\\.\\.", "\u2026");
            
            FoldInfo info = FoldInfo.range(start, end, JavaFoldTypeProvider.BUNDLE_STRING).
                    withDescription(message).
                    attach(new ResourceStringFoldInfo(bundle, key));
            proc.addFold(info, -1);
        }

        @Override
        public Void scan(Tree tree, Void p) {
            if (isCancelled()) {
                return null;
            }
            super.scan(tree, p);
            return p;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree node, Void p) {
            Void d = super.visitMemberSelect(node, p); 
            Element el = info.getTrees().getElement(getCurrentPath());
            messageMethod = null;
            if (el == null || el.getKind() != ElementKind.METHOD) {
                return d;
            }
            ExecutableElement ee = (ExecutableElement)el;
            String sn = ee.getSimpleName().toString();
            
            for (MessagePattern desc : descriptions) {
                if (!desc.getMethodNamePattern().matcher(sn).matches()) {
                    continue;
                }
                
                // check the defining type
                el = ee.getEnclosingElement();
                if (el == null || !(el.getKind().isClass() || el.getKind().isInterface())) {
                    continue;
                }
                TypeElement tel = (TypeElement)el;
                if (!desc.getOwnerTypePattern().matcher(tel.getQualifiedName().toString()).matches()) {
                    continue;
                }
                
                messageMethod = desc;
                methodName = sn;
                methodOwnerPath = new TreePath(getCurrentPath(), node.getExpression());
                break;
            }
            
            return d;
        }

        @Override
        public Void visitVariable(VariableTree node, Void p) {
            scan(node.getInitializer(), null);
            if (exprBundleName == null) {
                return null;
            }
            TypeMirror tm = info.getTrees().getTypeMirror(getCurrentPath());
            if (resourceBundleType == null || !info.getTypes().isAssignable(tm, resourceBundleType)) {
                return null;
            }
            Element dest = info.getTrees().getElement(getCurrentPath());
            if (dest.getKind() == ElementKind.LOCAL_VARIABLE || dest.getKind() == ElementKind.FIELD) {
                variableBundles.put(dest, exprBundleName);
            }
            return null;
        }

        @Override
        public Void visitAssignment(AssignmentTree node, Void p) {
            exprBundleName = null;
            Void d = super.visitAssignment(node, p);
            if (exprBundleName != null) {
                Element dest = info.getTrees().getElement(getCurrentPath());
                if (dest.getKind() == ElementKind.LOCAL_VARIABLE || dest.getKind() == ElementKind.FIELD) {
                    variableBundles.put(dest, exprBundleName);
                }
            }
            return d;
        }
        
        
        
        private void processGetBundleCall(MethodInvocationTree node) {
            TypeMirror tm = info.getTrees().getTypeMirror(getCurrentPath());
            if (resourceBundleType == null ||tm == null || tm.getKind() != TypeKind.DECLARED) {
                return;
            }
            if (!info.getTypes().isAssignable(tm, resourceBundleType)) {
                return;
            }
            
            // OK, get the parameter said to describe the bundle name
            exprBundleName = getBundleName(node, messageMethod.getBundleParam(), messageMethod.getBundleFile());
        }
        
        private String getBundleName(MethodInvocationTree n, int index, String bfn) {
            if (n.getArguments().size() <= index) {
                return null;
            }
            ExpressionTree t = n.getArguments().get(index);
            // recognize just string literals + .class references
            if (t.getKind() == Tree.Kind.STRING_LITERAL) {
                Object o = ((LiteralTree)t).getValue();
                return o == null ? null : o.toString();
            } else if (t.getKind() == Tree.Kind.MEMBER_SELECT) {
                MemberSelectTree mst = (MemberSelectTree)t;
                if (!mst.getIdentifier().contentEquals("class")) {
                    return null;
                }
                return bundleFileFromClass(new TreePath(getCurrentPath(), mst.getExpression()), bfn);
            }
            return null;
        }
        
        private String bundleFileFromClass(TreePath classTreePath, String bfn) {
            TypeMirror tm = info.getTrees().getTypeMirror(classTreePath);
            if (tm.getKind() != TypeKind.DECLARED) {
                return null;
            }
            Element clazz = ((DeclaredType)tm).asElement();
            while ((clazz instanceof TypeElement)) {
                clazz = ((TypeElement)clazz).getEnclosingElement();
            }
            if (clazz.getKind() == ElementKind.PACKAGE) {
                PackageElement pack = ((PackageElement)clazz);
                if (pack.isUnnamed()) {
                    return null;
                }
                return pack.getQualifiedName().toString().replaceAll("\\.", "/") + "/" + bfn;
            }
            return null;
        }

        
        @Override
        public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
            messageMethod = null;
            exprBundleName = null;

            Void d = scan(node.getMethodSelect(), p);
            
            try {
                if (messageMethod == null) {
                    return d;
                }
                String bundleFile = null;
                if (messageMethod.getKeyParam() == MessagePattern.GET_BUNDLE_CALL) {
                    processGetBundleCall(node);
                } else {
                    int bp = messageMethod.getBundleParam();
                    if (bp == MessagePattern.BUNDLE_FROM_CLASS) {
                        TypeMirror tm = info.getTrees().getTypeMirror(methodOwnerPath);
                        if (tm != null && tm.getKind() == TypeKind.DECLARED) {
                            bundleFile = bundleFileFromClass(methodOwnerPath, messageMethod.getBundleFile());
                        }
                    } else if (bp == MessagePattern.BUNDLE_FROM_INSTANCE) {
                        // simplification: assume the selector expression is a variable
                        Element el = info.getTrees().getElement(methodOwnerPath);
                        if (el != null && (el.getKind() == ElementKind.LOCAL_VARIABLE || el.getKind() == ElementKind.FIELD)) {
                            bundleFile = variableBundles.get(el);
                        } else {
                            bundleFile = exprBundleName;
                        }
                    } else if (bp >= 0 && bp < node.getArguments().size()) {
                        bundleFile = getBundleName(node, bp, messageMethod.getBundleFile());
                    }
                }

                if (bundleFile == null) {
                    return d;
                }

                int keyIndex = messageMethod.getKeyParam();
                if (node.getArguments().size() <= keyIndex) {
                    return d;
                }

                String keyVal;
                if (keyIndex == MessagePattern.KEY_FROM_METHODNAME) {
                    keyVal = this.methodName;
                } else {
                    ExpressionTree keyArg = node.getArguments().get(keyIndex);
                    if (keyArg.getKind() != Tree.Kind.STRING_LITERAL) {
                        return d;
                    }
                    Object o = ((LiteralTree)keyArg).getValue();
                    if (o == null) {
                        return d;
                    }
                    keyVal = o.toString();
                }

                defineFold(bundleFile, keyVal, node);
            } finally {
            
                String expr = exprBundleName;

                scan(node.getArguments(), p);

                this.exprBundleName = expr;
            }
            
            // simplification, accept only String literals
            return d;
        }
    }
    
}
