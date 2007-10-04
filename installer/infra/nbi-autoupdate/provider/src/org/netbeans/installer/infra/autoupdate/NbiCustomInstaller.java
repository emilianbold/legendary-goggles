/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.infra.autoupdate;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.OperationException.ERROR_TYPE;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.exceptions.DownloadException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.helper.FinishHandler;
import org.netbeans.installer.utils.progress.CompositeProgress;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.WizardComponent;
import org.netbeans.installer.wizard.components.sequences.ProductWizardSequence;
import org.netbeans.installer.wizard.components.actions.CacheEngineAction;
import org.netbeans.spi.autoupdate.CustomInstaller;

/**
 *
 * @author ks152834
 */
public class NbiCustomInstaller implements CustomInstaller {
    
    private Product product;
    
    public NbiCustomInstaller(final Product product) {
        this.product = product;
    }
    
    public boolean install(
            final String name, 
            final String version, 
            final ProgressHandle progressHandle) throws OperationException {
        final CompositeProgress composite = 
                new CompositeProgress(new ProgressHandleAdapter(progressHandle));
        
        final Progress logicProgress = new Progress();
        final Progress dataProgress = new Progress();
        final Progress installProgress = new Progress();
        
        composite.addChild(logicProgress, 10);
        composite.addChild(dataProgress, 60);
        composite.addChild(installProgress, 30);
        
        try {
            final List<WizardComponent> components = new LinkedList<WizardComponent>();
            
            components.add(new CacheEngineAction());
            components.add(new ProductWizardSequence(product));
            
            final Wizard wizard = new Wizard(null, components, -1);
            wizard.setFinishHandler(new FinishHandler() {
                public void cancel() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                public void finish() {
                    wizard.close();
                }
                
                public void criticalExit() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
             });
            wizard.openBlocking();
            
            product.downloadLogic(logicProgress);
            product.downloadData(dataProgress);
            product.install(installProgress);
        } catch (DownloadException e) {
            throw new OperationException(ERROR_TYPE.INSTALL, e);
        } catch (InstallationException e) {
            throw new OperationException(ERROR_TYPE.INSTALL, e);
        }
        
        return true;
    }
}
