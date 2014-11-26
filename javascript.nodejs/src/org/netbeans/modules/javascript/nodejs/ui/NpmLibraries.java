/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.nodejs.ui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript.nodejs.file.PackageJson;
import org.netbeans.modules.javascript.nodejs.ui.libraries.LibraryCustomizer;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

public final class NpmLibraries {

    private NpmLibraries() {
    }

    @NodeFactory.Registration(projectType = "org-netbeans-modules-web-clientproject", position = 600)
    public static NodeFactory forHtml5Project() {
        return new NpmLibrariesNodeFactory();
    }

    @NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 400)
    public static NodeFactory forPhpProject() {
        return new NpmLibrariesNodeFactory();
    }

    @NodeFactory.Registration(projectType = "org-netbeans-modules-web-project", position = 310)
    public static NodeFactory forWebProject() {
        return new NpmLibrariesNodeFactory();
    }

    @NodeFactory.Registration(projectType = "org-netbeans-modules-maven", position = 610)
    public static NodeFactory forMavenProject() {
        return new NpmLibrariesNodeFactory();
    }

    //~ Inner classes

    private static final class NpmLibrariesNodeFactory implements NodeFactory {

        @Override
        public NodeList<?> createNodes(Project project) {
            assert project != null;
            return new NpmLibrariesNodeList(project);
        }

    }

    private static final class NpmLibrariesNodeList implements NodeList<Node>, PropertyChangeListener {

        private final Project project;
        private final PackageJson packageJson;
        private final NpmLibrariesChildren npmLibrariesChildren;
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        // @GuardedBy("thread")
        private Node npmLibrariesNode;


        NpmLibrariesNodeList(Project project) {
            assert project != null;
            this.project = project;
            packageJson = new PackageJson(project.getProjectDirectory());
            npmLibrariesChildren = new NpmLibrariesChildren(packageJson);
        }

        @Override
        public List<Node> keys() {
            if (!npmLibrariesChildren.hasDependencies()) {
                return Collections.<Node>emptyList();
            }
            if (npmLibrariesNode == null) {
                npmLibrariesNode = new NpmLibrariesNode(project, npmLibrariesChildren);
            }
            return Collections.<Node>singletonList(npmLibrariesNode);
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        @Override
        public Node node(Node key) {
            return key;
        }

        @Override
        public void addNotify() {
            packageJson.addPropertyChangeListener(WeakListeners.propertyChange(this, packageJson));
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (PackageJson.PROP_DEPENDENCIES.equals(propertyName)
                    || PackageJson.PROP_DEV_DEPENDENCIES.equals(propertyName)
                    || PackageJson.PROP_PEER_DEPENDENCIES.equals(propertyName)
                    || PackageJson.PROP_OPTIONAL_DEPENDENCIES.equals(propertyName)) {
                fireChange();
            }
        }

        private void fireChange() {
            changeSupport.fireChange();
        }

    }

    private static final class NpmLibrariesNode extends AbstractNode {

        @StaticResource
        private static final String LIBRARIES_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-badge.png"; // NOI18N

        private final Project project;
        private final Node iconDelegate;


        NpmLibrariesNode(Project project, NpmLibrariesChildren npmLibrariesChildren) {
            super(npmLibrariesChildren);
            assert project != null;
            this.project = project;
            iconDelegate = DataFolder.findFolder(FileUtil.getConfigRoot()).getNodeDelegate();
        }

        @NbBundle.Messages("NpmLibrariesNode.name=npm Libraries")
        @Override
        public String getDisplayName() {
            return Bundle.NpmLibrariesNode_name();
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.mergeImages(iconDelegate.getIcon(type), ImageUtilities.loadImage(LIBRARIES_BADGE), 7, 7);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[] {
                new CustomizeLibrariesAction(project),
            };
        }

    }

    private static final class NpmLibrariesChildren extends Children.Keys<NpmLibraryInfo> {

        @StaticResource
        private static final String LIBRARIES_ICON = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries.gif"; // NOI18N
        @StaticResource
        private static final String DEV_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-dev-badge.gif"; // NOI18N
        @StaticResource
        private static final String PEER_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-peer-badge.png"; // NOI18N
        @StaticResource
        private static final String OPTIONAL_BADGE = "org/netbeans/modules/javascript/nodejs/ui/resources/libraries-optional-badge.png"; // NOI18N


        private final PackageJson packageJson;
        private final java.util.Map<String, Image> icons = new HashMap<>();


        public NpmLibrariesChildren(PackageJson packageJson) {
            super(true);
            assert packageJson != null;
            this.packageJson = packageJson;
        }

        public boolean hasDependencies() {
            refreshDependencies();
            return getNodesCount() > 0;
        }


        @Override
        protected Node[] createNodes(NpmLibraryInfo key) {
            return new Node[] {new NpmLibraryNode(key)};
        }

        @NbBundle.Messages({
            "NpmLibrariesChildren.library.dev=dev",
            "NpmLibrariesChildren.library.optional=optional",
            "NpmLibrariesChildren.library.peer=peer",
        })
        @Override
        protected void addNotify() {
            refreshDependencies();
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<NpmLibraryInfo>emptyList());
        }


        private void refreshDependencies() {
            PackageJson.NpmDependencies dependencies = packageJson.getDependencies();
            if (dependencies.isEmpty()) {
                setKeys(Collections.<NpmLibraryInfo>emptyList());
                return;
            }
            List<NpmLibraryInfo> keys = new ArrayList<>(dependencies.getCount());
            keys.addAll(getKeys(dependencies.dependencies, null, null));
            keys.addAll(getKeys(dependencies.devDependencies, DEV_BADGE, Bundle.NpmLibrariesChildren_library_dev()));
            keys.addAll(getKeys(dependencies.optionalDependencies, OPTIONAL_BADGE, Bundle.NpmLibrariesChildren_library_optional()));
            keys.addAll(getKeys(dependencies.peerDependencies, PEER_BADGE, Bundle.NpmLibrariesChildren_library_peer()));
            setKeys(keys);
        }

        @NbBundle.Messages({
            "# {0} - library name",
            "# {1} - library version",
            "NpmLibrariesChildren.description.short={0}: {1}",
            "# {0} - library name",
            "# {1} - library version",
            "# {2} - library type",
            "NpmLibrariesChildren.description.long={0}: {1} ({2})",
        })
        private List<NpmLibraryInfo> getKeys(java.util.Map<String, String> dependencies, String badge, String libraryType) {
            if (dependencies.isEmpty()) {
                return Collections.emptyList();
            }
            List<NpmLibraryInfo> keys = new ArrayList<>(dependencies.size());
            for (java.util.Map.Entry<String, String> entry : dependencies.entrySet()) {
                String description;
                if (libraryType != null) {
                    description = Bundle.NpmLibrariesChildren_description_long(entry.getKey(), entry.getValue(), libraryType);
                } else {
                    description = Bundle.NpmLibrariesChildren_description_short(entry.getKey(), entry.getValue());
                }
                keys.add(new NpmLibraryInfo(geIcon(badge), entry.getKey(), description));
            }
            Collections.sort(keys);
            return keys;
        }

        private Image geIcon(String badge) {
            Image icon = icons.get(badge);
            if (icon == null) {
                icon = ImageUtilities.loadImage(LIBRARIES_ICON);
                if (badge != null) {
                    icon = ImageUtilities.mergeImages(icon, ImageUtilities.loadImage(badge), 8, 8);
                }
                icons.put(badge, icon);
            }
            return icon;
        }

    }

    private static final class NpmLibraryNode extends AbstractNode {

        private final NpmLibraryInfo libraryInfo;


        NpmLibraryNode(NpmLibraryInfo libraryInfo) {
            super(Children.LEAF);
            this.libraryInfo = libraryInfo;
        }

        @Override
        public String getName() {
            return libraryInfo.name;
        }

        @Override
        public String getShortDescription() {
            return libraryInfo.description;
        }

        @Override
        public Image getIcon(int type) {
            return libraryInfo.icon;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return libraryInfo.icon;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }

    }

    private static final class NpmLibraryInfo implements Comparable<NpmLibraryInfo> {

        final Image icon;
        final String name;
        final String description;


        NpmLibraryInfo(Image icon, String name, String descrition) {
            this.icon = icon;
            this.name = name;
            this.description = descrition;
        }

        @Override
        public int compareTo(NpmLibraryInfo other) {
            return name.compareToIgnoreCase(other.name);
        }

    }

    private static final class CustomizeLibrariesAction extends AbstractAction {

        private final Project project;


        @NbBundle.Messages("CustomizeLibrariesAction.name=Properties")
        CustomizeLibrariesAction(Project project) {
            assert project != null;

            this.project = project;

            String name = Bundle.CustomizeLibrariesAction_name();
            putValue(NAME, name);
            putValue(SHORT_DESCRIPTION, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            project.getLookup().lookup(CustomizerProvider2.class).showCustomizer(LibraryCustomizer.CATEGORY_NAME, null);
        }

    }

}
