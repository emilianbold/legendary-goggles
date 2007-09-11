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

package org.netbeans.modules.cnd.navigation.classhierarchy;

import java.beans.PropertyVetoException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
public class ClassHierarchyPanel extends JPanel implements ExplorerManager.Provider {
    public static final String ICON_PATH = "org/netbeans/modules/cnd/navigation/classhierarchy/resources/subtypehierarchy.gif"; // NOI18N

    private AbstractNode root;
    private CsmUID<CsmClass> object;
    private boolean subDirection = true;
    private ExplorerManager explorerManager = new ExplorerManager();;
    
    /** Creates new form ClassHierarchyPanel */
    public ClassHierarchyPanel() {
        initComponents();
        setName(NbBundle.getMessage(getClass(), "CTL_ClassHierarchyTopComponent")); // NOI18N
        setToolTipText(NbBundle.getMessage(getClass(), "HINT_ClassHierarchyTopComponent")); // NOI18N
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        ((BeanTreeView)hierarchyPane).setRootVisible(false);
        Children.Array children = new Children.SortedArray();
        root = new AbstractNode(children);
        getExplorerManager().setRootContext(root);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        directionGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        refreshButton = new javax.swing.JButton();
        subtypeButton = new javax.swing.JToggleButton();
        supertypeButton = new javax.swing.JToggleButton();
        hierarchyPane = new BeanTreeView();

        setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setOrientation(1);
        toolBar.setMaximumSize(new java.awt.Dimension(28, 88));
        toolBar.setMinimumSize(new java.awt.Dimension(28, 88));

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/navigation/classhierarchy/resources/refresh.png"))); // NOI18N
        refreshButton.setToolTipText(org.openide.util.NbBundle.getMessage(ClassHierarchyPanel.class, "ClassHierarchyPanel.refreshButton.toolTipText")); // NOI18N
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setMaximumSize(new java.awt.Dimension(24, 24));
        refreshButton.setMinimumSize(new java.awt.Dimension(24, 24));
        refreshButton.setPreferredSize(new java.awt.Dimension(24, 24));
        refreshButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        toolBar.add(refreshButton);

        directionGroup.add(subtypeButton);
        subtypeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/navigation/classhierarchy/resources/subtypehierarchy.gif"))); // NOI18N
        subtypeButton.setSelected(true);
        subtypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ClassHierarchyPanel.class, "ClassHierarchyPanel.subtypeButton.toolTipText")); // NOI18N
        subtypeButton.setFocusable(false);
        subtypeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        subtypeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        subtypeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        subtypeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        subtypeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        subtypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtypeButtonActionPerformed(evt);
            }
        });
        toolBar.add(subtypeButton);

        directionGroup.add(supertypeButton);
        supertypeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/navigation/classhierarchy/resources/supertypehierarchy.gif"))); // NOI18N
        supertypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ClassHierarchyPanel.class, "ClassHierarchyPanel.supertypeButton.toolTipText")); // NOI18N
        supertypeButton.setFocusable(false);
        supertypeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        supertypeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        supertypeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        supertypeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        supertypeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        supertypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supertypeButtonActionPerformed(evt);
            }
        });
        toolBar.add(supertypeButton);

        add(toolBar, java.awt.BorderLayout.WEST);
        add(hierarchyPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        if (object != null) {
            CsmClass cls = object.getObject();
            if (cls != null){
                update(cls);
            }
        }
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void subtypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtypeButtonActionPerformed
        if (subDirection == subtypeButton.isSelected()) {
            return;
        }
        if (object != null) {
            CsmClass cls = object.getObject();
            if (cls != null){
                subDirection = subtypeButton.isSelected();
                updateButtons();
                update(cls);
            }
        }
    }//GEN-LAST:event_subtypeButtonActionPerformed

    private void supertypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supertypeButtonActionPerformed
        if (subDirection != supertypeButton.isSelected()){
            return;
        }
        if (object != null) {
            CsmClass cls = object.getObject();
            if (cls != null){
                subDirection = !supertypeButton.isSelected();
                updateButtons();
                update(cls);
            }
        }
    }//GEN-LAST:event_supertypeButtonActionPerformed
    
    public void setClass(CsmClass cls){
        object = cls.getUID();
        subDirection = true;
        updateButtons();
        update(cls);
    }
    
    private void updateButtons(){
        if (subDirection) {
            subtypeButton.setSelected(true);
            supertypeButton.setSelected(false);
        } else {
            subtypeButton.setSelected(false);
            supertypeButton.setSelected(true);
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return hierarchyPane.requestFocusInWindow();
    }
    
    private synchronized void update(final CsmClass csmClass) {
        if (csmClass != null){
            final Children children = root.getChildren();
            if (!Children.MUTEX.isReadAccess()){
                Children.MUTEX.writeAccess(new Runnable(){
                    public void run() {
                        children.remove(children.getNodes());
                        final Node node = new HierarchyNode(csmClass, new HierarchyModel(csmClass, subDirection), null);
                        children.add(new Node[]{node});
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                //((BeanTreeView) hierarchyPane).expandAll();
                                ((BeanTreeView) hierarchyPane).expandNode(node);
                                try {
                                    getExplorerManager().setSelectedNodes(new Node[]{node});
                                } catch (PropertyVetoException ex) {
                                }
                            }
                        });
                    }
                });
            }
        } else {
            final Children children = root.getChildren();
            if (!Children.MUTEX.isReadAccess()){
                Children.MUTEX.writeAccess(new Runnable(){
                    public void run() {
                        children.remove(children.getNodes());
                    }
                });
            }
        }
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup directionGroup;
    private javax.swing.JScrollPane hierarchyPane;
    private javax.swing.JButton refreshButton;
    private javax.swing.JToggleButton subtypeButton;
    private javax.swing.JToggleButton supertypeButton;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
    
}
