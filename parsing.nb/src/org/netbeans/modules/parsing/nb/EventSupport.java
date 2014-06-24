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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 *//*
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.nb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyListener;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.indexing.IndexingManager;
import org.netbeans.modules.parsing.impl.indexing.IndexingManagerAccessor;
import org.netbeans.modules.parsing.implspi.SchedulerControl;
import org.netbeans.modules.parsing.implspi.SourceControl;
import org.netbeans.modules.parsing.implspi.SourceEnvironment;
import org.netbeans.modules.parsing.implspi.TaskProcessorControl;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.UserQuestionException;
import org.openide.util.WeakListeners;


/**
 *
 * @author Tomas Zezula
 */
final class EventSupport extends SourceEnvironment {
    
    private static final Logger LOGGER = Logger.getLogger(EventSupport.class.getName());
    private static final RequestProcessor RP = new RequestProcessor ("parsing-event-collector",1, false, false);       //NOI18N
    /** Default reparse - sliding window for editor events*/
    private static final int DEFAULT_REPARSE_DELAY = 500;
    /** Default reparse - sliding window for focus events*/
    private static final int IMMEDIATE_REPARSE_DELAY = 10;

    private static int reparseDelay = DEFAULT_REPARSE_DELAY;
    private static int immediateReparseDelay = IMMEDIATE_REPARSE_DELAY;

    private DocListener docListener;
    private DataObjectListener dobjListener;

    private static final EditorRegistryListener editorRegistryListener  = new EditorRegistryListener();
    
    public EventSupport (final SourceControl sourceControl) {
        super(sourceControl);
    }

    @Override
    public Document readDocument(FileObject fileObject, boolean forceOpen) {
        EditorCookie ec = null;
        
        try {
            DataObject dataObject = DataObject.find (fileObject);
            ec = dataObject.getLookup ().lookup (EditorCookie.class);
        } catch (DataObjectNotFoundException ex) {
            //DataobjectNotFoundException may happen in case of deleting opened file
            //handled by returning null
        }

        if (ec == null) return null;
        Document doc = ec.getDocument ();
        if (doc == null && forceOpen) {
            try {
                try {
                    doc = ec.openDocument ();
                } catch (UserQuestionException uqe) {
                    uqe.confirmed ();
                    doc = ec.openDocument ();
                }
            } catch (IOException ioe) {
                LOGGER.log (Level.WARNING, null, ioe);
            }
        }
        return doc;
    }

    @Override
    public void activate() {
        final Source source = getSourceControl().getSource();
        final FileObject fo = source.getFileObject();
        Document doc;
        if (fo != null) {
            try {
                listenOnFileChanges();
                listenOnParser();
                DataObject dObj = DataObject.find(fo);
                assignDocumentListener (dObj);
                dobjListener = new DataObjectListener(dObj);
            } catch (DataObjectNotFoundException e) {
                LOGGER.log(Level.WARNING, "Ignoring events non existent file: {0}", FileUtil.getFileDisplayName(fo));     //NOI18N
            }
        } else if ((doc=source.getDocument(false)) != null) {
            listenOnParser();
            docListener = new DocListener (doc);
        }
    }

    @Override
    public boolean isReparseBlocked() {
        return EditorRegistryListener.k24.get();
    }

    private void resetState (
        final boolean           invalidate,
        final boolean           mimeChanged,
        final int               startOffset,
        final int               endOffset,
        final boolean           fast) {
        if (invalidate) {
            if (startOffset == -1 || endOffset == -1) {
                getSourceControl().sourceChanged(mimeChanged);
            } else {
                getSourceControl().regionChanged(startOffset, endOffset);
            }
        } else {
            getSourceControl().stateChanged();
        }
        getSourceControl().revalidate(getReparseDelay(fast));
    }

