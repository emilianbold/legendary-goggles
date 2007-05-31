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
package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webservice;

import java.util.ResourceBundle;
import javax.swing.JComponent;
import org.netbeans.modules.j2ee.sun.dd.api.common.ServiceRef;
import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;
import org.netbeans.modules.j2ee.sun.ddloaders.SunDescriptorDataObject;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.DDTextFieldEditorModel;
import org.netbeans.modules.xml.multiview.ItemEditorHelper;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataSynchronizer;
import org.netbeans.modules.xml.multiview.ui.SectionNodeInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;


/**
 *
 * @author Peter Williams
 */
public class ServiceRefPanel extends SectionNodeInnerPanel {

    static final ResourceBundle bundle = ResourceBundle.getBundle(
            "org.netbeans.modules.j2ee.sun.share.configbean.customizers.webservice.Bundle"); // NOI18N

    private SunDescriptorDataObject dataObject;
    private ServiceRef serviceRef;
    private ASDDVersion version;

	/** The two tabbed panels */
//	private ServiceRefGeneralPanel generalPanel;
//	private ServiceRefPortInfoPanel portInfoPanel;

	public ServiceRefPanel(SectionNodeView sectionNodeView, final ServiceRef serviceRef, final ASDDVersion version) {
        super(sectionNodeView);
        this.dataObject = (SunDescriptorDataObject) sectionNodeView.getDataObject();
        this.serviceRef = serviceRef;
        this.version = version;
        
		initComponents();
		initUserComponents();
	}

	private void initUserComponents() {
        XmlMultiViewDataSynchronizer synchronizer = dataObject.getModelSynchronizer();
        addRefreshable(new ItemEditorHelper(jTxtName, new ServiceRefTextFieldEditorModel(synchronizer, ServiceRef.SERVICE_REF_NAME)));
        addRefreshable(new ItemEditorHelper(jTxtWsdlOverride, new ServiceRefTextFieldEditorModel(synchronizer, ServiceRef.WSDL_OVERRIDE)));
	}
    
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPnlServiceName = new javax.swing.JPanel();
        jLblName = new javax.swing.JLabel();
        jTxtName = new javax.swing.JTextField();
        jLblWsdlOverride = new javax.swing.JLabel();
        jTxtWsdlOverride = new javax.swing.JTextField();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPnlServiceName.setOpaque(false);
        jPnlServiceName.setLayout(new java.awt.GridBagLayout());

        jLblName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLblName.setText(ServiceRefCustomizer.bundle.getString("LBL_ServiceReferenceName_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPnlServiceName.add(jLblName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPnlServiceName.add(jTxtName, gridBagConstraints);

        jLblWsdlOverride.setText(ServiceRefCustomizer.bundle.getString("LBL_WsdlOverride_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlServiceName.add(jLblWsdlOverride, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPnlServiceName.add(jTxtWsdlOverride, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(jPnlServiceName, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblName;
    private javax.swing.JLabel jLblWsdlOverride;
    private javax.swing.JPanel jPnlServiceName;
    private javax.swing.JTextField jTxtName;
    private javax.swing.JTextField jTxtWsdlOverride;
    // End of variables declaration//GEN-END:variables

//	private void initUserComponents() {
//		// add general panel
//		generalPanel = new ServiceRefGeneralPanel(this);
//		serviceRefTabbedPanel.addTab(bundle.getString("TAB_General"), generalPanel);	// NOI18N
//
//		// add port info panel
//		portInfoPanel = new ServiceRefPortInfoPanel(this);
//		serviceRefTabbedPanel.addTab(bundle.getString("TAB_PortInfo"), portInfoPanel);	// NOI18N
//	}
//
//	protected void initFields() {
//		generalPanel.initFields(theBean);
//		portInfoPanel.initFields(theBean);
//	}
//
//	public void partitionStateChanged(ErrorMessageDB.PartitionState oldState, ErrorMessageDB.PartitionState newState) {
//		if(newState.getPartition() == getPartition()) {
//			showErrors();
//		}
//
//		if(oldState.hasMessages() != newState.hasMessages()) {
//			serviceRefTabbedPanel.setIconAt(newState.getPartition().getTabIndex(), newState.hasMessages() ? panelErrorIcon : null);
//		}
//	}
//
//	protected void addListeners() {
//		super.addListeners();
//		generalPanel.addListeners(theBean);
//		portInfoPanel.addListeners(theBean);
//	}
//	
//	protected void removeListeners() {
//		super.removeListeners();
//		generalPanel.removeListeners(theBean);
//		portInfoPanel.removeListeners(theBean);
//	}	
	
//	/** Retrieve the partition that should be associated with the current
//	 *  selected tab.
//	 *
//	 *  @return ValidationError.Partition
//	 */
//	public ValidationError.Partition getPartition() {
//		switch(serviceRefTabbedPanel.getSelectedIndex()) {
//			case 1:
//				return ValidationError.PARTITION_SERVICEREF_PORTINFO;
//			default:
//				return ValidationError.PARTITION_SERVICEREF_GENERAL;
//		}
//	}

    public String getHelpId() {
		String result = "AS_CFG_ServiceRefGeneral"; // NOI18N

//		// Determine which tab has focus and return help context for that tab.
//		switch(serviceRefTabbedPanel.getSelectedIndex()) {
//			case 1:
//				result = "AS_CFG_ServiceRefPortInfo";	// NOI18N
//				break;
//		}

		return result;
	}

    public void setValue(JComponent source, Object value) {
    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {
    }

    public JComponent getErrorComponent(String errorId) {
        return null;
    }
    
    // Model class for handling updates to the text fields
    private class ServiceRefTextFieldEditorModel extends DDTextFieldEditorModel {

        public ServiceRefTextFieldEditorModel(XmlMultiViewDataSynchronizer synchronizer, String propertyName) {
            super(synchronizer, propertyName);
        }
        
        public ServiceRefTextFieldEditorModel(XmlMultiViewDataSynchronizer synchronizer, String propertyName, String attributeName) {
            super(synchronizer, propertyName, attributeName);
        }

        protected CommonDDBean getBean() {
            return serviceRef;
        }
        
    }
}
