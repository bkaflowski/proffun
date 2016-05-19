package proffun;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodEntryExitEventsVisitor extends ClassVisitor {

    private final String className;

    public MethodEntryExitEventsVisitor(ClassVisitor cv, String className) {
        super(Opcodes.ASM4, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new EntryExitMethodAdapter(mv, name, className);
        }

        return mv;
    }

    @Override
    public void visitEnd() {
        cv.visitEnd();
    }
}
