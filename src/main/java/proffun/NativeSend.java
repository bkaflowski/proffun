package proffun;

public class NativeSend {
    
    static {
        System.loadLibrary("nativesend");
    }
    
    public static native void send(String methodName, long timestamp);
}
