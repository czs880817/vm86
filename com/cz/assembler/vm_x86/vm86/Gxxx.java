package com.cz.assembler.vm_x86.vm86;

import java.util.Random;

class Gxxx {
    static InstructionDone findInstructionDone(String name) {
        InstructionDone instructionDone = null;
        switch (name) {
            case Command.LEAVE:
                instructionDone = new DoneLEAVE();
                break;
            case Command.RETN:
                instructionDone = new DoneRETN();
                break;
            case Command.CZ:
                instructionDone = new DoneCZ();
                break;
        }

        return instructionDone;
    }

    private static class DoneLEAVE implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Register.RegisterData[] registerData = machine.getRegisterData();
            Register.setValue(registerData, Register.VM86_REGISTER_ESP, Register.getValue(registerData, Register.VM86_REGISTER_EBP));
            machine.getStack().pop();
            return Instruction.ip[0] + 1;
        }
    }

    private static class DoneRETN implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            int res = machine.getStack().pop();
            return res == Stack.RETURN_ADDRESS ? Instruction.ip[0] + 1 : -1;
        }
    }

    private static class DoneCZ implements InstructionDone {
        @Override
        public int done(Instruction instruction, Machine machine) {
            Instruction.ip[0] = new Random().nextInt(machine.getText().getProc().getInstructionsCount() - 1);
            return Instruction.ip[0];
        }
    }
}
