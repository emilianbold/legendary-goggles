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

package org.netbeans.modules.profiler.ppoints;

import org.netbeans.lib.profiler.client.RuntimeProfilingPoint;
import org.netbeans.lib.profiler.ui.components.HTMLTextArea;
import org.netbeans.modules.profiler.ppoints.ui.StopwatchCustomizer;
import org.netbeans.modules.profiler.ppoints.ui.ValidityAwarePanel;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.netbeans.lib.profiler.ui.UIUtils;
import org.netbeans.modules.profiler.api.ProjectUtilities;
import org.openide.util.Lookup;


/**
 *
 * @author Tomas Hurka
 * @author Jiri Sedlacek
 */
@NbBundle.Messages({
    "StopwatchProfilingPoint_OneHitString=<b>1 hit</b> at {0}, <a href='#'>report</a>",
    "StopwatchProfilingPoint_NHitsString=<b>{0} hits</b>, last at {1}, <a href='#'>report</a>",
    "StopwatchProfilingPoint_NoResultsString=No results available",
    "StopwatchProfilingPoint_ReportAccessDescr=Report of {0}",
    "StopwatchProfilingPoint_NoHitsString=no hits",
    "StopwatchProfilingPoint_HeaderTypeString=<b>Type:</b> {0}",
    "StopwatchProfilingPoint_HeaderEnabledString=<b>Enabled:</b> {0}",
    "StopwatchProfilingPoint_HeaderProjectString=<b>Project:</b> {0}",
    "StopwatchProfilingPoint_HeaderLocationString=<b>Location:</b> {0}, line {1}",
    "StopwatchProfilingPoint_HeaderStartLocationString=<b>Start location:</b> {0}, line {1}",
    "StopwatchProfilingPoint_HeaderEndLocationString=<b>Stop location:</b> {0}, line {1}",
    "StopwatchProfilingPoint_HeaderMeasureDurationString=<b>Measure:</b> Timestamp and duration",
    "StopwatchProfilingPoint_HeaderMeasureTimestampString=<b>Measure:</b> Timestamp",
    "StopwatchProfilingPoint_HeaderHitsString=<b>Hits:</b> {0}",
    "StopwatchProfilingPoint_HitString=<b>{0}.</b> hit at <b>{1}</b>",
    "StopwatchProfilingPoint_HitDurationPendingString=<b>{0}.</b> hit at <b>{1}</b>, duration pending...",
    "StopwatchProfilingPoint_HitDurationKnownString=<b>{0}.</b> hit at <b>{1}</b>, duration <b>{2} &micro;s</b>",
    "StopwatchProfilingPoint_DataString=Data:",
    "StopwatchProfilingPoint_AnnotationStartString={0} (start)",
    "StopwatchProfilingPoint_AnnotationEndString={0} (end)"
})
public final class StopwatchProfilingPoint extends CodeProfilingPoint.Paired implements PropertyChangeListener {
    //~ Inner Classes ------------------------------------------------------------------------------------------------------------

    private class Annotation extends CodeProfilingPoint.Annotation {
        //~ Instance fields ------------------------------------------------------------------------------------------------------

        private boolean isStartAnnotation;

        //~ Constructors ---------------------------------------------------------------------------------------------------------

        public Annotation(boolean isStartAnnotation) {
            super();
            this.isStartAnnotation = isStartAnnotation;
        }

        //~ Methods --------------------------------------------------------------------------------------------------------------

        @Override
        public String getAnnotationType() {
            return StopwatchProfilingPoint.this.isEnabled() ? ANNOTATION_ENABLED : ANNOTATION_DISABLED;
        }

        @Override
        public String getShortDescription() {
            if (!usesEndLocation()) {
                return getName();
            }

            return isStartAnnotation ? Bundle.StopwatchProfilingPoint_AnnotationStartString(getName())
                                     : Bundle.StopwatchProfilingPoint_AnnotationEndString(getName());
        }

