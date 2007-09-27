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

package org.netbeans.modules.cnd.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;

import org.netbeans.modules.cnd.MIMENames;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.util.SharedClassObject;
import org.openide.util.Utilities;

/**
 *  Recognizes .h header files and create .h data objects for them
 *
 *  This data loader recognizes .h header data files, creates a data object for
 *  each file, and sets up an appropriate action menus for .h file objects.
 */
public final class HDataLoader extends CndAbstractDataLoader {
    
    private static HDataLoader instance = null;

    /** Serial version number */
    static final long serialVersionUID = -2924582006340980748L;

    /** The suffix list for C/C++ header files */
    private static final String[] hdrExtensions =
				{ "h", "H", "hpp", "hxx", "SUNWCCh", "tcc" }; // NOI18N

    public HDataLoader() {
        super("org.netbeans.modules.cnd.loaders.HDataObject"); // NOI18N
        instance = this;
        createExtentions(hdrExtensions);
    }

    public static HDataLoader getInstance(){
        if (instance == null) {
            instance = SharedClassObject.findObject(HDataLoader.class, true);
        }
        return instance;
    }
    
    public void addExtensions(Collection<String> newExt) {
        // Discovery wizard can detect headers' extensions.
        // See IZ#104651:Newly found file extensions are not suggested to be included into known object type list        
        // If discovery registered extension discovered file items with extensions are disappeared.
        // Fix depend on IZ#94935:File disappears from project when user is adding new extension
        ExtensionList oldList = getExtensions();
        ExtensionList newList = (ExtensionList) oldList.clone();
        for (String name : newExt) {
            newList.addExtension(name);
        }   
        putProperty(PROP_EXTENSIONS, newList, true);
    }
    
    protected String getMimeType(){
        return MIMENames.CHEADER_MIME_TYPE;
    }

    /** set the default display name */
    @Override
    protected String defaultDisplayName() {
	return NbBundle.getMessage(HDataLoader.class, "PROP_HDataLoader_Name"); // NOI18N
    }

    /** Override because we don't have secondary files */
    @Override
    protected FileObject findSecondaryFile(FileObject fo){
	return null;
    }
    
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        if (fo.isFolder()) {
            return null;
        }
        if (fo.getExt().length() == 0) {
            File file = FileUtil.toFile(fo);
            if (file != null) {
                try {
                    // Headerless include files in the Sun Studio compiler set usually
                    // have a symlink to <file>.SUNWCCh. Try this check on Solaris because
                    // its cheaper than detectCPPByComment().
                    if (Utilities.getOperatingSystem() == Utilities.OS_SOLARIS) {
                        String path = file.getCanonicalPath();
                        File sunwcch = new File(path + ".SUNWCCh");  // NOI18N
                        if (sunwcch.exists()) {
                            return fo;
                        }
                    }
                } catch (IOException ex) {
                }
                if (detectCPPByComment(fo)) {
                    return fo;
                }
            }
        }
        return super.findPrimaryFile(fo);
    }

    /**
     *  This is a special detector which samples suffix-less header files looking for the
     *  string "-*- C++ -*-".
     *
     *  Note: Not all Sun Studio headerless includes contain this comment.
     */
    public boolean detectCPPByComment(FileObject fo){
        boolean ret = false;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            if (fo.canRead() && fo.getExt().length() == 0) {
                isr = new InputStreamReader(fo.getInputStream());
                br = new BufferedReader(isr);
                String line = null;
                try {
                    line = br.readLine();
                } catch (IOException ex) {
                }
                if(line != null){
                    if (line.startsWith("//") && line.indexOf("-*- C++ -*-") > 0) { // NOI18N
                        ret = true;
                    }
                }
            }
        } catch (IOException ex) {
//            ex.printStackTrace();
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException ex) {
//                    ex.printStackTrace();
                }
            }
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException ex) {
//                    ex.printStackTrace();
                }
            }
        }
        return ret;
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile)
	throws DataObjectExistsException, IOException {
	return new HDataObject(primaryFile, this);
    }
  
    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj,
			    FileObject primaryFile) {
	return new CndAbstractDataLoader.CndFormat(obj, primaryFile);
    }
}

