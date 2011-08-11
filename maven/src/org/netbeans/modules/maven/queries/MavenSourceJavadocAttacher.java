/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.queries;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.queries.SourceJavadocAttacher.AttachmentListener;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.spi.java.queries.SourceJavadocAttacherImplementation;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=SourceJavadocAttacherImplementation.class, position=200)
public class MavenSourceJavadocAttacher implements SourceJavadocAttacherImplementation {

    private static final RequestProcessor RP = new RequestProcessor(MavenSourceJavadocAttacher.class.getName(), 5);

    @Override public boolean attachSources(@NonNull URL root, @NonNull AttachmentListener listener) throws IOException {
        return attach(root, listener, false);
    }

    @Override public boolean attachJavadoc(@NonNull URL root, @NonNull AttachmentListener listener) throws IOException {
        return attach(root, listener, true);
    }

    @Messages({"# {0} - artifact ID", "attaching=Attaching {0}"})
    private boolean attach(@NonNull URL root, @NonNull final AttachmentListener listener, final boolean javadoc) throws IOException {
        File file = FileUtil.archiveOrDirForURL(root);
        if (file == null) {
            return false;
        }
        final String[] coordinates = MavenFileOwnerQueryImpl.findCoordinates(file);
        if (coordinates == null) {
            return false;
        }
        RP.post(new Runnable() {
            @Override public void run() {
                boolean attached = false;
                try {
                    MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
                    Artifact art = online.createArtifactWithClassifier(coordinates[0], coordinates[1], coordinates[2], "jar", javadoc ? "javadoc" : "sources");
                    AggregateProgressHandle hndl = AggregateProgressFactory.createHandle(Bundle.attaching(art.getId()),
                        new ProgressContributor[] {AggregateProgressFactory.createProgressContributor("attach")},
                        ProgressTransferListener.cancellable(), null);
                    ProgressTransferListener.setAggregateHandle(hndl);
                    try {
                        hndl.start();
                        List<ArtifactRepository> repos = new ArrayList<ArtifactRepository>();
                        // XXX is there some way to determine from local metadata which remote repo it is from? (i.e. API to read _maven.repositories)
                        for (RepositoryInfo info : RepositoryPreferences.getInstance().getRepositoryInfos()) {
                            if (info.isRemoteDownloadable()) {
                                repos.add(EmbedderFactory.createRemoteRepository(online, info.getRepositoryUrl(), info.getId()));
                            }
                        }
                        online.resolve(art, repos, online.getLocalRepository());
                        if (art.getFile().isFile()) {
                            attached = true;
                        }
                    } catch (ThreadDeath d) {
                    } catch (AbstractArtifactResolutionException x) {
                        // XXX probably ought to display some sort of notification in status bar
                    } finally {
                        hndl.finish();
                        ProgressTransferListener.clearAggregateHandle();
                    }
                } finally {
                    if (attached) {
                        listener.attachmentSucceeded();
                    } else {
                        listener.attachmentFailed();
                    }
                }
            }
        });
        return true;
    }
}
