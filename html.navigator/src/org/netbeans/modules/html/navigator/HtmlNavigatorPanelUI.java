/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.html.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.html.editor.api.gsf.HtmlParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.web.clientproject.api.ServerURLMapping;
import org.netbeans.modules.web.inspect.PageInspectorImpl;
import org.netbeans.modules.web.inspect.PageModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author marekfukala
 */
public class HtmlNavigatorPanelUI extends JPanel implements ExplorerManager.Provider {

    private static final Logger LOGGER = Logger.getLogger(HtmlNavigatorPanelUI.class.getSimpleName());
    private static final String NOT_CONNECTED = "no connection";
    private static final String CONNECTED = "connected";
    public static RequestProcessor RP = new RequestProcessor(HtmlNavigatorPanelUI.class);
    private static int MESSAGE_SHOW_TIME = 5000; //5 seconds
    private ExplorerManager manager;
    private BeanTreeView view;
    private Lookup lookup;
    private PageModel pageModel;
    private FileObject inspectedFileObject;
    //the UI actions
    private Action[] panelActions;
    //UI stuff
    private WaitNode waitNode = new WaitNode();
    private JLabel statusLabel;
    private JLabel stateLabel;
    private JPanel statusPanel;
    private final PropertyChangeListener pageInspectorListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if (PageInspectorImpl.PROP_MODEL.equals(propName)) {
                setPageModel(PageInspectorImpl.getDefault().getPage());
            }
        }
    };
    private final PropertyChangeListener pageModelListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if (PageModel.PROP_DOCUMENT.equals(propName)) {
                pageModelDocumentChanged();
            }

        }
    };

    private final LookupListener ll = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent ev) {
            refresh((Lookup.Result<FileObject>) ev.getSource());
        }
    };
    
    private HtmlElementDescription sourceDescription;

    public HtmlNavigatorPanelUI() {
        manager = new ExplorerManager();
        view = new BeanTreeView();

        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);

        statusLabel = new JLabel();
        stateLabel = new JLabel(NOT_CONNECTED);
        stateLabel.setEnabled(false);
        statusPanel = new JPanel();
        statusPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(stateLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        manager.setRootContext(waitNode);

        panelActions = new Action[]{};

        lookup = ExplorerUtils.createLookup(manager, getActionMap());

        //listen on the page inspector
        PageInspectorImpl.getDefault().addPropertyChangeListener(pageInspectorListener);
    }

    //Set a new PageModel. It will install a new PropertyChangeListener for the PageModel changes
    private void setPageModel(PageModel model) {
        PageModel old = this.pageModel;
        this.pageModel = model;
        
        if (model == null) {
            //the new model is null model, disable
            if(old != null) {
                old.removePropertyChangeListener(pageModelListener);
            }

            stateLabel.setEnabled(false);
            stateLabel.setText(NOT_CONNECTED);
            setStatusText("Disconnected");

            //we need to explicitly call pageModelDocumentChanged() since
            //no change event from PageModel will come and we need to refresh
            //the nodes dom status
            pageModelRemoved();

        } else {
            //new model

            //possibly remove the old listener
            if (old != null) {
                old.removePropertyChangeListener(pageModelListener);
            }
            //add new listener to the pagemodel
            model.addPropertyChangeListener(pageModelListener);

            stateLabel.setEnabled(true);
            stateLabel.setText(CONNECTED);
            setStatusText("Connected");

            //no need to explicitly call pageModelDocumentChanged() as the
            //PageModel fill fire change event
        }

    }
    
    private void pageModelRemoved() {
        //refresh DOM status to empty
        HtmlElementNode root = getRootNode();
        if (root != null) {
            root.setDescription(Description.empty(Description.DOM));
        }
        inspectedFileObject = null;
    }

    private void pageModelDocumentChanged() {
        setStatusText("DOM has changed.");

        //try to find corresponding FileObject for the inspected document
        FileObject current = getInspectedFileFromPageModel();
        if (inspectedFileObject == null) {
            if (current != null) {
                //starting the inspection 
                inspectedFileObject = current;
                inspectedFileChanged(null, current);
            }
        } else {
            if (!inspectedFileObject.equals(current)) {
                //inspected file changed
                FileObject old = inspectedFileObject;
                inspectedFileObject = current;
                inspectedFileChanged(old, current);
            } else {
                //still same inspected file, just refresh the dom
                refreshNodeDOMStatus();
            }
        }
        
    }
    
    private void inspectedFileChanged(FileObject old, FileObject neww) {
        updateInspectedFileUI();
        
        HtmlElementNode root = getRootNode();
        if(root != null) {
            if(!root.getFileObject().equals(neww)) {
                //the inspected file no more corresponds to the navigator's active file
                root.setDescription(Description.empty(Description.DOM));
            } else {
                //same file as in the root node
                //=> reset the dom 
                refreshNodeDOMStatus();
            }
        }
    }

    private void updateInspectedFileUI() {
        if (inspectedFileObject == null) {
            LOGGER.log(Level.INFO, "inspectedFileObject set to null");
            setStatusText("No Inspected File");
            stateLabel.setText("No Inspected File");
        } else {
            LOGGER.log(Level.INFO, "inspectedFileObject set to {0}", inspectedFileObject.getPath());
            setStatusText("Inspecting " + inspectedFileObject.getNameExt());
            stateLabel.setText(String.format("Inspecting %s", inspectedFileObject.getNameExt()));
        }
    }

    private FileObject getInspectedFileFromPageModel() {
        //try to find corresponding FileObject for the inspected document
        if (pageModel == null) {
            return null;
        }
        String inspectedURL = pageModel.getDocumentURL();
        try {
            
            URL url = new URL(inspectedURL);
            Project owner = getCurrentProject();
            return ServerURLMapping.fromServer(owner, url);
        } catch (MalformedURLException ex) {
            Logger.getAnonymousLogger().log(
                    Level.WARNING, 
                    String.format("Cannot resolve URL '%s' obtained from web.inspect's PageModel.", inspectedURL), 
                    ex);
        }
        return null;
    }
    
    private void refreshDOM() {
        try {
            final PageModel page = PageInspectorImpl.getDefault().getPage();
            if (page==null)
                return;
            String inspectedURL = page.getDocumentURL();

            URL url = new URL(inspectedURL);
            FileObject fromServer = ServerURLMapping.fromServer(getCurrentProject(), url);


            if (fromServer == null || !fromServer.equals(inspectedFileObject)) {
                return;
            }
            RP.post(new Runnable() {
                @Override
                public void run() {
                    refreshNodeDOMStatus();
                }
            });
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void refreshNodeDOMStatus() {
        LOGGER.info("refreshNodeDOMStatus()");
        HtmlElementNode root = getRootNode();
        if (root != null) {
            //if we are inspecting the current file, the source changes propagates just after the fresh DOM is received
            if(sourceDescription != null) {
                root.setDescription(sourceDescription);
                sourceDescription = null;
                setSynchronizationState(true);
                updateInspectedFileUI(); //set the status text back to the inspected file
            }
            setPageModel(PageInspectorImpl.getDefault().getPage());
            //now apply to dom descriptions
            WebKitNodeDescription domDescription = new WebKitNodeDescription(null, Utils.getWebKitNode(pageModel.getDocumentNode()));
            root.setDescription(domDescription);
            
            LOGGER.info("root.refreshDOMStatus() called");
        }
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public FileObject getInspectedFileObject() {
        return inspectedFileObject;
    }

    private void setStatusText(String text) {
        LOGGER.log(Level.INFO, "HtmlNavigator: {0}", text);

        statusLabel.setText(text);

        RP.post(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        statusLabel.setText(null);
                    }
                });
            }
        }, MESSAGE_SHOW_TIME);
    }

    public Action[] getActions() {
        return panelActions;
    }

    void activate(Lookup context) {
        Result<FileObject> fileResult = context.lookupResult(FileObject.class);
        fileResult.addLookupListener(ll);
        refresh(fileResult);
    }
    
    private void refresh(Lookup.Result<FileObject> fileResult) {
        Collection<? extends FileObject> allInstances = fileResult.allInstances();
        if (allInstances.isEmpty()) {
            return;
        }
        final FileObject fo = allInstances.iterator().next();

        Source source = Source.create(fo);
        if (source == null) {
            return;
        }
        showWaitNode();
        
        try {
            ParserManager.parse(Collections.singleton(source), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    setParserResult((HtmlParserResult)resultIterator.getParserResult());
                    inspectedFileObject = fo;
                    refreshDOM();
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    

    void deactivate() {
        setPageModel(null);
        //TODO: remove lookup listener
    }

    private void showWaitNode() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                view.setRootVisible(true);
                manager.setRootContext(waitNode);
            }
        };
        
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    public HtmlElementNode getRootNode() {
        Node node = manager.getRootContext();
        return node instanceof HtmlElementNode ? (HtmlElementNode) node : null;
    }

    public void selectElementNode(final int offset) {
        HtmlElementNode root = getRootNode();
        if (root == null) {
            return;
        }

        final Node match = root.getNodeForOffset(offset);
        if (match == null) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Node[] selectedNodes = manager.getSelectedNodes();
                if (!(selectedNodes != null && selectedNodes.length == 1 && selectedNodes[0] == match)) {
                    try {
                        manager.setSelectedNodes(new Node[]{match});
                        LOGGER.log(Level.INFO, "Selected node set to {0}", match.getDisplayName());
                    } catch (PropertyVetoException propertyVetoException) {
                        Exceptions.printStackTrace(propertyVetoException);
                    }
                }
            }
        });
    }

    /**
     * Called by the SourceTask or NavigatorPanel when the ParserResult or the
     * caret position has changed.
     *
     * @param result
     * @param offset
     */
    public void setParserResult(HtmlParserResult result) {
        FileObject file = result.getSnapshot().getSource().getFileObject();
        sourceDescription = new HtmlElementDescription(null, result.root(), file);
        if(!file.equals(getInspectedFileObject())) {
            //the file is not inspected, apply changes
            refresh(sourceDescription);
        } else {
            setSynchronizationState(false);
        }
    }
    
    private void setSynchronizationState(boolean insynch) {
        if(!insynch) {
            setStatusText("Source changed");
            stateLabel.setForeground(Color.red.darker());
            stateLabel.setText("Unsynchronized");
        } else {
            stateLabel.setForeground(Color.black); //todo fix the hardcoded color
            updateInspectedFileUI();
        }
    }

    private void refresh(final HtmlElementDescription description) {
        final FileObject fileObject = description.getFileObject();
        final HtmlElementNode rootNode = getRootNode();

        if (rootNode != null && rootNode.getFileObject().equals(fileObject)) {
            //same file, just update the content
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    rootNode.setDescription(description);
                    long endTime = System.currentTimeMillis();
                    Logger.getLogger("TIMER").log(Level.FINE, "Navigator Merge",
                            new Object[]{fileObject, endTime - startTime});

                    LOGGER.info("refresh() - same file, descriptions updated");
                }
            };
            RP.post(r);
        } else {
            // new fileobject => refresh completely
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    view.setRootVisible(false);
                    view.setAutoWaitCursor(false);
                    
                    HtmlElementNode root = new HtmlElementNode(description, HtmlNavigatorPanelUI.this, fileObject);
                    manager.setRootContext(root);
                    
                    LOGGER.info("refresh() - new file, set new explorer root node");

                    int expandDepth = -1;

                    // impl hack: Node expansion is synced by VisualizerNode to the AWT thread, possibly delayed
                    expandNodeByDefaultRecursively(manager.getRootContext(), 0, expandDepth);
                    // set soe back only after all pending expansion events are processed:
//                    Mutex.EVENT.writeAccess(new Runnable() {
//                        @Override
//                        public void run() {
//                            view.setScrollOnExpand( scrollOnExpand );
//                        }
//                    });
                    view.setAutoWaitCursor(true);
                }
            };
            RP.post(r);
        }

    }

    public void setCaretOffset(final int offset) {
        if (offset != -1) {
            RP.post(new Runnable() {
                @Override
                public void run() {
                    selectElementNode(offset);
                }
            });
        }
    }

    void performExpansion(final Collection<Node> expand, final Collection<Node> expandRec) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (Node n : expand) {
                    expandNode(n);
                }

                for (Node n : expandRec) {
                    expandNodeByDefaultRecursively(n);
                }
            }
        };

        RP.post(r);
    }

    public void expandNode(Node n) {
        view.expandNode(n);
    }

    private void expandNodeByDefaultRecursively(Node node) {
        // using 0, -1 since we cannot quickly resolve currentDepth
        expandNodeByDefaultRecursively(node, 0, -1);
    }

    private void expandNodeByDefaultRecursively(Node node, int currentDepth, int maxDepth) {
        if (maxDepth >= 0 && currentDepth >= maxDepth) {
            return;
        }
        expandNode(node);
        for (Node subNode : node.getChildren().getNodes()) {
            expandNodeByDefaultRecursively(subNode, currentDepth + 1, maxDepth);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    public Lookup getLookup() {
        return lookup;
    }

    private Project getCurrentProject() {
        if (inspectedFileObject!=null) {
            return FileOwnerQuery.getOwner(inspectedFileObject);
        }
        if (sourceDescription!=null) {
            return FileOwnerQuery.getOwner(sourceDescription.getFileObject());
        }
        Node rootContext = manager.getRootContext();
        FileObject fo = null;
        if (rootContext instanceof HtmlElementNode) {
            fo = ((HtmlElementNode) rootContext).getFileObject();
        } else {
            LOGGER.log(Level.WARNING, "Root context is not HtmlElementNode");
        }
        Project owner = FileOwnerQuery.getOwner(fo);
        return owner;
    }

    private static class WaitNode extends AbstractNode {

        private Image waitIcon = ImageUtilities.loadImage("org/netbeans/modules/html/navigator/resources/wait.png"); // NOI18N
        private String displayName;

        @NbBundle.Messages("lbl.wait.node=Please wait...")
        WaitNode() {
            super(Children.LEAF);
            displayName = Bundle.lbl_wait_node();
        }

        @Override
        public Image getIcon(int type) {
            return waitIcon;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @java.lang.Override
        public java.lang.String getDisplayName() {
            return displayName;
        }
    }
}
