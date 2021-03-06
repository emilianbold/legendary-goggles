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

package org.netbeans.modules.maven.repository.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.modules.maven.api.CommonArtifactActions;
import static org.netbeans.modules.maven.repository.ui.Bundle.*;
import org.openide.awt.Actions;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 *
 * @author mkleint
 */
public class ProjectInfoPanel extends TopComponent implements MultiViewElement, LookupListener {
    private Lookup.Result<MavenProject> result;
    private JToolBar toolbar;

    /** Creates new form ProjectInfoPanel */
    public ProjectInfoPanel(Lookup lookup) {
        super(lookup);
        initComponents();
        btnCheckout.setIcon(null);
        if( "Aqua".equals(UIManager.getLookAndFeel().getID()) ) { //NOI18N
            setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.jPanel1.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.pnlCim.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.pnlIssues.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.pnlLicense.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.jPanel4.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.pnlMailingLists.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            this.pnlScm.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
        }
    }

    public @Override int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        lblProjectHome = new javax.swing.JLabel();
        btnProjectHome = new javax.swing.JButton();
        pnlIssues = new javax.swing.JPanel();
        lblSystem = new javax.swing.JLabel();
        txtSystem = new javax.swing.JTextField();
        lblIssues = new javax.swing.JLabel();
        btnIssues = new javax.swing.JButton();
        pnlScm = new javax.swing.JPanel();
        lblScmUrl = new javax.swing.JLabel();
        btnScmUrl = new javax.swing.JButton();
        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JTextField();
        lblDevConnection = new javax.swing.JLabel();
        txtDevConnection = new javax.swing.JTextField();
        btnCheckout = new javax.swing.JButton();
        pnlCim = new javax.swing.JPanel();
        lblCimSystem = new javax.swing.JLabel();
        txtCimSystem = new javax.swing.JTextField();
        lblCimUrl = new javax.swing.JLabel();
        btnCimUrl = new javax.swing.JButton();
        pnlLicense = new javax.swing.JPanel();
        pnlMailingLists = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        lblProjectName.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblProjectName.text")); // NOI18N

        txtProjectName.setEditable(false);

        lblDescription.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblDescription.text")); // NOI18N

        taDescription.setColumns(20);
        taDescription.setEditable(false);
        taDescription.setLineWrap(true);
        taDescription.setRows(3);
        taDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(taDescription);

        lblProjectHome.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblProjectHome.text")); // NOI18N

        btnProjectHome.setText("prj url"); // NOI18N
        btnProjectHome.setBorder(null);
        btnProjectHome.setBorderPainted(false);
        btnProjectHome.setContentAreaFilled(false);
        btnProjectHome.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        pnlIssues.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_Issues"))); // NOI18N

        lblSystem.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblSystem.text")); // NOI18N

        txtSystem.setEditable(false);

        lblIssues.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblIssues.text")); // NOI18N

