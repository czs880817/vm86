package com.cz.assembler.vm_x86.vm86;

class GxxxR0R1R2 {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.SHRD:
                instructionDone = new DoneSHRD();
                break;
        }
        return instructionDone;
    }

    private static class DoneSHRD implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r1 = Register.getValue(registerData, instruction.r1);
            int r2 = Register.getValue(registerData, instruction.r2);
            Register.setValue(registerData, instruction.r0, r1 >> r2);
            return Instruction.ip[0] + 1;
        }
    }
}
