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
        return className.equals("proffun/Agent")
                        || className.startsWith("proffun/EventsRecorder")
                        || className.startsWith("proffun/AddMethodEntryExitEventsTransformer")
                        || className.startsWith("proffun/EntryExitMethodAdapter")
                        || className.startsWith("proffun/MethodEntryExitEventsVisitor")
                        || className.startsWith("proffun/ObjectAllocationAdapter")
                        || className.startsWith("proffun/NativeSend")
                        || className.startsWith("proffun/MsgsBuffer")
                        || className.startsWith("sun/instrument/")
                        || className.startsWith("java/lang/instrument/")
                        || className.startsWith("ognl/")
                        || className.equals("java/lang/System")
                        || className.startsWith("java/lang/ref/Reference")
                        || className.startsWith("java/lang/ThreadLocal");
    }

}
