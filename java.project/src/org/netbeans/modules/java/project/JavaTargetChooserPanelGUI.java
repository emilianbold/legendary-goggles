/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.project;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.awt.Mnemonics;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Permits user to select a package to place a Java class (or other resource) into.
 * @author Petr Hrebejk, Jesse Glick
 */
public class JavaTargetChooserPanelGUI extends javax.swing.JPanel implements ActionListener, DocumentListener {
  
    private static final String DEFAULT_NEW_PACKAGE_NAME = 
        NbBundle.getMessage( JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_DefaultNewPackageName" ); // NOI18N
    private static final String NEW_CLASS_PREFIX = 
        NbBundle.getMessage( JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_NewJavaClassPrefix" ); // NOI18N
    
    /** preferred dimension of the panel */
    private static final Dimension PREF_DIM = new Dimension(500, 340);
    
    private Project project;
    private String expectedExtension;
    private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    private int type;
    private SourceGroup groups[];
    private boolean ignoreRootCombo;
    
    /** Creates new form SimpleTargetChooserGUI */
    public JavaTargetChooserPanelGUI( Project p, SourceGroup[] groups, Component bottomPanel, int type ) {
        this.type = type;
        this.project = p;
        this.groups = groups;
        
        initComponents();        
                
        if ( type == NewJavaFileWizardIterator.TYPE_PACKAGE ) {
            packageComboBox.setVisible( false );
            packageLabel.setVisible( false );
            Mnemonics.setLocalizedText (fileLabel, NbBundle.getMessage (JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_CreatedFolder_Label")); // NOI18N
            Mnemonics.setLocalizedText (documentNameLabel, NbBundle.getMessage (JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_PackageName_Label")); // NOI18N
            documentNameTextField.getDocument().addDocumentListener( this );
        }
        else if ( type == NewJavaFileWizardIterator.TYPE_PKG_INFO ) {
            documentNameTextField.setEditable (false);
        }
        else {
            packageComboBox.getEditor().addActionListener( this );
            documentNameTextField.getDocument().addDocumentListener( this );
        }
        
                
        if ( bottomPanel != null ) {
            bottomPanelContainer.add( bottomPanel, java.awt.BorderLayout.CENTER );
        }
                
        //initValues( project, null, null );
        

        // Not very nice
        Component packageEditor = packageComboBox.getEditor().getEditorComponent();
        if ( packageEditor instanceof javax.swing.JTextField ) {
            ((javax.swing.JTextField)packageEditor).getDocument().addDocumentListener( this );
        }
        else {
            packageComboBox.addActionListener( this );
        }
        
        rootComboBox.setRenderer(new GroupListCellRenderer());        
        packageComboBox.setRenderer(PackageView.listRenderer());
        rootComboBox.addActionListener( this );
        
        setPreferredSize( PREF_DIM );
        setName( NbBundle.getBundle (JavaTargetChooserPanelGUI.class).getString ("LBL_JavaTargetChooserPanelGUI_Name") ); // NOI18N
    }
    
    public void initValues( FileObject template, FileObject preselectedFolder ) {
        assert project != null : "Project must be specified."; // NOI18N
        // Show name of the project
        projectTextField.setText( ProjectUtils.getInformation(project).getDisplayName() );
        assert template != null;
        
        String displayName = null;
        try {
            DataObject templateDo = DataObject.find (template);
            displayName = templateDo.getNodeDelegate ().getDisplayName ();
        } catch (DataObjectNotFoundException ex) {
            displayName = template.getName ();
        }
        
        putClientProperty ("NewFileWizard_Title", displayName);// NOI18N        
        // Setup comboboxes 
        rootComboBox.setModel(new DefaultComboBoxModel(groups));
        SourceGroup preselectedGroup = getPreselectedGroup( preselectedFolder );
        ignoreRootCombo = true;
        rootComboBox.setSelectedItem( preselectedGroup );                       
        ignoreRootCombo = false;
        Object preselectedPackage = getPreselectedPackage(preselectedGroup, preselectedFolder, packageComboBox.getModel());
        if ( type == NewJavaFileWizardIterator.TYPE_PACKAGE ) {
            String docName = preselectedPackage == null || preselectedPackage.toString().length() == 0 ? 
                DEFAULT_NEW_PACKAGE_NAME : 
                preselectedPackage.toString() + "." + DEFAULT_NEW_PACKAGE_NAME;

            documentNameTextField.setText( docName );                    
            int docNameLen = docName.length();
            int defPackageNameLen = DEFAULT_NEW_PACKAGE_NAME.length();

            documentNameTextField.setSelectionEnd( docNameLen - 1 );
            documentNameTextField.setSelectionStart( docNameLen - defPackageNameLen );                
        } else {
            if (preselectedPackage != null) {
                // packageComboBox.setSelectedItem( preselectedPackage );
                packageComboBox.getEditor().setItem( preselectedPackage );
            }
            if (template != null) {
            	if ( documentNameTextField.getText().trim().length() == 0 ) { // To preserve the class name on back in the wiazard
                    if (this.type == NewJavaFileWizardIterator.TYPE_PKG_INFO) {
                        documentNameTextField.setText (template.getName ());
                    }
                    else {
                        //Ordinary file
                        documentNameTextField.setText (NEW_CLASS_PREFIX + template.getName ());
                        documentNameTextField.selectAll ();
                    }
                }
            }
            updatePackages( false );
        }
        // Determine the extension
        String ext = template == null ? "" : template.getExt(); // NOI18N
        expectedExtension = ext.length() == 0 ? "" : "." + ext; // NOI18N
        
        updateText();
        
    }
        
    public FileObject getRootFolder() {
        return ((SourceGroup) rootComboBox.getSelectedItem()).getRootFolder();        
    }
    
    public String getPackageFileName() {
        
        if ( type == NewJavaFileWizardIterator.TYPE_PACKAGE ) {
            return ""; // NOI18N
        }
        
        String packageName = packageComboBox.getEditor().getItem().toString();        
        return  packageName.replace( '.', '/' ); // NOI18N        
    }
    
    /**
     * Name of selected package, or "" for default package.
     */
    String getPackageName() {
        if ( type == NewJavaFileWizardIterator.TYPE_PACKAGE ) {
            return ""; // NOI18N
        }
        return packageComboBox.getEditor().getItem().toString();
    }
    
    public Dimension getPreferredSize() {
        return PREF_DIM;
    }
    
    public String getTargetName() {
        String text = documentNameTextField.getText().trim();
        
        if ( text.length() == 0 ) {
            return null;
        }
        else {
            return text;
        }

    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(e);
        }
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        documentNameLabel = new javax.swing.JLabel();
        documentNameTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        rootComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        targetSeparator = new javax.swing.JSeparator();
        bottomPanelContainer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("AD_JavaTargetChooserPanelGUI"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        documentNameLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("MNE_JavaTargetChooserPanelGUI_ClassName_Label").charAt(0));
        documentNameLabel.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_ClassName_Label"));
        documentNameLabel.setLabelFor(documentNameTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(documentNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(documentNameTextField, gridBagConstraints);
        documentNameTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("AD_documentNameTextField"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 24, 0);
        add(jPanel1, gridBagConstraints);

        jLabel5.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("MNE_JavaTargetChooserPanelGUI_jLabel5").charAt(0));
        jLabel5.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel5"));
        jLabel5.setLabelFor(projectTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jLabel5, gridBagConstraints);

        projectTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        add(projectTextField, gridBagConstraints);
        projectTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("AD_projectTextField"));

        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("MNE_JavaTargetChooserPanelGUI_jLabel1").charAt(0));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel1"));
        jLabel1.setLabelFor(rootComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        add(rootComboBox, gridBagConstraints);
        rootComboBox.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("AD_rootComboBox"));

        packageLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("MNE_JavaTargetChooserPanelGUI_jLabel2").charAt(0));
        packageLabel.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel2"));
        packageLabel.setLabelFor(packageComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(packageLabel, gridBagConstraints);

        packageComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        add(packageComboBox, gridBagConstraints);
        packageComboBox.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("AD_packageComboBox"));

        fileLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("MNE_JavaTargetChooserPanelGUI_CreatedFile_Label").charAt(0));
        fileLabel.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_CreatedFile_Label"));
        fileLabel.setLabelFor(fileTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 12, 0);
        add(fileLabel, gridBagConstraints);

        fileTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 0);
        add(fileTextField, gridBagConstraints);
        fileTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/project/Bundle").getString("AD_fileTextField"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(targetSeparator, gridBagConstraints);

        bottomPanelContainer.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(bottomPanelContainer, gridBagConstraints);

    }//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanelContainer;
    private javax.swing.JLabel documentNameLabel;
    private javax.swing.JTextField documentNameTextField;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JComboBox rootComboBox;
    private javax.swing.JSeparator targetSeparator;
    // End of variables declaration//GEN-END:variables

    // ActionListener implementation -------------------------------------------
        
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if ( rootComboBox == e.getSource() ) {            
            if ( !ignoreRootCombo && type != NewJavaFileWizardIterator.TYPE_PACKAGE ) {
                updatePackages( true );
            }
            updateText();
        }
        else if ( packageComboBox == e.getSource() ) {
            updateText();
            fireChange();
        }
        else if ( packageComboBox.getEditor()  == e.getSource() ) {
            updateText();
            fireChange();
        }
    }    
    
