package proffun;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Agent {

    public static void premain(String arg, Instrumentation inst) throws ClassNotFoundException {
       // System.out.println("Premain");

        try {
            Class.forName("proffun.EventsRecorder");
         } catch (Throwable t) {
           t.printStackTrace();
         }

        Class<?>[] classes = inst.getAllLoadedClasses();
        List<Class<?>> classList = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            if (inst.isModifiableClass(classes[i]) && !shouldNotInstrumentClass(classes[i])) {
                classList.add(classes[i]);
            }
        }
        
        inst.addTransformer(new AddMethodEntryExitEventsTransformer(), inst.isRetransformClassesSupported());

        try {
            Class<?>[] arrayForNotHavingMonitorCtrlBreakException = new Class<?>[classList.size()];
            inst.retransformClasses((Class<?>[]) classList.toArray(arrayForNotHavingMonitorCtrlBreakException));
        } catch (UnmodifiableClassException e) {
            System.err.println("Cannot retransform early loaded classes.");
        } catch (Throwable e) {
            System.err.println("Exception during class retransformation.");
            e.printStackTrace(System.err);
        }
        
        System.out.println("Finish transform");
    }
    
    private static boolean shouldNotInstrumentClass(Class<?> clazz) {
        return clazz.getName().startsWith("proffun/Agent")
                || clazz.getName().startsWith("proffun/EventsRecorder")
                || clazz.getName().startsWith("proffun/AddMethodEntryExitEventsTransformer")
                || clazz.getName().startsWith("proffun/EntryExitMethodAdapter")
                || clazz.getName().startsWith("proffun/MethodEntryExitEventsVisitor")
                || clazz.getName().startsWith("proffun/ObjectAllocationAdapter")
                || clazz.getName().startsWith("proffun/NativeSend")
                || clazz.getName().startsWith("proffun/MsgsBuffer")
                || clazz.getName().startsWith("sun/instrument/")
                || clazz.getName().startsWith("java/lang/instrument/")
                || clazz.getName().startsWith("ognl/")
                || clazz.getName().equals("java/lang/System")
                || clazz.getName().startsWith("java/lang/ThreadLocal");
    }
}
