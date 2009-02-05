/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.dlight.dtrace.collector.support;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.modules.dlight.api.execution.AttachableTarget;
import org.netbeans.modules.dlight.api.execution.DLightTarget;
import org.netbeans.modules.dlight.api.execution.DLightTarget.State;
import org.netbeans.modules.dlight.api.execution.ValidationStatus;
import org.netbeans.modules.dlight.api.execution.ValidationListener;
import org.netbeans.modules.dlight.api.storage.DataRow;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata;
import org.netbeans.modules.dlight.dtrace.collector.DTDCConfiguration;
import org.netbeans.modules.dlight.dtrace.collector.impl.DTDCConfigurationAccessor;
import org.netbeans.modules.dlight.management.api.DLightManager;
import org.netbeans.modules.dlight.spi.collector.DataCollector;
import org.netbeans.modules.dlight.spi.indicator.IndicatorDataProvider;
import org.netbeans.modules.dlight.spi.storage.DataStorage;
import org.netbeans.modules.dlight.spi.storage.DataStorageType;
import org.netbeans.modules.dlight.spi.support.DataStorageTypeFactory;
import org.netbeans.modules.dlight.impl.SQLDataStorage;
import org.netbeans.modules.dlight.util.DLightExecutorService;
import org.netbeans.modules.dlight.util.DLightLogger;
import org.netbeans.modules.dlight.util.Util;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.NativeProcessBuilder;
import org.netbeans.modules.nativeexecution.api.ObservableAction;
import org.netbeans.modules.nativeexecution.api.ObservableActionListener;
import org.netbeans.modules.nativeexecution.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.util.CommonTasksSupport;
import org.netbeans.modules.nativeexecution.util.HostInfoUtils;
import org.netbeans.modules.nativeexecution.util.HostNotConnectedException;
import org.netbeans.modules.nativeexecution.util.SolarisPrivilegesSupport;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

/**
 * Collector which collects data using DTrace sctiprs.
 * You should describe data collected using list of
 * {@link org.netbeans.modules.dlight.core.storage.model.DataTableMetadata}.
 * You can define your own implementation of
 * {@link org.netbeans.modules.dlight.dtrace.collector.DtraceParser}
 */
