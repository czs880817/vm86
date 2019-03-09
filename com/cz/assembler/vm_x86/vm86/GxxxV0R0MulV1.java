package com.cz.assembler.vm_x86.vm86;

class GxxxV0R0MulV1 {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.JA:
            case Command.JBE:
            case Command.JMP:
            case Command.JNB:
            case Command.JNZ:
            case Command.JZ:
                instructionDone = new DoneJXX();
                break;
        }
        return instructionDone;
    }

    private static class DoneJXX implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            boolean res = Instruction.doneJXXX(Register.getValue(registerData, Register.VM86_REGISTER_EFLAGS), instruction.name.charAt(1), instruction.name.charAt(2));
            if (!res) {
                return Instruction.ip[0] + 1;
            }

            int r0 = Register.getValue(registerData, instruction.r0);
            return machine.getMemory().get(instruction.v0 + instruction.v1 * r0);
        }
    }
}
