/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
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


package org.netbeans.modules.websvc.wsitmodelext.rm;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Martin Grebac
 */
public enum RMQName {
    RMASSERTION(createRMQName("RMAssertion")),                                  //NOI18N
    BASERETRANSMISSIONINTERVAL(createRMQName("BaseRetransmissionInterval")),    //NOI18N
    EXPONENTIALBACKOFF(createRMQName("ExponentialBackoff")),                    //NOI18N
    ACKNOWLEDGEMENTINTERVAL(createRMQName("AcknowledgementInterval")),          //NOI18N
    INACTIVITYTIMEOUT(createRMQName("InactivityTimeout"));                      //NOI18N

    public static final String RM_HEADERS_NS_URI = "http://schemas.xmlsoap.org/ws/2005/02/rm";    //NOI18N
    
    public static final String RM_NS_URI = "http://schemas.xmlsoap.org/ws/2005/02/rm/policy";    //NOI18N
    public static final String RM_NS_PREFIX = "wsrm";                                            //NOI18N
    
    public static QName createRMQName(String localName){
        return new QName(RM_NS_URI, localName, RM_NS_PREFIX);
    }
    
    RMQName(QName name) {
        qName = name;
    }
    
    public QName getQName(){
        return qName;
    }
    private static Set<QName> qnames = null;
    public static Set<QName> getQNames() {
        if (qnames == null) {
            qnames = new HashSet<QName>();
            for (RMQName wq : values()) {
                qnames.add(wq.getQName());
            }
        }
        return qnames;
    }
    private final QName qName;

}
