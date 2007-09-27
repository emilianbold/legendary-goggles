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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.cnd.ui.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.netbeans.modules.cnd.settings.CppSettings;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/** Manage the data for the ToolsPanel */
public abstract class ToolsPanelModel {
    
    protected ArrayList getPath() {
        ArrayList<String> path = new ArrayList();
        StringTokenizer tok = new StringTokenizer(CppSettings.getDefault().getPath(), File.pathSeparator);
        while (tok.hasMoreTokens()) {
            path.add(tok.nextToken());
        }
        return path;
    }
    
    protected void setPath(ArrayList<String> list) {
        StringBuffer path = new StringBuffer();
        for (String dir : list) {
            path.append(dir);
            path.append(File.pathSeparator);
        }
        CppSettings.getDefault().setPath(path.toString());
    }
    
    protected String getMakeName() {
        return CppSettings.getDefault().getMakeName();
    }
    
    protected void setMakeName(String name) {
        CppSettings.getDefault().setMakeName(name);
    }
    
    protected void setMakePath(String dir) {
        CppSettings.getDefault().setMakePath(dir);
    }
    
    public String getGdbName() {
        return CppSettings.getDefault().getGdbName();
    }
    
    public void setGdbName(String name) {
        CppSettings.getDefault().setGdbName(name);
    }
    
    public String getGdbPath() {
        return null;
    }
    
    public void setGdbPath(String dir) {
        CppSettings.getDefault().setGdbPath(dir);
    }
    
    /**
     * Check if the gdb module is enabled. Don't show the gdb line if it isn't.
     *
     * @return true if the gdb module is enabled, false if missing or disabled
     */
    protected boolean isGdbEnabled() {
        Iterator iter = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class)).allInstances().iterator();
        while (iter.hasNext()) {
            ModuleInfo info = (ModuleInfo) iter.next();
            if (info.getCodeNameBase().equals("org.netbeans.modules.cnd.debugger.gdb") && info.isEnabled()) { // NOI18N
                return true;
            }
        }
        return false;
    }
    
    public abstract void setGdbEnabled(boolean value);
    
    public abstract boolean isGdbRequired();
    
    public abstract void setGdbRequired(boolean value);
    
    public abstract boolean isCRequired();
    
    public abstract void setCRequired(boolean value);
    
    public abstract boolean isCppRequired();
    
    public abstract void setCppRequired(boolean value);
    
    public abstract boolean isFortranRequired();
    
    public abstract void setFortranRequired(boolean value);
    
    public abstract void setCompilerSetName(String name);
    
    public abstract String getCompilerSetName();
    
    protected abstract void setCCompilerName(String name);
    
    protected abstract void setCppCompilerName(String name);
    
    protected abstract void setFortranCompilerName(String name);
}
