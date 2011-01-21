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

package org.netbeans.modules.maven.apisupport;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import static org.netbeans.modules.maven.apisupport.Bundle.*;

/**
 * Dialog listing opened Maven NB platform projects with a button to browse for
 * a different project. Path to the select platform project will be saved in
 * given project's auxiliary properties to provide proper context for various UI
 * operations (e.g. branding editor or XML layer in context).
 *
 * @author S. Aubrecht
 */
final class SelectPlatformAppModulePanel extends javax.swing.JPanel {

    private DialogDescriptor dd;
    private Project appModuleProject;
    private final ArrayList<Project> openProjects;

    /** Creates new form SelectPlatformAppModulePanel */
    public SelectPlatformAppModulePanel() {
        initComponents();
        Project[] opened = OpenProjects.getDefault().getOpenProjects();
        openProjects = new ArrayList<Project>(opened.length);
        ArrayList<String> names = new ArrayList<String>(opened.length);
        for( Project p : opened ) {
            if( isNbAppProject(p) ) {
                openProjects.add(p);
                names.add(ProjectUtils.getInformation(p).getDisplayName());
            }
        }
        comboProjects.setModel(new DefaultComboBoxModel(names.toArray()));
        comboProjects.setSelectedIndex(-1);
        btnBrowse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                browseProject();
            }
        });
        comboProjects.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                dd.setValid(comboProjects.getSelectedItem() != null);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHint = new javax.swing.JLabel();
        lblOpenProjects = new javax.swing.JLabel();
        comboProjects = new javax.swing.JComboBox();
        btnBrowse = new javax.swing.JButton();

        lblHint.setText(NbBundle.getMessage(SelectPlatformAppModulePanel.class, "SelectPlatformAppModulePanel.lblHint.text")); // NOI18N

        lblOpenProjects.setText(NbBundle.getMessage(SelectPlatformAppModulePanel.class, "SelectPlatformAppModulePanel.lblOpenProjects.text")); // NOI18N

        btnBrowse.setText(NbBundle.getMessage(SelectPlatformAppModulePanel.class, "SelectPlatformAppModulePanel.btnBrowse.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHint)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblOpenProjects)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboProjects, 0, 222, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBrowse)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHint)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOpenProjects)
                    .addComponent(comboProjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse))
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JComboBox comboProjects;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblOpenProjects;
    // End of variables declaration//GEN-END:variables

    @Messages("Err_NotMavenNBAppProject=The selected project is not a Maven NetBeans Platform Application project.")
    private void browseProject() {
        JFileChooser chooser = ProjectChooser.projectChooser();
        while( true ) {
            if( JFileChooser.APPROVE_OPTION != chooser.showOpenDialog(this) ) {
                return;
            }
            File projectDir = chooser.getSelectedFile();
            FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(projectDir));
            if( null == fo || !fo.isFolder() ) {
                return;
            }
            try {
                Project p = ProjectManager.getDefault().findProject(fo);
                if( null == p || !isNbAppProject(p) ) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                            Err_NotMavenNBAppProject())); //NOI18N
                    continue;
                }
                appModuleProject = p;
                ArrayList<String> names = new ArrayList<String>(openProjects.size()+1);
                for( Project prj : openProjects ) {
                    names.add(ProjectUtils.getInformation(prj).getDisplayName());
                }
                names.add(ProjectUtils.getInformation(p).getDisplayName());
                comboProjects.setModel(new DefaultComboBoxModel(names.toArray()));
                comboProjects.setSelectedItem(ProjectUtils.getInformation(p).getDisplayName());
                return;
            } catch( IOException ex ) {
                Exceptions.printStackTrace(ex);
            } catch( IllegalArgumentException ex ) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private Project getSelectedProject() {
        Project res = appModuleProject;
        if( null == res ) {
            int selProject = comboProjects.getSelectedIndex();
            if( selProject >= 0 )
                res = openProjects.get(selProject);
        }
        return res;
    }

    /**
     * Ask user to select Maven NB Platform app module the given projec is part of
     * and store path to the module in project properties.
     * @param project Maven project which belongs to a Maven Platform App suite.
     * @return True if the user select NB Platform App project suite the given
     * project is part of, false otherwise.
     */
    @Messages("Title_SelectProject=Select Project")
    static boolean findAppModule(Project project) {
        SelectPlatformAppModulePanel panel = new SelectPlatformAppModulePanel();
        DialogDescriptor dd = new DialogDescriptor(panel, 
                Title_SelectProject(), true, null); //NOI18N
        panel.dd = dd;
        dd.setValid(false);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setVisible(true);
        Project appModule = panel.getSelectedProject();
        if( null != appModule ) {
            FileObject appDirFo = appModule.getProjectDirectory();
            File appDir = FileUtil.toFile(appDirFo);
            File projectDir = FileUtil.toFile(project.getProjectDirectory());
            String relPath = FileUtilities.relativizeFile(projectDir, appDir);
            AuxiliaryProperties props = project.getLookup().lookup(AuxiliaryProperties.class);
            props.put(MavenNbModuleImpl.PROP_PATH_NB_APPLICATION_MODULE, relPath, true); //TODO do we want the props to be shareable or not?
            return true;
        }
        return false;
    }

    private static boolean isNbAppProject( Project p ) {
        NbMavenProject watch = p.getLookup().lookup(NbMavenProject.class);
        return watch != null && NbMavenProject.TYPE_NBM_APPLICATION.equals(watch.getPackagingType());
    }
}
