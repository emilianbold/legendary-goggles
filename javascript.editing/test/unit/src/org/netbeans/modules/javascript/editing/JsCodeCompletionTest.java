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

package org.netbeans.modules.javascript.editing;

import javax.swing.JTextArea;
import javax.swing.text.Caret;
import org.netbeans.modules.gsf.api.Completable.QueryType;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.GsfTestCompilationInfo;
import org.netbeans.modules.gsf.api.Completable;

/**
 *
 * @author Tor Norbye
 */
public class JsCodeCompletionTest extends JsTestBase {
    
    public JsCodeCompletionTest(String testName) {
        super(testName);
    }
    
    @Override
    protected Completable getCodeCompleter() {
        return new JsCodeCompletion();
    }
    
    @Override
    protected void checkCall(GsfTestCompilationInfo info, int caretOffset, String param, boolean expectSuccess) {
        IndexedFunction[] methodHolder = new IndexedFunction[1];
        int[] paramIndexHolder = new int[1];
        int[] anchorOffsetHolder = new int[1];
        int lexOffset = caretOffset;
        int astOffset = caretOffset;
        boolean ok = JsCodeCompletion.computeMethodCall(info, lexOffset, astOffset, methodHolder, paramIndexHolder, anchorOffsetHolder, null);

        if (expectSuccess) {
            assertTrue(ok);
        } else {
            return;
        }
        IndexedFunction method = methodHolder[0];
        assertNotNull(method);
        int index = paramIndexHolder[0];
        assertTrue(index >= 0);
        
        // The index doesn't work right at test time - not sure why
        // it doesn't have all of the gems...
        //assertEquals(fqn, method.getFqn());
        assertEquals(param, method.getParameters().get(index));
    }
    
    public void testPrefix1() throws Exception {
        checkPrefix("testfiles/cc-prefix1.js");
    }
    
    public void testPrefix2() throws Exception {
        checkPrefix("testfiles/cc-prefix2.js");
    }

    public void testPrefix3() throws Exception {
        checkPrefix("testfiles/cc-prefix3.js");
    }

    public void testPrefix4() throws Exception {
        checkPrefix("testfiles/cc-prefix4.js");
    }

    public void testPrefix5() throws Exception {
        checkPrefix("testfiles/cc-prefix5.js");
    }

    public void testPrefix6() throws Exception {
        checkPrefix("testfiles/cc-prefix6.js");
    }

    public void testPrefix7() throws Exception {
        checkPrefix("testfiles/cc-prefix7.js");
    }

    public void testPrefix8() throws Exception {
        checkPrefix("testfiles/cc-prefix8.js");
    }
    
    
    private void assertAutoQuery(QueryType queryType, String source, String typedText) {
        JsCodeCompletion completer = new JsCodeCompletion();
        int caretPos = source.indexOf('^');
        source = source.substring(0, caretPos) + source.substring(caretPos+1);
        
        BaseDocument doc = getDocument(source);
        JTextArea ta = new JTextArea(doc);
        Caret caret = ta.getCaret();
        caret.setDot(caretPos);
        
        QueryType qt = completer.getAutoQuery(ta, typedText);
        assertEquals(queryType, qt);
    }

    public void testAutoQuery1() throws Exception {
        assertAutoQuery(QueryType.NONE, "foo^", "o");
        assertAutoQuery(QueryType.NONE, "foo^", " ");
        assertAutoQuery(QueryType.NONE, "foo^", "c");
        assertAutoQuery(QueryType.NONE, "foo^", "d");
        assertAutoQuery(QueryType.NONE, "foo^", "f");
        assertAutoQuery(QueryType.NONE, "Foo:^", ":");
        assertAutoQuery(QueryType.NONE, "Foo::^", ":");
        assertAutoQuery(QueryType.NONE, "Foo^ ", ":");
        assertAutoQuery(QueryType.NONE, "Foo^bar", ":");
        assertAutoQuery(QueryType.NONE, "Foo:^bar", ":");
        assertAutoQuery(QueryType.NONE, "Foo::^bar", ":");
    }

    public void testAutoQuery2() throws Exception {
        assertAutoQuery(QueryType.STOP, "foo^", ";");
        assertAutoQuery(QueryType.STOP, "foo^", "[");
        assertAutoQuery(QueryType.STOP, "foo^", "(");
        assertAutoQuery(QueryType.STOP, "foo^", "{");
        assertAutoQuery(QueryType.STOP, "foo^", "\n");
    }

    public void testAutoQuery3() throws Exception {
        assertAutoQuery(QueryType.COMPLETION, "foo.^", ".");
        assertAutoQuery(QueryType.COMPLETION, "foo^ ", ".");
        assertAutoQuery(QueryType.COMPLETION, "foo^bar", ".");
    }

    public void testAutoQueryComments() throws Exception {
        assertAutoQuery(QueryType.COMPLETION, "foo^ # bar", ".");
        assertAutoQuery(QueryType.NONE, "//^foo", ".");
        assertAutoQuery(QueryType.NONE, "/* foo^*/", ".");
        assertAutoQuery(QueryType.NONE, "// foo^", ".");
    }

