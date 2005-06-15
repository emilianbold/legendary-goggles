/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 2004-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jmx.test.helpers;

/**
 *
 * @author an156382
 */
public class NotificationType {
    
    private String type = "";
    
    public NotificationType(String type) {
        this.type = type;
    }
    
    /**
     * Method which returns a type of the notification
     * @return type a type of the notification
     *
     */
    public String getNotificationType() {
        return type;
    }
}
