/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.openide.explorer;

import java.awt.EventQueue;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import junit.framework.AssertionFailedError;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class ExplorerActionsImplTest extends NbTestCase implements PropertyChangeListener {
    private volatile AssertionFailedError err;
    private volatile int cnt;

    public ExplorerActionsImplTest(String s) {
        super(s);
    }

    @Override
    protected boolean runInEQ() {
        return true;
    }

    public void testIllegalStateException() throws Exception {
        N root = new N();
        final N ch1 = new N();
        final N ch2 = new N();
        final N ch3 = new N();
        PT mockPaste = new PT();
        ch3.pasteTypes = Collections.<PasteType>singletonList(mockPaste);

        root.getChildren().add(new Node[] { ch1, ch2, ch3 });
        final ExplorerManager em = new ExplorerManager();
        em.setRootContext(root);
        em.setSelectedNodes(new Node[] { root });
        Action action = ExplorerUtils.actionPaste(em);
        Action cut = ExplorerUtils.actionCut(em);
        assertFalse("Not enabled", action.isEnabled());
        
        action.addPropertyChangeListener(this);
        cut.addPropertyChangeListener(this);
        

        em.setSelectedNodes(new Node[] { ch3 });
        assertFalse("Cut is not enabled", cut.isEnabled());
        assertTrue("Now enabled", action.isEnabled());
        action.actionPerformed(new ActionEvent(this, 0, ""));

        assertEquals("The paste type is going to be called", 1, mockPaste.cnt);
        
        if (err != null) {
            throw err;
        }
        if (cnt == 0) {
            fail("There should be some change in actions: " + cnt);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (err == null && !EventQueue.isDispatchThread()) {
            err = new AssertionFailedError("Properties should be delivered in AWT Event thread");
        }
        cnt++;
    }

    private static class PT extends PasteType {
        int cnt;

        @Override
        public Transferable paste() throws IOException {
            assertTrue("paste is performed synchronously", EventQueue.isDispatchThread());
            cnt++;
            return null;
        }
    }

    private static class N extends AbstractNode {
        List<PasteType> pasteTypes;

        public N() {
            super(new Children.Array());
        }

        @Override
        protected void createPasteTypes(Transferable t, List<PasteType> s) {
            assertFalse("Don't block AWT", EventQueue.isDispatchThread());
            if (pasteTypes != null) {
                s.addAll(pasteTypes);
            }
        }
    }
}