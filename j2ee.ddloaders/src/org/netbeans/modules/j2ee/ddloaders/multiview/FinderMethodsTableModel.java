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

package org.netbeans.modules.j2ee.ddloaders.multiview;

import org.netbeans.modules.j2ee.dd.api.ejb.Query;
import org.netbeans.modules.j2ee.ejbjarproject.ui.customizer.QueryCustomizer;
import org.openide.src.MethodElement;

/**
 * @author pfiala
 */
class FinderMethodsTableModel extends QueryMethodsTableModel {

    protected static final String[] COLUMN_NAMES = {Utils.getBundleMessage("LBL_Method"),
                                                    Utils.getBundleMessage("LBL_ReturnsCollection"),
                                                    Utils.getBundleMessage("LBL_ResultInterface"),
                                                    Utils.getBundleMessage("LBL_Query"),
                                                    Utils.getBundleMessage("LBL_Description")};
    protected static final int[] COLUMN_WIDTHS = new int[]{200, 100, 120, 200, 100};

    public FinderMethodsTableModel(EntityHelper.Queries queries) {
        super(COLUMN_NAMES, COLUMN_WIDTHS, queries);
    }

    public void editRow(int row) {
        QueryMethodHelper helper = getQueryMethodHelper(row);
        boolean hasLocal = queries.getLocal() != null;
        boolean hasRemote = queries.getRemote() != null;
        boolean hasLocalMethod = helper.localMethod != null;
        boolean hasRemoteMethod = helper.remoteMethod != null;
        boolean returnsCollection = helper.returnsCollection();
        QueryCustomizer customizer = new QueryCustomizer();
        MethodElement methodElement = (MethodElement) helper.getPrototypeMethod().clone();
        Query aQuery = (Query) queries.getFinderMethod(row).clone();
        boolean result = customizer.showFinderCustomizer(methodElement, aQuery, returnsCollection,
                hasLocal, hasRemote, hasLocalMethod, hasRemoteMethod);
        if (result) {
            helper.updateFinderMethod(methodElement, aQuery, customizer.finderReturnIsSingle(),
                    customizer.publishToLocal(), customizer.publishToRemote());
            //fireTableRowsUpdated(row, row);
        }
    }

    public int addRow() {
        queries.addFinderMethod();
        //fireTableRowsInserted(-1, -1);
        return getRowCount() - 1;
    }

    public QueryMethodHelper getQueryMethodHelper(int row) {
        return queries.getFinderMethodHelper(row);
    }

    public int getRowCount() {
        return queries.getFinderMethodCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        QueryMethodHelper queryMethodHelper = getQueryMethodHelper(rowIndex);
        switch (columnIndex) {
            case 0:
                return queryMethodHelper.getQueryMethod().getMethodName();
            case 1:
                return new Boolean(queryMethodHelper.returnsCollection());
            case 2:
                return queryMethodHelper.getResultInterface();
            case 3:
                return queryMethodHelper.getEjbQl();
            case 4:
                return queryMethodHelper.getDefaultDescription();
        }
        return null;
    }

    public Class getColumnClass(int columnIndex) {
        return columnIndex == 1 ? Boolean.class : String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return super.isCellEditable(rowIndex, columnIndex);
    }
}
