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
/*
 * AquaSeparatorUI.java
 *
 * Created on March 14, 2004, 4:57 AM
 */

package org.netbeans.swing.plaf.aqua;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;

/**
 * Aqua SeparatorUI in JPopupMenu has a height of 12px. The line has a
 * padding-left and padding-right of 1px. And the line is draw at px 6.
 *
 * Only JPopupMenu Separator get draw, all other are 0x0 px.
 *
 * @author  Christopher Atlan
 */
public class AquaSeparatorUI extends SeparatorUI {
    private final static Color lineColor = new Color(215, 215, 215);
    
    private static ComponentUI separatorui = new AquaSeparatorUI();
    
    public static ComponentUI createUI(JComponent c) {
        return separatorui;
    }
    
    public void paint( Graphics g, JComponent c ) {
        if (c.getParent() instanceof JPopupMenu) {
            Dimension s = c.getSize();
            
            g.setColor(lineColor);
            g.drawLine(1, 5, s.width - 2, 5);
        }
    }
    
    public Dimension getPreferredSize(JComponent c) {
        Dimension s;
        if (c.getParent() instanceof JPopupMenu) {
            return new Dimension( 0, 12 );
        } else {
            s = new Dimension(0, 0);
        }
        
        return s;
    }
    
    public Dimension getMinimumSize( JComponent c ) { return null; }
    public Dimension getMaximumSize( JComponent c ) { return null; }
}
