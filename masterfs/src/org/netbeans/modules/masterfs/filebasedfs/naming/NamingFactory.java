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

package org.netbeans.modules.masterfs.filebasedfs.naming;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.netbeans.modules.masterfs.filebasedfs.utils.FileInfo;
import org.netbeans.modules.masterfs.filebasedfs.utils.Utils;
import org.netbeans.modules.masterfs.providers.ProvidedExtensions;
import org.openide.util.Utilities;

/**
 * @author Radek Matous
 */
public final class NamingFactory {
    private static NameRef[] names = new NameRef[2];
    private static int namesCount;

    public static FileNaming fromFile(File file) {
        if (Utilities.isWindows() && file.getPath().length() == 2 && file.getPath().charAt(1) == ':') {
            file = new File(file.getPath() + File.separator);
        }
        final Deque<FileInfo> queue = new ArrayDeque<FileInfo>();
        File current = file;
        while (current != null) {
            queue.addFirst(new FileInfo(current));
            current = current.getParentFile();
        }

        List<FileInfo> checkDirs = new ArrayList<FileInfo>();
        FileNaming fileName = null;
        final List<FileInfo> list = new ArrayList(queue);
        for (int i = 0; i < list.size(); ) {
            FileInfo f = list.get(i);
            if("\\\\".equals(f.getFile().getPath())) {
                // UNC file - skip \\, \\computerName
                i++;
                continue;
            }
            for (FileInfo fi : checkDirs) {
                fi.isDirectory();
            }
            checkDirs.clear();
            
            // returns unknown if last in the list, otherwise directory
            FileType type = (i == list.size() - 1) ? FileType.unknown : FileType.directory;
            synchronized (NamingFactory.class) {
                FileNaming fn = NamingFactory.registerInstanceOfFileNaming(fileName, f, type, checkDirs);
                if (fn == null) {
                    continue;
                }
                fileName = fn;
                i++;
            }
        }

        return fileName;
    }

    public static synchronized int getSize () {
        return namesCount;
    }
    
    public static FileNaming fromFile(final FileNaming parentFn, final File file, boolean ignoreCache) {
        FileInfo info = new FileInfo(file);
        List<FileInfo> checkDirs = new ArrayList<FileInfo>();
        for (;;) {
            for (FileInfo fileInfo : checkDirs) {
                fileInfo.isDirectory();
            }
            FileNaming ret;
            synchronized (NamingFactory.class) {
                ret = NamingFactory.registerInstanceOfFileNaming(
                    parentFn, info, null, ignoreCache,
                    FileType.unknown, checkDirs
                );
            }
            if (ret != null) {
                return ret;
            }
        }
    }
    
    public static synchronized FileNaming checkCaseSensitivity(final FileNaming childName, final File f) throws IOException {
        if (!childName.getFile().getName().equals(f.getName())) {
            boolean isCaseSensitive = !Utils.equals(new File(f,"a"), new File(f,"A"));//NOI18N
            if (!isCaseSensitive) {
                FileName fn = (FileName)childName;
                fn.updateCase(f.getName());
            }
        }
        return childName;
    }
    
    public static FileNaming[] rename (FileNaming fNaming, String newName, ProvidedExtensions.IOHandler handler) throws IOException {
        final Collection<FileNaming> all = new LinkedHashSet<FileNaming>();
        
        FileNaming newNaming = fNaming.rename(newName, handler);
        boolean retVal = newNaming != fNaming;
        
        synchronized(NamingFactory.class) {        
            all.add(newNaming);
            collectSubnames(fNaming, all);
            return (retVal) ? ((FileNaming[]) all.toArray(new FileNaming[all.size()])) : null;
        }
    }
    
    public static Collection<FileNaming> findSubTree(FileNaming root) {
        final Collection<FileNaming> all = new LinkedHashSet<FileNaming>();
        synchronized (NamingFactory.class) {
            collectSubnames(root, all);
        }
        return all;
    }
    
