/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.prefs.Preferences;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;


public class JSPreferences {
    
    public enum JSVersion {

        ECMA5("ECMAScript 5.1"),
        ECMA6("ECMAScript 6");
//        ECMA7("ECMAScript 7");
        
        private final String displayName;

        private JSVersion(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
        
        @CheckForNull
        public static JSVersion fromString(String str) {
            if (str == null) {
                return null;
            }
            for (JSVersion v : EnumSet.allOf(JSVersion.class)) {
                if (str.equals(v.name()) || str.equals(v.getDisplayName())) {
                    return v;
                }
            }
            return null;
        }
    }
    
    private static final JSVersion DEFAULT_JS_VERSION = JSVersion.ECMA6;
    private static final String JS_PREF_TAG = "jsversion"; // NOI18N

    public static List<JSVersion> getECMAScriptAvailableVersions() {
        return new ArrayList<>(EnumSet.allOf(JSVersion.class));
    }

    public static JSVersion getECMAScriptVersion(Project project) {
        if (project != null) {
            String strValue = getPreferences(project).get(JS_PREF_TAG, null);
            JSVersion version = JSVersion.fromString(strValue);
            if (version == null) {
                version = DEFAULT_JS_VERSION;
            }
            return version;
        }
        return DEFAULT_JS_VERSION;
    }

    public static void putECMAScriptVersion(Project project, JSVersion version) {
        if (project != null) {
            if (!version.equals(DEFAULT_JS_VERSION)) {
                getPreferences(project).put(JS_PREF_TAG, version.toString());
            } else {
                getPreferences(project).remove(JS_PREF_TAG);
            }
        }
    }
    
    private static Preferences getPreferences(Project project) {
        return ProjectUtils.getPreferences(project, JSPreferences.class, true);
    }

    public static boolean isPreECMAScript6(Project project) {
        return getECMAScriptVersion(project).ordinal() < JSVersion.ECMA6.ordinal();
    }
    
//    public static boolean isPreECMAScript7(Project project) {
//        return getECMAScriptVersion(project).ordinal() < JSVersion.ECMA7.ordinal();
//    }
    
}
