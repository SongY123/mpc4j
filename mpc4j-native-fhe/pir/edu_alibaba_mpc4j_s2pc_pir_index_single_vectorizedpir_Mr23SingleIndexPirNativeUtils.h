/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils */

#ifndef _Included_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
#define _Included_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
 * Method:    generateEncryptionParams
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils_generateEncryptionParams
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
 * Method:    keyGen
 * Signature: ([B)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils_keyGen
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
 * Method:    preprocessDatabase
 * Signature: ([B[[JI)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils_preprocessDatabase
  (JNIEnv *, jclass, jbyteArray, jobjectArray, jint);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
 * Method:    generateQuery
 * Signature: ([B[B[B[II)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils_generateQuery
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray, jintArray, jint);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
 * Method:    generateReply
 * Signature: ([BLjava/util/List;[[B[B[B[BI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils_generateReply
  (JNIEnv *, jclass, jbyteArray, jobject, jobjectArray, jbyteArray, jbyteArray, jbyteArray, jint);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils
 * Method:    decryptReply
 * Signature: ([B[B[BII)J
 */
JNIEXPORT jlong JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_vectorizedpir_Mr23SingleIndexPirNativeUtils_decryptReply
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
