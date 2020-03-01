#include <jni.h>
#include <string>
#include "AACEncoder.h"

extern "C" JNIEXPORT jint JNICALL
Java_me_huaisu_audio_encode_AacEncoder_encodePcmFile(
        JNIEnv* env,
        jobject thiz,
        jstring pcmFile,
        jstring aacFile) {
    AACEncoder* encoder = new AACEncoder();
    const char* pcm_file = env->GetStringUTFChars(pcmFile, NULL);
    if (pcm_file == NULL) {
        return NULL;
    }
    const char* aac_file = env->GetStringUTFChars(aacFile, NULL);
    if (aac_file == NULL) {
        return NULL;
    }
    env->ReleaseStringUTFChars(pcmFile, pcm_file);
    env->ReleaseStringUTFChars(aacFile, aac_file);
    return encoder->encode(pcm_file, aac_file);
}
