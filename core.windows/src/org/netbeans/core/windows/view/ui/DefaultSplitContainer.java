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


package org.netbeans.core.windows.view.ui;


import org.netbeans.swing.tabcontrol.customtabs.Tabbed;
import org.netbeans.core.windows.Constants;
import org.netbeans.core.windows.WindowManagerImpl;
import org.netbeans.core.windows.view.ModeView;
import org.netbeans.core.windows.view.ViewElement;
import org.netbeans.core.windows.view.dnd.TopComponentDroppable;
import org.netbeans.core.windows.view.dnd.WindowDnDManager;
import org.netbeans.core.windows.view.ui.tabcontrol.TabbedAdapter;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import org.netbeans.core.windows.ModeImpl;
import org.netbeans.core.windows.view.dnd.TopComponentDraggable;
import org.netbeans.swing.tabcontrol.customtabs.TabbedComponentFactory;
import org.netbeans.swing.tabcontrol.customtabs.TabbedType;
import org.openide.util.Lookup;
import org.openide.windows.Mode;


/**
 * Implementation of <code>ModeContainer</code> for joined mode kind.
 *
 * @author  Peter Zavadsky
 */
public final class DefaultSplitContainer extends AbstractModeContainer {


    /** JPanel instance representing split mode. */
    private final JPanel panel;
    

    /** Creates a DefaultSeparateContainer. */
    public DefaultSplitContainer(ModeView modeView, WindowDnDManager windowDnDManager, int kind) {
        super(modeView, windowDnDManager, kind);
        
        panel = new ModePanel(this);
        
        panel.add(this.tabbedHandler.getComponent(), BorderLayout.CENTER);
    }
    
    public void requestAttention (TopComponent tc) {
        tabbedHandler.requestAttention(tc);
    }
    
    public void cancelRequestAttention (TopComponent tc) {
        tabbedHandler.cancelRequestAttention(tc);
    }

    @Override
    public void setAttentionHighlight (TopComponent tc, boolean highlight) {
        tabbedHandler.setAttentionHighlight(tc, highlight);
    }

    @Override
    public void makeBusy(TopComponent tc, boolean busy) {
        tabbedHandler.makeBusy( tc, busy );
    }

    /** */
    protected Component getModeComponent() {
        return panel;
    }
    
    @Override
    protected Tabbed createTabbed() {
        TabbedComponentFactory factory = Lookup.getDefault().lookup(TabbedComponentFactory.class);
        TabbedType type = getKind() == Constants.MODE_KIND_EDITOR ? TabbedType.EDITOR : TabbedType.VIEW;
        return factory.createTabbedComponent( type, new TabbedAdapter.WinsysInfo(getKind()));
    }    
    
    protected void updateTitle(String title) {
        // no op
    }
    
    protected void updateActive(boolean active) {
        if(active) {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if(window != null && !window.isActive() && WindowManagerImpl.getInstance().getEditorAreaState() == Constants.EDITOR_AREA_SEPARATED) {
                // only front in SDI, in MID assume that it's either active or user doens't want it active..
                window.toFront();
            }
        }
    }
    
    public boolean isActive() {
        Window window = SwingUtilities.getWindowAncestor(panel);
        // #54791 and #56613 - just a doublecheck, IMHO should not happen anymore
        // after the winsys reenetrancy fix.
        return window != null ? window.isActive() : false;
    }

    protected boolean isAttachingPossible() {
        return true;
    }
    
    protected TopComponentDroppable getModeDroppable() {
        return (ModePanel)panel;
    }


    /** */
    static class ModePanel extends JPanel
    implements ModeComponent, TopComponentDroppable {
    
        private final AbstractModeContainer abstractModeContainer;
        
        public ModePanel(AbstractModeContainer abstractModeContainer) {
            super(new BorderLayout());
            this.abstractModeContainer = abstractModeContainer;
            // To be able to activate on mouse click.
            enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
//            Color fillC = (Color)UIManager.get("nb_workplace_fill"); //NOI18N
//            if (fillC != null) setBackground (fillC);
            if( UIManager.getBoolean( "NbMainWindow.showCustomBackground" ) ) //NOI18N
                setOpaque( false);
        }
        
        @Override
        public ModeView getModeView() {
            return abstractModeContainer.getModeView();
        }
        
        @Override
        public int getKind() {
            return abstractModeContainer.getKind();
        }
        
        // TopComponentDroppable>>
        @Override
        public Shape getIndicationForLocation(Point location) {
            return abstractModeContainer.getIndicationForLocation(location);
        }
        
        @Override
        public Object getConstraintForLocation(Point location) {
            return abstractModeContainer.getConstraintForLocation(location);
        }
        
        @Override
        public Component getDropComponent() {
            return abstractModeContainer.getDropComponent();
        }
        
        @Override
        public ViewElement getDropViewElement() {
            return abstractModeContainer.getDropModeView();
        }
        
        @Override
        public boolean canDrop(TopComponentDraggable transfer, Point location) {
            return abstractModeContainer.canDrop(transfer);
        }
        
        @Override
        public boolean supportsKind(TopComponentDraggable transfer) {
            if( transfer.isModeTransfer() ) {
                ModeView mv = getModeView();
                Mode mode = WindowManagerImpl.getInstance().findMode( mv.getTopComponents().get( 0 ) );
                if( mode.getName().equals( transfer.getMode().getName() ) )
                    return false;
            }
            if(transfer.isAllowedToMoveAnywhere()) {
                return true;
            }
            
            boolean isNonEditor = transfer.getKind() == Constants.MODE_KIND_VIEW || transfer.getKind() == Constants.MODE_KIND_SLIDING;
            boolean thisIsNonEditor = getKind() == Constants.MODE_KIND_VIEW || getKind() == Constants.MODE_KIND_SLIDING;

            return (isNonEditor == thisIsNonEditor);

        }
        // TopComponentDroppable<<
    } // End of ModePanel.
}

