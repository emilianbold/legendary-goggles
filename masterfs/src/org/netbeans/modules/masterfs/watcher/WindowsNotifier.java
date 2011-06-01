/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.masterfs.watcher;

import com.sun.jna.FromNativeContext;
import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.InterruptedIOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * A {@link Notifier} implementation using Win32 API ReadDirectoryChangesW.
 * Based on JNA examples and platform library stubs.
 *
 * @author nenik
 */
public class WindowsNotifier extends Notifier<Void> {

    public static final class HANDLE extends PointerType {
        private boolean immutable;
        public HANDLE() { }
        public HANDLE(Pointer p) {
            setPointer(p);
            immutable = true;
        }

        /** Override to the appropriate object for INVALID_HANDLE_VALUE. */
        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Object o = super.fromNative(nativeValue, context);
            if (INVALID_HANDLE_VALUE.equals(o))
                return INVALID_HANDLE_VALUE;
            return o;
        }

        @Override
        public void setPointer(Pointer p) {
            if (immutable) {
                throw new UnsupportedOperationException("immutable reference");
            }

            super.setPointer(p);
        }
    }

    public static class ULONG_PTR extends IntegerType {
        public ULONG_PTR() {
                this(0);
        }

        public ULONG_PTR(long value) {
                super(Pointer.SIZE, value);
        }
    }

    public static class OVERLAPPED extends Structure {
        public ULONG_PTR Internal;
        public ULONG_PTR InternalHigh;
        public int Offset;
        public int OffsetHigh;
        public HANDLE hEvent;
    }

    public static HANDLE INVALID_HANDLE_VALUE = new HANDLE(Pointer.createConstant(
    		Pointer.SIZE == 8 ? -1 : 0xFFFFFFFFL));



    public static class HANDLEByReference extends ByReference {

    	public HANDLEByReference() {
            this(null);
        }

        public HANDLEByReference(HANDLE h) {
            super(Pointer.SIZE);
            setValue(h);
        }

        public void setValue(HANDLE h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }

        public HANDLE getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null)
                return null;
            if (INVALID_HANDLE_VALUE.getPointer().equals(p))
                return INVALID_HANDLE_VALUE;
            HANDLE h = new HANDLE();
            h.setPointer(p);
            return h;
        }
    }

    public static class FILE_NOTIFY_INFORMATION extends Structure {
        public int NextEntryOffset;
        public int Action;
        public int FileNameLength;
        // filename is not nul-terminated, so we can't use a String/WString
        public char[] FileName = new char[1];

        private FILE_NOTIFY_INFORMATION() {}

        public FILE_NOTIFY_INFORMATION(int size) {
            if (size < size()) {
               throw new IllegalArgumentException("Size must greater than "
                               + size() + ", requested " + size);
            }
            allocateMemory(size);
        }

        /** WARNING: this filename may be either the short or long form of the filename. */
        public String getFilename() {
            return new String(FileName, 0, FileNameLength/2);
        }

        @Override
        public void read() {
            // avoid reading filename until we know how long it is
            FileName = new char[0];
            super.read();
            FileName = getPointer().getCharArray(12, FileNameLength/2);
        }

        public FILE_NOTIFY_INFORMATION next() {
            if (NextEntryOffset == 0)
                    return null;
            FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
            next.useMemory(getPointer(), NextEntryOffset);
            next.read();
            return next;
        }
    }

    public static class SECURITY_ATTRIBUTES extends Structure {
        public final int nLength = size();
        public Pointer lpSecurityDescriptor;
        public boolean bInheritHandle;
    }

    interface Kernel32 extends StdCallLibrary {
        HANDLE CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode,
                    SECURITY_ATTRIBUTES lpSecurityAttributes, int dwCreationDisposition,
                    int dwFlagsAndAttributes, HANDLE hTemplateFile);

        HANDLE CreateIoCompletionPort(HANDLE FileHandle, HANDLE ExistingCompletionPort,
                    Pointer CompletionKey, int NumberOfConcurrentThreads);

        int GetLastError();

        boolean GetQueuedCompletionStatus(HANDLE CompletionPort,
                    IntByReference lpNumberOfBytes, ByReference lpCompletionKey,
                    PointerByReference lpOverlapped, int dwMilliseconds);
       
        boolean PostQueuedCompletionStatus(HANDLE CompletionPort,
                    int dwNumberOfBytesTransferred, Pointer dwCompletionKey,
                    OVERLAPPED lpOverlapped);
       
        boolean CloseHandle(HANDLE hObject);

        interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallCallback {
            void callback(int errorCode, int nBytesTransferred,
                            OVERLAPPED overlapped);
        }

      
        public boolean ReadDirectoryChangesW(HANDLE directory,
                    FILE_NOTIFY_INFORMATION info, int length, boolean watchSubtree,
                int notifyFilter, IntByReference bytesReturned, OVERLAPPED overlapped,
                OVERLAPPED_COMPLETION_ROUTINE completionRoutine);


    }

    final static Kernel32 KERNEL32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class,
            new HashMap() {{
                put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
                put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            }});



    public WindowsNotifier() { // prepare port, start thread?
    }

    public @Override void removeWatch(Void key) throws IOException {}


    public @Override String nextEvent() throws IOException, InterruptedException {
        return events.take();
    }

    public static final int INFINITE = 0xFFFFFFFF;

    public static final int FILE_NOTIFY_CHANGE_NAME = 0x00000003;
    public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES = 0x00000004;
    public static final int FILE_NOTIFY_CHANGE_SIZE = 0x00000008;
    public static final int FILE_NOTIFY_CHANGE_LAST_WRITE = 0x00000010;
    public static final int FILE_NOTIFY_CHANGE_CREATION = 0x00000040;
    public static final int FILE_NOTIFY_CHANGE_SECURITY = 0x00000100;

    private static final int NOTIFY_MASK =
            FILE_NOTIFY_CHANGE_NAME |
            FILE_NOTIFY_CHANGE_ATTRIBUTES |
            FILE_NOTIFY_CHANGE_SIZE |
            FILE_NOTIFY_CHANGE_LAST_WRITE |
            FILE_NOTIFY_CHANGE_CREATION |
            FILE_NOTIFY_CHANGE_SECURITY;

    public static final int FILE_LIST_DIRECTORY = 0x00000001;
    public static final int OPEN_EXISTING =      3;

    public static final int FILE_SHARE_READ  = 0x00000001;
    public static final int FILE_SHARE_WRITE = 0x00000002;
    public static final int FILE_SHARE_DELETE = 0x00000004;

    public static final int FILE_FLAG_OVERLAPPED =           0x40000000;
    public static final int FILE_FLAG_BACKUP_SEMANTICS =     0x02000000;

    private class FileInfo {
        public final String path;
        public final HANDLE handle;
        public final FILE_NOTIFY_INFORMATION info = new FILE_NOTIFY_INFORMATION(BUFFER_SIZE);
        public final IntByReference infoLength = new IntByReference();
        public final OVERLAPPED overlapped = new OVERLAPPED();
        public FileInfo(String path, HANDLE h) {
            this.path = path;
            this.handle = h;
        }
    }

    private static int watcherThreadID;
    private Thread watcher;
    private HANDLE port;
    private final Map<String, FileInfo> rootMap = new HashMap<String, FileInfo>();
    private final Map<HANDLE, FileInfo> handleMap = new HashMap<HANDLE, FileInfo>();
    private final BlockingQueue<String> events = new LinkedBlockingQueue<String>();

    public @Override Void addWatch(String path) throws IOException {

        if (path.length() < 3 ) throw new IOException("wrong path: " + path);

        String root = path.substring(0, 3);
        if (root.charAt(1) != ':' || root.charAt(2) != '\\') throw new IOException("wrong path");

        if (rootMap.containsKey(root)) return null; // already listening
        path = root; // listen once on the rootpath instead

        int mask = FILE_SHARE_READ | FILE_SHARE_WRITE | FILE_SHARE_DELETE;
        int flags = FILE_FLAG_BACKUP_SEMANTICS | FILE_FLAG_OVERLAPPED;
        HANDLE handle = KERNEL32.CreateFile(path,
        		FILE_LIST_DIRECTORY,
        		mask, null, OPEN_EXISTING,
                flags, null);
        if (INVALID_HANDLE_VALUE.equals(handle)) {
            throw new IOException("Unable to open " + path + ": "
                                  + KERNEL32.GetLastError());
        }
        FileInfo finfo = new FileInfo(path, handle);
        rootMap.put(path, finfo);
        handleMap.put(handle, finfo);

        // Existing port is returned
        port = KERNEL32.CreateIoCompletionPort(handle, port, handle.getPointer(), 0);
        if (INVALID_HANDLE_VALUE.equals(port)) {
            throw new IOException("Unable to create/use I/O Completion port "
                    + "for " + path + ": " + KERNEL32.GetLastError());
        }

        if (!KERNEL32.ReadDirectoryChangesW(handle, finfo.info, finfo.info.size(),
                                        true, NOTIFY_MASK, finfo.infoLength,
                                        finfo.overlapped, null)) {
            int err = KERNEL32.GetLastError();
            throw new IOException("ReadDirectoryChangesW failed on "
                                  + finfo.path + ", handle " + handle
                                  + ": " + err);
        }
        if (watcher == null) {
            Thread t = new Thread("W32 File Monitor") {
                @Override
                public void run() {
                    FileInfo finfo;
                    while (watcher != null) {
                        finfo = waitForChange();
                        if (finfo == null) continue;

                        try {
                            handleChanges(finfo);
                        } catch(IOException e) {
                            Watcher.LOG.log(Level.INFO, "handleChanges", e); 
                        }
                    }
                }
            };
            t.setDaemon(true);
            t.start();
            watcher = t;
        }

        return null;
    }
    
    @Override
    public void stop() throws IOException {
        try {
            Thread w = watcher;
            if (w == null) {
                return;
            }
            watcher = null;
            w.interrupt();
            w.join(2000);
        } catch (InterruptedException ex) {
            throw (IOException)new InterruptedIOException().initCause(ex);
        }
    }

    private void notify(File file) {
        events.add(file.getPath());
    }


    private static final int BUFFER_SIZE = 4096;
        
    private void handleChanges(FileInfo finfo) throws IOException {
        FILE_NOTIFY_INFORMATION fni = finfo.info;
        // Lazily fetch the data from native to java - asynchronous update
        fni.read();
        do {
            File file = new File(finfo.path, fni.getFilename());
            notify(file);
            
            fni = fni.next();
        } while (fni != null);
        
        if (!KERNEL32.ReadDirectoryChangesW(finfo.handle, finfo.info,
        		finfo.info.size(), true, NOTIFY_MASK,
        		finfo.infoLength, finfo.overlapped, null)) {        	
        		int err = KERNEL32.GetLastError();
                throw new IOException("ReadDirectoryChangesW failed on "
                                  + finfo.path + ": " + err);
        }
    }

    private FileInfo waitForChange() {
        IntByReference rcount = new IntByReference();
        HANDLEByReference rkey = new HANDLEByReference();
        PointerByReference roverlap = new PointerByReference();
        KERNEL32.GetQueuedCompletionStatus(port, rcount, rkey, roverlap, INFINITE);
        
        synchronized (this) { 
            return (FileInfo)handleMap.get(rkey.getValue());
        }
    }

}
