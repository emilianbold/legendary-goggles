/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.editor.BaseDocument;
import static org.netbeans.modules.csl.api.test.CslTestBase.getCaretOffset;
import org.netbeans.modules.editor.bracesmatching.api.BracesMatchingTestUtils;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.spi.editor.bracesmatching.BraceContext;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 * Test for PHPBracesMatcher
 *
 * @author Marek Slama
 */
public class PHPBracesMatcherTest extends PHPTestBase {

    private static final String TEST_DIRECTORY = "testfiles/bracematching/"; //NOI18N

    public PHPBracesMatcherTest(String testName) {
        super(testName);
    }

    /**
     * Test for BracesMatcher, first ^ gives current caret position,
     * second ^ gives matching caret position. Test is done in forward and backward direction.
     */
    private void match2(String original) throws BadLocationException {
        super.assertMatches2(wrapAsPhp(original));
    }

    private static String wrapAsPhp(String s) {
        // XXX: remove \n
        return "<?php\n" + s + "\n?>";
    }

    public void testFindMatching2() throws Exception {
        match2("x=^(true^)\ny=5");
    }

    public void testFindMatching3() throws Exception {
        match2("x=^(true || (false)^)\ny=5");
    }


    public void testIssue164495_01() throws Exception {
        match2("foreach ^($q['answers'] as $a^)\n{\n $tag=\"{value_$a[id]}\";\n}");
    }

    public void testIssue164495_02() throws Exception {
        match2("foreach ($q^['answers'^] as $a)\n{\n $tag=\"{value_$a[id]}\";\n}");
    }

    public void testIssue164495_03() throws Exception {
        match2("foreach ($q['answers'] as $a)\n^{\n $tag=\"{value_$a[id]}\";\n^}");
    }


    public void testIssue197709_01() throws Exception {
        match2("if (true) ^{\n"
                + "    echo \"Some string with braced ${variables[ $index ]} in it.\";\n"
                + "^}");
    }

    public void testIssue197709_02() throws Exception {
        match2("if (true) {\n"
                + "    echo \"Some string with braced ^${variables[ $index ]^} in it.\";\n"
                + "}");
    }

    public void testAlternativeSyntax_01() throws Exception {
        match2(
                "if ($i == 0) :\n"
                + "    if ($j == 0) :\n"
                + "    endif;\n"
                + "elseif ($i == 1)^:\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else:\n"
                + "        $l = 22;\n"
                + "    endif;\n"
                + "^endif;\n"
                + "\n");
    }

    public void testAlternativeSyntax_02() throws Exception {
        match2(
                "if ($i == 0) :\n"
                + "    if ($j == 0) ^:\n"
                + "    ^endif;\n"
                + "elseif ($i == 1):\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else:\n"
                + "        $l = 22;\n"
                + "    endif;\n"
                + "endif;\n");
    }

    public void testAlternativeSyntax_03() throws Exception {
        match2(   "for ($i = 0; $i < count($array); $i++) ^:\n"
                + "    for ($i = 0; $i < count($array); $i++) :\n"
                + "    endfor;\n"
                + "^endfor;\n");
    }

    public void testAlternativeSyntax_04() throws Exception {
        match2(   "for ($i = 0; $i < count($array); $i++) :\n"
                + "    for ($i = 0; $i < count($array); $i++) ^:\n"
                + "    ^endfor;\n"
                + "endfor;\n");
    }

    public void testAlternativeSyntax_05() throws Exception {
        match2(   "while (true)^:\n"
                + "    while(false):\n"
                + "        if ($a == 1):\n"
                + "\n            "
                + "        endif;\n"
                + "    endwhile;\n"
                + "^endwhile;\n");
    }

    public void testAlternativeSyntax_06() throws Exception {
        match2(   "while (true):\n"
                + "    while(false)^:\n"
                + "        if ($a == 1):\n"
                + "\n            "
                + "        endif;\n"
                + "    ^endwhile;\n"
                + "endwhile;\n");
    }

    public void testAlternativeSyntax_07() throws Exception {
        match2(   "switch ($i)^:\n"
                + "    case 22:\n"
                + "        $i = 44;\n"
                + "        break;\n"
                + "    case 33:\n"
                + "    case 44:\n"
                + "        $i = 55;\n"
                + "        break;\n"
                + "    default:\n"
                + "        $i = 66;\n"
                + "^endswitch;\n");
    }

