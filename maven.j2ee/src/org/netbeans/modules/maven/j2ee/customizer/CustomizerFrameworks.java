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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.j2ee.customizer;
import java.awt.Component;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerInstance;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.maven.j2ee.utils.LoggingUtils;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.api.webmodule.WebFrameworks;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;

/**
 * 
 * @author mkleint
 */
public class CustomizerFrameworks extends JPanel implements ApplyChangesCustomizer, ListSelectionListener {

    private static final RequestProcessor RP = new RequestProcessor(CustomizerFrameworks.class.getName());

    private final ProjectCustomizer.Category category;
    private final Project project;
    
    private final List<WebModuleExtender> newExtenders = new LinkedList<WebModuleExtender>();
    private final List<WebModuleExtender> existingExtenders = new LinkedList<WebModuleExtender>();
    private final List<WebFrameworkProvider> usedFrameworks = new LinkedList<WebFrameworkProvider>();
    private final Map<WebFrameworkProvider, WebModuleExtender> extenders = new IdentityHashMap<WebFrameworkProvider, WebModuleExtender>();
    private final List<WebFrameworkProvider> addedFrameworks = new LinkedList<WebFrameworkProvider>();

    private final ExtenderController controller = ExtenderController.create();
    
    
    public CustomizerFrameworks(ProjectCustomizer.Category category, Project project) {
        this.category = category;
        this.project = project;
        
        initComponents();
        initFrameworksList();
        
        btnRemoveAdded.setEnabled(false);
        jListFrameworks.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                WebFrameworkProvider prov = (WebFrameworkProvider)value;
                Component toRet = super.getListCellRendererComponent(list, prov.getName(), index, isSelected, cellHasFocus);
                if (toRet instanceof JLabel) {
                    JLabel lbl = (JLabel)toRet;
                    if (addedFrameworks.contains(prov)) {
                        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    } else {
                        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return toRet;
            }
        });
    }

    @Override
    public void applyChanges() {
        doUIandUsageLogging();

        // see issue #211768 for more details.
        handleExtenders();

        existingExtenders.clear();
    }

    @Override
    public void applyChangesInAWT() {
    }
    
    private void doUIandUsageLogging() {
        if ((addedFrameworks != null) && (addedFrameworks.size() > 0)) {
            LoggingUtils.logUI(this.getClass(), "UI_PROJECT_CONFIG_MAVEN_FRAMEWORK_ADDED", addedFrameworks.toArray(), "web.project");  //NOI18N
            LoggingUtils.logUsage(this.getClass(), "USG_PROJECT_CONFIG_WEB", new Object[] { findServerName(), addedFrameworks.toArray()}, "web.project");  //NOI18N
        }
    }
    
    private String findServerName() {
        J2eeModuleProvider provider = project.getLookup().lookup(J2eeModuleProvider.class);
        
        if (provider != null) {
            ServerInstance si = Deployment.getDefault().getServerInstance(provider.getServerInstanceID());
            try {
                return si.getDisplayName();
            } catch (InstanceRemovedException ex) {
                return null;
            }
        }
        return null;
    }
    
    private void initFrameworksList() {
        final WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
        existingExtenders.clear();
        
        ExtenderController.Properties properties = controller.getProperties();
        String j2eeVersion = webModule.getJ2eePlatformVersion();
        properties.setProperty("j2eeLevel", j2eeVersion); // NOI18N
        properties.setProperty("maven", Boolean.TRUE);  //NOI18N
        J2eeModuleProvider provider = project.getLookup().lookup(J2eeModuleProvider.class);
        if (provider != null) {
            String serverInstanceID = provider.getServerInstanceID();
            if (serverInstanceID != null && !"".equals(serverInstanceID)) {
                properties.setProperty("serverInstanceID", serverInstanceID);   //NOI18N
            }
        }

        jListFrameworks.setModel(new DefaultListModel());
        jListFrameworks.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListFrameworks.addListSelectionListener(this);

        loadFrameworks();
    }
    
    private void loadFrameworks() {
        final WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
        final Task task = createLoadFrameworksTask(webModule);

        task.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        final DefaultListModel model = (DefaultListModel) jListFrameworks.getModel();
                        for (WebFrameworkProvider framework : usedFrameworks) {
                            model.addElement(framework);
                        }

                        if (usedFrameworks.size() > 0) {
                            jListFrameworks.setSelectedIndex(0);
                        }
                        if (WebFrameworks.getFrameworks().size() == jListFrameworks.getModel().getSize()) {
                            jButtonAdd.setEnabled(false);
                        }
                    }
                });
            }
        });
    }

    @NbBundle.Messages({"CustomizerFrameworks.label.loading.frameworks=Loading framework list..."})
    private Task createLoadFrameworksTask(final WebModule webModule) {
        return RP.post(new Runnable() {

            @Override
            public void run() {
                ProgressUtils.showProgressDialogAndRun(new Runnable() {

                    @Override
                    public void run() {
                        for (WebFrameworkProvider framework : WebFrameworks.getFrameworks()) {
                            if (framework.isInWebModule(webModule)) {
                                usedFrameworks.add(framework);
                                WebModuleExtender extender = framework.createWebModuleExtender(webModule, controller);
                                extenders.put(framework, extender);
                                existingExtenders.add(extender);
                                extender.addChangeListener(new ExtenderListener(extender));
                            }
                        }
                    }
                }, Bundle.CustomizerFrameworks_label_loading_frameworks());
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelFrameworks = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListFrameworks = new javax.swing.JList();
        jButtonAdd = new javax.swing.JButton();
        btnRemoveAdded = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelConfig = new javax.swing.JPanel();
        jLabelConfig = new javax.swing.JLabel();

        jLabelFrameworks.setLabelFor(jListFrameworks);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelFrameworks, org.openide.util.NbBundle.getMessage(CustomizerFrameworks.class, "LBL_UsedFrameworks")); // NOI18N

        jScrollPane1.setViewportView(jListFrameworks);
        jListFrameworks.getAccessibleContext().setAccessibleDescription("Used Frameworks");

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAdd, org.openide.util.NbBundle.getMessage(CustomizerFrameworks.class, "LBL_AddFramework")); // NOI18N
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnRemoveAdded, org.openide.util.NbBundle.getMessage(CustomizerFrameworks.class, "BTN_Remove")); // NOI18N
        btnRemoveAdded.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAddedActionPerformed(evt);
            }
        });

        jPanelConfig.setLayout(new java.awt.GridBagLayout());

        jLabelConfig.setLabelFor(jPanelConfig);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFrameworks)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonAdd)
                    .addComponent(btnRemoveAdded)))
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanelConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnRemoveAdded, jButtonAdd});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelFrameworks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveAdded))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );

        jButtonAdd.getAccessibleContext().setAccessibleDescription("Add Framework");
        btnRemoveAdded.getAccessibleContext().setAccessibleDescription("Remove framework");
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        AddFrameworkPanel panel = new AddFrameworkPanel(usedFrameworks);
        javax.swing.JPanel inner = new javax.swing.JPanel();
        inner.setLayout(new java.awt.GridBagLayout());
        inner.getAccessibleContext().setAccessibleDescription(panel.getAccessibleContext().getAccessibleDescription());
        inner.getAccessibleContext().setAccessibleName(panel.getAccessibleContext().getAccessibleName());
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        inner.add(panel, gridBagConstraints);
 
        DialogDescriptor desc = new DialogDescriptor(inner, NbBundle.getMessage(CustomizerFrameworks.class, "LBL_SelectWebExtension_DialogTitle")); //NOI18N
        Object res = DialogDisplayer.getDefault().notify(desc);
        if (res.equals(NotifyDescriptor.YES_OPTION)) {
            List<WebFrameworkProvider> newFrameworks = panel.getSelectedFrameworks();
            WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
            for (WebFrameworkProvider framework : newFrameworks) {
                if (!((DefaultListModel) jListFrameworks.getModel()).contains(framework))
                    ((DefaultListModel) jListFrameworks.getModel()).addElement(framework);

                boolean added = false;
                if (usedFrameworks.size() == 0) {
                    usedFrameworks.add(framework);
                    added = true;
                }
                else
                    for (int j = 0; j < usedFrameworks.size(); j++)
                        if (! usedFrameworks.get(j).getName().equals(framework.getName())) {
                            usedFrameworks.add(framework);
                            added = true;
                            break;
                        }
                
                if (added) {
                    WebModuleExtender extender = framework.createWebModuleExtender(wm, controller);
                    if (extender != null) {
                        extenders.put(framework, extender);
                        newExtenders.add(extender);
                        extender.addChangeListener(new ExtenderListener(extender));
                        addedFrameworks.add(framework);
                    }
                }
                jListFrameworks.setSelectedValue(framework, true);
            }
        }
        
        if (WebFrameworks.getFrameworks().size() == jListFrameworks.getModel().getSize())
            jButtonAdd.setEnabled(false);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void btnRemoveAddedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAddedActionPerformed
        WebFrameworkProvider framework = (WebFrameworkProvider) jListFrameworks.getSelectedValue();
        if (framework != null) {
            WebModuleExtender extender = extenders.get(framework);
            if (extender != null) {
                ((DefaultListModel)jListFrameworks.getModel()).removeElement(framework);
                addedFrameworks.remove(framework);
                newExtenders.remove(extender);
                extenders.remove(framework);
                usedFrameworks.remove(framework);
                boolean hasInvalid = false;
                for (WebModuleExtender ex : extenders.values()) {
                    if (!ex.isValid()) {
                        ex.update();
                        controller.setErrorMessage(null);
                        ex.isValid();
                        category.setValid(false);
                        category.setErrorMessage(controller.getErrorMessage());
                        hasInvalid = true;
                    }
                }
                if (!hasInvalid) {
                    if (!category.isValid()) {
                        category.setValid(true);
                        category.setErrorMessage(null);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnRemoveAddedActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoveAdded;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JLabel jLabelConfig;
    private javax.swing.JLabel jLabelFrameworks;
    private javax.swing.JList jListFrameworks;
    private javax.swing.JPanel jPanelConfig;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        btnRemoveAdded.setEnabled(false);
        WebFrameworkProvider framework = (WebFrameworkProvider) jListFrameworks.getSelectedValue();
        if (framework != null) {
            if (addedFrameworks.contains(framework)) {
                btnRemoveAdded.setEnabled(true);
            }
            WebModuleExtender extender = extenders.get(framework);
            if (extender != null) {
                String message = MessageFormat.format(NbBundle.getMessage(CustomizerFrameworks.class, "LBL_FrameworkConfiguration"), new Object[]{framework.getName()}); //NOI18N
                jLabelConfig.setText(message);
                jPanelConfig.removeAll();

                java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;

                jPanelConfig.add(extender.getComponent(), gridBagConstraints);
                jPanelConfig.repaint();
                jPanelConfig.revalidate();

                //always have the message according to the panel visible.
                extender.update();
                controller.setErrorMessage(null);
                extender.isValid();
                category.setErrorMessage(controller.getErrorMessage());
            } else {
                hideConfigPanel();
            }
        } else {
            hideConfigPanel();
        }
    }

    @NbBundle.Messages({
        "CustomizerFrameworks.label.adding.project.frameworks=Adding project frameworks",
        "CustomizerFrameworks.label.saving.project.frameworks=Saving project frameworks"
    })
    private void handleExtenders() {
        final List<WebModuleExtender> includedExtenders = new LinkedList<WebModuleExtender>(existingExtenders);
        final WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
        if (newExtenders != null && !newExtenders.isEmpty()) {
            // in case that new extenders should be included
            RP.post(new Runnable() {
                @Override
                public void run() {
                    // it mostly results into lenghty opperation, show progress dialog
                    ProgressUtils.showProgressDialogAndRun(new Runnable() {
                        @Override
                        public void run() {
                            // include newly added extenders into webmodule
                            for (int i = 0; i < newExtenders.size(); i++) {
                                ((WebModuleExtender) newExtenders.get(i)).extend(webModule);
                            }
                            newExtenders.clear();

                            // save all already included extenders
                            saveExistingExtenders(webModule, includedExtenders);
                        }
                    }, Bundle.CustomizerFrameworks_label_adding_project_frameworks());
                }
            });
        } else if (includedExtenders != null && !includedExtenders.isEmpty()) {
            // in case that webModule contains some extenders which should be saved
            RP.post(new Runnable() {

                @Override
                public void run() {
                    final FutureTask<Void> future = new FutureTask<Void>(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            // save all already included extenders
                            saveExistingExtenders(webModule, includedExtenders);
                            return null;
                        }
                    });
                    try {
                        // start the extenders saving task
                        RequestProcessor.getDefault().post(future);
                        // When the task doesn't finish shortly, run it with progress dialog to inform user
                        // that lenghty opperation is happening. BTW, initial waiting time is used to prevent
                        // dialogs flickering.
                        future.get(300, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (TimeoutException ex) {
                        // End of the 300ms period, continue in processing but display progress dialog
                        ProgressUtils.showProgressDialogAndRun(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Wait for finishing of the future
                                    future.get();
                                } catch (InterruptedException ex) {
                                    Exceptions.printStackTrace(ex);
                                } catch (ExecutionException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                        }, Bundle.CustomizerFrameworks_label_saving_project_frameworks());
                    }
                }
            });
        }
    }

    private void saveExistingExtenders(WebModule webModule, List<WebModuleExtender> existingExtenders) {
        if (existingExtenders != null) {
            for (WebModuleExtender webModuleExtender : existingExtenders) {
                if (webModuleExtender instanceof WebModuleExtender.Savable) {
                    ((WebModuleExtender.Savable) webModuleExtender).save(webModule);
                }
            }
        }
    }

    private final class ExtenderListener implements ChangeListener {
    
        private final WebModuleExtender extender;
        
        public ExtenderListener(WebModuleExtender extender) {
            this.extender = extender;
            extender.update();
            stateChanged(new ChangeEvent(this));
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            controller.setErrorMessage(null);
            if (extender.isValid()) {
                if (!category.isValid()) {
                    category.setValid(true);
                    category.setErrorMessage(null);
                    String message = (String) controller.getProperties().getProperty(WizardDescriptor.PROP_INFO_MESSAGE);
                    if (message != null) {
                        category.setErrorMessage(message);
                    }
                }
            } else {
                category.setValid(false);
                category.setErrorMessage(controller.getErrorMessage());
            }
        }
    }
    
    private void hideConfigPanel() {
	jLabelConfig.setText(""); //NOI18N
	jPanelConfig.removeAll();
	jPanelConfig.repaint();
	jPanelConfig.revalidate();
    }
}
