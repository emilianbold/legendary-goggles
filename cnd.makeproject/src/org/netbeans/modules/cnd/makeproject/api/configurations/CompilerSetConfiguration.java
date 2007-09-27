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

package org.netbeans.modules.cnd.makeproject.api.configurations;

import java.util.ArrayList;
import org.netbeans.modules.cnd.api.compilers.CompilerSet;
import org.netbeans.modules.cnd.api.compilers.CompilerSetChangeListener;
import org.netbeans.modules.cnd.api.compilers.CompilerSetEvent;
import org.netbeans.modules.cnd.api.compilers.CompilerSetManager;

public class CompilerSetConfiguration extends IntConfiguration implements CompilerSetChangeListener {
    
    private ArrayList<String> displayNames = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();
    private CompilerSetConfiguration master;
    private int def;
    private int value;
    private boolean modified;
    private String oldname;
    
    /**
     * A configuration for compiler set names.
     *
     * @param master ???
     * @param def The default value
     * @param displayNames An array of compiler set display names
     * @param optoins An array of compiler set names
     */
    public CompilerSetConfiguration(CompilerSetConfiguration master, int def, String[] displayNames, String[] names) {
        CompilerSetManager.addCompilerSetChangeListener(this);
        
        for (int i = 0; i < displayNames.length; i++) {
            this.displayNames.add(displayNames[i]);
            this.names.add(names[i]);
        }
        this.master = master;
        this.def = def;
        value = def;
        modified = false;
        oldname = null;
    }
    
    @Override
    public void setValue(int value) {
        if (value >= 0 && value <= displayNames.size()) {
            this.value = value;
        }
        if (master != null) {
            setModified(true);
        } else {
            setModified(value != getDefault());
        }
    }

    @Override
    public void setValue(String s) {
	if (s != null) {
            int i = 0;
            for (String csname : displayNames) {
                if (s.equals(csname)) {
                    setValue(i);
                    return;
                }
                i++;
            }
            i = 0;
            for (String csname : names) {
                if (s.equals(csname)) {
                    setValue(i);
                    return;
                }
                i++;
            }
            oldname = s;
	}
    }
    
    @Override
    public int getValue() {
        if (master != null && !getModified()) {
            return master.getValue();
        } else {
            return value;
        }
    }
    
    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    @Override
    public boolean getModified() {
        return modified;
    }
    
    @Override
    public int getDefault() {
        return def;
    }
    
    @Override
    public void reset() {
        value = getDefault();
        setModified(false);
    }

    @Override
    public String getName() {
        if (getValue() >= 0 && getValue() < displayNames.size()) {
	    return displayNames.get(getValue());
        } else {
	    return "";
        }
    }
    
    @Override
    public String[] getNames() {
        return displayNames.toArray(new String[0]);
    }

    @Override
    public String getOption() {
        if (getValue() >= 0 && getValue() <= names.size()) {
            return names.get(getValue());
        } else {
            return "";
        }
    }

    public int getDef() {
        return def;
    }
    
    public void assign(CompilerSetConfiguration conf) {
        setDirty(getValue() != conf.getValue());
        setValue(conf.getValue());
        setModified(conf.getModified());
    }

    @Override
    public Object clone() {
	CompilerSetConfiguration clone = new CompilerSetConfiguration(master, 
                def, displayNames.toArray(new String[0]), names.toArray(new String[0]));
	clone.setValue(getValue());
	clone.setModified(getModified());
	return clone;
    }
    
    public void compilerSetChange(CompilerSetEvent ev) {
        CompilerSetManager csm = (CompilerSetManager) ev.getSource();
        String defdname = displayNames.get(def);
        String curdname = value >= 0 ? displayNames.get(value) : "xxx"; // NOI18N - want non-matching string
        String savename = names.get(value);
        ArrayList<String> newNames = new ArrayList<String>();
        ArrayList<String> newDisplayNames = new ArrayList<String>();
        def = value = -1;
        
        int i = 0;
        for (CompilerSet cs : csm.getCompilerSets()) {
            newNames.add(cs.getName());
            newDisplayNames.add(cs.getDisplayName());
            if (cs.getDisplayName().equals(defdname)) {
                def = i;
            }
            if (cs.getDisplayName().equals(curdname)) {
                value = i;
            }
        }
        displayNames = newDisplayNames;
        names = newNames;
        
        // Save old name in case the selected compiler set gets removed from the user's path. This
        // lets it get restored during build or open validation
        if (oldname != null) {
            if (names.contains(oldname)) {
                value = names.indexOf(oldname);
                oldname = null;
            }
        } else if (!names.contains(savename)) {
            oldname = savename;
        }
        
        // Since I set them to -1 at the start of this method, ensure they don't get left that way
        if (def < 0) {
            def = 0;
        }
        if (value < 0) {
            value = def;
        }
    }
    
    /**
     * If oldname != null then we've had our compiler set removed. We save the oldname
     * so we can ask the user if they want to repair their PATH before operating on
     * the project.
     */
    public boolean isValid() {
        return oldname == null;
    }
    
    public void setValid() {
        oldname = null;
    }
    
    public String getOldName() {
        return oldname;
    }
}
