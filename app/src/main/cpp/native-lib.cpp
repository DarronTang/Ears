#include <jni.h>
#include <string>
#include <cmath>
#include <cstdlib>
#include <sstream>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ears_advcomp_ears3_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_ears_advcomp_ears3_MainActivity_mineTest(
        JNIEnv* env,
        jobject /* this */,
        jint i,
        jstring s) {
    return pow((double)i, atof(env->GetStringUTFChars(s, 0)));
}