        @Override
        public CodeProfilingPoint profilingPoint() {
            return StopwatchProfilingPoint.this;
        }
    }

    private class Report extends TopComponent {
        //~ Static fields/initializers -------------------------------------------------------------------------------------------

        private static final String START_LOCATION_URLMASK = "file:/1"; // NOI18N
        private static final String END_LOCATION_URLMASK = "file:/2"; // NOI18N

        //~ Instance fields ------------------------------------------------------------------------------------------------------

        private HTMLTextArea dataArea;
        private HTMLTextArea headerArea;

        //~ Constructors ---------------------------------------------------------------------------------------------------------

        public Report() {
            initDefaults();
            initComponents();
            refreshData();
        }

        //~ Methods --------------------------------------------------------------------------------------------------------------

        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        protected String preferredID() {
            return this.getClass().getName();
        }

        void refreshData() {
            StringBuilder headerAreaTextBuilder = new StringBuilder();

            headerAreaTextBuilder.append(getHeaderName());
            headerAreaTextBuilder.append("<br>"); // NOI18N
            headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            headerAreaTextBuilder.append(getHeaderType());
            headerAreaTextBuilder.append("<br>"); // NOI18N
            headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            headerAreaTextBuilder.append(getHeaderEnabled());
            headerAreaTextBuilder.append("<br>"); // NOI18N
            headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            headerAreaTextBuilder.append(getHeaderProject());
            headerAreaTextBuilder.append("<br>"); // NOI18N
            headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            headerAreaTextBuilder.append(getHeaderStartLocation());
            headerAreaTextBuilder.append("<br>"); // NOI18N

            if (StopwatchProfilingPoint.this.usesEndLocation()) {
                headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                headerAreaTextBuilder.append(getHeaderEndLocation());
                headerAreaTextBuilder.append("<br>");
            } // NOI18N

            headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            headerAreaTextBuilder.append(getHeaderMeasureLocation());
            headerAreaTextBuilder.append("<br>"); // NOI18N
            headerAreaTextBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            headerAreaTextBuilder.append(getHeaderHitsCount()); // NOI18N

            headerArea.setText(headerAreaTextBuilder.toString());

            StringBuilder dataAreaTextBuilder = new StringBuilder();

            synchronized(resultsSync) {
                if (results.size() == 0) {
                    dataAreaTextBuilder.append("&nbsp;&nbsp;&lt;").append(Bundle.StopwatchProfilingPoint_NoHitsString()).append("&gt;"); // NOI18N
                } else {
                    for (int i = 0; i < results.size(); i++) {
                        dataAreaTextBuilder.append("&nbsp;&nbsp;");
                        dataAreaTextBuilder.append(getDataResultItem(i));
                        dataAreaTextBuilder.append("<br>"); // NOI18N
                    }
                }
            }

            dataArea.setText(dataAreaTextBuilder.toString());
        }

        void refreshProperties() {
            setName(StopwatchProfilingPoint.this.getName());
            setIcon(((ImageIcon) StopwatchProfilingPoint.this.getFactory().getIcon()).getImage());
            getAccessibleContext().setAccessibleDescription(Bundle.StopwatchProfilingPoint_ReportAccessDescr(getName()));
        }

