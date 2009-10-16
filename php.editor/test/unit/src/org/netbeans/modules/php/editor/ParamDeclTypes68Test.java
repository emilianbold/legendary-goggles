/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.php.editor;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.php.project.api.PhpSourcePath;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author tomslot
 */
public class ParamDeclTypes68Test extends PHPTestBase {

    public ParamDeclTypes68Test(String testName) {
        super(testName);
    }

    public void testParamDeclTypes() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$c,$d) {^", false);
    }

    public void testParamDeclTypes2() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$c,$d)^", false);
    }

    public void testParamDeclTypes3() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$c,$d^", false);
    }

    public void testParamDeclTypes4() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$c,$^", false);
    }

    public void testParamDeclTypes5() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$c,^", false);
    }

    public void testParamDeclTypes6() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$c^", false);
    }

    public void testParamDeclTypes7() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,$^", false);
    }

    public void testParamDeclTypes8() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b,^", false);
    }

    public void testParamDeclTypes9() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$b^", false);
    }

    public void testParamDeclTypes10() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,$^", false);
    }

    public void testParamDeclTypes11() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a,^", false);
    }

    public void testParamDeclTypes12() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($a^", false);
    }

    public void testParamDeclTypes13() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc($^", false);
    }

    public void testParamDeclTypes14() throws Exception {
        checkCompletion("testfiles/completion/lib/netbeans68version/paramdecltypes/paramdecltypes.php", "function paramsfnc(^", false);
    }

    @Override
    protected Map<String, ClassPath> createClassPathsForTest() {
        return Collections.singletonMap(
                PhpSourcePath.SOURCE_CP,
                ClassPathSupport.createClassPath(new FileObject[]{
                    FileUtil.toFileObject(new File(getDataDir(), "/testfiles/completion/lib/netbeans68version/paramdecltypes"))
                }));
    }
}
