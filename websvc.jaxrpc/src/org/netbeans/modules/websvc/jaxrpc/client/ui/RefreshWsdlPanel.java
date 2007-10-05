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

package org.netbeans.modules.websvc.jaxrpc.client.ui;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.websvc.api.webservices.WsCompileEditorSupport;

/**
 *
 * @author  peterw99
 */
public class RefreshWsdlPanel extends javax.swing.JPanel {

    public static final Color ErrorTextForegroundColor = new Color(89, 79, 191);
    
    private DialogDescriptor descriptor;
    private String wsdlSource;
    private boolean hasMultipleServices;
    private String [] serviceNames;
    
    
    /** Creates new form RefreshWsdlPanel */
    public RefreshWsdlPanel(String wsdlSource, String [] supportedServices) {
        this.wsdlSource = wsdlSource;
        this.hasMultipleServices = (supportedServices.length > 1);
        this.serviceNames = supportedServices;
        
        initComponents();
        initUserComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLblDescription = new javax.swing.JLabel();
        jLblWsdlSource = new javax.swing.JLabel();
        jTxtWsdlSource = new javax.swing.JTextField();
        jLblMultipleServices = new javax.swing.JLabel();
        jLblError = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLblDescription.setText(NbBundle.getMessage(RefreshWsdlPanel.class, "LBL_Description"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 11);
        add(jLblDescription, gridBagConstraints);

        jLblWsdlSource.setLabelFor(jTxtWsdlSource);
        jLblWsdlSource.setText(NbBundle.getMessage(RefreshWsdlPanel.class, "LBL_WsdlSource"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 11, 0);
        add(jLblWsdlSource, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 11, 11);
        add(jTxtWsdlSource, gridBagConstraints);

        jLblMultipleServices.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 11);
        add(jLblMultipleServices, gridBagConstraints);

        jLblError.setText("xxx");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 11);
        add(jLblError, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblDescription;
    private javax.swing.JLabel jLblError;
    private javax.swing.JLabel jLblMultipleServices;
    private javax.swing.JLabel jLblWsdlSource;
    private javax.swing.JTextField jTxtWsdlSource;
    // End of variables declaration//GEN-END:variables
    
    private void initUserComponents() {
        jLblError.setForeground(ErrorTextForegroundColor);
        jTxtWsdlSource.setText(wsdlSource);
        
        jTxtWsdlSource.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                updateWsdlSource();
            }
            public void insertUpdate(DocumentEvent e) {
                updateWsdlSource();
            }
            public void changedUpdate(DocumentEvent e) {
                updateWsdlSource();
            }
        });
        
        if(hasMultipleServices) {
            StringBuffer buf = new StringBuffer(16+16*serviceNames.length);
            for(int i = 0; i < serviceNames.length; i++) {
                if(i > 0) {
                    buf.append(", "); // NOI18N
                }
                buf.append(serviceNames[i]);
            }
            jLblMultipleServices.setText(NbBundle.getMessage(RefreshWsdlPanel.class, 
                "LBL_MultipleServiceWarning", buf.toString())); // NOI18N
        } else {
            jLblMultipleServices.setText(" "); // NOI18N
        }
    }
    
    public void addNotify() {
        super.addNotify();
        
        checkSettings();
    }
    
    private void updateWsdlSource() {
        wsdlSource = jTxtWsdlSource.getText();
        checkSettings();
    }

    public String getWsdlSource() {
        return wsdlSource;
    }
    
    public void setDescriptor(DialogDescriptor descriptor) {
		this.descriptor = descriptor;
	}
    
    private boolean checkSettings() {
        String message = validateSettings();
        if(message != null) {
            jLblError.setText(message);
        } else {
            jLblError.setText(" ");
        }
        
        boolean isValid = (message == null);
        descriptor.setValid(isValid);
        return isValid;
    }
    
    private String validateSettings() {
        String message = null;

        if(wsdlSource == null || wsdlSource.length() == 0) {
            message = NbBundle.getMessage(RefreshWsdlPanel.class, "ERR_EnterSourceWsdlPath"); // NOI18N
        } else if(wsdlSource.indexOf("://") != -1) { // NOI18N
            try {
                URL wsdlSourceUrl = new URL(wsdlSource);
            } catch(MalformedURLException ex) {
                // not a URL
                message = NbBundle.getMessage(RefreshWsdlPanel.class, "ERR_InvalidURL", ex.getLocalizedMessage()); // NOI18N
            }
        } else {
            File wsdlSourceFile = new File(wsdlSource);
            if(!wsdlSourceFile.exists()) {
                message = NbBundle.getMessage(RefreshWsdlPanel.class, "ERR_FileDoesNotExist"); // NOI18N
            }
        }
        
        return message;
    }
}
