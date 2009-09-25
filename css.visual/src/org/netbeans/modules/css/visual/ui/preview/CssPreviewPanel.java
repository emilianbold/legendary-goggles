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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.css.visual.ui.preview;

import java.awt.Graphics;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.openide.awt.StatusDisplayer;
import org.xhtmlrenderer.simple.XHTMLPanel;

/**
 * JPanel wrapping XHTMLPanel, the Flying Saucer's rendering area.
 * The class also suppresses exceptions falling from the renderer
 * so they are just logged, not displayed to user as execeptions.
 *
 * @author  Marek Fukala
 */
public class CssPreviewPanel extends javax.swing.JPanel implements CssPreviewComponent {

    private static final Logger LOGGER = Logger.getLogger(CssPreviewPanel.class.getName());
    private static final boolean LOG = LOGGER.isLoggable(Level.INFO);
    
    private Handler FS_HANDLER = new FlyingSaucerLoggersHandler();
    
    private XHTMLPanel xhtmlPanel = new XHTMLPanel() {
        //workaround for FlyingSaucer bug (reported as netbeans issue #117499 (NullPointerException for unreachable url))
        @Override
        public void paintComponent(Graphics g) {
            try {
                super.paintComponent(g);
            } catch (Throwable e) {
                if(LOG) {
                    LOGGER.log(Level.INFO, "It seems there is a bug in FlyinSaucer XHTML renderer.", e);
                }
                CssPreviewTopComponent.getDefault().setError();
            }
        }
    };
    
    /** Creates new form CssPreviewPanel2 */
    public CssPreviewPanel() {
        initComponents();
        jScrollPane1.setViewportView(xhtmlPanel);
        
        configureFlyingSaucerLoggers();
    }
    
    public XHTMLPanel panel() {
        return xhtmlPanel;
    }

    public void setDocument(InputStream is, String url) throws Exception {
        xhtmlPanel.setDocument(is, url);
    }

    public JComponent getComponent() {
        return this;
    }

    public void dispose() {
        // nothing to dispose here
    }

    private void configureFlyingSaucerLoggers() {
        //remove potential flying saucer handlers
        Logger logger = Logger.getLogger("plumbing.exception");
        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }
        //do not report event to the parent handler ...
        logger.setUseParentHandlers(false);
        //...just to me
        logger.addHandler(FS_HANDLER);
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    //delegating flying saucer handler
    private class FlyingSaucerLoggersHandler extends Handler {

        public void publish(LogRecord record) {
            if (LOG) {
                //set log level to INFO to prevent the exceptions
                //popping up in a netbeans exceptions dialog
                record.setLevel(Level.INFO);
                LOGGER.log(record);
                //log the exception message to output
                LOGGER.log(Level.WARNING, record.getMessage());
                //...and to the status bar
                StatusDisplayer.getDefault().setStatusText(record.getMessage());
            }
        }

        public void flush() {
        }

        public void close() throws SecurityException {
        }
    }
    
}
