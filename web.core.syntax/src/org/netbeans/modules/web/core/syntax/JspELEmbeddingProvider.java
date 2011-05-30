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

package org.netbeans.modules.web.core.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.jsp.lexer.JspTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.web.common.api.Constants;

/**
 *
 * An Expression Language EmbeddingProvider for text/x-jsp and text/x-tag mimetypes
 *
 * @author mfukala@netbeans.org
 */
final class JspELEmbeddingProvider extends EmbeddingProvider {

    private static final String ATTRIBUTE_EL_MARKER = "A"; //NOI18N
    
    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {
        TokenHierarchy<?> th = snapshot.getTokenHierarchy();
        TokenSequence<JspTokenId> sequence = th.tokenSequence(JspTokenId.language());
        List<Embedding> embeddings = new ArrayList<Embedding>();
        sequence.moveStart();
        boolean inAttributeValueWithEL = false;
        while (sequence.moveNext()) {
            Token t = sequence.token();
            if (t.id() == JspTokenId.ATTR_VALUE && t.length() == 1 && 
                    (t.text().charAt(0) == '"' || t.text().charAt(0) == '\'')) {
                //a quote before/after attribute value with EL inside
                inAttributeValueWithEL = !inAttributeValueWithEL;
            }
            if (t.id() == JspTokenId.EL) {
                embeddings.add(snapshot.create(sequence.offset(), t.length(), "text/x-el")); //NOI18N
                //XXX hack - there's a need to distinguish between ELs inside or outside of attribute values
                if(inAttributeValueWithEL) {
                    embeddings.add(snapshot.create(ATTRIBUTE_EL_MARKER, "text/x-el")); //NOI18N
                }
                
                // just to separate expressions for easier handling in EL parser
                embeddings.add(snapshot.create(Constants.LANGUAGE_SNIPPET_SEPARATOR, "text/x-el")); //NOI18N
             
            }
        }
        if (embeddings.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(Embedding.create(embeddings));
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public void cancel() {
    }

     public static final class Factory extends TaskFactory {

        @Override
        public Collection<SchedulerTask> create(final Snapshot snapshot) {
            return Arrays.<SchedulerTask>asList(new JspELEmbeddingProvider());
        }
    }
}