    /**
     * Expert: Called by {@link IndexingManager#refreshIndexAndWait} to prevent
     * AWT deadlock. Never call this method in other cases.
     */
    public static void releaseCompletionCondition() {
        if (!IndexingManagerAccessor.getInstance().requiresReleaseOfCompletionLock() ||
            !IndexingManagerAccessor.getInstance().isCalledFromRefreshIndexAndWait()) {
            throw new IllegalStateException();
        }
        final boolean wask24 = EditorRegistryListener.k24.getAndSet(false);
        if (wask24) {
            TaskProcessorControl.resumeSchedulerTasks();
        }
    }

    /**
     * Sets the reparse delays.
     * Used by unit tests.
     */
    public static void setReparseDelays(
        final int standardReparseDelay,
        final int fastReparseDelay) throws IllegalArgumentException {
        if (standardReparseDelay < fastReparseDelay) {
            throw new IllegalArgumentException(
                    String.format(
                        "Fast reparse delay %d > standatd reparse delay %d",    //NOI18N
                        fastReparseDelay,
                        standardReparseDelay));
        }
        immediateReparseDelay = fastReparseDelay;
        reparseDelay = standardReparseDelay;
    }

    public static int getReparseDelay(final boolean fast) {
        return fast ? immediateReparseDelay : reparseDelay;
    }
    // <editor-fold defaultstate="collapsed" desc="Private implementation">

    private void assignDocumentListener(final DataObject od) {
        EditorCookie.Observable ec = od.getCookie(EditorCookie.Observable.class);
        if (ec != null) {
            docListener = new DocListener (ec);
        }
    }    
    
    private class DocListener implements PropertyChangeListener, DocumentListener, TokenHierarchyListener {
        
        private final EditorCookie.Observable ec;
        private DocumentListener docListener;
        private TokenHierarchyListener thListener;
        
        @SuppressWarnings("LeakingThisInConstructor")
        public DocListener (final EditorCookie.Observable ec) {
            assert ec != null;
            this.ec = ec;
            this.ec.addPropertyChangeListener(WeakListeners.propertyChange(this, this.ec));
            final Source source = getSourceControl().getSource();
            assert source != null;
            final Document doc = source.getDocument(false);
            if (doc != null) {
                assignDocumentListener(doc);
            }
        }
        
        public DocListener(final Document doc) {
            assert doc != null;
            this.ec = null;
            assignDocumentListener(doc);
        }                

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (EditorCookie.Observable.PROP_DOCUMENT.equals(evt.getPropertyName())) {
                Object old = evt.getOldValue();                
                if (old instanceof Document && docListener != null) {
                    Document doc = (Document) old;
                    TokenHierarchy th = TokenHierarchy.get(doc);
                    th.removeTokenHierarchyListener(thListener);
                    doc.removeDocumentListener(docListener);
                    thListener = null;
                    docListener = null;
                }                
                Source source = getSourceControl().getSource();
                if (source == null) {
                    return;
                }
                Document doc = source.getDocument(false);
                if (doc != null) {
                    assignDocumentListener(doc);
                    resetState(true, false, -1, -1, false);
                }                
            }
        }
        
        private void assignDocumentListener(final Document doc) {
            TokenHierarchy th = TokenHierarchy.get(doc);
            th.addTokenHierarchyListener(thListener = WeakListeners.create(TokenHierarchyListener.class, this,th));
            doc.addDocumentListener(docListener = WeakListeners.create(DocumentListener.class, this, doc));
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            TokenHierarchy th = TokenHierarchy.get(e.getDocument());
            if (th.isActive()) return ;//handled by the lexer based listener
            resetState (true, false, e.getOffset(), e.getOffset() + e.getLength(), false);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            TokenHierarchy th = TokenHierarchy.get(e.getDocument());
            if (th.isActive()) return;//handled by the lexer based listener
            resetState (true, false, e.getOffset(), e.getOffset(), false);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}

