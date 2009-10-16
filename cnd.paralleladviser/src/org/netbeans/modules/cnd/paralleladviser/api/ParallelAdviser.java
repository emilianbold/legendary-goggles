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
package org.netbeans.modules.cnd.paralleladviser.api;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.netbeans.modules.cnd.paralleladviser.paralleladviserview.Advice;
import org.netbeans.modules.cnd.paralleladviser.paralleladviserview.ParallelAdviserTopComponent;
import org.netbeans.modules.cnd.paralleladviser.spi.ParallelAdviserTipsProvider;
import org.netbeans.modules.cnd.paralleladviser.spi.ParallelAdviserTipsProviderListener;
import org.openide.util.Lookup;

/**
 * Parallel Adviser API.
 *
 * @author Nick Krasilnikov
 */
public class ParallelAdviser {

    /**
     * Constructor.
     */
    private ParallelAdviser() {
    }

    /** Static method to obtain the provider.
     * @return the provider
     */
    private static synchronized Collection<? extends ParallelAdviserTipsProvider> getParallelAdviserTipsProviders() {
        return Lookup.getDefault().lookupAll(ParallelAdviserTipsProvider.class);
    }

    /**
     * Collects tips from providers.
     *
     * @return - tips
     */
    public static Collection<Advice> getTips() {
        Collection<Advice> tips = new ArrayList<Advice>();
        for (ParallelAdviserTipsProvider provider : getParallelAdviserTipsProviders()) {
            tips.addAll(provider.getTips());
        }
        return tips;
    }

    /**
     * Add listener to all tips providers.
     *
     * @param listener - new listener
     */
    public static void addListener(ParallelAdviserTipsProviderListener listener) {
        for (ParallelAdviserTipsProvider provider : getParallelAdviserTipsProviders()) {
            provider.addListener(listener);
        }
    }

    /**
     * Opens Parallel Adviser top component.
     */
    public static void showParallelAdviserView() {
        Runnable updateView = new Runnable() {

            public void run() {
                ParallelAdviserTopComponent view = ParallelAdviserTopComponent.findInstance();
                if (!view.isOpened()) {
                    view.open();
                }
                view.requestActive();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            updateView.run();
        } else {
            SwingUtilities.invokeLater(updateView);
        }
    }
}
