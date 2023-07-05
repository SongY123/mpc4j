/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils */

#ifndef _Included_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
#define _Included_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    generateEncryptionParams
 * Signature: (IJ)[B
 */
JNIEXPORT jbyteArray JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_generateEncryptionParams
  (JNIEnv *, jclass, jint, jlong);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    keyGen
 * Signature: ([B)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_keyGen
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    nttTransform
 * Signature: ([BLjava/util/List;)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_nttTransform
  (JNIEnv *, jclass, jbyteArray, jobject);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    generateQuery
 * Signature: ([B[B[B[I[I)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_generateQuery
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray, jintArray, jintArray);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    generateReply
 * Signature: ([B[BLjava/util/List;[[B[I)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_generateReply
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jobject, jobjectArray, jintArray);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    decryptReply
 * Signature: ([B[BLjava/util/List;I)[J
 */
JNIEXPORT jlongArray JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_decryptReply
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jobject, jint);

/*
 * Class:     edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils
 * Method:    expansionRatio
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_edu_alibaba_mpc4j_s2pc_pir_index_single_sealpir_Acls18SingleIndexPirNativeUtils_expansionRatio
  (JNIEnv *, jclass, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