    public void testIssue240157_01() throws Exception {
        matchesBackward("if (isSomething@(^~)){}");
        matchesForward("if (isSomething~(^@)){}");
    }

    public void testIssue240157_02() throws Exception {
        matchesBackward("if (isSomething~(@)^){}");
        matchesForward("if ~(isSomething~()^@){}");
    }

    public void testIssue240157_03() throws Exception {
        matchesBackward("if ~(isSomething()@)^{}");
        matchesForward("if (isSomething())^@{~}");
    }

    public void testIssue240157_04() throws Exception {
        matchesBackward("if (isSomething())@{^~}");
        matchesForward("if (isSomething())~{^@}");
    }

    public void testIssue240157_05() throws Exception {
        matchesBackward("if (isSomething())~{@}^");
    }

    public void testIssue240157_06() throws Exception {
        matchesBackward("if (isSomething^@(~)){}");
        matchesForward("if (isSomething^@(~)){}");
    }

    public void testIssue240157_07() throws Exception {
        matchesBackward("if @(^isSomething()~){}");
    }

    public void testIssue240157_08() throws Exception {
        matchesBackward("if @^(isSomething()~){}");
        matchesForward("if @^(isSomething()~){}");
    }

    public void testIssue240157_09() throws Exception {
        matchesBackward("if (isSomething()) ^@{~}");
        matchesForward("if (isSomething()) ^@{~}");
    }

    public void testIssue240157_10() throws Exception {
        matchesBackward("echo \"Some string with braced @${^variables[ $index ]~} in it.\";");
    }

    public void testIssue240157_11() throws Exception {
        matchesBackward("echo \"Some string with braced ${variables~[ $index @]^} in it.\";");
    }

