
#include <stdio.h>
#include <string.h>

#include <jni.h>

#include <clang-c/Index.h>

#include "clangcompletion_LibClang.h"

CXIndex index_ = NULL;
CXTranslationUnit currentTu_ = NULL;
CXFile currentFile_ = NULL;

void disposeTu()
{
	if (currentTu_ != NULL)
	{
		clang_disposeTranslationUnit(currentTu_);
		currentTu_ = NULL;
	}
}

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
	disposeTu();
	clang_disposeIndex(index_);
}

JNIEXPORT jboolean JNICALL Java_clangcompletion_LibClang_setCurrentFile(
	JNIEnv* env, jclass obj, jstring filePath)
{
	disposeTu();
	
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

JNIEXPORT jstring JNICALL Java_clangcompletion_LibClang_getCursorType(
	JNIEnv* env, jclass obj, jint fileOffset)
{
	if (currentTu_ == NULL)
	{
		fprintf(stderr, "[getCursorType] no TU\n");
		return NULL;
	}
	
	fprintf(stderr, "[getCursorType] getting type for offset %d\n", fileOffset);
	
	CXSourceLocation srcLocation =
		clang_getLocationForOffset(currentTu_, currentFile_, fileOffset);
	CXCursor cursor = clang_getCursor(currentTu_, srcLocation);
	if (clang_Cursor_isNull(cursor))
	{
		fprintf(stderr, "[getCursorType] cursor is null\n");
		return NULL;
	}
	
	CXString cursorKindStr = clang_getCursorKindSpelling(clang_getCursorKind(cursor));
	jstring jCursorType = (*env)->NewStringUTF(env, clang_getCString(cursorKindStr));
	clang_disposeString(cursorKindStr);
	return jCursorType;
}

JNIEXPORT jobjectArray JNICALL Java_clangcompletion_LibClang_getCompletions(
	JNIEnv* env, jclass obj, jint line, jint column)
{
	if (currentTu_ == NULL)
	{
		fprintf(stderr, "[getCompletions] no TU\n");
		return NULL;
	}
	
	CXString filename = clang_getFileName(currentFile_);
	
	fprintf(stderr, "[getCompletions] file='%s' line=%d column=%d\n",
		clang_getCString(filename), line, column);
	
	CXCodeCompleteResults* results =
		clang_codeCompleteAt(currentTu_,
		                     clang_getCString(filename),
		                     line, column,
		                     /* unsaved files */ NULL, 0,
		                     clang_defaultCodeCompleteOptions());
	clang_disposeString(filename);
	
	if (results == NULL)
	{
		fprintf(stderr, "[getCompletions] no results returned\n");
		return NULL;
	}
	
	jobjectArray rtnStrings =
		(*env)->NewObjectArray(env, results->NumResults,
		                       (*env)->FindClass(env, "Ljava/lang/String;"),
		                       NULL);
	
	char completionStr[1024] = { 0 };
	for (unsigned int i = 0; i < results->NumResults; ++i)
	{
		completionStr[0] = 0;
		CXCompletionResult result = results->Results[i];
		
		unsigned int numChunks = clang_getNumCompletionChunks(result.CompletionString);
		for (unsigned int j = 0; j < numChunks; ++j)
		{
			CXString chunkText = clang_getCompletionChunkText(result.CompletionString, j);
			strcat(completionStr, clang_getCString(chunkText));
			clang_disposeString(chunkText);
		}
		
		jstring jStr = (*env)->NewStringUTF(env, completionStr);
		
		const char* cjStr = (*env)->GetStringUTFChars(env, jStr, NULL);
		fprintf(stderr, "[getCompletions] [%d] '%s'->'%s'\n",
		        i, completionStr, cjStr);
		(*env)->ReleaseStringUTFChars(env, jStr, cjStr);
		
		(*env)->SetObjectArrayElement(env, rtnStrings, i, jStr);
		
		(*env)->DeleteLocalRef(env, jStr);
	}
	
	clang_disposeCodeCompleteResults(results);
	return rtnStrings;
}
