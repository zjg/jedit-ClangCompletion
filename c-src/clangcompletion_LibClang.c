
#include <stdio.h>

#include <jni.h>

#include <clang-c/Index.h>

#include "clangcompletion_LibClang.h"

CXIndex index_ = NULL;
CXTranslationUnit currentTu_ = NULL;
CXFile currentFile_ = NULL;

JNIEXPORT jstring JNICALL Java_clangcompletion_LibClang_getClangVersion(
	JNIEnv* env, jobject obj)
{
	CXString clangVersion = clang_getClangVersion();
	jstring jVersion = (*env)->NewStringUTF(env, clang_getCString(clangVersion));
	clang_disposeString(clangVersion);
	return jVersion;
}


JNIEXPORT void JNICALL Java_clangcompletion_LibClang_startup(
	JNIEnv* env, jclass obj)
{
	clang_enableStackTraces();
	index_ = clang_createIndex(0, 1);
}

JNIEXPORT void JNICALL Java_clangcompletion_LibClang_shutdown(
	JNIEnv* env, jclass obj)
{
	if (currentTu_ != NULL)
	{
		clang_disposeTranslationUnit(currentTu_);
		currentTu_ = NULL;
	}

	clang_disposeIndex(index_);
}

JNIEXPORT jboolean JNICALL Java_clangcompletion_LibClang_setCurrentFile(
	JNIEnv* env, jclass obj, jstring filePath)
{
	const char* cFilePath = (*env)->GetStringUTFChars(env, filePath, NULL);
	fprintf(stderr, "[setCurrentFile] filePath='%s'\n", cFilePath);
	currentTu_ = clang_parseTranslationUnit(index_, cFilePath,
	                                        /* args */ NULL, 0,
	                                        /* unsaved files */ NULL, 0,
	                                        clang_defaultEditingTranslationUnitOptions());

	if (currentTu_ == NULL)
	{
		(*env)->ReleaseStringUTFChars(env, filePath, cFilePath);
		fprintf(stderr, "[setCurrentFile] failed to parse TU\n");
		return 0;
	}

	currentFile_ = clang_getFile(currentTu_, cFilePath);

	(*env)->ReleaseStringUTFChars(env, filePath, cFilePath);
	fprintf(stderr, "[setCurrentFile] success!\n");
	return 1;
}

