package com.cz.assembler.vm_x86.vm86;

class GxxxR0V0 {
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
            case Command.MOV:
                instructionDone = new DoneMOV();
                break;
            case Command.MOVZX:
                instructionDone = new DoneMOVZX();
                break;
            case Command.SAR:
                instructionDone = new DoneSAR();
                break;
            case Command.SHL:
                instructionDone = new DoneSHL();
                break;
            case Command.SHR:
                instructionDone = new DoneSHR();
                break;
            case Command.SUB:
                instructionDone = new DoneSUB();
                break;
            case Command.XOR:
                instructionDone = new DoneXOR();
                break;

        }
        return instructionDone;
    }

    private static class DoneADD implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, r0 + instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneAND implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, r0 & instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneCMP implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, Register.VM86_REGISTER_EFLAGS, Instruction.doneCMP(r0, instruction.v0));
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneMOV implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Register.setValue(registerData, instruction.r0, instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneMOVZX implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Register.setValue(registerData, instruction.r0, instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneSAR implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, machine.getMemory().get(r0) >> instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneSHL implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, r0 << instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneSHR implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, r0 >> instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneSUB implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, r0 - instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneXOR implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            Register.setValue(registerData, instruction.r0, r0 ^ instruction.v0);
            return Instruction.ip[0] + 1;
        }
    }
}
