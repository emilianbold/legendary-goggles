/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.editor.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.EditorKit;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;

/**
 *
 * @author vita
 */
public final class KitsTracker {
        
    private static final Logger LOG = Logger.getLogger(KitsTracker.class.getName());
    private static final Set<String> ALREADY_LOGGED = Collections.synchronizedSet(new HashSet<String>(10));
    
    private static KitsTracker instance = null;
    
    /**
     * Gets the <code>KitsTracker</code> singleton instance.
     * @return The <code>KitsTracker</code> instance.
     */
    public static synchronized KitsTracker getInstance() {
        if (instance == null) {
            instance = new KitsTracker();
        }
        return instance;
    }
    
    public static String getGenericPartOfCompoundMimeType(String mimeType) {
        int plusIdx = mimeType.lastIndexOf('+'); //NOI18N
        if (plusIdx != -1 && plusIdx < mimeType.length() - 1) {
            int slashIdx = mimeType.indexOf('/'); //NOI18N
            String prefix = mimeType.substring(0, slashIdx + 1);
            String suffix = mimeType.substring(plusIdx + 1);

            // fix for #61245
            if (suffix.equals("xml")) { //NOI18N
                prefix = "text/"; //NOI18N
            }

            return prefix + suffix;
        } else {
            return null;
        }
    }
    
    /**
     * Gets the list of mime types (<code>String</code>s) that use the given
     * class as an editor kit implementation.
     * 
     * @param kitClass The editor kit class to get mime types for.
     * @return The <code>List&lt;String&gt;</code> of mime types.
     */
    @SuppressWarnings("unchecked")
    public List<String> getMimeTypesForKitClass(Class kitClass) {
        if (kitClass != null) {
            return (List<String>) updateAndGet(kitClass);
        } else {
            return Collections.singletonList(""); //NOI18N
        }
    }

