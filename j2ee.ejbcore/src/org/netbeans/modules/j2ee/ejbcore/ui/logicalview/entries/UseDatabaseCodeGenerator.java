/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.ejbcore.ui.logicalview.entries;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.api.ejbjar.EnterpriseReferenceContainer;
import org.netbeans.modules.j2ee.common.DatasourceUIHelper;
import org.netbeans.modules.j2ee.core.api.support.progress.ProgressSupport;
import org.netbeans.modules.j2ee.core.api.support.progress.ProgressSupport.Context;
import org.netbeans.modules.j2ee.dd.api.common.ResourceRef;
import org.netbeans.modules.j2ee.dd.api.ejb.EnterpriseBeans;
import org.netbeans.modules.j2ee.dd.api.ejb.Ejb;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJarMetadata;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.ejbcore.action.UseDatabaseGenerator;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;


/**
 * Provide action for using a data source.
 * 
 * @author Chris Webster
 * @author Martin Adamek
 */
public class UseDatabaseCodeGenerator implements CodeGenerator {

    private FileObject fileObject;
    private TypeElement beanClass;

    public static class Factory implements CodeGenerator.Factory {

        public List<? extends CodeGenerator> create(Lookup context) {
            ArrayList<CodeGenerator> ret = new ArrayList<CodeGenerator>();
            JTextComponent component = context.lookup(JTextComponent.class);
            CompilationController controller = context.lookup(CompilationController.class);
            TreePath path = context.lookup(TreePath.class);
            path = path != null ? SendEmailCodeGenerator.getPathElementOfKind(Tree.Kind.CLASS, path) : null;
            if (component == null || controller == null || path == null)
                return ret;
            try {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                Element elem = controller.getTrees().getElement(path);
                if (elem != null) {
                    UseDatabaseCodeGenerator gen = createUseDatabaseAction(component, controller, elem);
                    if (gen != null)
                        ret.add(gen);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return ret;
        }

    }

    static UseDatabaseCodeGenerator createUseDatabaseAction(JTextComponent component, CompilationController cc, Element el) throws IOException {
        if (el.getKind() != ElementKind.CLASS)
            return null;
        TypeElement typeElement = (TypeElement)el;
        if (!isEnable(cc.getFileObject(), typeElement)) {
            return null;
        }
        return new UseDatabaseCodeGenerator(cc.getFileObject(), typeElement);
    }

    public UseDatabaseCodeGenerator(FileObject srcFile, TypeElement beanClass) {
        this.fileObject = srcFile;
        this.beanClass = beanClass;
    }

    public void invoke() {
        Project project = FileOwnerQuery.getOwner(fileObject);
        //make sure configuration is ready
        J2eeModuleProvider j2eeModuleProvider = project.getLookup().lookup(J2eeModuleProvider.class);
        j2eeModuleProvider.getConfigSupport().ensureConfigurationReady();
        EnterpriseReferenceContainer enterpriseReferenceContainer = project.getLookup().lookup(EnterpriseReferenceContainer.class);

        // get all the resources
        ResourcesHolder holder = getResources(j2eeModuleProvider, fileObject);
        
        final SelectDatabasePanel selectDatabasePanel = new SelectDatabasePanel(
                j2eeModuleProvider,
                enterpriseReferenceContainer.getServiceLocatorName(),
                holder.getReferences(),
                holder.getModuleDataSources(),
                holder.getServerDataSources(),
                ClasspathInfo.create(fileObject));
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(
                selectDatabasePanel,
                NbBundle.getMessage(UseDatabaseCodeGenerator.class, "LBL_ChooseDatabase"), //NOI18N
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(SelectDatabasePanel.class),
                null

                );
        dialogDescriptor.setValid(checkConnections(selectDatabasePanel));
        selectDatabasePanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SelectDatabasePanel.IS_VALID)) {
                    Object newvalue = evt.getNewValue();
                    if ((newvalue != null) && (newvalue instanceof Boolean)) {
                        dialogDescriptor.setValid(((Boolean)newvalue).booleanValue() && checkConnections(selectDatabasePanel));
                    }
                }
            }
        });
        
        Object option = DialogDisplayer.getDefault().notify(dialogDescriptor);
        if (option == NotifyDescriptor.OK_OPTION) {
            String refName = selectDatabasePanel.getDatasourceReference();
            
            UseDatabaseGenerator generator = new UseDatabaseGenerator();
            try {
                generator.generate(
                        fileObject,
                        beanClass.getQualifiedName().toString(),
                        j2eeModuleProvider,
                        refName,
                        selectDatabasePanel.getDatasource(),
                        selectDatabasePanel.createServerResources(),
                        selectDatabasePanel.getServiceLocator()
                        );
            } catch (ConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /** Get references, module- and server datasources. */
    private ResourcesHolder getResources(final J2eeModuleProvider j2eeModuleProvider, final FileObject fileObject) {
        
        final ResourcesHolder holder = new ResourcesHolder();
        
        // fetch references & datasources asynchronously
        Collection<ProgressSupport.Action> asyncActions = new ArrayList<ProgressSupport.Action>(1);
        asyncActions.add(new ProgressSupport.BackgroundAction() {
            
            public void run(Context actionContext) {
                String msg = NbBundle.getMessage(DatasourceUIHelper.class, "MSG_retrievingDS"); //NOI18N
                actionContext.progress(msg);
                try {
                    populateDataSourceReferences(holder, j2eeModuleProvider, fileObject);
                } catch (ConfigurationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }
            }
        });
        
        ProgressSupport.invoke(asyncActions);
        
        return holder;
    }
    
    // this method has to called asynchronously!
    private void populateDataSourceReferences(final ResourcesHolder holder, final J2eeModuleProvider j2eeModuleProvider,
            final FileObject fileObject) throws ConfigurationException, IOException {
        
        final HashMap<String, Datasource> references = new HashMap<String, Datasource>();
        holder.setReferences(references);
        holder.setModuleDataSources(j2eeModuleProvider.getModuleDatasources());
        holder.setServerDataSources(j2eeModuleProvider.getServerDatasources());
        
        if (j2eeModuleProvider.getJ2eeModule().getType().equals(J2eeModule.Type.EJB)) {
            
            MetadataModel<EjbJarMetadata> metadataModel = org.netbeans.modules.j2ee.api.ejbjar.EjbJar.getEjbJar(fileObject).getMetadataModel();
            metadataModel.runReadAction(new MetadataModelAction<EjbJarMetadata, Void>() {
                public Void run(EjbJarMetadata metadata) throws Exception {
                    EnterpriseBeans beans = metadata.getRoot().getEnterpriseBeans();
                    if (beans == null) {
                        return null;
                    }

                    Ejb[] ejbs = beans.getEjbs();
                    for (Ejb ejb : ejbs) {
                        ResourceRef[] refs = ejb.getResourceRef();
                        for (ResourceRef ref : refs) {
                            String refName = ref.getResRefName();
                            Datasource ds = findDatasourceForReferenceForEjb(holder, j2eeModuleProvider, refName, ejb.getEjbName());
                            if (ds != null) {
                                references.put(refName, ds);
                            }
                        }
                    }
                    return null;
                }
            });
            
        }
        else
        if (j2eeModuleProvider.getJ2eeModule().getType().equals(J2eeModule.Type.WAR)) {
            
            MetadataModel<WebAppMetadata> metadataModel = WebModule.getWebModule(fileObject).getMetadataModel();
            metadataModel.runReadAction(new MetadataModelAction<WebAppMetadata, Void>() {
                public Void run(WebAppMetadata metadata) throws Exception {
                    List<ResourceRef> refs = metadata.getResourceRefs();
                    for (ResourceRef ref : refs) {
                        String refName = ref.getResRefName();
                        Datasource ds = findDatasourceForReference(holder, j2eeModuleProvider, refName);
                        if (ds != null) {
                            references.put(refName, ds);
                        }
                    }
                    return null;
                }
            });
            
        }
    }

    private Datasource findDatasourceForReference(final ResourcesHolder holder, J2eeModuleProvider j2eeModuleProvider, String referenceName) throws ConfigurationException {
        String jndiName = j2eeModuleProvider.getConfigSupport().findDatasourceJndiName(referenceName);
        if (jndiName == null) {
            return null;
        }
        return findDataSource(holder, jndiName);
    }
    
    public Datasource findDatasourceForReferenceForEjb(final ResourcesHolder holder, J2eeModuleProvider j2eeModuleProvider, String referenceName, String ejbName) throws ConfigurationException {
        String jndiName = j2eeModuleProvider.getConfigSupport().findDatasourceJndiNameForEjb(ejbName, referenceName);
        if (jndiName == null) {
            return null;
        }
        return findDataSource(holder, jndiName);
    }
    
    // this is faster implementation than in API (@see ConfigSupportImpl#findDatasource())
    // TODO this method (as well as API method) should not use <code>equals()</code>
    private Datasource findDataSource(ResourcesHolder holder, String jndiName) {
        
        assert holder != null;
        assert jndiName != null;
        
        // project ds
        for (Datasource ds : holder.getModuleDataSources()) {
            if (jndiName.equals(ds.getJndiName())) {
                return ds;
            }
        }
        for (Datasource ds : holder.getServerDataSources()) {
            if (jndiName.equals(ds.getJndiName())) {
                return ds;
            }
        }
        
        return null;
    }
    
    private static boolean isEnable(FileObject fileObject, TypeElement typeElement) {
        Project project = FileOwnerQuery.getOwner(fileObject);
        if (project == null) {
            return false;
        }
        J2eeModuleProvider j2eeModuleProvider = project.getLookup().lookup(J2eeModuleProvider.class);
        if (j2eeModuleProvider == null) {
            return false;
        }
        if (project.getLookup().lookup(EnterpriseReferenceContainer.class) == null) {
            return false;
        }
        return ElementKind.INTERFACE != typeElement.getKind();
    }
    
    private boolean checkConnections(SelectDatabasePanel selectDatabasePanel) {
        return selectDatabasePanel.getDatasource() != null;
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(UseDatabaseCodeGenerator.class, "LBL_UseDbAction"); // NOI18N
    }
    
    /**
     * Just holder for few properties.
     */
    private static final class ResourcesHolder {
        private Map<String, Datasource> references;
        private Set<Datasource> moduleDataSources;
        private Set<Datasource> serverDataSources;
        
        public ResourcesHolder() {
        }

        public void setReferences(final Map<String, Datasource> references) {
            this.references = references;
        }

        public void setModuleDataSources(final Set<Datasource> moduleDataSources) {
            this.moduleDataSources = moduleDataSources;
        }

        public void setServerDataSources(final Set<Datasource> serverDataSources) {
            this.serverDataSources = serverDataSources;
        }

        public Map<String, Datasource> getReferences() {
            if (references == null) {
                references = new HashMap<String, Datasource>();
            }
            return references;
        }

        public Set<Datasource> getModuleDataSources() {
            if (moduleDataSources == null) {
                moduleDataSources = new HashSet<Datasource>();
            }
            return moduleDataSources;
        }

        public Set<Datasource> getServerDataSources() {
            if (serverDataSources == null) {
                serverDataSources = new HashSet<Datasource>();
            }
            return serverDataSources;
        }
        
    }
}
