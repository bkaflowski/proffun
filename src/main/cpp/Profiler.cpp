#include <jvmti.h>
#include <jni.h>
#include <stdlib.h>


void JNICALL
vmInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
  printf("Preparing to callback Java method \n");

  char *className = "pl/kaflowski/Profiler";
  char *methodName = "VMInit";
  char *descriptor = "()V";

  jclass callbackClass = env->FindClass(className);

  if(!callbackClass) {
    fprintf(stderr, "Unable to locate callback class.\n");
    return;
  }

  jmethodID callbackMethodID = env->GetStaticMethodID(callbackClass, methodName, descriptor);
  
  if(!callbackMethodID) {
    fprintf(stderr, "Unable to locate callback VMInit method\n");
    return;
  }

  env->CallStaticVoidMethod(callbackClass, callbackMethodID, NULL);
  
  printf("VMInit, callback Java method returned successfully\n");
}

void JNICALL
loadClass(jvmtiEnv *jvmti,
	  JNIEnv* env,
	  jclass class_being_redefined,
	  jobject loader,
	  const char* name,
	  jobject protection_domain,
	  jint class_data_len,
	  const unsigned char* class_data,
	  jint* new_class_data_len,
	  unsigned char** new_class_data)
{
  printf("Loading class: %s\n", name);
  for(int i=0; i<class_data_len; i++) {
    printf(reinterpret_cast<const char*>(class_data[i]));
  }
}

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *jvm, char *options, void *reserverd){
  jvmtiEnv              *jvmti;
  jvmtiError            error;
  jint                  res;
  jvmtiCapabilities     capabilities;
  jvmtiEventCallbacks   *eventCallbacks;

  jint returnCode = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_0);

  if(returnCode != JNI_OK) {
    fprintf(stderr, "The version of JVMTI requested is not supported.\n");
    return JVMTI_ERROR_UNSUPPORTED_VERSION;
  }


  eventCallbacks = static_cast<jvmtiEventCallbacks*>(calloc(1, sizeof(jvmtiEventCallbacks)));
  
  if(!eventCallbacks)
    {
      fprintf(stderr, "Unable to allocate memory\n");
      return JVMTI_ERROR_OUT_OF_MEMORY;
    }

  eventCallbacks->VMInit = &vmInit;
  eventCallbacks->ClassFileLoadHook = &loadClass;
  
  returnCode = jvmti->SetEventCallbacks(eventCallbacks, (jint) sizeof(*eventCallbacks));

  if(returnCode != JNI_OK)
    {
      fprintf(stderr, "JVM blew up", returnCode);
      exit(-1);
    }

  returnCode = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, (jthread)NULL);
  returnCode = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, (jthread)NULL);
  
  if(returnCode != JNI_OK)
    {
      fprintf(stderr, "JVM blew up 2.", returnCode);
      exit(-1);
    }
  
  return JNI_OK;
}

