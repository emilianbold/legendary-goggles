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
package org.netbeans.modules.java.preprocessorbridge.spi;

import com.sun.source.tree.CompilationUnitTree;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * JavaCustomIndexer plugin called during scan on fully attributed trees.
 * @since 1.21
 * @author Tomas Zezula
 */
public interface JavaIndexerPlugin {


    /**
     * Process given attributed compilation unit.
     * @param toProcess the compilation unit to process
     * @param relativePath the relative path of the source in the source root
     * @param services a {@link Lookup} containing javac services (Elements, Types, Trees)
     */
    public void process (@NonNull CompilationUnitTree toProcess, @NonNull String relativePath, @NonNull Lookup services);

    /**
     * Handles deletion of given source file.
     * @param relativePath the relative path of the deleted source file inside the source root
     */
    public void delete (@NonNull String relativePath);

    /**
     * Called when the {@link JavaIndexerPlugin} is not more used.
     * The implementor may do any clean up, storing of metadata.
     */
    public void finish ();

    /**
     * Factory to create JavaIndexerPlugin.
     * The factory instance should be registered in mime lookup.
     */
    public interface Factory {
        /**
         * Creates a new instance of {@link JavaIndexerPlugin}.
         * @param root the source root for which the plugin is created
         * @param cacheFolder used to store metadata
         * @return the new instance of {@link JavaIndexerPlugin} or null
         * if the factory does not handle given source root
         */
        @CheckForNull
        JavaIndexerPlugin create(@NonNull FileObject root, @NonNull FileObject cacheFolder);
    }
}
