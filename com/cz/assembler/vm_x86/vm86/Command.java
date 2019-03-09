package com.cz.assembler.vm_x86.vm86;

public class Command {
    public static final String SEGMENT_DATA = ".data";
    public static final String SEGMENT_CODE = ".code";

    public static final String SEGMENT_DS = "ds";
    public static final String SEGMENT_CS = "cs";

    public static final String REGISTER_AH = "ah";
    public static final String REGISTER_AL = "al";
    public static final String REGISTER_AX = "ax";
    public static final String REGISTER_BH = "bh";
    public static final String REGISTER_BL = "bl";
    public static final String REGISTER_BX = "bx";
    public static final String REGISTER_CH = "ch";
    public static final String REGISTER_CL = "cl";
    public static final String REGISTER_CX = "cx";
    public static final String REGISTER_DH = "dh";
    public static final String REGISTER_DL = "dl";
    public static final String REGISTER_DX = "dx";
    public static final String REGISTER_EAX = "eax";
    public static final String REGISTER_EBX = "ebx";
    public static final String REGISTER_ECX = "ecx";
    public static final String REGISTER_EDX = "edx";
    public static final String REGISTER_EBP = "ebp";
    public static final String REGISTER_EDI = "edi";
    public static final String REGISTER_ESI = "esi";
    public static final String REGISTER_ESP = "esp";

    public static final String PROC = "proc";
    public static final String NEAR = "near";
    public static final String ENDP = "endp";
    public static final String DWORD = "dword";
    public static final String WORD = "word";
    public static final String BYTE = "byte";
    public static final String QWORD = "qword";
    public static final String DD = "dd";
    public static final String DW = "dw";
    public static final String DB = "db";
    public static final String OFFSET = "offset";

    public static final String LEAVE = "leave";
    public static final String RETN = "retn";
    public static final String CALL = "call";
    public static final String DIV = "div";
    public static final String MUL = "mul";
    public static final String JA = "ja";
    public static final String JBE = "jbe";
    public static final String JMP = "jmp";
    public static final String JNB = "jnb";
    public static final String JNZ = "jnz";
    public static final String JZ = "jz";
    public static final String PUSH = "push";
    public static final String CMP = "cmp";
    public static final String MOV = "mov";
    public static final String NOT = "not";
    public static final String POP = "pop";
    public static final String SHRD = "shrd";
    public static final String ADD = "add";
    public static final String AND = "and";
    public static final String MOVZX = "movzx";
    public static final String SAR = "sar";
    public static final String SHL = "shl";
    public static final String SHR = "shr";
    public static final String SUB = "sub";
    public static final String XOR = "xor";
    public static final String OR = "or";
    public static final String IMUL = "imul";
    public static final String LEA = "lea";

    public static final String CZ = "cz";
}
