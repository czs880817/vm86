package com.cz.assembler.vm_x86.vm86;

class GxxxV0 {
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
            case Command.PUSH:
                instructionDone = new DonePUSH();
                break;
        }
        return instructionDone;
    }

    private static class DoneJXX implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            boolean res = Instruction.doneJXXX(Register.getValue(registerData, Register.VM86_REGISTER_EFLAGS), instruction.name.charAt(1), instruction.name.charAt(2));
            return res ? instruction.v0 : Instruction.ip[0] + 1;
        }
    }

    private static class DonePUSH implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Stack stack = machine.getStack();
            return stack.push(instruction.v0) ? Instruction.ip[0] + 1 : -1;
        }
    }
}