    public void testIssue240157_AlternativeSyntax_01() throws Exception {
        matchesBackward(
                "if ($i == 0) @:^\n"
                + "    if ($j == 0) :\n"
                + "    endif;\n"
                + "~elseif ($i == 1):\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else:\n"
                + "        $l = 22;\n"
                + "    endif;\n"
                + "endif;\n"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_02() throws Exception {
        matchesBackward(
                "if ($i == 0) ~:\n"
                + "    if ($j == 0) :\n"
                + "    endif;\n"
                + "@elseif^ ($i == 1):\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else:\n"
                + "        $l = 22;\n"
                + "    endif;\n"
                + "endif;\n"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_03() throws Exception {
        matchesBackward(
                "if ($i == 0) :\n"
                + "    if ($j == 0) :\n"
                + "    endif;\n"
                + "elseif ($i == 1):\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else~:\n"
                + "        $l = 22;\n"
                + "    @endif^;\n"
                + "endif;\n"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_04() throws Exception {
        matchesBackward(
                "if ($i == 0) ^@:\n"
                + "    if ($j == 0) :\n"
                + "    endif;\n"
                + "~elseif ($i == 1):\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else:\n"
                + "        $l = 22;\n"
                + "    endif;\n"
                + "endif;\n"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_05() throws Exception {
        matchesBackward(
                "if ($i == 0) :\n"
                + "    if ($j == 0) :\n"
                + "    endif;\n"
                + "elseif ($i == 1)~:\n"
                + "    if ($j == 2):\n"
                + "        $l = 33;\n"
                + "    else:\n"
                + "        $l = 22;\n"
                + "    endif;\n"
                + "@en^dif;\n"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_06() throws Exception {
        matchesBackward(
                "for ($i = 0; $i < count($array); $i++) ~:\n"
                + "    for ($i = 0; $i < count($array); $i++) :\n"
                + "    endfor;\n"
                + "@endfor^;"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_07() throws Exception {
        matchesBackward(
                "for ($i = 0; $i < count($array); $i++) :\n"
                + "    for ($i = 0; $i < count($array); $i++) ~:\n"
                + "    ^@endfor;\n"
                + "endfor;"
                + "\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_08() throws Exception {
        matchesBackward(
                "while (true)~:\n"
                + "    while(false):\n"
                + "        if ($a == 1):\n"
                + "\n            "
                + "        endif;\n"
                + "    endwhile;\n"
                + "@endwhile^;\n");
    }

    public void testIssue240157_AlternativeSyntax_09() throws Exception {
        matchesBackward(
                "while (true)~:\n"
                + "    while(false):\n"
                + "        if ($a == 1):\n"
                + "\n            "
                + "        endif;\n"
                + "    endwhile;\n"
                + "^@endwhile;\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_10() throws Exception {
        matchesBackward(
                "switch ($i)~:\n"
                + "    case 22:\n"
                + "        $i = 44;\n"
                + "        break;\n"
                + "    case 33:\n"
                + "    case 44:\n"
                + "        $i = 55;\n"
                + "        break;\n"
                + "    default:\n"
                + "        $i = 66;\n"
                + "@endswitch^;\n"
        );
    }

    public void testIssue240157_AlternativeSyntax_11() throws Exception {
        matchesBackward(
                "switch ($i)~:\n"
                + "    case 22:\n"
                + "        $i = 44;\n"
                + "        break;\n"
                + "    case 33:\n"
                + "    case 44:\n"
                + "        $i = 55;\n"
                + "        break;\n"
                + "    default:\n"
                + "        $i = 66;\n"
                + "^@endswitch;\n"
        );
    }

    public void testFindContext_01() throws Exception {
        checkBraceContext("braceContextTest.php", "^} elseif ($i == 1) { // if", true);
    }

    public void testFindContext_02() throws Exception {
        checkBraceContext("braceContextTest.php", "^} else { // elseif", true);
    }

    public void testFindContext_03() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // else", true);
    }

    public void testFindContext_04() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // if2", true);
    }

    public void testFindContext_05() throws Exception {
        checkBraceContext("braceContextTest.php", "^elseif ($i == 1) : // alternative if", true);
    }

    public void testFindContext_06() throws Exception {
        checkBraceContext("braceContextTest.php", "^else : // alternative elseif", true);
    }

    public void testFindContext_07() throws Exception {
        checkBraceContext("braceContextTest.php", "^endif; // alternative else", true);
    }

    public void testFindContext_08() throws Exception {
        checkBraceContext("braceContextTest.php", "^else : // alternative nested if", true);
    }

    public void testFindContext_09() throws Exception {
        checkBraceContext("braceContextTest.php", "^endif; // alternative nested else", true);
    }

    public void testFindContext_10() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // while", true);
    }

    public void testFindContext_11() throws Exception {
        checkBraceContext("braceContextTest.php", "^endwhile; // alternative while", true);
    }

    public void testFindContext_12() throws Exception {
        checkBraceContext("braceContextTest.php", "^} while (true); // do", true);
    }

    public void testFindContext_13() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // for", true);
    }

    public void testFindContext_14() throws Exception {
        checkBraceContext("braceContextTest.php", "^endfor; // alternative for", true);
    }

    public void testFindContext_15() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // foreach", true);
    }

    public void testFindContext_16() throws Exception {
        checkBraceContext("braceContextTest.php", "^endforeach; // alternative foreach", true);
    }

    public void testFindContext_17() throws Exception {
        checkBraceContext("braceContextTest.php", "^endswitch; // alternative switch", true);
    }

    public void testFindContext_18() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // foo method", true);
    }

    public void testFindContext_19() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // Foo class", true);
    }

    public void testFindContext_20() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // bar method", true);
    }

    public void testFindContext_21() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // Bar class", true);
    }

    public void testFindContext_22() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // FooInterface", true);
    }

    public void testFindContext_23() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // AbstractClass", true);
    }

    public void testFindContext_24() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // FooTrait", true);
    }

    public void testFindContext_25() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // complex syntax", true);
    }

    public void testFindContext_26() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // try", true);
    }