        private String getDataResultItem(int index) {
            synchronized(resultsSync) {
                Result result = results.get(index);

                // TODO: enable once thread name by id is available
                //String threadName = Utils.getThreadName(result.getThreadID());
                //String threadClassName = Utils.getThreadClassName(result.getThreadID());
                //String threadInformation = (threadName == null ? "&lt;unknown thread&gt;" : (threadClassName == null ? threadName : threadName + " (" + threadClassName + ")"));
                String hitTime = Utils.formatProfilingPointTimeHiRes(result.getTimestamp());

                if (!StopwatchProfilingPoint.this.usesEndLocation()) {
                    //return "<b>" + (index + 1) + ".</b> hit at <b>" + hitTime + "</b> by " + threadInformation;
                    return Bundle.StopwatchProfilingPoint_HitString((index + 1), hitTime);
                } else if (result.getEndTimestamp() == -1) {
                    //return "<b>" + (index + 1) + ".</b> hit at <b>" + hitTime + "</b>, duration pending..., thread " + threadInformation;
                    return Bundle.StopwatchProfilingPoint_HitDurationPendingString((index + 1), hitTime);
                } else {
                    //return "<b>" + (index + 1) + ".</b> hit at <b>" + hitTime + "</b>, duration <b>" + Utils.getDurationInMicroSec(result.getTimestamp(),result.getEndTimestamp()) + " &micro;s</b>, thread " + threadInformation;
                    return Bundle.StopwatchProfilingPoint_HitDurationKnownString(
                                (index + 1), 
                                hitTime,
                                Utils.getDurationInMicroSec(result.getTimestamp(),
                                    result.getEndTimestamp() - result.getTimeAdjustment()));
                }
            }
        }

        private String getHeaderEnabled() {
            return Bundle.StopwatchProfilingPoint_HeaderEnabledString(StopwatchProfilingPoint.this.isEnabled());
        }

        private String getHeaderEndLocation() {
            CodeProfilingPoint.Location location = StopwatchProfilingPoint.this.getEndLocation();
            File file = new File(location.getFile());
            String shortFileName = file.getName();
            int lineNumber = location.getLine();
            String locationUrl = "<a href='" + END_LOCATION_URLMASK + "'>"; // NOI18N

            return Bundle.StopwatchProfilingPoint_HeaderEndLocationString(locationUrl + shortFileName, lineNumber)
                   + "</a>"; // NOI18N
        }

        private String getHeaderHitsCount() {
            synchronized(resultsSync) {
                return Bundle.StopwatchProfilingPoint_HeaderHitsString(results.size());
            }
        }

        private String getHeaderMeasureLocation() {
            return StopwatchProfilingPoint.this.usesEndLocation() ?
                        Bundle.StopwatchProfilingPoint_HeaderMeasureDurationString() : 
                        Bundle.StopwatchProfilingPoint_HeaderMeasureTimestampString();
        }

        private String getHeaderName() {
            return "<h2><b>" + StopwatchProfilingPoint.this.getName() + "</b></h2>"; // NOI18N
        }

        private String getHeaderProject() {
            return Bundle.StopwatchProfilingPoint_HeaderProjectString(
                        ProjectUtilities.getDisplayName(StopwatchProfilingPoint.this.getProject()));
        }

        private String getHeaderStartLocation() {
            CodeProfilingPoint.Location location = StopwatchProfilingPoint.this.getStartLocation();
            File file = new File(location.getFile());
            String shortFileName = file.getName();
            int lineNumber = location.getLine();
            String locationUrl = "<a href='" + START_LOCATION_URLMASK + "'>"; // NOI18N

            return StopwatchProfilingPoint.this.usesEndLocation()
                   ? (Bundle.StopwatchProfilingPoint_HeaderStartLocationString(locationUrl + shortFileName, lineNumber)
                   + "</a>")
                   : (Bundle.StopwatchProfilingPoint_HeaderLocationString(locationUrl + shortFileName, lineNumber)
                   + "</a>"); // NOI18N
        }

