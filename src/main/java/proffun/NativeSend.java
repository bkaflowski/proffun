package proffun;

import java.nio.ByteBuffer;

public class NativeSend {
    
    static {
        System.loadLibrary("nativesend");
    }
    
    public static native void send(ByteBuffer buffer, int bufferLength);
}
