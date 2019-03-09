package com.cz.assembler.vm_x86.vm86;

class GxxxR0R1AddR2OpV0 {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.LEA:
                instructionDone = new DoneLEA();
                break;
        }
        return instructionDone;
    }

    private static class DoneLEA implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r1 = Register.getValue(registerData, instruction.r1);
            int r2 = Register.getValue(registerData, instruction.r2);
            Register.setValue(registerData, instruction.r0, instruction.op == '+' ? r1 + r2 + instruction.v0 : r1 + r2 * instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }
}
