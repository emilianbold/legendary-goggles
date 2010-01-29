/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.core.ui.options.general;

import java.awt.CardLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import org.netbeans.core.ui.options.general.WebBrowsersOptionsModel.PropertyPanelDesc;

/**
 * Panel for customization of web browser preffered property
 * 
 * @author Milan Kubec
 */
public class WebBrowsersOptionsPanel extends JPanel implements ListSelectionListener {
    
    private WebBrowsersOptionsModel browsersModel;
    private DocumentListener fieldDocListener;
    
    /** Creates new form WebBrowsersOptionsPanel */
    public WebBrowsersOptionsPanel(WebBrowsersOptionsModel mdl, String selectedItem) {
        
        browsersModel = mdl;
        initComponents();
        
        List<PropertyPanelDesc> propPanelDescs = browsersModel.getPropertyPanels();
        for (PropertyPanelDesc panelDesc : propPanelDescs) {
            customPropertyPanel.add(panelDesc.panel, panelDesc.id);
        }
        
        browsersList.setModel(browsersModel);
        browsersList.addListSelectionListener(this);
        browsersList.setSelectedValue(selectedItem, true);
        
        fieldDocListener = new BrowsersDocListener();
        addListenerToField();
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        browsersLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        browsersList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        customPropertyPanel = new javax.swing.JPanel();

        browsersLabel.setLabelFor(browsersList);
        org.openide.awt.Mnemonics.setLocalizedText(browsersLabel, org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.browsersLabel.text")); // NOI18N

        jScrollPane1.setViewportView(browsersList);
        browsersList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.browsersList.AccessibleContext.accessibleName")); // NOI18N
        browsersList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.browsersList.AccessibleContext.accessibleDescription")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        nameLabel.setLabelFor(nameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.nameLabel.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.nameTextField.text")); // NOI18N

        customPropertyPanel.setMaximumSize(new java.awt.Dimension(350, 250));
        customPropertyPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        customPropertyPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(nameLabel)
                                .addGap(18, 18, 18)
                                .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                            .addComponent(customPropertyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)))
                    .addComponent(browsersLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, removeButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(browsersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nameLabel)
                            .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(customPropertyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(removeButton))
                .addContainerGap())
        );

        browsersLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.browsersLabel.AccessibleContext.accessibleDescription")); // NOI18N
        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.addButton.AccessibleContext.accessibleDescription")); // NOI18N
        removeButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.removeButton.AccessibleContext.accessibleDescription")); // NOI18N
        nameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.nameLabel.AccessibleContext.accessibleDescription")); // NOI18N
        nameTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.nameTextField.AccessibleContext.accessibleName")); // NOI18N
        nameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.nameTextField.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WebBrowsersOptionsPanel.class, "WebBrowsersOptionsPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        
        int index = browsersList.getSelectedIndex();
        browsersModel.removeBrowser(index);
        if (index > 1) {
            browsersList.setSelectedIndex(index - 1);
        } else {
            browsersList.setSelectedIndex(0);
        }
        if (browsersModel.getSize() == 0) {
            removeButton.setEnabled(false);
            removeListenerFromField();
            nameTextField.setText(""); // NOI18N
            addListenerToField();
        }
        
}//GEN-LAST:event_removeButtonActionPerformed

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        
        browsersModel.addBrowser();
        int index = browsersModel.getSize() - 1;
        customPropertyPanel.add(browsersModel.getPropertyPanel(index), browsersModel.getPropertyPanelID(index));
        browsersList.setSelectedIndex(index);
        browsersList.ensureIndexIsVisible(index);
        if (browsersModel.getSize() > 0 && removeButton.isEnabled() == false) {
            removeButton.setEnabled(true);
        }
        
}//GEN-LAST:event_addButtonActionPerformed
    
    public void valueChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting() == false && browsersModel.isAdjusting() == false) {
            int index = browsersList.getSelectedIndex();
            String panelID = browsersModel.getPropertyPanelID(index);
            ((CardLayout) customPropertyPanel.getLayout()).show(customPropertyPanel, panelID);
            nameTextField.setText(browsersModel.getBrowserName(index));
            if (browsersModel.isDefaultBrowser(index)) {
                // if any of default browsers is selected then browser name won't be editable and remove button disabled
                nameTextField.setEditable(false);
                removeButton.setEnabled(false);
            } else {
                nameTextField.setEditable(true);
                removeButton.setEnabled(true);
            }
            browsersModel.setSelectedValue(browsersList.getSelectedValue());
        }
    }

    private class BrowsersDocListener implements DocumentListener {
        
        public void insertUpdate(DocumentEvent e) {
            update(e);
        }
        
        public void removeUpdate(DocumentEvent e) {
            update(e);
        }
        
        public void changedUpdate(DocumentEvent e) {}
        
        private void update(DocumentEvent evt) {
            int index = browsersList.getSelectedIndex();
            Document doc = evt.getDocument();
            if (doc.equals(nameTextField.getDocument())) {
                browsersModel.setBrowserName(index, nameTextField.getText());
            } 
        }
        
    }
    
    private void removeListenerFromField() {
        nameTextField.getDocument().removeDocumentListener(fieldDocListener);
    }
    
    private void addListenerToField() {
        nameTextField.getDocument().addDocumentListener(fieldDocListener);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel browsersLabel;
    private javax.swing.JList browsersList;
    private javax.swing.JPanel customPropertyPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
}
