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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.web.jsf.editor.facelets;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import org.netbeans.modules.web.common.taginfo.LibraryMetadata;
import org.netbeans.modules.web.jsfapi.api.NamespaceUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 *
 * @deprecated use DefaultFaceletLibraries
 *
 * @todo remove this class along with the **.libdefs.* package content
 * since DefaultFaceletLibraries provides basically the same info
 */
public class FaceletsLibraryMetadata {
    private static Map<String, LibraryMetadata> libMap = new TreeMap<>();

    static {
        loadLib("composite");  //NOI18N
        loadLib("core");  //NOI18N
        loadLib("functions");  //NOI18N
        loadLib("html");  //NOI18N
        loadLib("ui");  //NOI18N
    }

    public static LibraryMetadata get(String libraryURL){
        LibraryMetadata metadata = libMap.get(libraryURL);
        if (metadata == null) {
            String legacyNamespace = NamespaceUtils.NS_MAPPING.get(libraryURL);
            if (legacyNamespace != null) {
                metadata = libMap.get(legacyNamespace);
            }
        }
        return metadata;
    }

    private static void loadLib(String filePath){
        InputStream is = FaceletsLibraryMetadata.class.getResourceAsStream("libdefs/" + filePath + ".xml"); //NOI18N

        try {
            LibraryMetadata lib = LibraryMetadata.readFromXML(is);
            libMap.put(lib.getId(), lib);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
