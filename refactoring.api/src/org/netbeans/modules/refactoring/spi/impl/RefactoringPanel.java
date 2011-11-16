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
package org.netbeans.modules.refactoring.spi.impl;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Collection;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.*;
import org.netbeans.modules.refactoring.api.*;
import org.netbeans.modules.refactoring.spi.ui.RefactoringCustomUI;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.netbeans.modules.refactoring.spi.ui.TreeElement;
import org.netbeans.modules.refactoring.spi.ui.TreeElementFactory;
import org.netbeans.modules.refactoring.spi.ui.TreeElementFactoryImplementation;
import org.netbeans.modules.refactoring.spi.ui.UI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.awt.Mnemonics;
import org.openide.awt.StatusDisplayer;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.PositionBounds;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Panel for showing proposed changes (refactoring elements) of any refactoring.
 *
 * @author  Pavel Flaska, Martin Matula
 */
public class RefactoringPanel extends JPanel implements InvalidationListener {
    private static final RequestProcessor RP = new RequestProcessor(RefactoringPanel.class.getName(), 1, false, false);
    
    // PRIVATE FIELDS
    /* tree contains elements which will be changed by refactoring action */
    private transient JTree tree = null;
    /* toolbar button causing refresh of the data */
    private transient JButton refreshButton = null;
    /* button lying in the toolbar allows expansion of all nodes in a tree */
    private transient JToggleButton expandButton = null;
    private JToolBar toolBar = null;

    private transient JButton refactorButton = null;
    private transient JButton cancelButton = null;
    private transient ButtonL buttonListener = null;
    private transient JButton rerunButton = null;

    private final RefactoringUI ui;
    private final boolean isQuery;
    
    private transient boolean isVisible = false;
    private transient RefactoringSession session = null;
    private transient ParametersPanel parametersPanel = null;
    private transient JScrollPane scrollPane = null; 
    private transient JPanel southPanel;
    public JSplitPane splitPane;
    private JPanel left;
    private Action callback = null;
    
    private static final int MAX_ROWS = 50;
    
    private transient JToggleButton logicalViewButton = null;
    private transient JToggleButton physicalViewButton = null;
    private transient JToggleButton customViewButton = null;
    private transient ProgressListener progressListener;

    private transient JButton prevMatch = null;
    private transient JButton nextMatch = null;
    private WeakReference<TopComponent> refCallerTC;
    private boolean inited = false;
    private Component customComponent;

    
    static Image PACKAGE_BADGE = ImageUtilities.loadImage( "org/netbeans/spi/java/project/support/ui/packageBadge.gif" ); // NOI18N
    
    public RefactoringPanel(RefactoringUI ui) {
        this(ui,null);
    }
    
    public RefactoringPanel(RefactoringUI ui, TopComponent caller) {
        if (caller!=null)
            refCallerTC = new WeakReference<TopComponent>(caller);
        this.ui = ui;
        this.isQuery = ui.isQuery();
        refresh(true);
    }
    
    public RefactoringPanel(RefactoringUI ui, RefactoringSession session, Action callback) {
        this.session = session;
        this.ui = ui;
        this.isQuery = ui.isQuery();
        this.callback = callback;
        initialize();
        refresh(false);
    }
    
