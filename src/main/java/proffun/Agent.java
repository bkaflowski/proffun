package proffun;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String arg, Instrumentation inst) throws ClassNotFoundException {
        System.out.println("Premain");
        inst.addTransformer(new AddMethodEntryExitEventsTransformer());
    }
}
