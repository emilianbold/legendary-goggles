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

package org.netbeans.core;

import org.openide.*;
import org.openide.loaders.*;
import org.openide.options.*;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.util.HelpCtx;
import org.openide.util.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.modules.ManifestSection.NodeSection;


import org.netbeans.core.actions.*;

/** This object represents environment settings in the Corona system.
* This class is final only for performance purposes.
* Can be unfinaled if desired.
*
* @author Petr Hamernik, Dafe Simonek
*/
final class EnvironmentNode extends AbstractNode {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 4782447107972624693L;
    /** name of section to filter */
    private String filter;
    /** icon base for icons of this node */
    private static final String EN_ICON_BASE = "/org/netbeans/core/resources/"; // NOI18N
    /** map between type of node and the parent node for this type (String, Node) */
    private static java.util.HashMap types = new java.util.HashMap (11);
    

    /** Constructor */
    private EnvironmentNode (String filter) {
        super (new NbPlaces.Ch (filter));

        this.filter = filter;
        
        String resourceName = "CTL_" + filter + "_name"; // NOI18N
        String iconBase = EN_ICON_BASE + filter.toLowerCase ();
        
        setName(NbBundle.getMessage (EnvironmentNode.class, resourceName));
        setIconBase(iconBase);
    }
    
    /** Finds the node for given name.
     */
    public static EnvironmentNode find (String name) {
         EnvironmentNode n = (EnvironmentNode)types.get (name);
         if (n == null) {
             n = new EnvironmentNode (name);
             types.put (name, n);
         }
         return n;
    }
    

    public HelpCtx getHelpCtx () {
        return new HelpCtx (EnvironmentNode.class);
    }

    /** Getter for set of actions that should be present in the
    * popup menu of this node. This set is used in construction of
    * menu returned from getContextMenu and specially when a menu for
    * more nodes is constructed.
    *
    * @return array of system actions that should be in popup menu
    */
    public SystemAction[] createActions () {
        return new SystemAction[] {
                   SystemAction.get(ToolsAction.class),
                   SystemAction.get(PropertiesAction.class)
               };
    }

    /** For deserialization */
    public Node.Handle getHandle () {
        return new EnvironmentHandle (filter);
    }

    static final class EnvironmentHandle implements Node.Handle {
        static final long serialVersionUID =-850350968366553370L;
        
        /** field */
        private String filter;
        
        /** constructor */
        public EnvironmentHandle (String filter) {
            this.filter = filter;
        }
        public Node getNode () {
            String f = filter;
            if (f == null) {
                // use the original node
                f = NodeSection.TYPE_ENVIRONMENT;
            }
            
            return find (f);
        }
    }
}

/*
 * Log
 *  23   Gandalf   1.22        1/16/00  Ian Formanek    Removed semicolons after
 *       methods body to prevent fastjavac from complaining
 *  22   Gandalf   1.21        1/13/00  Jaroslav Tulach I18N
 *  21   Gandalf   1.20        10/22/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  20   Gandalf   1.19        8/9/99   Ian Formanek    Generated Serial Version
 *       UID
 *  19   Gandalf   1.18        7/30/99  David Simonek   again serialization of 
 *       nodes repaired
 *  18   Gandalf   1.17        7/30/99  David Simonek   serialization fixes
 *  17   Gandalf   1.16        7/8/99   Jesse Glick     Context help.
 *  16   Gandalf   1.15        6/9/99   Ian Formanek    ToolsAction
 *  15   Gandalf   1.14        6/8/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  14   Gandalf   1.13        5/9/99   Ian Formanek    Fixed bug 1655 - 
 *       Renaming of top level nodes is not persistent (removed the possibility 
 *       to rename).
 *  13   Gandalf   1.12        3/28/99  David Simonek   menu support improved 
 *       (icons, actions...)
 *  12   Gandalf   1.11        3/26/99  Jaroslav Tulach 
 *  11   Gandalf   1.10        3/26/99  Ian Formanek    Fixed use of obsoleted 
 *       NbBundle.getBundle (this)
 *  10   Gandalf   1.9         3/18/99  Jaroslav Tulach 
 *  9    Gandalf   1.8         2/25/99  Jaroslav Tulach Change of clipboard 
 *       management  
 *  8    Gandalf   1.7         2/12/99  Ian Formanek    Reflected renaming 
 *       Desktop -> Workspace
 *  7    Gandalf   1.6         1/25/99  Jaroslav Tulach Added default project, 
 *       its desktop and changed default explorer in Main.
 *  6    Gandalf   1.5         1/20/99  Jaroslav Tulach 
 *  5    Gandalf   1.4         1/7/99   Ian Formanek    
 *  4    Gandalf   1.3         1/7/99   Ian Formanek    fixed resource names
 *  3    Gandalf   1.2         1/6/99   Ian Formanek    Fixed outerclass 
 *       specifiers uncompilable under JDK 1.2
 *  2    Gandalf   1.1         1/6/99   Jaroslav Tulach ide.* extended to 
 *       ide.loaders.*
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
