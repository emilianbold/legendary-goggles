/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.javadoc.search;
import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;

import org.netbeans.api.java.classpath.ClassPath;

import org.openide.filesystems.FileObject;

import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.classpath.GlobalPathRegistryEvent;

import org.netbeans.api.java.classpath.GlobalPathRegistryListener;

import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.modules.javadoc.settings.DocumentationSettings;
import org.openide.ErrorManager;
import org.openide.filesystems.URLMapper;
import org.openide.filesystems.FileUtil;

/**
 * Class which is able to serve index files of Javadoc for all
 * currently used Javadoc documentation sets.
 * @author Petr Hrebejk
 */
public class JavadocRegistry implements GlobalPathRegistryListener, ChangeListener, PropertyChangeListener  {
        
    private static JavadocRegistry INSTANCE;

    
    private GlobalPathRegistry regs;    
    private ArrayList listeners;
    private Set/*<JavadocForBinaryQuery.Result>*/ results = new HashSet ();
    private Set/*ClassPath*/ classpaths = new HashSet ();
    private Set /*<FileObject*/ roots;
    
    /** Creates a new instance of JavadocRegistry */
    private JavadocRegistry() {
        this.regs = GlobalPathRegistry.getDefault ();        
        this.regs.addGlobalPathRegistryListener(this);
    }
    
    public static synchronized JavadocRegistry getDefault() {
        if ( INSTANCE == null ) {
            INSTANCE = new JavadocRegistry();
        }
        return INSTANCE;
    }

    /** Returns Array of the Javadoc Index roots
     */
    public synchronized FileObject[] getDocRoots() {
        if (this.roots == null) {
            readRoots();
        }
        FileObject[] result = new FileObject[ roots.size() ];
        roots.toArray( result );
        return result;
    }
    
    
    public JavadocSearchType findSearchType( FileObject apidocRoot ) {
        // XXX Should try to find correct engine
        JavadocSearchType type = (JavadocSearchType)DocumentationSettings.getDefault().getSearchEngine();
        assert type != null;
        return type;
    }    
        
    // Private methods ---------------------------------------------------------
    
    private void readRoots() {
        assert this.classpaths.size() == 0 & this.results.size () == 0 : "Illegal state of object!";
        this.roots = new HashSet ();
        List paths = new LinkedList();
        paths.addAll( this.regs.getPaths( ClassPath.COMPILE ) );        
        paths.addAll( this.regs.getPaths( ClassPath.BOOT ) );
        for( Iterator it = paths.iterator(); it.hasNext(); ) {
            ClassPath ccp = (ClassPath)it.next();
            ccp.addPropertyChangeListener(this);
            this.classpaths.add (ccp);
            //System.out.println("CCP " + ccp );
            FileObject ccpRoots[] = ccp.getRoots();
            
            for( int i = 0; i < ccpRoots.length; i++ ) {
                //System.out.println(" CCPR " + ccpRoots[i]);
                JavadocForBinaryQuery.Result result = JavadocForBinaryQuery.findJavadoc( URLMapper.findURL(ccpRoots[i], URLMapper.EXTERNAL ) );
                result.addChangeListener(this);
                this.results.add (result);
                URL[] jdRoots = result.getRoots();                    
                for ( int j = 0; j < jdRoots.length; j++ ) {
                    //System.out.println( "  JDR " + jdRoots[j] );
                    //System.out.println("Looking for root of: "+jdRoots[j]);
                    FileObject fo = URLMapper.findFileObject(jdRoots[j]);
                    //System.out.println("Found: "+fo);
                    if (fo != null) {                        
                        roots.add(fo);
                    }
                }
                                    
            }
        }
        //System.out.println("roots=" + roots);
    }

    public void pathsAdded(GlobalPathRegistryEvent event) {
        this.throwCache ();
        this.fireChange ();
    }

    public void pathsRemoved(GlobalPathRegistryEvent event) {
        this.throwCache ();
        this.fireChange ();
    }
    
    public void propertyChange (PropertyChangeEvent event) {        
        if (ClassPath.PROP_ENTRIES.equals (event.getPropertyName())) {
            this.throwCache ();
            this.fireChange ();
        }
    }
    
    
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        this.throwCache ();
        this.fireChange ();
    }

    
    
    public synchronized void addChangeListener (ChangeListener l) {
        assert l != null : "Listener can not be null.";     //NOI18N
        if (this.listeners == null) {
            this.listeners = new ArrayList ();
        }
        this.listeners.add (l);
    }
    
    public synchronized void removeChangeListener (ChangeListener l) {
        assert l != null : "Listener can not be null.";     //NOI18N
        if (this.listeners == null) {
            return;
        }
        this.listeners.remove (l);
    }
    
    private void fireChange () {
        Iterator it = null;
        synchronized (this) {
            if (this.listeners == null) {
                return;
            }
            it = ((ArrayList)this.listeners.clone()).iterator();
        }
        ChangeEvent event = new ChangeEvent (this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(event);
        }
    }    
    
    private synchronized void throwCache () {
        this.roots = null;
        //Unregister itself from classpaths, not interested in events
        for (Iterator it = this.classpaths.iterator(); it.hasNext();) {
            ClassPath cp = (ClassPath) it.next ();
            cp.removePropertyChangeListener(this);
            it.remove ();
        }
        //Unregister itself from results, not interested in events
        for (Iterator it = this.results.iterator(); it.hasNext();) {
            JavadocForBinaryQuery.Result result = (JavadocForBinaryQuery.Result) it.next ();
            result.removeChangeListener (this);
            it.remove ();
        }
    }
        
}
