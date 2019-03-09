package com.cz.assembler.vm_x86.vm86;

class GxxxR0AddV0 {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.DIV:
                instructionDone = new DoneDIV();
                break;
            case Command.MUL:
                instructionDone = new DoneMUL();
                break;
        }

        return instructionDone;
    }

    private static class DoneDIV implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            long dividend = ((long)Register.getValue(registerData, Register.VM86_REGISTER_EDX) << 32)
                    | (long)Register.getValue(registerData, Register.VM86_REGISTER_EAX);
            long divisor = (long)machine.getMemory().get(r0 + instruction.v0);
            int quotient = (int)(dividend / divisor);
            int remainder = (int)(dividend % divisor);
            Register.setValue(registerData, Register.VM86_REGISTER_EAX, quotient);
            Register.setValue(registerData, Register.VM86_REGISTER_EDX, remainder);
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneMUL implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            int r0 = Register.getValue(registerData, instruction.r0);
            int multiplicand = Register.getValue(registerData, Register.VM86_REGISTER_EAX);
            int multiplier = machine.getMemory().get(r0 + instruction.v0);
            long res = (long)multiplicand * (long)multiplier;
            Register.setValue(registerData, Register.VM86_REGISTER_EAX, (int)res);
            Register.setValue(registerData, Register.VM86_REGISTER_EDX, (int)(res >> 32));
            return Instruction.ip[0] + 1;
        }
    }
}
