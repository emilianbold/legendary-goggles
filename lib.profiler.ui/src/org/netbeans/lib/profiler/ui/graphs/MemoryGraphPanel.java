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

package org.netbeans.lib.profiler.ui.graphs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import org.netbeans.lib.profiler.charts.axis.AxisComponent;
import org.netbeans.lib.profiler.charts.ChartContext;
import org.netbeans.lib.profiler.charts.ChartDecorator;
import org.netbeans.lib.profiler.charts.ChartItem;
import org.netbeans.lib.profiler.charts.ChartSelectionModel;
import org.netbeans.lib.profiler.charts.ItemsModel;
import org.netbeans.lib.profiler.charts.swing.LongRect;
import org.netbeans.lib.profiler.charts.swing.CrossBorderLayout;
import org.netbeans.lib.profiler.charts.PaintersModel;
import org.netbeans.lib.profiler.charts.axis.BytesAxisUtils;
import org.netbeans.lib.profiler.charts.axis.BytesMarksPainter;
import org.netbeans.lib.profiler.charts.xy.BytesXYItemMarksComputer;
import org.netbeans.lib.profiler.charts.axis.TimeMarksPainter;
import org.netbeans.lib.profiler.charts.axis.TimelineMarksComputer;
import org.netbeans.lib.profiler.charts.swing.Utils;
import org.netbeans.lib.profiler.charts.xy.XYItem;
import org.netbeans.lib.profiler.charts.xy.XYItemPainter;
import org.netbeans.lib.profiler.charts.xy.CompoundXYItemPainter;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItem;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItemMarker;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItemPainter;
import org.netbeans.lib.profiler.results.DataManagerListener;
import org.netbeans.lib.profiler.results.monitor.VMTelemetryDataManager;
import org.netbeans.lib.profiler.ui.UIUtils;
import org.netbeans.lib.profiler.ui.charts.xy.ProfilerXYChart;
import org.netbeans.lib.profiler.ui.charts.xy.ProfilerXYTooltipOverlay;
import org.netbeans.lib.profiler.ui.charts.xy.ProfilerXYTooltipPainter;
import org.netbeans.lib.profiler.ui.charts.xy.ProfilerXYTooltipModel;
import org.netbeans.lib.profiler.ui.components.ColorIcon;
import org.netbeans.lib.profiler.ui.monitor.VMTelemetryModels;



/**
 *
 * @author Jiri Sedlacek
 */
public final class MemoryGraphPanel extends GraphPanel {

    private ProfilerXYChart chart;
    private Action[] chartActions;

    private final VMTelemetryModels models;


    private final boolean smallPanel;


    // --- Constructors --------------------------------------------------------
    
    public static MemoryGraphPanel createBigPanel(VMTelemetryModels models) {
        return new MemoryGraphPanel(models, false, null);
    }
    
    public static MemoryGraphPanel createSmallPanel(VMTelemetryModels models,
                                             Action chartAction) {
        return new MemoryGraphPanel(models, true, chartAction);
    }

    private MemoryGraphPanel(VMTelemetryModels models,
                             boolean smallPanel, Action chartAction) {

        // Save models and panel type
        this.models = models;
        this.smallPanel = smallPanel;

        // Create UI
        initComponents(chartAction);

        // Register listener
        models.getDataManager().addDataListener(new DataManagerListener() {
            public void dataChanged() { updateData(); }
            public void dataReset() { resetData(); }
        });

        // Initialize chart & legend
        resetData();
    }


    // --- GraphPanel implementation -------------------------------------------

    public Action[] getActions() {
        return chartActions;
    }


    // --- Private implementation ----------------------------------------------

    private void updateData() {
        if (smallPanel) {
            if (chart.fitsWidth()) {
                VMTelemetryDataManager manager = models.getDataManager();
                long[] timestamps = manager.timeStamps;
                if (timestamps[manager.getItemCount() - 1] - timestamps[0] >=
                    SMALL_CHART_FIT_TO_WINDOW_PERIOD)
                        chart.setFitsWidth(false);
            }
        } else {
        }
    }

