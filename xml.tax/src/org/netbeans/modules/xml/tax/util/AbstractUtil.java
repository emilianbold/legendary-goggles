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
package org.netbeans.modules.xml.tax.util;

import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * This provides package scope utilities for debugging and code
 * internationalization. It is designed to have a subclass,
 * <code>ErrorManager</code> instance and bundle in each client's package.
 * <p>
 * It ensures localized strings will be loaded from <code>Bundle.properties</code>
 * (branded) in same package as subclass belongs.
 * <p>
 * Debugging methods use {@link org.openide.ErrorManager} to log messages.
 * <code>ErrorManager</code> instance also belongs to sub-class's package.
 *
 * @author     Libor Kramolis
 */
public abstract class AbstractUtil {
    
    /** Cached package name. */
    private String packageName;
    /** Instance package ErrorManager. */
    private ErrorManager packageErrorManager;
    /** Default debug severity used with ErrorManager. */
    private static final int DEBUG_SEVERITY = ErrorManager.INFORMATIONAL;
    
    /**
     * @return package name of this instance
     */
    private final synchronized String getPackageName () {
        if ( packageName == null ) {
            //??? what for classed from default package? -> A: we do not have classes in default package!
            packageName = this.getClass().getPackage().getName().intern();
        }
        return packageName;
    }
    
    
    //
    // String localizing purposes
    //
    

    /** 
     * Get localized string from package bundle.
     * @param key Key identifing localized value.
     * @return localized value.
     */
    public final String getString (String key) {
        if (key == null) throw new NullPointerException();
	return NbBundle.getMessage (this.getClass(), key);
    }
    
    /** 
     * Get localized string from package bundle.
     * @param key Key identifing localized value (<code>MessageFormat</code>).
     * @param param An argument <code>{0}</code> used for message parametrization.
     * @return localized value.
     */
    public final String getString (String key, Object param) {
        if (key == null) throw new NullPointerException();        
	return NbBundle.getMessage (this.getClass(), key, param);
    }
    
    /**
     * Get localized string from package bundle.
     * @param key Key identifing localized value (<code>MessageFormat</code>).
     * @param param1 An argument <code>{0}</code> used for message parametrization.
     * @param param2 An argument <code>{1}</code> used for message parametrization.
     * @return Localized value.
     */
    public final String getString (String key, Object param1, Object param2) {
        if (key == null) throw new NullPointerException();        
	return NbBundle.getMessage (this.getClass(), key, param1, param2);
    }
    
    /** 
     * Get localized character from package bundle. Usually used on mnemonic.
     * @param key Key identifing localized value.
     * @return localized value.
     */
    public final char getChar (String key) {
        if (key == null) throw new NullPointerException();        
	return NbBundle.getMessage (this.getClass(), key).charAt (0);
    }
    
    
    //
    // Debugging purposes
    //
    
    /**
     * Check whether running at loggable level.
     * @return true if <code>debug (...)</code> will log something.
     */
    public final boolean isLoggable () {
        return getErrorManager().isLoggable (DEBUG_SEVERITY);
    }

    /**
     * Log a message if package log level passes.
     * @param message Message to log down. <code>null</code> is allowed
     *        but is not logged.
     */
    public final void debug (String message) {
        if (message == null) return;
        getErrorManager().log (DEBUG_SEVERITY, message);
    }

    /**
     * Always log a exception.
     * @param ex Exception to log down. <code>null</code> is allowed
     *           but is not logged.
     */
    public final void debug (Throwable ex) {
        if (ex == null) return;
        getErrorManager().notify (DEBUG_SEVERITY, ex);
    }

    /**
     * Always log an annotated exception.
     * @param message Message used for exception annotation or <code>null</code>.
     * @param ex Exception to log down. <code>null</code> is allowed
     *        but is not logged.
     */
    public final void debug (String message, Throwable ex) {
        if (ex == null) return;
        if (message != null) {
            ex = getErrorManager().annotate(ex, DEBUG_SEVERITY,  message, null, null, null);
        }
        debug (ex);
    }

    /**
     * Provide an <code>ErrorManager</code> instance named per subclass package.
     * @return ErrorManager which is default for package where is class
     * declared .
     */
    public final synchronized ErrorManager getErrorManager () {
        if ( packageErrorManager == null ) {
            String pack = getPackageName();
            packageErrorManager = ErrorManager.getDefault().getInstance(pack);
        }
        return packageErrorManager;
    }

}
