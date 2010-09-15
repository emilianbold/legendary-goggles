/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.modelimpl.csm.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.util.UIDs;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceKind;
import org.netbeans.modules.cnd.modelimpl.repository.FileReferencesKey;
import org.netbeans.modules.cnd.modelimpl.repository.PersistentUtils;
import org.netbeans.modules.cnd.modelimpl.textcache.NameCache;
import org.netbeans.modules.cnd.modelimpl.uid.UIDObjectFactory;
import org.netbeans.modules.cnd.repository.spi.Persistent;
import org.netbeans.modules.cnd.repository.support.SelfPersistent;

/**
 *
 * @author Alexander Simon
 */
public class FileComponentReferences extends FileComponent implements Persistent, SelfPersistent {

    private final Map<CsmReference, CsmUID<CsmObject>> references;
    private final ReadWriteLock referencesLock = new ReentrantReadWriteLock();
    private final CsmUID<CsmFile> owner;

    // empty stub
    private static final FileComponentReferences EMPTY = new FileComponentReferences() {

        @Override
        public void put() {
        }
    };

    public static FileComponentReferences empty() {
        return EMPTY;
    }

    public FileComponentReferences(FileImpl file) {
        super(new FileReferencesKey(file));
        references = new HashMap<CsmReference, CsmUID<CsmObject>>();
        owner = file.getUID();
        put();
    }

    public FileComponentReferences(DataInput input) throws IOException {
        super(input);
        owner = UIDObjectFactory.getDefaultFactory().readUID(input);
        int collSize = input.readInt();
        references = new HashMap<CsmReference, CsmUID<CsmObject>>(collSize);
        for(int i = 0; i < collSize; i++) {
            int start = input.readInt();
            int end = input.readInt();
            CharSequence identifier = PersistentUtils.readUTF(input, NameCache.getManager());
            CsmReferenceKind refKind = CsmReferenceKind.values()[input.readByte()];
            CsmUID<CsmObject> refObj = UIDObjectFactory.getDefaultFactory().readUID(input);
            CsmReference ref = new ReferenceImpl(owner, refKind, refObj, start, end, identifier);
            references.put(ref, refObj);
        }
    }

    // only for EMPTY static field
    private FileComponentReferences() {
        super((org.netbeans.modules.cnd.repository.spi.Key) null);
        references = new HashMap<CsmReference, CsmUID<CsmObject>>();
        owner = null;
    }

    void clean() {
        referencesLock.writeLock().lock();
        try {
            references.clear();
        } finally {
            referencesLock.writeLock().unlock();
        }
        put();
    }

    Iterator<CsmReference> getReferences(Collection<CsmObject> objects) {
        Set<CsmUID<CsmObject>> searchFor = new HashSet<CsmUID<CsmObject>>(objects.size());
        for(CsmObject obj : objects) {
            CsmUID<CsmObject> uid = UIDs.get(obj);
            searchFor.add(uid);
        }
        List<CsmReference> res = new ArrayList<CsmReference>();
        referencesLock.readLock().lock();
        try {
            for(Map.Entry<CsmReference, CsmUID<CsmObject>> entry : references.entrySet()) {
                if (searchFor.contains(entry.getValue())){
                    res.add(entry.getKey());
                }
            }
        } finally {
            referencesLock.readLock().unlock();
        }
        return res.iterator();
    }

    void addReference(CsmReference ref, CsmObject referencedObject) {
        CsmUID<CsmObject> referencedUID = UIDs.get(referencedObject);
        ReferenceImpl refImpl = new ReferenceImpl(owner, ref, referencedUID);
        referencesLock.writeLock().lock();
        try {
            references.put(refImpl, referencedUID);
        } finally {
            referencesLock.writeLock().unlock();
        }
        put();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        UIDObjectFactory defaultFactory = UIDObjectFactory.getDefaultFactory();
        defaultFactory.writeUID(owner, out);
        referencesLock.readLock().lock();
        try {
            out.write(references.size());
            for(Map.Entry<CsmReference, CsmUID<CsmObject>> entry : references.entrySet()) {
                out.writeInt(entry.getKey().getStartOffset());
                out.writeInt(entry.getKey().getEndOffset());
                out.writeUTF(entry.getKey().getText().toString());
                out.writeByte(entry.getKey().getKind().ordinal());
                defaultFactory.writeUID(entry.getValue(), out);
            }
        } finally {
            referencesLock.readLock().unlock();
        }
    }

    private static final class ReferenceImpl implements CsmReference {
        private final CsmUID<CsmFile> file;
        private final CsmReferenceKind refKind;
        private final CsmUID<CsmObject> refObj;
        private final int start;
        private final int end;
        private final CharSequence identifier;

        private ReferenceImpl(CsmUID<CsmFile> file, CsmReferenceKind refKind, CsmUID<CsmObject> refObj,
                int start, int end, CharSequence identifier) {
            this.file = file;
            this.refKind = refKind;
            this.refObj = refObj;
            this.start = start;
            this.end = end;
            this.identifier = identifier;
        }

        private ReferenceImpl(CsmUID<CsmFile> file, CsmReference delegate, CsmUID<CsmObject> refObj) {
            this.file = file;
            this.refKind = delegate.getKind();
            this.refObj = refObj;
            this.start = delegate.getStartOffset();
            this.end = delegate.getEndOffset();
            this.identifier = delegate.getText();
        }

        @Override
        public CsmReferenceKind getKind() {
            return refKind;
        }

        @Override
        public CsmObject getReferencedObject() {
            return refObj.getObject();
        }

        @Override
        public CsmObject getOwner() {
            throw new UnsupportedOperationException("Not supported yet."); // NOI18N
        }

        @Override
        public CsmFile getContainingFile() {
            return file.getObject();
        }

        @Override
        public int getStartOffset() {
            return start;
        }

        @Override
        public int getEndOffset() {
            return end;
        }

        @Override
        public Position getStartPosition() {
            throw new UnsupportedOperationException("Not supported yet."); // NOI18N
        }

        @Override
        public Position getEndPosition() {
            throw new UnsupportedOperationException("Not supported yet."); // NOI18N
        }

        @Override
        public CharSequence getText() {
            return identifier;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 17 * hash + (this.refKind != null ? this.refKind.hashCode() : 0);
            hash = 17 * hash + this.start;
            hash = 17 * hash + this.end;
            hash = 17 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ReferenceImpl other = (ReferenceImpl) obj;
            if (this.refKind != other.refKind) {
                return false;
            }
            if (this.start != other.start) {
                return false;
            }
            if (this.end != other.end) {
                return false;
            }
            if (this.identifier != other.identifier && (this.identifier == null || !this.identifier.equals(other.identifier))) {
                return false;
            }
            return true;
        }
    }
}
