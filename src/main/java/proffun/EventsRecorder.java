package proffun;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EventsRecorder {
    
   private static final ThreadLocal<MsgsBuffer> eventsLog =
            ThreadLocal.withInitial(() -> new MsgsBuffer(512));

    public static void recordMethodEntry(long threadId, long nanoTime, String methodName, String className) {
        //NativeSend.send(methodName, nanoTime);
        eventsLog.get().write(1, threadId, nanoTime, methodName, className);
    }

    public static void recordMethodExit(long threadId, long nanoTime, String methodName, String className) {
        eventsLog.get().write(2, threadId, nanoTime, methodName, className);
    }
    
    /*
        Very PoC version -> add proper alignment within message, work over false sharing etc.
     */
    private static class MsgsBuffer {
        private final ByteBuffer buffer;
        private static final String CHARSET = "UTF-8";
        private static final int LONG_SIZE = 8;
        private static final int INT_SIZE = 4;
        private final int capacity;
        private int currentPosition;

        private MsgsBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
            this.capacity = capacity;
        }
        
        public boolean write(int eventType, long threadId, long timestamp, String methodName, String className) {
            try {
                final byte[] methodNameBytes = methodName.getBytes(CHARSET);
                final byte[] classNameBytes = className.getBytes(CHARSET);
                final int methodNameSize = methodNameBytes.length;
                final int classNameSize = classNameBytes.length;
                final int size = 2 * LONG_SIZE + 3 * INT_SIZE + methodNameSize + classNameSize;

                if(currentPosition + size > capacity) {
                    //send(buffer, currentPosition);
                    sendNative(buffer, currentPosition);
                    currentPosition = 0;
                    buffer.position(currentPosition);
                }
                
                buffer.putInt(methodNameSize);
                buffer.putInt(classNameSize);
                buffer.putInt(eventType);
                buffer.putLong(threadId);
                buffer.putLong(timestamp);
                buffer.put(methodNameBytes);
                buffer.put(classNameBytes);
                currentPosition += size;
                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
        }

        private void send(ByteBuffer buffer, int length) {
            buffer.position(0);
            System.out.printf("b: %d, l: %d\n", buffer.position(), length);
            while(buffer.position() < length) {
                final int methodNameSize = buffer.getInt();
                final int classNameSize = buffer.getInt();

                final int eventType = buffer.getInt();
                final long threadId = buffer.getLong();
                final long timestamp = buffer.getLong();
                
                final byte[] methodNameBytes = new byte[methodNameSize];
                buffer.get(methodNameBytes);
                
                final byte[] classNameBytes = new byte[classNameSize];
                buffer.get(classNameBytes);

                try {
                    System.out.println(getEventName(eventType) + " method: " + new String(methodNameBytes, CHARSET)
                            + " of class: " + new String(classNameBytes, CHARSET) +
                            " at nanotime: " + timestamp + " within threadId: " + threadId);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            
        }
        
        private void sendNative(ByteBuffer buffer, int length) {
            buffer.position(0);
            NativeSend.send(buffer, length);
        }
        
        private String getEventName(int eventType) {
            if(eventType == 1) {
                return "Enter";
            }
            if(eventType == 2) {
                return "Exit";
            }
            return "Unknown";
        }

    }
    
}
