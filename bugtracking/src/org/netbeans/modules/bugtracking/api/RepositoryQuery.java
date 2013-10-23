/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.bugtracking.api;

import java.io.File;
import java.util.Collection;
import org.netbeans.modules.bugtracking.RepositoryImpl;
import org.netbeans.modules.bugtracking.RepositoryRegistry;
import org.netbeans.modules.bugtracking.spi.RepositoryQueryImplementation;
import org.netbeans.modules.bugtracking.BugtrackingOwnerSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;


/**
 *
 * @author Tomas Stupka
 */
public final class RepositoryQuery {
    private static RepositoryQuery instance;
    
    private RepositoryQuery() {};
    
    public static synchronized RepositoryQuery getInstance() {
        if(instance == null) {
            instance = new RepositoryQuery();
        }
        return instance;
    }
    
    public Repository getRepository(FileObject fileObject, boolean askIfUnknown) {
        if(fileObject == null) {
            return null;
        }
        Collection<? extends RepositoryQueryImplementation> impls = getImplementations();
        for (RepositoryQueryImplementation repositoryOwnerQuery : impls) {
            String url = repositoryOwnerQuery.getRepositoryUrl(fileObject);
            if(url != null) {
                Collection<RepositoryImpl> repos = RepositoryRegistry.getInstance().getKnownRepositories(false);
                for (RepositoryImpl r : repos) {
                    if(r.getUrl().equals(url)) {
                        return r.getRepository();
                    }
                }
            }
        }
        return getRepositoryIntern(fileObject, askIfUnknown);
    }

    private Collection<? extends RepositoryQueryImplementation> getImplementations() {
        Collection<? extends RepositoryQueryImplementation> result = Lookup.getDefault().lookupAll(RepositoryQueryImplementation.class);
        return result;
    }
    
    private Repository getRepositoryIntern(FileObject fileObject, boolean askIfUnknown) {
        File file = FileUtil.toFile(fileObject);
        if(file == null) {
            return null;
        }
        RepositoryImpl impl = BugtrackingOwnerSupport.getInstance().getRepository(file, askIfUnknown);
        return impl != null ? impl.getRepository() : null;
    }
    
}
