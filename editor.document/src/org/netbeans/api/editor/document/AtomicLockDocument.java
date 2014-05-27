/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.api.editor.document;

import javax.swing.text.Document;
import org.netbeans.api.annotations.common.NonNull;
/**
 * Document that supports atomic locking allows
 * for transactional modifications.
 * The document is write-locked during the whole atomic
 * operation. All the operations since
 * the begining of the atomic operation
 * can be undone by using atomicUndo().
 * Typical scenario of the operation
 * is the following: <PRE>
 *   doc.atomicLock();
 *   try {
 *     ...
 *     modification1
 *     modification2
 *     ...
 *   } catch (BadLocationException e) {
 *     // something went wrong - undo till begining
 *     doc.atomicUndo();
 *   } finally {
 *     doc.atomicUnlock();
 *   }
 *   <PRE>
 *   <P>The external clients can watch for atomic operations
 *   by registering an listener through
 *   {@link addAtomicLockListener(AtomicLockListener)}
 * <p/>
 * The infrastructure registers a stub implementation for all documents;
 * the stub does not perform any locking, except that execution of {@link #runAtomic}
 * Runnable is synchronized on the document object.
 * 
 * Also see the predecessor, {@link org.netbeans.editor.AtomicLockDocument}.
 */
public interface AtomicLockDocument {

    /**
     * Provides access to the underlying Document
     * @return Document instance
     */
    public @NonNull Document getDocument();
    
    /**
     * Obtains an atomic lock. The document will become write-locked and
     * undo support should accumulate the subsequent events into one Undo item.
     */
    public void atomicLock();
    
    /**
     * Releases the atomic lock. It is an error to call atomicUnlock without
     * calling first a matching {@link #atomicLock}.
     */
    public void atomicUnlock();
    
    /**
     * Reverts modifications done during the atomic operation.
     */
    public void atomicUndo();
    
    /**
     * Runs the Runnable under atomic lock.
     * The runnable is executed while holding an atomic lock. If the Runnable
     * throws an Exception, the changes are undone as if {@link #atomicUndo} was
     * called.
     * @param r the executable to run.
     */
    public void runAtomic(@NonNull Runnable r);
    
    /**
     * Runs the Runnable under atomic lock, respecting document protection.
     * The runnable is executed while holding an atomic lock. If the Runnable
     * throws an Exception, the changes are undone as if {@link #atomicUndo} was
     * called. If an operation executed by the Runnable attempts to alter a protected 
     * document area, an exception will be thrown and all changes will be rolled back.
     * 
     * @param r the executable to run.
     */
    public void runAtomicAsUser(@NonNull Runnable r);
    
    /**
     * Attaches a Listener to receive start/end atomic lock events.
     * @param l the listener
     */
    public void addAtomicLockListener(@NonNull AtomicLockListener l);
    
    /**
     * Detaches a Listener for start/end atomic lock events.
     * @param l the listener
     */
    public void removeAtomicLockListener(@NonNull AtomicLockListener l);

}
