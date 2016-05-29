#include <jvmti.h>
#include <jni.h>
#include <stdlib.h>


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
}

void JNICALL
methodEntry(jvmtiEnv *jvmti_env,
	    JNIEnv* jni_env,
	    jthread thread,
	    jmethodID method) {
  char* name;
  jint error_code = jvmti_env->GetMethodName(method, &name, NULL, NULL);

  if(error_code != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Method is not a jmethodID", error_code);
    jvmti_env->Deallocate((unsigned char *)name);
    return;
  }
  
  printf("Method entered: %s\n", name);
  jvmti_env->Deallocate((unsigned char *)name);
}

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *jvm, char *options, void *reserverd){
  jvmtiEnv              *jvmti;
  jvmtiError            error;
  jint                  res;
  jvmtiCapabilities     *capabilities;
  jvmtiEventCallbacks   *eventCallbacks;

  jint returnCode = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_0);

  if(returnCode != JNI_OK) {
    fprintf(stderr, "The version of JVMTI requested is not supported.\n");
    return JVMTI_ERROR_UNSUPPORTED_VERSION;
  }

  capabilities = static_cast<jvmtiCapabilities*>(calloc(1, sizeof(jvmtiCapabilities)));

  if(!capabilities) {
    fprintf(stderr, "Unable to allocate memory\n");
    return JVMTI_ERROR_OUT_OF_MEMORY;    
  }

  capabilities->can_generate_method_entry_events = 1;
  returnCode = jvmti->AddCapabilities(capabilities);
  if(returnCode != JNI_OK) {
    fprintf(stderr, "Problem during setting capabilities.\n");
    return JVMTI_ERROR_NOT_AVAILABLE;
  }

  eventCallbacks = static_cast<jvmtiEventCallbacks*>(calloc(1, sizeof(jvmtiEventCallbacks)));
  
  if(!eventCallbacks)
    {
      fprintf(stderr, "Unable to allocate memory\n");
      return JVMTI_ERROR_OUT_OF_MEMORY;
    }

  //eventCallbacks->ClassFileLoadHook = &loadClass;
  eventCallbacks->MethodEntry = &methodEntry; 
  
  returnCode = jvmti->SetEventCallbacks(eventCallbacks, (jint) sizeof(*eventCallbacks));

  if(returnCode != JNI_OK)
    {
      fprintf(stderr, "JVM blew up", returnCode);
      exit(-1);
    }

  //returnCode = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, (jthread)NULL);
  returnCode = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_METHOD_ENTRY, (jthread)NULL);
  
  if(returnCode != JNI_OK)
    {
      fprintf(stderr, "JVM blew up 2.", returnCode);
      exit(-1);
    }
  
  return JNI_OK;
}

