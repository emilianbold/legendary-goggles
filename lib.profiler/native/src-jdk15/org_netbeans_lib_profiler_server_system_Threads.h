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
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_netbeans_lib_profiler_server_system_Threads */

#ifndef _Included_org_netbeans_lib_profiler_server_system_Threads
#define _Included_org_netbeans_lib_profiler_server_system_Threads
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    recordProfilerOwnThreads
 * Signature: (ZLjava/lang/Thread;)I
 */
JNIEXPORT jint JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_recordProfilerOwnThreads
  (JNIEnv *, jclass, jboolean, jobject);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    recordAdditionalProfilerOwnThread
 * Signature: (Ljava/lang/Thread;)V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_recordAdditionalProfilerOwnThread
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    getTotalNumberOfThreads
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_getTotalNumberOfThreads
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    suspendTargetAppThreads
 * Signature: (Ljava/lang/Thread;)V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_suspendTargetAppThreads
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    resumeTargetAppThreads
 * Signature: (Ljava/lang/Thread;)V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_resumeTargetAppThreads
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    terminateTargetAppThreads
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_terminateTargetAppThreads
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    targetAppThreadsExist
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_targetAppThreadsExist
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    getAllThreads
 * Signature: ([Ljava/lang/Thread;)[Ljava/lang/Thread;
 */
JNIEXPORT jobjectArray JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_getAllThreads
  (JNIEnv *, jclass, jobjectArray);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    getThreadsStatus
 * Signature: ([Ljava/lang/Thread;[I)V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_getThreadsStatus
  (JNIEnv *, jclass, jobjectArray, jintArray);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    getJVMArguments
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_getJVMArguments
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Threads
 * Method:    getJavaCommand
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_netbeans_lib_profiler_server_system_Threads_getJavaCommand
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
