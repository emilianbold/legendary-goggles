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
package org.netbeans.modules.dlight.perfan.spi;

import java.net.ConnectException;
import org.netbeans.modules.dlight.api.execution.DLightTarget.State;
import org.netbeans.modules.dlight.perfan.SunStudioDCConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.modules.dlight.api.execution.AttachableTarget;
import org.netbeans.modules.dlight.api.execution.DLightTarget;
import org.netbeans.modules.dlight.api.execution.ValidationListener;
import org.netbeans.modules.dlight.api.execution.ValidationStatus;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata.Column;
import org.netbeans.modules.dlight.management.api.DLightManager;
import org.netbeans.modules.dlight.perfan.SunStudioDCConfiguration.CollectedInfo;
import org.netbeans.modules.dlight.perfan.storage.impl.PerfanDataStorage;
import org.netbeans.modules.dlight.spi.collector.DataCollector;
import org.netbeans.modules.dlight.spi.storage.DataStorage;
import org.netbeans.modules.dlight.spi.storage.DataStorageType;
import org.netbeans.modules.dlight.spi.support.DataStorageTypeFactory;
import org.netbeans.modules.dlight.util.DLightExecutorService;
import org.netbeans.modules.dlight.util.DLightLogger;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.NativeProcessBuilder;
import org.netbeans.modules.nativeexecution.api.util.AsynchronousAction;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.openide.windows.InputOutput;

/**
 * This class will represent SunStudio Performance Analyzer collect
 * which will be used as DataCollector
 */
public class SunStudioDataCollector implements DataCollector<SunStudioDCConfiguration> {

    public static final Column TOP_FUNCTION_INFO = new Column("currentFunction", String.class);
    private static final String ID = "PerfanDataStorage";
    private static final List<DataStorageType> supportedStorageTypes = Arrays.asList(DataStorageTypeFactory.getInstance().getDataStorageType(ID));
    private static final DataTableMetadata dataTableMetadata = new DataTableMetadata("idbe", Arrays.asList(TOP_FUNCTION_INFO));
    private static final Logger log = DLightLogger.getLogger(SunStudioDataCollector.class);
    private final List<ValidationListener> validationListeners;
    private final List<CollectedInfo> collectedInfoList;
    private ValidationStatus validationStatus = ValidationStatus.initialStatus();
//    private IndicatorsNotifyerTask indicatorsNotifyerTask;
    private PerfanDataStorage storage = null;
    private String experimentDir = null;
    private String collect = null;
    private Future<Integer> collectTask = null;
    private ExecutionEnvironment execEnv = null;

    SunStudioDataCollector(List<CollectedInfo> collectedInfoList) {
        this.collectedInfoList = Collections.synchronizedList(
                new ArrayList<CollectedInfo>(collectedInfoList));

        this.validationListeners = Collections.synchronizedList(
                new ArrayList<ValidationListener>());

    }

    public Future<ValidationStatus> validate(final DLightTarget targetToValidate) {
        return DLightExecutorService.service.submit(new Callable<ValidationStatus>() {

            public ValidationStatus call() throws Exception {
                if (validationStatus.isValid()) {
                    return validationStatus;
                }

                ValidationStatus oldStatus = validationStatus;
                ValidationStatus newStatus = doValidation(targetToValidate);

                notifyStatusChanged(oldStatus, newStatus);

                validationStatus = newStatus;
                return newStatus;
            }
        });
    }

