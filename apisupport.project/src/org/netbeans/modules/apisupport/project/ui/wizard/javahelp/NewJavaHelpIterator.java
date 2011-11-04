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

package org.netbeans.modules.apisupport.project.ui.wizard.javahelp;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modules.apisupport.project.api.ManifestManager;
import org.netbeans.modules.apisupport.project.api.UIUtil;
import org.netbeans.modules.apisupport.project.ui.wizard.common.BasicWizardIterator;
import org.netbeans.modules.apisupport.project.ui.wizard.common.CreatedModifiedFiles;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Exceptions;

/**
 * Wizard for creating JavaHelp
 *
 * @author Radek Matous, Jesse Glick
 */
@TemplateRegistration(folder = UIUtil.TEMPLATE_FOLDER, position = 900, displayName = "#Templates/NetBeansModuleDevelopment/newJavaHelp", iconBase = "org/netbeans/modules/apisupport/project/ui/resources/newJavaHelp.png", description = "/org/netbeans/modules/apisupport/project/ui/resources/newJavaHelp.html", category = UIUtil.TEMPLATE_CATEGORY)
public class NewJavaHelpIterator extends BasicWizardIterator {
    
    private NewJavaHelpIterator.DataModel data;
    
    public Set instantiate() throws IOException {
        CreatedModifiedFiles cmf = data.getCreatedModifiedFiles();
        cmf.run();
        return getCreatedFiles(cmf, data.getProject());
    }
    
    protected BasicWizardIterator.Panel[] createPanels(WizardDescriptor wiz) {
        data = new NewJavaHelpIterator.DataModel(wiz);
        return new BasicWizardIterator.Panel[] {
            new JavaHelpPanel(wiz, data)
        };
    }
    
    public @Override void uninitialize(WizardDescriptor wiz) {
        super.uninitialize(wiz);
        data = null;
    }
    
    static final class DataModel extends BasicWizardIterator.BasicDataModel {
        
        private static final String TEMPLATE_SUFFIX_HS = "-hs.xml"; // NOI18N
        private static final String[] TEMPLATE_SUFFIXES = {
            TEMPLATE_SUFFIX_HS,
            "-idx.xml", // NOI18N
            "-map.xml", // NOI18N
            "-toc.xml", // NOI18N
            "-about.html", // NOI18N
        };
        private static final String[] TEMPLATE_RESOURCES = {
            "template_myplugin.xml", // NOI18N
            "template_myplugin-idx.xml", // NOI18N
            "template_myplugin-map.xml", // NOI18N
            "template_myplugin-toc.xml", // NOI18N
            "template_myplugin-about.html", // NOI18N
        };
        
        private CreatedModifiedFiles files;
        
        DataModel(WizardDescriptor wiz) {
            super(wiz);
        }
        
        public CreatedModifiedFiles getCreatedModifiedFiles() {
            if (files == null) {
                // org.netbeans.modules.foo
                String codeNameBase = getModuleInfo().getCodeNameBase();
                // foo
                String basename = codeNameBase.substring(codeNameBase.lastIndexOf('.') + 1);
                // org/netbeans/modules/foo/docs/
                String path = codeNameBase.replace('.','/') + "/docs/"; // NOI18N
                boolean ann = true;
                try {
                    SpecificationVersion javahelpVersion = getModuleInfo().getDependencyVersion("org.netbeans.modules.javahelp");
                    if (javahelpVersion != null && javahelpVersion.compareTo(new SpecificationVersion("2.20")) < 0) {
                        ann = false;
                    }
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                }
                
                files = new CreatedModifiedFiles(getProject());
                Map<String,String> tokens = new HashMap<String,String>();
                tokens.put("CODE_NAME", basename); // NOI18N
                tokens.put("FULL_CODE_NAME", codeNameBase); // NOI18N
                tokens.put("DISPLAY_NAME", ProjectUtils.getInformation(getProject()).getDisplayName()); // NOI18N
                // Pick an arbitrary place to put it. Can always be moved elsewhere if anyone cares:
                int position = 3000 + new Random().nextInt(1000);

                if (ann) {
                    files.add(files.addModuleDependency("org.netbeans.modules.javahelp"));
                    Map<String,Object> attrs = new LinkedHashMap<String,Object>();
                    attrs.put("helpSet", basename + TEMPLATE_SUFFIX_HS);
                    attrs.put("position", position);
                    files.add(files.packageInfo(codeNameBase + ".docs", Collections.singletonMap("org.netbeans.api.javahelp.HelpSetRegistration", attrs)));
                } else {
                    tokens.put("HELPSET_PATH", path + basename + TEMPLATE_SUFFIX_HS); // NOI18N
                    files.add(files.createLayerEntry("Services/JavaHelp/" + basename + "-helpset.xml", // NOI18N
                            CreatedModifiedFiles.getTemplate("template_myplugin-helpset.xml"), // NOI18N
                            tokens,
                            null,
                            Collections.singletonMap("position", position))); // NOI18N
                    files.add(files.addManifestToken(ManifestManager.OPENIDE_MODULE_REQUIRES, "org.netbeans.api.javahelp.Help")); // NOI18N
                }

                boolean isMaven = getProject().getProjectDirectory().getFileObject("nbproject/project.properties") == null; // XXX clumsy... but irrelevant unless !ann
                
                //copying templates
                for (int i = 0; i < TEMPLATE_SUFFIXES.length; i++) {
                    FileObject template = CreatedModifiedFiles.getTemplate(TEMPLATE_RESOURCES[i]);
                    String filePath = (ann ? getModuleInfo().getResourceDirectoryPath(false) + '/' : isMaven ? "src/main/javahelp/" : "javahelp/") + path + basename + TEMPLATE_SUFFIXES[i]; // NOI18N
                    files.add(files.createFileWithSubstitutions(filePath, template, tokens));
                }

                if (!ann && !isMaven) {
                    // edit some properties
                    Map<String,String> props = new HashMap<String,String>();
                    // Default for javahelp.base (org/netbeans/modules/foo/docs) is correct.
                    // For <checkhelpset> (nb.org modules only):
                    props.put("javahelp.hs", basename + TEMPLATE_SUFFIX_HS); // NOI18N
                    files.add(files.propertiesModification("nbproject/project.properties", props)); // NOI18N
                }
            }
            return files;
        }
        
    }
    
}
