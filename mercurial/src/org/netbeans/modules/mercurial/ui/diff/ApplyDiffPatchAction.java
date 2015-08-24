/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.mercurial.ui.diff;

import org.netbeans.modules.versioning.spi.VCSContext;

import java.awt.event.ActionEvent;
import java.io.File;
import org.netbeans.modules.mercurial.HgProgressSupport;
import org.netbeans.modules.mercurial.Mercurial;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.mercurial.util.HgUtils;
import org.netbeans.modules.mercurial.ui.actions.ContextAction;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.DialogDisplayer;
import org.openide.DialogDescriptor;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.diff.PatchAction;
import org.netbeans.modules.versioning.util.AccessibleJFileChooser;
import org.openide.nodes.Node;

/**
 * ImportDiff action for mercurial: 
 * hg export
 * 
 * @author Padraig O'Briain
 */
@NbBundle.Messages({
    "CTL_MenuItem_ApplyDiffPatch=Appl&y Diff Patch...",
    "CTL_PopupMenuItem_ApplyDiffPatch=Apply Diff Patch...",
    "CTL_PatchDialog_FileFilter=Patch Files (*.diff, *.patch)",
    "ACSD_ApplyDiffPatchBrowseFolder=Lets you browse for patch files.",
    "ApplyDiffPatchBrowse_title=Browse for Patch File",
    "ApplyDiffPatch_Apply=Apply",
    "MSG_ApplyDiffPatch_checkingFile=Checking patch file",
    "MSG_ApplyDiffPatch_importingPatch=Importing Patch"
})
public class ApplyDiffPatchAction extends ContextAction {
    
    @Override
    protected boolean enable(Node[] nodes) {
        return HgUtils.isFromHgRepository(HgUtils.getCurrentContext(nodes));
    }

    @Override
    protected String getBaseName(Node[] nodes) {
        return "CTL_MenuItem_ApplyDiffPatch"; // NOI18N
    }

    @Override
    protected void performContextAction(Node[] nodes) {
        VCSContext ctx = HgUtils.getCurrentContext(nodes);
        final File roots[] = HgUtils.getActionRoots(ctx);
        if (roots == null || roots.length == 0) {
            return;
        }
        final File root = Mercurial.getInstance().getRepositoryRoot(roots[0]);

        final JFileChooser fileChooser = new AccessibleJFileChooser(Bundle.ACSD_ApplyDiffPatchBrowseFolder(), null);
        fileChooser.setDialogTitle(Bundle.ApplyDiffPatchBrowse_title());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonMnemonic(Bundle.ApplyDiffPatch_Apply().charAt(0));
        fileChooser.setApproveButtonText(Bundle.ApplyDiffPatch_Apply());
        fileChooser.setCurrentDirectory(new File(HgModuleConfig.getDefault().getImportFolder()));
        // setup filters, default one filters patch files
        FileFilter patchFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("diff") || f.getName().endsWith("patch") || f.isDirectory();  // NOI18N
            }
            @Override
            public String getDescription() {
                return Bundle.CTL_PatchDialog_FileFilter();
            }
        };
        fileChooser.addChoosableFileFilter(patchFilter);
        fileChooser.setFileFilter(patchFilter);

        DialogDescriptor dd = new DialogDescriptor(fileChooser, Bundle.ApplyDiffPatchBrowse_title());
        dd.setOptions(new Object[0]);
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        
        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String state = e.getActionCommand();
                if (state.equals(JFileChooser.APPROVE_SELECTION)) {
                    final File patchFile = fileChooser.getSelectedFile();
                    final RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(root);
                    new HgProgressSupport() {

                        @Override
                        protected void perform () {
                            if (isNetBeansPatch(patchFile)) {
                                PatchAction.performPatch(patchFile, roots[0]);
                            } else {
                                new ImportDiffAction.ImportDiffProgressSupport(root, patchFile, false, ImportDiffAction.ImportDiffProgressSupport.Kind.PATCH)
                                        .start(rp, root, Bundle.MSG_ApplyDiffPatch_importingPatch());
                            }
                        }
                    }.start(rp, root, Bundle.MSG_ApplyDiffPatch_checkingFile());
                }
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private static boolean isNetBeansPatch (File patchFile) {
        try (BufferedReader reader = Files.newBufferedReader(patchFile.toPath(), Charset.forName("UTF-8"))) {
            boolean netbeansPatch = false;
            boolean cont = true;
            for (String line = reader.readLine(); line != null && cont; line = reader.readLine()) {
                if (line.trim().isEmpty()) {
                    // skip
                } else if (line.startsWith("#")) {
                    line = line.substring(1).trim();
                    if (line.startsWith(ExportDiffChangesAction.PATCH_FILE_HEADER)) {
                        // line was generated by NB HG diff action
                        netbeansPatch = true;
                        cont = false;
                    }
                } else {
                    // start of the patch file itself, NB header not found
                    cont = false;
                }
            }
            return netbeansPatch;
        } catch (IOException ex) {
            return false;
        }
    }
}
