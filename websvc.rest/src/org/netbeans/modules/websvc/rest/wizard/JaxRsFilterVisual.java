/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.websvc.rest.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.project.Project;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author den
 */
class JaxRsFilterVisual extends javax.swing.JPanel {

    private static final long serialVersionUID = -3816981483676324257L;

    JaxRsFilterVisual(WizardDescriptor descriptor) {
        initComponents();
        listeners = new CopyOnWriteArrayList<ChangeListener>();
        
        Project project = Templates.getProject(descriptor);
        WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
        if ( webModule == null ){
            containerFilter.setEnabled(false);
        }
        else {
            containerFilter.setSelected(true);
            provider.setEnabled(true);
        }
        requestFilter.setSelected(true);
        responseFilter.setSelected(true);
        
        ActionListener listener = new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent event ) {
                fireChangeEvent();
            }
        };
        containerFilter.addActionListener(listener);
        clientFilter.addActionListener(listener);
        requestFilter.addActionListener(listener);
        responseFilter.addActionListener(listener);
        provider.addActionListener(listener);
    }
    
    String getError() {
        if ( clientFilter.isSelected() && !containerFilter.isSelected() &&
                provider.isSelected())
        {
            return NbBundle.getMessage(JaxRsFilterVisual.class, "ERR_ClientProvider");  // NOI18N
        }
        if ( !clientFilter.isSelected() && !containerFilter.isSelected()){
            return NbBundle.getMessage(JaxRsFilterVisual.class, "ERR_NoFilterRoleType");  // NOI18N
        }
        if ( !requestFilter.isSelected() && !responseFilter.isSelected() ){
            return NbBundle.getMessage(JaxRsFilterVisual.class, "ERR_NoFilterType");  // NOI18N
        }
        return null;
    }

    void readSettings( WizardDescriptor descriptor ) {
    }

    void storeSettings( WizardDescriptor descriptor ) {
        descriptor.putProperty(JaxRsFilterPanel.CLIENT_FILTER,clientFilter.isSelected());
        descriptor.putProperty(JaxRsFilterPanel.SERVER_FILTER,containerFilter.isSelected());
        descriptor.putProperty(JaxRsFilterPanel.REQUEST, requestFilter.isSelected());
        descriptor.putProperty(JaxRsFilterPanel.RESPONSE, responseFilter.isSelected());
        descriptor.putProperty(JaxRsFilterPanel.PRE_MATCHING, preMatching.isSelected());
        descriptor.putProperty(JaxRsFilterPanel.PROVIDER, provider.isSelected());
    }
    
    void addChangeListener( ChangeListener listener ) {
        listeners.add(listener);        
    }
    
    void removeChangeListener( ChangeListener listener ) {
        listeners.remove(listener);        
    }
    
    private void fireChangeEvent(){
        ChangeEvent event = new ChangeEvent(this);
        for(ChangeListener listener :listeners ){
            listener.stateChanged(event);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filterTypeLbl = new javax.swing.JLabel();
        filterPanel = new javax.swing.JPanel();
        requestFilter = new javax.swing.JCheckBox();
        responseFilter = new javax.swing.JCheckBox();
        clientFilter = new javax.swing.JCheckBox();
        containerFilter = new javax.swing.JCheckBox();
        provider = new javax.swing.JCheckBox();
        preMatching = new javax.swing.JCheckBox();

        filterTypeLbl.setLabelFor(filterPanel);
        org.openide.awt.Mnemonics.setLocalizedText(filterTypeLbl, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_FilterType")); // NOI18N

        filterPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(requestFilter, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_RequestFilter")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(responseFilter, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_ResponseFilter")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(clientFilter, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_ClientFilter")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(containerFilter, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_ContainerFilter")); // NOI18N

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(requestFilter)
                    .addComponent(responseFilter))
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filterPanelLayout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(clientFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filterPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(containerFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(requestFilter)
                    .addComponent(containerFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(responseFilter)
                    .addComponent(clientFilter))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        requestFilter.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_RequestFilter")); // NOI18N
        requestFilter.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_RequestFilter")); // NOI18N
        responseFilter.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_ResponseFilter")); // NOI18N
        responseFilter.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_ResponseFilter")); // NOI18N
        clientFilter.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_ClientFilter")); // NOI18N
        clientFilter.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_ClientFilter")); // NOI18N
        containerFilter.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_ContainerFilter")); // NOI18N
        containerFilter.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_ContainerFilter")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(provider, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_Provider")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(preMatching, org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "LBL_PreMatching")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preMatching)
                            .addComponent(provider)
                            .addComponent(filterTypeLbl))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterTypeLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(provider)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(preMatching)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        filterTypeLbl.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_FilterType")); // NOI18N
        filterTypeLbl.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_FilterType")); // NOI18N
        filterPanel.getAccessibleContext().setAccessibleName(filterTypeLbl.getAccessibleContext().getAccessibleName());
        filterPanel.getAccessibleContext().setAccessibleDescription(filterTypeLbl.getAccessibleContext().getAccessibleDescription());
        provider.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_Provider")); // NOI18N
        provider.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_Provider")); // NOI18N
        preMatching.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSN_PreMatching")); // NOI18N
        preMatching.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JaxRsFilterVisual.class, "ACSD_PreMatching")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox clientFilter;
    private javax.swing.JCheckBox containerFilter;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel filterTypeLbl;
    private javax.swing.JCheckBox preMatching;
    private javax.swing.JCheckBox provider;
    private javax.swing.JCheckBox requestFilter;
    private javax.swing.JCheckBox responseFilter;
    // End of variables declaration//GEN-END:variables
    
    private List<ChangeListener> listeners;

}