    public void testFindContext_27() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // catch", true);
    }

    public void testFindContext_28() throws Exception {
        checkBraceContext("braceContextTest.php", "^} // finally", true);
    }

    public void testFindContext_29() throws Exception {
        checkBraceContext("braceContextUseTraitTest.php", "^} // use", true);
    }

    private void matchesBackward(String original) throws BadLocationException {
        matches(original, true);
    }

    private void matchesForward(String original) throws BadLocationException {
        matches(original, false);
    }

    /**
     * "^": a caret position, "@": an origin position, "~": a matching position.
     *
     * @param original code
     * @param backward {@code true} if search backward, otherwise {@code false}
     * @throws BadLocationException
     */
    private void matches(final String original, boolean backward) throws BadLocationException {
        BracesMatcherFactory factory = MimeLookup.getLookup(getPreferredMimeType()).lookup(BracesMatcherFactory.class);
        String wrappedOriginal = wrapAsPhp(original);
        int caretPosition = wrappedOriginal.replaceAll("(@|~)", "").indexOf('^');
        int originPosition = wrappedOriginal.replaceAll("(\\^|~)", "").indexOf('@');
        int matchingPosition = wrappedOriginal.replaceAll("(\\^|@)", "").indexOf('~');
        wrappedOriginal = wrappedOriginal.replaceAll("(\\^|@|~)", "");

        BaseDocument doc = getDocument(wrappedOriginal);

        MatcherContext context = BracesMatchingTestUtils.createMatcherContext(doc, caretPosition, backward, 1);
        BracesMatcher matcher = factory.createMatcher(context);
        int[] origin = null, matches = null;
        try {
            origin = matcher.findOrigin();
            matches = matcher.findMatches();
        } catch (InterruptedException ex) {
        }

        assertNotNull("Did not find origin for " + " position " + originPosition, origin);
        assertNotNull("Did not find matches for " + " position " + matchingPosition, matches);

        assertEquals("Incorrect origin", originPosition, origin[0]);
        assertEquals("Incorrect matches", matchingPosition, matches[0]);
    }

    /**
     * Check BraceContexts. To check brace context reanges, Please wrap them in
     * {@code |>MARK_BC: <|} or {@code |>MARK_RELATED_BC: <|} in a
     * testFileName.testCaseName.bracecontext file.
     *
     * @param filePath Path of the file which is in testfiles/bracemacthing/
     * directory.
     * @param caretLine The text contained in the line which has the caret.
     * @param backward {@code true} if searching barckward in the BraceMatcher
     * class, otherwise {@code false}.
     * @throws Exception
     */
    private void checkBraceContext(String filePath, String caretLine, boolean backward) throws Exception {
        Source testSource = getTestSource(getTestFile(TEST_DIRECTORY + filePath));

        Document doc = testSource.getDocument(true);
        final int caretOffset = getCaretOffset(doc.getText(0, doc.getLength()), caretLine);

        BracesMatcherFactory factory = MimeLookup.getLookup(getPreferredMimeType()).lookup(BracesMatcherFactory.class);
        MatcherContext matcherContext = BracesMatchingTestUtils.createMatcherContext(doc, caretOffset, backward, 1);
        PHPBracesMatcher matcher = (PHPBracesMatcher) factory.createMatcher(matcherContext);
        int[] origin = null, matches = null;
        try {
            origin = matcher.findOrigin();
            matches = matcher.findMatches();
        } catch (InterruptedException ex) {
        }
        assertNotNull(origin);
        assertNotNull(matches);

        BraceContext context = matcher.findContext(origin[0]);
        assertNotNull(context);

        String result = annoteteBraceContextRanges(doc, context);
        assertDescriptionMatches(testSource.getFileObject(), result, true, ".bracecontext", true);
    }

    private String annoteteBraceContextRanges(Document document, final BraceContext context) throws BadLocationException {
        List<BraceContext> relatedContexts = new ArrayList<>();
        BraceContext relatedContext = context.getRelated();
        while (relatedContext != null) {
            relatedContexts.add(relatedContext);
            relatedContext = relatedContext.getRelated();
        }
        Collections.reverse(relatedContexts);

        StringBuilder sb = new StringBuilder();
        int index = 0;
        int contextStart = context.getStart().getOffset();
        int contextEnd = context.getEnd().getOffset();

        // related context
        for (BraceContext related : relatedContexts) {
            int start = related.getStart().getOffset();
            int end = related.getEnd().getOffset();
            sb.append(document.getText(index, start - index));
            sb.append("|>MARK_RELATED_BC:");
            sb.append(document.getText(start, end - start));
            sb.append("<|");
            index = end;
            assertTrue("Related context offset > context offset", end <= contextStart);
        }

        // context
        sb.append(document.getText(index, contextStart - index));
        sb.append("|>MARK_BC:");
        sb.append(document.getText(contextStart, contextEnd - contextStart));
        sb.append("<|");
        index = contextEnd;
        sb.append(document.getText(index, document.getLength() - index));
        return sb.toString();
    }

}
