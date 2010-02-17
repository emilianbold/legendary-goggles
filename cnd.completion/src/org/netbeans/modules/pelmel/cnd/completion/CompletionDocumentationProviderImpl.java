/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.pelmel.cnd.completion;

import java.net.URL;
import javax.swing.Action;
import javax.swing.text.Document;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.completion.cplusplus.ext.CsmResultItem;
import org.netbeans.modules.cnd.completion.spi.dynhelp.CompletionDocumentationProvider;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jan Lahoda
 */
@ServiceProvider(service=CompletionDocumentationProvider.class)
public class CompletionDocumentationProviderImpl implements CompletionDocumentationProvider {

    public CompletionTask createDocumentationTask(CompletionItem item) {
        if (!(item instanceof CsmResultItem)) return null;

        Object assoc = ((CsmResultItem) item).getAssociatedObject();

        if (assoc instanceof CsmObject) {
            return new AsyncCompletionTask(new DocQuery((CsmObject) assoc));
        }
        
        return null;
    }

    private static class DocQuery extends AsyncCompletionQuery {

        private final CsmObject obj;

        public DocQuery(CsmObject obj) {
            this.obj = obj;
        }

        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            CompletionDocumentation documentation = DoxygenDocumentation.create(obj);

            if (documentation == null) {
                documentation = ManDocumentation.getDocumentation(obj);
            }

            if (documentation == null) {
                StringBuilder w = new StringBuilder();

                w.append("<html><body><p>No documentation found.</p>");
                w.append(ManDocumentation.constructWarning(obj));
                documentation = new EmptyCompletionDocumentationImpl(w.toString());
            }

            if (documentation != null)
                resultSet.setDocumentation(documentation);

            resultSet.finish();
        }

    }
    
    private static final class EmptyCompletionDocumentationImpl implements CompletionDocumentation {
        private final String text;

        public EmptyCompletionDocumentationImpl(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public URL getURL() {
            return null;
        }

        public CompletionDocumentation resolveLink(String link) {
            return null;
        }

        public Action getGotoSourceAction() {
            return null;
        }
    }

}
