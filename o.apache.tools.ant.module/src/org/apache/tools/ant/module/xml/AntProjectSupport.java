/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Ant module
 * The Initial Developer of the Original Code is Jayme C. Edwards.
 * Portions created by Jayme C. Edwards are Copyright (c) 2002.
 * All Rights Reserved.
 *
 * Contributor(s): Jesse Glick.
 */
 
package org.apache.tools.ant.module.xml;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import javax.swing.JEditorPane;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledDocument;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.*;
import org.w3c.dom.*;
import org.w3c.dom.events.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.openide.*;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListener;

import org.apache.tools.ant.module.AntModule;
import org.apache.tools.ant.module.api.AntProjectCookie;
import org.openide.filesystems.Repository;

public class AntProjectSupport implements AntProjectCookie.ParseStatus, DocumentListener, FileChangeListener, org.w3c.dom.events.EventListener, Runnable, ChangeListener {
    
    /**
     * XML parser to use.
     * Must match contents of META-INF/services/javax.xml.parsers.DocumentBuilderFactory in xerces.jar.
     * Using JAXP is not enough because we might get Crimson, which won't work (no DOM Events).
     * XMLUtil.parse also does not currently enforce Xerces over Crimson.
     */
    private static final String XERCES_DOCUMENT_BUILDER_FACTORY = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl"; // NOI18N
  
    private File file;
    private FileObject fo;
    private String fsName = null;
    private String fileName = null;

    private transient Document projDoc = null; // [PENDING] SoftReference
    private transient Throwable exception = null;
    private transient boolean parsed = false;
    private transient java.lang.ref.WeakReference styledDocRef = null;
    private transient Object parseLock; // see init()
    /** File should be regenerated whenever this field is false. */
    private transient boolean upToDate = false; // see handleEvent (), run ()

    private transient Set listeners; // see init(); Set<ChangeListener>
    private transient EditorCookie editor = null;
    
    private transient DocumentBuilder documentBuilder;
    
    // milliseconds of quiet time after a textual document change after which
    // changes will be fired and the XML may be reparsed
    private static final long REPARSE_DELAY = 3000;

    // Document key; if corresponding value exists, we are expecting an update
    // to be fired from that document currently, and it should be ignored
    private static final Object expectingDocUpdates = new Object ();

    private static final long serialVersionUID = 7366509989041657663L;
    
    public AntProjectSupport (FileObject fo) {
        this (fo, null);
    }
  
    public AntProjectSupport (File f) {
        this (findFileObject(f), f);
    }
    
    /** try to find a matching FileObject, else null */
    private static FileObject findFileObject(File f) {
        FileObject[] fos = FileUtil.fromFile(f);
        if (fos.length > 0) {
            return fos[0];
        } else {
            return null;
        }
    }
  
    private AntProjectSupport (FileObject fo, File f) {
        this.fo = fo;
        this.file = f;
        init ();
    }

    private void init () {
        parseLock = new Object ();
        listeners = new HashSet ();
    }