public final class DtraceDataCollector
        extends IndicatorDataProvider<DTDCConfiguration>
        implements DataCollector<DTDCConfiguration> {

    private static final List<String> ultimateDTracePrivilegesList =
            Arrays.asList(
            new String[]{
                DTDCConfiguration.DTRACE_KERNEL,
                DTDCConfiguration.DTRACE_PROC,
                DTDCConfiguration.DTRACE_USER
            });
    private static final String cmd_dtrace = "/usr/sbin/dtrace"; // NOI18N
    private static final Logger log =
            DLightLogger.getLogger(DtraceDataCollector.class);
    private List<String> requiredPrivilegesList;
    private DataTableMetadata tableMetaData = null;
    private String localScriptPath;
    private String extraArgs;
    private String scriptPath;
    private Future<Integer> dtraceTask = null;
    private DTDCConfiguration configuration;
    private ValidationStatus validationStatus =
            ValidationStatus.initialStatus();
    private List<ValidationListener> validationListeners =
            Collections.synchronizedList(new ArrayList<ValidationListener>());
    private String command;
    private String argsTemplate;
    private DataStorage storage;
    private List<DataTableMetadata> dataTablesMetadata;
    private DtraceParser parser;
    private final List<DataRow> indicatorDataBuffer = new ArrayList<DataRow>();
    private boolean isSlave;
    private int indicatorFiringFactor;
    private ProcessLineCallback callback = new ProcessLineCallBackImpl();

    DtraceDataCollector(DTDCConfiguration configuration) {
        this.command = cmd_dtrace;
        this.argsTemplate = null;

        final DTDCConfigurationAccessor cfgInfo =
                DTDCConfigurationAccessor.getDefault();

        this.dataTablesMetadata = cfgInfo.getDatatableMetadata(configuration);

        this.tableMetaData =
                (dataTablesMetadata != null && dataTablesMetadata.size() > 0)
                ? dataTablesMetadata.get(0)
                : null;

        if (cfgInfo.isStackSupportEnabled(configuration)) {
            this.parser = new DtraceDataAndStackParser(tableMetaData);
        } else {
            this.parser = cfgInfo.getParser(configuration) == null
                    ? (tableMetaData != null ? new DtraceParser(tableMetaData)
                    : (DtraceParser) null) : cfgInfo.getParser(configuration);
        }

        // super(cmd_dtrace, null,
//            configuration.getParser() == null
//        ? (configuration.getDatatableMetadata() != null &&
//        configuration.getDatatableMetadata().size() > 0
//        ? new DtraceParser(configuration.getDatatableMetadata().get(0))
//        : (DtraceParser) null)
//        : configuration.getParser(), configuration.getDatatableMetadata());


        this.localScriptPath = cfgInfo.getScriptPath(configuration);
        this.extraArgs = cfgInfo.getArgs(configuration);

        this.requiredPrivilegesList =
                cfgInfo.getRequiredPrivileges(configuration) == null
                ? ultimateDTracePrivilegesList
                : cfgInfo.getRequiredPrivileges(configuration);

        this.configuration = configuration;

        this.indicatorFiringFactor =
                cfgInfo.getIndicatorFiringFactor(configuration);
    }

    void setProcessLineCallback(ProcessLineCallback callback) {
        this.callback = callback;
    }

    void setSlave(boolean isSlave) {
        this.isSlave = isSlave;
    }

    String getLocalScriptPath() {
        return localScriptPath;
    }

    void setLocalScriptPath(String path) {
        localScriptPath = path;
    }

    ProcessLineCallback getProcessLineCallback() {
        return callback;
    }

    protected DataStorage getStorage() {
        return storage;
    }

    /**
     * The types of storage this collector supports
     * @return returns list of
     * {@link org.netbeans.modules.dlight.core.storage.model.DataStorageType}
     * data collector can put data into
     */
    public Collection<DataStorageType> getSupportedDataStorageTypes() {
        DataStorageTypeFactory dstf = DataStorageTypeFactory.getInstance();
        DataStorageType dst =
                dstf.getDataStorageType(SQLDataStorage.SQL_DATA_STORAGE_TYPE);

        return Arrays.asList(dst);
    }

    public boolean isAttachable() {
        return true;
    }

    @Override
    public void init(DataStorage storage, DLightTarget target) {
        this.storage = storage;

        if (isSlave) {
            return;
        }

        ExecutionEnvironment execEnv = target.getExecEnv();

        if (execEnv.isLocal()) {
            // No need to copy file on localhost -
            // just esnure execution permissions...
            scriptPath = localScriptPath;
            Util.setExecutionPermissions(Arrays.asList(scriptPath));
        } else {
            File script = new File(localScriptPath);
            scriptPath = "/tmp/" + script.getName(); // NOI18N

            CommonTasksSupport.copyLocalFile(
                    execEnv, localScriptPath, scriptPath, 777, null);
        }
    }

    public String getCmd() {
        return command;
    }

    public String[] getArgs() {
        return null;
    }

    public List<DataTableMetadata> getDataTablesMetadata() {
        return dataTablesMetadata;
    }

    /** 
     * override this if you need to add extra parameters to the DTrace script
     */
    protected String getCollectorTaskExtraParams() {
        return extraArgs;
    }

    private void targetFinished(DLightTarget target) {
        if (!isSlave) {
            if (dtraceTask != null && !dtraceTask.isDone()) {
                // It could be already done here, because tracked process is
                // finished and, depending on command-line utility, it may exit
                // as well... But, if not - terminate it.
                log.fine("Stopping DtraceDataCollector: " + // NOI18N
                        dtraceTask.toString());

                dtraceTask.cancel(true);
                dtraceTask = null;
            }
        }

        synchronized (indicatorDataBuffer) {
            if (!indicatorDataBuffer.isEmpty()) {
                notifyIndicators(indicatorDataBuffer);
                indicatorDataBuffer.clear();
            }
        }
    }

    private static String loc(String key, String... param) {
        return NbBundle.getMessage(DtraceDataCollector.class, key, param);
    }

    private ValidationStatus doValidation(final DLightTarget target) {
        DLightLogger.assertNonUiThread();

        ValidationStatus result = null;
        boolean fileExists = false;
        boolean connected = true;

        try {
            fileExists = HostInfoUtils.fileExists(target.getExecEnv(), command);
        } catch (HostNotConnectedException ex) {
            connected = false;
        }

        if (connected) {
            if (fileExists) {
                result = ValidationStatus.validStatus();
            } else {
                result = ValidationStatus.invalidStatus(
                        loc("ValidationStatus.CommandNotFound", // NOI18N
                        command));
            }
        } else {
            ObservableActionListener<Boolean> listener =
                    new ObservableActionListener<Boolean>() {

                        public void actionCompleted(Action source, Boolean result) {
                            DLightManager.getDefault().revalidateSessions();
                        }

                        public void actionStarted(Action source) {
                        }
                    };

            final ConnectionManager mgr = ConnectionManager.getInstance();

            ObservableAction<Boolean> connectAction =
                    mgr.getConnectToAction(target.getExecEnv());

            connectAction.addObservableActionListener(listener);

            result = ValidationStatus.unknownStatus(
                    loc("ValidationStatus.HostNotConnected"), // NOI18N
                    connectAction);
        }

        ExecutionEnvironment execEnv = target.getExecEnv();

        if (!result.isValid()) {
            return result;
        }

        // /usr/sbin/dtrace exists...
        // check for permissions ...
        SolarisPrivilegesSupport sps =
                SolarisPrivilegesSupport.getInstance();

        boolean status = sps.hasPrivileges(execEnv, requiredPrivilegesList);

        ObservableAction<Boolean> requestPrivilegesAction =
                sps.requestPrivilegesAction(execEnv, requiredPrivilegesList);

        ObservableActionListener al =
                new ObservableActionListener<Boolean>() {

                    public void actionCompleted(Action source, Boolean result) {
                        DLightManager.getDefault().revalidateSessions();
                    }

                    public void actionStarted(Action source) {
                    }
                };

        requestPrivilegesAction.addObservableActionListener(al);

        if (!status) {
            result = result.merge(ValidationStatus.unknownStatus(
                    loc("DTraceDataCollector_Status_NotEnoughPrivileges"), // NOI18N
                    requestPrivilegesAction));
        }

        return result;
    }

    public Future<ValidationStatus> validate(final DLightTarget target) {
        Callable<ValidationStatus> valiationTask =
                new Callable<ValidationStatus>() {

                    public ValidationStatus call() throws Exception {
                        if (validationStatus.isValid()) {
                            return validationStatus;
                        }

                        ValidationStatus oldStatus = validationStatus;
                        ValidationStatus newStatus = doValidation(target);

                        notifyStatusChanged(oldStatus, newStatus);

                        validationStatus = newStatus;
                        return newStatus;
                    }
                };

        return DLightExecutorService.service.submit(valiationTask);
    }

    public void invalidate() {
        validationStatus = ValidationStatus.initialStatus();
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    private void targetStarted(DLightTarget target) {
        if (isSlave) {
            return;
        }

        String taskCommand = scriptPath; //"pfexec " + scriptPath;

        if (target instanceof AttachableTarget) {
            AttachableTarget at = (AttachableTarget) target;
            taskCommand += " " + at.getPID(); // NOI18N
        }

        final String extraParams = getCollectorTaskExtraParams();
        if (extraParams != null) {
            taskCommand += " " + extraParams; // NOI18N
        }

        NativeProcessBuilder npb = new NativeProcessBuilder(taskCommand);

        ExecutionDescriptor descr = new ExecutionDescriptor();
        descr = descr.outProcessorFactory(new DtraceInputProcessorFactory());
        descr = descr.errProcessorFactory(new StdErrRedirectorFactory());
        descr = descr.inputOutput(InputOutput.NULL);

        ExecutionService execService = ExecutionService.newService(
                npb, descr, "DTraceDataCollector " + taskCommand); // NOI18N

        dtraceTask = execService.run();

        log.fine("DtraceDataCollector (" + dtraceTask.toString() + // NOI18N
                ") for " + taskCommand + " STARTED"); // NOI18N
    }

    public void addValidationListener(ValidationListener listener) {
        if (!validationListeners.contains(listener)) {
            validationListeners.add(listener);
        }
    }

    public void removeValidationListener(ValidationListener listener) {
        validationListeners.remove(listener);
    }

    protected void notifyStatusChanged(
            ValidationStatus oldStatus, ValidationStatus newStatus) {
        if (oldStatus.equals(newStatus)) {
            return;
        }

        ValidationListener[] ll =
                validationListeners.toArray(new ValidationListener[0]);

        for (ValidationListener l : ll) {
            l.validationStateChanged(this, oldStatus, newStatus);
        }
    }

    public void targetStateChanged(
            DLightTarget source, State oldState, State newState) {

        switch (newState) {
            case RUNNING:
                targetStarted(source);
                break;
            case FAILED:
                targetFinished(source);
                break;
            case TERMINATED:
                targetFinished(source);
                break;
            case DONE:
                targetFinished(source);
                break;
            case STOPPED:
                targetFinished(source);
                return;
        }
    }

    private final class ProcessLineCallBackImpl implements ProcessLineCallback {

        public void processLine(String line) {
            DataRow dataRow = parser.process(line);
            if (dataRow != null) {
                if (storage != null && tableMetaData != null) {
                    storage.addData(
                            tableMetaData.getName(), Arrays.asList(dataRow));
                }
                synchronized (indicatorDataBuffer) {
                    if (indicatorDataBuffer.size() >= indicatorFiringFactor) {
                        notifyIndicators(indicatorDataBuffer);
                        indicatorDataBuffer.clear();
                    }
                    indicatorDataBuffer.add(dataRow);
                }
            }
        }
    }

    private class DtraceInputProcessorFactory implements InputProcessorFactory {

        public InputProcessor newInputProcessor(InputProcessor p) {
            return InputProcessors.bridge(new LineProcessor() {

                @Override
                public void processLine(String line) {
                    callback.processLine(line);
                }

                public void reset() {
                }

                public void close() {
                }
            });
        }
    }

    private static class StdErrRedirectorFactory
            implements InputProcessorFactory {

        public InputProcessor newInputProcessor(InputProcessor p) {
            return InputProcessors.copying(new OutputStreamWriter(System.err));
        }
    }
}
