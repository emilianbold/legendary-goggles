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
package org.netbeans.modules.profiler.j2ee.sunas;

import java.awt.Font;
import javax.swing.ComboBoxModel;
import javax.swing.UIManager;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.profiler.attach.panels.components.DirectorySelector;
import org.netbeans.modules.profiler.attach.panels.components.JavaPlatformPanelComponent;
import org.netbeans.modules.profiler.attach.providers.TargetPlatform;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class SunASIntegrationPanelUI extends javax.swing.JPanel {

    private SunASIntegrationPanel.Model model = null;
    private boolean internalChanges = false;
    private ComboBoxModel domainsComboModel = new ComboBoxModel() {

        public void addListDataListener(ListDataListener l) {
        }

        public Object getElementAt(int index) {
            return model.getDomains().get(index);
        }

        public Object getSelectedItem() {
            return model.getDomain();
        }

        public int getSize() {
            if (model == null || model.getDomains() == null) {
                return 0;
            }
            return model.getDomains().size();
        }

        public void removeListDataListener(ListDataListener l) {
        }

        public void setSelectedItem(Object anItem) {
            if (anItem instanceof String && model.getDomains().contains(anItem)) {
                model.setDomain((String) anItem);
            }
        }
    };

    /** Creates new form SunASIntegrationPanelUI */
    public SunASIntegrationPanelUI(SunASIntegrationPanel.Model model) {
        this.model = model;
        initComponents();
        loadModel();
    }

    public ComboBoxModel getDomainsComboModel() {
        return domainsComboModel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        targetJavaSelector = new org.netbeans.modules.profiler.attach.panels.components.JavaPlatformPanelComponent();
        jPanel2 = new javax.swing.JPanel();
        labelInstall = new javax.swing.JLabel();
        labelDomain = new javax.swing.JLabel();
        directorySelector = new org.netbeans.modules.profiler.attach.panels.components.DirectorySelector();
        domainsCombo = new org.netbeans.modules.profiler.attach.panels.components.ComboSelector();

        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(500, 400));

        targetJavaSelector.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(SunASIntegrationPanelUI.class, "SunASIntegrationPanelUI.border.targetJavaSelector.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N
        targetJavaSelector.setTitle(org.openide.util.NbBundle.getMessage(SunASIntegrationPanelUI.class, "SunASIntegrationPanelUI.targetJavaSelector.title")); // NOI18N
        targetJavaSelector.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                targetJavaSelectorPropertyChange(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SunASIntegrationPanelUI.class, "SunAS8IntegrationProvider_ProvideInfoLabelText"))); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/j2ee/sunas/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelInstall, bundle.getString("SunAS8IntegrationProvider_SunAsInstallLabelText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(labelDomain, bundle.getString("SunAS8IntegrationProvider_DomainNameLabelText")); // NOI18N

        directorySelector.setHint(org.openide.util.NbBundle.getMessage(SunASIntegrationPanelUI.class, "SunASIntegrationPanelUI.directorySelector.hint")); // NOI18N
        directorySelector.setPath(org.openide.util.NbBundle.getMessage(SunASIntegrationPanelUI.class, "SunASIntegrationPanelUI.directorySelector.path")); // NOI18N
        directorySelector.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                directorySelectorPropertyChange(evt);
            }
        });

        domainsCombo.setHint(org.openide.util.NbBundle.getMessage(SunASIntegrationPanelUI.class, "SunASIntegrationPanelUI.domainsCombo.hint")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(directorySelector, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                    .add(domainsCombo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                    .add(labelInstall)
                    .add(labelDomain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(labelInstall)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(directorySelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(labelDomain)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(domainsCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, targetJavaSelector, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(targetJavaSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

  private void targetJavaSelectorPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_targetJavaSelectorPropertyChange
      if (evt.getPropertyName().equals(JavaPlatformPanelComponent.JAVA_PLATFORM_PROPERTY)) {
          this.model.setTargetJava((TargetPlatform) evt.getNewValue());
      }
  }//GEN-LAST:event_targetJavaSelectorPropertyChange

  private void directorySelectorPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_directorySelectorPropertyChange
      if (internalChanges) {
          return;
      }
      if (evt.getPropertyName().equals(DirectorySelector.PATH_PROPERTY)) {
          applyValues();
          loadDomainsAndHints();
      } else if (evt.getPropertyName().equals(DirectorySelector.LAYOUT_CHANGED_PROPERTY)) {
          validate();
      }
  }//GEN-LAST:event_directorySelectorPropertyChange
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.profiler.attach.panels.components.DirectorySelector directorySelector;
    private org.netbeans.modules.profiler.attach.panels.components.ComboSelector domainsCombo;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel labelDomain;
    private javax.swing.JLabel labelInstall;
    private org.netbeans.modules.profiler.attach.panels.components.JavaPlatformPanelComponent targetJavaSelector;
    // End of variables declaration//GEN-END:variables
    private void applyValues() {
        this.model.setDomain((String) domainsCombo.getSelectedItem());
        this.model.setInstallPath(directorySelector.getPath());
        this.model.setTargetJava(targetJavaSelector.getSelectedPlatform());
    }

    public void loadModel() {
        try {
            internalChanges = true;

            directorySelector.setHint(this.model.getInstallPathHint());
            directorySelector.setPath(this.model.getInstallPath());
            domainsCombo.setHint(this.model.getDomainHint());
            domainsCombo.setModel(getDomainsComboModel());
            domainsCombo.getModel().setSelectedItem(this.model.getDomain());
            targetJavaSelector.setSelectedPlatform(this.model.getSelectedPlatform());
        } finally {
            internalChanges = false;
        }

    }

    private void loadDomainsAndHints() {
        try {
            internalChanges = true;

            directorySelector.setHint(this.model.getInstallPathHint());
            domainsCombo.setHint(this.model.getDomainHint());
            domainsCombo.setModel(getDomainsComboModel());
            domainsCombo.getModel().setSelectedItem(this.model.getDomain());
        } finally {
            internalChanges = false;
        }

    }

    public void refresh() {
        targetJavaSelector.refresh();
        applyValues();
        loadModel();
    }

    public void refreshJvmList(final TargetPlatform preselectedPlatform) {
        targetJavaSelector.refresh(preselectedPlatform);
    }
}
