/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.lang.model.element.TypeElement;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.modules.j2ee.ejbcore._RetoucheUtil;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 * Action that can always be invoked and work procedurally.
 * 
 * @author Chris Webster
 * @author Martin Adamek
 */
public abstract class AbstractAddMethodAction extends AbstractAction implements Presenter.Popup, ContextAwareAction {
    
    /** Action context. */
    private Lookup context;
    private final AbstractAddMethodStrategy strategy;

    public AbstractAddMethodAction(AbstractAddMethodStrategy strategy) {
        super(/*strategy.getTitle()*/);
        this.strategy = strategy;
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        this.context = actionContext;
        boolean enable = enable(actionContext.lookup(new Lookup.Template<Node>(Node.class)).allInstances().toArray(new Node[0]));
        return enable ? this : null;
    }
    
    public String getName(){
        return strategy.getTitle();
    }
    
    protected boolean enable(org.openide.nodes.Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return false;
        }
        FileObject fileObject = activatedNodes[0].getLookup().lookup(FileObject.class);
        try {
            if (fileObject != null) {
                ElementHandle<TypeElement> elementHandle = _RetoucheUtil.getJavaClassFromNode(activatedNodes[0]);
                if (elementHandle != null) {
                    if (strategy.supportsEjb(fileObject, elementHandle.getQualifiedName())) {
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        performAction(context.lookup(new Lookup.Template<Node>(Node.class)).allInstances().toArray(new Node[0]));
    }
    
    protected void performAction(org.openide.nodes.Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return;
        }
        FileObject fileObject = activatedNodes[0].getLookup().lookup(FileObject.class);
        try {
            if (fileObject != null) {
                ElementHandle<TypeElement> elementHandle = _RetoucheUtil.getJavaClassFromNode(activatedNodes[0]);
                if (elementHandle != null) {
                    if (strategy.supportsEjb(fileObject, elementHandle.getQualifiedName())) {
                        strategy.addMethod(fileObject, elementHandle.getQualifiedName());
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public Object getValue(String key) {
        if (NAME.equals(key)) {
            return getName();
        } else {
            return super.getValue(key);
        }
    }

    public JMenuItem getPopupPresenter() {
        return new JMenuItem (this);
    }

}
