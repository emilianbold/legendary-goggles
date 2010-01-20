/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints.finalize;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.jackpot.code.spi.Hint;
import org.netbeans.modules.java.hints.jackpot.code.spi.TriggerTreeKind;
import org.netbeans.modules.java.hints.jackpot.spi.HintContext;
import org.netbeans.modules.java.hints.jackpot.spi.support.ErrorDescriptionFactory;
import org.netbeans.modules.java.hints.spi.support.FixFactory;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Zezula
 */
@Hint(category="finalization",suppressWarnings={"FinalizeNotProtected"},enabled=false)    //NOI18N
public class FinalizeNotProtected {

    @TriggerTreeKind(Kind.METHOD)
    public static ErrorDescription hint(final HintContext ctx) {
        assert ctx != null;
        final TreePath tp = ctx.getPath();
        final MethodTree tree = (MethodTree) tp.getLeaf();
        if (Util.isFinalize(tree)) {
            final Set<Modifier> modifiers = tree.getModifiers().getFlags();
            if (modifiers.contains(Modifier.PUBLIC)) {
                return ErrorDescriptionFactory.forName(ctx, tp,
                        NbBundle.getMessage(FinalizeNotProtected.class, "TXT_FinalizeNotProtected"),
                        new FixImpl(TreePathHandle.create(tp, ctx.getInfo())),
                        FixFactory.createSuppressWarningsFix(ctx.getInfo(), ctx.getPath(), "FinalizeNotProtected"));    //NOI18N
            }
        }
        return null;
    }

    static class FixImpl implements Fix {

        private final TreePathHandle handle;

        FixImpl(final TreePathHandle handle) {
            assert handle != null;
            this.handle = handle;
        }

        public String getText() {
            return NbBundle.getMessage(FinalizeNotProtected.class, "FIX_FinalizeNotProtected_MakePublic");
        }

        public ChangeInfo implement() throws Exception {
            JavaSource.forFileObject(handle.getFileObject()).runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(JavaSource.Phase.RESOLVED);
                    final TreePath tp = handle.resolve(wc);
                    if (tp == null) {
                        return;
                    }
                    final Tree tree = tp.getLeaf();
                    if (tree.getKind() != Tree.Kind.METHOD) {
                        return;
                    }
                    final TreeMaker tm = wc.getTreeMaker();
                    wc.rewrite(((MethodTree)tree).getModifiers(), tm.addModifiersModifier(
                            tm.removeModifiersModifier(((MethodTree)tree).getModifiers(), Modifier.PUBLIC),
                            Modifier.PROTECTED));
                }
            }).commit();
            return null;
        }
    }
}
