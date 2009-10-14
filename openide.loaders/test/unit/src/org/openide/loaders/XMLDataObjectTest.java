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

package org.openide.loaders;

import org.openide.filesystems.*;
import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import junit.framework.Assert;
import org.netbeans.junit.Log;
import org.netbeans.junit.RandomlyFails;
import org.openide.cookies.*;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.xml.XMLUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 *
 * @author  Jaroslav Tulach
 */
public class XMLDataObjectTest extends org.netbeans.junit.NbTestCase {
    private FileObject data;
    private CharSequence log;

    /** Creates new MultiFileLoaderHid */
    public XMLDataObjectTest (String name) {
        super (name);
    }

//    public static Test suite() {
//        return new XMLDataObjectTest("testWrongUTFCharacer");
//    }

    @Override
    protected void setUp () throws Exception {
        clearWorkDir();
        
        log = Log.enable("org.openide.loaders", Level.WARNING);
        
        super.setUp ();
        System.setProperty ("org.openide.util.Lookup", "org.openide.loaders.XMLDataObjectTest$Lkp");
        String fsstruct [] = new String [] {
        };
        FileSystem fs = TestUtilHid.createLocalFileSystem (getWorkDir(), fsstruct);
        data = FileUtil.createData (
            fs.getRoot (),
            "kuk/test/my.xml"
        );
        FileLock lock = data.lock ();
        OutputStream os = data.getOutputStream (lock);
        PrintStream p = new PrintStream (os);
        
        p.println ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        p.println ("<root>");
        p.println ("</root>");
        
        p.close ();
        lock.releaseLock ();

        // initialize the document before we start to measure the access
        assertNotNull("Constructor ready", XMLDataObject.cnstr);
        CountingSecurityManager.initialize();

        assertParse(false, false);
        assertParse(false, true);
        assertParse(true, false);
        assertParse(true, true);
    }

    private static void assertParse(boolean validate, boolean namespace) throws Exception {
        try {
            XMLUtil.parse(new InputSource(new ByteArrayInputStream(new byte[0])), validate, namespace, null, null);
        } catch (SAXParseException ex) {
        }
        CountingSecurityManager.assertMembers(1);
    }
    
    @Override
    protected void tearDown () throws Exception {
        super.tearDown ();
        TestUtilHid.destroyLocalFileSystem (getName());
        if (log.length() > 0) {
            fail("There should be no warnings:\n" + log);
        }
    }
    
    public void testGetStatusBehaviour () throws Exception {
        DataObject obj = DataObject.find (data);
        
        assertEquals ("Is xml", XMLDataObject.class, obj.getClass ());
        
        XMLDataObject xml = (XMLDataObject)obj;
        
        assertEquals ("not parsed yet", XMLDataObject.STATUS_NOT, xml.getStatus ());
        
        org.w3c.dom.Document doc = xml.getDocument ();
        assertEquals ("still not parsed as we have lazy document", XMLDataObject.STATUS_NOT, xml.getStatus ());
        
        String id = doc.getDoctype ().getPublicId ();
        assertEquals ("still not parsed as we have special support for publilc id", XMLDataObject.STATUS_NOT, xml.getStatus ());
        
        org.w3c.dom.Element e = doc.getDocumentElement ();
        assertNotNull ("Document parsed", doc);
        
        assertEquals ("status is ok", XMLDataObject.STATUS_OK, xml.getStatus ());
        
        assertNotNull("Has open cookie", xml.getCookie(OpenCookie.class));
        assertNotNull("Has open cookie in lookup", xml.getLookup().lookup(OpenCookie.class));
        
        
        Reference<Object> ref = new WeakReference<Object>(xml);
        xml = null;
        obj = null;
        doc = null;
        e = null;
        assertGC("Data object has to be garbage collectable", ref);


        CountingSecurityManager.assertMembers(0);
    }

    public void testCookieIsUpdatedWhenContentChanges () throws Exception {
        
        
        FileLock lck;
        DataObject obj;
        lck = data.lock();
        
        PCL pcl = new PCL ();
        
        // this next line causes the test to fail on 2004/03/03
        obj = DataObject.find (data);
        obj.addPropertyChangeListener (pcl);
        
        assertNull ("No instance cookie", obj.getCookie (org.openide.cookies.InstanceCookie.class));
        assertEquals (0, pcl.cnt);
        
        try {
            OutputStream ostm = data.getOutputStream(lck);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(ostm, "UTF8")); //NOI18N
            pw.println("<?xml version='1.0'?>"); //NOI18N
            pw.println("<!DOCTYPE driver PUBLIC '-//NetBeans//DTD JDBC Driver 1.0//EN' 'http://www.netbeans.org/dtds/jdbc-driver-1_0.dtd'>"); //NOI18N
            pw.println("<driver>"); //NOI18N
            pw.println("  <name value='somename'/>"); //NOI18N
            pw.println("  <class value='java.lang.String'/>"); //NOI18N
            pw.println("  <urls>"); //NOI18N
            pw.println("  </urls>"); //NOI18N
            pw.println("</driver>"); //NOI18N
            pw.flush();
            pw.close();
            ostm.close();
        } finally {
            lck.releaseLock();
        }
        assertEquals ("One change fired when the file was written", 1, pcl.cnt);
        assertNotNull ("There is an cookie", obj.getCookie (org.openide.cookies.InstanceCookie.class));

