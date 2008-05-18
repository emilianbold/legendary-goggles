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

package org.netbeans.modules.groovy.editor.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.gsf.api.Error;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.groovy.editor.lexer.GroovyTokenId;
import org.netbeans.modules.groovy.editor.parser.GroovyParser;
import org.netbeans.modules.gsf.GsfTestCompilationInfo;
import org.netbeans.modules.gsf.GsfTestCompilationInfo.GsfTestParseListener;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.netbeans.modules.gsf.spi.DefaultParserFile;
import org.openide.filesystems.FileObject;

/**
 *
 * @author tor
 * @author Martin Adamek
 */
public class TestCompilationInfo extends GsfTestCompilationInfo {
    public TestCompilationInfo(GroovyTestBase test, FileObject fileObject, BaseDocument doc, String text) throws IOException {
        super(test, fileObject, doc, text);
    }
    
    @Override
    public ParserResult getEmbeddedResult(String embeddedMimeType, int offset) {
        assert embeddedMimeType.equals(GroovyTokenId.GROOVY_MIME_TYPE);
        
        if (embeddedResults.size() == 0) {
            GsfTestParseListener listener = new GsfTestParseListener();
            List<ParserFile> sourceFiles = new ArrayList<ParserFile>(1);
            ParserFile file = new DefaultParserFile(getFileObject(), null, false);
            sourceFiles.add(file);
            
TranslatedSource translatedSource = null; // TODO            
            GroovyParser.Context context = new GroovyParser.Context(file, listener, text, caretOffset);
            GroovyParser parser = new GroovyParser();
            ParserResult parserResult = parser.parseBuffer(context, GroovyParser.Sanitize.NONE);
            for (Error error : listener.getErrors()) {
                parserResult.addError(error);
            }
            embeddedResults.put(GroovyTokenId.GROOVY_MIME_TYPE, parserResult);
            parserResult.setInfo(this);
        }
        
        return embeddedResults.get(embeddedMimeType);
    }

    public String getPreferredMimeType() {
        return GroovyTokenId.GROOVY_MIME_TYPE;
    }
}