        btnIssues.setText("isssue tracking url"); // NOI18N
        btnIssues.setBorder(null);
        btnIssues.setBorderPainted(false);
        btnIssues.setContentAreaFilled(false);
        btnIssues.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout pnlIssuesLayout = new javax.swing.GroupLayout(pnlIssues);
        pnlIssues.setLayout(pnlIssuesLayout);
        pnlIssuesLayout.setHorizontalGroup(
            pnlIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIssuesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSystem)
                    .addComponent(lblIssues))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnIssues, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                    .addComponent(txtSystem, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlIssuesLayout.setVerticalGroup(
            pnlIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIssuesLayout.createSequentialGroup()
                .addGroup(pnlIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSystem)
                    .addComponent(txtSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIssuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIssues)
                    .addComponent(lblIssues))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlScm.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_SCM"))); // NOI18N

        lblScmUrl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblScmUrl.text")); // NOI18N

        btnScmUrl.setText("scm url"); // NOI18N
        btnScmUrl.setBorder(null);
        btnScmUrl.setBorderPainted(false);
        btnScmUrl.setContentAreaFilled(false);
        btnScmUrl.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        lblConnection.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblConnection.text")); // NOI18N

        txtConnection.setEditable(false);

        lblDevConnection.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblDevConnection.text")); // NOI18N

        txtDevConnection.setEditable(false);

        btnCheckout.setAction(CommonArtifactActions.createScmCheckoutAction(getLookup()));
        btnCheckout.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.btnCheckout.text")); // NOI18N

        javax.swing.GroupLayout pnlScmLayout = new javax.swing.GroupLayout(pnlScm);
        pnlScm.setLayout(pnlScmLayout);
        pnlScmLayout.setHorizontalGroup(
            pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScmLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlScmLayout.createSequentialGroup()
                        .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblScmUrl)
                            .addComponent(lblConnection))
                        .addGap(42, 42, 42)
                        .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnScmUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScmLayout.createSequentialGroup()
                                .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtDevConnection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                                    .addComponent(txtConnection, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCheckout)))
                        .addContainerGap())
                    .addComponent(lblDevConnection)))
        );
        pnlScmLayout.setVerticalGroup(
            pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScmLayout.createSequentialGroup()
                .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScmUrl)
                    .addComponent(btnScmUrl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCheckout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlScmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDevConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDevConnection))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCim.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_CIManagement"))); // NOI18N

        lblCimSystem.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblCimSystem.text")); // NOI18N

        txtCimSystem.setEditable(false);

        lblCimUrl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lblCimUrl.text")); // NOI18N

        btnCimUrl.setText("cim url"); // NOI18N
        btnCimUrl.setBorder(null);
        btnCimUrl.setBorderPainted(false);
        btnCimUrl.setContentAreaFilled(false);
        btnCimUrl.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout pnlCimLayout = new javax.swing.GroupLayout(pnlCim);
        pnlCim.setLayout(pnlCimLayout);
        pnlCimLayout.setHorizontalGroup(
            pnlCimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCimLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCimSystem)
                    .addComponent(lblCimUrl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCimUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                    .addComponent(txtCimSystem, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCimLayout.setVerticalGroup(
            pnlCimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCimLayout.createSequentialGroup()
                .addGroup(pnlCimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCimSystem)
                    .addComponent(txtCimSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCimUrl)
                    .addComponent(lblCimUrl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlLicense.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_Licenses"))); // NOI18N
        pnlLicense.setLayout(new java.awt.GridLayout(1, 1));

        pnlMailingLists.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "TIT_MailingLists"))); // NOI18N
        pnlMailingLists.setLayout(new java.awt.GridLayout(1, 1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlScm, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMailingLists, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                    .addComponent(pnlLicense, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProjectName)
                            .addComponent(lblDescription))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                            .addComponent(txtProjectName, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblProjectHome)
                        .addGap(18, 18, 18)
                        .addComponent(btnProjectHome, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE))
                    .addComponent(pnlIssues, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCim, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProjectName)
                    .addComponent(txtProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProjectHome, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProjectHome))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlIssues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlScm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlLicense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlMailingLists, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(158, 158, 158))
        );

        jScrollPane2.setViewportView(jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 869, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 531, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnCimUrl;
    private javax.swing.JButton btnIssues;
    private javax.swing.JButton btnProjectHome;
    private javax.swing.JButton btnScmUrl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCimSystem;
    private javax.swing.JLabel lblCimUrl;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDevConnection;
    private javax.swing.JLabel lblIssues;
    private javax.swing.JLabel lblProjectHome;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel lblScmUrl;
    private javax.swing.JLabel lblSystem;
    private javax.swing.JPanel pnlCim;
    private javax.swing.JPanel pnlIssues;
    private javax.swing.JPanel pnlLicense;
    private javax.swing.JPanel pnlMailingLists;
    private javax.swing.JPanel pnlScm;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField txtCimSystem;
    private javax.swing.JTextField txtConnection;
    private javax.swing.JTextField txtDevConnection;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtSystem;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new JToolBar();
            if( "Aqua".equals(UIManager.getLookAndFeel().getID()) ) { //NOI18N
                toolbar.setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
            }
            toolbar.setFloatable(false);
            Action[] a = new Action[1];
            Action[] actions = getLookup().lookup(a.getClass());
            Dimension space = new Dimension(3, 0);
            toolbar.addSeparator(space);
            for (Action act : actions) {
                JButton btn = new JButton();
                Actions.connect(btn, act);
                toolbar.add(btn);
                toolbar.addSeparator(space);
            }
        }
        return toolbar;
    }


    @Override
    public void componentOpened() {
        super.componentOpened();
        result = getLookup().lookupResult(MavenProject.class);
        populateFields();
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        result.removeLookupListener(this);
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }


    public @Override void setMultiViewCallback(MultiViewElementCallback callback) {}

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private void populateFields() {
        boolean loading = true;
        Iterator<? extends MavenProject> iter = result.allInstances().iterator();
        String name = null, desc = null, homeUrl = null;
        String imUrl = null, imSystem = null;
        String scmUrl = null, scmConn = null, scmDevConn = null;
        String cimSystem = null, cimUrl = null;
        if (iter.hasNext()) {
            loading = false;
            MavenProject prj = iter.next();
            name = prj.getName();
            desc = prj.getDescription();
            if (desc != null) {
                desc = desc.replaceAll("\\s+", " ").trim();
            }
            homeUrl = prj.getUrl();
            IssueManagement im = prj.getIssueManagement();
            if (im != null) {
                imUrl = im.getUrl();
                imSystem = im.getSystem();
            }
            Scm scm = prj.getScm();
            if (scm != null) {
                scmUrl = scm.getUrl();
                scmConn = scm.getConnection();
                scmDevConn = scm.getDeveloperConnection();
            }
            CiManagement cim = prj.getCiManagement();
            if (cim != null) {
                cimSystem = cim.getSystem();
                cimUrl = cim.getUrl();
            }
            @SuppressWarnings("unchecked")
            List<License> licenses = prj.getLicenses();
            if (licenses != null) {
                GridLayout layout = (GridLayout)pnlLicense.getLayout();
                layout.setColumns(1);
                layout.setRows(licenses.size());
                for (License lic : licenses) {
                    LicensePanel pnl = new LicensePanel();
                    setPlainText(pnl.txtName, lic.getName(), loading);
                    setLinkedText(pnl.btnURL, lic.getUrl(), loading);
                    pnlLicense.add(pnl);
                }
            }
            @SuppressWarnings("unchecked")
            List<MailingList> mailings = prj.getMailingLists();
            if (mailings != null) {
                GridLayout layout = (GridLayout)pnlMailingLists.getLayout();
                layout.setColumns(1);
                layout.setRows(mailings.size());
                for (MailingList list : mailings) {
                    MailingListPanel pnl = new MailingListPanel();
                    setPlainText(pnl.txtName, list.getName(), loading);
                    setLinkedText(pnl.btnArchive, list.getArchive(), loading);
                    setPlainText(pnl.txtSubscribe, list.getSubscribe(), loading);
                    setPlainText(pnl.txtUnsubscribe, list.getUnsubscribe(), loading);
                    pnlMailingLists.add(pnl);
                }
            }
        }
        setPlainText(txtProjectName, name, loading); 
        setPlainText(taDescription, desc, loading); 
        setLinkedText(btnProjectHome, homeUrl, loading);
        
        setLinkedText(btnIssues, imUrl, loading);
        setPlainText(txtSystem, imSystem, loading); 

        setLinkedText(btnScmUrl, scmUrl, loading);
        setPlainText(txtConnection, scmConn, loading);
        setPlainText(txtDevConnection, scmDevConn, loading);

        setLinkedText(btnCimUrl, cimUrl, loading);
        setPlainText(txtCimSystem, cimSystem, loading);

    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                populateFields();
            }
        });
    }

    @Messages({
        "LBL_Loading=<Loading>",
        "LBL_Undefined=<Undefined>"
    })
    private void setLinkedText(JButton btn, String url, boolean loading) {
        if (url == null) {
            btn.setAction(null);
            if (loading) {
                btn.setText(LBL_Loading());
            } else {
                btn.setText(LBL_Undefined());
            }
            btn.setCursor(null);
        } else {
            btn.setAction(new LinkAction(url));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setText("<html><a href=\"\">" + url + "</a></html>");
        }
    }

    private void setPlainText(JTextComponent field, String value, boolean loading) {
        if (value == null) {
            if (loading) {
                field.setText(LBL_Loading());
            } else {
                field.setText(LBL_Undefined());
            }
        } else {
            field.setText(value);
            field.setCaretPosition(0);
        }
    }

    private class LinkAction extends AbstractAction {
        private final String url;

        public LinkAction(String url) {
            this.url = url;
        }

        @Messages("ERR_WrongURL=Not a proper URL, cannot open in browser: {0}")
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                URL u = new URL(url);
                HtmlBrowser.URLDisplayer.getDefault().showURL(u);
            } catch (MalformedURLException ex) {
                StatusDisplayer.getDefault().setStatusText(ERR_WrongURL(url));
            }
        }

    }
}
