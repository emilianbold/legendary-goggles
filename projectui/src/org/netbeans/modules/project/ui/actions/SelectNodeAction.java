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

package org.netbeans.modules.project.ui.actions;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.modules.project.ui.ProjectTab;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Action sensitive to current project
 * 
 * @author Pet Hrebejk 
 */
public class SelectNodeAction extends LookupSensitiveAction {
    
    // XXX Better icons
    private static final Icon SELECT_IN_PROJECTS_ICON = new ImageIcon( Utilities.loadImage( "org/netbeans/modules/project/ui/resources/projectTab.gif" ) ); //NOI18N
    private static final Icon SELECT_IN_FILES_ICON = new ImageIcon( Utilities.loadImage( "org/netbeans/modules/project/ui/resources/filesTab.gif" ) ); //NOI18N
    
    private static final String SELECT_IN_PROJECTS_NAME = NbBundle.getMessage( CloseProject.class, "LBL_SelectInProjectsAction_Name" ); // NOI18N
    private static final String SELECT_IN_FILES_NAME = NbBundle.getMessage( CloseProject.class, "LBL_SelectInFilesAction_Name" ); // NOI18N
    
    private String command;
    private ProjectActionPerformer performer;
    private String namePattern;
    
    private String findIn;
    
    public static Action inProjects() {
        SelectNodeAction a = new SelectNodeAction( SELECT_IN_PROJECTS_ICON, SELECT_IN_PROJECTS_NAME );
        a.findIn = ProjectTab.ID_LOGICAL;
        return a;
    }
    
    public static Action inFiles() {
        SelectNodeAction a = new SelectNodeAction( SELECT_IN_FILES_ICON, SELECT_IN_FILES_NAME );
        a.findIn = ProjectTab.ID_PHYSICAL;
        return a;
    }
    
    /** 
     * Constructor for global actions. E.g. actions in main menu which 
     * listen to the global context.
     *
     */
    public SelectNodeAction( Icon icon, String name ) {
        super( icon, null, new Class[] { DataObject.class } );
        this.setDisplayName( name );
    }
    
    private SelectNodeAction(String command, ProjectActionPerformer performer, String namePattern, Icon icon, Lookup lookup) {
        super( icon, lookup, new Class[] { Project.class, DataObject.class } );
        this.command = command;
        this.performer = performer;
        this.namePattern = namePattern;
        refresh( getLookup() );
    }
       
    protected void actionPerformed( Lookup context ) {        
        FileObject fo = getFileFromLookup( context );
        if ( fo == null ) {
            return;
        }
        ProjectTab pt  = ProjectTab.findDefault( findIn );
        pt.selectNode( fo );        
    }
    
    protected void refresh( Lookup context ) {        
        FileObject fo = getFileFromLookup( context );
        setEnabled( fo != null );        
    }
    
    protected final String getCommand() {
        return command;
    }
    
    protected final String getNamePattern() {
        return namePattern;
    }
    
    // Private methods ---------------------------------------------------------
    
    private FileObject getFileFromLookup( Lookup context ) {
        
        DataObject dobj = (DataObject)context.lookup( DataObject.class );
        
        return dobj == null ? null : dobj.getPrimaryFile();
    }
    
}