    public void testAutoQueryStrings() throws Exception {
        assertAutoQuery(QueryType.COMPLETION, "foo^ 'foo'", ".");
        assertAutoQuery(QueryType.NONE, "'^foo'", ".");
        assertAutoQuery(QueryType.NONE, "/f^oo/", ".");
        assertAutoQuery(QueryType.NONE, "\"^\"", ".");
        assertAutoQuery(QueryType.NONE, "\" foo^ \"", ".");
    }

//    public void testAutoQueryRanges() throws Exception {
//        assertAutoQuery(QueryType.NONE, "x..^", ".");
//        assertAutoQuery(QueryType.NONE, "x..^5", ".");
//    }

//    public void testCompletion1() throws Exception {
//        checkCompletion("testfiles/completion/lib/test1.js", "f.e^");
//    }
//    
//    public void testCompletion2() throws Exception {
//        // This test doesn't pass yet because we need to index the -current- file
//        // before resuming
//        checkCompletion("testfiles/completion/lib/test2.js", "Result is #{@^myfield} and #@another.");
//    }
//    
//    public void testCompletion3() throws Exception {
//        checkCompletion("testfiles/completion/lib/test2.js", "Result is #{@myfield} and #@a^nother.");
//    }
//    

    public void testLocalCompletion1() throws Exception {
        checkCompletion("testfiles/completion/lib/test2.js", "^alert('foo1", false);
    }

    public void testLocalCompletion2() throws Exception {
        checkCompletion("testfiles/completion/lib/test2.js", "^alert('foo2", false);
    }
    
    public void test129036() throws Exception {
        checkCompletion("testfiles/completion/lib/test129036.js", "my^ //Foo", false);
    }
    
    public void testCompletionStringCompletion1() throws Exception {
        checkCompletion("testfiles/completion/lib/test1.js", "Hell^o World", false);
    }

    public void testCompletionStringCompletion2() throws Exception {
        checkCompletion("testfiles/completion/lib/test1.js", "\"f\\^oo\"", false);
    }
    
    public void testCompletionRegexpCompletion1() throws Exception {
        checkCompletion("testfiles/completion/lib/test1.js", "/re^g/", false);
    }

    public void testCompletionRegexpCompletion2() throws Exception {
        checkCompletion("testfiles/completion/lib/test1.js", "/b\\^ar/", false);
    }

    public void testExpression1() throws Exception {
        checkCompletion("testfiles/completion/lib/expressions.js", "^escape", false);
    }

    public void testExpressions2() throws Exception {
        checkCompletion("testfiles/completion/lib/expressions.js", "^toE", false);
    }

    public void testExpressions2b() throws Exception {
        checkCompletion("testfiles/completion/lib/expressions2.js", "ownerDocument.^", false);
    }

    public void testExpressions3() throws Exception {
        checkCompletion("testfiles/completion/lib/expressions3.js", "specified.^", false);
    }

    public void testExpressions4() throws Exception {
        checkCompletion("testfiles/completion/lib/expressions4.js", "document.b^", false);
    }

    public void testExpressions5() throws Exception {
        checkCompletion("testfiles/completion/lib/expressions5.js", "dur.^t", false);
    }

    public void testComments1() throws Exception {
        checkCompletion("testfiles/completion/lib/comments.js", "@^param", false);
    }

    public void testComments2() throws Exception {
        checkCompletion("testfiles/completion/lib/comments.js", "@p^aram", false);
    }

    public void testComments3() throws Exception {
        checkCompletion("testfiles/completion/lib/comments.js", "^@param", false);
    }

    public void testComments4() throws Exception {
        checkCompletion("testfiles/completion/lib/comments.js", "T^his", false);
    }

    public void testNewCompletionEol() throws Exception {
        checkCompletion("testfiles/completion/lib/newcompletion.js", "new ^", false);
    }

    public void testYahoo() throws Exception {
        checkCompletion("testfiles/completion/lib/yahoo.js", "e^ // complete on editor members etc", true);
    }
    
    public void testParameterCompletion1() throws Exception {
        checkCompletion("testfiles/completion/lib/configcalls.js", "somefunction({}, {^}, {});", false);
    }

    public void testParameterCompletion2() throws Exception {
        checkCompletion("testfiles/completion/lib/configcalls.js", "somefunction({}, {}, {^});", false);
    }

    // This test is unstable for some reason
    //    public void testParameterCompletion3() throws Exception {
    //        checkCompletion("testfiles/completion/lib/configcalls.js", "somefunction({^}, {}, {});", false);
    //    }

    public void testParameterCompletion4() throws Exception {
        checkCompletion("testfiles/completion/lib/configcalls.js", "somefunctio^n({}, {}, {});", false);
    }

    public void testParameterCompletion5() throws Exception {
        checkCompletion("testfiles/completion/lib/configcalls.js", "somefunction({}, {f^:1}, {});", false);
    }
    
    public void testDeprecatedProperties() throws Exception {
        checkCompletion("testfiles/completion/lib/domproperties.js", ".s^", true);
    }

    // TODO: Test open classes, class inheritance, relative symbols, finding classes, superclasses, def completion, ...

// The call tests don't work yet because I don't have a preindexed database for jsstubs
// (and the test infrastructure refuses to update the index for test files themselves)
//    public void testCall1() throws Exception {
//        //checkComputeMethodCall("testfiles/calls/call1.js", "foo2(^x);", "Foo#bar", "name", true);
//        checkComputeMethodCall("testfiles/calls/call1.js", "x.addEventListener(type, ^listener, useCapture)", "Foo#bar", "listener", true);
//    }
//
//    public void testCall2() throws Exception {
//        checkComputeMethodCall("testfiles/calls/call1.js", "foo1(^);",
//                "Foo#bar", "name", true);
//    }
//
//    public void testCall3() throws Exception {
//        checkComputeMethodCall("testfiles/calls/call1.js", "foo3(x^,y)",
//                "Foo#bar", "name", false);
//    }
//    public void testCall4() throws Exception {
//        checkComputeMethodCall("testfiles/calls/call2.js", "foo3(x,^)",
//                "Foo#bar", "name", false);
//    }
}