    /**
     * Find mime type for a given editor kit implementation class.
     * 
     * @param kitClass The editor kit class to get the mime type for.
     * @return The mime type or <code>null</code> if the mime type can't be
     *   resolved for the given kit class.
     */
    public String findMimeType(Class kitClass) {
        if (kitClass != null) {
            if (WELL_KNOWN_PARENTS.contains(kitClass.getName())) {
                // these classes are not directly registered as a kit for any mime type
                return null;
            }

            String contextMimeType = null;
            Stack<String> context = contexts.get();
            if (context != null && !context.empty()) {
                contextMimeType = context.peek();
            }
            
            if (contextMimeType == null || contextMimeType.length() ==0) {
                List mimeTypes = getMimeTypesForKitClass(kitClass);
                if (mimeTypes.size() == 0) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        logOnce(Level.WARNING, "No mime type uses editor kit implementation class: " + kitClass); //NOI18N
                    }
                    return null;
                } else if (mimeTypes.size() == 1) {
                    return (String) mimeTypes.get(0);
                } else {
                    if (LOG.isLoggable(Level.WARNING)) {
        //                Throwable t = new Throwable("Stacktrace"); //NOI18N
        //                LOG.log(Level.WARNING, "Ambiguous mime types for editor kit implementation class: " + kitClass + "; mime types: " + mimeTypes, t); //NOI18N
                        logOnce(Level.WARNING, "Ambiguous mime types for editor kit implementation class: " + kitClass + "; mime types: " + mimeTypes); //NOI18N
                    }
                    return null;
                }
            } else{
                return contextMimeType;
            }
        } else {
            return ""; //NOI18N
        }
    }

    /**
     * Gets all know mime types registered in the system.
     * 
     * @return The set of registered mimne types.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getMimeTypes() {
        return (Set<String>) updateAndGet(null);
    }
    
    public String setContextMimeType(String mimeType) {
        String previousMimeType = null;
        
        Stack<String> context = contexts.get();
        if (context == null) {
            context = new Stack<String>();
            contexts.set(context);
        }
        
        if (mimeType != null) {
            assert MimePath.validate(mimeType) : "Invalid mime type: '" + mimeType + "'"; //NOI18N
            if (!context.empty()) {
                previousMimeType = context.peek();
            }
            context.push(mimeType);
        } else {
            if (!context.empty()) {
                previousMimeType = context.pop();
            }
        }
        
        return previousMimeType;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        PCS.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        PCS.removePropertyChangeListener(l);
    }
    
    // ------------------------------------------------------------------
    // private implementation
    // ------------------------------------------------------------------
    
    // The map of mime type -> kit class
    private final Map<String, FileObject> mimeType2kitClass = new HashMap<String, FileObject>();
    private final Set<String> knownMimeTypes = new HashSet<String>();
    private List<FileObject> eventSources = null;
    private boolean needsReloading = true;
    private final PropertyChangeSupport PCS = new PropertyChangeSupport(this);

    private static final Set<String> WELL_KNOWN_PARENTS = new HashSet<String>(Arrays.asList(new String [] {
        "java.lang.Object", //NOI18N
        "javax.swing.text.EditorKit", //NOI18N
        "javax.swing.text.DefaultEditorKit", //NOI18N
        "org.netbeans.editor.BaseKit", //NOI18N
        "org.netbeans.editor.ext.ExtKit", //NOI18N
        "org.netbeans.modules.editor.NbEditorKit", //NOI18N
    }));
    
    private final ThreadLocal<Stack<String>> contexts = new ThreadLocal<Stack<String>>();
    
    private final FileChangeListener fcl = new FileChangeAdapter() {
        @Override
        public void fileFolderCreated(FileEvent fe) {
            invalidateCache();
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            if (fe.getFile().isFolder()) {
                invalidateCache();
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            if (fe.getFile().isFolder()) {
                invalidateCache();
            }
        }
    };
    
    private KitsTracker() {

    }

    private static final ThreadLocal<Boolean> inReload = new  ThreadLocal<Boolean>() {
        protected @Override Boolean initialValue() {
            return false;
        }
    };
    
    /**
     * Scans fonlders under 'Editors' and finds <code>EditorKit</code>s for
     * each mime type.
     * 
     * @param map The map of a mime type to its registered <code>EditorKit</code>.
     * @param eventSources The list of folders with registered <code>EditorKits</code>.
     *   Changes in these folders mean that the map may need to be recalculated.
     */
    private static void reload(Map<String, FileObject> map, Set<String> set, List<FileObject> eventSources) {
        assert !inReload.get() : "Re-entering KitsTracker.reload() is prohibited. This situation usually indicates wrong initialization of some setting."; //NOI18N
        
        inReload.set(true);
        try {
            _reload(map, set, eventSources);
        } finally {
            inReload.set(false);
        }
    }
    
    private static void _reload(Map<String, FileObject> map, Set<String> set, List<FileObject> eventSources) {
        // Get the root of the MimeLookup registry
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Editors"); //NOI18N

        // Generally may not exist (e.g. in tests)
        if (fo != null) {
            // Go through mime type types
            FileObject [] types = fo.getChildren();
            for(int i = 0; i < types.length; i++) {
                if (!isValidType(types[i])) {
                    continue;
                }

                // Go through mime type subtypes
                FileObject [] subTypes = types[i].getChildren();
                for(int j = 0; j < subTypes.length; j++) {
                    if (!isValidSubtype(subTypes[j])) {
                        continue;
                    }

                    String mimeType = types[i].getNameExt() + "/" + subTypes[j].getNameExt(); //NOI18N
                    FileObject kitInstanceFile = findKitRegistration(subTypes[j]);
                    
                    if (kitInstanceFile != null) {
                        map.put(mimeType, kitInstanceFile);
                    } else {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine("No kit for '" + mimeType + "'");
                        }
                    }
                    
                    set.add(mimeType);
                }

                eventSources.add(types[i]);
            }

            eventSources.add(fo);
        }
    }

    private static FileObject findKitRegistration(FileObject folder) {
        for(FileObject f : folder.getChildren()) {
            if (isInstanceOf(f, EditorKit.class, false)) {
                return f;
            }
        }
        
        return null;
    }
    
    private static boolean isInstanceOf(FileObject f, Class clazz, boolean exactMatch) {
        try {
            DataObject d = DataObject.find(f);
            InstanceCookie ic = d.getLookup().lookup(InstanceCookie.class);

            if (ic != null) {
                if (!exactMatch && (ic instanceof InstanceCookie.Of)) {
                    if (((InstanceCookie.Of) ic).instanceOf(clazz)) {
                        return true;
                    }
                } else {
                    Class instanceClass = ic.instanceClass();
                    if (!exactMatch) {
                        if (clazz.isAssignableFrom(instanceClass)) {
                            return true;
                        }
                    } else {
                        if (clazz == instanceClass) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        
        return false;
    }
    
    private void invalidateCache() {
        synchronized (mimeType2kitClass) {
            needsReloading = true;
        }
        PCS.firePropertyChange(null, null, null);
    }

    private static boolean isValidType(FileObject typeFile) {
        if (!typeFile.isFolder()) {
            return false;
        }

        String typeName = typeFile.getNameExt();
        return MimePath.validate(typeName, null);
    }

    private static boolean isValidSubtype(FileObject subtypeFile) {
        if (!subtypeFile.isFolder()) {
            return false;
        }

        String typeName = subtypeFile.getNameExt();
        return MimePath.validate(null, typeName);
    }        
    
    private static void logOnce(Level level, String msg) {
        if (!ALREADY_LOGGED.contains(msg)) {
            LOG.log(level, msg);
            ALREADY_LOGGED.add(msg);
        }
    }

    private Object updateAndGet(Class kitClass) {
        boolean reload;
        Map<String, FileObject> reloadedMap = new HashMap<String, FileObject>();
        Set<String> reloadedSet = new HashSet<String>();
        List<FileObject> newEventSources = new ArrayList<FileObject>();
        
        ArrayList<String> list = new ArrayList<String>();
        HashSet<String> set = new HashSet<String>();
        
        synchronized (mimeType2kitClass) {
            reload = needsReloading;
        }
        
        // This needs to be outside of the synchronized block to prevent deadlocks
        // See eg #107400
        if (reload) {
            reload(reloadedMap, reloadedSet, newEventSources);
        }
            
        synchronized (mimeType2kitClass) {
            if (reload) {
                // Stop listening
                if (eventSources != null) {
                    for(FileObject fo : eventSources) {
                        fo.removeFileChangeListener(fcl);
                    }
                }

                // Update the cache
                mimeType2kitClass.clear();
                mimeType2kitClass.putAll(reloadedMap);
                knownMimeTypes.clear();
                knownMimeTypes.addAll(reloadedSet);

                // Start listening again
                eventSources = newEventSources;
                for(FileObject fo : eventSources) {
                    fo.addFileChangeListener(fcl);
                }

                // Set the flag
                needsReloading = false;
            }
            
            // Compute the list
            if (kitClass != null) {
                for(String mimeType : mimeType2kitClass.keySet()) {
                    FileObject f = mimeType2kitClass.get(mimeType);
                    if (isInstanceOf(f, kitClass, true)) {
                        list.add(mimeType);
                    }
                }
            } else {
                set.addAll(knownMimeTypes);
            }
        }
        
        return kitClass != null ? list : set;
    }
}