    private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
        fsName = null;
        fileName = null;
        in.defaultReadObject ();
        init ();
    }
    
    private void writeObject (ObjectOutputStream out) throws IOException {
        updateFileObject();
        out.defaultWriteObject();
    }

    private synchronized EditorCookie getEditor () {
        FileObject fo = getFileObject ();
        if (fo == null) return null;
                if (editor == null) {
                    try {
                        editor = (EditorCookie) DataObject.find (fo).getCookie (EditorCookie.class);
                        if (editor != null && (editor instanceof CloneableEditorSupport)) {
                            ((CloneableEditorSupport) editor).addChangeListener (WeakListener.change (this, editor));
                        }
                    } catch (DataObjectNotFoundException donfe) {
                        AntModule.err.notify (ErrorManager.INFORMATIONAL, donfe);
                    }
                }
        return editor;
    }
    
    public File getFile () {
        if (file != null) {
            return file;
        } else {
            FileObject fo = getFileObject();
            if (fo != null) {
                return FileUtil.toFile(fo);
            } else {
                return null;
            }
        }
    }
    
    public FileObject getFileObject () {
        if (fo != null && ! fo.isValid ()) { // #11065
            
            // if older version of object was deserialized which does not have 
            // fsName and fileName attributes, then just reset the fileobject and continue
            if (fsName == null && fileName == null) {
                AntModule.err.log ("AntProjectSupport fo=" + fo + " was not valid, clearing");
                fo = null;
                return fo;
            }
            
            // try to resolve fileobject according to fsName and fileName
            // see also #25701
            resolveFileObject();
            if (fo != null && !fo.isValid())
                return null;
                
        }
        return fo;
    }
    
    private void resolveFileObject () {  // #25701
        Repository rep = Repository.getDefault();
        FileSystem fs = rep.findFileSystem (fsName);

        FileObject fobj = null;
        if (fs != null) {
            // scan desired system
            fobj = fs.findResource (fileName);
        }
        if (fobj == null) {
            // scan all systems
            fobj = rep.findResource (fileName);
        }
        if (fobj != null) {
            fo = fobj;
        }
    }
    
    private void updateFileObject() { // #25701
        fsName = null;
        fileName = null;
        if (fo == null)
            return;
        fileName = fo.getPackageNameExt('/','.');
        try {
            fsName = fo.getFileSystem().getSystemName();
        } catch (FileStateInvalidException ex) {
            AntModule.err.notify (ErrorManager.INFORMATIONAL, ex);
            fsName = null;
            fileName = null;
        }
    }
    
    public void setFile (File f) { // #11979
        file = f;
        fo = findFileObject(f);
        invalidate ();
    }
    
    public void setFileObject (FileObject fo) { // #11979
        this.fo = fo;
        file = null; // compute on demand - note that parent folders may change etc.
        invalidate ();
        updateFileObject();
    }
    
    public boolean isParsed() {
        return parsed;
    }
    
    public Document getDocument () {
        if (parsed) {
            return projDoc;
        }
        synchronized (parseLock) {
            if (parsed) {
                return projDoc;
            }
            parseDocument ();
            return projDoc;
        }
    }
    
    public Throwable getParseException () {
        if (parsed) {
            return exception;
        }
        synchronized (parseLock) {
            if (parsed) {
                return exception;
            }
            parseDocument ();
            return exception;
        }
    }
    
    /**
     * Make a DocumentBuilder object for use in this support.
     * Thread-safe, but of course the result is not.
     * @throws Exception for various reasons of configuration
     */
    private static synchronized DocumentBuilder createDocumentBuilder() throws Exception {
        DocumentBuilderFactory factory = (DocumentBuilderFactory)Class.forName(XERCES_DOCUMENT_BUILDER_FACTORY).newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        documentBuilder.setErrorHandler(ErrHandler.DEFAULT);
        return documentBuilder;
    }
    
    /**
     * XML parser error handler; passes on all errors.
     */
    private static final class ErrHandler implements ErrorHandler {
        static final ErrorHandler DEFAULT = new ErrHandler();
        private ErrHandler() {}
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }
    
    private void parseDocument () {
        assert Thread.holdsLock(parseLock); // so it is OK to use documentBuilder
        FileObject fo = getFileObject ();
        AntModule.err.log ("AntProjectSupport.parseDocument: fo=" + fo);
        try {
            Reader rd;
            EditorCookie editor = getEditor ();
            File file = getFile(); // #19705
            if (editor != null) {
                StyledDocument doc = editor.openDocument ();
                rd = new DocumentReader (doc, fo);
                // add only one Listener (listeners for doc are hold in a List!)
                if ((styledDocRef != null && styledDocRef.get () != doc) || styledDocRef == null) {
                    doc.addDocumentListener (this);
                    styledDocRef = new java.lang.ref.WeakReference (doc);
                }
            } else if (fo != null) {
                rd = new InputStreamReader (fo.getInputStream ());
                fo.addFileChangeListener (this);
            } else if (file != null) {
                rd = new FileReader (file);
            } else {
                // [PENDING] this happens sometimes...why?
                exception = new NullPointerException ();
                return;
            }
            try {
                InputSource in = new InputSource (rd);
                if (file != null) { // #10348
                    try {
                        in.setSystemId(file.toURI().toURL().toString());
                    } catch (MalformedURLException mfue) {
                        AntModule.err.notify (ErrorManager.WARNING, mfue);
                    }
                    // [PENDING] Ant's ProjectHelper has an elaborate set of work-
                    // arounds for inconsistent parser behavior, e.g. file:foo.xml
                    // works in Ant but not with Xerces parser. You must use just foo.xml
                    // as the system ID. If necessary, Ant's algorithm could be copied
                    // here to make the behavior match perfectly, but it ought not be necessary.
                }
                if (documentBuilder == null) {
                    documentBuilder = createDocumentBuilder();
                }
                Document doc = documentBuilder.parse(in);
                if (editor != null) {
                    //AntModule.err.log ("doc=" + doc);
                    // Xerces DOM parser implements DOM event model.
                    EventTarget targ = (EventTarget) doc;
                    targ.addEventListener ("DOMSubtreeModified", this, false); // NOI18N
                    // Normal bubbling mutation events:
                    //targ.addEventListener ("DOMNodeInserted", this, false); // NOI18N
                    //targ.addEventListener ("DOMNodeRemoved", this, false); // NOI18N
                    // See comment in ElementNode:
                    targ.addEventListener ("DOMAttrModified", this, false); // NOI18N
                    //targ.addEventListener ("DOMCharacterDataModified", this, false); // NOI18N
                }
                projDoc = doc;
                exception = null;
            } finally {
                rd.close ();
            }
        } catch (Exception e) {
            // leave projDoc the way it is...
            exception = e;
            if (!(exception instanceof SAXParseException)) {
                AntModule.err.annotate(exception, ErrorManager.UNKNOWN, "Strange parse error in " + this, null, null, null); // NOI18N
                AntModule.err.notify(ErrorManager.INFORMATIONAL, exception);
            }
        }
        fireChangeEvent();
        parsed = true;
    }
    
    public Element getProjectElement () {
        Document doc = getDocument ();
        if (doc != null) {
            return doc.getDocumentElement ();
        } else {
            return null;
        }
    }
    
    public boolean equals (Object o) {
        if (! (o instanceof AntProjectSupport)) return false;
        AntProjectSupport other = (AntProjectSupport) o;
        if (fo != null && other.fo != null) {
            return fo.equals (other.fo);
        } else if (file != null && other.file != null) {
            return file.equals (other.file);
        } else {
            return false;
        }
    }
    
    public int hashCode () {
        return 27825 ^ (fo == null ? (file == null ? 0 : file.hashCode()) : fo.hashCode());
    }
    
    public String toString () {
        FileObject fo = getFileObject ();
        if (fo != null) {
            return fo.getNameExt();
        } else if (file != null) {
            return file.getAbsolutePath ();
        } else {
            return "<missing Ant script>"; // NOI18N
        }
    }
    
    private synchronized void regenerate () {
        FileObject fo = getFileObject ();
        AntModule.err.log("AntProjectSupport.regenerate: fo=" + fo);
        if (projDoc == null) throw new IllegalStateException ();
        try {
            EditorCookie editor = getEditor ();
            if (editor != null) {
                StyledDocument doc = editor.openDocument ();
                // Gack. What a mess. Have to regenerate whole document.
                // XXX replace with XMLUtil.write when possible....
                // but that method is not well suited to regenerating hand-edited text
                // because it sets indentation, etc., which clobbers whitespace changes
                JEditorPane[] panes = editor.getOpenedPanes (); // #11738
                int[] carets = new int[(panes == null) ? 0 : panes.length];
                for (int i = 0; i < carets.length; i++) {
                    carets[i] = panes[i].getCaretPosition ();
                }
                OutputFormat format = new OutputFormat (projDoc);
                format.setPreserveSpace (true);
                Writer wr = new DocumentWriter (doc, fo);
                try {
                    XMLSerializer ser = new XMLSerializer (wr, format);
                    ser.serialize (projDoc);
                    // Apache serializer also fails to include trailing newline, sigh.
                    wr.write ('\n');
                } finally {
                    wr.close ();
                }
                for (int i = 0; i < carets.length; i++) {
                    if (carets[i] < doc.getLength ()) {
                        try {
                            panes[i].setCaretPosition (carets[i]);
                        } catch (IllegalArgumentException iae) {
                            AntModule.err.notify (ErrorManager.INFORMATIONAL, iae);
                        }
                    }
                }
                exception = null;
                parsed = true;
            }
        } catch (IOException ioe) {
            exception = ioe;
        }
        // Tell listeners the document is now different:
        fireChangeEvent ();
    }
  
    public void addChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.add (l);
        }
    }
    
    public void removeChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.remove (l);
        }
    }
    
    protected void fireChangeEvent () {
        AntModule.err.log ("AntProjectSupport.fireChangeEvent: fo=" + fo);
        Iterator it;
        synchronized (listeners) {
            it = new HashSet (listeners).iterator ();
        }
        ChangeEvent ev = new ChangeEvent (this);
        RequestProcessor.postRequest (new ChangeFirer (it, ev));
    }
    private static final class ChangeFirer implements Runnable {
        private final Iterator it; // Iterator<ChangeListener>
        private final ChangeEvent ev;
        public ChangeFirer (Iterator it, ChangeEvent ev) {
            this.it = it;
            this.ev = ev;
        }
        public void run () {
            AntModule.err.log ("AntProjectSupport.ChangeFirer.run");
            while (it.hasNext ()) {
                ChangeListener l = (ChangeListener) it.next ();
                try {
                    l.stateChanged (ev);
                } catch (RuntimeException re) {
                    AntModule.err.notify (re);
                }
            }
        }
    }
    
    public void removeUpdate (javax.swing.event.DocumentEvent ev) {
        if (ev.getDocument ().getProperty (expectingDocUpdates) == null) {
            invalidate ();
        }
    }
    
    public void changedUpdate (javax.swing.event.DocumentEvent ev) {
        // Not to worry, just text attributes or something...
    }
    
    public void insertUpdate (javax.swing.event.DocumentEvent ev) {
        if (ev.getDocument ().getProperty (expectingDocUpdates) == null) {
            invalidate ();
        }
    }
    
    // Called when editor support changes state: #11616
    public void stateChanged (ChangeEvent changeEvent) {
        invalidate ();
    }
    
    public void fileDeleted (org.openide.filesystems.FileEvent p1) {
        // Hmm, not our problem.
    }
    
    public void fileDataCreated (org.openide.filesystems.FileEvent p1) {
        // ignore
    }
    
    public void fileFolderCreated (org.openide.filesystems.FileEvent p1) {
        // ignore
    }
    
    public void fileRenamed (org.openide.filesystems.FileRenameEvent p1) {
        // ignore
    }
    
    public void fileAttributeChanged (org.openide.filesystems.FileAttributeEvent p1) {
        // ignore
    }
    
    public void handleEvent (org.w3c.dom.events.Event ev) {
        AntModule.err.log ("AntProjectSupport.handleEvent: fo=" + fo);
        //Thread.dumpStack ();
        AntModule.err.log("\tev=" + ev);
        AntModule.err.log("\tev.type=" + ev.getType ());
        AntModule.err.log("\tev.target=" + ev.getTarget ());
        // Make sure we regenerate from the same DOM tree that is current!
        if (exception != null || ev.getCurrentTarget () != projDoc) {
            // Attempt to modify stale DOM tree -> ignore it and return.
            // Ideally would cancel ev, but DOM2 does not support
            // cancelling mutation events, so just give up.
            AntModule.err.log (ErrorManager.WARNING, "AntProjectSupport.handleEvent on stale DOM tree");
            return;
        }
        // Parser fires too many events: Attribute change causes a DOMAttrModified
        // and a DOMSubtreeModified to be fired, etc. try to filter out unneeded
        // regenerations. we cannot do more. (see Issue#: 12880)
        upToDate = false;
        RequestProcessor.postRequest (this);
    }
    
    public void run () {
        if (! upToDate) {
            upToDate = true;
            regenerate ();
        }
    }
    
    public void fileChanged (org.openide.filesystems.FileEvent p1) {
        invalidate ();
    }
    
    private static class DocumentReader extends PipedReader implements Runnable {
        private StyledDocument doc;
        private PipedWriter wr;
        public DocumentReader (StyledDocument doc, FileObject fo) throws IOException {
            this.doc = doc;
            wr = new PipedWriter ();
            connect (wr);
            new Thread (this, "ant DocumentReader: " + fo).start (); // NOI18N
        }
        public void run () {
            try {
                doc.render (new Runnable () {
                        public void run () {
                            try {
                                new DefaultEditorKit ().write (wr, doc, 0, doc.getLength ());
                            } catch (IOException e) {
                                AntModule.err.notify (ErrorManager.INFORMATIONAL, e);
                            } catch (BadLocationException e) {
                                AntModule.err.notify (ErrorManager.INFORMATIONAL, e);
                            }
                        }
                    });
            } finally {
                try {
                    wr.close ();
                } catch (IOException e) {
                    AntModule.err.notify (ErrorManager.INFORMATIONAL, e);
                }
            }
        }
    }
    
    private static class DocumentWriter extends PipedWriter implements Runnable {
        private StyledDocument doc;
        private PipedReader rd;
        private Thread t;
        public DocumentWriter (StyledDocument doc, FileObject fo) throws IOException {
            this.doc = doc;
            rd = new PipedReader ();
            connect (rd);
            (t = new Thread (this, "ant DocumentWriter: " + fo)).start (); // NOI18N
        }
        public void run () {
            try {
                NbDocument.runAtomicAsUser (doc, new Runnable () {
                        public void run () {
                            doc.putProperty (expectingDocUpdates, Boolean.TRUE);
                            try {
                                doc.remove (0, doc.getLength ());
                                new DefaultEditorKit ().read (rd, doc, 0);
                            } catch (IOException e) {
                                AntModule.err.notify (ErrorManager.INFORMATIONAL, e);
                            } catch (BadLocationException e) {
                                AntModule.err.notify (ErrorManager.INFORMATIONAL, e);
                            } finally {
                                doc.putProperty (expectingDocUpdates, null);
                            }
                        }
                    });
            } catch (BadLocationException e) {
                AntModule.err.notify (ErrorManager.INFORMATIONAL, e);
            }
        }
        public void close () throws IOException {
            super.close ();
            try {
                t.join ();
            } catch (InterruptedException ie) {
                IOException ioe = new IOException ();
                AntModule.err.annotate (ioe, ie);
                throw ioe;
            }
        }
    }
    
    // Firing processor which tells AntProjectSupport's when to fire changes.
    
    // when true, processor should be running
    private static boolean runFiringProcessor = false;
    
    // set of supports which should fire changes soon, to times when changes should be fired
    // also serves as a monitor for thread-safe communication with the processor
    private static final java.util.Map tofire = new HashMap (); // Map<AntProjectSupport,Date>
    
    protected final void invalidate () {
        AntModule.err.log ("AntProjectSupport.invalidate: fo=" + fo);
        parsed = false;
        synchronized (tofire) {
            if (! runFiringProcessor) {
                startFiringProcessor ();
            }
            if (tofire.put (this, new Date (System.currentTimeMillis () + REPARSE_DELAY)) == null) {
                // Was not previously enqueued; make sure processor does not wait forever!
                tofire.notify ();
            } // else was already enqueued, processor will wake up sometime
        }
    }

    // start it; accessible for AntModule
    public static void startFiringProcessor () {
        synchronized (tofire) {
            if (runFiringProcessor) return;
            runFiringProcessor = true;
            new FiringProcessor ().start ();
        }
    }
    
    // stop it; accessible for AntModule
    public static void stopFiringProcessor () {
        synchronized (tofire) {
            if (! runFiringProcessor) return;
            runFiringProcessor = false;
            tofire.notify ();
        }
    }
    
    private static final class FiringProcessor extends Thread {
        
        public FiringProcessor () {
            super ("AntProjectSupport.FiringProcessor"); // NOI18N
        }
        
        public void run () {
            synchronized (tofire) {
                // Do not fire changes *while* going through tofire.iterator; for it could happen
                // that some listener changes something, causing invalidate() to be
                // called synchronously -> ConcurrentModificationException on the
                // iterator! Instead, keep track of changes to be fired in this round.
                Set tofirenow = new HashSet ();
                while (runFiringProcessor) {
                    // First, fire any due/overdue changes.
                    Iterator it = tofire.entrySet ().iterator ();
                    // Keep track of when the next pending change is.
                    long next = Long.MAX_VALUE;
                    long now = System.currentTimeMillis ();
                    while (it.hasNext ()) {
                        Map.Entry entry = (Map.Entry) it.next ();
                        Date d = (Date) entry.getValue ();
                        long time = d.getTime ();
                        if (time <= now) {
                            tofirenow.add (entry.getKey ());
                            it.remove ();
                        } else if (time < next) {
                            next = time;
                        }
                    }
                    // Now actually fire the ones we want.
                    it = tofirenow.iterator ();
                    while (it.hasNext ()) {
                        ((AntProjectSupport) it.next ()).fireChangeEvent ();
                    }
                    tofirenow.clear ();
                    // Now go to sleep until the next one comes up, or
                    // a new change is added (not moved up).
                    try {
                        // While waiting, other threads may enqueue entries or ask
                        // to shut down the processor.
                        tofire.wait (next - now);
                    } catch (InterruptedException ie) {
                        // Ignore.
                    }
                }
                // Shutting down; fire any remaining changes and exit.
                Iterator it = tofire.keySet ().iterator ();
                while (it.hasNext ()) {
                    ((AntProjectSupport) it.next ()).fireChangeEvent ();
                }
            }
        }
        
    }
    
}
