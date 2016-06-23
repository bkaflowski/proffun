#include "proffun_NativeSend.h"
#include <stdlib.h>

void* cur_pos(void* address, int offset) {
  return (char *)address + offset;
}

int readIntVal(void* address, int& alreadyReadBytes) {
  int result = *((int *)address);
  alreadyReadBytes += sizeof(int);
  return result;
}

long readLongVal(void* address, int& alreadyReadBytes) {
  long result = *((long *)address);
  alreadyReadBytes += sizeof(long);
  return result;
}

jbyte* readByteArray(void* address, int arrayLength, int& alreadyReadBytes) {
  jbyte* result = (jbyte *)((char*)address + alreadyReadBytes);
  alreadyReadBytes += arrayLength;
  return result;
}

char* barrayToString(jbyte* barray, int length) {
  //decode byte to char array here
}


JNIEXPORT void JNICALL Java_proffun_NativeSend_send(JNIEnv *env, jclass cls, jobject buffer, jint length)
{
  void* pos = env->GetDirectBufferAddress(buffer);
  int readBytesSum = 0;
  while(readBytesSum < length) {
    int alreadyReadBytes = 0;
    int methodNameSize = readIntVal(cur_pos(pos, alreadyReadBytes), alreadyReadBytes);
    int classNameSize = readIntVal(cur_pos(pos, alreadyReadBytes), alreadyReadBytes);
    int eventType = readIntVal(cur_pos(pos, alreadyReadBytes), alreadyReadBytes);
    long threadId = readLongVal(cur_pos(pos, alreadyReadBytes), alreadyReadBytes);
    long timestamp = readLongVal(cur_pos(pos, alreadyReadBytes), alreadyReadBytes);
    jbyte* methodNamePos = readByteArray(cur_pos(pos, alreadyReadBytes), methodNameSize, alreadyReadBytes);
    jbyte* classNamePos = readByteArray(cur_pos(pos, alreadyReadBytes), classNameSize, alreadyReadBytes);

    char* methodName = barrayToString(methodNamePos, methodNameSize);
    char* className = barrayToString(classNamePos, classNameSize);
    
    readBytesSum += alreadyReadBytes;
    pos = cur_pos(pos, alreadyReadBytes);
    
    printf("Method name size: %i, classNameSize: %i, eventType: %i, threadId: %ld, timestamp: %ld, readBytes: %i, sumOfBytes: %i\n", 
           methodNameSize, classNameSize, eventType, threadId, timestamp, alreadyReadBytes, readBytesSum);
  }
  
  printf("Native method called for buffer of length: %i\n", length);
}
