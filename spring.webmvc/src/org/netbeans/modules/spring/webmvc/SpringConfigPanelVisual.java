/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997, 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
/*
 * Contributor(s): Craig MacKay
 */

package org.netbeans.modules.spring.webmvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.spring.api.SpringUtilities;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

/**
 * Provides the user interface for configuring a Spring Web MVC web application
 *
 * @author Craig MacKay
 */
public class SpringConfigPanelVisual extends javax.swing.JPanel {

    private static final Logger LOG = Logger.getLogger(SpringConfigPanelVisual.class.getName());
    private static final long serialVersionUID = 1L;
    private boolean libsInitialized = false;
    private List<SpringLibrary> springLibs = new ArrayList<>();
    private SpringLibrary springLibrary;
    private final SpringWebModuleExtender extender;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final DocumentListener docListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            fireChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fireChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fireChange();
        }
    };
    
    public SpringConfigPanelVisual(SpringWebModuleExtender extender) {
        this.extender = extender;
        initComponents();
        dispatcherNameText.setText(extender.getDispatcherName());
        dispatcherNameText.getDocument().addDocumentListener(docListener);
        dispatcherMappingText.setText(extender.getDispatcherMapping());
        dispatcherMappingText.getDocument().addDocumentListener(docListener);
        includeJstlCheckBox.setSelected(extender.getIncludeJstl());
        // Only add the listener at the end to make sure no events are
        // fired while initializing the UI.
        changeSupport.addChangeListener(extender);
        initLibraries();
    }

    @Override
    public void setEnabled(boolean enabled) {
        tabbedPanel.setEnabled(enabled);
        dispatcherNameText.setEnabled(enabled);
        dispatcherMappingText.setEnabled(enabled);
        includeJstlCheckBox.setEnabled(enabled);
        springVersionLabel.setEnabled(enabled);
        cbSpringVersion.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public String getDispatcherName() {
        return dispatcherNameText.getText();
    }

    public String getDispatcherMapping() {
        return dispatcherMappingText.getText();
    }

    public boolean getIncludeJstl() {
        return includeJstlCheckBox.isSelected();
    }               

    private void fireChange() {
        changeSupport.fireChange();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPanel = new javax.swing.JTabbedPane();
        libPanel = new javax.swing.JPanel();
        includeJstlCheckBox = new javax.swing.JCheckBox();
        springVersionLabel = new javax.swing.JLabel();
        cbSpringVersion = new javax.swing.JComboBox();
        standardPanel = new javax.swing.JPanel();
        dispatcherNameText = new javax.swing.JTextField();
        dispatcherNameLabel = new javax.swing.JLabel();
        dispatcherMappingLabel = new javax.swing.JLabel();
        dispatcherMappingText = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        libPanel.setAlignmentX(0.2F);
        libPanel.setAlignmentY(0.2F);

        org.openide.awt.Mnemonics.setLocalizedText(includeJstlCheckBox, org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "LBL_IncludeJstl")); // NOI18N
        includeJstlCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includeJstlCheckBoxActionPerformed(evt);
            }
        });

        springVersionLabel.setLabelFor(cbSpringVersion);
        org.openide.awt.Mnemonics.setLocalizedText(springVersionLabel, org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "SpringConfigPanelVisual.springVersionLabel.text")); // NOI18N

        cbSpringVersion.setModel(getLibrariesComboBoxModel());
        cbSpringVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSpringVersionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout libPanelLayout = new javax.swing.GroupLayout(libPanel);
        libPanel.setLayout(libPanelLayout);
        libPanelLayout.setHorizontalGroup(
            libPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(libPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(libPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(libPanelLayout.createSequentialGroup()
                        .addComponent(springVersionLabel)
                        .addGap(18, 18, 18)
                        .addComponent(cbSpringVersion, 0, 450, Short.MAX_VALUE))
                    .addGroup(libPanelLayout.createSequentialGroup()
                        .addComponent(includeJstlCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        libPanelLayout.setVerticalGroup(
            libPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(libPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(libPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(springVersionLabel)
                    .addComponent(cbSpringVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(includeJstlCheckBox)
                .addContainerGap(313, Short.MAX_VALUE))
        );

        includeJstlCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "SpringConfigPanelVisual.includeJstlCheckBox.AccessibleContext.accessibleDescription")); // NOI18N

        tabbedPanel.addTab(org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "LBL_Libraries"), libPanel); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dispatcherNameLabel, org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "LBL_DispatcherName")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dispatcherMappingLabel, org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "LBL_DispatcherMapping")); // NOI18N

        javax.swing.GroupLayout standardPanelLayout = new javax.swing.GroupLayout(standardPanel);
        standardPanel.setLayout(standardPanelLayout);
        standardPanelLayout.setHorizontalGroup(
            standardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(standardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(standardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dispatcherNameLabel)
                    .addComponent(dispatcherMappingLabel))
                .addGap(8, 8, 8)
                .addGroup(standardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dispatcherNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                    .addComponent(dispatcherMappingText, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                .addContainerGap())
        );
        standardPanelLayout.setVerticalGroup(
            standardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(standardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(standardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dispatcherNameLabel)
                    .addComponent(dispatcherNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(standardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dispatcherMappingLabel)
                    .addComponent(dispatcherMappingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(334, Short.MAX_VALUE))
        );

        tabbedPanel.addTab(org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "LBL_Configuration"), standardPanel); // NOI18N

        add(tabbedPanel, java.awt.BorderLayout.CENTER);
        tabbedPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SpringConfigPanelVisual.class, "SpringConfigPanelVisual.tabbedPanel.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void includeJstlCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeJstlCheckBoxActionPerformed
        fireChange();
    }//GEN-LAST:event_includeJstlCheckBoxActionPerformed

    private void cbSpringVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSpringVersionActionPerformed
        springLibrary = springLibs.get(cbSpringVersion.getSelectedIndex());
    }//GEN-LAST:event_cbSpringVersionActionPerformed

    public void enableComponents(boolean enabled) {
        standardPanel.setEnabled(enabled);
        dispatcherMappingLabel.setEnabled(enabled);
        dispatcherMappingText.setEnabled(enabled);
        dispatcherNameLabel.setEnabled(enabled);
        dispatcherNameText.setEnabled(enabled);
        tabbedPanel.setEnabled(enabled);
        springVersionLabel.setEnabled(enabled);
        cbSpringVersion.setEnabled(enabled);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbSpringVersion;
    private javax.swing.JLabel dispatcherMappingLabel;
    private javax.swing.JTextField dispatcherMappingText;
    private javax.swing.JLabel dispatcherNameLabel;
    private javax.swing.JTextField dispatcherNameText;
    private javax.swing.JCheckBox includeJstlCheckBox;
    private javax.swing.JPanel libPanel;
    private javax.swing.JLabel springVersionLabel;
    private javax.swing.JPanel standardPanel;
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables

    public Library getSpringLibrary() {
        if (springLibrary == null) {
            return null;
        }
        return springLibrary.getLibrary();
    }

    public String getSpringLibraryVersion() {
        return springLibrary.getVersion();
    }

    @Messages("JSFConfigurationPanelVisual.lbl.searching.libraries=Searching Libraries...")
    private static ComboBoxModel getLibrariesComboBoxModel() {
        return new DefaultComboBoxModel(new String[] {Bundle.JSFConfigurationPanelVisual_lbl_searching_libraries()});
    }

    private synchronized void initLibraries() {
        if (libsInitialized) {
            return;
        }
        springLibs.clear();

        RequestProcessor.getDefault().submit(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                final List<String> items = new ArrayList<>();
                for (Library library : LibraryManager.getDefault().getLibraries()) {
                    if (SpringUtilities.isSpringLibrary(library)) {
                        items.add(library.getDisplayName());
                        springLibs.add(new SpringLibrary(library));
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cbSpringVersion.setModel(new DefaultComboBoxModel(items.toArray(new String[items.size()])));
                        int selectedIndex = cbSpringVersion.getSelectedIndex();
                        if (selectedIndex < springLibs.size()) {
                            springLibrary = springLibs.get(selectedIndex);
                            libsInitialized = true;
                            repaint();
                            fireChange();
                        }
                    }
                });
                LOG.log(Level.FINEST, "Time spent in {0} initLibraries = {1} ms",
                        new Object[]{this.getClass().getName(), System.currentTimeMillis() - startTime});
            }
        });
    }

    private class SpringLibrary {

        private Library springLibrary;
        private String version;

        public SpringLibrary(Library springLibrary) {
            this.springLibrary = springLibrary;
        }

        public String getVersion() {
            if (version == null) {
                version = SpringUtilities.getSpringLibraryVersion(springLibrary);
            }
            return version;
        }

        public Library getLibrary() {
            return springLibrary;
        }

        @Override
        public String toString() {
            return springLibrary.getDisplayName();
        }

    }
}
