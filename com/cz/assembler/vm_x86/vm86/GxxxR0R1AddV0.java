package com.cz.assembler.vm_x86.vm86;

class GxxxR0R1AddV0 {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.ADD:
                instructionDone = new DoneADD();
                break;
            case Command.AND:
                instructionDone = new DoneAND();
                break;
            case Command.CMP:
                instructionDone = new DoneCMP();
                break;
            case Command.OR:
                instructionDone = new DoneOR();
                break;
            case Command.IMUL:
                instructionDone = new DoneIMUL();
                break;
            case Command.SUB:
                instructionDone = new DoneSUB();
                break;
            case Command.XOR:
                instructionDone = new DoneXOR();
                break;
            case Command.MOV:
                instructionDone = new DoneMOV();
                break;

        }
        return instructionDone;
    }

    private static class DoneADD implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, instruction.r0, r0 + machine.getMemory().get(r1 + instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneAND implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, instruction.r0, r0 & machine.getMemory().get(r1 + instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneCMP implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, Register.VM86_REGISTER_EFLAGS, Instruction.doneCMP(r0, machine.getMemory().get(r1 + instruction.v0)));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneOR implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, instruction.r0, r0 | machine.getMemory().get(r1 + instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneIMUL implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            long res = (long)r0 * (long)machine.getMemory().get(r1 + instruction.v0);
            Register.setValue(registerData, instruction.r0, (int)res);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneSUB implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, instruction.r0, r0 - machine.getMemory().get(r1 + instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneXOR implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, instruction.r0, r0 ^ machine.getMemory().get(r1 + instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneMOV implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int r1 = Register.getValue(registerData, instruction.r1);
            Register.setValue(registerData, instruction.r0, machine.getMemory().get(r1 + instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }
}
