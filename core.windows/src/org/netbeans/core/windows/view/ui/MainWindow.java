/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.windows.view.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.NotifyException;
import org.netbeans.core.windows.Constants;
import org.netbeans.core.windows.WindowManagerImpl;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.awt.MenuBar;
import org.openide.awt.ToolbarPool;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** The MainWindow of IDE. Holds toolbars, main menu and also entire desktop
 * if in MDI user interface. Singleton.
 * This class is final only for performance reasons, can be unfinaled
 * if desired.
 *
 * @author Ian Formanek, Petr Hamernik
 */
public final class MainWindow extends JFrame {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -1160791973145645501L;

    /** Desktop. */
    private Component desktop;
    
    /** Inner panel which contains desktop component */
    private JPanel desktopPanel;
    
    /** Flag indicating main window is initialized. */ 
    private boolean inited;
    

    /** Constructs main window. */
    public MainWindow() {
// ignore the policy - #46922        
//        setFocusTraversalPolicy(new WrapperFocusTraversalPolicy(getFocusTraversalPolicy()));
    }
    
    /** Overrides superclass method, adds help context to the new root pane. */
    protected void setRootPane(JRootPane root) {
        super.setRootPane(root);
        if(root != null) {
            HelpCtx.setHelpIDString(
                    root, new HelpCtx(MainWindow.class).getHelpID());
        }
        //Optimization related to jdk bug 4939857 - on pre 1.5 jdk's an
        //extra repaint is caused by the search for an opaque component up
        //to the component root.  Post 1.5, root pane will automatically be
        //opaque.
        root.setOpaque(true);
        if (Utilities.isWindows()) {
            // use glass pane that will not cause repaint/revalidate of parent when set visible
            // is called (when setting wait cursor in ModuleActions) #40689
            JComponent c = new JPanel() {
                public void setVisible(boolean flag) {
                    if (flag != isVisible ()) {
                        super.setVisible(flag);
                    }
                }
            };
            c.setName(root.getName()+".nbGlassPane");  // NOI18N
            c.setVisible(false);
            ((JPanel)c).setOpaque(false);
            root.setGlassPane(c);
        }
    }
    
    /** Initializes main window. */
    public void initializeComponents() {
        if(inited) {
            return;
        }
        inited = true;
        
        // initialize frame
        setIconImage(createIDEImage());
        
        initListeners();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getAccessibleContext().setAccessibleDescription(
                NbBundle.getBundle(MainWindow.class).getString("ACSD_MainWindow"));

        setJMenuBar(createMenuBar());
    
        if (!Constants.NO_TOOLBARS) {
            JComponent tb = getToolbarComponent();
            getContentPane().add(tb, BorderLayout.NORTH);
        }
        
        if(!Constants.SWITCH_STATUSLINE_IN_MENUBAR) {
            if (Constants.CUSTOM_STATUS_LINE_PATH == null) {
                JLabel status = new StatusLine();
                // XXX #19910 Not to squeeze status line.
                status.setText(" "); // NOI18N
                status.setPreferredSize(new Dimension(0, status.getPreferredSize().height));

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JSeparator(), BorderLayout.NORTH);
                panel.add(status, BorderLayout.CENTER);
                Component exceptionStatus = NotifyException.getNotificationVisualizer();
                if( null != exceptionStatus )
                    panel.add( exceptionStatus, BorderLayout.EAST );
                panel.setName("statusLine"); //NOI18N
                getContentPane().add(panel, BorderLayout.SOUTH);
            } else { // custom status line provided
                JComponent status = getCustomStatusLine();
                if (status != null) {
                    getContentPane().add(status, BorderLayout.SOUTH);
                }
            }
        }
        
        // initialize desktop panel
        desktopPanel = new JPanel();
    
        desktopPanel.setBorder(getDesktopBorder());
        desktopPanel.setLayout(new BorderLayout());
        
