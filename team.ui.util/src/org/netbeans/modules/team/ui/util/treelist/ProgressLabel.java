/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.team.ui.util.treelist;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import javax.swing.Timer;
import org.jdesktop.swingx.icon.PainterIcon;
import org.jdesktop.swingx.painter.BusyPainter;

/**
 *
 * @author jpeska
 */
public final class ProgressLabel extends TreeLabel {

    private int frame = 0;
    private Timer t;
    private BusyPainter painter;
    private Reference<TreeListNode> refNode = new WeakReference<TreeListNode>(null);
    private Reference<Component> refComp = new WeakReference<Component>(null);

    public ProgressLabel(String text, Component comp) {
        super(text);
        refComp = new WeakReference<Component>(comp);
        setupProgress();
    }

    public ProgressLabel(String text, TreeListNode nd) {
        super(text);
        refNode = new WeakReference<TreeListNode>(nd);
        setupProgress();
    }

    private void setupProgress() {
        painter = new BusyPainter(16);
        PainterIcon icon = new PainterIcon(new Dimension(16, 16));
        icon.setPainter(painter);
        setIcon(icon);
        t = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component comp = refComp.get();
                TreeListNode nd = refNode.get();
                if (nd == null && comp == null) {
                    t.stop();
                    Container p = getParent();
                    if (p != null) {
                        p.remove(ProgressLabel.this);
                    }
                    return;
                } else {
                    frame = (frame + 1) % painter.getPoints();
                    painter.setFrame(frame);
                    ProgressLabel.this.repaint();
                    if (nd != null) {
                        nd.fireContentChanged();
                    } else {
                        comp.repaint();
                    }
                }
            }
        });
        t.setRepeats(true);
        super.setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        boolean old = isVisible();
        super.setVisible(visible);
        if (old != visible) {
            if (visible) {
                t.start();
            } else {
                t.stop();
            }
        }
    }

    /**
     * Stop the timer. Make sure to call this method if you do not explicitly call setVisible(false) on this label. Otherwise, its timer will keep running and it will be referenced forever.
     */
    public void stop() {
        t.stop();
    }

    //The usual cell-renderer performance overrides
    public void repaint() {
        //do nothing
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        //do nothing
    }

    @Override
    public void validate() {
        //do nothing
    }

    @Override
    public void invalidate() {
        //do nothing
    }
}