        private String getHeaderType() {
            return Bundle.StopwatchProfilingPoint_HeaderTypeString(StopwatchProfilingPoint.this.getFactory().getType());
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel contentsPanel = new JPanel(new BorderLayout());
            contentsPanel.setBackground(UIUtils.getProfilerResultsBackground());
            contentsPanel.setOpaque(true);
            contentsPanel.setBorder(BorderFactory.createMatteBorder(0, 15, 15, 15, UIUtils.getProfilerResultsBackground()));

            headerArea = new HTMLTextArea() {
                    protected void showURL(URL url) {
                        String urlString = url.toString();

                        if (START_LOCATION_URLMASK.equals(urlString)) {
                            Utils.openLocation(StopwatchProfilingPoint.this.getStartLocation());
                        } else if (StopwatchProfilingPoint.this.usesEndLocation()) {
                            Utils.openLocation(StopwatchProfilingPoint.this.getEndLocation());
                        }
                    }
                };

            JScrollPane headerAreaScrollPane = new JScrollPane(headerArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                               JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            headerAreaScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 15, 0, UIUtils.getProfilerResultsBackground()));
            headerAreaScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
            contentsPanel.add(headerAreaScrollPane, BorderLayout.NORTH);

            dataArea = new HTMLTextArea();

            JScrollPane dataAreaScrollPane = new JScrollPane(dataArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            TitledBorder tb = new TitledBorder(Bundle.StopwatchProfilingPoint_DataString());
            tb.setTitleFont(Utils.getTitledBorderFont(tb).deriveFont(Font.BOLD));
            tb.setTitleColor(javax.swing.UIManager.getColor("Label.foreground")); // NOI18N
            dataAreaScrollPane.setBorder(tb);
            dataAreaScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
            dataAreaScrollPane.setBackground(UIUtils.getProfilerResultsBackground());
            contentsPanel.add(dataAreaScrollPane, BorderLayout.CENTER);

            add(contentsPanel, BorderLayout.CENTER);
        }

        private void initDefaults() {
            refreshProperties();
            setFocusable(true);
        }
    }

    private static class Result {
        //~ Instance fields ------------------------------------------------------------------------------------------------------

        private final int threadId;
        private final long timestamp;
        private long endTimestamp = -1;
        private long timeAdjustment;

        //~ Constructors ---------------------------------------------------------------------------------------------------------

        private Result(long timestamp, int threadId) {
            this.timestamp = timestamp;
            this.threadId = threadId;
        }

        //~ Methods --------------------------------------------------------------------------------------------------------------

        private void setEndTimestamp(long endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        private long getEndTimestamp() {
            return endTimestamp;
        }

        private int getThreadID() {
            return threadId;
        }

        private long getTimeAdjustment() {
            return timeAdjustment;
        }

        private long getTimestamp() {
            return timestamp;
        }

        private void timeAdjust(long timeDiff) {
            timeAdjustment += timeDiff;
        }
    }

    //~ Static fields/initializers -----------------------------------------------------------------------------------------------

    // --- Implementation --------------------------------------------------------
    private static final String ANNOTATION_ENABLED = "stopwatchProfilingPoint"; // NOI18N
    private static final String ANNOTATION_DISABLED = "stopwatchProfilingPointD"; // NOI18N

    //~ Instance fields ----------------------------------------------------------------------------------------------------------

    private Annotation endAnnotation;
    private Annotation startAnnotation;
    private List<Result> results = new ArrayList();
    private final Object resultsSync = new Object();
    private WeakReference<Report> reportReference;

