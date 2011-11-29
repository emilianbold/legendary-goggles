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
package org.netbeans.modules.refactoring.java.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.MoveRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.java.RefactoringUtils;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUIBypass;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

public class MoveClassUI implements RefactoringUI, RefactoringUIBypass {
    
    private DataObject javaObject;    
    private MoveClassPanel panel;
    private MoveRefactoring refactoring;
    private String targetPkgName = "";
    private boolean disable;
    private FileObject targetFolder;
    private PasteType pasteType;
    
    static final String getString(String key) {
        return NbBundle.getMessage(MoveClassUI.class, key);
    }
    
    public MoveClassUI (DataObject javaObject) {
        this(javaObject, null, null, Collections.<TreePathHandle>emptyList());
    }
    
    public MoveClassUI (DataObject javaObject, FileObject targetFolder, PasteType pasteType, Collection<TreePathHandle> handles) {
        this.disable = targetFolder != null ;
        this.targetFolder = targetFolder;
        this.javaObject = javaObject;
        this.pasteType = pasteType;
        this.refactoring = new MoveRefactoring(Lookups.fixed(javaObject.getPrimaryFile(), handles.toArray(new Object[handles.size()])));
        this.refactoring.getContext().add(RefactoringUtils.getClasspathInfoFor(javaObject.getPrimaryFile()));
    }
    
    @Override
    public String getName() {
        return getString ("LBL_MoveClass");
    }
     
    @Override
    public String getDescription() {
        return new MessageFormat(getString("DSC_MoveClass")).format(
                new Object[] {javaObject.getName(), packageName()}
        );
    }
    
    @Override
    public boolean isQuery() {
        return false;
    }
        
    @Override
    public CustomRefactoringPanel getPanel(ChangeListener parent) {
        if (panel == null) {
            String pkgName = targetFolder!=null?getPackageName(targetFolder):getPackageName(javaObject.getPrimaryFile().getParent());
            panel = new MoveClassPanel (parent, pkgName, 
                    new MessageFormat(getString("LBL_MoveClassNamed")).format (
                    new Object[] {javaObject.getPrimaryFile().getName()}
                ),
                targetFolder != null ? targetFolder : (javaObject != null ? javaObject.getPrimaryFile(): null)
            );
            panel.setCombosEnabled(!disable);
        }
        return panel;
    }
    
    private static String getPackageName(FileObject file) {
        ClassPath cp = ClassPath.getClassPath(file, ClassPath.SOURCE);
        return cp.getResourceName(file, '.', false);
    }

    private String packageName () {
        return targetPkgName.trim().length() == 0 ? getString ("LBL_DefaultPackage") : targetPkgName.trim ();
    }
    
    private Problem setParameters(boolean checkOnly) {
        if (panel==null)
            return null;
        targetPkgName = panel.getPackageName ();

        URL url = URLMapper.findURL(panel.getRootFolder(), URLMapper.EXTERNAL);
        try {
            refactoring.setTarget(Lookups.singleton(new URL(url.toExternalForm() + panel.getPackageName().replace('.','/')))); // NOI18N
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (checkOnly) {
            return refactoring.fastCheckParameters();
        } else {
            return refactoring.checkParameters();
        }
    }
    
    @Override
    public Problem checkParameters() {
        return setParameters(true);
    }
    
    @Override
    public Problem setParameters() {
        return setParameters(false);
    }
    
    @Override
    public AbstractRefactoring getRefactoring() {
        return refactoring;
    }
    
    @Override
    public boolean hasParameters() {
        return true;
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(MoveClassUI.class);
    }

    @Override
    public boolean isRefactoringBypassRequired() {
        return !panel.isUpdateReferences();
    }
    @Override
    public void doRefactoringBypass() throws IOException {
        pasteType.paste();
    }
}
