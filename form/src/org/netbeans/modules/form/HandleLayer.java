/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.form;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import javax.swing.border.Border;
import java.text.MessageFormat;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.datatransfer.NewType;
import org.openide.awt.MouseUtils;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.form.forminfo.*;
import org.netbeans.modules.form.palette.*;

import org.netbeans.modules.form.layoutsupport.*;

/**
 *
 * @author Tran Duc Trung
 */

class HandleLayer extends JPanel
{
    // constants for mode parameter of getMetaComponentAt(Point,int) method
    static final int COMP_DEEPEST = 0; // get the deepest component (at given point)
    static final int COMP_SELECTED = 1; // get the deepest selected component
    static final int COMP_ABOVE_SELECTED = 2; // get the component above the deepest selected component
    static final int COMP_UNDER_SELECTED = 3; // get the component under the deepest selected component
    
    private FormDesigner formDesigner;
    private boolean viewOnly;

    /** The FormLoaderSettings instance */
    private static FormLoaderSettings formSettings = FormEditor.getFormSettings();


    HandleLayer(FormDesigner fd) {
        formDesigner = fd;
        addMouseListener(new HandleLayerMouseListener());
        addMouseMotionListener(new HandleLayerMouseMotionListener());
        setNextFocusableComponent(this);
        setLayout(null);
    }

