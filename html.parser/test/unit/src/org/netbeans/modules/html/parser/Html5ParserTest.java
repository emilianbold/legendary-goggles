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

package org.netbeans.modules.html.parser;

import java.io.IOException;
import org.netbeans.editor.ext.html.parser.api.SyntaxAnalyzer;
import org.netbeans.editor.ext.html.parser.api.AstNode;
import org.netbeans.editor.ext.html.parser.api.AstNodeUtils;
import org.netbeans.editor.ext.html.parser.api.HtmlSource;
import org.netbeans.editor.ext.html.parser.api.ParseException;
import org.netbeans.junit.NbTestCase;
import org.xml.sax.SAXException;

/**
 *
 * @author marekfukala
 */
public class Html5ParserTest extends NbTestCase {

    public Html5ParserTest(String name) {
        super(name);
//        AstNodeTreeBuilder.DEBUG = true;
    }

//        String code = "<!DOCTYPE html><body xmlns:f=\"http://sun.com/jsf/core\"></p><section><f:xx></f:xx><p>cau<p>  <p>ahoj</section><section><div></div></section></body>";
//        String code = "<!DOCTYPE html></p><section><p>cau<p>  <p>ahoj</section><section><div></div></section>";
//        String code = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><section><p id=\"my\">cau<p><p class=\"klasa\">ahoj</section>";
//        String code = "<!DOCTYPE html><html xmlns:myns=\"http://marek.org/\"><myns:section><p id=\"my\">cau<p><p class=\"klasa\">ahoj</myns:section></html>";

//        AstNodeUtils.dumpTree(root);

    
    public void testBasic() throws SAXException, IOException, ParseException {
        AstNode root = parse("<!doctype html><section><div></div></section>");
        assertNotNull(root);
        assertNotNull(AstNodeUtils.query(root, "html/body/section/div")); //html/body are generated
    }

    
    public void testHtmlAndBodyTags() throws ParseException {
        AstNode root = parse("<!DOCTYPE html><html><head><title>hello</title></head><body><div>ahoj</div></body></html>");
        assertNotNull(root);
        assertNotNull(AstNodeUtils.query(root, "html"));
        assertNotNull(AstNodeUtils.query(root, "html/head"));
        assertNotNull(AstNodeUtils.query(root, "html/head/title"));
        assertNotNull(AstNodeUtils.query(root, "html/body"));
        assertNotNull(AstNodeUtils.query(root, "html/body/div"));

    }

    private AstNode parse(CharSequence code) throws ParseException {
        HtmlSource source = new HtmlSource(code);
        AstNode root = SyntaxAnalyzer.create(source).analyze().parseHtml().root();

        assertNotNull(root);

        return root;
    }

}