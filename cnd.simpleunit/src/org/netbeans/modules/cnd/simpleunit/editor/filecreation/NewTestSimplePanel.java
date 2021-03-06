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

package org.netbeans.modules.cnd.simpleunit.editor.filecreation;

import java.awt.Component;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.cnd.editor.filecreation.CndPanel;
import org.netbeans.modules.cnd.editor.filecreation.NewCndFileChooserPanel;
import org.netbeans.modules.cnd.simpleunit.utils.MakefileUtils;
import org.netbeans.modules.cnd.utils.MIMEExtensions;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author Nikolay Krasilnikov (nnnnnk@netbeans.org)
 */
public class NewTestSimplePanel extends CndPanel {

    private final String baseTestName;
    private final MIMEExtensions es;
    private final String defaultExt;
    private final boolean fileWithoutExtension;

    NewTestSimplePanel(Project project, SourceGroup[] folders, WizardDescriptor.Panel<WizardDescriptor> bottomPanel, MIMEExtensions es, String defaultExt, String baseTestName) {
        super(project, folders, bottomPanel);
        this.baseTestName = baseTestName;
        this.es = es;
        this.defaultExt = defaultExt;
        this.fileWithoutExtension = "".equals(defaultExt);
    }

    public Component getComponent() {
        if (gui == null) {
            gui = new NewTestSimplePanelGUI(project, folders, bottomPanel == null ? null : bottomPanel.getComponent(), es, defaultExt, baseTestName);
            gui.addChangeListener(this);
        }
        return gui;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("CreateTestWizardP2");
    }

    @Override
    protected void doStoreSettings(WizardDescriptor settings) {
        if (getTargetExtension().length() > 0) {
            if (((NewTestSimplePanelGUI)gui).useTargetExtensionAsDefault()) {
                es.setDefaultExtension(getTargetExtension());
            } else {
                es.addExtension(getTargetExtension());
            }
        }
    }

    @Override
    public boolean isValid() {
        boolean ok = super.isValid();

        setErrorMessage (""); // NOI18N

        if (!ok) {

            return false;
        }

        String documentName = gui.getTargetName();

        if ((!fileWithoutExtension && getTargetExtension().length() == 0) || documentName.charAt(0) == '.') {
            // ignore invalid filenames
            setErrorMessage(NbBundle.getMessage(NewCndFileChooserPanel.class, "MSG_Invalid_File_Name"));
            return false;
        }

        if (!fileWithoutExtension && !es.getValues().contains(getTargetExtension())) {
            //MSG_new_extension_introduced
            String msg = NbBundle.getMessage(NewCndFileChooserPanel.class, "MSG_new_extension_introduced", getTargetExtension()); // NOI18N

            setErrorMessage(msg); // NOI18N
        }

        // check if the file name can be created
        String errorMessage = canUseFileName(gui.getTargetGroup().getRootFolder(), gui.getTargetFolder(), documentName, false);
        if (errorMessage != null) {
            setErrorMessage(errorMessage); // NOI18N
            return false;
        }

        if (MakefileUtils.getMakefile(project) == null) {
            setInfoMessage( NbBundle.getMessage(NewTestSimplePanel.class, "MSG_Missing_Makefile") );
            return false;
        }
        
        return true;
    }

    private String getTargetExtension() {
        return ((NewTestSimplePanelGUI)gui).getTargetExtension();
    }

    public static FileObject getTemplateFileObject(String formType) {
        return FileUtil.getConfigFile("Templates/testFiles/" + formType); // NOI18N
    }

    public static DataObject getTemplateDataObject(String formType) {
        FileObject fileObj = getTemplateFileObject(formType);
        if (fileObj != null) {
            try {
                return DataObject.find(fileObj);
            } catch (DataObjectNotFoundException ex) {
                // do nothing here, return null later
            }
        }
        return null;
    }

    public static String getTemplateDisplayName(String formType) {
        DataObject dataObj = getTemplateDataObject(formType);
        if (dataObj != null) {
            return dataObj.getNodeDelegate().getDisplayName();
        }
        return formType;
    }
}
