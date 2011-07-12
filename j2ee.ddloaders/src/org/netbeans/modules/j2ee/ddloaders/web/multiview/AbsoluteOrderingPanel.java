/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.j2ee.ddloaders.web.multiview;

import java.awt.event.ItemEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.netbeans.modules.j2ee.dd.api.common.VersionNotSupportedException;
import org.netbeans.modules.j2ee.dd.api.web.AbsoluteOrdering;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.ddloaders.web.DDDataObject;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * @author Petr Slechta
 */
public class AbsoluteOrderingPanel extends SectionInnerPanel implements java.awt.event.ItemListener {
    private DDDataObject dObj;
    private WebApp webApp;
    private DefaultListModel listModel;

    public AbsoluteOrderingPanel(SectionView sectionView, DDDataObject dObj) {
        super(sectionView);
        this.dObj = dObj;
        webApp = dObj.getWebApp();

        initComponents();
        listModel = new DefaultListModel();
        listOrdering.setModel(listModel);
        try {
            for (AbsoluteOrdering ordering : webApp.getAbsoluteOrdering()) {
                for (String name : ordering.getName()) {
                    listModel.addElement(name);
                }
            }
        }
        catch (VersionNotSupportedException e) {
            // ignore
        }
    }
    
    public javax.swing.JComponent getErrorComponent(String errorId) {
        return null;
    }

    public void setValue(javax.swing.JComponent source, Object value) {
    }
    
    public void linkButtonPressed(Object obj, String id) {
    }

    public void itemStateChanged(ItemEvent e) {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listOrdering = new javax.swing.JList();
        bAdd = new javax.swing.JButton();
        bEdit = new javax.swing.JButton();
        bRemove = new javax.swing.JButton();
        bUp = new javax.swing.JButton();
        bDown = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(250, 150));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AbsoluteOrderingPanel.class, "LBL_AbsoluteOrder")); // NOI18N

        listOrdering.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(listOrdering);

        org.openide.awt.Mnemonics.setLocalizedText(bAdd, org.openide.util.NbBundle.getMessage(AbsoluteOrderingPanel.class, "BTN_Add")); // NOI18N
        bAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bEdit, org.openide.util.NbBundle.getMessage(AbsoluteOrderingPanel.class, "BTN_Edit")); // NOI18N
        bEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEditActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bRemove, org.openide.util.NbBundle.getMessage(AbsoluteOrderingPanel.class, "BTN_Remove")); // NOI18N
        bRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bUp, org.openide.util.NbBundle.getMessage(AbsoluteOrderingPanel.class, "BTN_Up")); // NOI18N
        bUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUpActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bDown, org.openide.util.NbBundle.getMessage(AbsoluteOrderingPanel.class, "BTN_Down")); // NOI18N
        bDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDownActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(92, 92, 92))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bDown))
                    .addComponent(jScrollPane2))
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddActionPerformed
        String name = getNameFromUser("");
        if (name != null) {
            listModel.addElement(name);
            refreshDdModel();
        }
    }//GEN-LAST:event_bAddActionPerformed

    private void bEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEditActionPerformed
        int x = listOrdering.getSelectedIndex();
        if (x >= 0) {
            String name = getNameFromUser((String)listModel.get(x));
            if (name != null) {
                listModel.set(x, name);
                refreshDdModel();
            }
        }
    }//GEN-LAST:event_bEditActionPerformed

    private void bRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveActionPerformed
        int x = listOrdering.getSelectedIndex();
        if (x >= 0) {
            listModel.remove(x);
            refreshDdModel();
        }
    }//GEN-LAST:event_bRemoveActionPerformed

    private void bUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUpActionPerformed
        int x = listOrdering.getSelectedIndex();
        if (x > 0) {
            exchange(x-1);
            listOrdering.setSelectedIndex(x-1);
            refreshDdModel();
        }
    }//GEN-LAST:event_bUpActionPerformed

    private void bDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDownActionPerformed
        int x = listOrdering.getSelectedIndex();
        if (x >= 0 && x < listModel.size()-1) {
            exchange(x);
            listOrdering.setSelectedIndex(x+1);
            refreshDdModel();            
        }
    }//GEN-LAST:event_bDownActionPerformed

    private String getNameFromUser(String value) {
        OrderingItemPanel p = new OrderingItemPanel(value);
        DialogDescriptor dd = new DialogDescriptor(p,
                NbBundle.getMessage(RelativeOrderingPanel.class, "TTL_Ordering"));
        dd.createNotificationLineSupport();
        p.setDlgSupport(dd);
        dd.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            return p.getResult();
        }
        return null;
    }

    private void exchange(int x) {
        Object tmp1 = listModel.get(x);
        Object tmp2 = listModel.get(x+1);
        listModel.set(x, tmp2);
        listModel.set(x+1, tmp1);
    }
    
    private void refreshDdModel() {
        try {
            AbsoluteOrdering ordering = webApp.newAbsoluteOrdering();
            AbsoluteOrdering[] orderings = new AbsoluteOrdering[1];
            String[] items = new String[listModel.size()];
            for (int i=0,maxi=listModel.size(); i<maxi; i++) {
                items[i] = (String)listModel.get(i);
            }
            ordering.setName(items);
            orderings[0] = ordering;
            webApp.setAbsoluteOrdering(orderings);
            dObj.modelUpdatedFromUI();
        }
        catch (VersionNotSupportedException e) {
            Logger.global.log(Level.SEVERE, "refresh of DD model failed", e);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAdd;
    private javax.swing.JButton bDown;
    private javax.swing.JButton bEdit;
    private javax.swing.JButton bRemove;
    private javax.swing.JButton bUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList listOrdering;
    // End of variables declaration//GEN-END:variables
 
}
