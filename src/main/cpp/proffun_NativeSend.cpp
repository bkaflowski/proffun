#include "proffun_NativeSend.h"
#include <stdlib.h>

JNIEXPORT void JNICALL Java_proffun_NativeSend_send(JNIEnv *env, jclass cls, jstring methodName, jlong timestamp)
{
  const char *m_name = env->GetStringUTFChars(methodName, 0);
  printf("Native method called %s on time: %ld\n", m_name, timestamp);
  env->ReleaseStringUTFChars(methodName, m_name);
}
