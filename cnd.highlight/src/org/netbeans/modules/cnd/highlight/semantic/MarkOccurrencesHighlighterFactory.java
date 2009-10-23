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
package org.netbeans.modules.cnd.highlight.semantic;

import javax.swing.text.Document;
import org.netbeans.modules.cnd.highlight.semantic.options.SemanticHighlightingOptions;
import org.netbeans.modules.cnd.model.tasks.CaretAwareCsmFileTaskFactory;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Sergey Grinev
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.cnd.model.tasks.CsmFileTaskFactory.class, position=10)
public class MarkOccurrencesHighlighterFactory extends CaretAwareCsmFileTaskFactory {

    @Override
    protected PhaseRunner createTask(final FileObject fo) {
        MarkOccurrencesHighlighter ph = null;
        if (enabled()) {
            try {
                DataObject dobj = DataObject.find(fo);
                EditorCookie ec = dobj.getCookie(EditorCookie.class);
                Document doc = ec.getDocument();
                if (doc != null) {
                    ph = new MarkOccurrencesHighlighter(doc);
                }
            } catch (DataObjectNotFoundException ex) {
                // file object or data object can be already invalid
                // Exceptions.printStackTrace(ex);
            }
        }
        return ph != null ? ph : new PhaseRunner() {

            private boolean valid = true;

            public void run(Phase phase) {
                valid = !enabled();
                // rest
            }

            public boolean isValid() {
                return valid;
            }
            
            public void cancel() {
                valid = !enabled();
            }

            public boolean isHighPriority() {
                return false;
            }
        };
    }
    
    private static boolean enabled() {
        return SemanticHighlightingOptions.instance().getEnableMarkOccurrences()
                &&!HighlighterBase.MINIMAL;
    }

    @Override
    protected int taskDelay() {
        return ModelUtils.OCCURRENCES_DELAY;
    }

    @Override
    protected int rescheduleDelay() {
        return ModelUtils.RESCHEDULE_OCCURRENCES_DELAY;
    }
}
