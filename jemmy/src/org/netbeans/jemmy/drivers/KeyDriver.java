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

package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.Timeout;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface KeyDriver extends Driver {
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers);
    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers);
    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, Timeout pushTime);
    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, Timeout pushTime);
}
