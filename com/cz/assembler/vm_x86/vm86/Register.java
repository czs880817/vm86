package com.cz.assembler.vm_x86.vm86;

class Register {
    private static final String TAG = "Register";

    private static final String UNK = "unk";

    // registers
    static final int VM86_REGISTER_EAX = 0x00;
    static final int VM86_REGISTER_EBX = 0x01;
    static final int VM86_REGISTER_ECX = 0x02;
    static final int VM86_REGISTER_EDX = 0x03;

    static final int VM86_REGISTER_AL = 0x01 << 4;
    static final int VM86_REGISTER_BL = 0x05 << 4;
    static final int VM86_REGISTER_CL = 0x09 << 4;
    static final int VM86_REGISTER_DL = 0x0D << 4;

    static final int VM86_REGISTER_AH = 0x02 << 4;
    static final int VM86_REGISTER_BH = 0x06 << 4;
    static final int VM86_REGISTER_CH = 0x0A << 4;
    static final int VM86_REGISTER_DH = 0x0E << 4;

    static final int VM86_REGISTER_AX = 0x03 << 4;
    static final int VM86_REGISTER_BX = 0x07 << 4;
    static final int VM86_REGISTER_CX = 0x0B << 4;
    static final int VM86_REGISTER_DX = 0x0F << 4;

    static final int VM86_REGISTER_ESP = 0x04;
    static final int VM86_REGISTER_EBP = 0x05;
    static final int VM86_REGISTER_ESI = 0x06;
    static final int VM86_REGISTER_EDI = 0x07;

    static final int VM86_REGISTER_CS = 0x08;
    static final int VM86_REGISTER_DS = 0x09;
    static final int VM86_REGISTER_SS = 0x0A;
    static final int VM86_REGISTER_ES = 0x0B;
    static final int VM86_REGISTER_FS = 0x0C;
    static final int VM86_REGISTER_GS = 0x0D;

    static final int VM86_REGISTER_EIP = 0x0E;
    static final int VM86_REGISTER_EFLAGS = 0x0F;

    static final int VM86_REGISTER_MAXN = 16;
    static final int VM86_REGISTER_MASK = 0x0F;

    // flag register
    static final int VM86_REGISTER_EFLAG_CF = 0x01 << 0;
    static final int VM86_REGISTER_EFLAG_PF = 0x01 << 2;
    static final int VM86_REGISTER_EFLAG_AF = 0x01 << 4;
    static final int VM86_REGISTER_EFLAG_ZF = 0x01 << 6;
    static final int VM86_REGISTER_EFLAG_SF = 0x01 << 7;
    static final int VM86_REGISTER_EFLAG_OF = 0x01 << 11;

    static class RegisterData {
        private int[] u32;

        RegisterData() {
            u32 = new int[1];
        }

        int[] getU32Pointer() {
            return u32;
        }

        private void setU32(int value) {
            u32[0] = value;
        }

        private int getU32() {
            return u32[0];
        }

        private void setU16(int value) {
            u32[0] = (u32[0] & 0xFFFF0000) + (value & 0x0000FFFF);
        }

        private int getU16() {
            return u32[0] & 0x0000FFFF;
        }

        private void setU8H(int value) {
            u32[0] = (u32[0] & 0xFFFF00FF) + (value & 0x0000FF00);
        }

        private int getU8H() {
            return (u32[0] & 0x0000FF00) >> 8;
        }

        private void setU8L(int value) {
            u32[0] = (u32[0] & 0xFFFFFF00) + (value & 0x000000FF);
        }

        private int getU8L() {
            return u32[0] & 0x000000FF;
        }
    }

    static void clear(RegisterData[] registerData) {
        for (RegisterData data : registerData) {
            data.setU32(0);
        }
    }

    static int getValue(RegisterData[] registerData, int index) {
        int r = index & VM86_REGISTER_MASK;
        int value = 0;
        switch ((index >> 4) & 0x03) {
            case 0:
                value = registerData[r].getU32();
                break;
            case 1:
                value = registerData[r].getU8L();
                break;
            case 2:
                value = registerData[r].getU8H();
                break;
            case 3:
                value = registerData[r].getU16();
                break;
            default:
                ALog.e(TAG, "#getValue error!");
                break;
        }

        return value;
    }

    static void setValue(RegisterData[] registerData, int index, int value) {
        int r = index & VM86_REGISTER_MASK;
        switch ((index >> 4) & 0x03) {
            case 0:
                registerData[r].setU32(value);
                break;
            case 1:
                registerData[r].setU8L(value);
                break;
            case 2:
                registerData[r].setU8H(value);
                break;
            case 3:
                registerData[r].setU16(value);
                break;
            default:
                ALog.e(TAG, "#setValue error!");
                break;
        }
    }

    static String getName(int index) {
        String res;
        switch (index) {
            case VM86_REGISTER_EAX:
                res = Command.REGISTER_EAX;
                break;
            case VM86_REGISTER_EBX:
                res = Command.REGISTER_EBX;
                break;
            case VM86_REGISTER_ECX:
                res = Command.REGISTER_ECX;
                break;
            case VM86_REGISTER_EDX:
                res = Command.REGISTER_EDX;
                break;
            case VM86_REGISTER_ESP:
                res = Command.REGISTER_ESP;
                break;
            case VM86_REGISTER_EBP:
                res = Command.REGISTER_EBP;
                break;
            case VM86_REGISTER_ESI:
                res = Command.REGISTER_ESI;
                break;
            case VM86_REGISTER_EDI:
                res = Command.REGISTER_EDI;
                break;
            case VM86_REGISTER_EAX | VM86_REGISTER_AL:
                res = Command.REGISTER_AL;
                break;
            case VM86_REGISTER_EAX | VM86_REGISTER_AH:
                res = Command.REGISTER_AH;
                break;
            case VM86_REGISTER_EAX | VM86_REGISTER_AX:
                res = Command.REGISTER_AX;
                break;
            case VM86_REGISTER_EBX | VM86_REGISTER_BL:
                res = Command.REGISTER_BL;
                break;
            case VM86_REGISTER_EBX | VM86_REGISTER_BH:
                res = Command.REGISTER_BH;
                break;
            case VM86_REGISTER_EBX | VM86_REGISTER_BX:
                res = Command.REGISTER_BX;
                break;
            case VM86_REGISTER_ECX | VM86_REGISTER_CL:
                res = Command.REGISTER_CL;
                break;
            case VM86_REGISTER_ECX | VM86_REGISTER_CH:
                res = Command.REGISTER_CH;
                break;
            case VM86_REGISTER_ECX | VM86_REGISTER_CX:
                res = Command.REGISTER_CX;
                break;
            case VM86_REGISTER_EDX | VM86_REGISTER_DL:
                res = Command.REGISTER_DL;
                break;
            case VM86_REGISTER_EDX | VM86_REGISTER_DH:
                res = Command.REGISTER_DH;
                break;
            case VM86_REGISTER_EDX | VM86_REGISTER_DX:
                res = Command.REGISTER_DX;
                break;
            default:
                res = UNK;
                break;
        }

        return res;
    }

    static void registerLog(RegisterData[] registerData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i != registerData.length; i++) {
            String name = getName(i);
            if (name.equals(UNK)) {
                continue;
            }
            stringBuilder
                    .append(name)
                    .append(":")
                    .append(registerData[i].getU32())
                    .append(" ");
        }
        ALog.d(TAG, stringBuilder.toString());
    }
}
