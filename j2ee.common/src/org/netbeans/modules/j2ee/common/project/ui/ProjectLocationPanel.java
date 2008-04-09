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

package org.netbeans.modules.j2ee.common.project.ui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.modules.j2ee.common.project.ui.UserProjectSettings;
import org.netbeans.spi.java.project.support.ui.SharableLibrariesUtils;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

final class ProjectLocationPanel extends JPanel implements DocumentListener {
    
    public static final String PROP_PROJECT_NAME = "projectName"; //NOI18N
    public static final String PROP_PROJECT_LOCATION = "projectLocation"; //NOI18N
    
    static final String PROP_ERROR_MESSAGE = "WizardPanel_errorMessage"; // NOI18N
    
    private String generatedProjectName = "";
    private int generatedProjectNameIndex = 0;
    private String currentLibrariesLocation;
    private ProjectLocationWizardPanel wizard;
    private Object j2eeModuleType;
    private String nameFormatter;

    /** Creates new form ProjectLocationPanel */
    public ProjectLocationPanel(Object j2eeModuleType, String name, String title, 
            ProjectLocationWizardPanel wizard, String defaultNameFormatter) {
        initComponents();
        this.wizard = wizard;
        this.j2eeModuleType = j2eeModuleType;
        this.nameFormatter = defaultNameFormatter;
        currentLibrariesLocation = "."+File.separatorChar+"lib"; // NOI18N
        librariesLocation.setText(currentLibrariesLocation);
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener( this );
        projectLocationTextField.getDocument().addDocumentListener( this );        
        
        // Provide a name in the title bar.
        setName(name);
        putClientProperty ("NewProjectWizard_Title", title);
        // update state:
        sharableProjectActionPerformed(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        librariesLabel = new javax.swing.JLabel();
        librariesLocation = new javax.swing.JTextField();
        browseLibraries = new javax.swing.JButton();
        sharableProject = new javax.swing.JCheckBox();
        setAsMainCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        lblHint = new javax.swing.JLabel();

        projectNameLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_ProjectName_LabelMnemonic").charAt(0));
        projectNameLabel.setLabelFor(projectNameTextField);
        projectNameLabel.setText(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_ProjectName_Label")); // NOI18N

        projectLocationLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_ProjectLocation_LabelMnemonic").charAt(0));
        projectLocationLabel.setLabelFor(projectLocationTextField);
        projectLocationLabel.setText(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_ProjectLocation_Label")); // NOI18N

        browseButton.setMnemonic(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_Browse_LabelMnemonic").charAt(0));
        browseButton.setText(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_BrowseLocation_Button")); // NOI18N
        browseButton.setActionCommand("BROWSE");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLocationAction(evt);
            }
        });

        createdFolderLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_CreatedProjectFolder_LabelMnemonic").charAt(0));
        createdFolderLabel.setLabelFor(createdFolderTextField);
        createdFolderLabel.setText(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_CreatedProjectFolder_Label")); // NOI18N

        createdFolderTextField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(librariesLabel, org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "ProjectLocationPanel.librariesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseLibraries, org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "PanelSharabilityVisual.browseLibraries.text")); // NOI18N
        browseLibraries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLibrariesActionPerformed(evt);
            }
        });

        sharableProject.setSelected(SharableLibrariesUtils.isLastProjectSharable());
        org.openide.awt.Mnemonics.setLocalizedText(sharableProject, org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "ProjectLocationPanel.sharableProject.text")); // NOI18N
        sharableProject.setMargin(new java.awt.Insets(2, 0, 2, 2));
        sharableProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sharableProjectActionPerformed(evt);
            }
        });

        setAsMainCheckBox.setSelected(true);
        setAsMainCheckBox.setText(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "LBL_NWP1_SetAsMain_CheckBox")); // NOI18N
        setAsMainCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        lblHint.setText(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "HINT_LibrariesFolder")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(createdFolderLabel)
                .add(339, 339, 339))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectLocationLabel)
                    .add(projectNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, projectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, createdFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, projectLocationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browseButton))
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sharableProject)
                    .add(layout.createSequentialGroup()
                        .add(librariesLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblHint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 296, Short.MAX_VALUE)
                            .add(librariesLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browseLibraries))
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(setAsMainCheckBox)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectNameLabel)
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectLocationLabel)
                    .add(browseButton)
                    .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(createdFolderLabel)
                    .add(createdFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sharableProject)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(librariesLabel)
                    .add(browseLibraries)
                    .add(librariesLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(setAsMainCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 41, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        projectNameLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSN_projectNameLabel")); // NOI18N
        projectNameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSD_projectNameLabel")); // NOI18N
        projectLocationLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSN_projectLocationLabel")); // NOI18N
        projectLocationLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSD_projectLocationLabel")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSN_browseButton")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSD_browseButton")); // NOI18N
        createdFolderLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSN_createdFolderLabel")); // NOI18N
        createdFolderLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ProjectLocationPanel.class).getString("ACSD_createdFolderLabel")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "ACSN_PanelProjectLocationVisual")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectLocationPanel.class, "ACSD_PanelProjectLocationVisual")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void browseLocationAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLocationAction
        String command = evt.getActionCommand();        
        if ( "BROWSE".equals( command ) ) { // NOI18N                
            JFileChooser chooser = new JFileChooser ();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle(NbBundle.getMessage(ProjectLocationPanel.class,"LBL_NWP1_SelectProjectLocation"));
            chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File (path);
                if (f.exists ()) {
                    chooser.setSelectedFile(f);
                }
            }
            if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) { //NOI18N
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText( projectDir.getAbsolutePath() );
            }            
            wizard.fireChangeEvent();
        }
    }//GEN-LAST:event_browseLocationAction

