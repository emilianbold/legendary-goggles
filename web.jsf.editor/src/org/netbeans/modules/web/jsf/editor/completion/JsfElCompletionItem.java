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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.web.jsf.editor.completion;

import javax.swing.ImageIcon;
import org.netbeans.modules.web.core.syntax.completion.api.ElCompletionItem;
import org.netbeans.modules.web.core.syntax.completion.api.JspCompletionItem;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Petr Pisl
 * @author Marek Fukala
 */
public class JsfElCompletionItem {

    public static class JsfBean extends ElCompletionItem.ELBean {

        private static final String BEAN_PATH = "org/netbeans/modules/web/jsf/editor/jspel/resources/jsf_bean_16.png";  //NOI18N

        public JsfBean(String text, int substitutionOffset, String type) {
            super(text, substitutionOffset, type);
        }

        @Override
        public int getSortPriority() {
            return 5;
        }

        @Override
        protected ImageIcon getIcon() {
            return ImageUtilities.loadImageIcon(BEAN_PATH, false);
        }
    }

    public static class JsfMethod extends ElCompletionItem.ELBean {

        private static final String METHOD_PATH = "org/netbeans/modules/web/jsf/editor/jspel/resources/method_16.png";      //NOI18N

        public JsfMethod(String text, int substitutionOffset, String type) {
            super(text, substitutionOffset, type);
        }

        @Override
        protected ImageIcon getIcon() {
            return ImageUtilities.loadImageIcon(METHOD_PATH, false);
        }
    }

    public static class JsfResourceBundle extends ElCompletionItem.ELBean {

        private static final String BUNDLE_ICON_PATH = "org/netbeans/modules/web/jsf/editor/jspel/resources/propertiesLocale.gif";  //NOI18N

        public JsfResourceBundle(String text, int substitutionOffset, String type) {
            super(text, substitutionOffset, type);
        }

        public int getSortPriority() {
            return 10;
        }

        @Override
        protected ImageIcon getIcon() {
            return ImageUtilities.loadImageIcon(BUNDLE_ICON_PATH, false);
        }
    }

    public static class JsfResourceItem extends JspCompletionItem {

        private static final String BUNDLE_ICON_PATH = "org/netbeans/modules/web/jsf/editor/jspel/resources/propertiesKey.gif";  //NOI18N

        public JsfResourceItem(String text, int substitutionOffset, String type) {
            this(text, text, substitutionOffset, type);
        }
        
        public JsfResourceItem(String text, String insertText , 
                int substitutionOffset, String type) 
        {
            super(text, substitutionOffset, type);
            myInsertText = insertText;
        }
        
        /* (non-Javadoc)
         * @see org.netbeans.modules.web.core.syntax.completion.api.JspCompletionItem#getSubstituteText()
         */
        @Override
        protected String getSubstituteText() {
            return myInsertText;
        }

        @Override
        protected ImageIcon getIcon() {
            return ImageUtilities.loadImageIcon(BUNDLE_ICON_PATH, false);
        }
        
        private String myInsertText;
    }
}
