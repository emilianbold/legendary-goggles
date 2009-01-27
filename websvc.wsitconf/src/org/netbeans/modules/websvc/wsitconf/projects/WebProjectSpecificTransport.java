/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.websvc.wsitconf.projects;

import java.io.FileOutputStream;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.common.NameAlreadyUsedException;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Listener;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.websvc.api.jaxws.project.config.Service;
import org.netbeans.modules.websvc.jaxwsruntimemodel.JavaWsdlMapper;
import org.netbeans.modules.websvc.wsitconf.spi.ProjectSpecificTransport;
import org.netbeans.modules.websvc.wsitconf.util.ServerUtils;
import org.netbeans.modules.websvc.wsitconf.util.Util;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.XMLDataObject;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Element;

/**
 *
 * @author snajper
 */
public class WebProjectSpecificTransport extends ProjectSpecificTransport {

    private WebWsitProvider wsitProvider;

    private static final String TCP_GF_NONJSR109 = "com.sun.xml.ws.transport.tcp.server.glassfish.WSStartupServlet";   //NOI18N
    private static final String TCP_TOMCAT =       "com.sun.xml.ws.transport.http.servlet.WSServletContextListener";   //NOI18N
    
    public WebProjectSpecificTransport(Project p, WebWsitProvider provider) {
        this.project = p;
        this.wsitProvider = provider;
    }

    @Override
    public void setTCPUrl(Service s, boolean tomcat) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null) {
            try {
                WebApp wApp = DDProvider.getDefault ().getDDRoot(wm.getDeploymentDescriptor());
                if (wsitProvider.isJsr109Project()) {
                    String servletClassName = s.getImplementationClass();       //NOI18N
                    Servlet servlet = Util.getServlet(wApp, servletClassName);
                    if (servlet == null) {      //NOI18N
                        try {
                            String servletName = s.getName();
                            servlet = (Servlet)wApp.addBean("Servlet", new String[]{WebWsitProvider.SERVLET_NAME,"ServletClass"},    //NOI18N
                                    new Object[]{servletName,servletClassName}, WebWsitProvider.SERVLET_NAME);
                            servlet.setLoadOnStartup(new java.math.BigInteger("1"));                            //NOI18N
                            String serviceName = s.getServiceName();
                            if (serviceName == null) {
                                serviceName = servletClassName.substring(servletClassName.lastIndexOf('.')+1) + JavaWsdlMapper.SERVICE;
                            }
                            wApp.addBean("ServletMapping", new String[]{WebWsitProvider.SERVLET_NAME,"UrlPattern"}, //NOI18N
                                    new Object[]{servletName, "/" + serviceName}, WebWsitProvider.SERVLET_NAME);
                            wApp.write(wm.getDeploymentDescriptor());
                        } catch (NameAlreadyUsedException ex) {
                            ex.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        servlet.setLoadOnStartup(new java.math.BigInteger("1"));
                    }
                } else {
                    String listener = tomcat ? TCP_TOMCAT : TCP_GF_NONJSR109;
                    if (!isTcpListener(wApp, listener)) {
                        try {
                            wApp.addBean("Listener", new String[]{"ListenerClass"},  //NOI18N
                                    new Object[]{listener}, "ListenerClass");        //NOI18N
                            wApp.write(wm.getDeploymentDescriptor());
                        } catch (NameAlreadyUsedException ex) {
                            ex.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (tomcat) {
                        addConnector(project);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean isTcpListener(WebApp wa, String listener) {
        Listener[] listeners = wa.getListener();
        for (Listener l : listeners) {
            if (listener.equals(l.getListenerClass())) {
                return true;
            }
        }
        return false;
    }

    private static final String PROP_CONNECTOR = "Connector"; // NOI18N
    private static final String CONN_PROTOCOL = "com.sun.xml.ws.transport.tcp.server.tomcat.grizzly10.WSTCPGrizzly10ProtocolHandler";
    private static final String CONN_PORT = "5773";
    private static final String CONN_TIMEOUT = "20000";
    private static final String CONN_REDIRECT_PORT = "8080";

    /**
     * Make some Tomcat specific changes in server.xml.
     */
    public static void addConnector(Project p) {
        FileObject serverXml = ServerUtils.getServerXml(p);
        if (serverXml != null) {
            try {
                XMLDataObject dobj = (XMLDataObject)DataObject.find(serverXml);
                org.w3c.dom.Document doc = dobj.getDocument();
                org.w3c.dom.Element root = doc.getDocumentElement();
                org.w3c.dom.NodeList list = root.getElementsByTagName("Service"); //NOI18N
                int size=list.getLength();
                if (size>0) {
                    org.w3c.dom.Element service=(org.w3c.dom.Element)list.item(0);
                    org.w3c.dom.NodeList cons = service.getElementsByTagName(PROP_CONNECTOR);
                    for (int i=0;i<cons.getLength();i++) {
                        org.w3c.dom.Element con=(org.w3c.dom.Element)cons.item(i);
                        String protocol = con.getAttribute("protocol");
                        if (CONN_PROTOCOL.equals(protocol))return;
                    }

                    Element e = doc.createElement(PROP_CONNECTOR);
                    e.setAttribute("port", CONN_PORT);
                    e.setAttribute("connectionTimeout", CONN_TIMEOUT);
                    e.setAttribute("protocol", CONN_PROTOCOL);
                    e.setAttribute("redirectHttpPort", CONN_REDIRECT_PORT);
                    e.setAttribute("redirectPort", CONN_REDIRECT_PORT);

                    service.appendChild(e);

                    XMLUtil.write(doc, new FileOutputStream(FileUtil.toFile(serverXml)), "UTF-8");
                }
            } catch(org.xml.sax.SAXException ex){
                Exceptions.printStackTrace(ex);
            } catch(org.openide.loaders.DataObjectNotFoundException ex){
                Exceptions.printStackTrace(ex);
            } catch(java.io.IOException ex){
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
