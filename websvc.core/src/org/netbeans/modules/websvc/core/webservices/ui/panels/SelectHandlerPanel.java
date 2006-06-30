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

package org.netbeans.modules.websvc.core.webservices.ui.panels;

import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import java.awt.BorderLayout;
import java.awt.Dimension;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.NbBundle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.api.project.Project;
import org.openide.nodes.Children;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import java.util.HashSet;
import org.openide.nodes.AbstractNode;

/**
 * @author  rico
 */
public class SelectHandlerPanel extends JPanel implements ExplorerManager.Provider {
    
    private ExplorerManager manager;
    private Node[] selectedNodes;
    private JPanel panel;
    private Project project;
    
    /** Creates a new instance of SelectHandlerPanel */
    public SelectHandlerPanel(Project project) {
        initComponents();
        this.project = project;
        manager = new ExplorerManager();
        manager.addPropertyChangeListener(
        new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                selectedNodes = manager.getSelectedNodes();
            }
        });
        populateTree();
    }
    
    public Node[] getSelectedNodes(){
        return selectedNodes;
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    private void populateTree(){
        LogicalViewProvider lvp = (LogicalViewProvider)project.getLookup().lookup(LogicalViewProvider.class);
        HashSet set = new HashSet();
        set.add(lvp.createLogicalView());
        Children.Array children = new Children.Array();
        children.add((Node[]) set.toArray(new Node[set.size()]));
        Node root = new AbstractNode(children);
        root.setDisplayName(NbBundle.getMessage(SelectHandlerPanel.class, "LBL_Select_Handler_Class"));
        manager.setRootContext(root);
    }
    
    private void initComponents() {
        panel = new JPanel();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(550, 440));
        panel.setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        
        BeanTreeView btv = new BeanTreeView();
        btv.getAccessibleContext().
        setAccessibleName(NbBundle.getMessage(SelectHandlerPanel.class,"LBL_Class_Tree"));
        btv.getAccessibleContext().setAccessibleDescription
        (NbBundle.getMessage(SelectHandlerPanel.class,"TTL_SelectHandler"));
        panel.add(btv, "Center");   //NOI18N
        panel.validate();
        validate();
    }
}