    public void invalidate() {
        validationStatus = ValidationStatus.initialStatus();
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    protected synchronized ValidationStatus doValidation(DLightTarget targetToValidate) {
        String os = null;
        final ExecutionEnvironment targetEnv = targetToValidate.getExecEnv();
        
        try {
            os = HostInfoUtils.getOS(targetEnv);
        } catch (ConnectException ex) {
            final ConnectionManager mgr = ConnectionManager.getInstance();
            Runnable onConnect = new Runnable() {

                public void run() {
                    DLightManager.getDefault().revalidateSessions();
                }
            };
            
            AsynchronousAction connectAction = mgr.getConnectToAction(targetEnv, onConnect);

            return ValidationStatus.unknownStatus("Host is not connected...", // NOI18N
                    connectAction);
        }

        if (!"SunOS".equals(os)) {
            return ValidationStatus.invalidStatus("SunStudioDataCollector works on SunOS only."); // NOI18N
        }

        ValidationStatus result = null;

        // TODO: files copying...????
        result = ValidationStatus.validStatus();

        return result;
    }

    public void addValidationListener(ValidationListener listener) {
        if (!validationListeners.contains(listener)) {
            validationListeners.add(listener);
        }
    }

    public void removeValidationListener(ValidationListener listener) {
        validationListeners.remove(listener);
    }

    protected void notifyStatusChanged(ValidationStatus oldStatus, ValidationStatus newStatus) {
        if (newStatus.equals(oldStatus)) {
            return;
        }

        for (ValidationListener validationListener : validationListeners) {
            validationListener.validationStateChanged(this, oldStatus, newStatus);
        }
    }

    /**
     * Returns unmodifiable list of information collected
     * @return an unmodifiable view of the {@link CollectedInfo}
     */
    public List<CollectedInfo> getCollectedInfo() {
        return Collections.unmodifiableList(collectedInfoList);
    }

    void addCollectedInfo(List<CollectedInfo> collectedInfo) {
        //should add if do not have yet
        for (CollectedInfo c : collectedInfo) {
            if (!collectedInfoList.contains(c)) {
                collectedInfoList.add(c);
            }
        }
    }

    public String getExperimentDirectory() {
        return experimentDir;
    }

    // Is called before target has been started
    public void init(DataStorage dataStorage, DLightTarget target) {
        if (!(dataStorage instanceof PerfanDataStorage)) {
            throw new IllegalArgumentException("You can not use storage " + dataStorage + " for PerfanDataCollector!");
        }

        log.fine("Initialize perfan collector and storage for target " + target.toString());
        log.fine("Prepare PerfanDataCollector. Clean directory " + experimentDir);

//        NativeTask rmTask = CommonTasksSupport.getRemoveDirectoryTask(
//                target.getExecEnv(), experimentDir, true, null);
//
//        try {
//            rmTask.invoke(true);
//        } catch (Exception ex) {
//        }

        storage = (PerfanDataStorage) dataStorage;
        storage.setCollector(this);

    }

    public ExecutionEnvironment getExecEnv() {
        return execEnv;
    }

    private void targetStarted(DLightTarget target) {

        if (isAttachable()) {
            // i.e. should start separate process
            execEnv = target.getExecEnv();
            AttachableTarget at = (AttachableTarget) target;
            NativeProcessBuilder npb = new NativeProcessBuilder(execEnv, collect);
            npb = npb.setArguments("-P", "" + at.getPID(), "-o", experimentDir);
            ExecutionDescriptor descr = new ExecutionDescriptor().inputOutput(InputOutput.NULL);
            ExecutionService service = ExecutionService.newService(npb, descr, "collect");
            collectTask = service.run();
        } else {
            execEnv = target.getExecEnv();
        }
    }

    private void targetFinished(DLightTarget target) {
        log.fine("Stopping PerfanDataCollector: " + collect);

        if (isAttachable()) {
            // i.e. separate process
            collectTask.cancel(true);
        } else {
            // i.e. this means that exactly this collector finished
            // do nothing
        }

        collectTask = null;
        execEnv = null;

//    if (indicatorsNotifyerTask != null) {
//      DLightGlobalTimer.getInstance().unregisterTimerTask(indicatorsNotifyerTask);
//    }
    }

    public Collection<DataStorageType> getSupportedDataStorageTypes() {
        return supportedStorageTypes;
    }

    public List<DataTableMetadata> getDataTablesMetadata() {
        return Arrays.asList(dataTableMetadata);
    }

    public boolean isAttachable() {
        if (collectedInfoList.contains(CollectedInfo.SYNCHRONIZARION)) {
            return false;
        }
        return true;
    }

    public String getCmd() {
        return collect;
    }

    public String[] getArgs() {
        List<String> args = new ArrayList<String>();
        if (collectedInfoList.contains(CollectedInfo.SYNCHRONIZARION)) {
            args.add("-s");
            args.add("30");
        }
        args.add("-o");
        args.add(getExperimentDir());
        return args.toArray(new String[0]);
    }

    protected String getExperimentDir() {
        return experimentDir;
    }

    public void targetStateChanged(DLightTarget source, State oldState, State newState) {
        switch (newState) {
            case RUNNING:
                targetStarted(source);
                return;
            case FAILED:
                targetFinished(source);
                return;
            case TERMINATED:
                targetFinished(source);
                return;
            case DONE:
                targetFinished(source);
                return;
            case STOPPED:
                targetFinished(source);
                return;
        }
    }
}