    private static void collectSubnames(FileNaming root, Collection<FileNaming> all) {
        assert Thread.holdsLock(NamingFactory.class);
        Collection<FileNaming> not = new HashSet<FileNaming>(names.length);
        for (int i = 0; i < names.length; i++) {
            NameRef value = names[i];
            while (value != null) {
                FileNaming fN = value.get();
                Deque<FileNaming> above = new ArrayDeque<FileNaming>();
                for (FileNaming up = fN;;) {
                    if (up == null || not.contains(up)) {
                        not.addAll(above);
                        break;
                    }
                    above.addFirst(up);
                    if (root.equals(up) || all.contains(up)) {
                        all.addAll(above);
                        break;
                    }
                    up = up.getParent();
                }
                value = value.next();
            }
        }
    }
    
    public static Integer createID(final File file) {
        return Utils.hashCode(file);
    }
    private static FileNaming registerInstanceOfFileNaming(
        FileNaming parentName, FileInfo file, FileType type,
        Collection<? super FileInfo> computeDirectoryStatus
    ) {
        return NamingFactory.registerInstanceOfFileNaming(
            parentName, file, null,false, type, computeDirectoryStatus
        );
    }
    
    private static void rehash(int newSize) {
        assert Thread.holdsLock(NamingFactory.class);
        NameRef[] arr = new NameRef[newSize];
        for (int i = 0; i < names.length; i++) {
            NameRef v = names[i];
            if (v == null) {
                continue;
            }
            for (NameRef nr : names[i].disconnectAll()) {
                FileNaming fn = nr.get();
                if (fn == null) {
                    continue;
                }
                Integer id = createID(fn.getFile());
                int index = Math.abs(id) % arr.length;
                NameRef prev = arr[index];
                arr[index] = nr;
                if (prev == null) {
                    nr.setIndex(index);
                } else {
                    nr.setNext(prev);
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            assert checkIndex(arr, i);
        }
        names = arr;
    }

    private static FileNaming registerInstanceOfFileNaming(
        final FileNaming parentName, final FileInfo file, 
        final FileNaming newValue,boolean ignoreCache, FileType type,
        Collection<? super FileInfo> computeDirectoryStatus
    ) {
        assert Thread.holdsLock(NamingFactory.class);
        
        cleanQueue();
        
        FileNaming retVal;
        Integer key = createID(file.getFile());
        int index = Math.abs(key) % names.length;
        NameRef ref = getReference(names[index], file.getFile());

        FileNaming cachedElement = (ref != null) ? (FileNaming) ref.get() : null;
        Boolean cachedIsDirectory = null;
        Boolean fileIsDirectory = null;
        if (ignoreCache) {
            if (cachedElement != null) {
                cachedIsDirectory = cachedElement.isDirectory();
                if (!file.isDirectoryComputed()) {
                    computeDirectoryStatus.add(file);
                    return null;
                }
                fileIsDirectory = file.isDirectory();
                if (cachedIsDirectory != fileIsDirectory) {
                    cachedElement = null;
                }
            }
            if (cachedElement != null) {
                try {
                    checkCaseSensitivity(cachedElement, file.getFile());
                } catch (IOException ex) {
                    // OK, give up
                }
            }
        }

        Boolean filesEqual = null;
        if (
            cachedElement != null && 
            (filesEqual = Utils.equals(cachedElement.getFile(), file.getFile()))
        ) {
            retVal = cachedElement;
        } else {
            if (newValue == null) {
                if (type == FileType.unknown && !file.isDirectoryComputed()) {
                    computeDirectoryStatus.add(file);
                    return null;
                }
                retVal = NamingFactory.createFileNaming(file, key, parentName, type);
            } else {
                retVal = newValue;
            }
            NameRef refRetVal = new NameRef(retVal);

            NameRef prev = names[index];
            names[index] = refRetVal;
            if (prev == null) {
                refRetVal.setIndex(index);
            } else {
                refRetVal.setNext(prev);
            }
            assert checkIndex(names, index);
            if (ref != null) {
                NameRef nr = refRetVal;
                for (;;) {
                    if (nr.next() == ref) {
                        FileNaming orig = ref.get();
                        if (orig instanceof FileName) {
                            ((FileName)orig).recordCleanup(
                                "cachedElement: " + cachedElement + // NOI18N 
                                " ref: " + orig + // NOI18N
                                " file: " + file + // NOI18N
                                " filesEqual: " + filesEqual + // NOI18N
                                " cachedIsDirectory: " + cachedIsDirectory + // NOI18N
                                " fileIsDirectory: " + fileIsDirectory // NOI18N
                            );
                        }
                        ref.clear();
                        nr.skip(ref);
                        break;
                    }
                    nr = nr.next();
                }
            } else {
                namesCount++;
            }                
            assert checkIndex(names, index);
            if (namesCount * 4 > names.length * 3) {
                rehash(names.length * 2);
            }
        }
        assert retVal != null;
        return retVal;
    }
    
    private static NameRef getReference(NameRef value, File f) {
        while (value != null) {
            FileNaming fn = value.get();
            if (fn != null && Utils.equals(fn.getFile(), f)) {
                return value;
            }
            value = value.next();
        }
        return null;
    }

    static enum FileType {file, directory, unknown}
    
    private static FileNaming createFileNaming(
        final FileInfo f, Integer theKey, final FileNaming parentName, FileType type
    ) {
        FileName retVal = null;
        //TODO: check all tests for isFile & isDirectory
        if (type.equals(FileType.unknown)) {
            if (f.isDirectory()) {
                type = FileType.directory;
            } else {
                //important for resolving  named pipes
                 type = FileType.file;
            }            
        }
        switch(type) {
            case file:
                retVal = new FileName(parentName, f.getFile(), theKey);
                break;
            case directory:
                retVal = new FolderName(parentName, f.getFile(), theKey);
                break;
        }
        return retVal;
    }
    
    public static String dumpId(Integer id) {
        return dump(id, null);
    }
    
    public static synchronized boolean isValid(FileNaming fn) {
        int index = Math.abs(fn.getId()) % names.length;
        NameRef value = names[index];
        while (value != null) {
            if (value.get() == fn) {
                return true;
            }
            value = value.next();
        }
        return false;
    }

    
    synchronized static String dump(Integer id, File file) {
        StringBuilder sb = new StringBuilder();
        final String hex = Integer.toHexString(id);

        sb.append("Showing references to ").append(hex).append("\n");
        int cnt = 0;
        int index = Math.abs(id) % names.length;
        NameRef value = names[index];
        while (value != null) {
            if (file == null || file.equals(value.getFile())) {
                cnt++;
                dumpFileNaming(sb, value.get());
            }
            value = value.next();
        } 
        sb.append("References: ").append(cnt);
        return sb.toString();
    }
    private static void dumpFileNaming(StringBuilder sb, Object fn) {
        if (fn == null) {
            sb.append("null");
            return;
        }
        if (fn instanceof FolderName) {
            sb.append("FolderName: ");
        } else {
            sb.append("FileName: ");
        }
        sb.append(fn).append("#")
           .append(Integer.toHexString(fn.hashCode())).append("@")
           .append(Integer.toHexString(System.identityHashCode(fn)))
           .append("\n");
        if (fn instanceof FileName) {
            ((FileName)fn).dumpCreation(sb);
        }
    }
    
    private static void cleanQueue() {
        assert Thread.holdsLock(NamingFactory.class);
        for (;;) {
            NameRef nr = (NameRef)NameRef.QUEUE.poll();
            if (nr == null) {
                return;
            }
            int index = nr.getIndex();
            if (index == -1) {
                continue;
            }
            if (names[index] != null) {
                names[index] = names[index].remove(nr);
                namesCount--;
            }
            assert checkIndex(names, index);
        }
    }
    private static boolean checkIndex(NameRef[] arr, int index) {
        if (arr[index] == null) {
            return true;
        } else {
            return index == arr[index].getIndex();
        }
    }
}