    // DocumentListener implementation -----------------------------------------
    
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateText();
        fireChange();        
    }    
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    // Private methods ---------------------------------------------------------
        
    private RequestProcessor.Task updatePackagesTask = null;
    
    private static final ComboBoxModel WAIT_MODEL = new DefaultComboBoxModel( 
        new String[] {
            NbBundle.getMessage( JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_PackageName_PleaseWait" ) // NOI18N
        } 
    ); 
    
    private void updatePackages( final boolean clean ) {
        WAIT_MODEL.setSelectedItem( packageComboBox.getEditor().getItem() );
        packageComboBox.setModel( WAIT_MODEL );
        
        if ( updatePackagesTask != null ) {
            updatePackagesTask.cancel();
        }
        
        updatePackagesTask = new RequestProcessor( "ComboUpdatePackages" ).post(
            new Runnable() {
            
                private ComboBoxModel model;
            
                public void run() {
                    if ( !SwingUtilities.isEventDispatchThread() ) {
                        model = PackageView.createListView((SourceGroup) rootComboBox.getSelectedItem());                        
                        SwingUtilities.invokeLater( this );
                    }
                    else {
                        if ( !clean ) {
                            model.setSelectedItem( packageComboBox.getEditor().getItem() );
                        }
                        packageComboBox.setModel( model );
                    }
                }
            }
        );
                
    }
        
    private File getFolder() {
        FileObject rootFo = getRootFolder();
        File rootFile = FileUtil.toFile( rootFo );
        if ( rootFile == null ) {
            return null;
        }        
        String packageFileName = getPackageFileName();        
        File folder = new File( rootFile, packageFileName );
        return folder;
    }
    
    private void updateText() {
        
        SourceGroup g = (SourceGroup) rootComboBox.getSelectedItem();
        FileObject rootFolder = g.getRootFolder();
        String packageName = getPackageFileName();
        String documentName = documentNameTextField.getText().trim();
        if ( type == NewJavaFileWizardIterator.TYPE_PACKAGE ) {
            documentName = documentName.replace( '.', '/' ); // NOI18N
        }
        else if ( documentName.length() > 0 ) {
            documentName = documentName + expectedExtension;
        }
        String createdFileName = FileUtil.getFileDisplayName( rootFolder ) + 
            ( packageName.startsWith("/") || packageName.startsWith( File.separator ) ? "" : "/" ) + // NOI18N
            packageName + 
            ( packageName.endsWith("/") || packageName.endsWith( File.separator ) || packageName.length() == 0 ? "" : "/" ) + // NOI18N
            documentName;
        
        fileTextField.setText( createdFileName.replace( '/', File.separatorChar ) ); // NOI18N        
    }
    
    private SourceGroup getPreselectedGroup(FileObject folder) {
        for(int i = 0; folder != null && i < groups.length; i++) {
            FileObject root = groups[i].getRootFolder();
            if (root.equals(folder) || FileUtil.isParentOf(root, folder)) {
                return groups[i];
            }
        }
        return groups[0];
    }
    
    /**
     * Get a package combo model item for the package the user selected before opening the wizard.
     * May return null if it cannot find it; or a String instance if there is a well-defined
     * package but it is not listed among the packages shown in the list model.
     */
    private Object getPreselectedPackage(SourceGroup group, FileObject folder, ListModel model) {
        if ( folder == null ) {
            return null;
        }
        FileObject root = group.getRootFolder();
        
        String relPath = FileUtil.getRelativePath( root, folder );
        
        if ( relPath == null ) {
            // Group Root folder is no a parent of the preselected folder
            // No package should be selected
            return null; 
        }        
        else {
            // Find the right item.            
            String name = relPath.replace('/', '.');
            /*
            int max = model.getSize();
            for (int i = 0; i < max; i++) {
                Object item = model.getElementAt(i);
                if (item.toString().equals(name)) {
                    return item;
                }
            }
             */
            // Didn't find it.
            // #49954: should nonetheless show something in the combo box.
            return name;
        }        
    }
    
    // Private innerclasses ----------------------------------------------------

    /**
     * Displays a {@link SourceGroup} in {@link #rootComboBox}.
     */
    private static final class GroupListCellRenderer extends DefaultListCellRenderer/*<SourceGroup>*/ {
        
        public GroupListCellRenderer() {}
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            SourceGroup g = (SourceGroup) value;
            super.getListCellRendererComponent(list, g.getDisplayName(), index, isSelected, cellHasFocus);
            setIcon(g.getIcon(false));
            return this;
        }
        
    }
    
}
