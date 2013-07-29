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
package org.netbeans.modules.web.browser.spi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.web.browser.Helper;
import org.netbeans.modules.web.common.api.DependentFileQuery;
import org.netbeans.modules.web.common.api.ServerURLMapping;
import org.netbeans.modules.web.common.api.WebUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 * This class has limited purpose for now and is likely going to be refactored.
 * Its only usecase is that Chrome Dev Tools send back to IDE modified content
 * of a URL and this support tries to match the URL with a project source file
 * and update it.
 *
 * @since 1.12
 */
public final class ExternalModificationsSupport {


    /**
     * There was a change in browser which needs to be persisted in the IDE.
     * @param url resource being changed
     * @param type type of resource being changed (??)
     * @param content new content of the file
     * @param currentBrowserURL URL which is currently opened in the browser;
     *   difference from url param is that currentBrowserURL can be index.html
     *   while url might be some.js file on which index.html depends
     */
    public synchronized static void handle(String url, String type, String content, URL currentBrowserURL) {
        Helper.urlBeingRefreshedFromBrowser.set(currentBrowserURL != null ? currentBrowserURL.toExternalForm() : null);
        try {
        URL u = WebUtils.stringToUrl(url);
        for (Project p : OpenProjects.getDefault().getOpenProjects()) {
            FileObject fo = ServerURLMapping.fromServer(p, u);
            if (fo != null) {
                updateFileObject(fo, content);
                break;
            }
        }
        } finally {
            Helper.urlBeingRefreshedFromBrowser.set(null);
        }
    }

    private static void updateFileObject(FileObject modifiedFile, String content) {
        for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
            FileObject fo = tc.getLookup().lookup(FileObject.class);
            if (fo != null && fo.equals(modifiedFile)) {
                EditorCookie ec = tc.getLookup().lookup(EditorCookie.class);
                if (ec != null) {
                    if (ec.isModified()) {
                        DialogDisplayer.getDefault().notify(new DialogDescriptor.Message(
                                "Content of "+FileUtil.getFileDisplayName(modifiedFile)+" is modified in the IDE and therefore cannot be replaced "
                                + "with changes coming from the Chrome Developer Tools!"));
                        return;
                    }
                    StyledDocument doc = ec.getDocument();
                    if (doc != null) {
                        try {
                            doc.remove(0, doc.getLength());
                            doc.insertString(0, content, null);
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        try {
                            ec.saveDocument();
                            return;
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }
        OutputStream os = null;
        FileLock lock = null;
        try {
            lock = modifiedFile.lock();
            os = modifiedFile.getOutputStream(lock);
            // TODO: is encoding going to be OK?? what encoding CDT sends the file in??
            FileUtil.copy(new ByteArrayInputStream(content.getBytes()), os);
        } catch (FileAlreadyLockedException ex) {
            DialogDisplayer.getDefault().notify(new DialogDescriptor.Message(
                    "Content of "+FileUtil.getFileDisplayName(modifiedFile)+" cannot be updated with "
                    + "changes coming from the Chrome Developer Tools because file is locked!"));
            return;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }
}
