/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.languages.features;


import java.awt.Component;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.languages.LanguagesManager;
import org.openide.cookies.EditorCookie;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.loaders.DataObject;


/**
 *
 * @author Jan Jancura
 */
public class LanguagesNavigator implements NavigatorPanel {

    /** holds UI of this panel */
    private JComponent          panelUI;
    private JTree               tree;
    private MyLookupListener    lookupListener;    

    public String getDisplayHint () {
        return "This is Navigator";
    }

    public String getDisplayName () {
        return "Navigator";
    }

    public JComponent getComponent () {
        if (panelUI == null) {
            tree = new JTree () {
                public String getToolTipText (MouseEvent ev) {
                    TreePath path = tree.getPathForLocation 
                        (ev.getX (), ev.getY ());
                    if (path == null) return null;
                    Object node = path.getLastPathComponent ();
                    LanguagesNavigatorModel model = (LanguagesNavigatorModel) tree.getModel ();
                    return model.getTooltip (node);
                }
            };
            ToolTipManager.sharedInstance ().registerComponent (tree);
            tree.setRootVisible (false);
            tree.setShowsRootHandles (true);
            Listener listener = new Listener ();
            tree.addMouseListener(listener);
            tree.addKeyListener(listener);
            tree.setToggleClickCount(Integer.MAX_VALUE); // [PENDING]
            tree.setModel (new LanguagesNavigatorModel ());
            tree.setCellRenderer (new Renderer ());
            /*
            tree.addTreeSelectionListener (new TreeSelectionListener () {
                public void valueChanged (TreeSelectionEvent e) {
                    selectionChanged ();
                }
            });
            tree.addFocusListener (new FocusListener () {
                public void focusGained (FocusEvent e) {
                    selectionChanged ();
                }
                public void focusLost (FocusEvent e) {
                    selectionChanged ();
                }
            });
            */
            panelUI = new JScrollPane (tree);
        }
        return panelUI;
    }

    public void panelActivated (Lookup context) {
        getComponent ();
        Result<DataObject> result = context.<DataObject>lookupResult (DataObject.class);
        DataObject dataObject = result.allInstances ().iterator ().next ();
        
        if (lookupListener != null)
            lookupListener.remove ();
        lookupListener = new MyLookupListener (result);
        
        setDataObject (dataObject);
    }

    public void panelDeactivated () {
        if (lastEditor != null)
            lastEditor.removeCaretListener (caretListener);
        lastEditor = null;
    }
    
    public Lookup getLookup () {
        return null;
    }
    
    
    // other methods ...........................................................
    
    private DataObject dataObject;
    
    private void setDataObject (DataObject dataObject) {
        if (this.dataObject == dataObject) return;
        final EditorCookie ec = dataObject.getCookie (EditorCookie.class);    
        if (ec == null) return;
        LanguagesNavigatorModel model = (LanguagesNavigatorModel) tree.getModel ();
        try {
            NbEditorDocument document = (NbEditorDocument) ec.openDocument ();
            String mimeType = (String) document.getProperty ("mimeType");      
            if (mimeType == null) return;
            if (!LanguagesManager.getDefault ().isSupported (mimeType)) return;
            model.setContext (document);
        } catch (IOException ex) {
            model.setContext (null);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                if (ec.getOpenedPanes () != null && 
                    ec.getOpenedPanes ().length > 0
                ) 
                    setCurrentEditor (ec.getOpenedPanes () [0]);
                else
                    setCurrentEditor (null);
            }
        });
    }

    private JEditorPane         lastEditor;
    private MyCaretListener     caretListener;
    
    private void setCurrentEditor (JEditorPane editor) {
        if (caretListener == null)
            caretListener = new MyCaretListener ();
        if (lastEditor != null)
            lastEditor.removeCaretListener (caretListener);
        lastEditor = editor;
        if (lastEditor != null)
            lastEditor.addCaretListener (caretListener);
    }

    // highlight selected node in editor ...
    