    private void resetData() {
        if (smallPanel) {
            chart.setScale(INITIAL_CHART_SCALEX, 1);
            chart.setOffset(0, 0);
            chart.setFitsWidth(true);
        } else {
            chart.setScale(INITIAL_CHART_SCALEX, 1);
            chart.setOffset(0, 0);
            chart.setFitsWidth(false);
        }
        chart.setInitialDataBounds(new LongRect(System.currentTimeMillis(), 0,
                                       2500, GraphsUI.HEAP_SIZE_INITIAL_VALUE));
    }

    
    private void initComponents(final Action chartAction) {
        // Painters model
        PaintersModel paintersModel = createMemoryPaintersModel();

        // Chart
        chart = createChart(models.memoryItemsModel(),
                            paintersModel, smallPanel);
        chart.setBackground(GraphsUI.CHART_BACKGROUND_COLOR);
        chart.setViewInsets(new Insets(10, 0, 0, 0));
        
        chart.addPreDecorator(createMaxHeapDecorator());

        // Horizontal axis
        AxisComponent hAxis =
                new AxisComponent(chart, new TimelineMarksComputer(
                         models.memoryItemsModel().getTimeline(),
                         chart.getChartContext(), SwingConstants.HORIZONTAL),
                         new TimeMarksPainter(),
                         SwingConstants.SOUTH, AxisComponent.MESH_FOREGROUND);

        // Vertical axis
        XYItem memoryItem = models.memoryItemsModel().getItem(0);
        XYItemPainter memoryPainter = (XYItemPainter)paintersModel.getPainter(memoryItem);
        AxisComponent vAxis =
                new AxisComponent(chart, new BytesXYItemMarksComputer(
                         memoryItem, memoryPainter, chart.getChartContext(),
                         SwingConstants.VERTICAL),
                         new BytesMarksPainter(), SwingConstants.WEST,
                         AxisComponent.MESH_FOREGROUND);

        // Chart panel (chart & axes)
        JPanel chartPanel = new JPanel(new CrossBorderLayout());
        chartPanel.setBackground(GraphsUI.CHART_BACKGROUND_COLOR);
        chartPanel.setBorder(BorderFactory.createMatteBorder(
                             10, 10, 10, 10, GraphsUI.CHART_BACKGROUND_COLOR));
        chartPanel.add(chart, new Integer[] { SwingConstants.CENTER });
        chartPanel.add(hAxis, new Integer[] { SwingConstants.SOUTH,
                                              SwingConstants.SOUTH_WEST });
        chartPanel.add(vAxis, new Integer[] { SwingConstants.WEST,
                                              SwingConstants.SOUTH_WEST });

        // Small panel UI
        if (smallPanel) {

            // Customize chart
            chart.setMouseZoomingEnabled(false);
            chart.getSelectionModel().setHoverMode(ChartSelectionModel.HOVER_NONE);

            // Heap Size
            JLabel heapSizeSmall = new JLabel(GraphsUI.HEAP_SIZE_NAME,
                                              new ColorIcon(GraphsUI.
                                              HEAP_SIZE_PAINTER_FILL_COLOR, null,
                                              8, 8), SwingConstants.LEADING);
            heapSizeSmall.setFont(getFont().deriveFont((float)(getFont().getSize()) - 1));
            heapSizeSmall.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            // Used heap
            JLabel usedHeapSmall = new JLabel(GraphsUI.USED_HEAP_NAME,
                                              new ColorIcon(GraphsUI.
                                              USED_HEAP_PAINTER_FILL_COLOR, null,
                                              8, 8), SwingConstants.LEADING);
            usedHeapSmall.setFont(getFont().deriveFont((float) (getFont().getSize()) - 1));
            usedHeapSmall.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            // Legend container
            JPanel smallLegendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 1));
            smallLegendPanel.setBackground(GraphsUI.SMALL_LEGEND_BACKGROUND_COLOR);
            smallLegendPanel.setBorder(new LineBorder(GraphsUI.SMALL_LEGEND_BORDER_COLOR, 1));
            smallLegendPanel.add(heapSizeSmall);
            smallLegendPanel.add(usedHeapSmall);
            JPanel smallLegendContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            smallLegendContainer.setBackground(GraphsUI.SMALL_LEGEND_BACKGROUND_COLOR);
            smallLegendContainer.add(smallLegendPanel);

