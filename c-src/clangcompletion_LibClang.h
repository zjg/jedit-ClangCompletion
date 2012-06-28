/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class clangcompletion_LibClang */

#ifndef _Included_clangcompletion_LibClang
#define _Included_clangcompletion_LibClang
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     clangcompletion_LibClang
 * Method:    getClangVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_clangcompletion_LibClang_getClangVersion
  (JNIEnv *, jclass);

/*
 * Class:     clangcompletion_LibClang
 * Method:    startup
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_clangcompletion_LibClang_startup
  (JNIEnv *, jclass);

/*
 * Class:     clangcompletion_LibClang
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_clangcompletion_LibClang_shutdown
  (JNIEnv *, jclass);

/*
 * Class:     clangcompletion_LibClang
 * Method:    setCurrentFile
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_clangcompletion_LibClang_setCurrentFile
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