    //~ Constructors -------------------------------------------------------------------------------------------------------------
    public StopwatchProfilingPoint(String name, Location startLocation, Location endLocation, Lookup.Provider project, ProfilingPointFactory factory) {
        super(name, startLocation, endLocation, project, factory);
        getChangeSupport().addPropertyChangeListener(this);
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------

    public boolean hasResults() {
        synchronized(resultsSync) {
            return results.size() > 0;
        }
    }

    public void hideResults() {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (hasReport()) {
                        getReport().close();
                    }
                }
            });
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (hasReport()) {
            if (evt.getPropertyName() == PROPERTY_NAME) {
                getReport().refreshProperties();
            }

            getReport().refreshData();
        }
    }

    public void showResults(URL url) {
        TopComponent topComponent = getReport();
        topComponent.open();
        topComponent.requestActive();
    }

    protected CodeProfilingPoint.Annotation getEndAnnotation() {
        if (!usesEndLocation()) {
            endAnnotation = null;
        } else if (endAnnotation == null) {
            endAnnotation = new Annotation(false);
        }

        return endAnnotation;
    }

    protected String getResultsText() {
        synchronized(resultsSync) {
            if (hasResults()) {
                return (results.size() == 1)
                       ? Bundle.StopwatchProfilingPoint_OneHitString(
                            Utils.formatProfilingPointTime(results.get(results.size() - 1).getTimestamp()))
                       : Bundle.StopwatchProfilingPoint_NHitsString(
                            results.size(),
                            Utils.formatProfilingPointTime(results.get(results.size() - 1).getTimestamp()));
            } else {
                return Bundle.StopwatchProfilingPoint_NoResultsString();
            }
        }
    }

    protected CodeProfilingPoint.Annotation getStartAnnotation() {
        if (startAnnotation == null) {
            startAnnotation = new Annotation(true);
        }

        return startAnnotation;
    }

    protected void timeAdjust(final int threadId, final long timeDiff0, final long timeDiff1) {
        if (usesEndLocation()) { // we have start and stop StopwatchProfilingPoint
            synchronized(resultsSync) {
                for (Result result : results) {
                    if ((result.getEndTimestamp() == -1) && (result.getThreadID() == threadId)) {
                        //System.out.println("Time adjust thread "+threadId+" time "+Long.toHexString(timeDiff1)+ " diff "+Long.toHexString(timeDiff0));
                        result.timeAdjust(timeDiff0);
                    }
                }
            }
        }
    }

    protected void updateCustomizer(ValidityAwarePanel c) {
        StopwatchCustomizer customizer = (StopwatchCustomizer) c;
        customizer.setPPName(getName());
        customizer.setPPStartLocation(getStartLocation());
        customizer.setPPEndLocation(getEndLocation());
    }

    protected boolean usesEndLocation() {
        return getEndLocation() != null;
    }

    void setValues(ValidityAwarePanel c) {
        StopwatchCustomizer customizer = (StopwatchCustomizer) c;
        setName(customizer.getPPName());
        setStartLocation(customizer.getPPStartLocation());
        setEndLocation(customizer.getPPEndLocation());
        
        Utils.checkLocation(this);
    }

    void hit(RuntimeProfilingPoint.HitEvent hitEvent, int index) {
        synchronized(resultsSync) {
            if (!usesEndLocation() || (index == 0)) {
                // TODO: should endpoint hit before startpoint hit be processed somehow?
                results.add(new Result(hitEvent.getTimestamp(), hitEvent.getThreadId()));

                //System.out.println("Time start  thread "+hitEvent.getThreadId()+" time "+Long.toHexString(hitEvent.getTimestamp()));
            } else {
                for (Result result : results) {
                    if ((result.getEndTimestamp() == -1) && (result.getThreadID() == hitEvent.getThreadId())) {
                        result.setEndTimestamp(hitEvent.getTimestamp());

                        //System.out.println("Time end    thread "+hitEvent.getThreadId()+" time "+Long.toHexString(hitEvent.getTimestamp()));
                        break;
                    }
                }

                // TODO: endpoind hit without startpoint hit, what to do?
            }
        }

        getChangeSupport().firePropertyChange(PROPERTY_RESULTS, false, true);
    }

    void reset() {
        synchronized(resultsSync) {
            boolean change = results.size() > 0;
            results.clear();

            if (change) {
                getChangeSupport().firePropertyChange(PROPERTY_RESULTS, false, true);
            }
        }
    }

    private Report getReport() {
        if (hasReport()) {
            return reportReference.get();
        }

        Report report = new Report();
        reportReference = new WeakReference(report);

        return report;
    }

    private boolean hasReport() {
        return (reportReference != null) && (reportReference.get() != null);
    }
}
