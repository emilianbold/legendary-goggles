/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;
import org.netbeans.junit.*;
import junit.textui.TestRunner;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.*;
import org.openide.util.datatransfer.ExClipboard;

/** Test that verifies that Clipboard is used by swing components.
 * @author Jaroslav Tulach
 * @see "#40693"
 */
public class NbClipboardIsUsedByAlreadyInitializedComponentsTest extends NbClipboardIsUsedBySwingComponentsTest {
    private static final javax.swing.JTextField field = new javax.swing.JTextField ();
    
    public NbClipboardIsUsedByAlreadyInitializedComponentsTest (String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(NbClipboardIsUsedByAlreadyInitializedComponentsTest.class));
    }
    /** overrides to return field that exists since begining and was not instantiated
     * after SecurityManager hack is started */
    protected javax.swing.JTextField getField () {
        return field;
    }
    
}
