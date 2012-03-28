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

package org.netbeans.lib.editor.bookmarks.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.Document;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.bookmarks.BookmarkAPIAccessor;
import org.netbeans.modules.editor.bookmarks.BookmarkInfo;
import org.netbeans.modules.editor.bookmarks.BookmarksPersistence;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.util.NbBundle;


/**
 * Interface to a bookmark.
 *
 * @author Miloslav Metelka
 */

public final class Bookmark {
    
    static {
        BookmarkAPIAccessor.INSTANCE = new BookmarkAPIAccessorImpl();
    }

    public static final String BOOKMARK_ANNOTATION_TYPE = "editor-bookmark"; // NOI18N

    // cary mary fuk!
    private static Map<Line,Reference<AAnnotation>> lineToAnnotation = new WeakHashMap<Line,Reference<AAnnotation>> (); // Hanziii;)

    /**
     * Bookmark list to which this bookmark belongs.
     */
    private BookmarkList    bookmarkList;

    /**
     * Whether this mark was released or not.
     */
    private boolean         released;
    
    private final BookmarkInfo info; // Hold name and key

    private Line            line;
    private AAnnotation     annotation;
    private Map<BookmarkList,LineListener>
                            bookmarkListToLineListener = new WeakHashMap<BookmarkList,LineListener> ();
    
    /**
     * Construct new instance of bookmark.
     *
     * <p>
     * The constructor is not public intentionally.
     * Please see <code>BookmarksApiPackageAccessor</code> for details.
     */
    Bookmark (BookmarkList bookmarkList, BookmarkInfo info, int offset) {
        if (info == null) {
            throw new IllegalArgumentException("info cannot be null"); // NOI18N
        }
        this.bookmarkList = bookmarkList;
        this.info = info;
        Document document = bookmarkList.getDocument ();
        int lineIndex = BookmarksPersistence.offset2LineIndex(document, offset);
        DataObject dataObject = NbEditorUtilities.getDataObject (document);
        for (Line _line : lineToAnnotation.keySet ()) {
            if (_line.getLineNumber () == lineIndex &&
                _line.getLookup().lookup (DataObject.class).equals (dataObject)
            ) {
                this.line = _line;
                Reference<AAnnotation> annoRef = lineToAnnotation.get (_line);
                this.annotation = annoRef.get();
                if (this.annotation != null) {
                    return;
                }
            }
        }
        line = NbEditorUtilities.getLine (bookmarkList.getDocument (), offset, false);
        if (line != null) { // In tests it may be null
            annotation = new AAnnotation ();
            lineToAnnotation.put (line, new WeakReference<AAnnotation>(annotation));
            annotation.attach (line);
            LineListener lineListener = bookmarkListToLineListener.get (bookmarkList);
            if (lineListener == null) {
                lineListener = new LineListener (bookmarkList);
                bookmarkListToLineListener.put (bookmarkList, lineListener);
            }
            line.addPropertyChangeListener (lineListener);
        }
    }

    /**
     * Bookmark name may be used to identify bookmark in a bookmark manager.
     * <br/>
     * All its characters satisfy {@link Character#isJavaIdentifierPart(char) }.
     * <br/>
     * Since bookmarks are stored on a per-project manner the bookmark names may be duplicate
     * across projects and there is no restriction to have unique names even
     * within a single project.
     */
    /*public*/ String getName() {
        return info.getName();
    }
    
    /**
     * Get offset of this bookmark.
     * <br>
     * Offsets behave like {@link javax.swing.text.Position}s (they track
     * inserts/removals).
     */
    public int getOffset () {
        return BookmarksPersistence.lineIndex2Offset(bookmarkList.getDocument(), line.getLineNumber());
    }

    /**
     * Get zero-based index of line at which this bookmark resides.
     */
    public int getLineNumber () {
        return line.getLineNumber ();
    }
    
    /**
     * Current implementation returns a single char [0-9a-z] used for jumping
     * to the bookmark by a keystroke in a Goto dialog or an empty string
     * when no shortcut was assigned yet.
     * <br/>
     * Non-single char values are reserved for future use (current code ignores them).
     * <br/>
     * Since bookmarks are stored on a per-project manner the bookmark keys may be duplicate
     * across projects and there is no restriction to have unique keys even
     * within a single project. In case of conflict an arbitrary mark with the given key is chosen.
     */
    public String getKey() {
        return info.getKey();
    }

    /**
     * Get the bookmark list for which this bookmark was created.
     */
    public BookmarkList getList() {
        return bookmarkList;
    }
    
    /**
     * Return true if this mark was released (removed from its bookmark list)
     * and is no longer actively used.
     */
    public boolean isReleased() {
        return released;
    }
    
    /**
     * Mark the current bookmark as invalid.
     */
    void release () {
        assert (!released);
        released = true;
        annotation.detach ();
        lineToAnnotation.remove (line);
    }
    
    BookmarkInfo info() {
        return info;
    }
    
    
    // innerclasses ............................................................
    
    public final class AAnnotation extends Annotation {

        public String getAnnotationType () {
            return BOOKMARK_ANNOTATION_TYPE;
        }

        public String getShortDescription () {
            String fmt = NbBundle.getBundle (Bookmark.class).getString ("Bookmark_Tooltip"); // NOI18N
            int lineIndex = getLineNumber ();
            return MessageFormat.format (fmt, new Object[] {new Integer (lineIndex + 1)});
        }

        public String toString() {
            return getShortDescription();
        }
    }

    private static class LineListener implements PropertyChangeListener {

        private WeakReference<BookmarkList> bookmarkListReference;

        LineListener (BookmarkList bookmarkList) {
            bookmarkListReference = new WeakReference<BookmarkList> (bookmarkList);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            BookmarkList bookmarkList = bookmarkListReference.get ();
            if (bookmarkList == null)
                return;
            List<Bookmark> bookmarks = new ArrayList<Bookmark> (bookmarkList.getBookmarks ());
            int lineNumber = -1;
            for (Bookmark bookmark : bookmarks) {
                if (bookmark.getLineNumber () == lineNumber) {
                    bookmarkList.removeBookmark (bookmark);
                }
                lineNumber = bookmark.getLineNumber ();
            }
        }
    };

    private static final class BookmarkAPIAccessorImpl extends BookmarkAPIAccessor {

        @Override
        public BookmarkInfo getInfo(Bookmark b) {
            return b.info();
        }
        
    }
    
}

