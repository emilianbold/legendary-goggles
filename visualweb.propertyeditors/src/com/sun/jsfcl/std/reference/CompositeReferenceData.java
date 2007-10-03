/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.sun.jsfcl.std.reference;

import java.util.List;

/**
 * @author eric
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CompositeReferenceData extends ReferenceData {

    protected BaseReferenceData baseReferenceData;
    protected List itemsSorted;
    protected ProjectAttachedReferenceData projectReferenceData;
    protected Object projectVersionMarker;
    protected DesignPropertyAttachedReferenceData livePropertyReferenceData;

    /**
     * @param manager
     * @param definer
     */
    public CompositeReferenceData(
        ReferenceDataManager manager,
        String name,
        ReferenceDataDefiner definer,
        BaseReferenceData baseReferenceData,
        ProjectAttachedReferenceData projectReferenceData,
        DesignPropertyAttachedReferenceData livePropertyReferenceData) {

        super(manager, definer, name);
        this.name = name;
        this.baseReferenceData = baseReferenceData;
        this.projectReferenceData = projectReferenceData;
        if (projectReferenceData != null) {
            definer = projectReferenceData.getDefiner();
        }
        this.livePropertyReferenceData = livePropertyReferenceData;
    }

    public void add(ReferenceDataItem item) {

        if (projectReferenceData != null) {
            projectReferenceData.add(item);
            invalidateItemsCache();
        }
    }

    public boolean canAddRemoveItems() {

        if (projectReferenceData != null && projectReferenceData.canAddRemoveItems()) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.jsfcl.std.reference.ReferenceData#addToItems(java.util.List)
     */
    protected void defineItems() {

        if (baseReferenceData != null) {
            items.addAll(baseReferenceData.getItems());
        }
        if (projectReferenceData != null) {
            items.addAll(projectReferenceData.getItems());
        }
        if (livePropertyReferenceData != null) {
            items.addAll(livePropertyReferenceData.getItems());
        }
    }

    public List getItems() {

        if (items != null && projectReferenceData != null &&
            projectReferenceData.getVersionMarker() != projectVersionMarker) {
            invalidateItemsCache();
            projectVersionMarker = projectReferenceData.getVersionMarker();
        }
        return super.getItems();
    }

    public List getItemsSorted() {

        if (itemsSorted != null && projectReferenceData != null &&
            projectReferenceData.getVersionMarker() != projectVersionMarker) {
            invalidateItemsCache();
        }
        if (itemsSorted == null) {
            itemsSorted = ReferenceDataItem.sorted(getItems());
        }
        return itemsSorted;
    }

    public void invalidateItemsCache() {

        super.invalidateItemsCache();
        itemsSorted = null;
    }

    public void invalidateDesignContextRelatedCaches() {

        if (livePropertyReferenceData != null) {
            livePropertyReferenceData.invalidateItemsCache();
            invalidateItemsCache();
        }
    }

    public void remove(ReferenceDataItem item) {

        if (projectReferenceData != null) {
            projectReferenceData.remove(item);
            invalidateItemsCache();
        }
    }

}
