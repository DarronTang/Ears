#include <jni.h>
#include <android/log.h>
#include <string>
#include <cmath>
#include <cstdlib>
#include <sstream>

const char* TAG = "Native-Darron";

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ears_advcomp_ears3_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "We're in native");
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_ears_advcomp_ears3_MainActivity_test(
        JNIEnv* env,
        jobject /* this */,
        jdouble input,
        jdouble exponent
) {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "We're in test");
    return pow(input, exponent);
}
