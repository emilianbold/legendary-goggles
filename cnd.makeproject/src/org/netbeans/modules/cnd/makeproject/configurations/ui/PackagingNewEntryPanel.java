/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.makeproject.configurations.ui;

import java.util.ResourceBundle;
import org.netbeans.modules.cnd.makeproject.api.configurations.PackagingConfiguration;
import org.netbeans.modules.cnd.makeproject.packaging.InfoElement;
import org.openide.util.NbBundle;

/**
 *
 * @author  thp
 */
public class PackagingNewEntryPanel extends javax.swing.JPanel {
    private PackagingConfiguration packagingConfiguration;

    /** Creates new form PackagingNewEntryPanel */
    public PackagingNewEntryPanel(PackagingConfiguration packagingConfiguration) {
        initComponents();
        
        this.packagingConfiguration = packagingConfiguration;
        if (packagingConfiguration.getType().getValue() == PackagingConfiguration.TYPE_SVR4_PACKAGE) {
            entryComboBox.addItem("BASEDIR"); // NOI18N
            entryComboBox.addItem("CLASSES"); // NOI18N
            entryComboBox.addItem("DESC"); // NOI18N
            entryComboBox.addItem("EMAIL"); // NOI18N
            entryComboBox.addItem("HOTLINE"); // NOI18N
            entryComboBox.addItem("INTONLY"); // NOI18N
            entryComboBox.addItem("ISTATES"); // NOI18N
            entryComboBox.addItem("MAXINST"); // NOI18N
            entryComboBox.addItem("ORDER"); // NOI18N
            entryComboBox.addItem("PSTAMP"); // NOI18N
            entryComboBox.addItem("RSTATES"); // NOI18N
            entryComboBox.addItem("SUNW_ISA"); // NOI18N
            entryComboBox.addItem("SUNW_LOC"); // NOI18N
            entryComboBox.addItem("SUNW_PKG_DIR"); // NOI18N
            entryComboBox.addItem("SUNW_PKG_ALLZONES"); // NOI18N
            entryComboBox.addItem("SUNW_PKG_HOLLOW"); // NOI18N
            entryComboBox.addItem("SUNW_PKG_THISZONE"); // NOI18N
            entryComboBox.addItem("SUNW_PKGLIST"); // NOI18N
            entryComboBox.addItem("SUNW_PKGTYPE"); // NOI18N
            entryComboBox.addItem("SUNW_PKGVERS"); // NOI18N
            entryComboBox.addItem("SUNW_PRODNAME"); // NOI18N
            entryComboBox.addItem("SUNW_PRODVERS"); // NOI18N
            entryComboBox.addItem("ULIMIT"); // NOI18N
            entryComboBox.addItem("VENDOR"); // NOI18N
            entryComboBox.addItem("VSTOCK"); // NOI18N
        }
        else if (packagingConfiguration.getType().getValue() == PackagingConfiguration.TYPE_RPM_PACKAGE) {
            entryComboBox.addItem("Patch"); // NOI18N
            entryComboBox.addItem("%changelog"); // NOI18N
            entryComboBox.addItem("%pre"); // NOI18N
            entryComboBox.addItem("%post"); // NOI18N
            entryComboBox.addItem("%preun"); // NOI18N
            entryComboBox.addItem("%postun"); // NOI18N
        }
        else
            assert false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        entryLabel = new javax.swing.JLabel();
        entryComboBox = new javax.swing.JComboBox();
        entryValueTextField = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        docArea = new javax.swing.JTextArea();
        docArea.setBackground(getBackground());
        valueLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(600, 400));
        setLayout(new java.awt.GridBagLayout());

