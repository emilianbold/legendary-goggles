/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * CacheHelperPanel.java
 *
 * Created on January 7, 2004, 5:11 PM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.beans.PropertyVetoException;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;
import org.netbeans.modules.j2ee.sun.dd.api.web.CacheHelper;
import org.netbeans.modules.j2ee.sun.dd.api.web.DefaultHelper;
import org.netbeans.modules.j2ee.sun.dd.api.web.WebProperty;

import org.netbeans.modules.j2ee.sun.share.configbean.StorageBeanFactory;
import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot;
import org.netbeans.modules.j2ee.sun.share.configbean.WebAppCache;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTableModel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTablePanel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.HelpContext;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.data.DynamicPropertyPanel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.data.PropertyListMapping;


/**
 *
 * @author Peter Williams
 */
public class CacheHelperPanel extends javax.swing.JPanel implements TableModelListener {
	
	// Standard resource bundle from common
	private static final ResourceBundle commonBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.Bundle");	// NOI18N
	
	// Resource bundle for webapp
	private static final ResourceBundle webappBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp.Bundle");	// NOI18N
	
	private WebAppRootCustomizer masterPanel;
	
	// Table for editing default helper web properties
	private GenericTableModel defaultHelperPropertiesModel;
	private GenericTablePanel defaultHelperPropertiesPanel;
	
	// Table for editing cache helper classes
	private GenericTableModel cacheHelperClassesModel;
	private GenericTablePanel cacheHelperClassesPanel;
	
	/** Creates new form CacheHelperPanel */
	public CacheHelperPanel(WebAppRootCustomizer src) {
		masterPanel = src;
		
		initComponents();
		initUserComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_CacheHelpersTab"));
        getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_CacheHelpersTab"));
    }//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	
	private void initUserComponents() {
		/** Default helper properties table panel :
		 *  TableEntry list has three properties: Name, Value, Description
		 */
		ArrayList tableColumns = new ArrayList(3);
		tableColumns.add(new GenericTableModel.AttributeEntry(
			WebProperty.NAME, commonBundle.getString("LBL_Name"), true));	// NOI18N
		tableColumns.add(new GenericTableModel.AttributeEntry(
			WebProperty.VALUE, commonBundle.getString("LBL_Value"), true));	// NOI18N
		tableColumns.add(new GenericTableModel.ValueEntry(
			WebProperty.DESCRIPTION, commonBundle.getString("LBL_Description")));	// NOI18N		
		
		// Default helper property table
		defaultHelperPropertiesModel = new GenericTableModel(DefaultHelper.PROPERTY, WebAppRootCustomizer.webPropertyFactory, tableColumns);
		defaultHelperPropertiesPanel = new GenericTablePanel(defaultHelperPropertiesModel, 
			webappBundle, "DefaultHelperProperties",	// NOI18N - property name
			DynamicPropertyPanel.class, HelpContext.HELP_CACHE_DEFAULT_HELPER_POPUP,
			PropertyListMapping.getPropertyList(PropertyListMapping.CACHE_DEFAULT_HELPER_PROPERTIES));
		
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		add(defaultHelperPropertiesPanel, gridBagConstraints);
		
		/** Cache helper classes table panel :
		 *  TableEntry list has three properties: Name, Classname, Properties
		 */
		tableColumns = new ArrayList(3);
			tableColumns.add(new GenericTableModel.AttributeEntry(
				CacheHelper.NAME, commonBundle.getString("LBL_Name"), true));	// NOI18N
		tableColumns.add(new GenericTableModel.AttributeEntry(
			CacheHelper.CLASSNAME, webappBundle.getString("LBL_Classname"), true));	// NOI18N
		tableColumns.add(new PropertiesEntry());
		
		// add Property table
		cacheHelperClassesModel = new GenericTableModel(cacheHelperFactory, tableColumns);
		cacheHelperClassesPanel = new GenericTablePanel(cacheHelperClassesModel, 
			webappBundle, "CacheHelperProperties",	// NOI18N - property name
			CacheHelperEntryPanel.class, HelpContext.HELP_CACHE_HELPER_DEFINITION_POPUP);
		
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		add(cacheHelperClassesPanel, gridBagConstraints);		
	}
	
	public void addListeners() {
		defaultHelperPropertiesModel.addTableModelListener(this);
		cacheHelperClassesModel.addTableModelListener(this);
	}
	
	public void removeListeners() {
		defaultHelperPropertiesModel.removeTableModelListener(this);
		cacheHelperClassesModel.removeTableModelListener(this);
	}
	
	/** Initialization of all the fields in this panel from the bean that
	 *  was passed in.
	 */
	public void initFields(WebAppCache cacheBean) {
		defaultHelperPropertiesPanel.setModel(cacheBean.getDefaultHelper());
		cacheHelperClassesPanel.setModel(cacheBean.getCacheHelpers());		
	}
	
	/** ----------------------------------------------------------------------- 
	 *  Implementation of javax.swing.event.TableModelListener
	 */
	public void tableChanged(TableModelEvent e) {
		WebAppRoot bean = masterPanel.getBean();
		if(bean != null) {
			WebAppCache cacheBean = bean.getCacheBean();
			Object eventSource = e.getSource();
			try {
				if(eventSource == defaultHelperPropertiesModel) {
					// This statement will not produce a change event because
					// of the way we handle property storage for properties like
					// this one.  (The DefaultHelper we're modifying is already the
					// one owned by the bean).
					//
//					cacheBean.setDefaultHelper((DefaultHelper) defaultHelperPropertiesModel.getDataBaseBean());
				} else if(eventSource == cacheHelperClassesModel) {
					List cacheHelperList = cacheHelperClassesModel.getData();
					cacheBean.setCacheHelpers(cacheHelperList);
					masterPanel.firePropertyChange(WebAppRootCustomizer.CACHE_HELPER_LIST_CHANGED, false, true);
				}

				// Force property change to be issued by the bean
				bean.setDirty();
			} catch(PropertyVetoException ex) {
				// FIXME undo whatever changed... how?
			}
		}		
	}
    
    // New for migration to sun DD API model.  Factory instance to pass to generic table model
    // to allow it to create cacheHelper beans.
	static GenericTableModel.ParentPropertyFactory cacheHelperFactory =
        new GenericTableModel.ParentPropertyFactory() {
            public CommonDDBean newParentProperty() {
                return StorageBeanFactory.getDefault().createCacheHelper();
            }
        };
}
