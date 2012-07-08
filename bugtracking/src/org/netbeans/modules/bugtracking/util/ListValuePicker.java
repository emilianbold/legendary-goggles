/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.bugtracking.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import org.openide.util.NbBundle;  

/**
 *
 * @author Tomas Stupka, Jan Stola
 */
public class ListValuePicker extends javax.swing.JPanel {

    public static String getValues(String title, String label, String message, String valuesString, List<String> knownValues) {
        List<ListValue> lv = new ArrayList<ListValue>(knownValues.size());
        for (String s : knownValues) {
            lv.add(new ListValue(s, s));
        }
        return getValues(title, label, message, valuesString, lv.toArray(new ListValue[lv.size()]));
    }
    
    public static String getValues(String title, String label, String message, String valuesString, ListValue[] knownValues) {
        String[] values = valuesString.split(","); // NOI18N
        if(values == null || values.length == 0) {
            return null;
        }

        ListValuePicker vp = new ListValuePicker(label, message, knownValues, values);
        if (BugtrackingUtil.show(vp, title, NbBundle.getMessage(ListValuePicker.class, "LBL_Ok"))) { // NOI18N
            values = vp.getSelectedValues();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                String s = values[i];
                sb.append(s);
                if(i < values.length - 1) {
                    sb.append(", "); // NOI18N    
                }
            }
            return sb.toString();
        }
        return valuesString;
    }
    
    private ListValuePicker(String label, String message, ListValue[] knownValues, String[] toSelect) {
        initComponents();
        
        this.messageLabel.setText(message);
        org.openide.awt.Mnemonics.setLocalizedText(valuesLabel, label); 
        
        valuesList.setCellRenderer(new ListValueRenderer());
        DefaultListModel model = new DefaultListModel();
        for (ListValue lvalue : knownValues) {
            model.addElement(lvalue);
        }
        valuesList.setModel(model);
        int[] selection = new int[toSelect.length];
        for (int i = 0; i < toSelect.length; i++) {
            String s = toSelect[i];
            s = s.trim();
            int idx = getIndex(model, s);
            if(idx == -1 ) {
                idx = getIndex(model, s.toUpperCase());
            }
            if(idx == -1 ) {
                idx = getIndex(model, s.toLowerCase());
            }
            selection[i] = idx;
        }

        valuesList.setSelectedIndices(selection);
        int idx = selection.length > 0 ? selection[0] : -1;
        if(idx > -1) {
            valuesList.scrollRectToVisible(valuesList.getCellBounds(idx, idx));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        valuesList = new javax.swing.JList();
        messageLabel = new javax.swing.JLabel();
        valuesLabel = new javax.swing.JLabel();

        scrollPane.setViewportView(valuesList);
        valuesList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ListValuePicker.class, "ListValuePicker.valuesList.AccessibleContext.accessibleDescription")); // NOI18N

        valuesLabel.setLabelFor(valuesList);
        valuesLabel.setText(org.openide.util.NbBundle.getMessage(ListValuePicker.class, "ListValuePicker.valuesLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(valuesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(valuesLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ListValuePicker.class, "ListValuePicker.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private String[] getSelectedValues() {
        Object[] values = valuesList.getSelectedValues();
        String[] ret = new String[values.length];
        for (int i=0; i < values.length; i++) {
            ret[i] = ((ListValue)values[i]).value;
        }
        return ret;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel messageLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel valuesLabel;
    private javax.swing.JList valuesList;
    // End of variables declaration//GEN-END:variables

    private int getIndex(DefaultListModel model, String s) {
        for (int i = 0; i < model.getSize(); i++) {
            ListValue e = (ListValue) model.getElementAt(i);
            if(e.value.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public static class ListValue {
        private String displayValue;
        private String value;
        public ListValue(String displayValue, String value) {
            this.displayValue = displayValue;
            this.value = value;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ListValue other = (ListValue) obj;
            if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
                return false;
            }
            return true;
        }
        
    }

    private class ListValueRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(value instanceof ListValue) {
                ListValue lv = (ListValue) value;
                return super.getListCellRendererComponent(list, lv.displayValue, index, isSelected, cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        
    }
}
