/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.tests.xml;

import java.io.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.util.Dumper;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.test.oo.gui.jelly.JellyProperties;

/**
 * Provides the basic support for XML Jemmy tests.
 * @author  ms113234
 */
public class JXTest extends XTest {
    public static final String DELIM = "|";
    protected static boolean captureScreen = true;
    protected static boolean dumpScreen = true;
    
    
    /** Creates a new instance of JXMLXtest */
    public JXTest(String name) {
        super(name);
        boolean dbgTimeouts = Boolean.getBoolean(System.getProperty("xmltest.dbgTimeouts", "true"));
        try {
            if (dbgTimeouts) {
                JemmyProperties.getCurrentTimeouts().loadDebugTimeouts();
                JellyProperties.setJemmyDebugTimeouts();
            }
        } catch (IOException ioe) {
            fail("Load Debug Timeouts fail.");
            ioe.printStackTrace();
        }
    }

    void fail(String msg, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        if (captureScreen) {
            try {
                PNGEncoder.captureScreen(getWorkDirPath()+File.separator+"screen.png");
            } catch (Exception e1) {}
        }
        if (dumpScreen) {
            try {
                Dumper.dumpAll(getWorkDirPath()+File.separator+"screen.xml");
            } catch (Exception e2) {}
        }
        fail(msg + "\n" + sw);
    }
    
//    /**
//     * Returns work directory subnode or null
//     */
//    protected FolderNode getWorkDirNode(String name) throws IOException {
//        final String FILESYSTEMS = JelloBundle.getString("org.netbeans.core.Bundle", "dataSystemName");
//        String path = FILESYSTEMS + ", " + getWorkDirPath() + ", " + name;
//        
//        Explorer explorer = new Explorer();
//        explorer.switchToFilesystemsTab();
//        //TreePath treePath = explorer.getJTreeOperator().findPath(path, ", ");
//        //explorer.getJTreeOperator().expandPath(treePath);
//        
//        return FolderNode.findFolder(FILESYSTEMS + ", " + getWorkDirPath() + ", " + name);
//    }
}
