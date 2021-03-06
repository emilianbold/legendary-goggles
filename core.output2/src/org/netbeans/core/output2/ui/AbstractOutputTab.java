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

package org.netbeans.core.output2.ui;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import org.netbeans.core.output2.Controller;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import org.netbeans.core.output2.OutputDocument;
import org.netbeans.core.output2.options.OutputOptions;

/**
 * A basic output pane.  This class implements the non-output window specific
 * gui management for the output window - creating the text component,
 * locking the caret and scrollbar to the bottom of the document to the 
 * bottom, etc.  Could be merged with OutputView, but it's more readable
 * and maintainable to keep the pure gui code separate.  Mainly contains 
 * logic for layout and showing and hiding a toolbar and input area.
 *
 * @author  Tim Boudreau
 */
public abstract class AbstractOutputTab extends JComponent implements Accessible {
    private boolean inputVisible = false;
    private AbstractOutputPane outputPane;
    private Action[] actions = new Action[0];  
    protected static final String ACCELERATORS_KEY = "ACCELERATORS_KEY";//NOI18N

    private Component toFocus;
    
    public AbstractOutputTab() {
        outputPane = createOutputPane();
        add (outputPane);
        setFocusable(false);
    }
    
    public void setDocument (Document doc) {
        outputPane.setDocument(doc);
        //#114290
        if (doc instanceof OutputDocument) {
            ((OutputDocument)doc).setPane(outputPane);
        }
    }

    public AbstractOutputPane setOutputPane(AbstractOutputPane pane) {
        AbstractOutputPane old = outputPane;
        remove(outputPane);
        outputPane = pane;
        add(outputPane);
        return old;
    }

    /* Read accessible context
     * @return - accessible context
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJComponent() {
                        @Override
                        public AccessibleRole getAccessibleRole() {
                            // is it really a panel?
                            return AccessibleRole.PANEL;
                        }

                        @Override
                        public String getAccessibleName() {
                            if (accessibleName != null) {
                                return accessibleName;
                            }
                            return getName();
                        }
                    };
        }

        return accessibleContext;
    }
    

    /**
     * on mouse click the specialized component is marked, and activation is requested.
     * activation results in request focus on the tab -> the marked component gets focus.
     */
    public void setToFocus(Component foc) {
        toFocus = foc;
    }
    
    @Override
    public void requestFocus() {
    // on mouse click the specialized component is marked, and activation is requested.
    // activation results in request focus on the tab -> the marked component gets focus.
        if (toFocus != null) {
            toFocus.requestFocus();
            toFocus = null;
            return;
        }
        outputPane.requestFocus();
    }
    
    @Override
    public boolean requestFocusInWindow() {
        return getOutputPane().requestFocusInWindow();
    }    

    protected abstract AbstractOutputPane createOutputPane();
    
    public abstract void inputSent (String txt);

    /**
     * Accessed reflectively from org.netbeans.jellytools.OutputTabOperator.
     */
    public final AbstractOutputPane getOutputPane() {
        return outputPane;
    }

    public final void setToolbarActions(Action[] a) {
        if (a == null || a.length == 0) {
            actions = new Action[0];
            return;
        }
        actions = new Action[a.length];
        for (int i = 0; i < a.length; i++) {
            actions[i] = a[i];
            installKeyboardAction(actions[i]);
        }
    }

    /**
     * Get the toolbar actions, if any, which have been supplied by the client.
     * Used to add them to the popup menu if they return a non-null name.
     *
     * @return An array of actions
     */
    public Action[] getToolbarActions() {
        return actions;
    }

    /**
     * Install a keyboard action.  This is used in two places - all toolbar actions with
     * accelerator keys and names will also be installed as keyboard actions.  Also, the
     * master controller installs its actions which should be accessible via the keyboard.
     * The actions are actually installed into the text control.
     *
     * @param a An action to install, if its name and accelerator are non-null
     */
    public void installKeyboardAction (Action a) {
        if (!(a instanceof WeakAction)) {
            //It is a Controller.ControllerAction - don't create a memory leak by listening to it
            a = new WeakAction(a);
        }
        KeyStroke[] accels = null;
        String name;
        Object o = a.getValue(ACCELERATORS_KEY);
        if (o instanceof KeyStroke[]) {
            accels = (KeyStroke[]) o;
        }
        name = (String) a.getValue(Action.NAME);
        if (accels != null) {
            for (KeyStroke accel : accels) {
                if (Controller.LOG) {
                    Controller.log("Installed action " //NOI18N
                            + name + " on " + accel);                   //NOI18N
                }
                // if the logic here changes, check the popup escaping hack in
                // Controller it temporarily removes the VK_ESCAPE from input
                // maps..
                JComponent c = getOutputPane().textView;
                c.getInputMap().put(accel, name);
                c.getActionMap().put(name, a);
                getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(accel, name);
                getActionMap().put(name, a);
            }
        }
    }

    public final boolean isInputVisible() {
        return inputVisible;
    }
    
    public void setInputVisible(boolean val) {
        if (val == isInputVisible()) {
            return;
        }
        inputVisible = val;
        outputPane.textView.setEditable(val);
        validate();
        getOutputPane().ensureCaretPosition();
    }

    protected abstract void inputEof();

    @Override
    public void doLayout() {
        Insets ins = getInsets();
        int left = ins.left;
        int bottom = getHeight() - ins.bottom;
        
        Component main = outputPane;
        
        if (main != null) {
            main.setBounds (left, ins.top, getWidth() - (left + ins.right), 
                bottom - ins.top);
        }
    }

    public abstract void hasSelectionChanged(boolean val);
    
    void notifyInputFocusGained(){
        getOutputPane().lockScroll();
        getOutputPane().ensureCaretPosition();
    }
}
