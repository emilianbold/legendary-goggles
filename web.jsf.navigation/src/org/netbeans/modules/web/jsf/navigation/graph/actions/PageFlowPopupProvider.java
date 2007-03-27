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
 *
 * NodePopupMenuProvider.java
 *
 * Created on February 2, 2007, 6:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.web.jsf.navigation.graph.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.web.jsf.navigation.graph.PageFlowScene;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author joelle
 */
public class PageFlowPopupProvider implements PopupMenuProvider {
    
    PageFlowScene graphScene;
    
    private JMenuItem miAddWebPage;
    private JPopupMenu graphPopup;
    
    private String addPage = NbBundle.getMessage(PageFlowPopupProvider.class, "MSG_AddPage");
    
    /**
     * Creates a Popup for any right click on Page Flow Editor
     * @param graphScene The related PageFlow Scene.
     */
    public PageFlowPopupProvider(PageFlowScene graphScene) {
        
        this.graphScene = graphScene;
        initialize();
    }
    
    
    // <actions from layers>
    private static final String PATH_PAGEFLOW_ACTIONS = "PageFlowEditor/PopupActions"; // NOI18N
//        private static final String PATH_PAGEFLOW_ACTIONS = "PageFlowEditor/application/x-pageflow/Popup"; // NOI18N
    private void initialize() {
        InstanceContent ic = new InstanceContent();
        ic.add(graphScene);
        Lookup lookup = new AbstractLookup(ic);
        
        graphPopup = Utilities.actionsToPopup(
                SystemFileSystemSupport.getActions(PATH_PAGEFLOW_ACTIONS), lookup);
        //        graphPopup = new JPopupMenu("Transition Menu");
        //
        //        graphPopup = new JPopupMenu();
        //        miAddWebPage = new JMenuItem(addPage);
        //        miAddWebPage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
        //        miAddWebPage.addActionListener(this);
        //        graphPopup.add(miAddWebPage);
    }
    
    
    
    public JPopupMenu getPopupMenu(Widget widget, Point point){
        return graphPopup;
    }
    
    
    /** Weak reference to the lookup. */
    private WeakReference<Lookup> lookupWRef = new WeakReference<Lookup>(null);
    
    /** Adds <code>NavigatorLookupHint</code> into the original lookup,
     * for the navigator. */
    private Lookup getLookup() {
        Lookup lookup = (Lookup)lookupWRef.get();
        
        if (lookup == null) {
            InstanceContent ic = new InstanceContent();
            //                ic.add(firstObject);
            ic.add(graphScene);
            lookup = new AbstractLookup(ic);
            lookupWRef = new WeakReference<Lookup>(lookup);
        }
        
        return lookup;
    }
    
}