    void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(formSettings.getSelectionBorderSize()));

        RADComponent conSource = formDesigner.getConnectionSource();
        RADComponent conTarget = formDesigner.getConnectionTarget();
        if (conSource != null || conTarget != null) {
            // paint connection
            g2.setColor(formSettings.getConnectionBorderColor());
            if (conSource != null)
                paintSelection(g2, conSource);
            if (conTarget != null)
                paintSelection(g2, conTarget);
        }
        else {
            // paint selection
            g2.setColor(formSettings.getSelectionBorderColor());
            Iterator metacomps = formDesigner.getSelectedComponents();
            while (metacomps.hasNext()) {
                paintSelection(g2, (RADComponent) metacomps.next());
            }
        }
    }

    private void paintSelection(Graphics2D g, RADComponent metacomp) {
        Object comp = formDesigner.getComponent(metacomp);
        if (comp instanceof Component && ((Component)comp).isShowing()) {
            Component component = (Component) comp;

            Rectangle rect = component.getBounds();
            rect = SwingUtilities.convertRectangle(component.getParent(),
                                                   rect,
                                                   this);

            Rectangle parentRect = new Rectangle(new Point(0,0),
                                                 component.getParent().getSize());
            parentRect = SwingUtilities.convertRectangle(component.getParent(),
                                                         parentRect,
                                                         this);

            Rectangle2D selRect = rect.createIntersection(parentRect);

            int correction = formSettings.getSelectionBorderSize() % 2;
            g.draw(new Rectangle2D.Double(
                selRect.getX() - correction,
                selRect.getY() - correction,
                selRect.getWidth() + correction,
                selRect.getHeight() + correction));
        }
    }

    public boolean isOpaque() {
        return false;
    }

    protected void processKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // we are interested in TAB key - and we want to know about it
        // before focus manager does - not focus but selection is changed
        if (keyCode == KeyEvent.VK_TAB || e.getKeyChar() == '\t') {
            if (e.getID() == KeyEvent.KEY_PRESSED) {

                RADComponent nextComp = formDesigner.getNextVisualComponent(
                    (e.getModifiers()&InputEvent.SHIFT_MASK)!=InputEvent.SHIFT_MASK);

                 if (nextComp != null)
                     formDesigner.setSelectedComponent(nextComp);
            }
            e.consume();
        }
        else if (keyCode == KeyEvent.VK_SPACE) {
            if (!viewOnly && e.getID() == KeyEvent.KEY_RELEASED) {
                Iterator it = formDesigner.getSelectedComponents();
                if (it.hasNext()) {
                    RADComponent comp = (RADComponent)it.next();
                    if (!it.hasNext()) // just one component is selected
                        formDesigner.startInPlaceEditing(comp);
                }
            }
            e.consume();
        }
        else super.processKeyEvent(e);
    }

    public boolean isFocusTraversable() {
        return true;
    }

    /** Returns metacomponent at given position.
     * @param point - position on component layer
     * @param mode - what to get:
     *   COMP_DEEPEST - get the deepest component
     *   COMP_SELECTED - get the deepest selected component
     *   COMP_ABOVE_SELECTED - get the component above the deepest selected component
     *   COMP_UNDER_SELECTED - get the component under the deepest selected component
     * @returns the metacomponent at given point
     *   If no component is currently selected then:
     *     for COMP_SELECTED the deepest component is returned
     *     for COMP_ABOVE_SELECTED the deepest component is returned
     *     for COMP_UNDER_SELETCED the top component is returned
     */
    private RADComponent getMetaComponentAt(Point point, int mode) {
        Component componentLayer = formDesigner.getComponentLayer();
        point = SwingUtilities.convertPoint(this, point, componentLayer);
        Component comp = SwingUtilities.getDeepestComponentAt(
            componentLayer, point.x, point.y);

        RADComponent topMetaComp = formDesigner.getTopDesignContainer(),
                     firstMetaComp = null,
                     currMetaComp,
                     prevMetaComp = null;

        while (comp != null) {
            currMetaComp = formDesigner.getMetaComponent(comp);

            if (currMetaComp != null) {
                if (firstMetaComp == null)
                    firstMetaComp = currMetaComp;

                switch (mode) {
                    case COMP_DEEPEST: 
                        return currMetaComp;

                    case COMP_SELECTED:
                        if (formDesigner.isComponentSelected(currMetaComp))
                            return currMetaComp;
                        if (currMetaComp == topMetaComp)
                            return firstMetaComp; // nothing selected - return the deepest
                        break;

                    case COMP_ABOVE_SELECTED:
                        if (prevMetaComp != null 
                                && formDesigner.isComponentSelected(prevMetaComp))
                            return currMetaComp;
                        if (currMetaComp == topMetaComp)
                            return firstMetaComp; // nothing selected - return the deepest
                        break;

                    case COMP_UNDER_SELECTED:
                        if (formDesigner.isComponentSelected(currMetaComp))
                            return prevMetaComp != null ?
                                     prevMetaComp : topMetaComp;
                        if (currMetaComp == topMetaComp)
                            return topMetaComp; // nothing selected - return the top
                        break;
                }

                prevMetaComp = currMetaComp;
            }
            comp = comp.getParent();
        }
        return firstMetaComp;
    }


    static private void showInstErrorMessage(Throwable ex) {
//        if (System.getProperty("netbeans.debug.exceptions") != null)
            ex.printStackTrace();

        String message = MessageFormat.format(
            FormEditor.getFormBundle().getString("FMT_ERR_CannotInstantiate"),
            new Object [] { ex.getClass().getName(), ex.getMessage() });
        TopManager.getDefault().notify(new NotifyDescriptor.Message(
            message, NotifyDescriptor.ERROR_MESSAGE));
    }


    private class HandleLayerMouseListener implements MouseListener
    {
        public void mouseClicked(MouseEvent e) {
            e.consume();
        }

        public void mouseReleased(MouseEvent e) {
            if (!HandleLayer.this.isVisible())
                return;

            if (MouseUtils.isRightMouseButton(e)) {
                showContextMenu(e.getPoint());
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            TopManager.getDefault().setStatusText(""); // NOI18N
        }
        
        public void mousePressed(MouseEvent e) {
            if (!HandleLayer.this.isVisible()) return;

            if (MouseUtils.isRightMouseButton(e)) {
                RADComponent hitMetaComp = getMetaComponentAt(e.getPoint(), COMP_SELECTED);
                if (!formDesigner.isComponentSelected(hitMetaComp))
                    formDesigner.setSelectedComponent(hitMetaComp);
                e.consume();
            }
            else if ((e.getModifiers() & InputEvent.BUTTON1_MASK) ==
                     InputEvent.BUTTON1_MASK) { // left mouse button pressed

                ComponentPalette palette = ComponentPalette.getDefault();

                if (palette.getMode() == PaletteAction.MODE_SELECTION) {
                    selectComponent(e);

                    if (!e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
                        RADComponent hitMetaComp = getMetaComponentAt(
                                                   e.getPoint(), COMP_DEEPEST);
                        if (e.getClickCount() == 2)
                            invokeDefaultAction(hitMetaComp);
                        else if (formDesigner.isComponentSelected(hitMetaComp))
                            processMouseClickInLayoutSupport(hitMetaComp, e);
                    }
                }
                else if (!viewOnly) {
                    RADComponent hitMetaComp = getMetaComponentAt(e.getPoint(),
                            e.isControlDown() || e.isAltDown() ?
                            COMP_SELECTED : COMP_DEEPEST);

                    if (palette.getMode() == PaletteAction.MODE_CONNECTION) {
                        if (hitMetaComp != null)
                            formDesigner.connectBean(hitMetaComp);
                    }
                    else if (palette.getMode() == PaletteAction.MODE_ADD) {
                        PaletteItem item = palette.getSelectedItem();

                        if (item.isBorder()) {
                            if (hitMetaComp != null)
                                setComponentBorder(hitMetaComp, item);
                        }
                        else if (item.isLayout()) {
                            if (hitMetaComp == null)
                                hitMetaComp = formDesigner.getModel().getTopRADComponent();
                            setContainerLayout(hitMetaComp, item);
                        }
                        else if (item.isMenu()) {
                            addMenu(item);
                        }
                        else if (item.isVisual()) {
                            if (hitMetaComp == null)
                                hitMetaComp = formDesigner.getModel().getTopRADComponent();
                            addVisualBean(hitMetaComp, item, e);
                        }
                        else {
                            addNonVisualBean(item);
                        }

                        if ((e.getModifiers() & InputEvent.SHIFT_MASK) == 0)
                            palette.setMode(PaletteAction.MODE_SELECTION);
                    }
                }
                e.consume();
            }
        }

        /** Selects component at the position e.getPoint() on component layer.
         * What component is selected further depends on whether CTRL or ALT
         * keys are hold. */
        private void selectComponent(MouseEvent e) {
            boolean ctrl = e.isControlDown() && !e.isAltDown();
            boolean alt = e.isAltDown() && !e.isControlDown();

            int selMode = ctrl ? COMP_UNDER_SELECTED :
                                 (alt ? COMP_ABOVE_SELECTED : COMP_DEEPEST);

            RADComponent hitMetaComp = getMetaComponentAt(e.getPoint(), selMode);

            if (e.isShiftDown()) {
                if (formDesigner.isComponentSelected(hitMetaComp))
                    formDesigner.removeComponentFromSelection(hitMetaComp);
                else
                    formDesigner.addComponentToSelection(hitMetaComp);
            }
            else {
                if (hitMetaComp != null)
                    formDesigner.setSelectedComponent(hitMetaComp);
                else
                    formDesigner.clearSelection();
            }
            repaint();
        }

        private void processMouseClickInLayoutSupport(RADComponent metacomp,
                                                      MouseEvent e) {
            if (!(metacomp instanceof RADVisualComponent)) return;

            RADVisualContainer metacont = metacomp instanceof RADVisualContainer ?
                    (RADVisualContainer)metacomp :
                    ((RADVisualComponent)metacomp).getParentContainer();

            LayoutSupport laysup = metacont.getLayoutSupport();
            if (laysup instanceof LayoutSupportArranging) {
                Container cont = (Container) formDesigner.getComponent(metacont);
                Point p = SwingUtilities.convertPoint(HandleLayer.this, e.getPoint(), cont);
                ((LayoutSupportArranging)laysup).processMouseClick(p, cont);
            }
        }

        private void invokeDefaultAction(RADComponent metacomp) {
            Node node = metacomp.getNodeReference();
            if (node != null) {
                SystemAction action = node.getDefaultAction();
                if (action != null && action.isEnabled()) {
                    action.actionPerformed(new ActionEvent(
                            node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
                }
            }
        }

        private void setComponentBorder(RADComponent metacomp, PaletteItem item) {
            if (!(metacomp instanceof RADVisualComponent) || !item.isBorder())
                return;
            
            if (!(JComponent.class.isAssignableFrom(metacomp.getBeanClass()))) {
                TopManager.getDefault().notify(new NotifyDescriptor.Message(
                    FormEditor.getFormBundle().getString("MSG_BorderNotApplicable"),
                                                         NotifyDescriptor.INFORMATION_MESSAGE));
                return;
            }

            RADProperty prop = metacomp.getPropertyByName("border");
            if (prop == null) return;

            try {
                Object border = item.createInstance();
                prop.setValue(border);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            formDesigner.setSelectedComponent(metacomp);
        }

/*        private void setDesignLayout(RADComponent metacomp, PaletteItem item) {
            if (!(metacomp instanceof RADVisualComponent)
                || ! item.isDesignLayout())
                return;

            RADVisualContainer metacont;
            
            if (metacomp instanceof RADVisualContainer)
                metacont = (RADVisualContainer) metacomp;
            else
                metacont = ((RADVisualComponent)metacomp).getParentContainer();
            
            DesignLayout newLayout;
            
            try {
                newLayout = (DesignLayout) item.createInstance();
            } catch (Exception e) {
                TopManager.getDefault().notify(
                    new NotifyDescriptor.Message(
                        MessageFormat.format(
                            FormEditor.getFormBundle().getString("FMT_ERR_LayoutInit"),
                            new Object [] {
                                item.getItemClass().getName(),
                                e.getClass().getName() }),
                        NotifyDescriptor.ERROR_MESSAGE));
                return;
            }

            LayoutSupport layoutSup = Compat31LayoutFactory.createCompatibleLayoutSupport(newLayout);
            if (layoutSup == null) return;
            metacont.getFormModel().setContainerLayout(metacont, layoutSup);
//XXX              DesignLayout oldLayout = metacont.getDesignLayout();
//              metacont.setDesignLayout(newLayout);

            if (metacont.getNodeReference() != null) {
                // it can be null during init
                Node[] childrenNodes = ((RADChildren) metacont.getNodeReference().getChildren()).getNodes();

                if (childrenNodes != null) {
                    Node layoutNode = null;
                    if ((metacont instanceof FormContainer) &&(childrenNodes.length > 0)) {
                        layoutNode = childrenNodes[1];
                    } else if (childrenNodes.length > 0) {
                        layoutNode =  childrenNodes[0]; // [PENDING IAN - ugly patch !!! - on Form nodes, the layout is the second child]
                    }
//XXX                      if ((layoutNode != null) &&(layoutNode instanceof RADLayoutNode)) {
//                          ((RADLayoutNode)layoutNode).updateState();
//                      }
                }
            }
            //fireCodeChange();
        } */

        private void setContainerLayout(RADComponent metacomp, PaletteItem item) {
            if (!(metacomp instanceof RADVisualComponent)
                    || !item.isLayout())
                return;

            LayoutSupport layoutSupport = null;
            try {
                layoutSupport = item.createLayoutSupportInstance();
            }
            catch (Exception e) {
                if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                    e.printStackTrace();

                TopManager.getDefault().notify(
                    new NotifyDescriptor.Message(
                        MessageFormat.format(
                            FormEditor.getFormBundle().getString("FMT_ERR_LayoutInit"),
                            new Object[] { item.getItemClass().getName(),
                                            e.getClass().getName() }),
                        NotifyDescriptor.ERROR_MESSAGE));
                return;
            }

            if (layoutSupport == null) {
                TopManager.getDefault().notify(
                    new NotifyDescriptor.Message(
                        MessageFormat.format(
                            FormEditor.getFormBundle().getString("FMT_ERR_LayoutNotFound"),
                            new Object[] { item.getItemClass().getName() }),
                        NotifyDescriptor.ERROR_MESSAGE));
                return;
            }

            // get container on which the layout will be set
            RADVisualContainer metaCont = metacomp instanceof RADVisualContainer ?
                (RADVisualContainer) metacomp :
                ((RADVisualComponent)metacomp).getParentContainer();

            metaCont.getFormModel().setContainerLayout(metaCont, layoutSupport);
        }

        private void addVisualBean(RADComponent metacomp, PaletteItem item,
                                   MouseEvent e) {
            if (!(metacomp instanceof RADVisualComponent)
                    || !item.isVisual())
                return;

            // get parent container into which new component will be added
            RADVisualContainer parentCont = metacomp instanceof RADVisualContainer ?
                (RADVisualContainer) metacomp :
                ((RADVisualComponent)metacomp).getParentContainer();

            RADVisualComponent newMetacomp = null;
            RADVisualContainer newMetacont = item.isContainer() ?
                new RADVisualContainer() : null;

            while (newMetacomp == null) {
                // initialize meta-component and its bean instance
                newMetacomp = newMetacont == null ?
                    new RADVisualComponent() : newMetacont;

                newMetacomp.initialize(formDesigner.getModel());

                try {
                    newMetacomp.initInstance(item.getInstanceCookie());
                }
                catch (Throwable th) {
                    if (th instanceof ThreadDeath)
                        throw (ThreadDeath)th;
                    else {
                        showInstErrorMessage(th);
                        return;
                    }
                }

                if (newMetacont != null) { // the new component is a container
                    // initialize LayoutSupport
                    newMetacont.initLayoutSupport();
                    if (newMetacont.getLayoutSupport() == null) {
                        // no LayoutSupport found for the container,
                        // create RADVisualComponent only
                        newMetacont = null;
                        newMetacomp = null;
                    }
                }
            }

            Container cont = (Container) formDesigner.getComponent(parentCont);
            Point p = SwingUtilities.convertPoint(HandleLayer.this,
                                                  e.getPoint(), cont);
            LayoutSupport.ConstraintsDesc constraints =
                parentCont.getLayoutSupport().getNewConstraints(cont, p, null, null);

            formDesigner.getModel().addVisualComponent(newMetacomp, parentCont,
                                                       constraints);

            // for some components, we initialize their properties with some
            // non-default values e.g. a label on buttons, checkboxes
            FormEditor.defaultComponentInit(newMetacomp);
            
            formDesigner.setSelectedComponent(newMetacomp);
            //formWindow.validate();
            //fireCodeChange();
        }

        private void addNonVisualBean(PaletteItem item) {
            RADComponent newMetacomp = new RADComponent();
            newMetacomp.initialize(formDesigner.getModel());

            try {
                newMetacomp.initInstance(item.getInstanceCookie());
//                newComp = item.createInstance();
//                newMetacomp.setInstance(newComp);
            }
            catch (Throwable th) {
                if (th instanceof ThreadDeath)
                    throw (ThreadDeath)th;
                else {
                    showInstErrorMessage(th);
                    return;
                }
            }

            formDesigner.getModel().addNonVisualComponent(newMetacomp, null);
            formDesigner.setSelectedComponent(newMetacomp);
            //formWindow.validate();
            //fireCodeChange();
        }

        private void addMenu(PaletteItem item) {
            FormModel formModel = formDesigner.getModel();
            
            RADMenuComponent newMenuComp = new RADMenuComponent();
            newMenuComp.initialize(formModel);
            newMenuComp.setComponent(item.getItemClass());
            newMenuComp.initSubComponents(new RADComponent[0]);
            formModel.addNonVisualComponent(newMenuComp, null);
            
            // for some components, we initialize their properties with some
            // non-default values e.g. a label on buttons, checkboxes
            FormEditor.defaultMenuInit(newMenuComp);
            
            NewType[] newTypes = newMenuComp.getNewTypes();
            if (newTypes.length != 0) {
                try {
                    newTypes[0].create();
                } catch (java.io.IOException e) {
                }
            }

            FormInfo formInfo = formModel.getFormInfo();
            
            if ((formInfo instanceof JMenuBarContainer
                 && JMenuBar.class.isAssignableFrom(item.getItemClass()))
                || (formInfo instanceof MenuBarContainer
                    && MenuBar.class.isAssignableFrom(item.getItemClass()))) {
                if (((RADVisualFormContainer)formModel.getTopRADComponent()).getFormMenu() == null) {
                    ((RADVisualFormContainer)formModel.getTopRADComponent()).setFormMenu(newMenuComp.getName());
                }
            }
            
            formDesigner.setSelectedComponent(newMenuComp);
        }

        private void showContextMenu(Point popupPos) {
            Node[] selectedNodes = ComponentInspector.getInstance().getSelectedNodes();
            JPopupMenu popup = NodeOp.findContextMenu(selectedNodes);
            if (popup != null) {
                popup.show(HandleLayer.this, popupPos.x, popupPos.y);
            }
        }
    }

    private class HandleLayerMouseMotionListener implements MouseMotionListener
    {
        public void mouseDragged(MouseEvent e) {
//            System.err.println("** dragging : " + e.getPoint()); // XXX
        }

        public void mouseMoved(MouseEvent e) {
            ComponentPalette palette = ComponentPalette.getDefault();
            if (palette.getMode() == PaletteAction.MODE_ADD) {
                RADComponent hitMetaComp = getMetaComponentAt(e.getPoint(), COMP_DEEPEST);
                displayHint(hitMetaComp, e.getPoint(), palette.getSelectedItem());
            }
        }
        
        private void displayHint(RADComponent metacomp, Point p, PaletteItem item) {
            if (metacomp == null) {
                TopManager.getDefault().setStatusText(""); // NOI18N
                return;
            }
            
            RADVisualContainer metacont;
        
            if (metacomp instanceof RADVisualContainer)
                metacont = (RADVisualContainer) metacomp;
            else
                metacont = ((RADVisualComponent)metacomp).getParentContainer();
        
            if (item.isLayout()) {
                LayoutSupport layoutSupp = metacont.getLayoutSupport();
                if (layoutSupp != null
                    && !(layoutSupp instanceof LayoutSupport))
                {
                    setStatusText("FMT_MSG_CannotSetLayout",
                                  new Object[] { metacont.getName() });
                } else {
                    setStatusText("FMT_MSG_SetLayout",
                                  new Object[] { metacont.getName() });
                }
            }
            else if (item.isBorder()) {
                if (JComponent.class.isAssignableFrom(metacomp.getBeanClass())) {
                    setStatusText("FMT_MSG_SetBorder",
                                  new Object[] { metacomp.getName() });
                }
                else {
                    setStatusText("FMT_MSG_CannotSetBorder",
                                  new Object[] { metacomp.getName() });
                }
            } else if (!item.isVisual() || item.isMenu()) {
                setStatusText("FMT_MSG_AddNonVisualComponent",
                              new Object[] { item.getItemClass().getName() });
            } else {
                LayoutSupport layoutSupp = metacont.getLayoutSupport();
                if (layoutSupp != null) {
                    Container cont = metacont.getContainerDelegate(
                            formDesigner.getComponent(metacont));
                    Point point = SwingUtilities.convertPoint(
                            HandleLayer.this, p, cont);
                    LayoutSupport.ConstraintsDesc cd =
                            layoutSupp.getNewConstraints(cont, p, null, null);
                    if (cd != null) {
                        setStatusText("FMT_MSG_AddComponent",
                                      new Object[] {
                                          cd.getJavaInitializationString(),
                                          metacont.getName(),
                                          item.getItemClass().getName()
                                      });
                    }
                }
            }
        }
    }

    private static void setStatusText(String formatId, Object[] args) {
        TopManager.getDefault().setStatusText(
            MessageFormat.format(
                FormEditor.getFormBundle().getString(formatId),
                args));
    }
}
