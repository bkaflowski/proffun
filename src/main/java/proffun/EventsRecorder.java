package proffun;

public class EventsRecorder {

    public static void recordMethodEntry(long threadId, long nanoTime, String methodName, String className) {
        System.out.println("Entering method: " + methodName + " of class: " + className +
                " at nanotime: " + nanoTime + " within threadId: " + threadId);
    }

    public static void recordMethodExit(long threadId, long nanoTime, String methodName, String className) {
        System.out.println("Exiting method: " + methodName + " of class: " + className +
                " at nanotime: " + nanoTime + " within threadId: " + threadId);

    }
}
