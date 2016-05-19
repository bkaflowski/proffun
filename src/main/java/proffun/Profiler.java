package proffun;

public class Profiler {

    private static volatile StringBuilder result = new StringBuilder();

    public static void VMInit() {
        System.out.println("Java JVMTI callback class, VMInit()");

    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            innerMethod(i);
        }
        System.out.println(result.toString());
    }

    private static void innerMethod(int i) throws InterruptedException {
        result.append(i);
        Thread.sleep(1000L);
    }
}
