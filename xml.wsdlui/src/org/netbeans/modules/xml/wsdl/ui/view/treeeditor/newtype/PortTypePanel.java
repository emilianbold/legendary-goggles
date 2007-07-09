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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * PortTypePanel.java
 *
 * Created on September 7, 2006, 4:28 PM
 */

package org.netbeans.modules.xml.wsdl.ui.view.treeeditor.newtype;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.wsdl.model.PortType;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.ui.actions.NameGenerator;
import org.netbeans.modules.xml.wsdl.ui.api.property.PropertyUtil;
import org.netbeans.modules.xml.wsdl.ui.view.OperationConfigurationPanel;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author  radval
 */
public class PortTypePanel extends javax.swing.JPanel {
    
    private Project mProject;
    
    private WSDLModel mModel;
    
    private DialogDescriptor mDD;
    
    String mErrorMessage = null;
    
    private List existingMeessages = new ArrayList();
            
    /** Creates new form PortTypePanel */
    public PortTypePanel() {
        initComponents();
        initGUI();
    }
    
    public PortTypePanel(Project project, WSDLModel model) {
        this.mProject = project;
        this.mModel = model;
        initComponents();
        initGUI();
    }
    
    public void setDialogDescriptor(DialogDescriptor dd) {
        this.mDD = dd;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        portTypeConfigurationPanel1 = new org.netbeans.modules.xml.wsdl.ui.view.OperationConfigurationPanel(this.mProject, true, mModel, true);
        commonMessagePanel1 = new org.netbeans.modules.xml.wsdl.ui.view.common.CommonMessagePanel();

        portTypeConfigurationPanel1.setFocusCycleRoot(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(portTypeConfigurationPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(107, 107, 107)
                        .add(commonMessagePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(portTypeConfigurationPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                .add(36, 36, 36)
                .add(commonMessagePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    public OperationConfigurationPanel getPortTypeConfiguration() {
        return this.portTypeConfigurationPanel1;
    }
    
    private void initGUI() {
        
        ModelSource modelSource = this.mModel.getModelSource();
        FileObject wsdlFile = modelSource.getLookup().lookup(FileObject.class);
        if(wsdlFile != null) {
            String fileName = wsdlFile.getName();
            String portTypePrefix = fileName + NbBundle.getMessage(PortTypePanel.class, "LBL_PortType_suffix");
            String portTypeName = NameGenerator.getInstance().generateUniquePortTypeName(this.mModel, portTypePrefix);
            this.portTypeConfigurationPanel1.setPortTypeName(portTypeName);

            String operationName = fileName + NbBundle.getMessage(PortTypePanel.class, "LBL_Operation_suffix");
            this.portTypeConfigurationPanel1.setOperationName(operationName);
            
            String[] messages = PropertyUtil.getAllMessages(this.mModel);
            if(messages != null) {
                for(int i = 0; i < messages.length; i++) {
                 existingMeessages.add(messages[i]);
                }
            }
            
            String inputMessageName = NameGenerator.getInstance().generateUniqueInputMessageName(operationName, mModel);
            String outputMessageName = NameGenerator.getInstance().generateUniqueOutputMessageName(operationName, mModel);
            String faultMessageName = NameGenerator.getInstance().generateUniqueFaultMessageName(operationName, mModel);

            MessageNameTextChangeListener messageListener = new MessageNameTextChangeListener();
                    
            this.portTypeConfigurationPanel1.setInputMessages(messages, inputMessageName, messageListener);
            this.portTypeConfigurationPanel1.setOutputMessages(messages, outputMessageName, messageListener);
            this.portTypeConfigurationPanel1.setFaultMessages(messages, faultMessageName, messageListener);
            
            
        }
        
                
        PortTypeNameTextChangeListener portTypeListner  = new PortTypeNameTextChangeListener();
        OperationNameTextChangeListener operationListener  = new OperationNameTextChangeListener();
        
        this.portTypeConfigurationPanel1.getPortTypeNameTextField().getDocument().addDocumentListener(portTypeListner);
        this.portTypeConfigurationPanel1.getOperationNameTextField().getDocument().addDocumentListener(operationListener);
    }
    
    private boolean isValidName(String text) {
        try {
            boolean isValid  = org.netbeans.modules.xml.xam.dom.Utils.isValidNCName(text);
            if(!isValid) {
                mErrorMessage = NbBundle.getMessage(OperationPanel.class, "ERR_MSG_INVALID_NAME" , text);
            } else {
                mErrorMessage = null;
            }
            
        }  catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return mErrorMessage == null;
    }
    
    private void validateAll() {
        boolean validPortType = isValidName(this.portTypeConfigurationPanel1.getPortTypeNameTextField().getText());
        if(!validPortType) {
            updateMessagePanel();
            return;
        }
        
        
        boolean isPortTypeExist = isPortTypeExists();
        if(isPortTypeExist) {
            updateMessagePanel();
            return;
        }
        
        
        boolean validOperation = isValidName(this.portTypeConfigurationPanel1.getOperationNameTextField().getText());
        if(!validOperation) {
            updateMessagePanel();
            return;
        }
        
                
        boolean isValidInputMessage = isValidInputMessage();
        if(!isValidInputMessage) {
            updateMessagePanel();
            return;
        }
        
        boolean isValidOutputMessage = isValidOutputMessage();
        if(!isValidOutputMessage) {
            updateMessagePanel();
            return;
        }
        
        boolean isValidFaultMessage = isValidFaultMessage();
        if(!isValidFaultMessage) {
            updateMessagePanel();
            return;
        }
        
        this.mErrorMessage = null;
        updateMessagePanel();
        
    }
    
    private boolean isPortTypeExists() {
        boolean exist = false;
        
        String text = this.portTypeConfigurationPanel1.getPortTypeNameTextField().getText();
        PortType pt = mModel.findComponentByName(text, PortType.class);
            
        if(pt != null) {
            this.mErrorMessage = "PortType \"" + text + "\" already exists.";
            exist = true;
        } 
                
        return exist;
    }

    private boolean isValidInputMessage() {
        boolean valid = true;
        
        String messageName = this.portTypeConfigurationPanel1.getInputMessageName();
        if(messageName != null) {
            //if message is not existing message
            if(!isExistingMessage(messageName)) {
                valid = isValidName(messageName);
            }
        }
        return valid;
    }
    
    private boolean isValidOutputMessage() {
        boolean valid = true;
        
        String messageName = this.portTypeConfigurationPanel1.getOutputMessageName();
        if(messageName != null) {
            //if message is not existing message
            if(!isExistingMessage(messageName)) {
                valid = isValidName(messageName);
            }
        }
        return valid;
    }

        
    private boolean isValidFaultMessage() {
        boolean valid = true;
        
        String messageName = this.portTypeConfigurationPanel1.getFaultMessageName();
        if(messageName != null) {
            //if message is not existing message
            if(!isExistingMessage(messageName)) {
                valid = isValidName(messageName);
            }
        }
        return valid;
    }
    
    
    private boolean isExistingMessage(String messageName) {
         return existingMeessages.contains(messageName);
     }
    
    private void updateMessagePanel() {
        if(this.mErrorMessage != null) {
            commonMessagePanel1.setErrorMessage(mErrorMessage);
            if(this.mDD != null) {
                this.mDD.setValid(false);
            }
        } else {
            commonMessagePanel1.setMessage("");
            if(this.mDD != null) {
                this.mDD.setValid(true);
            }
        }
        
    }    
    
    class PortTypeNameTextChangeListener implements DocumentListener {
     
         public void changedUpdate(DocumentEvent e) {
            validateAll();
         }
         
         public void insertUpdate(DocumentEvent e) {
             validateAll();
         }

         public void removeUpdate(DocumentEvent e) {
             validateAll();
         }
 
    }
    
    class OperationNameTextChangeListener implements DocumentListener {
     
         public void changedUpdate(DocumentEvent e) {
            validateAll();
         }
         
         public void insertUpdate(DocumentEvent e) {
             validateAll();
         }

         public void removeUpdate(DocumentEvent e) {
             validateAll();
         }
 

        
    }
    
    class MessageNameTextChangeListener implements DocumentListener {
     
         public void changedUpdate(DocumentEvent e) {
             validateAll();
         }
         
         public void insertUpdate(DocumentEvent e) {
             validateAll();
         }

         public void removeUpdate(DocumentEvent e) {
             validateAll();
         }
         
         
        
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.xml.wsdl.ui.view.common.CommonMessagePanel commonMessagePanel1;
    private org.netbeans.modules.xml.wsdl.ui.view.OperationConfigurationPanel portTypeConfigurationPanel1;
    // End of variables declaration//GEN-END:variables
    
}
