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

package org.netbeans.modules.php.editor.parser;

import org.netbeans.modules.php.editor.PHPTestBase;

/**
 *
 * @author Petr Pisl
 */
public class SemanticAnalyzerTest extends PHPTestBase {

    public SemanticAnalyzerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAnalysisFields() throws Exception {
        checkSemantic("testfiles/class001.php");
    }

    public void testAnalysisStatic() throws Exception {
        checkSemantic("testfiles/class002.php");
    }

    public void testAnalysisUnusedPrivateField() throws Exception {
        checkSemantic("testfiles/class003.php");
    }

    public void testAnalysisUnusedPrivateMethod() throws Exception {
        checkSemantic("testfiles/class004.php");
    }

    public void testAnalysisAll() throws Exception {
        checkSemantic("testfiles/class005.php");
    }

    public void testAnalysisDeclarationAfterUsage() throws Exception {
        checkSemantic("testfiles/class006.php");
    }

    public void testIssue142005() throws Exception {
        checkSemantic("testfiles/class007.php");
    }

    // issue #139813
    public void testAbstract() throws Exception {
        checkSemantic("testfiles/abstract01.php");
    }

    public void testIssue142644() throws Exception {
        checkSemantic("testfiles/issue142644.php");
    }

    public void testIssue141041() throws Exception {
        checkSemantic("testfiles/issue141041.php");
    }

    public void testIssue146193() throws Exception {
        checkSemantic("testfiles/issue146193.php");
    }

    public void testIssue146197() throws Exception {
        checkSemantic("testfiles/issue146197.php");
    }

    public void testIssue145694() throws Exception {
        checkSemantic("testfiles/issue145694.php");
    }

    public void testIssue144195() throws Exception {
        checkSemantic("testfiles/issue144195.php");
    }

    public void testIssue154876() throws Exception {
        checkSemantic("testfiles/issue154876.php");
    }

    public void testVarComment01() throws Exception {
        checkSemantic("testfiles/varcomment/simple01.php");
    }

    public void testVarComment02() throws Exception {
        checkSemantic("testfiles/varcomment/mixed01.php");
    }

    public void testIssue194535() throws Exception {
        checkSemantic("testfiles/issue194535.php");
    }

    public void testTraits01() throws Exception {
        checkSemantic("testfiles/traits_01.php");
    }

    public void testTraits02() throws Exception {
        checkSemantic("testfiles/traits_02.php");
    }

    public void testTraits03() throws Exception {
        checkSemantic("testfiles/traits_03.php");
    }

    public void testTraits04() throws Exception {
        checkSemantic("testfiles/traits_04.php");
    }

    public void testTraits05() throws Exception {
        checkSemantic("testfiles/traits_05.php");
    }

    public void testTraits06() throws Exception {
        checkSemantic("testfiles/traits_06.php");
    }

    public void testUnusedUses01() throws Exception {
        checkSemantic("testfiles/unusedUses_01.php");
    }

    public void testUnusedUses02() throws Exception {
        checkSemantic("testfiles/unusedUses_02.php");
    }

    public void testUnusedUses03() throws Exception {
        checkSemantic("testfiles/unusedUses_03.php");
    }

    public void testUnusedUses04() throws Exception {
        checkSemantic("testfiles/unusedUses_04.php");
    }

    public void testUnusedUses05() throws Exception {
        checkSemantic("testfiles/unusedUses_05.php");
    }

    public void testUnusedUses06() throws Exception {
        checkSemantic("testfiles/unusedUses_06.php");
    }
}