            // Master UI
            setLayout(new BorderLayout());
            add(chartPanel, BorderLayout.CENTER);
            add(smallLegendContainer, BorderLayout.SOUTH);

            
            // Doubleclick action
            chart.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) &&
                        e.getClickCount() == 2)
                            chartAction.actionPerformed(null);
                }
            });

            // Toolbar actions
            chartActions = new Action[] {};

        // Big panel UI
        } else {

            // Setup tooltip painter
            ProfilerXYTooltipPainter tooltipPainter = new ProfilerXYTooltipPainter(
                                                GraphsUI.TOOLTIP_OVERLAY_LINE_WIDTH,
                                                GraphsUI.TOOLTIP_OVERLAY_LINE_COLOR,
                                                GraphsUI.TOOLTIP_OVERLAY_FILL_COLOR,
                                                getTooltipModel());

            // Customize chart
            chart.addOverlayComponent(new ProfilerXYTooltipOverlay(chart,
                                                                   tooltipPainter));

            // Chart scrollbar
            JScrollBar hScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
            chart.attachHorizontalScrollBar(hScrollBar);

            // Chart container (chart panel & scrollbar)
            JPanel chartContainer = new JPanel(new BorderLayout());
            chartContainer.setBorder(BorderFactory.createEmptyBorder());
            chartContainer.add(chartPanel, BorderLayout.CENTER);
            chartContainer.add(hScrollBar, BorderLayout.SOUTH);

            // Heap Size
            JLabel heapSizeBig = new JLabel(GraphsUI.HEAP_SIZE_NAME,
                                            new ColorIcon(GraphsUI.
                                            HEAP_SIZE_PAINTER_FILL_COLOR, Color.
                                            BLACK, 18, 9), SwingConstants.LEADING);
            heapSizeBig.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

            // Used heap
            JLabel usedHeapBig = new JLabel(GraphsUI.USED_HEAP_NAME,
                                            new ColorIcon(GraphsUI.
                                            USED_HEAP_PAINTER_FILL_COLOR, Color.
                                            BLACK, 18, 9), SwingConstants.LEADING);
            usedHeapBig.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

            // Legend container
            JPanel bigLegendPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 7, 8));
            bigLegendPanel.add(heapSizeBig);
            bigLegendPanel.add(usedHeapBig);
            
            JPanel legendContainer = new JPanel(new BorderLayout(0, 0));
            legendContainer.add(UIUtils.createHorizontalLine(legendContainer.getBackground()), BorderLayout.NORTH);
            legendContainer.add(bigLegendPanel, BorderLayout.CENTER);

            // Master UI
            setLayout(new BorderLayout());
            add(chartContainer, BorderLayout.CENTER);
            add(legendContainer, BorderLayout.SOUTH);


            // Toolbar actions
            chartActions = new Action[] { chart.zoomInAction(),
                                          chart.zoomOutAction(),
                                          chart.toggleViewAction()};

        }

    }
    

    protected ProfilerXYTooltipModel createTooltipModel() {
        return new ProfilerXYTooltipModel() {

            public String getTimeValue(long timestamp) {
                return DATE_FORMATTER.format(new Date(timestamp));
            }

            public int getRowsCount() {
                return 2;
            }

            public String getRowName(int index) {
                switch (index) {
                    case 0:
                        return GraphsUI.HEAP_SIZE_NAME;
                    case 1:
                        return GraphsUI.USED_HEAP_NAME;
                    default:
                        return null;
                }
            }

            public Color getRowColor(int index) {
                switch (index) {
                    case 0:
                        return GraphsUI.HEAP_SIZE_PAINTER_FILL_COLOR;
                    case 1:
                        return GraphsUI.USED_HEAP_PAINTER_FILL_COLOR;
                    default:
                        return null;
                }
            }

            public String getRowValue(int index, long itemValue) {
                return INT_FORMATTER.format(itemValue);
            }

            public String getRowUnits(int index, long itemValue) {
                return BytesAxisUtils.UNITS_B;
            }

            public int getExtraRowsCount() {
                return getRowsCount();
            }

            public String getExtraRowName(int index) {
                return getMaxValueString(getRowName(index));
            }

            public Color getExtraRowColor(int index) {
                return getRowColor(index);
            }

            public String getExtraRowValue(int index) {
                SynchronousXYItem item = models.memoryItemsModel().getItem(index);
                return INT_FORMATTER.format(item.getMaxYValue());
            }

            public String getExtraRowUnits(int index) {
                return getRowUnits(index, -1);
            }

        };
    }

    private PaintersModel createMemoryPaintersModel() {
        // Heap size
        SynchronousXYItemPainter heapSizePainter =
                SynchronousXYItemPainter.absolutePainter(GraphsUI.HEAP_SIZE_PAINTER_LINE_WIDTH,
                                                      GraphsUI.HEAP_SIZE_PAINTER_LINE_COLOR,
                                                      GraphsUI.HEAP_SIZE_PAINTER_FILL_COLOR);
        SynchronousXYItemMarker heapSizeMarker =
                 SynchronousXYItemMarker.absolutePainter(GraphsUI.HEAP_SIZE_MARKER_RADIUS,
                                                      GraphsUI.HEAP_SIZE_MARKER_LINE1_WIDTH,
                                                      GraphsUI.HEAP_SIZE_MARKER_LINE1_COLOR,
                                                      GraphsUI.HEAP_SIZE_MARKER_LINE2_WIDTH,
                                                      GraphsUI.HEAP_SIZE_MARKER_LINE2_COLOR,
                                                      GraphsUI.HEAP_SIZE_MARKER_FILL_COLOR);
        XYItemPainter hsp = new CompoundXYItemPainter(heapSizePainter,
                                                      heapSizeMarker);

        // Used heap
        SynchronousXYItemPainter usedHeapPainter =
                SynchronousXYItemPainter.absolutePainter(GraphsUI.USED_HEAP_PAINTER_LINE_WIDTH,
                                                      GraphsUI.USED_HEAP_PAINTER_LINE_COLOR,
                                                      GraphsUI.USED_HEAP_PAINTER_FILL_COLOR);
        SynchronousXYItemMarker usedHeapMarker =
                 SynchronousXYItemMarker.absolutePainter(GraphsUI.USED_HEAP_MARKER_RADIUS,
                                                      GraphsUI.USED_HEAP_MARKER_LINE1_WIDTH,
                                                      GraphsUI.USED_HEAP_MARKER_LINE1_COLOR,
                                                      GraphsUI.USED_HEAP_MARKER_LINE2_WIDTH,
                                                      GraphsUI.USED_HEAP_MARKER_LINE2_COLOR,
                                                      GraphsUI.USED_HEAP_MARKER_FILL_COLOR);
        XYItemPainter uhp = new CompoundXYItemPainter(usedHeapPainter,
                                                      usedHeapMarker);

        // Model
        ItemsModel items = models.memoryItemsModel();
        PaintersModel model = new PaintersModel.Default(
                                            new ChartItem[] { items.getItem(0),
                                                              items.getItem(1) },
                                            new XYItemPainter[] { hsp, uhp });

        return model;
    }

    private ChartDecorator createMaxHeapDecorator() {
        return new ChartDecorator() {
            public void paint(Graphics2D g, Rectangle dirtyArea,
                              ChartContext context) {

                int limitHeight = Utils.checkedInt(
                                  context.getViewY(models.getDataManager().
                                  maxHeapSize));
                if (limitHeight <= context.getViewportHeight()) {
                    g.setColor(GraphsUI.HEAP_LIMIT_FILL_COLOR);
                    if (context.isBottomBased())
                        g.fillRect(0, 0, context.getViewportWidth(), limitHeight);
                    else
                        g.fillRect(0, limitHeight, context.getViewportWidth(),
                                   context.getViewportHeight() - limitHeight);
                }
            }
        };
    }

}
