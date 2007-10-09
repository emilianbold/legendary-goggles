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
/* Header for class org_netbeans_lib_profiler_server_system_Classes */

#ifndef _Included_org_netbeans_lib_profiler_server_system_Classes
#define _Included_org_netbeans_lib_profiler_server_system_Classes
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    getAllLoadedClasses
 * Signature: ()[Ljava/lang/Class;
 */
JNIEXPORT jobjectArray JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_getAllLoadedClasses
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    cacheLoadedClasses
 * Signature: ([Ljava/lang/Class;I)V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_cacheLoadedClasses
  (JNIEnv *, jclass, jobjectArray, jint);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    getCachedClassFileBytes
 * Signature: (Ljava/lang/Class;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_getCachedClassFileBytes
  (JNIEnv *, jclass, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    enableClassLoadHook
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_enableClassLoadHook
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    disableClassLoadHook
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_disableClassLoadHook
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    getObjectSize
 * Signature: (Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_getObjectSize
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    setWaitTrackingEnabled
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_setWaitTrackingEnabled
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    setSleepTrackingEnabled
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_setSleepTrackingEnabled
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    setVMObjectAllocEnabled
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_setVMObjectAllocEnabled
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    notifyAboutClassLoaderUnloading
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_notifyAboutClassLoaderUnloading
  (JNIEnv *, jclass);

/*
 * Class:     org_netbeans_lib_profiler_server_system_Classes
 * Method:    doRedefineClasses
 * Signature: ([Ljava/lang/Class;[[B)I
 */
JNIEXPORT jint JNICALL Java_org_netbeans_lib_profiler_server_system_Classes_doRedefineClasses
  (JNIEnv *, jclass, jobjectArray, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif
