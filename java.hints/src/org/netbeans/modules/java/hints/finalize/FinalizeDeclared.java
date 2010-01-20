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
import com.sun.source.util.TreePath;
import org.netbeans.modules.java.hints.jackpot.code.spi.Hint;
import org.netbeans.modules.java.hints.jackpot.code.spi.TriggerTreeKind;
import org.netbeans.modules.java.hints.jackpot.spi.HintContext;
import org.netbeans.modules.java.hints.jackpot.spi.support.ErrorDescriptionFactory;
import org.netbeans.modules.java.hints.spi.support.FixFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Zezula
 */
@Hint(category="finalization",suppressWarnings={"FinalizeDeclaration"}) //NOI18N
public class FinalizeDeclared {

    @TriggerTreeKind(Tree.Kind.METHOD)
    public static ErrorDescription hint(final HintContext ctx) {
        assert ctx != null;
        final TreePath tp = ctx.getPath();
        final MethodTree tree = (MethodTree) tp.getLeaf();
        if (Util.isFinalize(tree)) {
            if (!Util.isInObject(ctx, tp)) {
                return ErrorDescriptionFactory.forName(ctx, tp, NbBundle.getMessage(FinalizeDeclared.class, "TXT_FinalizeDeclared"),
                FixFactory.createSuppressWarningsFix(ctx.getInfo(), tp, "FinalizeDeclaration"));    //NOI18N
            }
        }
        return null;
    }
}
