package proffun;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AddMethodEntryExitEventsTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (shouldNotInstrumentClass(className)) {
            return classfileBuffer;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new MethodEntryExitEventsVisitor(cw, className);
        cr.accept(visitor, 0);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private boolean shouldNotInstrumentClass(String className) {
        return className.startsWith("com/sun")
                || className.startsWith("java/")
                || className.startsWith("javax/")
                || className.startsWith("org/ietf")
                || className.startsWith("org/jcp")
                || className.startsWith("org/omg")
                || className.startsWith("org/w3c")
                || className.startsWith("org/xml")
                || className.startsWith("sun/")
                || className.startsWith("com/oracle/")
                || className.startsWith("jdk/")
                || className.startsWith("oracle/")
                || className.startsWith("javafx/")
                || className.contains("Agent")
                || className.contains("EventsRecorder")
                || className.contains("AddMethodEntryExitEventsTransformer")
                || className.contains("EntryExitMethodAdapter")
                || className.contains("NativeSend")
                || className.contains("MethodEntryExitEventsVisitor");
    }

}
