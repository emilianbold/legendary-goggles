/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docker.ui.node;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.docker.api.DockerInstance;
import org.openide.nodes.Node;

/**
 *
 * @author Petr Hejl
 */
public class DockerInstanceChildFactory extends NodeClosingFactory<Boolean> implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(DockerInstanceChildFactory.class.getName());

    private final StatefulDockerInstance instance;

    private final Set<Node> current = new HashSet<>();

    public DockerInstanceChildFactory(StatefulDockerInstance instance) {
        this.instance = instance;

        instance.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(false);
            }
        });
        instance.refresh();
    }

    @Override
    protected Node[] createNodesForKey(Boolean key) {
        Node[] ret;
        if (key) {
            DockerInstance dockerInstance = instance.getInstance();
            DockerImagesChildFactory factoryRepo = new DockerImagesChildFactory(dockerInstance);
            DockerContainersChildFactory factoryCont = new DockerContainersChildFactory(dockerInstance);
            ret = new Node[]{new DockerImagesNode(dockerInstance, factoryRepo),
                new DockerContainersNode(dockerInstance, factoryCont)};
        } else {
            ret = new Node[] {};
        }
        synchronized (current) {
            current.clear();
            Collections.addAll(current, ret);
        }
        return ret;
    }

    @Override
    protected boolean createKeys(List<Boolean> toPopulate) {
        toPopulate.add(instance.isAvailable());
        return true;
    }

    @Override
    public void close() {
        Set<Node> nodes;
        synchronized (current) {
            nodes = new HashSet<>(current);
        }
        for (Node n : nodes) {
            for (Closeable c : n.getLookup().lookupAll(Closeable.class)) {
                try {
                    c.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.INFO, null, ex);
                }
            }
        }
    }
}
