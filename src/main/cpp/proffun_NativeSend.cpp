#include "proffun_NativeSend.h"
#include <stdlib.h>

void* cur_pos(void* address, int offset) {
  return (char *)address + offset;
}

JNIEXPORT void JNICALL Java_proffun_NativeSend_send(JNIEnv *env, jclass cls, jobject buffer, jint length)
{
  void* pos = env->GetDirectBufferAddress(buffer);
  int readBytesSum = 0;
  while(readBytesSum < length) {
    int msgReadBytes = 0;
    int methodNameSize = *((int *)cur_pos(pos, msgReadBytes));
    msgReadBytes += sizeof(int);
    int classNameSize = *((int *)cur_pos(pos, msgReadBytes));
    msgReadBytes += sizeof(int);
    int eventType = *((int *)cur_pos(pos, msgReadBytes));
    msgReadBytes += sizeof(int);
    long threadId = *((long *)cur_pos(pos, msgReadBytes));
    msgReadBytes += sizeof(long);
    long timestamp = *((long *)cur_pos(pos, msgReadBytes));
    msgReadBytes += sizeof(long);
    //jbyte* methodNamePos = (jbyte *)((char*)pos + msgReadBytes);
    msgReadBytes += methodNameSize;
    //jbyte* classNamePos = (jbyte *)((char *)pos + msgReadBytes);
    msgReadBytes += classNameSize;
    readBytesSum += msgReadBytes;
    pos = cur_pos(pos, msgReadBytes);
    printf("Method name size: %i, classNameSize: %i, eventType: %i, threadId: %ld, timestamp: %ld, readBytes: %i, sumOfBytes: %i\n", 
           methodNameSize, classNameSize, eventType, threadId, timestamp, msgReadBytes, readBytesSum);
  }
  printf("Native method called for buffer of length: %i\n", length);
}
