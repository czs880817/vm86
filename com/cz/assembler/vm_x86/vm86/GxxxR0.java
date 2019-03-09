package com.cz.assembler.vm_x86.vm86;

class GxxxR0 {
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
            case Command.NOT:
                instructionDone = new DoneNOT();
                break;
            case Command.POP:
                instructionDone = new DonePOP();
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
            if (!res) {
                return Instruction.ip[0] + 1;
            }

            int r0 = Register.getValue(registerData, instruction.r0);
            return machine.getMemory().get(r0);
        }
    }

    private static class DoneNOT implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, ~r0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DonePOP implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Stack stack = machine.getStack();
            int r0 = stack.pop();
            if (r0 == Stack.INVALID_VALUE) {
                return -1;
            }

            Register.setValue(registerData, instruction.r0, r0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DonePUSH implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Stack stack = machine.getStack();
            int r0 = Register.getValue(registerData, instruction.r0);
            return stack.push(r0) ? Instruction.ip[0] + 1 : -1;
        }
    }
}
