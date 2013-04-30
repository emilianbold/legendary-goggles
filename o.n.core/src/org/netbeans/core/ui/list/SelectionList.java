/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.core.ui.list;

import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/**
 *
 * @author S. Aubrecht
 */
public final class SelectionList {

    private final SelectionListImpl theList;

    SelectionList() {
        theList = new SelectionListImpl();
    }

    public JComponent getComponent() {
        return theList;
    }

    public void setItems( List<? extends ListItem> items ) {
        final List<ListItem> listItems = Collections.unmodifiableList( items );
        setItems( new AbstractListModel<ListItem>() {

            @Override
            public int getSize() {
                return listItems.size();
            }

            @Override
            public ListItem getElementAt( int index ) {
                return listItems.get( index );
            }
        });
    }

    public void setItems( ListModel<ListItem> items ) {
        ListItem selItem = theList.getSelectedValue();
        theList.setModel( items );
        if( null != selItem ) {
            setSelectedItem( selItem );
        }
    }

//    public void setMaximumVisibleItems( int maxVisible ) {
//
//    }

//    public void setMaximumWidth( int maxWidth ) {
//
//    }

    ListSelectionModel getSelectionModel() {
        return theList.getSelectionModel();
    }

    void clearSelection() {
        theList.clearSelection();
    }

    ListItem getSelectedItem() {
        return theList.getSelectedValue();
    }

    /**
     * Attempts to select the given item in this list.
     * @param item Item to select.
     * @return True if this list contains the given item, false otherwise.
     */
    boolean setSelectedItem( ListItem item ) {
        ListModel<ListItem> model = theList.getModel();
        for( int i=0; i<model.getSize(); i++ ) {
            if( item.equals( model.getElementAt( i )  ) ) {
                theList.setSelectedIndex( i );
                return true;
            }
        }
        return false;
    }
}