        Color fillC = (Color)UIManager.get("nb_workplace_fill"); //NOI18N
        if (fillC != null) {
            desktopPanel.setBackground(fillC);
        }

        getContentPane().add(desktopPanel, BorderLayout.CENTER);
        //#38810 start - focusing the main window in case it's not active and the menu is
        // selected..
        MenuSelectionManager.defaultManager().addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                MenuElement[] elems = MenuSelectionManager.defaultManager().getSelectedPath();
                if (elems != null && elems.length > 0) {
                    if (elems[0] == getJMenuBar()) {
                        if (!isActive()) {
                            toFront();
                        }
                    }
                }
            }
        });
        //#38810 end
    }
    
    /** Creates and returns border for desktop which is visually aligned
     * with currently active LF */
    private static Border getDesktopBorder () {
        Border b = (Border) UIManager.get ("nb.desktop.splitpane.border");
        if (b != null) {
            return b;
        } else {
            return new EmptyBorder(1, 1, 1, 1);
        }
    }
    
    private static final String ICON_SMALL = "org/netbeans/core/resources/frames/ide.gif"; // NOI18N
    private static final String ICON_BIG = "org/netbeans/core/resources/frames/ide32.gif"; // NOI18N
    static Image createIDEImage() {
        return Utilities.loadImage(Utilities.isLargeFrameIcons() ? ICON_BIG : ICON_SMALL, true);
    }
    
    private void initListeners() {
        addWindowListener (new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    LifecycleManager.getDefault().exit();
                }

                public void windowActivated (WindowEvent evt) {
                   // #19685. Cancel foreigner popup when
                   // activated main window.
                   org.netbeans.core.windows.RegistryImpl.cancelMenu(MainWindow.this);
                }
            }
        );
    }

    /** Creates menu bar. */
    private static JMenuBar createMenuBar() {
        JMenuBar menu = getCustomMenuBar();
        if (menu == null) {
             menu = new MenuBar (null);
        }
        menu.setBorderPainted(false);
        if (menu instanceof MenuBar) {
            ((MenuBar)menu).waitFinished();
        }
        
        if(Constants.SWITCH_STATUSLINE_IN_MENUBAR) {
            if (Constants.CUSTOM_STATUS_LINE_PATH == null) {
                JLabel status = new StatusLine();
                JPanel panel = new JPanel(new BorderLayout());
                JSeparator sep = new JSeparator(JSeparator.VERTICAL);
                Dimension d = sep.getPreferredSize();
                d.width += 6; // need a bit more padding...
                sep.setPreferredSize(d);
                panel.add(sep, BorderLayout.WEST);
                panel.add(status, BorderLayout.CENTER);
                Component exceptionStatus = NotifyException.getNotificationVisualizer();
                if( null != exceptionStatus )
                    panel.add( exceptionStatus, BorderLayout.EAST );
                panel.setName("statusLine"); //NOI18N
                menu.add(panel);
            } else {
                JComponent status = getCustomStatusLine();
                if (status != null) {
                    menu.add(status);
                }
            }
        }
        
        return menu;
    }

     /**
      * Tries to find custom menu bar component on system file system.
      * @return menu bar component or <code>null</code> if no menu bar
      *         component is found on system file system.
      */
     private static JMenuBar getCustomMenuBar() {
         try {
             String fileName = Constants.CUSTOM_MENU_BAR_PATH;
             if (fileName == null) {
                 return null;
             }
             FileObject fo =
                 Repository.getDefault().getDefaultFileSystem().findResource(
                     fileName);
             if (fo != null) {
                 DataObject dobj = DataObject.find(fo);
                 InstanceCookie ic = (InstanceCookie)dobj.getCookie(InstanceCookie.class);
                 if (ic != null) {
                     return (JMenuBar)ic.instanceCreate();
                 }
             }
         } catch (Exception e) {
             ErrorManager.getDefault().notify(e);
         }
         return null;
     }
    
     /**
      * Tries to find custom status line component on system file system.
      * @return status line component or <code>null</code> if no status line
      *         component is found on system file system.
      */
     private static JComponent getCustomStatusLine() {
         try {
             String fileName = Constants.CUSTOM_STATUS_LINE_PATH;
             if (fileName == null) {
                 return null;
             }
             FileObject fo =
                 Repository.getDefault().getDefaultFileSystem().findResource(
                     fileName);
             if (fo != null) {
                 DataObject dobj = DataObject.find(fo);
                 InstanceCookie ic = (InstanceCookie)dobj.getCookie(InstanceCookie.class);
                 if (ic != null) {
                     return (JComponent)ic.instanceCreate();
                 }
             }
         } catch (Exception e) {
             ErrorManager.getDefault().notify(e);
         }
         return null;
     }
     
    /** Creates toolbar component. */
    private static JComponent getToolbarComponent() {
        ToolbarPool tp = ToolbarPool.getDefault();
        tp.waitFinished();
//        ErrorManager.getDefault().getInstance(MainWindow.class.getName()).log("toolbar config name=" + WindowManagerImpl.getInstance().getToolbarConfigName());
//        tp.setConfiguration(WindowManagerImpl.getInstance().getToolbarConfigName()); // NOI18N
        
        return tp;
    }

    /** Packs main window, to set its border */
    private void initializeBounds() {
        Rectangle bounds;
        if(WindowManagerImpl.getInstance().getEditorAreaState() == Constants.EDITOR_AREA_JOINED) {
            bounds = WindowManagerImpl.getInstance().getMainWindowBoundsJoined();
        } else {
            bounds = WindowManagerImpl.getInstance().getMainWindowBoundsSeparated();
        }
        
        if(!bounds.isEmpty()) {
            setBounds(bounds);
        }
    }
    
    /**
     * don't allow smaller bounds than the one constructed from preffered sizes, making sure everything is visible when
     * in SDI. #40063
     */
    public void setBounds(Rectangle rect) {
        Rectangle bounds = rect;
        if (bounds != null) {
            if (bounds.height < getPreferredSize().height) {
                bounds = new Rectangle(bounds.x, bounds.y, bounds.width, getPreferredSize().height);
            }
        }
        super.setBounds(bounds);
    }
    
    /** Prepares main window, has to be called after {@link initializeComponents()}. */
    public void prepareWindow() {
        initializeBounds();
    }

    /** Sets desktop component. */
    public void setDesktop(Component comp) {
        if(desktop == comp) {
            // XXX PENDING revise how to better manipulate with components
            // so there don't happen unneeded removals.
            if(desktop != null
            && !Arrays.asList(desktopPanel.getComponents()).contains(desktop)) {
                desktopPanel.add(desktop, BorderLayout.CENTER);
            }
            return;
        }

        if(desktop != null) {
            desktopPanel.remove(desktop);
        }
        
        desktop = comp;
        
        if(desktop != null) {
            desktopPanel.add(desktop, BorderLayout.CENTER);
        } 
        invalidate();
        validate();
        if( !firstTimeHack )
            repaint();
    }

    // XXX PENDING used in DnD only.
    public Component getDesktop() {
        return desktop;
    }
    
    public boolean hasDesktop() {
        return desktop != null;
    }

    // XXX
    /** Gets bounds of main window without the dektop component. */
    public Rectangle getPureMainWindowBounds() {
        Rectangle bounds = getBounds();

        // XXX Substract the desktop height, we know the pure main window
        // is always at the top, the width is same.
        if(desktop != null) {
            Dimension desktopSize = desktop.getSize();
            bounds.height -= desktopSize.height;
        }
        
        return bounds;
    }

    public void setProjectName(String projectName) {
        updateTitle(projectName);
    }
    
    private static final String BUILD_NUMBER = System.getProperty("netbeans.buildnumber"); // NOI18N
    private static final String TITLE_NO_PROJECT = NbBundle.getMessage(MainWindow.class, "CTL_MainWindow_Title_No_Project", BUILD_NUMBER);
    private static final Format FORMAT_PROJECT = new MessageFormat(NbBundle.getMessage(MainWindow.class, "CTL_MainWindow_Title"));
    
    /** Updates the MainWindow's title */
    private void updateTitle(String projectName) {
        // XXX might be a good idea to put this into a RequestProcessor task
        // scheduled for 100msec in the future to coalesce changes (also need
        // to then repost back to EQ); seems that JFrame.setTitle can be expensive
        if (projectName == null) {
            setTitle(TITLE_NO_PROJECT);
        } else {
            setTitle(FORMAT_PROJECT.format(new Object[] {BUILD_NUMBER, projectName}));
        }
        
    }

