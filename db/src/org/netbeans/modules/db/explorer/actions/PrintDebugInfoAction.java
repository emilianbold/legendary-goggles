/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package com.netbeans.enterprise.modules.db.explorer.actions;

import java.sql.Connection;
import com.netbeans.ide.*;
import com.netbeans.ide.nodes.*;
import com.netbeans.enterprise.modules.db.explorer.nodes.*;
import com.netbeans.enterprise.modules.db.explorer.infos.*;

public class PrintDebugInfoAction extends DatabaseAction
{
	public void performAction (Node[] activatedNodes) 
	{
		Node node = activatedNodes[0];
		System.out.println("Node info: "+node.getCookie(DatabaseNodeInfo.class));
	}
}