        entryLabel.setDisplayedMnemonic('n');
        entryLabel.setLabelFor(entryComboBox);
        entryLabel.setText(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.entryLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(16, 16, 0, 0);
        add(entryLabel, gridBagConstraints);

        entryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entryComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(16, 4, 0, 0);
        add(entryComboBox, gridBagConstraints);
        entryComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.entryComboBox.AccessibleContext.accessibleName")); // NOI18N
        entryComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.entryComboBox.AccessibleContext.accessibleDescription")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(16, 4, 0, 16);
        add(entryValueTextField, gridBagConstraints);
        entryValueTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.entryValueTextField.AccessibleContext.accessibleDescription")); // NOI18N

        scrollPane.setBorder(null);

        docArea.setColumns(20);
        docArea.setEditable(false);
        docArea.setLineWrap(true);
        docArea.setRows(5);
        docArea.setWrapStyleWord(true);
        scrollPane.setViewportView(docArea);
        docArea.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.docArea.AccessibleContext.accessibleName")); // NOI18N
        docArea.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.docArea.AccessibleContext.accessibleDescription")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 16, 16);
        add(scrollPane, gridBagConstraints);

        valueLabel.setDisplayedMnemonic('v');
        valueLabel.setLabelFor(entryValueTextField);
        valueLabel.setText(org.openide.util.NbBundle.getMessage(PackagingNewEntryPanel.class, "PackagingNewEntryPanel.valueLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(14, 8, 0, 0);
        add(valueLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void entryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entryComboBoxActionPerformed
    String elemName = (String)entryComboBox.getSelectedItem();
        docArea.setText(""); // NOI18N
    
        if (elemName.equals("BASEDIR")) { //NOI18N
            docArea.setText(getString("PACKAGING_BASEDIR_DOC"));
        }
        else if (elemName.equals("CLASSES")) { //NOI18N
            docArea.setText(getString("PACKAGING_CLASSES_DOC"));
        }
        else if (elemName.equals("DESC")) { //NOI18N
            docArea.setText(getString("PACKAGING_DESC_DOC"));
        }
        else if (elemName.equals("EMAIL")) { //NOI18N
            docArea.setText(getString("PACKAGING_EMAIL_DOC"));
        }
        else if (elemName.equals("HOTLINE")) { //NOI18N
            docArea.setText(getString("PACKAGING_HOTLINE_DOC"));
        }
        else if (elemName.equals("INTONLY")) { //NOI18N
            docArea.setText(getString("PACKAGING_INTONLY_DOC"));
        }
        else if (elemName.equals("ISTATES")) { //NOI18N
            docArea.setText(getString("PACKAGING_ISTATES_DOC"));
        }
        else if (elemName.equals("MAXINST")) { //NOI18N
            docArea.setText(getString("PACKAGING_MAXINST_DOC"));
        }
        else if (elemName.equals("ORDER")) { //NOI18N
            docArea.setText(getString("PACKAGING_ORDER_DOC"));
        }
        else if (elemName.equals("PSTAMP")) { //NOI18N
            docArea.setText(getString("PACKAGING_PSTAMP_DOC"));
        }
        else if (elemName.equals("RSTATES")) { //NOI18N
            docArea.setText(getString("PACKAGING_RSTATES_DOC"));
        }
        else if (elemName.equals("SUNW_ISA")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_ISA_DOC"));
        }
        else if (elemName.equals("SUNW_LOC")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_LOC_DOC"));
        }
        else if (elemName.equals("SUNW_PKG_DIR")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKG_DIR_DOC"));
        }
        else if (elemName.equals("SUNW_PKG_ALLZONES")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKG_ALLZONES_DOC"));
        }
        else if (elemName.equals("SUNW_PKG_HOLLOW")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKG_HOLLOW_DOC"));
        }
        else if (elemName.equals("SUNW_PKG_THISZONE")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKG_THISZONE_DOC"));
        }
        else if (elemName.equals("SUNW_PKGLIST")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKGLIST_DOC"));
        }
        else if (elemName.equals("SUNW_PKGTYPE")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKGTYPE_DOC"));
        }
        else if (elemName.equals("SUNW_PKGVERS")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PKGVERS_DOC"));
        }
        else if (elemName.equals("SUNW_PRODNAME")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PRODNAME_DOC"));
        }
        else if (elemName.equals("SUNW_PRODVERS")) { //NOI18N
            docArea.setText(getString("PACKAGING_SUNW_PRODVERS_DOC"));
        }
        else if (elemName.equals("ULIMIT")) { //NOI18N
            docArea.setText(getString("PACKAGING_ULIMIT_DOC"));
        }
        else if (elemName.equals("VENDOR")) { //NOI18N
            docArea.setText(getString("PACKAGING_VENDOR_DOC"));
        }
        else if (elemName.equals("VSTOCK")) { //NOI18N
            docArea.setText(getString("PACKAGING_VSTOCK_DOC"));
        }
}//GEN-LAST:event_entryComboBoxActionPerformed

public InfoElement getInfoElement() {
    String name = (String)entryComboBox.getSelectedItem();
    String value = entryValueTextField.getText();
    return new InfoElement(name, value);
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea docArea;
    private javax.swing.JComboBox entryComboBox;
    private javax.swing.JLabel entryLabel;
    private javax.swing.JTextField entryValueTextField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel valueLabel;
    // End of variables declaration//GEN-END:variables

    
    /** Look up i18n strings here */
    private static ResourceBundle bundle;

    private static String getString(String s) {
        if (bundle == null) {
            bundle = NbBundle.getBundle(PackagingNewEntryPanel.class);
        }
        return bundle.getString(s);
    }
}
