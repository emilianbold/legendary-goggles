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

package org.netbeans.spi.project.ui.support;

import javax.swing.Action;
import javax.swing.Icon;
import org.netbeans.modules.project.uiapi.Utilities;

/**
 * Factory for creating file-sensitive actions.
 * @author Petr Hrebejk
 */
public class FileSensitiveActions {
    
    private FileSensitiveActions() {}
        
    /**
     * Creates an action sensitive to the set of currently selected files.
     * When performed the action will call the given command on the {@link ActionProvider} of
     * the selected project(s) and pass the proper context to it. Enablement of the
     * action depends on the behavior of the project's action provider.<BR>
     * Shortcuts for actions are shared according to command, i.e. actions based on the same command
     * will have the same shortcut.
     * @param command the command which should be invoked when the action is
     *        performed
     * @param namePattern pattern which should be used for determining the action's
     *        name (label). It takes two parameters a la {@link MessageFormat}: <code>{0}</code> - number of selected projects;
     *        <code>{1}</code> - name of the first project.
     * @param icon icon of the action (or null)
     */    
    public static Action fileCommandAction( String command, String namePattern, Icon icon ) {
        return Utilities.getActionsFactory().fileCommandAction( command, namePattern, icon );
    }
    
}
