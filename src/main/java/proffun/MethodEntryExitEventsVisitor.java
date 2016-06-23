package proffun;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodEntryExitEventsVisitor extends ClassVisitor {

    private final String className;

    public MethodEntryExitEventsVisitor(ClassVisitor cv, String className) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && !name.equals("<init>") && !name.equals("<clinit>")
                && !(className.equals("java/lang/Thread") && name.equals("getId"))
                && !(className.equals("java/lang/Thread") && name.equals("currentThread"))
                && !(className.equals("java/lang/Throwable") && name.equals("fillInStackTrace"))
                && !(className.equals("java/lang/System") && name.equals("nanoTime"))) {
            //System.out.println("Method to instrument: " + name + " of " + className);
            mv = new EntryExitMethodAdapter(mv, name, className);
            //System.out.println("Instrumented method: " + name + " of " + className);
        }/* else if (mv != null) {
            mv = new ObjectAllocationAdapter(mv, className);
        }*/

        return mv;
    }

    @Override
    public void visitEnd() {
        cv.visitEnd();
    }
}