//    private Document        highlightedDocument = null;
//    private Object          highlighted = null;
//    private JEditorPane     highlightedEditor = null;
//
//    private void selectionChanged () {
//        removeHighlight ();
//        if (!tree.hasFocus ()) return;
//        TreePath selPath = tree.getSelectionPath ();
//        if (selPath == null) return;
//        Object selObj = selPath.getLastPathComponent ();
//        if (selObj == null || !(selObj instanceof NavigatorNode))
//            return;
//        NavigatorNode node = (NavigatorNode)selObj;
//        if (node.line == null) return;
//        node.line.show (Line.SHOW_SHOW, node.column);
//        //S ystem.out.println ("highlight " + lastDocument + " : " + lastEditor);
//        highlighted = node.item;
//        Highlighting.getHighlighting (highlightedDocument = lastDocument).
//            highlight (node.item, getHighlightAS ());
//        DataObject dataObject = (DataObject) node.line.getLookup ().
//            lookup (DataObject.class);
//        EditorCookie ec = (EditorCookie) dataObject.getCookie 
//            (EditorCookie.class);
//        highlightedEditor = ec.getOpenedPanes () [0];
//        highlightedEditor.repaint ();
//    }
//    
//    private void removeHighlight () {
//        if (highlighted == null) return;
//        if (highlighted instanceof ASTToken)
//            Highlighting.getHighlighting (highlightedDocument).removeHighlight 
//                ((ASTToken) highlighted);
//        else
//            Highlighting.getHighlighting (highlightedDocument).removeHighlight 
//                ((ASTNode) highlighted);
//        highlightedEditor.repaint ();
//        highlighted = null;
//        highlightedDocument = null;
//        highlightedEditor = null;
//    }
//    
//    private static AttributeSet highlightAS = null;
//    
//    private static AttributeSet getHighlightAS () {
//        if (highlightAS == null) {
//            SimpleAttributeSet as = new SimpleAttributeSet ();
//            as.addAttribute (StyleConstants.Background, Color.yellow); //new Color (230, 230, 230));
//            highlightAS = as;
//        }
//        return highlightAS;
//    }
    
    
    // innerclasses ............................................................    
    
    static class Renderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent (
            JTree tree, Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus
        ) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent (
                tree, value, sel, expanded, leaf, row, hasFocus
            );

            if (value instanceof DefaultMutableTreeNode) {
                l.setIcon (null);
                l.setText ((String) ((DefaultMutableTreeNode) value).getUserObject ());
                return l;
            }
            LanguagesNavigatorModel model = (LanguagesNavigatorModel) tree.getModel ();
            l.setIcon (getCIcon (model.getIcon (value)));
            l.setText (model.getDisplayName (value));
            return l;
        }
    
        private static Map<String,Icon> icons = new HashMap<String,Icon> ();

        private static Icon getCIcon (String resourceName) {
            if (resourceName == null) return null;
            if (!icons.containsKey (resourceName)) {
                Image image = Utilities.loadImage (resourceName);
                if (image == null)
                    image = Utilities.loadImage (
                        "org/netbeans/modules/languages/resources/node.gif"
                    );
                icons.put (
                    resourceName,
                    new ImageIcon (image)
                );
            }
            return icons.get (resourceName);
        }
    }
        
    class Listener implements MouseListener, KeyListener {
        
        public void mouseClicked (MouseEvent ev) {
            if (ev.getClickCount () != 2) return;
            TreePath path = tree.getPathForLocation 
                (ev.getX (), ev.getY ());
            if (path == null) return;
            Object node = path.getLastPathComponent ();
            LanguagesNavigatorModel model = (LanguagesNavigatorModel) tree.getModel ();
            model.show (node);
        }
        
        public void mouseEntered (MouseEvent e) {
        }
        public void mouseExited (MouseEvent e) {
        }
        public void mousePressed (MouseEvent e) {
        }
        public void mouseReleased (MouseEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() != 10) return; // ENTER pressed?
            TreePath path = tree.getSelectionPath();
            if (path == null) return;
            Object node = path.getLastPathComponent ();
            LanguagesNavigatorModel model = (LanguagesNavigatorModel) tree.getModel ();
            model.show (node);
        }

        public void keyReleased(KeyEvent e) {
        }
    }
    
    class MyCaretListener implements CaretListener {

        public void caretUpdate (CaretEvent e) {
            LanguagesNavigatorModel model = (LanguagesNavigatorModel) tree.getModel ();
            TreePath treePath = model.getTreePath (e.getDot ());
            if (treePath == null) return;
            tree.setSelectionPath (treePath);
            tree.scrollPathToVisible (treePath);
        }
    }
   
    class MyLookupListener implements LookupListener {
        
        private Result<DataObject> result;

        MyLookupListener (Result<DataObject> result) {
            this.result = result;
            result.addLookupListener (this);
        }
        
        void remove () {
            result.removeLookupListener (this);
        }
        
        public void resultChanged (LookupEvent ev) {
            Iterator<? extends DataObject> it = result.allInstances ().iterator ();
            if (!it.hasNext ())
                setDataObject (null);
            else
                setDataObject (it.next ());
        }
    }
}



