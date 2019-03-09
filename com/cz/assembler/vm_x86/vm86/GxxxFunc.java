package com.cz.assembler.vm_x86.vm86;

import android.text.TextUtils;

class GxxxFunc {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.CALL:
                instructionDone = new DoneCALL();
                break;
        }

        return instructionDone;
    }

    private static class DoneCALL implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            switch (instruction.functionName) {
                case "printf":
                    int p = machine.getStack().top();
                    String s = machine.getMemory().getString(p);
                    if (!TextUtils.isEmpty(s)) {
                        machine.logOutput(s);
                    }
                    break;
            }
            return Instruction.ip[0] + 1;
        }
    }
}
