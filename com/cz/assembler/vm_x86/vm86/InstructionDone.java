package com.cz.assembler.vm_x86.vm86;

interface InstructionDone {
    int done(Instruction instruction, Machine machine);
}