private void browseLibrariesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLibrariesActionPerformed
    // below folder is used just for relativization:
    File f = new File(createdFolderTextField.getText());
    String curr = SharableLibrariesUtils.browseForLibraryLocation(librariesLocation.getText().trim(), this, f);
    if (curr != null) {
        currentLibrariesLocation = curr;
        if (sharableProject.isSelected()) {
            librariesLocation.setText(currentLibrariesLocation);
        }
    }
}//GEN-LAST:event_browseLibrariesActionPerformed

private void sharableProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sharableProjectActionPerformed
        librariesLocation.setEnabled(sharableProject.isSelected());
        browseLibraries.setEnabled(sharableProject.isSelected());
        librariesLabel.setEnabled(sharableProject.isSelected());
        lblHint.setEnabled(sharableProject.isSelected());
        if (sharableProject.isSelected()) {
           librariesLocation.setText(currentLibrariesLocation);
        } else {
            librariesLocation.setText("");
        }
        wizard.fireChangeEvent();
}//GEN-LAST:event_sharableProjectActionPerformed
    
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }
    
    boolean valid( WizardDescriptor wizardDescriptor ) {
        
        if ( projectNameTextField.getText().length() == 0 ) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", // NOI18N
            NbBundle.getMessage(ProjectLocationPanel.class,"MSG_IllegalProjectName"));
            return false; // Display name not specified
        }
        File f = new File (projectLocationTextField.getText()).getAbsoluteFile();
        if (getCanonicalFile (f)==null) {
            String message = NbBundle.getMessage (ProjectLocationPanel.class,"MSG_IllegalProjectLocation");
            wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
            return false;
        }
        final File destFolder = getCanonicalFile(new File( createdFolderTextField.getText() ).getAbsoluteFile());
        if (destFolder == null) {
            String message = NbBundle.getMessage (ProjectLocationPanel.class,"MSG_IllegalProjectName");
            wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
            return false;
        }

        File projLoc = destFolder;
        while (projLoc != null && !projLoc.exists()) {
            projLoc = projLoc.getParentFile();
        }
        if (projLoc == null || !projLoc.canWrite()) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", // NOI18N
            NbBundle.getMessage(ProjectLocationPanel.class,"MSG_ProjectFolderReadOnly"));
            return false;
        }
        
        if (FileUtil.toFileObject(projLoc) == null) {
            String message = NbBundle.getMessage (ProjectLocationPanel.class,"MSG_IllegalProjectLocation");
            wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
            return false;
        }
        
        File[] kids = destFolder.listFiles();
        if ( destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", // NOI18N
            NbBundle.getMessage(ProjectLocationPanel.class,"MSG_ProjectFolderExists"));
            return false;
        }
        
        wizardDescriptor.putProperty( "WizardPanel_errorMessage", "");
        
        if (sharableProject.isSelected()) {
            String location = librariesLocation.getText();
            if (new File(location).isAbsolute()) {
                wizardDescriptor.putProperty(PROP_ERROR_MESSAGE, decorateMessage(
                        NbBundle.getMessage(ProjectLocationPanel.class, "PanelSharability.absolutePathWarning.text")));

            } else {
                File projectLoc = FileUtil.normalizeFile(new File(createdFolderTextField.getText()));
                File libLoc = PropertyUtils.resolveFile(projectLoc, location);
                if (!CollocationQuery.areCollocated(projectLoc, libLoc)) {
                    wizardDescriptor.putProperty(PROP_ERROR_MESSAGE, decorateMessage(
                            NbBundle.getMessage(ProjectLocationPanel.class, "PanelSharability.relativePathWarning.text")));
                }
            }
        }
        
        return true;
    }
    
    void store( WizardDescriptor d ) {                
        String name = projectNameTextField.getText().trim();
        String folder = createdFolderTextField.getText().trim();
        
        d.putProperty( ProjectLocationWizardPanel.PROJECT_DIR, new File( folder ));
        d.putProperty( ProjectLocationWizardPanel.NAME, name );
        
        d.putProperty(ProjectLocationWizardPanel.SET_AS_MAIN, setAsMainCheckBox.isSelected() ? Boolean.TRUE : Boolean.FALSE );
        d.putProperty(ProjectLocationWizardPanel.SHARED_LIBRARIES, sharableProject.isSelected() ? librariesLocation.getText() : null);
    }
    
    void read (WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty ("projdir");  //NOI18N
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory ()) {
            // honor the contract in issue 58987
            File currentDirectory = null;
            FileObject existingSourcesFO = Templates.getExistingSourcesFolder(settings);
            if (existingSourcesFO != null) {
                File existingSourcesFile = FileUtil.toFile(existingSourcesFO);
                if (existingSourcesFile != null && existingSourcesFile.isDirectory()) {
                    currentDirectory = existingSourcesFile;
                }
            }
            if (currentDirectory != null) {
                projectLocation = currentDirectory;
            } else {
                projectLocation = ProjectChooser.getProjectsFolder();
            }
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        this.projectLocationTextField.setText (projectLocation.getAbsolutePath());
        
        String projectName = (String) settings.getProperty ("name"); //NOI18N
        if (projectName == null) {
            int baseCount = 1;
            while ((projectName=validFreeProjectName(projectLocation, nameFormatter, baseCount))==null) {
                baseCount++;
            }
        }
        this.projectNameTextField.setText (projectName);                
        this.projectNameTextField.selectAll();
    }
        
    void validate (WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton browseLibraries;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel librariesLabel;
    private javax.swing.JTextField librariesLocation;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    protected javax.swing.JTextField projectNameTextField;
    private javax.swing.JCheckBox setAsMainCheckBox;
    private javax.swing.JCheckBox sharableProject;
    // End of variables declaration//GEN-END:variables
    
    
    // Private methods ---------------------------------------------------------
    
    private String validFreeProjectName (final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format (formater, new Object[]{Integer.valueOf(index)});                
        File file = new File (parentFolder, name);
        return file.exists() ? null : name;
    }

    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
        if (this.projectLocationTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_LOCATION,null,this.projectLocationTextField.getText());
        }
    }
    
    public void insertUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
        if (this.projectLocationTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_LOCATION,null,this.projectLocationTextField.getText());
        }
    }
    
    public void removeUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
        if (this.projectLocationTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_LOCATION,null,this.projectLocationTextField.getText());
        }
    }
    
    /** Handles changes in the Project name and project directory
     */
    private void updateTexts( DocumentEvent e ) {
        Document doc = e.getDocument();
        if (doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument()) {
            String projectName = projectNameTextField.getText();
            
            if (doc == projectLocationTextField.getDocument()) {
                if (projectName.equals(generatedProjectName)) {
                    File f = new File(projectLocationTextField.getText().trim());
                    generatedProjectNameIndex = getValidProjectNameIndex(nameFormatter, generatedProjectNameIndex, f);
                } else {
                    generatedProjectNameIndex = 0;
                }
                generatedProjectName = generatedProjectNameIndex > 0 ? getProjectName(nameFormatter, generatedProjectNameIndex) : null;
                if(generatedProjectNameIndex > 0) {
                    projectName = generatedProjectName;
                    projectNameTextField.setText(generatedProjectName);
                    projectNameTextField.selectAll();
                }
            }
            
            String projectFolder = projectLocationTextField.getText(); 
            createdFolderTextField.setText(projectFolder + File.separatorChar + projectName);
        }
        wizard.fireChangeEvent();
    }

    static File getCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getProjectName(String formatter, int index) {
        return MessageFormat.format(formatter, new Object[] {String.valueOf(index)});
    }

    private static int getValidProjectNameIndex(String formatter, int currentIndex, File projectLocation) {
        int index = currentIndex > 0 ? currentIndex : UserProjectSettings.getDefault().getNewProjectCount() + 1;
        if(projectLocation != null) {
            while (new File(projectLocation, getProjectName(formatter, index)).exists()) {
                index++;
            }
        }
        return index;
    }

    public static String decorateMessage(String message) {
        if (message != null) {
            return "<html>" + message.replaceAll("<",  "&lt;").replaceAll(">",  "&gt;") + "</html>"; // NIO18N
        }
        return null;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(ProjectImportLocationPanel.generateHelpID(ProjectLocationPanel.class, j2eeModuleType));
    }
}
