/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Jemmy library.
 * The Initial Developer of the Original Code is Alexandre Iline.
 * All Rights Reserved.
 * 
 * Contributor(s): Alexandre Iline.
 * 
 * $Id$ $Revision$ $Date$
 * 
 */

package org.netbeans.jemmy.drivers.input;

import java.awt.Component;

import java.awt.event.MouseEvent;

import org.netbeans.jemmy.Timeout;

import org.netbeans.jemmy.drivers.MouseDriver;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

public class MouseEventDriver extends EventDriver implements MouseDriver {
    public void pressMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_PRESSED, 
		      modifiers, x, y, 1,
		      mouseButton);
    }
    public void releaseMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_RELEASED, 
		      modifiers, x, y, 1,
		      mouseButton);
    }
    public void moveMouse(ComponentOperator oper, int x, int y) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_MOVED, 
		      0, x, y, 0,
		      Operator.getDefaultMouseButton());
    }
    public void clickMouse(ComponentOperator oper, int x, int y, int clickCount, int mouseButton, 
			   int modifiers, Timeout mouseClick) {
	moveMouse(oper, x, y);
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_ENTERED, 
		      0, x, y, 0,
		      Operator.getDefaultMouseButton());
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_PRESSED, 
		      modifiers, x, y, 1,
		      mouseButton);
	for(int i = 1; i < clickCount; i++) {
	    dispatchEvent(oper.getSource(),
			  MouseEvent.MOUSE_RELEASED, 
			  modifiers, x, y, i,
			  mouseButton);
	    dispatchEvent(oper.getSource(),
			  MouseEvent.MOUSE_CLICKED, 
			  modifiers, x, y, i,
			  mouseButton);
	    dispatchEvent(oper.getSource(),
			  MouseEvent.MOUSE_PRESSED, 
			  modifiers, x, y, i + 1,
			  mouseButton);
	}
	mouseClick.sleep();
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_RELEASED, 
		      modifiers, x, y, clickCount,
		      mouseButton);
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_CLICKED, 
		      modifiers, x, y, clickCount,
		      mouseButton);
	exitMouse(oper);
    }
    public void dragMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_DRAGGED, 
		      modifiers, x, y, 1,
		      mouseButton);
    }
    public void dragNDrop(ComponentOperator oper, int start_x, int start_y, int end_x, int end_y, 
			  int mouseButton, int modifiers, Timeout before, Timeout after) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_ENTERED, 
		      0, start_x, start_y, 0,
		      Operator.getDefaultMouseButton());
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_PRESSED, 
		      modifiers, start_x, start_y, 1,
		      mouseButton);
	before.sleep();
	dragMouse(oper, end_x,  end_y, mouseButton, modifiers);
	after.sleep();
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_RELEASED, 
		      modifiers, end_x, end_y, 1,
		      mouseButton);
	exitMouse(oper);
    }
    public void enterMouse(ComponentOperator oper) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_ENTERED, 
		      0, oper.getCenterX(), oper.getCenterY(), 0,
		      Operator.getDefaultMouseButton());
    }
    public void exitMouse(ComponentOperator oper) {
	dispatchEvent(oper.getSource(),
		      MouseEvent.MOUSE_EXITED, 
		      0, oper.getCenterX(), oper.getCenterY(), 0,
		      Operator.getDefaultMouseButton());
    }
    protected void dispatchEvent(Component comp, int id, int modifiers, int x, int y, int clickCount, int mouseButton) {
	dispatchEvent(comp,
		      new MouseEvent(comp, 
				     id, 
				     System.currentTimeMillis(), 
				     modifiers | mouseButton, x, y, clickCount,
				     mouseButton == Operator.getPopupMouseButton()));
    }
}
