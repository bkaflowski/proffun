package proffun;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EntryExitMethodAdapter extends MethodVisitor {

    private final String methodName;
    private final String className;

    public EntryExitMethodAdapter(MethodVisitor mv, String methodName, String className) {
        super(Opcodes.ASM5, mv);
        this.methodName = methodName;
        this.className = className;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        instrumentWithEventRecording("recordMethodEntry");
    }

    @Override
    public void visitInsn(int opcode) {
        if (isReturnOpCode(opcode) || isThrowExceptionOpCode(opcode)) {
            instrumentWithEventRecording("recordMethodExit");
        }
        mv.visitInsn(opcode);
    }

    private boolean isReturnOpCode(int opcode) {
        return (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN);
    }

    private boolean isThrowExceptionOpCode(int opcode) {
        return opcode == Opcodes.ATHROW;
    }

    private void instrumentWithEventRecording(String recordingMethodName) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitLdcInsn(methodName);
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Lproffun/EventsRecorder;",
                    recordingMethodName, "(JJLjava/lang/String;Ljava/lang/String;)V", false);
        }
}