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
package org.netbeans.modules.java.source.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.modules.java.source.classpath.AptCacheForSourceQuery;
import org.netbeans.modules.java.source.indexing.JavaIndex;
import org.netbeans.modules.java.source.indexing.TransactionContext;
import org.netbeans.modules.java.source.usages.Pair;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;
import org.openide.util.Utilities;

/**
 *
 * @author Tomas Zezula
 */
//@NotThreadSafe
public final class ProcessorGenerated extends TransactionContext.Service {

    private static final Logger LOG = Logger.getLogger(ProcessorGenerated.class.getName());
    
    public enum Type {
        SOURCE,
        RESOURCE
    }
    
    private final URL root;
    private final Map<URL,Pair<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>>> generated =
            new HashMap<URL,Pair<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>>>();
    private ClasspathInfo owner;
    private ClassPath userSources;
    private ClassPath aptSources;
    private File cachedFile;
    private StringBuilder cachedValue;
    private Set<String> cachedResources;
    private boolean cacheChanged;
    private boolean closedTx;
    
    
    private ProcessorGenerated(@NullAllowed final URL root) {
        this.root = root;
    }
    
    public Set<javax.tools.FileObject> getGeneratedSources(final URL forSource) {
        Pair<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>> res = 
            generated.get(forSource);
        return res == null ? null : res.first;
    }
    
    public boolean canWrite() {
        return root != null;
    }

    @CheckForNull
    public URL findSibling(@NonNull final Collection<? extends URL> candidates) {
        URL res = null;
        for (URL candiate : candidates) {
            if (root == null || FileObjects.isParentOf(root, candiate)) {
                res = candiate;
                break;
            }
        }
        return res;
    }
    
    public void bind(
         @NonNull final ClasspathInfo  owner,
         @NonNull final ClassPath userSources,
         @NullAllowed final ClassPath aptSources) {
        Parameters.notNull("owner", owner);             //NOI18N
        Parameters.notNull("userSources", userSources); //NOI18N
        if (!canWrite()) {
            return;
        }
        if (this.owner != null && !this.owner.equals(owner)) {
            throw new IllegalStateException(MessageFormat.format(
                "Previous owner: {0}({1}), New owner: {2}({3})",                //NOI18N
                this.owner,
                System.identityHashCode(this.owner),
                owner,
                System.identityHashCode(owner)));
        }
        this.userSources = userSources;
        this.aptSources = aptSources;
        this.owner = owner;
    }
    
