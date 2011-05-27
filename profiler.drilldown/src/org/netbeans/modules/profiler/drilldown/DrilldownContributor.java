/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.profiler.drilldown;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.netbeans.api.project.Project;
import org.netbeans.lib.profiler.ProfilerClient;
import org.netbeans.lib.profiler.ui.UIUtils;
import org.netbeans.lib.profiler.ui.cpu.LiveFlatProfilePanel;
import org.netbeans.lib.profiler.ui.cpu.statistics.StatisticalModule;
import org.netbeans.lib.profiler.ui.cpu.statistics.StatisticalModuleContainer;
import org.netbeans.modules.profiler.categorization.api.ProjectAwareStatisticalModule;
import org.netbeans.lib.profiler.ui.cpu.LiveResultsWindowContributor;
import org.netbeans.modules.profiler.utilities.ProfilerUtils;
import org.openide.util.Lookup;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jaroslav Bachorik
 */
@ServiceProvider(service = LiveResultsWindowContributor.class)
public class DrilldownContributor extends LiveResultsWindowContributor.Adapter {

    private static final Logger LOGGER = Logger.getLogger(DrilldownContributor.class.getName());
    private DrillDown dd;
    volatile private boolean drillDownGroupOpened;

    @Override
    public void addToCpuResults(final LiveFlatProfilePanel cpuPanel, final JToolBar toolBar, ProfilerClient client, Lookup.Provider project) {
        List<StatisticalModule> additionalStats = new ArrayList<StatisticalModule>();

        dd = Lookup.getDefault().lookup(DrillDownFactory.class).createDrillDown((Project)project, client);
        if (dd != null) {
            StatisticalModuleContainer container = Lookup.getDefault().lookup(StatisticalModuleContainer.class);
            additionalStats.addAll(container.getAllModules());

            for(StatisticalModule sm : additionalStats) {
                if (sm instanceof ProjectAwareStatisticalModule) {
                    ((ProjectAwareStatisticalModule)sm).setProject((Project)project);
                }
            }
            DrillDownWindow.getDefault().setDrillDown(dd, additionalStats);
            showDrillDown();
        } else {
            hideDrillDown();
        }


        if (dd != null) {
            dd.addListener(new DrillDownListener() {

                public void dataChanged() {
                }

                public void drillDownPathChanged(List list) {
                    cpuPanel.updateLiveResults();
                }
            });
        }

        cpuPanel.setAdditionalStats(additionalStats);

        final DrillDownWindow drillDownWin = DrillDownWindow.getDefault();
        DrillDownWindow.closeIfOpened();
        drillDownWin.getPresenter().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (drillDownWin.getPresenter().isSelected()) {
                    drillDownWin.open();
                } else {
                    drillDownWin.close();
                }
            }
        });

        JPanel toolbarSpacer = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0)) {

            public Dimension getPreferredSize() {
                if (UIUtils.isGTKLookAndFeel() || UIUtils.isNimbusLookAndFeel()) {
                    int currentWidth = toolBar.getSize().width;
                    int minimumWidth = toolBar.getMinimumSize().width;
                    int extraWidth = currentWidth - minimumWidth;
                    return new Dimension(Math.max(extraWidth, 0), 0);
                } else {
                    return super.getPreferredSize();
                }
            }
        };
        toolbarSpacer.setOpaque(false);

        toolBar.add(toolbarSpacer);
        toolBar.add(drillDownWin.getPresenter());
    }

    @Override
    public void reset() {
        if (dd != null) {
            dd.reset();
        }
    }

    @Override
    public void hide() {
        if (drillDownGroupOpened) {
            hideDrillDown();
        }
    }

    @Override
    public void refresh() {
        if (dd != null) {
            dd.refresh(); // TODO race condition by cleaning the dd variable!
        }
    }

    @Override
    public void show() {
        if (dd != null && dd.isValid() && !drillDownGroupOpened) {
            showDrillDown();
        }
    }

    private void showDrillDown() {
        ProfilerUtils.runInEventDispatchThread(new Runnable() {

            public void run() {
                TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("LiveResultsGroup"); //NOI18N

                if (group != null) {
                    group.open();
                    drillDownGroupOpened = true;
                    DrillDownWindow.getDefault().getPresenter().setEnabled(true);

                    //          if (DrillDownWindow.getDefault().needsDocking()) DrillDownWindow.getDefault().open(); // Do not open DrillDown by default, only on demand by DrillDownWindow.getDefault().getPresenter()
                } else {
                    LOGGER.severe("LiveResultsGroup not existing!"); // NOI18N
                }
            }
        });
    }

    private void hideDrillDown() {
        ProfilerUtils.runInEventDispatchThread(new Runnable() {

            public void run() {
                TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("LiveResultsGroup"); //NOI18N

                if (group != null) {
                    group.close();
                }
                drillDownGroupOpened = false;
                DrillDownWindow.getDefault().getPresenter().setEnabled(false);
            }
        });
    }
}