    public static void checkEventThread() {
        if (!SwingUtilities.isEventDispatchThread()) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalStateException("This must happen in event thread!")); //NOI18N
        }
    }
    
    /* initializes all the ui */
    private void initialize() {
        if (inited)
            return ;
        checkEventThread();
        setFocusCycleRoot(true);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        left = new JPanel();
        splitPane.setLeftComponent(left);
        left.setLayout(new BorderLayout());
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        if (!isQuery) {
            splitPane.setRightComponent(new JLabel(org.openide.util.NbBundle.getMessage(RefactoringPanel.class, "LBL_Preview_not_Available"), SwingConstants.CENTER));
        }
        // add panel with buttons
        JButton[] buttons = getButtons();
        //if (buttons.length != 0) {
            // there will be at least one button on panel
            southPanel = new JPanel(new GridBagLayout());
            for (int i = 0; i < buttons.length; i++) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                c.insets = new Insets(5, 5, 5, 0);
                southPanel.add(buttons[i], c);
            }
            JPanel pp = new JPanel(new BorderLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            southPanel.add(pp, c);
            
            if (!isQuery|| callback != null) {
                left.add(southPanel, BorderLayout.SOUTH);
            }
        //}
        // put the toolbar to the panel. If the getToolBar() returns null,
        // suppose the toolbar does not exist.
        JToolBar toolBar = getToolBar();
        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) { //NOI18N
            toolBar.setBackground(UIManager.getColor("NbExplorerView.background"));
            southPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
        }
        if (toolBar != null)
            left.add(toolBar, BorderLayout.WEST);
        validate();
        inited=true;
    }

    /**
     * Returns the toolbar. In this default implementation, toolbar is
     * oriented vertically in the west and contains 'expand tree' toggle
     * button and refresh button.
     * Override this method and return null if you do not want toolbar
     * in your panel.
     * 
     * @return  toolBar with actions for refactoring panel
     */
    private JToolBar getToolBar() {
        checkEventThread();
        refreshButton = new JButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/refresh.png", false));
        Dimension dim = new Dimension(24, 24);
        refreshButton.setMaximumSize(dim);
        refreshButton.setMinimumSize(dim);
        refreshButton.setPreferredSize(dim);
        refreshButton.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_refresh") // NOI18N
        );
        refreshButton.addActionListener(getButtonListener());
        // expand button settings
        expandButton = new JToggleButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/expandTree.png", false));
        expandButton.setSelectedIcon(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/colapseTree.png", false));
        expandButton.setMaximumSize(dim);
        expandButton.setMinimumSize(dim);
        expandButton.setPreferredSize(dim);
        expandButton.setSelected(true);
        expandButton.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_expandAll") // NOI18N
        );
        expandButton.addActionListener(getButtonListener());
        // create toolbar
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false); 
        
        logicalViewButton = new JToggleButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/logical_view.png", false));
        
        logicalViewButton.setMaximumSize(dim);
        logicalViewButton.setMinimumSize(dim);
        logicalViewButton.setPreferredSize(dim);
        logicalViewButton.setSelected(currentView==LOGICAL);
        logicalViewButton.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_logicalView") // NOI18N
        );
        logicalViewButton.addActionListener(getButtonListener());

        physicalViewButton = new JToggleButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/file_view.png", false));
        
        physicalViewButton.setMaximumSize(dim);
        physicalViewButton.setMinimumSize(dim);
        physicalViewButton.setPreferredSize(dim);
        physicalViewButton.setSelected(currentView==PHYSICAL);
        physicalViewButton.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_physicalView") // NOI18N
        );
        physicalViewButton.addActionListener(getButtonListener());

        if (!Utilities.isMac()) {
            refreshButton.setMnemonic(
                    NbBundle.getMessage(RefactoringPanel.class, "MNEM_refresh").charAt(0));

            expandButton.setMnemonic(
                    NbBundle.getMessage(RefactoringPanel.class, "MNEM_expandAll").charAt(0) // NOI18N
                    );

            logicalViewButton.setMnemonic(
                    NbBundle.getMessage(RefactoringPanel.class, "MNEM_logicalView").charAt(0) // NOI18N
                    );
            physicalViewButton.setMnemonic(
                    NbBundle.getMessage(RefactoringPanel.class, "MNEM_physicalView").charAt(0) // NOI18N
                    );
        }

        if (ui instanceof RefactoringCustomUI) {
            customViewButton = new JToggleButton(((RefactoringCustomUI)ui).getCustomIcon());
            customViewButton.setMaximumSize(dim);
            customViewButton.setMinimumSize(dim);
            customViewButton.setPreferredSize(dim);
            customViewButton.setSelected(currentView==GRAPHICAL);
            customViewButton.setToolTipText(((RefactoringCustomUI)ui).getCustomToolTip());
            customViewButton.addActionListener(getButtonListener());
        }
        
        nextMatch = new JButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/nextmatch.png", false));
        
        nextMatch.setMaximumSize(dim);
        nextMatch.setMinimumSize(dim);
        nextMatch.setPreferredSize(dim);
        nextMatch.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_nextMatch") // NOI18N
        );
        nextMatch.addActionListener(getButtonListener());

        prevMatch = new JButton(
            ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/prevmatch.png", false));
        
        prevMatch.setMaximumSize(dim);
        prevMatch.setMinimumSize(dim);
        prevMatch.setPreferredSize(dim);
        prevMatch.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_prevMatch") // NOI18N
        );
        prevMatch.addActionListener(getButtonListener());
        
        toolBar.add(refreshButton);
        toolBar.add(expandButton);
        toolBar.add(logicalViewButton);
        toolBar.add(physicalViewButton);
        if (ui instanceof RefactoringCustomUI) {
            toolBar.add(customViewButton);
        }
        toolBar.add(prevMatch);
        toolBar.add(nextMatch);
        
        return toolBar;
    }
    
    /**
     * Returns array of available buttons. Initially, it returns only
     * basic "do refactoring/cancel refactoring" button. Override this method, 
     * if you want to provide any other buttons with different action to be 
     * performed.
     *
     * @return  array of avasilable buttons.
     */
    private JButton[] getButtons() {
        checkEventThread();
        if (isQuery) {
            refactorButton = null;
            if (callback==null) {
                return new JButton[] {};
            } else {
                rerunButton = new JButton((String) callback.getValue(callback.NAME)); // NOI18N
                rerunButton.addActionListener(getButtonListener());
                return new JButton[] {rerunButton};
            }
        } else {
            refactorButton = new JButton(); // NOI18N
            Mnemonics.setLocalizedText(refactorButton, NbBundle.getMessage(RefactoringPanel.class, "LBL_DoRefactor"));
            refactorButton.setToolTipText(NbBundle.getMessage(RefactoringPanel.class, "HINT_DoRefactor")); // NOI18N
            refactorButton.addActionListener(getButtonListener());
            cancelButton = new JButton(NbBundle.getMessage(RefactoringPanel.class, "LBL_CancelRefactor")); // NOI18N
            Mnemonics.setLocalizedText(cancelButton, NbBundle.getMessage(RefactoringPanel.class, "LBL_CancelRefactor"));
            cancelButton.setToolTipText(NbBundle.getMessage(RefactoringPanel.class, "HINT_CancelRefactor")); // NOI18N
            cancelButton.addActionListener(getButtonListener());
            return new JButton[] {refactorButton, cancelButton};
        }
    }
    
    private static final byte LOGICAL = 0;
    private static final byte PHYSICAL = 1;
    private static final byte GRAPHICAL = 2;
    
    private static final String PREF_VIEW_TYPE = "PREF_VIEW_TYPE";
    private byte currentView = getPrefViewType();

    void switchToLogicalView() {
        logicalViewButton.setSelected(true);
        if (currentView == LOGICAL)
            return ;
        currentView = LOGICAL;
        physicalViewButton.setSelected(false);
        if (customViewButton!=null) {
            customViewButton.setSelected(false);
            prevMatch.setEnabled(true);
            nextMatch.setEnabled(true);
            expandButton.setEnabled(true);
        }
        storePrefViewType();
        refresh(false);
    }
    
    void switchToPhysicalView() {
        physicalViewButton.setSelected(true);
        if (currentView == PHYSICAL)
            return ;
        currentView = PHYSICAL;
        logicalViewButton.setSelected(false);
        if (customViewButton!=null) {
            customViewButton.setSelected(false);
            prevMatch.setEnabled(true);
            nextMatch.setEnabled(true);
            expandButton.setEnabled(true);
        }
        storePrefViewType();
        refresh(false);
    }

    void switchToCustomView() {
        customViewButton.setSelected(true);
        if (currentView == GRAPHICAL)
            return ;
        currentView = GRAPHICAL;
        logicalViewButton.setSelected(false);
        physicalViewButton.setSelected(false);
        prevMatch.setEnabled(false);
        nextMatch.setEnabled(false);
        expandButton.setEnabled(false);
        refresh(false);
    }
    
    private CheckNode createNode(TreeElement representedObject, Map<Object, CheckNode> nodes, CheckNode root) {
        //checkEventThread();
        boolean isLogical = currentView == LOGICAL;
        
        CheckNode node = null;
        if (representedObject instanceof SourceGroup) {
            //workaround for issue 52541
            node = nodes.get(((SourceGroup) representedObject).getRootFolder());
        } else {
            node = nodes.get(representedObject);
        }
        if (node != null) {
            return node;
        }
        
        TreeElement parent = representedObject.getParent(isLogical);
        String displayName = representedObject.getText(isLogical);
        Icon icon = representedObject.getIcon();
        
        node = new CheckNode(representedObject, displayName, icon);
        CheckNode parentNode = parent == null ? root : createNode(parent, nodes, root);
        parentNode.add(node);
        
        if (representedObject instanceof SourceGroup) {
            //workaround for issue 52541
            nodes.put(((SourceGroup) representedObject).getRootFolder(), node);
        } else {
            nodes.put(representedObject, node);
        }
        return node;
    }
    
    private static final String getString(String key) {
        return NbBundle.getMessage(RefactoringPanel.class, key);
    }

    /**
     * Overrides default ExplorerPanel behaviour. Does nothing now.
     */
    protected void updateTitle() {
    }
    
    /**
     * Method is responsible for making changes in sources.
     */
    private void refactor() {
        checkEventThread();
        disableComponents(RefactoringPanel.this);
        progressListener = new ProgressL();
        RP.post(new Runnable() {
            public void run() {
                try {
                    session.addProgressListener(progressListener);
                    session.doRefactoring(true);
                } finally {
                    session.removeProgressListener(progressListener);
                    progressListener.stop(null);
                    progressListener = null;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            RefactoringPanel.this.close();
                        }
                    });
                }
            }
        });
    }

    /**
     * Cancel refactor action. This default implementation is closing window
     * only. It can return result state. In this implementation it returns
     * everytime 0.
     *
     * @return  result of cancel operation. Zero represent successful cancel.
     */
    private int cancel() {
        checkEventThread();
        this.close();
        return 0;
    }
    
    void close() {
        if (isQuery) {
            RefactoringPanelContainer.getUsagesComponent().removePanel(this);
        } else {
            RefactoringPanelContainer.getRefactoringComponent().removePanel(this);
        }
        closeNotify();
    }
    
    
    /*
     * Initializes button listener. The subclasses must not need this listener.
     * This is the reason of lazy initialization.
     */
    private ButtonL getButtonListener() {
        if (buttonListener == null)
            buttonListener = new ButtonL();
        
        return buttonListener;
    }

    RequestProcessor rp = new RequestProcessor();

    /* expandAll nodes in the tree */
    public void expandAll() { 
        checkEventThread();
        final Cursor old = getCursor();
        expandButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }

        setCursor(old);
        expandButton.setEnabled(true);
        expandButton.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_collapseAll") // NOI18N
        );
        requestFocus();
    } 

    /* collapseAll nodes in the tree */
    public void collapseAll() {
        checkEventThread();
        expandButton.setEnabled(false);
        final Cursor old = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int row = tree.getRowCount() - 1;
        while (row > 0) {
            tree.collapseRow(row);
            row--;
        }

        setCursor(old);
        expandButton.setEnabled(true);
        expandButton.setToolTipText(
            NbBundle.getMessage(RefactoringPanel.class, "HINT_expandAll") // NOI18N
        );
        requestFocus();
    }
    
    public void invalidateObject() {
        if (isQuery) {
            return;
        }
        Runnable invalidate = new Runnable() {
            public void run() {
                setRefactoringEnabled(false, false);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            invalidate.run();
        } else {
            SwingUtilities.invokeLater(invalidate);
        }
    }

    private void refresh(final boolean showParametersPanel) {
        checkEventThread();

        if (showParametersPanel) {
            // create parameters panel for refactoring
            if (parametersPanel == null) {
                parametersPanel = new ParametersPanel(ui);
            }
            // show parameters dialog
            RefactoringSession tempSession = parametersPanel.showDialog();
            // if no elements were returned, action was either cancelled or preview
            // was skipped -> finish
            if (tempSession == null) {
                if (!parametersPanel.isCanceledDialog()) {
                    // close tab in case the refactoring is bypassed but it has been open before
                    close();
                }
                return;
            } else if (tempSession.getRefactoringElements().isEmpty()) {
                DialogDescriptor nd = new DialogDescriptor(NbBundle.getMessage(ParametersPanel.class, "MSG_NoPatternsFound"),
                                        ui.getName(),
                                        true,
                                        new Object[] {DialogDescriptor.OK_OPTION},
                                        DialogDescriptor.OK_OPTION,
                                        DialogDescriptor.DEFAULT_ALIGN,
                                        ui.getHelpCtx(),
                                        null);
                                DialogDisplayer.getDefault().notifyLater(nd);
                return;
            }
            
            session = tempSession;
        }
        
        initialize();

        final String description = ui.getDescription();
        setToolTipText("<html>" + description + "</html>"); // NOI18N
        final Collection<RefactoringElement> elements = session.getRefactoringElements();
        setName(ui.getName());
        if (ui instanceof RefactoringCustomUI) {
            if (customComponent==null)
                customComponent = ((RefactoringCustomUI) ui).getCustomComponent(elements);
            this.left.remove(customComponent);
        }
        final ProgressHandle progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(RefactoringPanel.class, isQuery ? "LBL_PreparingUsagesTree":"LBL_PreparingRefactoringTree"));
        if (currentView == GRAPHICAL) {
            assert ui instanceof RefactoringCustomUI;
            assert customComponent != null;
            RefactoringCustomUI cui = (RefactoringCustomUI) ui;
            this.left.remove(scrollPane);
            this.left.add(customComponent, BorderLayout.CENTER);
            UI.setComponentForRefactoringPreview(null);
            this.splitPane.validate();
            this.repaint();
            tree=null;
        } else {
            RP.post(new Runnable() {
                public void run() {
                    Set<CloneableEditorSupport> editorSupports = new HashSet<CloneableEditorSupport>();
                    int errorsNum = 0;
                    if (!isQuery) {
                        for (Iterator iter = elements.iterator(); iter.hasNext(); ) {
                            RefactoringElement elem = (RefactoringElement) iter.next();
                            if (elem.getStatus() == RefactoringElement.GUARDED || elem.getStatus() == RefactoringElement.READ_ONLY) {
                                errorsNum++;
                            }
                        }
                    }
                    int occurencesNum = elements.size();
                    StringBuffer errorsDesc = new StringBuffer();
                    errorsDesc.append(" [" + occurencesNum); // NOI18N
                    errorsDesc.append(' ');
                    errorsDesc.append(occurencesNum == 1 ?
                        NbBundle.getMessage(RefactoringPanel.class, "LBL_Occurence") :
                        NbBundle.getMessage(RefactoringPanel.class, "LBL_Occurences")
                        );
                    if (errorsNum > 0) {
                        errorsDesc.append(',');
                        errorsDesc.append(' ');
                        errorsDesc.append("<font color=#CC0000>" + errorsNum); // NOI18N
                        errorsDesc.append(' ');
                        errorsDesc.append(errorsNum == 1 ?
                            NbBundle.getMessage(RefactoringPanel.class, "LBL_Error") :
                            NbBundle.getMessage(RefactoringPanel.class, "LBL_Errors")
                            );
                        errorsDesc.append("</font>"); // NOI18N
                    }
                    errorsDesc.append(']');
                    final CheckNode root = new CheckNode(ui, description + errorsDesc.toString(),ImageUtilities.loadImageIcon("org/netbeans/modules/refactoring/api/resources/" + (isQuery ? "findusages.png" : "refactoring.gif"), false));
                    Map<Object, CheckNode> nodes = new HashMap<Object, CheckNode>();
                    
                    final Cursor old = getCursor();
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    progressHandle.start(elements.size()/10);
                    int i=0;
                    try {
                        //[retouche]                    JavaModel.getJavaRepository().beginTrans(false);
                        try {
                            // ui.getRefactoring().setClassPath();
                            for (Iterator it = elements.iterator(); it.hasNext();i++) {
                                RefactoringElement e = (RefactoringElement) it.next();
                                createNode(TreeElementFactory.getTreeElement(e), nodes, root);
                                PositionBounds pb = e.getPosition();
                                if (pb != null) {
                                    CloneableEditorSupport ces = pb.getBegin().getCloneableEditorSupport();
                                    editorSupports.add(ces);
                                }
                                
                                if (i % 10 == 0)
                                    progressHandle.progress(i/10);
                            }
                        } finally {
                            //[retouche]                        JavaModel.getJavaRepository().endTrans();
                        }
                        UndoManager.getDefault().watch(editorSupports, RefactoringPanel.this);
                        sortTree(root);
                    } catch (RuntimeException t) {
                        cleanupTreeElements();
                        throw t;
                    } catch (Error e) {
                        cleanupTreeElements();
                        throw e;
                    } finally {
                        progressHandle.finish();
                        setCursor(old);
                    }
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (tree == null) {
                                // add panel with appropriate content
                                tree = new JTree(root);
                                if ("Aqua".equals(UIManager.getLookAndFeel().getID())) { //NOI18N
                                    tree.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
                                }
                                ToolTipManager.sharedInstance().registerComponent(tree);
                                tree.setCellRenderer(new CheckRenderer(isQuery, tree.getBackground()));
                                String s = NbBundle.getMessage(RefactoringPanel.class, "ACSD_usagesTree"); // NOI18N
                                tree.getAccessibleContext().setAccessibleDescription(s);
                                tree.getAccessibleContext().setAccessibleName(s);
                                CheckNodeListener l = new CheckNodeListener(isQuery);
                                tree.addMouseListener(l);
                                tree.addKeyListener(l);
                                tree.setToggleClickCount(0);
                                tree.setTransferHandler(new TransferHandlerImpl());
                                scrollPane = new JScrollPane(tree);
                                    scrollPane.setBorder(new EmptyBorder(0,0,0,0));
                                RefactoringPanel.this.left.add(scrollPane, BorderLayout.CENTER);
                                RefactoringPanel.this.validate();
                            } else {
                                tree.setModel(new DefaultTreeModel(root));
                            }
                            tree.setRowHeight((int) ((CheckRenderer) tree.getCellRenderer()).getPreferredSize().getHeight());
                            
                            if (showParametersPanel) {
                                splitPane.setDividerLocation(0.3);
                                if (elements.size() < MAX_ROWS) {
                                    expandAll();
                                    if (!isQuery)
                                        selectNextUsage();
                                } else
                                    expandButton.setSelected(false);
                            } else {
                                if (expandButton.isSelected()) {
                                    expandAll();
                                    if (!isQuery)
                                        selectNextUsage();
                                } else
                                    expandButton.setSelected(false);
                            }
                            
                            tree.setSelectionRow(0);
                            requestFocus();
                            setRefactoringEnabled(true, true);
                            if (parametersPanel!=null && (Boolean) parametersPanel.getClientProperty(ParametersPanel.JUMP_TO_FIRST_OCCURENCE)) {
                                selectNextUsage();
                            }
                        }
                    });
                }
            });
        }
        if (!isVisible) {
            // dock it into output window area and display
            RefactoringPanelContainer cont = isQuery ? RefactoringPanelContainer.getUsagesComponent() : RefactoringPanelContainer.getRefactoringComponent();
            cont.open();
            cont.requestActive();
            if (isQuery && parametersPanel!=null && !parametersPanel.isCreateNewTab()) {
                cont.removePanel(null);
            }
            cont.addPanel(this);
            isVisible = true;
        }
        setRefactoringEnabled(false, true);
    }
    
    private void sortTree(CheckNode root) {
        ArrayList<CheckNode> nodes = new ArrayList<CheckNode>();
        ArrayList<CheckNode> leaves = new ArrayList<CheckNode>();
        for (int i = 0; i < root.getChildCount(); i++){
                CheckNode node = (CheckNode) root.getChildAt(i);
                if(!node.isLeaf()) {
                        sortTree(node);
                        nodes.add(node);
                }
        }
        for (int i = 0; i < root.getChildCount(); i++){
            CheckNode node = (CheckNode) root.getChildAt(i);
            if (node.isLeaf()) {
                leaves.add(node);
            }
        }
        Collections.sort(nodes, new Comparator<CheckNode>() {
            public int compare(CheckNode o1, CheckNode o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        root.removeAllChildren();
        for (CheckNode checkNode : nodes) {
            root.add(checkNode);
        }
        for (CheckNode checkNode : leaves) {
            root.add(checkNode);
        }
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        if (refactorButton != null) {
            refactorButton.requestFocus();
        } else {
            if (tree!=null)
                tree.requestFocus();
        }
    }
    
    void setRefactoringEnabled(boolean enabled, boolean isRefreshing) {
        checkEventThread();
        if (tree != null) {
            if (!enabled) {
                CheckNode c = (CheckNode) tree.getModel().getRoot();
                if (!isRefreshing) {
                    c.setNeedsRefresh();
                } else {
                    c.setDisabled();
                }
                tree.setModel(new DefaultTreeModel(c, false));
            }
//            tree.validate();
            tree.setEnabled(enabled);
            if (refactorButton != null) {
                refactorButton.setEnabled(enabled);
            }
        }
    }

    // disables all components in a given container
    private static void disableComponents(Container c) {
        checkEventThread();
        Component children[] = c.getComponents();
        for (int i = 0; i < children.length; i++) {
            if (children[i].isEnabled()) {
                children[i].setEnabled(false);
            }
            if (children[i] instanceof Container) {
                disableComponents((Container) children[i]);
            }
        }
    }
    
    void selectNextUsage() {
        CheckNodeListener.selectNextPrev(true, isQuery, tree);
    }
    
    void selectPrevUsage() {
        CheckNodeListener.selectNextPrev(false, isQuery, tree);
    }
    
    private int location;
    public void storeDividerLocation() {
        if (splitPane.getRightComponent()!=null)
            location = splitPane.getDividerLocation();
    }
    
    public void restoreDeviderLocation() {
        if (splitPane.getRightComponent()!=null)
            splitPane.setDividerLocation(location);
    }

    private byte getPrefViewType() {
        Preferences prefs = NbPreferences.forModule(RefactoringPanel.class);
        return (byte) prefs.getInt(PREF_VIEW_TYPE, PHYSICAL);
    }

    private void storePrefViewType() {
        assert currentView!=GRAPHICAL;
        Preferences prefs = NbPreferences.forModule(RefactoringPanel.class);
        prefs.putInt(PREF_VIEW_TYPE, currentView);
    }

    ////////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    ////////////////////////////////////////////////////////////////////////////

    private class ButtonL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Object o = event.getSource();
            // Cancel button pressed, remove refactoring panel
            if (o == cancelButton) {
                cancel();
            } else if (o == refactorButton) {
                refactor();
            } else if (o == rerunButton) {
                close();
                callback.actionPerformed(event);
            }
            // expandAll button selected/deselected
            else if (o == expandButton && tree != null) {
                if (expandButton.isSelected())
                    expandAll();
                else
                    collapseAll();
            } else if (o == refreshButton) {
                if (callback!=null) {
                    close();
                    callback.actionPerformed(event);
                } else {
                    refresh(true);
                }
            } else if (o == physicalViewButton) {
                switchToPhysicalView();
            } else if (o == logicalViewButton) {
                switchToLogicalView();
            } else if (o == customViewButton) {
                switchToCustomView();
            } else if (o == nextMatch) {
                selectNextUsage();
            } else if (o == prevMatch) {
                selectPrevUsage();
            }
        }
    } // end ButtonL
    ////////////////////////////////////////////////////////////////////////////

    private static String normalize(String input) {
        int size = input.length();
        char[] c = new char[size];
        input.getChars(0, size, c, 0);
        boolean wb = false;
        int pos = 0;
        char[] nc = new char[size];
        
        for (int i = 0; i < size; i++) {
            if (Character.isWhitespace(c[i])) {
                if (!wb) {
                    nc[pos++] = ' ';

                    wb = true;
                }
            }
            else {
                nc[pos++] = c[i];
                wb = false;
            }
        }
        return new String(nc, 0, pos);
    }

    /** Processes returned problems from refactoring operations and notifies
     * user (in case of non-fatal problems gives user a chance to continue or cancel).
     * @param problem Problems returned from a refactoring operation.
     * @return <code>true</code> if no fatal problems were found and user decided
     * to continue in case of non-fatal problems; <code>false</code> if there was at
     * least one fatal problem or at least one non-fatal problem in response to which
     * user decided to cancel the operation.
     */
    /* public static boolean confirmProblems(Problem problem) {
        while (problem != null) {
            int result;
            if (problem.isFatal()) {
                JOptionPane.showMessageDialog(null, problem.getMessage(), NbBundle.getMessage(ParametersPanel.class, "LBL_Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                if (JOptionPane.showConfirmDialog(
                    null, 
                    problem.getMessage() + ' ' + NbBundle.getMessage(ParametersPanel.class, "QST_Continue"),
                    NbBundle.getMessage(ParametersPanel.class, "LBL_Warning"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                ) != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            problem = problem.getNext();
        }
        return true;
    } */
    
    protected void closeNotify() {
        UndoWatcher.stopWatching(this);
        if (tree!=null) {
            ToolTipManager.sharedInstance().unregisterComponent(tree);
            scrollPane.getViewport().remove(tree);
        }
        if (scrollPane!=null)
            scrollPane.setViewport(null);
        if (refCallerTC != null) {
            TopComponent tc = refCallerTC.get();
            if (tc != null && tc.isShowing()) {
                tc.requestActive();
            }
        }
        cleanupTreeElements();
        PreviewManager.getDefault().clean(this);
        tree = null;
        session =null;
        parametersPanel = null;
        //super.closeNotify();
    }
    
    private void cleanupTreeElements() {
        for (TreeElementFactoryImplementation tefi: Lookup.getDefault().lookupAll(TreeElementFactoryImplementation.class)) {
            tefi.cleanUp();
        }
    }

    private static class ProgressL implements ProgressListener {
        
        private ProgressHandle handle;
        private Dialog d;
        private int counter;
        
        public void start(final ProgressEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    counter = 0;
                    final String lab = NbBundle.getMessage(RefactoringPanel.class, "LBL_RefactorProgressLabel");
                    handle = ProgressHandleFactory.createHandle(lab);
                    JComponent progress = ProgressHandleFactory.createProgressComponent(handle);
                    JPanel component = new JPanel();
                    component.setLayout(new BorderLayout());
                    component.setBorder(new EmptyBorder(12,12,11,11));
                    JLabel label = new JLabel(lab);
                    label.setBorder(new EmptyBorder(0, 0, 6, 0));
                    component.add(label, BorderLayout.NORTH);
                    component.add(progress, BorderLayout.CENTER);
                    DialogDescriptor desc = new DialogDescriptor(component, NbBundle.getMessage(RefactoringPanel.class, "LBL_RefactoringInProgress"), true, new Object[]{}, null, 0, null, null);
                    desc.setLeaf(true);
                    d = DialogDisplayer.getDefault().createDialog(desc);
                    ((JDialog) d).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    
                    handle.start(event.getCount());
                    d.setVisible(true);
                }
            });
        }
        
        public void step(ProgressEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        handle.progress(++counter);
                    } catch (Throwable e) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }
                }
            });
        }
        
        public void stop(final ProgressEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (event!=null) {
                        handle.finish();
                    }
                    d.setVisible(false);
                }
            });
        }
    }

    private static class TransferHandlerImpl extends TransferHandler {
        @Override
        protected Transferable createTransferable(JComponent c) {
            if (c instanceof JTree) {
                JTree tree = (JTree) c;
                TreePath[] paths = tree.getSelectionPaths();

                if (paths == null || paths.length == 0) {
                    return null;
                }

                Html2Text html2Text = new Html2Text();
                StringBuilder plain = new StringBuilder();
                StringBuilder html = new StringBuilder("<html><ul>"); // NOI18N
                int depth = 1;
                for(TreePath path: paths) {
                    for(; depth < path.getPathCount(); depth++) {
                        html.append("<ul>"); // NOI18N
                    }
                    for(; depth > path.getPathCount(); depth--) {
                        html.append("</ul>"); // NOI18N
                    }
                    Object o = path.getLastPathComponent();
                    if(o instanceof CheckNode) {
                        CheckNode node = (CheckNode) o;
                        String label = node.getLabel();
                        try {
                            html2Text.parse(new StringReader(label));
                        } catch (IOException ex) {
                            assert false : ex;
                        }

                        plain.append(html2Text.getText());
                        plain.append("\n"); // NOI18N
                        html.append("<li>"); // NOI18N
                        html.append(label);
                        html.append("</li>"); // NOI18N
                    }
                }
                for(; depth > 1; depth--) {
                        html.append("</ul>"); // NOI18N
                }
                html.append("</ul></html>"); // NOI18N

                return new ResultTransferable(plain.toString(), html.toString());
            }
            return null;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }
    /**
     * Transferable implementation for ResultPanel.
     */
    private static class ResultTransferable implements Transferable {
        private static DataFlavor[] stringFlavors;
        private static DataFlavor[] plainFlavors;
        private static DataFlavor[] htmlFlavors;

        static {
            try {
                htmlFlavors = new DataFlavor[3];
                htmlFlavors[0] = new DataFlavor("text/html;class=java.lang.String"); // NOI18N
                htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader"); // NOI18N
                htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"); // NOI18N

                plainFlavors = new DataFlavor[3];
                plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String"); // NOI18N
                plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader"); // NOI18N
                // XXX isn't this just DataFlavor.plainTextFlavor?
                plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream"); // NOI18N

                stringFlavors = new DataFlavor[2];
                stringFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.String"); // NOI18N
                stringFlavors[1] = DataFlavor.stringFlavor;
            } catch (ClassNotFoundException cle) {
                assert false : cle;
            }
        }

        protected String plainData;
        protected String htmlData;

        public ResultTransferable(String plainData, String htmlData) {
            this.plainData = plainData;
            this.htmlData = htmlData;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            int nHtml = (isHtmlSupported()) ? htmlFlavors.length : 0;
            int nPlain = (isPlainSupported()) ? plainFlavors.length : 0;
            int nString = (isPlainSupported()) ? stringFlavors.length : 0;
            int nFlavors = nHtml + nPlain + nString;
            DataFlavor[] flavors = new DataFlavor[nFlavors];

            // fill in the array
            int nDone = 0;
            if (nHtml > 0) {
                System.arraycopy(htmlFlavors, 0, flavors, nDone, nHtml);
                nDone += nHtml;
            }
            if (nPlain > 0) {
                System.arraycopy(plainFlavors, 0, flavors, nDone, nPlain);
                nDone += nPlain;
            }
            if (nString > 0) {
                System.arraycopy(stringFlavors, 0, flavors, nDone, nString);
                nDone += nString;
            }
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
            if (isHtmlFlavor(flavor)) {
                String html = getHtmlData();
                html = (html == null) ? "" : html;
                if (String.class.equals(flavor.getRepresentationClass())) {
                    return html;
                } else if (Reader.class.equals(flavor.getRepresentationClass())) {
                    return new StringReader(html);
                } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
                    // XXX should this enforce UTF-8 encoding?
                    return new StringBufferInputStream(html);
                }
                // fall through to unsupported
            } else if (isPlainFlavor(flavor)) {
                String data = getPlainData();
                data = (data == null) ? "" : data;
                if (String.class.equals(flavor.getRepresentationClass())) {
                    return data;
                } else if (Reader.class.equals(flavor.getRepresentationClass())) {
                    return new StringReader(data);
                } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
                    // XXX should this enforce UTF-8 encoding?
                    return new StringBufferInputStream(data);
                }
                // fall through to unsupported
            } else if (isStringFlavor(flavor)) {
                String data = getPlainData();
                data = (data == null) ? "" : data;

                return data;
            }

            throw new UnsupportedFlavorException(flavor);
        }

        // --- plain text flavors ----------------------------------------------

        /**
         * Returns whether or not the specified data flavor is an plain flavor
         * that is supported.
         *
         * @param flavor the requested flavor for the data
         * @return boolean indicating whether or not the data flavor is supported
         */
        protected boolean isPlainFlavor(DataFlavor flavor) {
            DataFlavor[] flavors = plainFlavors;

            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Should the plain text flavors be offered?  If so, the method
         * getPlainData should be implemented to provide something reasonable.
         */
        protected boolean isPlainSupported() {
            return plainData != null;
        }

        /**
         * Fetch the data in a text/plain format.
         */
        protected String getPlainData() {
            return plainData;
        }

        // --- string flavors --------------------------------------------------

        /**
         * Returns whether or not the specified data flavor is a String flavor
         * that is supported.
         *
         * @param flavor the requested flavor for the data
         * @return boolean indicating whether or not the data flavor is supported
         */
        protected boolean isStringFlavor(DataFlavor flavor) {
            DataFlavor[] flavors = stringFlavors;

            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

        // --- html flavors ----------------------------------------------------

        /**
         * Returns whether or not the specified data flavor is a html flavor
         * that is supported.
         *
         * @param flavor the requested flavor for the data
         * @return boolean indicating whether or not the data flavor is supported
         */
        protected boolean isHtmlFlavor(DataFlavor flavor) {
            DataFlavor[] flavors = htmlFlavors;

            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Should the html text flavors be offered? If so, the method
         * getHtmlData should be implemented to provide something reasonable.
         */
        protected boolean isHtmlSupported() {
            return htmlData != null;
        }

        /**
         * Fetch the data in text/html format.
         */
        protected String getHtmlData() {
            return htmlData;
        }
    }

    private static class Html2Text extends HTMLEditorKit.ParserCallback {
        StringBuffer s;

        public Html2Text() {
        }

        public void parse(Reader in) throws IOException {
            s = new StringBuffer();
            ParserDelegator delegator = new ParserDelegator();
            // the third parameter is TRUE to ignore charset directive
            delegator.parse(in, this, Boolean.TRUE);
        }

        @Override
        public void handleText(char[] text, int pos) {
            s.append(text);
        }

        public String getText() {
            return s.toString();
        }
    }
} // end Refactor Panel
