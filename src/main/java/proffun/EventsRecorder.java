package proffun;

import java.util.function.Supplier;

public class EventsRecorder {
    
    private static final ThreadLocal<MsgsBuffer> eventsLog = new ThreadLocal<>();
    private static final ThreadLocal<Monitor> monitor = new ThreadLocal<>();
    
    static {
        monitor.set(new Monitor());
        eventsLog.set(new MsgsBuffer(512));
    }
    
    private static final MsgsBuffer buff = new MsgsBuffer(512);

    public static void recordMethodEntry(long threadId, long nanoTime, String methodName, String className) {
        if(monitor.get().wasEntered()) {
            return;
        }

        monitor.get().enter();
        buff.write(1, threadId, nanoTime, methodName, className);
        monitor.get().leave();
        //System.out.println("Entry: " + methodName + " in " + className);
        //eventsLog.get().write(1, threadId, nanoTime, methodName, className);
    }

    public static void recordMethodExit(long threadId, long nanoTime, String methodName, String className) {
        //System.out.println("Exit: " + methodName + " in " + className);
        //eventsLog.get().write(2, threadId, nanoTime, methodName, className);
        if(monitor.get().wasEntered()) {
            return;
        }
        
        monitor.get().enter();
        buff.write(1, threadId, nanoTime, methodName, className);
        monitor.get().leave();
    }
    
    public static class Monitor {
        private boolean entered;
        
        public void enter() {
            this.entered = true;
        }
        
        public void leave() {
            this.entered = false;
        }
        
        public boolean wasEntered() {
            return this.entered;
        }
    }

}
