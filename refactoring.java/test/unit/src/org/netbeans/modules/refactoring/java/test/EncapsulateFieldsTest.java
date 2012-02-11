/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing the
 * software, include this License Header Notice in each file and include the
 * License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by Oracle
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or only
 * the GPL Version 2, indicate your decision by adding "[Contributor] elects to
 * include this software in this distribution under the [CDDL or GPL Version 2]
 * license." If you do not indicate a single choice of license, a recipient has
 * the option to distribute your version of this file under either the CDDL, the
 * GPL Version 2 or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.refactoring.java.test;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.junit.RandomlyFails;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RefactoringSession;
import org.netbeans.modules.refactoring.java.plugins.EncapsulateFieldRefactoringPlugin;
import org.netbeans.modules.refactoring.java.ui.EncapsulateFieldsRefactoring;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Ralph Ruijs
 */
public class EncapsulateFieldsTest extends RefactoringTestBase {

    public EncapsulateFieldsTest(String name) {
        super(name);
    }

    public void testEncapsulateFields() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap; public class A { public int i; public int j; }"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0, 1});
        verifyContent(src,
                new File("encap/A.java", "package encap; public class A { private int i; private int j;\n"
                + "public int getI() { return i; }\n"
                + "public void setI(int i) { this.i = i; }\n"
                + "public int getJ() { return j; }\n"
                + "public void setJ(int j) { this.j = j; } }"));
    }

    public void testSelfEncapsulateFields() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    public int i;\n"
                + "\n"
                + "    public void foo() {\n"
                + "        i = 5;\n"
                + "        System.out.println(i);\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private int i;\n"
                + "\n"
                + "    public void foo() {\n"
                + "        setI(5);\n"
                + "        System.out.println(getI());\n"
                + "    }\n"
                + "\n"
                + "    public int getI() {\n"
                + "        return i;\n"
                + "    }\n"
                + "\n"
                + "    public void setI(int i) {\n"
                + "        this.i = i;\n"
                + "    }\n"
                + "}\n"));
    }

    public void testEncapsulateFieldsReferences() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    public int i;\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B {\n"
                + "    private A a;\n"
                + "    B() {\n"
                + "        a = new A();\n"
                + "    }\n"
                + "\n"
                + "    public void foo() {\n"
                + "        a.i = 5;\n"
                + "        System.out.println(a.i);\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private int i;\n"
                + "\n"
                + "    public int getI() {\n"
                + "        return i;\n"
                + "    }\n"
                + "\n"
                + "    public void setI(int i) {\n"
                + "        this.i = i;\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B {\n"
                + "    private A a;\n"
                + "    B() {\n"
                + "        a = new A();\n"
                + "    }\n"
                + "\n"
                + "    public void foo() {\n"
                + "        a.setI(5);\n"
                + "        System.out.println(a.getI());\n"
                + "    }\n"
                + "}\n"));
    }
    
    public void testEncapsulateFieldsSubclass() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    public int i;\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        i = 5;\n"
                + "        System.out.println(i);\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private int i;\n"
                + "\n"
                + "    public int getI() {\n"
                + "        return i;\n"
                + "    }\n"
                + "\n"
                + "    public void setI(int i) {\n"
                + "        this.i = i;\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        setI(5);\n"
                + "        System.out.println(getI());\n"
                + "    }\n"
                + "}\n"));
    }
    
    @RandomlyFails
    public void testEncapsulateFieldsCompound() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    public int i;\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        (i)++;\n"
                + "        i += 5;\n"
                + "        System.out.println(i);\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private int i;\n"
                + "\n"
                + "    public int getI() {\n"
                + "        return i;\n"
                + "    }\n"
                + "\n"
                + "    public void setI(int i) {\n"
                + "        this.i = i;\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        setI(getI() + 1);\n"
                + "        setI(getI() + 5);\n"
                + "        System.out.println(getI());\n"
                + "    }\n"
                + "}\n"));
    }
    
    @RandomlyFails
    public void testEncapsulateFieldsCompoundByte() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    public byte i;\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        (i)++;\n"
                + "        i += 5;\n"
                + "        System.out.println(i);\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private byte i;\n"
                + "\n"
                + "    public byte getI() {\n"
                + "        return i;\n"
                + "    }\n"
                + "\n"
                + "    public void setI(byte i) {\n"
                + "        this.i = i;\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        setI((byte) (getI() + 1));\n"
                + "        setI((byte) (getI() + 5));\n"
                + "        System.out.println(getI());\n"
                + "    }\n"
                + "}\n"));
    }
    
    public void testEncapsulateFieldsThisSuper() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    public int i;\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        super.i++;\n"
                + "        this.i += 5;\n"
                + "        System.out.println(i);\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private int i;\n"
                + "\n"
                + "    public int getI() {\n"
                + "        return i;\n"
                + "    }\n"
                + "\n"
                + "    public void setI(int i) {\n"
                + "        this.i = i;\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "public class B extends A {\n"
                + "\n"
                + "    public void foo() {\n"
                + "        super.setI(super.getI() + 1);\n"
                + "        this.setI(this.getI() + 5);\n"
                + "        System.out.println(getI());\n"
                + "    }\n"
                + "}\n"));
    }
    
    /**
     * TODO: Test for issue 108473. The issue was closed, but the case still fails.
     */
    public void FAILtest108473() throws Exception {
        writeFilesAndWaitForScan(src, new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private String theField;\n"
                + "    public static void main(String[] args) {\n"
                + "        B b = new A().new B();\n"
                + "        System.out.println(b.getTheField());\n"
                + "    }\n"
                + "\n"
                + "    private class B extends A {\n"
                + "        private String getTheField() {\n"
                + "            return theField;\n"
                + "        }\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0}, new Problem(false, "WRN_OverriddenGetter"));
        verifyContent(src, new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private String theField;\n"
                + "    public static void main(String[] args) {\n"
                + "        B b = new A().new B();\n"
                + "        System.out.println(b.getTheField());\n"
                + "    }\n"
                + "\n"
                + "    public String getTheField() {\n"
                + "        return theField;\n"
                + "    }\n"
                + "    public void setTheField(String theField) {\n"
                + "        this.theField = theField;\n"
                + "    }\n"
                + "    private class B extends A {\n"
                + "        private String getTheField() {\n"
                + "            return getTheField();\n"
                + "        }\n"
                + "    }\n"
                + "}\n"));
    }

    /**
     * TODO: Test for issue #108489. The issue was closed, but the case still fails.
     */
    public void FAILtest108489() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    String f;\n"
                + "    public static void main(String[] args) {\n"
                + "        B b=new B();\n"
                + "        b.setF(\"abcd\");\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "class B extends A {\n"
                + "    public int setF(  String theField){\n"
                + "        this.f=theField;\n"
                + "        return 2;\n"
                + "    }\n"
                + "}\n"));
        performEncapsulate(src.getFileObject("encap/A.java"), new int[]{0});
        verifyContent(src,
                new File("encap/A.java", "package encap;\n"
                + "public class A {\n"
                + "    private String f;\n"
                + "    public static void main(String[] args) {\n"
                + "        B b=new B();\n"
                + "        b.setF(\"abcd\");\n"
                + "    }\n"
                + "    public String getF() {\n"
                + "        return f;\n"
                + "    }\n"
                + "    public void setF(String f) {\n"
                + "        this.f = f;\n"
                + "    }\n"
                + "}\n"),
                new File("encap/B.java", "package encap;\n"
                + "class B extends A {\n"
                + "    public int setF(String theField){\n"
                + "        super.setF(theField);\n"
                + "        return 2;\n"
                + "    }\n"
                + "}\n"));
    }

    private void performEncapsulate(FileObject source, final int[] position, Problem... expectedProblems) throws IOException, IllegalArgumentException, InterruptedException {
        final EncapsulateFieldsRefactoring[] r = new EncapsulateFieldsRefactoring[1];
        JavaSource.forFileObject(source).runUserActionTask(new Task<CompilationController>() {

            @Override
            public void run(CompilationController info) throws Exception {
                info.toPhase(JavaSource.Phase.RESOLVED);
                CompilationUnitTree cut = info.getCompilationUnit();

                final ClassTree classTree = (ClassTree) cut.getTypeDecls().get(0);
                final TreePath classPath = info.getTrees().getPath(cut, classTree);
                TypeElement classEl = (TypeElement) info.getTrees().getElement(classPath);
                List<? extends Element> allMembers = info.getElements().getAllMembers(classEl);
                List<VariableElement> fieldsIn = ElementFilter.fieldsIn(allMembers);
                LinkedList<EncapsulateFieldsRefactoring.EncapsulateFieldInfo> fields = new LinkedList<EncapsulateFieldsRefactoring.EncapsulateFieldInfo>();
                for (int p : position) {
                    VariableElement field = fieldsIn.get(p);
                    String getName = EncapsulateFieldRefactoringPlugin.computeGetterName(field);
                    String setName = EncapsulateFieldRefactoringPlugin.computeSetterName(field);
                    EncapsulateFieldsRefactoring.EncapsulateFieldInfo encInfo = new EncapsulateFieldsRefactoring.EncapsulateFieldInfo(TreePathHandle.create(field, info), getName, setName);
                    fields.add(encInfo);
                }
                r[0] = new EncapsulateFieldsRefactoring(TreePathHandle.create(classEl, info));
                r[0].setAlwaysUseAccessors(true);
                r[0].setRefactorFields(fields);
                r[0].setFieldModifiers(EnumSet.of(Modifier.PRIVATE));
                r[0].setMethodModifiers(EnumSet.of(Modifier.PUBLIC));
            }
        }, true);

        RefactoringSession rs = RefactoringSession.create("Session");
        List<Problem> problems = new LinkedList<Problem>();

        addAllProblems(problems, r[0].preCheck());
        if (!problemIsFatal(problems)) {
            Thread.sleep(1000);
            addAllProblems(problems, r[0].prepare(rs));
        }
        if (!problemIsFatal(problems)) {
            addAllProblems(problems, rs.doRefactoring(true));
        }

        assertProblems(Arrays.asList(expectedProblems), problems);
    }
}