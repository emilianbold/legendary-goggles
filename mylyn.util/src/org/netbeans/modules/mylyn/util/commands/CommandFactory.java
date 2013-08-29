/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.mylyn.util.commands;

import java.io.OutputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskMigrationEvent;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobListener;
import org.netbeans.modules.mylyn.util.NbTask;
import org.netbeans.modules.mylyn.util.NbTaskDataModel;
import org.netbeans.modules.mylyn.util.internal.Accessor;
import org.netbeans.modules.mylyn.util.internal.CommandsAccessor;

/**
 *
 * @author Ondrej Vrabec
 */
public final class CommandFactory {
    
    static {
        // see static initializer of CommandAccessor
        CommandsAccessor.INSTANCE = new CommandsAccessorImpl();
    }
    
    private static final Logger LOG = Logger.getLogger(CommandFactory.class.getName());
    private final TaskList taskList;
    private final TaskDataManager taskDataManager;
    private final TaskRepositoryManager taskRepositoryManager;
    private final RepositoryModel repositoryModel;

    CommandFactory (TaskList taskList,
            TaskDataManager taskDataManager, TaskRepositoryManager taskRepositoryManager,
            RepositoryModel repositoryModel) {
        this.taskList = taskList;
        this.taskDataManager = taskDataManager;
        this.taskRepositoryManager = taskRepositoryManager;
        this.repositoryModel = repositoryModel;
    }

    public SynchronizeQueryCommand createSynchronizeQueriesCommand (TaskRepository taskRepository, IRepositoryQuery iquery) {
        assert iquery instanceof RepositoryQuery;
        RepositoryQuery repositoryQuery;
        if (iquery instanceof RepositoryQuery) {
            repositoryQuery = (RepositoryQuery) iquery;
        } else {
            return null;
        }
        AbstractRepositoryConnector repositoryConnector = taskRepositoryManager.getRepositoryConnector(taskRepository.getConnectorKind());
        return new SynchronizeQueryCommand(repositoryModel, repositoryConnector,
                taskRepository, taskList, taskDataManager, repositoryQuery);
    }
    
    /**
     * Returns a bugtracking command submitting the given task to the remote
     * repository.
     *
     * @param model task data to submit
     * @return the command ready to be executed
     * @throws CoreException problem while submitting
     */
    public SubmitTaskCommand createSubmitTaskCommand (NbTaskDataModel model) throws CoreException {
        final AbstractRepositoryConnector repositoryConnector;
        final ITask task = Accessor.getInstance().getITask(model);
        TaskRepository taskRepository = Accessor.getInstance().getTaskRepositoryFor(task);
        if (task.getSynchronizationState() == ITask.SynchronizationState.OUTGOING_NEW) {
            repositoryConnector = taskRepositoryManager.getRepositoryConnector(
                    task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND));
        } else {
            repositoryConnector = taskRepositoryManager.getRepositoryConnector(task.getConnectorKind());
        }

        SubmitTaskCommand command = new SubmitTaskCommand(taskDataManager,
                repositoryConnector,
                taskRepository,
                task,
                model.getLocalTaskData(), model.getChangedOldAttributes() /*??? no idea what's this good for*/);
        command.setSubmitJobListener(new SubmitJobListener() {
            @Override
            public void taskSubmitted (SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
            }

            @Override
            public void taskSynchronized (SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
            }

            @Override
            public void done (SubmitJobEvent event) {
                // turn into full task
                SubmitJob job = event.getJob();
                ITask newTask = job.getTask();
                if (newTask != null && newTask != task) {
                    // copy anything you want
                    taskList.deleteTask(task);
                    taskList.addTask(newTask);
                    repositoryConnector.migrateTask(new TaskMigrationEvent(task, newTask));
                    try {
                        taskDataManager.deleteTaskData(task);
                    } catch (CoreException ex) {
                        LOG.log(Level.INFO, null, ex);
                    }
                }
            }
        });

        return command;
    }

    /**
     * 
     * @param taskRepository
     * @param tasks
     * @return
     * @deprecated run {@link #createSynchronizeTasksCommand(org.eclipse.mylyn.tasks.core.TaskRepository, java.util.Set, boolean) }
     */
    @Deprecated
    public SynchronizeTasksCommand createSynchronizeTasksCommand (TaskRepository taskRepository, Set<NbTask> tasks) {
        return createSynchronizeTasksCommand(taskRepository, tasks, true);
    }

    /**
     * Synchronizes given tasks with their state in a repository.
     *
     * @param taskRepository repository
     * @param tasks tasks to synchronize
     * @param isUserAction when set to <code>true</code> mylyn will force the
     * refresh and may run certain additional tasks like fetching subtasks and
     * parent tasks.
     * @return
     */
    public SynchronizeTasksCommand createSynchronizeTasksCommand (TaskRepository taskRepository, Set<NbTask> tasks, boolean isUserAction) {
        AbstractRepositoryConnector repositoryConnector = taskRepositoryManager.getRepositoryConnector(taskRepository.getConnectorKind());
        return new SynchronizeTasksCommand(repositoryConnector, taskRepository,
                repositoryModel, taskDataManager, taskList, tasks, isUserAction);
    }

    public GetRepositoryTasksCommand createGetRepositoryTasksCommand (TaskRepository taskRepository, Set<String> taskIds) throws CoreException {
        AbstractRepositoryConnector repositoryConnector = taskRepositoryManager.getRepositoryConnector(taskRepository.getConnectorKind());
        GetRepositoryTasksCommand cmd = new GetRepositoryTasksCommand(repositoryConnector,
                taskRepository, taskIds, taskDataManager);
        return cmd;
    }

    public SimpleQueryCommand createSimpleQueryCommand (TaskRepository taskRepository, IRepositoryQuery query) throws CoreException {
        AbstractRepositoryConnector repositoryConnector = taskRepositoryManager.getRepositoryConnector(taskRepository.getConnectorKind());
        return new SimpleQueryCommand(repositoryConnector, taskRepository, taskDataManager, query);
    }

    public PostAttachmentCommand createPostAttachmentCommand (TaskRepository taskRepository, NbTask task,
            TaskAttribute attAttribute, FileTaskAttachmentSource attachmentSource, String comment) {
        AbstractRepositoryConnector repositoryConnector = taskRepositoryManager.getRepositoryConnector(taskRepository.getConnectorKind());
        return new PostAttachmentCommand(repositoryConnector, taskRepository,
                Accessor.getInstance().getDelegate(task),
                attAttribute, attachmentSource, comment);
    }

    public GetAttachmentCommand createGetAttachmentCommand (TaskRepository taskRepository, 
            NbTask nbTask, TaskAttribute ta, OutputStream os) {
        AbstractRepositoryConnector repositoryConnector = taskRepositoryManager.getRepositoryConnector(taskRepository.getConnectorKind());
        return new GetAttachmentCommand(repositoryConnector, taskRepository,
                Accessor.getInstance().getDelegate(nbTask), ta, os);
    }
}
