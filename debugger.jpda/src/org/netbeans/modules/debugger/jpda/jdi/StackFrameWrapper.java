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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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

package org.netbeans.modules.debugger.jpda.jdi;

// DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
// Generated by org.netbeans.modules.debugger.jpda.jdi.Generate class located in 'gensrc' folder,
// perform the desired modifications there and re-generate by "ant generate".

/**
 * Wrapper for StackFrame JDI class.
 * Use methods of this class instead of direct calls on JDI objects.
 * These methods assure that exceptions thrown from JDI calls are handled appropriately.
 *
 * @author Martin Entlicher
 */
public final class StackFrameWrapper {

    private StackFrameWrapper() {}

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    /** Wrapper for method getArgumentValues from JDK 1.6. */
    public static java.util.List<com.sun.jdi.Value> getArgumentValues(com.sun.jdi.StackFrame a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return (java.util.List<com.sun.jdi.Value>) com.sun.jdi.StackFrame.class.getMethod("getArgumentValues").invoke(a);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof com.sun.jdi.InvalidStackFrameException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper((com.sun.jdi.InvalidStackFrameException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    /** Wrapper for method getArgumentValues from JDK 1.6. */
    public static java.util.List<com.sun.jdi.Value> getArgumentValues0(com.sun.jdi.StackFrame a) throws org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return (java.util.List<com.sun.jdi.Value>) com.sun.jdi.StackFrame.class.getMethod("getArgumentValues").invoke(a);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                return java.util.Collections.emptyList();
            }
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                return java.util.Collections.emptyList();
            }
            if (t instanceof com.sun.jdi.InvalidStackFrameException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper((com.sun.jdi.InvalidStackFrameException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.Value getValue(com.sun.jdi.StackFrame a, com.sun.jdi.LocalVariable b) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return a.getValue(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.Map<com.sun.jdi.LocalVariable, com.sun.jdi.Value> getValues0(com.sun.jdi.StackFrame a, java.util.List<? extends com.sun.jdi.LocalVariable> b) throws org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return a.getValues(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyMap();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyMap();
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.Map<com.sun.jdi.LocalVariable, com.sun.jdi.Value> getValues(com.sun.jdi.StackFrame a, java.util.List<? extends com.sun.jdi.LocalVariable> b) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return a.getValues(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.Location location(com.sun.jdi.StackFrame a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return a.location();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static void setValue(com.sun.jdi.StackFrame a, com.sun.jdi.LocalVariable b, com.sun.jdi.Value c) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            a.setValue(b, c);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.ObjectReference thisObject(com.sun.jdi.StackFrame a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            try {
                return a.thisObject();
            } catch (com.sun.jdi.InternalException iex) {
                if (iex.errorCode() == 35) { // INVALID_SLOT, see http://www.netbeans.org/issues/show_bug.cgi?id=163652
                    return null;
                } else {
                    throw iex; // re-throw the original
                }
            }
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.ThreadReference thread(com.sun.jdi.StackFrame a) throws org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper {
        try {
            return a.thread();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static com.sun.jdi.LocalVariable visibleVariableByName(com.sun.jdi.StackFrame a, java.lang.String b) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper {
        try {
            return a.visibleVariableByName(b);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        } catch (com.sun.jdi.NativeMethodException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.List<com.sun.jdi.LocalVariable> visibleVariables0(com.sun.jdi.StackFrame a) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper {
        try {
            return a.visibleVariables();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        } catch (com.sun.jdi.NativeMethodException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper(ex);
        }
    }

    // DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
    public static java.util.List<com.sun.jdi.LocalVariable> visibleVariables(com.sun.jdi.StackFrame a) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper {
        try {
            return a.visibleVariables();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InvalidStackFrameException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.InvalidStackFrameExceptionWrapper(ex);
        } catch (com.sun.jdi.NativeMethodException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.NativeMethodExceptionWrapper(ex);
        }
    }

}
