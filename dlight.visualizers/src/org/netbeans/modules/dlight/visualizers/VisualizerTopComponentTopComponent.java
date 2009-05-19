/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.dlight.visualizers;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.naming.event.EventDirContext;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.dlight.spi.visualizer.Visualizer;
import org.netbeans.modules.dlight.spi.visualizer.VisualizerContainer;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
public final class VisualizerTopComponentTopComponent extends TopComponent implements VisualizerContainer {

    private static VisualizerTopComponentTopComponent instance;
    private static final String PREFERRED_ID = "VisualizerTopComponentTopComponent";//NOI18N
    //private List<JComponent> visualizerComponents = new ArrayList<JComponent>();
    //private CloseListener closeListener = new CloseListener();
    private JPanel performanceMonitorViewsArea = new JPanel();
    private JComponent viewComponent;
    private String currentToolName;
//    private JTabbedPane tabbedPane = null;
    //private HashMap<String, Visualizer> visualizerComponents = new HashMap<String, Visualizer>();
    
    private VisualizerTopComponentTopComponent() {
        initComponents();
        initPerformanceMonitorViewComponents();
        setName(NbBundle.getMessage(VisualizerTopComponentTopComponent.class, "CTL_VisualizerTopComponentTopComponent"));//NOI18N
        setToolTipText(NbBundle.getMessage(VisualizerTopComponentTopComponent.class, "HINT_VisualizerTopComponentTopComponent"));//NOI18N
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setLayout(new java.awt.BorderLayout());
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized VisualizerTopComponentTopComponent getDefault() {
        if (instance == null) {
            instance = new VisualizerTopComponentTopComponent();
        }
        return instance;
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        viewComponent.requestFocus();
        viewComponent.requestFocusInWindow();
    }



    /**
     * Obtain the VisualizerTopComponentTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized VisualizerTopComponentTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(VisualizerTopComponentTopComponent.class.getName()).warning(
                "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");//NOI18N
            return getDefault();
        }
        if (win instanceof VisualizerTopComponentTopComponent) {
            return (VisualizerTopComponentTopComponent) win;
        }
        Logger.getLogger(VisualizerTopComponentTopComponent.class.getName()).warning(
            "There seem to be multiple components with the '" + PREFERRED_ID +//NOI18N
            "' ID. That is a potential source of errors and unexpected behavior.");//NOI18N
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public void setContent(String toolName, JComponent viewComponent) {
        if (currentToolName != null && currentToolName.equals(toolName) && this.viewComponent == viewComponent){//INCORRECT! should update if different component itself
            return;//DO NOTHING
        }
        currentToolName = toolName;
//        if (visualizerComponents.get(toolName) == null) {//no component - add new one
//            tabbedPane.addTab(toolName, viewComponent);
////      visualizerComponents.put(tool, view);
//        } else {
//            //we should remove the tabs and add
//            int index = tabbedPane.indexOfTab(toolName);
//            if (index != -1){
//                closePerformanceMonitor(visualizerComponents.get(toolName));
//                tabbedPane.insertTab(toolName, null, viewComponent, toolName, index);
//                tabbedPane.setSelectedComponent(viewComponent);
//            }else{
//                tabbedPane.addTab(toolName, viewComponent);
//                tabbedPane.setSelectedComponent(viewComponent);
//            }
//        }
//
//        visualizerComponents.put(toolName, view);
        //if we have it already DO NOT REMOVE - REUSE
        this.performanceMonitorViewsArea.removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.viewComponent = viewComponent;
        this.performanceMonitorViewsArea.add(viewComponent);
        this.setName( toolName);
        this.setToolTipText(toolName);
        validate();
        repaint();
    }

    public void addVisualizer(String toolName, Visualizer view) {
        setContent(toolName, view.getComponent());
        view.refresh();

    }

    public void showup() {
        open();
        requestActive();
    }

    public void removeVisualizer(final Visualizer v) {
        if (EventQueue.isDispatchThread()){
            closePerformanceMonitor(v);
        }else{
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    closePerformanceMonitor(v);
                }
            });
        }
    }

    public void addContent(String toolName, JComponent viewComponent) {
        if (currentToolName == null || !currentToolName.equals(toolName) || this.viewComponent != viewComponent) {
            this.currentToolName = toolName;
            this.performanceMonitorViewsArea.removeAll();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }
        this.viewComponent = viewComponent;
        this.performanceMonitorViewsArea.add(viewComponent);
        this.setName( toolName);
        this.setToolTipText(toolName);
        validate();
        repaint();

    }

    static final class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return VisualizerTopComponentTopComponent.getDefault();
        }
    }

    private void initPerformanceMonitorViewComponents() {
        setLayout(new BorderLayout());
//        tabbedPane = TabbedPaneFactory.createCloseButtonTabbedPane();
//        tabbedPane.addPropertyChangeListener(closeListener);
        performanceMonitorViewsArea.setLayout(new BorderLayout());
        //     performanceMonitorViewsArea.add(tabbedPane, BorderLayout.CENTER);
        this.add(performanceMonitorViewsArea, BorderLayout.CENTER);
    }

    public void closePerformanceMonitor(Visualizer view) {
//    view.stopMonitor();
//        closePerformanceMonitor(viewComponent);
        if (viewComponent != view.getComponent()){//nothing to do
            return;
        }
        performanceMonitorViewsArea.remove( view.getComponent());
        setName(NbBundle.getMessage(VisualizerTopComponentTopComponent.class, "RunMonitorDetailes"));
        repaint();
    }

//    private void closePerformanceMonitor(JComponent viewComponent) {
//        visualizerComponents.remove(viewComponent);
//
//        if (tabbedPane != null) {
//            tabbedPane.remove(viewComponent);
//        }
//        performanceMonitorViewsArea.remove(viewComponent);
//        viewComponent = null;
//        validate();
//        repaint();
//    }

//    private class CloseListener implements PropertyChangeListener {
//
//        public void propertyChange(java.beans.PropertyChangeEvent evt) {
//            if (TabbedPaneFactory.PROP_CLOSE.equals(evt.getPropertyName())) {
//                closePerformanceMonitor((JComponent) evt.getNewValue());
//            }
//        }
//    }
}
