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

package org.netbeans.modules.java.hints.encapsulation;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.modules.java.hints.errors.Utilities;
import org.netbeans.modules.java.hints.errors.Utilities.Visibility;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.BooleanOption;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.Hint.Options;
import org.netbeans.spi.java.hints.TriggerTreeKind;
import org.netbeans.spi.java.hints.UseOptions;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.support.FixFactory;
import org.openide.awt.Actions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Zezula
 */
public class ClassEncapsulation {

    private static final Logger LOG = Logger.getLogger(ClassEncapsulation.class.getName());

    static final boolean ALLOW_ENUMS_DEFAULT = false;
    @BooleanOption(defaultValue=ALLOW_ENUMS_DEFAULT)
    static final String ALLOW_ENUMS_KEY = "allow.enums";

    @Hint(category="encapsulation",suppressWarnings={"PublicInnerClass"}, enabled=false, options=Options.QUERY)   //NOI18N
    @UseOptions(ALLOW_ENUMS_KEY)
    @TriggerTreeKind({Tree.Kind.ANNOTATION_TYPE, Tree.Kind.CLASS, Tree.Kind.ENUM, Tree.Kind.INTERFACE})
    public static ErrorDescription publicCls(final HintContext ctx) {
        assert ctx != null;
        return create(ctx, Visibility.PUBLIC,
            NbBundle.getMessage(ClassEncapsulation.class, "TXT_PublicInnerClass"), "PublicInnerClass");  //NOI18N
    }

    @Hint(category="encapsulation",suppressWarnings={"ProtectedInnerClass"}, enabled=false, options=Options.QUERY)    //NOI18N
    @UseOptions(ALLOW_ENUMS_KEY)
    @TriggerTreeKind({Tree.Kind.ANNOTATION_TYPE, Tree.Kind.CLASS, Tree.Kind.ENUM, Tree.Kind.INTERFACE})
    public static ErrorDescription protectedCls(final HintContext ctx) {
        assert ctx != null;
        return create(ctx, Visibility.PROTECTED,
            NbBundle.getMessage(ClassEncapsulation.class, "TXT_ProtectedInnerClass"), "ProtectedInnerClass"); //NOI18N
    }

    @Hint(category="encapsulation", suppressWarnings={"PackageVisibleInnerClass"}, enabled=false, options=Options.QUERY)
    @UseOptions(ALLOW_ENUMS_KEY)
    @TriggerTreeKind({Tree.Kind.ANNOTATION_TYPE, Tree.Kind.CLASS, Tree.Kind.ENUM, Tree.Kind.INTERFACE})
    public static ErrorDescription packageCls(final HintContext ctx) {
        assert ctx != null;
        return create(ctx, Visibility.PACKAGE_PRIVATE,
            NbBundle.getMessage(ClassEncapsulation.class, "TXT_PackageInnerClass"), "PackageVisibleInnerClass");    //NOI18N
    }

    private static ErrorDescription create(final HintContext ctx, final Visibility visibility,
        final String description, final String suppressWarnings) {
        assert ctx != null;
        assert description != null;
        assert suppressWarnings != null;
        final TreePath tp = ctx.getPath();
        final Tree owner = tp.getParentPath().getLeaf();
        if (!TreeUtilities.CLASS_TREE_KINDS.contains(owner.getKind())) {
            return null;
        }
        if (Utilities.effectiveVisibility(tp) != visibility) {
            return null;
        }
        if (ctx.getPreferences().getBoolean(ALLOW_ENUMS_KEY, ALLOW_ENUMS_DEFAULT)) {
            if (ctx.getInfo().getTreeUtilities().isEnum((ClassTree) tp.getLeaf())) {
                return null;
            }
        }
        return ErrorDescriptionFactory.forName(ctx, tp, description,
            new FixImpl(TreePathHandle.create(tp, ctx.getInfo())),
            FixFactory.createSuppressWarningsFix(ctx.getInfo(), tp, suppressWarnings));
    }

    private static class FixImpl implements Fix {

        private final TreePathHandle handle;

        private FixImpl(final TreePathHandle handle) {
            assert handle != null;
            this.handle = handle;
        }

        @Override
        public String getText() {
            return NbBundle.getMessage(ClassEncapsulation.class,"FIX_MoveInnerToOuter");
        }

        @Override
        public ChangeInfo implement() throws Exception {
            final FileObject file = handle.getFileObject();
            final JTextComponent component = EditorRegistry.lastFocusedComponent();
            if (file != null && file == getFileObject(component)) {
                final int[] position = new int[] {-1};
                JavaSource.forFileObject(file).runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController controller) throws Exception {
                        controller.toPhase(JavaSource.Phase.PARSED);
                        final TreePath tp = handle.resolve(controller);
                        if (tp != null && TreeUtilities.CLASS_TREE_KINDS.contains(tp.getLeaf().getKind())) {
                            position[0] = (int) controller.getTrees().getSourcePositions().getStartPosition(
                                    tp.getCompilationUnit(),
                                    (ClassTree)tp.getLeaf())+1;
                        }
                    }
                }, true);
                invokeRefactoring(component, position[0]);
            }
            return null;
        }

        private static FileObject getFileObject (final JTextComponent comp) {
            if (comp == null) {
                return null;
            }
            final Document doc = comp.getDocument();
            if (doc == null) {
                return null;
            }
            final Object sdp = doc.getProperty(Document.StreamDescriptionProperty);
            if (sdp instanceof FileObject) {
                return (FileObject)sdp;
            }
            if (sdp instanceof DataObject) {
                return ((DataObject)sdp).getPrimaryFile();
            }
            return null;
        }

        private void invokeRefactoring(final JTextComponent component, final int position) {
            assert component != null;
            final Action a = Actions.forID("Refactoring", "org.netbeans.modules.refactoring.java.api.ui.InnerToOuterAction");
            if (a == null) {
                LOG.warning("Move Inner to Outer action not found"); //NOI18N
                return;
            }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (position != -1) {
                            component.setCaretPosition(position);
                        }
                        a.actionPerformed(new ActionEvent(component, 0, null));
                    }
                });
        }
    }

}