    public void register(
        @NonNull final URL forSource,
        @NonNull final javax.tools.FileObject file,
        @NonNull final Type type) {
        if (!canWrite()) {
            return;
        }
        LOG.log(
            Level.FINE,
            "Generated: {0} from: {1} type: {2}",   //NOI18N
            new Object[]{
                file.toUri(),
                forSource,
                type
        });
        Pair<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>> insertInto =
                generated.get(forSource);
        if (insertInto == null) {
            insertInto = Pair.<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>>of(
                    new HashSet<javax.tools.FileObject>(),
                    new HashSet<javax.tools.FileObject>());
            generated.put(forSource, insertInto);
        }
        switch (type) {
            case SOURCE:
                insertInto.first.add(file);
                break;
            case RESOURCE:
                insertInto.second.add(file);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    protected void commit() throws IOException {
        closeTx();
        if (!canWrite()) {
            assert generated.isEmpty();
            return;
        }
        try {
            if (!generated.isEmpty()) {
                assert userSources != null;
                for (Map.Entry<URL,Pair<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>>> entry : generated.entrySet()) {
                    final URL source = entry.getKey();
                    final Pair<Set<javax.tools.FileObject>,Set<javax.tools.FileObject>> gen = entry.getValue();
                    final Set<javax.tools.FileObject> genSources = gen.first;
                    final Set<javax.tools.FileObject> genResources =  gen.second;
                    commitSource(source, genSources, genResources);
                }
                writeResources();
            }
        } finally {
            clear();
        }
    }

    @Override
    protected void rollBack() throws IOException {
        closeTx();
        if (!canWrite()) {
            assert generated.isEmpty();
            return;
        }
        clear();
    }
    
    private void clear() {
        generated.clear();
        cachedFile = null;
        cachedResources = null;
        cachedValue = null;
        cacheChanged = false;
    }

    private void closeTx() {
        if (closedTx) {
            throw new IllegalStateException("Already commited or rolled back transaction.");    //NOI18N
        }
        closedTx = true;
    }
    
    private void commitSource(
        @NonNull final URL forSource,
        @NonNull final Set<javax.tools.FileObject> genSources,
        @NonNull final Set<javax.tools.FileObject> genResources) {
        try {
            boolean apt = false;
            URL sourceRootURL = getOwnerRoot(forSource, userSources);
            if (sourceRootURL == null) {
                sourceRootURL = aptSources != null ? getOwnerRoot(forSource, aptSources) : null;
                if (sourceRootURL == null) {
                    return;
                }
                apt = true;
            }
            final File sourceRoot = Utilities.toFile(sourceRootURL.toURI());
            final File classCache = apt ?
                Utilities.toFile(AptCacheForSourceQuery.getClassFolder(sourceRootURL).toURI()):
                JavaIndex.getClassFolder(sourceRoot);
            if (!genSources.isEmpty()) {
                final File sourceFile = Utilities.toFile(forSource.toURI());
                final String relativePath = FileObjects.stripExtension(FileObjects.getRelativePath(sourceRoot, sourceFile));
                final File cacheFile = new File (classCache, relativePath+'.'+FileObjects.RAPT);
                if (!cacheFile.getParentFile().exists()) {
                    cacheFile.getParentFile().mkdirs();
                }
                final URL aptRootURL = AptCacheForSourceQuery.getAptFolder(sourceRootURL);
                final StringBuilder sb = new StringBuilder();
                for (javax.tools.FileObject file : genSources) {
                    sb.append(FileObjects.getRelativePath(aptRootURL, file.toUri().toURL()));
                    sb.append('\n');    //NOI18N
                }
                writeFile(cacheFile, sb);
            }
            if (!genResources.isEmpty()) {
                final File resFile = new File (classCache,FileObjects.RESOURCES);
                final Set<String> currentResources = new HashSet<String>();
                final StringBuilder sb = readResources(resFile, currentResources);
                boolean changed = false;
                for (javax.tools.FileObject file : genResources) {
                    String resPath = FileObjects.getRelativePath(Utilities.toURI(classCache).toURL(), file.toUri().toURL());
                    if (currentResources.add(resPath)) {
                        sb.append(resPath);
                        sb.append('\n');    //NOI18N
                        changed = true;
                    }
                }
                if (changed) {
                    updateCache(sb, currentResources);
                }
            }
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        } catch (URISyntaxException use) {
            Exceptions.printStackTrace(use);
        }
    }
    
    private StringBuilder readResources(
            @NonNull final File file,
            @NonNull Set<? super String> currentResources) {
        if (cachedFile == null) {
            cachedValue = readFile(file);
            cachedResources = new HashSet<String>(Arrays.asList(cachedValue.toString().split("\n")));  //NOI18N
            cachedFile = file;
        }        
        assert cachedValue != null;
        assert cachedResources != null;
        assert cachedFile.equals(file);
        currentResources.addAll(cachedResources);
        return cachedValue;
    }
    
    private StringBuilder readFile(final File file) {        
        StringBuilder sb = new StringBuilder();
        try {
            final Reader in = new InputStreamReader (new FileInputStream (file),"UTF-8");   //NOI18N
            try {
                char[] buffer = new char[1024];
                int len;
                while ((len=in.read(buffer))>0) {
                    sb.append(buffer, 0, len);
                }
            } finally {
                in.close();
            }
        } catch (IOException ioe) {
            if (sb.length() != 0) {
                sb = new StringBuilder();
            }
        }
        return sb;
    }
    
    
    private void writeResources() throws IOException {
        if (cacheChanged) {
            assert cachedFile != null;
            assert cachedValue != null;
            writeFile(cachedFile, cachedValue);
        }
    }
    
    private void writeFile (@NonNull final File file, @NonNull final StringBuilder data) throws IOException {        
        final Writer out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");    //NOI18N
        try {
            out.write(data.toString());
        } finally {
            out.close();
        }
    }

    private void updateCache(
            @NonNull final StringBuilder data,
            @NonNull final Set<String> currentResources) {
        assert data != null;
        assert currentResources != null;
            cachedValue = data;
            cachedResources = currentResources;
            cacheChanged = true;
    }

    private static URL getOwnerRoot (@NonNull final URL source, @NonNull ClassPath cp) throws URISyntaxException {
        assert source != null;
        assert cp != null;
        for (ClassPath.Entry entry : cp.entries()) {
            final URL rootURL = entry.getURL();
            if (FileObjects.isParentOf(rootURL, source)) {
                return rootURL;
            }
        }
        return null;
    }

    @NonNull
    public static ProcessorGenerated create(@NonNull final URL root) {
        return new ProcessorGenerated(root);
    }

    @NonNull
    public static ProcessorGenerated nullWrite() {
        return new ProcessorGenerated(null);
    }
}
