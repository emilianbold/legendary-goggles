/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.subversion.remote.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.versioning.core.Utils;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.api.VersioningSupport;

/**
 * Encapsulates context of an action. There are two ways in which context may be defined:
 * - list of files (f/b.txt, f/c.txt, f/e.txt)
 * - list of roots (top folders) plus list of exclusions (f),(a.txt, d.txt)
 *
 * @author Maros Sandor
 */
public class Context implements Serializable {

    public static final Context Empty = new Context( getEmptyList(), getEmptyList(), getEmptyList() );

    private static final long serialVersionUID = 1L;
    
    private final List<VCSFileProxy> filteredFiles;
    private final List<VCSFileProxy> rootFiles;
    private final List<VCSFileProxy> exclusions;

    public Context(List<VCSFileProxy> filteredFiles, List<VCSFileProxy> rootFiles, List<VCSFileProxy> exclusions) {
        this.filteredFiles = filteredFiles;
        this.rootFiles = rootFiles;
        this.exclusions = exclusions;
        while (normalize());
    }

    public Context(VCSFileProxy file) {
        this(new VCSFileProxy [] { file });
    }

    public Context(VCSFileProxy [] files) {
        List<VCSFileProxy> list = new ArrayList<VCSFileProxy>(files.length);
        list.addAll(Arrays.asList(files));
        removeDuplicates(list);
        this.filteredFiles = list;
        this.rootFiles = list;
        this.exclusions = Collections.emptyList();
    }

    private boolean normalize() {
        for (Iterator<VCSFileProxy> i = rootFiles.iterator(); i.hasNext();) {
            VCSFileProxy root = i.next();
            for (Iterator<VCSFileProxy> j = exclusions.iterator(); j.hasNext();) {
                VCSFileProxy exclusion = j.next();
                if (Utils.isAncestorOrEqual(exclusion, root)) {
                    j.remove();
                    exclusionRemoved(exclusion, root);
                    return true;
                }
            }
        }
        removeDuplicates(rootFiles);
        removeDuplicates(exclusions);
        return false;
    }

    private void removeDuplicates(List<VCSFileProxy> files) {
        List<VCSFileProxy> newFiles = new ArrayList<VCSFileProxy>();
        outter: for (Iterator<VCSFileProxy> i = files.iterator(); i.hasNext();) {
            VCSFileProxy file = i.next();
            for (Iterator<VCSFileProxy> j = newFiles.iterator(); j.hasNext();) {
                VCSFileProxy includedFile = j.next();
                if (Utils.isAncestorOrEqual(includedFile, file) && (file.isFile() || !VersioningSupport.isFlat(includedFile))) {
                    continue outter;
                }
                if (Utils.isAncestorOrEqual(file, includedFile) && (includedFile.isFile() || !VersioningSupport.isFlat(file))) {
                    j.remove();
                }
            }
            newFiles.add(file);
        }
        files.clear();
        files.addAll(newFiles);
    }
    
    private void exclusionRemoved(VCSFileProxy exclusion, VCSFileProxy root) {
        VCSFileProxy [] exclusionChildren = exclusion.listFiles();
        if (exclusionChildren == null) {
            return;
        }
        for (int i = 0; i < exclusionChildren.length; i++) {
            VCSFileProxy child = exclusionChildren[i];
            if (!Utils.isAncestorOrEqual(root, child)) {
                exclusions.add(child);
            }
        }
    }

    public List<VCSFileProxy> getRoots() {
        return rootFiles;
    }

    public List<VCSFileProxy> getExclusions() {
        return exclusions;
    }

    /**
     * Gets exact set of files to operate on, it is effectively defined as (rootFiles - exclusions). This set
     * is NOT suitable for Update command because Update should operate on all rootFiles and just exclude some subfolders.
     * Otherwise update misses new files and folders directly in rootFiles folders. 
     *  
     * @return files to operate on
     */ 
    public VCSFileProxy [] getFiles() {
        return filteredFiles.toArray(new VCSFileProxy[filteredFiles.size()]);
    }

    public VCSFileProxy[] getRootFiles() {
        return rootFiles.toArray(new VCSFileProxy[rootFiles.size()]);
    }
    
    public boolean contains(VCSFileProxy file) {
        outter : for (Iterator i = rootFiles.iterator(); i.hasNext();) {
            VCSFileProxy root = (VCSFileProxy) i.next();
            if (SvnUtils.isParentOrEqual(root, file)) {
                for (Iterator j = exclusions.iterator(); j.hasNext();) {
                    VCSFileProxy excluded = (VCSFileProxy) j.next();
                    if (Utils.isAncestorOrEqual(excluded, file)) {
                        continue outter;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static final List<VCSFileProxy> getEmptyList() {
        return Collections.emptyList();
    }
}
