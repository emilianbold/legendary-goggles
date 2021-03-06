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
package org.netbeans.modules.xml.catalog.impl;

import java.beans.*;
import java.io.File;
import java.net.MalformedURLException;
import org.netbeans.modules.xml.catalog.impl.XCatalog;
import static org.netbeans.modules.xml.catalog.impl.res.Bundle.*;

/**
 * XML catalog customizer. It allows to customize catalog location.
 *
 * @author  Petr Kuzel
 * @version
 */
public class XCatalogCustomizer extends javax.swing.JPanel implements Customizer {

    /** Serial Version UID */
    private static final long serialVersionUID =-1437233290256708363L;

    XCatalog model = null;

    /** Creates new customizer XCatalogCustomizer */
    public XCatalogCustomizer() {
        initComponents ();

        this.getAccessibleContext().setAccessibleDescription(ACSD_XCatalogCustomizer());
        locationLabel.setDisplayedMnemonic(XCatalogCustomizer_locationLabel_mne().charAt(0));
        locationTextField.getAccessibleContext().setAccessibleDescription(ACSD_locationTextField());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        locationLabel = new javax.swing.JLabel();
        locationTextField = new javax.swing.JTextField();
        descTextArea = new javax.swing.JTextArea();
        selectButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        locationLabel.setLabelFor(locationTextField);
        locationLabel.setText(XCatalogCustomizer_locationLabel_text());
        add(locationLabel, new java.awt.GridBagConstraints());

        locationTextField.setColumns(20);
        locationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationTextFieldActionPerformed(evt);
            }
        });
        locationTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                locationTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(locationTextField, gridBagConstraints);

        descTextArea.setEditable(false);
        descTextArea.setFont(javax.swing.UIManager.getFont ("Label.font"));
        descTextArea.setForeground(new java.awt.Color(102, 102, 153));
        descTextArea.setLineWrap(true);
        descTextArea.setText(DESC_xcatalog_fmts());
        descTextArea.setWrapStyleWord(true);
        descTextArea.setDisabledTextColor(javax.swing.UIManager.getColor ("Label.foreground"));
        descTextArea.setEnabled(false);
        descTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(descTextArea, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/xml/catalog/impl/res/Bundle"); // NOI18N
        selectButton.setText(bundle.getString("PROP_choose_file")); // NOI18N
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(selectButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        File f = org.netbeans.modules.xml.catalog.lib.Util.selectCatalogFile("txt xml cat catalog"); // NOI18N
        if (f == null) return;
        try {
            String location = f.toURL().toExternalForm();
            locationTextField.setText(location);
            model.setSource(location);
        } catch (MalformedURLException ex) {
            // ignore
        }
    }//GEN-LAST:event_selectButtonActionPerformed

    //!!! find out whether action performed is not enought
    
    private void locationTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_locationTextFieldFocusLost
        //if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug("FocusLost-setting location: " + locationTextField.getText()); // NOI18N
        model.setSource(locationTextField.getText());
    }//GEN-LAST:event_locationTextFieldFocusLost

    private void locationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationTextFieldActionPerformed
        //if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug("ActionPerformed-setting location: " + locationTextField.getText()); // NOI18N
        model.setSource(locationTextField.getText());
    }//GEN-LAST:event_locationTextFieldActionPerformed

    /**
     * Set model for this customizer.
     */
    public void setObject(java.lang.Object peer) {
        if ((peer instanceof XCatalog) == false) {
            throw new IllegalArgumentException("XCatalog instance expected (" + peer.getClass() + ").");  // NOI18N
        }
        
        model = (XCatalog) peer;        
        locationTextField.setText(model.getSource());
    }    

    public void addPropertyChangeListener(java.beans.PropertyChangeListener p1) {
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener p1) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JButton selectButton;
    // End of variables declaration//GEN-END:variables

}
