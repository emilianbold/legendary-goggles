<#--
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.

Oracle and Java are registered trademarks of Oracle and/or its affiliates.
Other names may be trademarks of their respective owners.

The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
particular file as subject to the "Classpath" exception as provided
by Oracle in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):
-->
<#if comment>

  TEMPLATE DESCRIPTION:

  This is Java template for 'JSF Pages From Entity Beans' controller class. Templating
  is performed using FreeMaker (http://freemarker.org/) - see its documentation
  for full syntax. Variables available for templating are:

    controllerClassName - controller class name (type: String)
    controllerPackageName - controller package name (type: String)
    entityClassName - entity class name without package (type: String)
    importEntityFullClassName - whether to import entityFullClassName or not
    entityFullClassName - fully qualified entity class name (type: String)
    ejbClassName - EJB class name (type: String)
    importEjbFullClassName - whether to import ejbFullClassName or not
    ejbFullClassName - fully qualified EJB class name (type: String)
    managedBeanName - name of managed bean (type: String)
    keyEmbedded - is entity primary key is an embeddable class (type: Boolean)
    keyType - fully qualified class name of entity primary key
    keyBody - body of Controller.Converter.getKey() method
    keyStringBody - body of Controller.Converter.getStringKey() method
    keyGetter - entity getter method returning primaty key instance
    keySetter - entity setter method to set primary key instance
    embeddedIdFields - contains information about embedded primary IDs
    cdiEnabled - project contains beans.xml, so Named beans can be used
    bundle - name of the variable defined in the JSF config file for the resource bundle (type: String)

  This template is accessible via top level menu Tools->Templates and can
  be found in category JavaServer Faces->JSF from Entity.

</#if>
package ${controllerPackageName};

<#if importEntityFullClassName?? && importEntityFullClassName == true>
import ${entityFullClassName};
</#if>
import ${controllerPackageName}.util.JsfUtil;
import ${controllerPackageName}.util.JsfUtil.PersistAction;
<#if importEjbFullClassName?? && importEjbFullClassName == true>
    <#if ejbClassName??>
import ${ejbFullClassName};
    <#elseif jpaControllerClassName??>
import ${jpaControllerFullClassName};
    </#if>
</#if>

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
<#if isInjected?? && isInjected==true>
import javax.annotation.Resource;
</#if>
<#if ejbClassName??>
import javax.ejb.EJB;
</#if>
import javax.ejb.EJBException;
<#if managedBeanName??>
<#if cdiEnabled?? && cdiEnabled>
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
<#else>
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
</#if>
</#if>
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
<#if jpaControllerClassName??>
  <#if isInjected?? && isInjected==true>
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
  <#else>
import javax.persistence.Persistence;
  </#if>
</#if>


<#if managedBeanName??>
<#if cdiEnabled?? && cdiEnabled>
@Named("${managedBeanName}")
<#else>
@ManagedBean(name="${managedBeanName}")
</#if>
@SessionScoped
</#if>
public class ${controllerClassName} implements Serializable {

<#if isInjected?? && isInjected==true>
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit<#if persistenceUnitName??>(unitName = "${persistenceUnitName}")</#if>
    private EntityManagerFactory emf = null;
</#if>

<#if ejbClassName??>
    @EJB private ${ejbFullClassName} ejbFacade;
<#elseif jpaControllerClassName??>
    private ${jpaControllerClassName} jpaController = null;
</#if>
    private List<${entityClassName}> items = null;
    private ${entityClassName} selected;

    public ${controllerClassName}() {
    }

    public ${entityClassName} getSelected() {
        return selected;
    }

    public void setSelected(${entityClassName} selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
<#list embeddedIdFields as fields>
            selected.${keyGetter}().${fields.getEmbeddedSetter()}(selected.${fields.getCodeToPopulate()});
</#list>
    }

    protected void initializeEmbeddableKey() {
<#if keyEmbedded>
        selected.${keySetter}(new ${keyType}());
</#if>
    }

<#if ejbClassName??>
    private ${ejbClassName} getFacade() {
        return ejbFacade;
    }
<#elseif jpaControllerClassName??>
    private ${jpaControllerClassName} getJpaController() {
        if (jpaController == null) {
<#if isInjected?? && isInjected==true>
            jpaController = new ${jpaControllerClassName}(utx, emf);
<#else>
            jpaController = new ${jpaControllerClassName}(Persistence.createEntityManagerFactory(<#if persistenceUnitName??>"${persistenceUnitName}"</#if>));
</#if>
        }
        return jpaController;
    }
</#if>

    public ${entityClassName} prepareCreate() {
        selected = new ${entityClassName}();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("${bundle}").getString("${entityClassName}Created"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("${bundle}").getString("${entityClassName}Updated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("${bundle}").getString("${entityClassName}Deleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<${entityClassName}> getItems() {
        if (items == null) {
<#if ejbClassName??>
            items = getFacade().findAll();
<#elseif jpaControllerClassName??>
            items = getJpaController().find${entityClassName}Entities();
</#if>
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
<#if ejbClassName??>
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
<#elseif jpaControllerClassName??>
                if (persistAction == PersistAction.UPDATE) {
                    getJpaController().edit(selected);
                } else if (persistAction == PersistAction.CREATE) {
                    getJpaController().create(selected);
                } else {
                    getJpaController().destroy(selected.getId());
                }
</#if>
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("${bundle}").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("${bundle}").getString("PersistenceErrorOccured"));
            }
        }
    }

<#if ejbClassName?? && cdiEnabled?? && cdiEnabled>
    public ${entityClassName} get${entityClassName}(${keyType} id) {
        return getFacade().find(id);
    }
</#if>

    public List<${entityClassName}> getItemsAvailableSelectMany() {
<#if ejbClassName??>
        return getFacade().findAll();
<#elseif jpaControllerClassName??>
        return getJpaController().find${entityClassName}Entities();
</#if>
    }

    public List<${entityClassName}> getItemsAvailableSelectOne() {
<#if ejbClassName??>
        return getFacade().findAll();
<#elseif jpaControllerClassName??>
        return getJpaController().find${entityClassName}Entities();
</#if>
    }

    @FacesConverter(forClass=${entityClassName}.class)
    public static class ${controllerClassName}Converter implements Converter {
<#if keyEmbedded>

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";
</#if>

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ${controllerClassName} controller = (${controllerClassName})facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "${managedBeanName}");
<#if ejbClassName??>
<#if cdiEnabled?? && cdiEnabled>
            return controller.get${entityClassName}(getKey(value));
<#else>
            return controller.getFacade().find(getKey(value));
</#if>
<#elseif jpaControllerClassName??>
            return controller.getJpaController().find${entityClassName}(getKey(value));
</#if>
        }

        ${keyType} getKey(String value) {
            ${keyType} key;
${keyBody}
            return key;
        }

        String getStringKey(${keyType} value) {
            StringBuilder sb = new StringBuilder();
${keyStringBody}
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ${entityClassName}) {
                ${entityClassName} o = (${entityClassName}) object;
                return getStringKey(o.${keyGetter}());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ${entityClassName}.class.getName()});
                return null;
            }
        }

    }

}