        CountingSecurityManager.assertMembers(0);
    }

    @RandomlyFails // NB-Core-Build #1691
    public void testToolbarsAreBrokenAsTheLookupIsClearedTooOftenIssue41360 () throws Exception {
        FileLock lck;
        DataObject obj;
        lck = data.lock();
        String id = "-//NetBeans//DTD Fake Toolbar 1.0//EN";
        
        XMLDataObject.Info info = new XMLDataObject.Info ();
        info.addProcessorClass (ToolbarProcessor.class);
        try {
            XMLDataObject.registerInfo (id, info);
            
            
            OutputStream ostm = data.getOutputStream(lck);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(ostm, "UTF8")); //NOI18N
            pw.println("<?xml version='1.0'?>"); //NOI18N
            pw.println("<!DOCTYPE toolbar PUBLIC '" + id + "' 'http://www.netbeans.org/dtds/jdbc-driver-1_0.dtd'>"); //NOI18N
            pw.println("<toolbar>"); //NOI18N
            pw.println("  <name value='somename'/>"); //NOI18N
            pw.println("  <class value='java.lang.String'/>"); //NOI18N
            pw.println("  <urls>"); //NOI18N
            pw.println("  </urls>"); //NOI18N
            pw.println("</toolbar>"); //NOI18N
            pw.flush();
            pw.close();
            ostm.close();
            
            obj = DataObject.find (data);
            PCL pcl = new PCL ();
            obj.addPropertyChangeListener (pcl);
            
            InstanceCookie cookie = (InstanceCookie)obj.getCookie (InstanceCookie.class);
            assertNotNull (cookie);
            assertEquals ("No changes yet", 0, pcl.cnt);
            
            assertNotNull("Data object in lkp", obj.getLookup().lookup(DataObject.class));
            assertNotNull("Data object in lkp", obj.getLookup().lookup(FileObject.class));
            assertNotNull("Data object in cookie", obj.getCookie(DataObject.class));
            checkLookup(obj, 4);

            pcl.cnt = 0;

            ostm = data.getOutputStream(lck);
            pw = new java.io.PrintWriter(new OutputStreamWriter(ostm, "UTF8")); //NOI18N
            pw.println("<?xml version='1.0'?>"); //NOI18N
            pw.println("<!DOCTYPE toolbar PUBLIC '" + id + "' 'http://www.netbeans.org/dtds/jdbc-driver-1_0.dtd'>"); //NOI18N
            pw.println("<toolbar>"); //NOI18N
            pw.println("  <name value='somename'/>"); //NOI18N
            pw.println("  <class value='java.lang.String'/>"); //NOI18N
            pw.println("  <urls>"); //NOI18N
            pw.println("  </urls>"); //NOI18N
            pw.println("</toolbar>"); //NOI18N
            pw.flush();
            pw.close();
            ostm.close();
            
            InstanceCookie newCookie = (InstanceCookie)obj.getCookie (InstanceCookie.class);
            assertNotNull (newCookie);
            assertEquals ("One change in document", 1, pcl.docChange);
            assertEquals ("The cookie is still the same", cookie, newCookie);
            assertEquals ("No cookie change", 0, pcl.cnt);
            
        } finally {
            XMLDataObject.registerInfo (id, null);
            lck.releaseLock ();
        }
        CountingSecurityManager.assertMembers(1);
    }

    public void testWrongUTFCharacer() throws Exception {
        FileLock lck;
        DataObject obj;

        FileObject d = data.getParent().createData("wrongutfchar.xml");

        lck = d.lock();


        OutputStream os = d.getOutputStream(lck);
        os.write(0xc5); // multibyte
        os.write(0x00); // wrong char after multibyte
        os.close();

        obj = DataObject.find(d);

        XMLDataObject xml = (XMLDataObject)obj;
        String id = xml.getDocument().getDoctype().getPublicId();
        assertEquals("No ID", null, id);
        
        assertEquals("No warnings\n" + log, 0, log.length());
        CountingSecurityManager.assertMembers(0);
    }
    
    public void testCheckLookupContent() throws DataObjectNotFoundException {
        DataObject obj = DataObject.find(data);
        checkLookup(obj, 12);
    }
    
    private static void checkLookup(DataObject obj, int expected) throws DataObjectNotFoundException {
        Collection<? extends Object> all = obj.getLookup().lookupAll(Object.class);

        int cnt = 0;
        for (Object object : all) {
            assertEquals("Is in lkp", object, obj.getLookup().lookup(object.getClass()));
            Class c = object.getClass();
            if (object instanceof EditorCookie) {
                c = EditorCookie.class;
            }
            if (object instanceof Node.Cookie) {
                assertEquals("Is in cookie: " + c.getSuperclass(), object, obj.getCookie(c));
            }
            cnt++;
        }
        assertEquals("There are some items:\n" + all, expected, cnt);
    }
    
    public static final class Lkp extends org.openide.util.lookup.AbstractLookup 
    implements org.openide.loaders.Environment.Provider {
        public Lkp () {
            this (new org.openide.util.lookup.InstanceContent ());
        }
        
        private Lkp (org.openide.util.lookup.InstanceContent ic) {
            super (ic);
            ic.add (this); // Environment.Provider
        }
        
        public org.openide.util.Lookup getEnvironment (org.openide.loaders.DataObject obj) {
            if (obj instanceof XMLDataObject) {
                try {
                    XMLDataObject xml = (XMLDataObject)obj;
                    final String id = xml.getDocument ().getDoctype ().getPublicId ();
                    if (id != null) {
                        return org.openide.util.lookup.Lookups.singleton (new org.openide.cookies.InstanceCookie () {
                            public Object instanceCreate () {
                                return id;
                            }
                            public Class instanceClass () {
                                return String.class;
                            }
                            public String instanceName () {
                                return instanceClass ().getName ();
                            }
                        });
                    }
                } catch (Exception ex) {
                    fail (ex.getMessage ());
                }
            }
            return null;
        }
        
    } // end of Lkp

    
    /** Processor.
     */
    public static class ToolbarProcessor 
    implements XMLDataObject.Processor, InstanceCookie.Of {
        
        public void attachTo (org.openide.loaders.XMLDataObject xmlDO) {
        }
        
        public Class instanceClass () throws java.io.IOException, ClassNotFoundException {
            return getClass ();
        }
        
        public Object instanceCreate () throws java.io.IOException, ClassNotFoundException {
            return this;
        }
        
        public String instanceName () {
            return getClass ().getName ();
        }
        
        public boolean instanceOf (Class type) {
            return type.isAssignableFrom (getClass());
        }
        
    } // end of ToolbarProcessor

    public void testGetCookieCannotBeReentrantFromMoreThreads () throws Exception {
        FileLock lck;
        DataObject obj;
        lck = data.lock();
        String id = "-//NetBeans//DTD X Prcs 1.0//EN";
        
        XMLDataObject.Info info = new XMLDataObject.Info ();
        info.addProcessorClass (XProcessor.class);
        try {
            XMLDataObject.registerInfo (id, info);
            
            
            OutputStream ostm = data.getOutputStream(lck);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(ostm, "UTF8")); //NOI18N
            pw.println("<?xml version='1.0'?>"); //NOI18N
            pw.println("<!DOCTYPE toolbar PUBLIC '" + id + "' 'http://www.netbeans.org/dtds/jdbc-driver-1_0.dtd'>"); //NOI18N
            pw.println("<toolbar>"); //NOI18N
            pw.println("  <name value='somename'/>"); //NOI18N
            pw.println("  <class value='java.lang.String'/>"); //NOI18N
            pw.println("  <urls>"); //NOI18N
            pw.println("  </urls>"); //NOI18N
            pw.println("</toolbar>"); //NOI18N
            pw.flush();
            pw.close();
            ostm.close();
            
            obj = DataObject.find (data);
            
            Object ic = obj.getCookie(InstanceCookie.class);
            assertNotNull("There is a cookie", ic);
            assertEquals("The right class", XProcessor.class, ic.getClass());
            
            XProcessor xp = (XProcessor)ic;
            
            // now it can finish
            xp.task.waitFinished();
            
            assertNotNull("Cookie created", xp.cookie);
            assertEquals("It is the same as me", xp.cookie, xp);
        } finally {
            XMLDataObject.registerInfo (id, null);
            lck.releaseLock ();
        }
    }

    /** Processor.
     */
    public static class XProcessor 
    implements XMLDataObject.Processor, InstanceCookie.Of, Runnable {
        private XMLDataObject obj;
        private Node.Cookie cookie;
        private RequestProcessor.Task task;
        
        public void attachTo (org.openide.loaders.XMLDataObject xmlDO) {
            obj = xmlDO;
            task = RequestProcessor.getDefault().post(this);
            try {
                assertFalse("This is going to time out", task.waitFinished(500));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                fail("No exceptions please");
            }
            assertNull("Cookie is still null", cookie);
            
        }
        
        public void run () {
            cookie = obj.getCookie(InstanceCookie.class);
        }
        
        public Class instanceClass () throws java.io.IOException, ClassNotFoundException {
            return getClass ();
        }
        
        public Object instanceCreate () throws java.io.IOException, ClassNotFoundException {
            return this;
        }
        
        public String instanceName () {
            return getClass ().getName ();
        }
        
        public boolean instanceOf (Class type) {
            return type.isAssignableFrom (getClass());
        }
        
    } // end of XProcessor
    
    private static class PCL implements java.beans.PropertyChangeListener {
        int cnt;
        int docChange;

        public void propertyChange (java.beans.PropertyChangeEvent ev) {
            if (DataObject.PROP_COOKIE.equals (ev.getPropertyName ())) {
                cnt++;
            }
            if (XMLDataObject.PROP_DOCUMENT.equals (ev.getPropertyName ())) {
                docChange++;
            }
        }
    } // end of PCL


    static final class CountingSecurityManager extends SecurityManager {
        public static void initialize() {
            if (System.getSecurityManager() instanceof CountingSecurityManager) {
                // ok
            } else {
                System.setSecurityManager(new CountingSecurityManager());
            }
            members.clear();
        }

        static void assertMembers(int cnt) {
            int myCnt = 0;
            StringWriter w = new StringWriter();
            PrintWriter p = new PrintWriter(w);
            Set<Who> m;
            synchronized (members) {
                m = new TreeSet<Who>(members.values());
            }
            for (Who wh : m) {
                if (wh.isIgnore()) {
                    continue;
                }

                myCnt += wh.count;
                wh.printStackTrace(p);
                wh.count = 0;
            }
            if (myCnt > cnt) {
                Assert.fail("Expected at much " + cnt + " reflection efforts, but was: " + myCnt + "\n" + w);
            }
        }

        static Map<Class,Who> members = Collections.synchronizedMap(new HashMap<Class, Who>());
        @Override
        public void checkMemberAccess(Class<?> clazz, int which) {
            if (clazz == null) {
                assertMembers(which);
            }

            Who w = members.get(clazz);
            if (w == null) {
                w = new Who(clazz);
                members.put(clazz, w);
            }
            w.count++;
        }

        private static class Who extends Exception implements Comparable<Who> {
            int hashCode;
            final Class<?> clazz;
            int count;

            public Who(Class<?> who) {
                super("");
                this.clazz = who;
            }

            @Override
            public void printStackTrace(PrintWriter s) {
                s.println("Members of class " + clazz.getName() + " initialized " + count + " times");
                super.printStackTrace(s);
            }

            @Override
            public int hashCode() {
                if (hashCode != 0) {
                    return hashCode;
                }
                hashCode = clazz.hashCode();
                for (StackTraceElement stackTraceElement : getStackTrace()) {
                    hashCode = hashCode * 2 + stackTraceElement.hashCode();
                }
                return hashCode;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                if (getClass() != obj.getClass()) {
                    return false;
                }
                final Who other = (Who) obj;
                if (this.clazz != other.clazz) {
                    return false;
                }
                if (this.hashCode() != other.hashCode()) {
                    return false;
                }
                return Arrays.equals(getStackTrace(), other.getStackTrace());
            }

            public int compareTo(Who o) {
                if (o == this) {
                    return 0;
                }
                if (o.count < this.count) {
                    return -1;
                }
                if (o.count > this.count) {
                    return 1;
                }
                return this.clazz.getName().compareTo(o.clazz.getName());
            }

            private boolean isIgnore() {
                for (StackTraceElement stackTraceElement : getStackTrace()) {
                    if (stackTraceElement.getClassName().startsWith("org.openide.loaders.XMLDataObject$")) {
                        return false;
                    }
                    if (stackTraceElement.getClassName().equals("org.openide.loaders.XMLDataObject")) {
                        return false;
                    }
                    if (stackTraceElement.getClassName().equals("org.openide.nodes.FilterNode")) {
                        return true;
                    }
                    if (stackTraceElement.getClassName().equals("org.openide.loaders.DataNode")) {
                        return true;
                    }
                }
                return true;
            }
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        @Override
        public void checkPermission(Permission perm) {
        }
    }
    
}
