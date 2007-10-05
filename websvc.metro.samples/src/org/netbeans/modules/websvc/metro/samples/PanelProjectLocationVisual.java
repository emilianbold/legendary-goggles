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

package org.netbeans.modules.websvc.metro.samples;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.netbeans.spi.project.ui.support.ProjectChooser;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public class PanelProjectLocationVisual extends SettingsPanel implements DocumentListener {

    private PanelConfigureProject panel;

    /** Creates new form PanelProjectLocationVisual */
    public PanelProjectLocationVisual(PanelConfigureProject panel) {
        initComponents();
        this.panel = panel;
//        projectNameTextField.setEditable(false);
        
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener(this);
        projectLocationTextField.getDocument().addDocumentListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        Button = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        projectNameLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_ProjectName_LabelMnemonic").charAt(0));
        projectNameLabel.setLabelFor(projectNameTextField);
        projectNameLabel.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_ProjectName_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(projectNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(projectNameTextField, gridBagConstraints);
        projectNameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "ACS_LBL_NWP1_ProjectName_A11YDesc"));

        projectLocationLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_ProjectLocation_LabelMnemonic").charAt(0));
        projectLocationLabel.setLabelFor(projectLocationTextField);
        projectLocationLabel.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_ProjectLocation_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(projectLocationLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(projectLocationTextField, gridBagConstraints);
        projectLocationTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "ACS_LBL_NPW1_ProjectLocation_A11YDesc"));

        Button.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_BrowseLocation_Button"));
        Button.setActionCommand("BROWSE");
        Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLocationAction(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 0);
        add(Button, gridBagConstraints);
        Button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "ACS_LBL_NWP1_BrowseLocation_A11YDesc"));

        createdFolderLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_CreatedProjectFolder_LablelMnemonic").charAt(0));
        createdFolderLabel.setLabelFor(createdFolderTextField);
        createdFolderLabel.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_CreatedProjectFolder_Lablel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(createdFolderLabel, gridBagConstraints);

        createdFolderTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(createdFolderTextField, gridBagConstraints);
        createdFolderTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "ACS_LBL_NWP1_CreatedProjectFolder_A11YDesc"));

    }//GEN-END:initComponents

    private void browseLocationAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLocationAction
        String command = evt.getActionCommand();
        
        if ("BROWSE".equals(command)) { //NOI18N
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(NbBundle.getMessage(PanelProjectLocationVisual.class,"LBL_NWP1_SelectProjectLocation")); //NOI18N
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists())
                    chooser.setSelectedFile(f);
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText(projectDir.getAbsolutePath());
            }            
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_browseLocationAction
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectLocationTextField.requestFocus();
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        if (projectNameTextField.getText().length() == 0) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PanelProjectLocationVisual.class,"MSG_IllegalProjectName")); //NOI18N
            return false; // Display name not specified
        }
        
        File destFolder = new File(createdFolderTextField.getText());
        File[] children = destFolder.listFiles();
        if (destFolder.exists() && children != null && children.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PanelProjectLocationVisual.class,"MSG_ProjectFolderExists")); //NOI18N
            return false;
        }
                
        wizardDescriptor.putProperty("WizardPanel_errorMessage", ""); //NOI18N
        return true;
    }
    
    void store(WizardDescriptor d) {        
        String name = projectNameTextField.getText().trim();
        
        d.putProperty(WizardProperties.PROJECT_DIR, new File(createdFolderTextField.getText().trim()));
        d.putProperty(WizardProperties.NAME, name);
        
        File projectsDir = new File(this.projectLocationTextField.getText());
        if (projectsDir.isDirectory()) {
            ProjectChooser.setProjectsFolder (projectsDir);
        }
    }
        
    void read (WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty(WizardProperties.PROJECT_DIR);
        if (projectLocation == null)
            projectLocation = ProjectChooser.getProjectsFolder();
        else
            projectLocation = projectLocation.getParentFile();
        
        projectLocationTextField.setText(projectLocation.getAbsolutePath());
        projectLocationTextField.selectAll();
        
        String formater = null;
        String projectName = (String) settings.getProperty(WizardProperties.NAME);
        
        if (projectName == null) {
            formater = NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NPW1_DefaultProjectName"); //NOI18N
        } else {
            formater = projectName + "{0}"; //NOI18N
        }
        if ((projectName == null) || (validFreeProjectName(projectLocation, projectName) == null)) {
            int baseCount = FoldersListSettings.getDefault().getNewProjectCount() + 1;
            while ((projectName = validFreeProjectName(projectLocation, formater, baseCount)) == null)
                baseCount++;
//            settings.putProperty(NewWebProjectWizardIterator.PROP_NAME_INDEX, new Integer(baseCount));
        }
        projectNameTextField.setText(projectName);                
        projectNameTextField.selectAll();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    protected javax.swing.JTextField projectNameTextField;
    // End of variables declaration//GEN-END:variables
        
    private String validFreeProjectName(final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format(formater, new Object[] {Integer.valueOf(index)});
        File file = new File(parentFolder, name);
        return file.exists() ? null : name;
    }

    private String validFreeProjectName(final File parentFolder, final String name) {
        File file = new File(parentFolder, name);
        return file.exists() ? null : name;
    }
    
    // Implementation of DocumentListener --------------------------------------
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    // End if implementation of DocumentListener -------------------------------
    
    
    /** Handles changes in the project name and project directory
     */
    private void updateTexts(DocumentEvent e) {
        createdFolderTextField.setText(getCreatedFolderPath());

        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    private String getCreatedFolderPath() {
        StringBuffer folder = new StringBuffer(projectLocationTextField.getText().trim());
        if (!projectLocationTextField.getText().endsWith(File.separator))
            folder.append(File.separatorChar);
        folder.append(projectNameTextField.getText().trim());
        
        return folder.toString();
    }
    
}

//TODO implement check for project folder name and location