/*    private final class WrapperFocusTraversalPolicy extends FocusTraversalPolicy {
        private FocusTraversalPolicy w;
        public WrapperFocusTraversalPolicy (FocusTraversalPolicy w) {
            this.w = w;
        }
        
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            return w.getComponentAfter(focusCycleRoot, aComponent);
        }

        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            return w.getComponentBefore (focusCycleRoot, aComponent);
        }

        public Component getDefaultComponent(Container focusCycleRoot) {
            Component result = w.getDefaultComponent(focusCycleRoot);
            
            ModeImpl mi = WindowManagerImpl.getInstance().getActiveMode();
            if (mi != null) {
                TopComponent tc = mi.getSelectedTopComponent();
                return tc;
            }
            return null;
        }

        public Component getFirstComponent(Container focusCycleRoot) {
            return w.getFirstComponent (focusCycleRoot);
        }

        public Component getLastComponent(Container focusCycleRoot) {
            Component result = w.getLastComponent(focusCycleRoot);
            return result;
        }
    }    
 */

    public Graphics getGraphics() {
        // Return the dummy graphics that paint nowhere, until we receive a paint() 
        if (waitingForPaintDummyGraphic != null) {
            // If we are the PaintEvent we are waiting for is being dispatched
            // we better return the correct graphics.
            AWTEvent event = EventQueue.getCurrentEvent();
            if (event == null || (event.getID() != PaintEvent.PAINT && event.getSource() != this)) {
                return waitingForPaintDummyGraphic;
	        }
	        releaseWaitingForPaintDummyGraphic();
        }
        return super.getGraphics();
    }

    public void paint(Graphics g) {
        // As a safeguard, always release the dummy graphic when we get a paint
        if (waitingForPaintDummyGraphic != null) {
            releaseWaitingForPaintDummyGraphic();
            // Since the release did not occur before the getGraphics() call,
            // I need to get the actual graphics now that I've released
            g = getGraphics();
        }
        super.paint(g);
    }
    

    protected Image waitingForPaintDummyImage; 
    protected Graphics waitingForPaintDummyGraphic; 
    
    public void setVisible( boolean flag ) {
        if( firstTimeHack ) {
            waitingForPaintDummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            waitingForPaintDummyGraphic = waitingForPaintDummyImage.getGraphics();
        }
        super.setVisible(flag);
    }
    
    protected void releaseWaitingForPaintDummyGraphic() {
        if (waitingForPaintDummyGraphic != null) {
            waitingForPaintDummyGraphic.dispose();
            waitingForPaintDummyGraphic = null;
            waitingForPaintDummyImage = null;
        }
    }

    boolean firstTimeHack = true;
    public void setExtendedState(int state) {

        int prevState = getExtendedState();

        if( !System.getProperty("os.name").startsWith("Windows") )
            releaseWaitingForPaintDummyGraphic();

        super.setExtendedState(state);
        
        if( firstTimeHack && state != prevState && state == Frame.MAXIMIZED_BOTH ) {
            //we're switching to the maximized mode for the first time upon startup
            //so the main window must repaint now (instead of repainting from the
            //setDesktop method)
            firstTimeHack = false;
            repaint();        
        }
    }
}

