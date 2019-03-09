package com.cz.assembler.vm_x86.vm86;

class GxxxR0AddV0R1 {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.CMP:
                instructionDone = new DoneCMP();
                break;
            case Command.MOV:
                instructionDone = new DoneMOV();
                break;
        }
        return instructionDone;
    }

    private static class DoneCMP implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Memory memory = machine.getMemory();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, Register.VM86_REGISTER_EFLAGS, Instruction.doneCMP(memory.get(r0 + instruction.v0), r1));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneMOV implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Memory memory = machine.getMemory();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            memory.set(r0 + instruction.v0, r1);
            return Instruction.ip[0] + 1;
        }
    }
}
