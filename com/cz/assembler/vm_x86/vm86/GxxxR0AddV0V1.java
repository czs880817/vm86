package com.cz.assembler.vm_x86.vm86;

class GxxxR0AddV0V1 {
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
            Register.setValue(registerData, Register.VM86_REGISTER_EFLAGS, Instruction.doneCMP(memory.get(r0 + instruction.v0), instruction.v1));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneMOV implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Memory memory = machine.getMemory();
            int r0 = Register.getValue(registerData, instruction.r0);
            memory.set(r0 + instruction.v0, instruction.v1);
            return Instruction.ip[0] + 1;
        }
    }
}
