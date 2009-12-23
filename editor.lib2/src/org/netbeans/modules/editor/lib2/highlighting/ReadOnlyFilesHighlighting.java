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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.editor.lib2.highlighting;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;
import org.openide.filesystems.FileObject;

/**
 *
 * @author vita
 */
public final class ReadOnlyFilesHighlighting extends AbstractHighlightsContainer {

    public static final String LAYER_TYPE_ID = "org.netbeans.modules.editor.lib2.highlighting.ReadOnlyFilesHighlighting"; //NOI18N

    public ReadOnlyFilesHighlighting(Document doc) {
        this.document = doc;
        FontColorSettings fcs = MimeLookup.getLookup(MimePath.EMPTY).lookup(FontColorSettings.class);
        if (fcs != null) {
            AttributeSet readOnlyFilesColoring = fcs.getFontColors("readonly-files"); //NOI18N
            if (readOnlyFilesColoring != null) {
                this.attribs = AttributesUtilities.createImmutable(
                        readOnlyFilesColoring,
                        AttributesUtilities.createImmutable(ATTR_EXTENDS_EMPTY_LINE, Boolean.TRUE, ATTR_EXTENDS_EOL, Boolean.TRUE));
            } else {
                this.attribs = null;
            }
        } else {
            this.attribs = null;
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("~~~ this=" + s2s(this) + ", doc=" + s2s(doc) + ", file=" + fileFromDoc(doc) //NOI18N
                    + ", attribs=" + attribs + (attribs != null ? ", bg=" + attribs.getAttribute(StyleConstants.Background) : "")); //NOI18N
        }
    }
    
    @Override
    public HighlightsSequence getHighlights(int startOffset, int endOffset) {
        FileObject file = fileFromDoc(document);
        if (attribs != null && file != null) {
            if (!file.canWrite()) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Highlighting file " + file + " in <" + startOffset + ", " + endOffset + ">"); //NOI18N
                }
                return new CaretBasedBlockHighlighting.SimpleHighlightsSequence(
                    Math.max(0, startOffset),
                    Math.min(document.getLength(), endOffset),
                    attribs);
            }
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("No highlights for file " + file + ", doc=" + s2s(document)); //NOI18N
        }

        return HighlightsSequence.EMPTY;
    }

    // -----------------------------------------------------------------------
    // Private implementation
    // -----------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(ReadOnlyFilesHighlighting.class.getName());
    
    private final Document document;
    private final AttributeSet attribs;

    private static String s2s(Object o) {
        return o == null ? "null" : o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o)); //NOI18N
    }

    private static FileObject fileFromDoc(Document d) {
        Object streamDescription = d.getProperty(Document.StreamDescriptionProperty);
        if (d instanceof FileObject) {
            return (FileObject) d;
        } else if (streamDescription != null) {
            try {
                Method m = streamDescription.getClass().getMethod("getPrimaryFile"); //NOI18N
                return (FileObject) m.invoke(streamDescription);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }
}
