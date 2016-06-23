package proffun;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ObjectAllocationAdapter extends MethodVisitor {
    
    private final String className;

    public ObjectAllocationAdapter(MethodVisitor mv, String className) {
        super(Opcodes.ASM5, mv);
        this.className = className;
    }
    
        @Override
        public void visitEnd() {
            /*cv.visitEnd();*/
        }
}
