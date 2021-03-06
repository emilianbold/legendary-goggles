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
package org.netbeans.modules.collab.ui.actions;

import java.awt.*;
import javax.swing.JPanel;

import org.openide.util.*;
import org.openide.util.actions.NodeAction;
import org.openide.windows.WindowManager;

/**
 *
 *
 * @author Todd Fast <todd.fast@sun.com>
 */
public class TestAction extends NodeAction {
    protected boolean enable(org.openide.nodes.Node[] node) {
        return true;
    }

    public String getName() {
        return "Test";
    }

    protected String iconResource() {
        return "org/openide/resources/actions/empty.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return true;
    }

    protected void performAction(org.openide.nodes.Node[] node) {
        System.out.println("----------");

        Container container = WindowManager.getDefault().getMainWindow();
        listChildren(container, "");
    }

    private void listChildren(Container parent, String indent) {
        for (int i = 0; i < parent.getComponentCount(); i++) {
            Component component = parent.getComponent(i);
            System.out.println(indent + component.getClass() /*+" ["+component+"]"*/);

            if (component instanceof Container) {
                if (component.getClass().getName().equals("org.netbeans.core.windows.view.ui.StatusLine")) {
                    JPanel panel = (JPanel) component.getParent();

                    //					System.out.println(component.getParent().toString());
                    //					System.out.println(component.toString());
                }

                listChildren((Container) component, indent + "  ");
            }
        }

        //TabControl
    }
}
