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


package org.netbeans.modules.i18n.wizard;


import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.i18n.*;

import org.netbeans.modules.i18n.I18nSupport;

import org.openide.loaders.DataObject;


/**
 * Object representing source dependent i18n data passed to i18n wizard 
 * descriptor and its panels via readSettings and storeSettings methods.
 * It's the "value part" of <code>Map</code> (keyed by DataObject)
 * passed as settings for wizard descriptor and to individual panels.
 *
 * @author  Peter Zavadsky
 * @see ResourceWizardPanel where lifecycle begins.
 * @see org.openide.WizardDescriptor
 * @see org.openide.WizardDescriptor.Panel#readSettings
 * @see org.openide.WizardDecritptor.Panel#storeSettings
 */
final class SourceData {

    /** Resource where to put i18n string */
    private DataObject resource;

    /** Support used by i18n-zing. */
    private I18nSupport support;

    /** Mapping found hard coded strings to i18n strings. */
    private Map stringMap = new java.util.TreeMap(new HardStringComparator());
    
    private static class HardStringComparator implements java.util.Comparator {
        public int compare(Object obj, Object obj1) {
            HardCodedString hcs1 = (HardCodedString)obj;
            HardCodedString hcs2 = (HardCodedString)obj1;
            return hcs1.getStartPosition().getOffset() - 
                    hcs2.getStartPosition().getOffset();
        }
     }
    
    /** Hard coded strings user selected to non-proceed. */
    private Set removedStrings;

    
    /** Constructor. */
    public SourceData(DataObject resource) {
        this.resource = resource;
    }

    /** Constructor. */
    public SourceData(DataObject resource, I18nSupport support) {
        this.resource = resource;
        this.support = support;
        
        support.getResourceHolder().setResource(resource);
    }


    /** Getter for <code>resource</code> property. */
    public DataObject getResource() {
        return resource;
    }

    /** Getter for <code>resource</code> property. */
    public I18nSupport getSupport() {
        return support;
    }

    /** Getter for <code>stringMap</code> property. */
    public Map getStringMap() {
        return stringMap;
    }
    
    /** Setter for <code>stringMap</code> prtoperty. */
    public void setStringMap(Map stringMap) {
        this.stringMap.clear();
        this.stringMap.putAll(stringMap);
    }
    
    /** Getter for <code>removedStrings</code> property. */
    public Set getRemovedStrings() {
        return removedStrings;
    }
    
    /** Setter for <code>removedStrings</code> property. */
    public void setRemovedStrings(Set removedStrings) {
        this.removedStrings = removedStrings;
    }
    
}
