/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.multiview.ui;

import java.awt.*;
import javax.swing.*;

import org.openide.explorer.view.BeanTreeView;
import org.openide.windows.*;

/**
 * The TreePanelDesignEditor two pane editor. This is basically a container that implements the ExplorerManager
 * interface. It coordinates the selection of a node in the structure pane and the display of a panel by the a PanelView
 * in the content pane. It will populate the tree view in the structure pane
 * from the root node of the supplied PanelView.
 *
 **/

public class TreePanelDesignEditor extends AbstractDesignEditor {
    
    public static final int CONTENT_RIGHT = 0;
    public static final int CONTENT_LEFT = 1;
    
    /** The default width of the ComponentInspector */
    public static final int DEFAULT_STRUCTURE_WIDTH = 170;
    /** The default height of the ComponentInspector */
    public static final int DEFAULT_STRUCTURE_HEIGHT = 300;
    /** The default percents of the splitting of the ComponentInspector */
    public static final int DEFAULT_CONTENT_SPLIT = 150;
    
    protected static EmptyInspectorNode emptyInspectorNode;
    
    /** Default icon base for control panel. */
    private static final String EMPTY_INSPECTOR_ICON_BASE =
    "/org/netbeans/modules/form/resources/emptyInspector"; // NOI18N
    
    
    
    protected JSplitPane split;
    protected int panelOrientation;
    
    /** The icon for ComponentInspector */
    protected static String iconURL = "/org/netbeans/modules/form/resources/inspector.gif"; // NOI18N
    
    protected static final long serialVersionUID =1L;
    
    /**
     * Creates a new instance of ComponentPanel
     * @param panel The PanelView which will provide the node tree for the structure view
     *              and the set of panels the nodes map to.
     */
    public TreePanelDesignEditor(PanelView panel) {
        super(panel);
    }
    
    /**
     * Creates a new instance of ComponentPanel
     * @param panel The PanelView which will provide the node tree for the structure view
     *              and the set of panels the nodes map to.
     * @param orientation Determines if the content pane is on the left or the right.
     */
    public TreePanelDesignEditor(PanelView panel, int orientation){
        super(panel);
        panelOrientation = orientation;
    }
    /**
     * Creates a new instance of ComponentPanel
     * @param panel The PanelView which will provide the node tree for the structure view
     *              and the set of panels the nodes map to.
     * @param structure The JComponent that will be used in the structure pane. Should follow the
     *			ExplorerManager protocol. Will usually be some subclass of BeanTreeView.
     */
    
    public TreePanelDesignEditor(PanelView panel, JComponent structure){
        super(panel,structure);
    }
    /**
     * Creates a new instance of ComponentPanel
     * @param panel The PanelView which will provide the node tree for the structure view
     *              and the set of panels the nodes map to.
     * @param structure The JComponent that will be used in the structure pane. Should follow the
     *			ExplorerManager protocol. Will usually be some subclass of BeanTreeView.
     * @param orientation Determines if the content pane is on the left or the right.
     */
    
    public TreePanelDesignEditor(PanelView panel, JComponent structure, int orientation){
        super(panel,structure);
        panelOrientation = orientation;
    }
    
     protected void initComponents() {
        super.initComponents();
         add(BorderLayout.CENTER,createDesignPanel());
        
    }
   
    
    protected JComponent createDesignPanel() {
        if (panelOrientation == CONTENT_LEFT)
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,getContentView(), getStructureView());
        else
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,getStructureView(), getContentView());
        split.setOneTouchExpandable(true);
	  return split;
        
    }
    
    /**
     * Used to get the JComponent used for the structure pane. Usually a container for the structure component or the structure component itself.
     * @return the JComponent
     */
    public JComponent getStructureView(){
        if (structureView ==null){
            structureView = createStructureComponent();
            structureView.getAccessibleContext().setAccessibleName("ACS_StructureView");
            structureView.getAccessibleContext().setAccessibleDescription("ACSD_StructureView");
            structureView.setMinimumSize(new Dimension(100,100));
            structureView.setPreferredSize(new Dimension(DEFAULT_STRUCTURE_WIDTH ,DEFAULT_STRUCTURE_HEIGHT ));
        }
        return structureView;
    }
    /**
     * Used to create an instance of the JComponent used for the structure component. Usually a subclass of BeanTreeView.
     * @return the JComponent
     */
    public JComponent createStructureComponent() {
        return new BeanTreeView();
    }
    
 
     /**
     * Used to create an instance of the JComponent used for the properties component. Usually a subclass of PropertySheetView.
     * @return JComponent
     */
    public JComponent createPropertiesComponent(){
        return null;
    }

    public ErrorPanel getErrorPanel() {
        return getContentView().getErrorPanel();
    }
    
}
