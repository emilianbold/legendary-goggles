/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.model.impl;

import org.netbeans.modules.css.lib.api.Node;
import org.netbeans.modules.css.model.api.Media;
import org.netbeans.modules.css.model.api.MediaBody;
import org.netbeans.modules.css.model.api.MediaQueryList;
import org.netbeans.modules.css.model.api.Model;
import org.netbeans.modules.css.model.api.PlainElement;
import org.openide.util.CharSequences;

/**
 *
 * @author marekfukala
 */
public class MediaI extends ModelElement implements Media {

    private MediaQueryList mediaQueryList;
    private MediaBody mediaBody;
    
    
    private final ModelElementListener elementListener = new ModelElementListener.Adapter() {

        @Override
        public void elementAdded(MediaQueryList value) {
            mediaQueryList = value;
        }

        @Override
        public void elementAdded(MediaBody value) {
            mediaBody = value;
        }

    };

    public MediaI(Model model) {
        super(model);
        
        addTextElement("@media");
        addTextElement(" ");
        addEmptyElement(MediaQueryList.class);
        addTextElement(" ");
        addTextElement("{");
        addEmptyElement(MediaBody.class);
        addTextElement("}");
    }

    public MediaI(Model model, Node node) {
        super(model, node);
        initChildrenElements();
    }

    @Override
    protected Class getModelClass() {
        return Media.class;
    }

    @Override
    protected ModelElementListener getElementListener() {
        return elementListener;
    }

    @Override
    public MediaQueryList getMediaQueryList() {
        return mediaQueryList;
    }

    @Override
    public void setMediaQueryList(MediaQueryList mediaQueryList) {
        setElement(mediaQueryList);
    }

    @Override
    public MediaBody getMediaBody() {
        return mediaBody;
    }

    @Override
    public void setMediaBody(MediaBody mediaBody) {
        //find the existing curly braces and put the element between them
        for(int i = getElementsCount() - 1; i >= 0; i--) {
            PlainElement e = getElementAt(i, PlainElement.class);
            if(e != null && CharSequences.indexOf(e.getContent(), "}") != -1) {
                //found the closing curly brace, put the mediaBody before it
                insertElement(i, mediaBody);
                return ;
            }
        }
        throw new IllegalStateException("Can't found curly braces in Media element!"); //NOI18N
    }

}