        @Override
        public void tokenHierarchyChanged(TokenHierarchyEvent evt) {
            resetState (true, false, evt.affectedStartOffset(), evt.affectedEndOffset(), false);
        }
    }
    
    private final class DataObjectListener implements PropertyChangeListener {
                     
        private DataObject dobj;
        private final FileObject fobj;
        private PropertyChangeListener wlistener;
        
        @SuppressWarnings("LeakingThisInConstructor")
        public DataObjectListener(final DataObject dobj) {            
            this.dobj = dobj;
            this.fobj = dobj.getPrimaryFile();
            wlistener = WeakListeners.propertyChange(this, dobj);
            this.dobj.addPropertyChangeListener(wlistener);
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            DataObject invalidDO = (DataObject) pce.getSource();
            if (invalidDO != dobj)
                return;
            final String propName = pce.getPropertyName();
            if (DataObject.PROP_VALID.equals(propName)) {
                handleInvalidDataObject(invalidDO);
            } else if (pce.getPropertyName() == null && !dobj.isValid()) {
                handleInvalidDataObject(invalidDO);
            }            
        }
        
        private void handleInvalidDataObject(final DataObject invalidDO) {
            RP.post(new Runnable() {
                @Override
                public void run() {
                    handleInvalidDataObjectImpl(invalidDO);
                }
            });
        }
        
        private void handleInvalidDataObjectImpl(DataObject invalidDO) {
            invalidDO.removePropertyChangeListener(wlistener);
            if (fobj.isValid()) {
                // file object still exists try to find new data object
                try {
                    DataObject dobjNew = DataObject.find(fobj);
                    synchronized (DataObjectListener.this) {
                        if (dobjNew == dobj) {
                            return;
                        }
                        dobj = dobjNew;
                        dobj.addPropertyChangeListener(wlistener);
                    }
                    assignDocumentListener(dobjNew);
                    resetState(true, false, -1, -1, false);
                } catch (DataObjectNotFoundException e) {
                    //Ignore - invalidated after fobj.isValid () was called
                } catch (IOException ex) {
                    // should not occur
                    Exceptions.printStackTrace(ex);
                }
            }
        }        
    }

    //Public because of test
    public static class EditorRegistryListener implements CaretListener, PropertyChangeListener {

        private static final AtomicBoolean k24 = new AtomicBoolean();
                        
        private Reference<JTextComponent> lastEditorRef;
        
        private EditorRegistryListener () {
            EditorRegistry.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    editorRegistryChanged();
                }
            });
            editorRegistryChanged();
        }
                
        private void editorRegistryChanged() {
            final JTextComponent editor = EditorRegistry.lastFocusedComponent();
            final JTextComponent lastEditor = lastEditorRef == null ? null : lastEditorRef.get();
            if (lastEditor != editor && (editor == null || editor.getClientProperty("AsTextField") == null)) {
                if (lastEditor != null) {
                    lastEditor.removeCaretListener(this);
                    lastEditor.removePropertyChangeListener(this);
                    k24.set(false);
                }
                lastEditorRef = new WeakReference<JTextComponent>(editor);
                if (editor != null) {
                    editor.addCaretListener(this);
                    editor.addPropertyChangeListener(this);
                }
                final JTextComponent focused = EditorRegistry.focusedComponent();
                if (focused != null) {
                    final Document doc = editor.getDocument();
                    final String mimeType = DocumentUtilities.getMimeType (doc);
                    if (doc != null && mimeType != null) {
                        final Source source = Source.create (doc);
                        if (source != null) {
                            ((EventSupport)SourceEnvironment.forSource(source)).resetState(true, false, -1, -1, true);
                        }
                    }
                }
            }
        }
        
        @Override
        public void caretUpdate(final CaretEvent event) {
            final JTextComponent lastEditor = lastEditorRef == null ? null : lastEditorRef.get();
            if (lastEditor != null) {
                Document doc = lastEditor.getDocument ();
                String mimeType = DocumentUtilities.getMimeType (doc);
                if (doc != null && mimeType != null) {
                    Source source = Source.create(doc);
                    if (source != null) {
                        ((EventSupport)SourceEnvironment.forSource(source)).resetState(false, false, -1, -1, false);
                    }
                }
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if ("completion-active".equals(propName)) { //NOI18N
                Source source = null;
                final JTextComponent lastEditor = lastEditorRef == null ? null : lastEditorRef.get();
                final Document doc = lastEditor == null ? null : lastEditor.getDocument();
                if (doc != null) {
                    String mimeType = DocumentUtilities.getMimeType (doc);
                    if (mimeType != null) {
                        source = Source.create(doc);
                    }
                }
                if (source != null) {
                    handleCompletionActive(source, evt.getNewValue());
                }
            }
        }

        private void handleCompletionActive(
                final @NonNull Source source,
                final @NullAllowed Object rawValue) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "completion-active={0} for {1}", new Object [] { rawValue, source }); //NOI18N
            }
            if (rawValue instanceof Boolean && ((Boolean) rawValue).booleanValue()) {
                k24.set(true);
                TaskProcessorControl.suspendSchedulerTasks(source);
            } else {
                k24.set(false);
                final EventSupport support = (EventSupport) SourceEnvironment.forSource(source);
                support.getSourceControl().revalidate(0);
            }
        }
    }

    // </editor-fold>

    /**
     * Each scheduler may be mapped to a DObj Pchange listener. When primary file
     * changes, the scheduler fires a its tasks with the Source object created
     * for the new file.
     */
    private final static Map<Scheduler, SchedL> scheduledSources = new HashMap<>(7);
    
    private static class SchedL implements PropertyChangeListener {
        /**
         * The controlled scheduler
         */
        SchedulerControl               control;
        
        /**
         * The current source attached to the scheduler
         */
        Source                      source;
        
        /**
         * WeakListener last in effect
         */
        PropertyChangeListener      weakListener;

        public SchedL(SchedulerControl control) {
            this.control = control;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_PRIMARY_FILE.equals(evt.getPropertyName())) {
                final DataObject dobj = (DataObject) evt.getSource();
                final Source newSource = Source.create(dobj.getPrimaryFile());
                if (newSource != null) {
                    LOGGER.log(
                        Level.FINE,
                        "Rescheduling {0} due to change of primary file.",  //NOI18N
                        dobj.getPrimaryFile());
                    
                    control.sourceChanged(newSource);
                }
            }
        }
        
        public synchronized void attachSource(Source s, boolean attach) {
            if (source != null) {
                assert (attach || source == s) && weakListener != null;
                final FileObject fo = source.getFileObject();
                if (fo != null) {
                    try {
                        final DataObject dobj = DataObject.find(fo);
                        dobj.removePropertyChangeListener(weakListener);
                    } catch (DataObjectNotFoundException nfe) {
                        //No DataObject for file - ignore
                    }
                }
                weakListener = null;
                this.source = null;
            }
            if (attach) {
                final FileObject fo = s.getFileObject();
                if (fo != null) {
                    try {
                        final DataObject dobj = DataObject.find(fo);
                        weakListener = WeakListeners.propertyChange(this, dobj);
                        dobj.addPropertyChangeListener(weakListener);
                    } catch (DataObjectNotFoundException ex) {
                        //No DataObject for file - ignore
                    }
                }
                this.source = s;
            }
        }
    }
    
    @Override
    public void attachScheduler(SchedulerControl s, boolean attach) {
        SchedL l;
        Source now = getSourceControl().getSource();
        
        synchronized (scheduledSources) {
            Scheduler sched = s.getScheduler();
             l = scheduledSources.get(sched);
            if (attach && l == null) {
                l = new SchedL(s);
                scheduledSources.put(sched, l);
            }
        }
        if (l != null) {
            l.attachSource(now, attach);
        }
    }
}
