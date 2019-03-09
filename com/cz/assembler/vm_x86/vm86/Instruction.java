package com.cz.assembler.vm_x86.vm86;

import android.text.TextUtils;

class Instruction {
    static int[] ip;

    int r0 = -1;
    int r1 = -1;
    int r2 = -1;

    char op;
    String name;
    String detail;
    String functionName;

    int v0 = 0;
    int v1 = 0;

    InstructionDone done;

    private Register.RegisterData[] mRegisterData;

    Instruction(Register.RegisterData[] registerData) {
        mRegisterData = registerData;
    }

    static boolean doneJXXX(int flag, char h1, char h2) {
        boolean res = false;
        if (h1 == 'm') {
            res = true;
        } else if (h1 == 'z') {
            res = (flag & Register.VM86_REGISTER_EFLAG_ZF) != 0;
        } else if (h1 == 'n') {
            if (h2 == 'b') {
                res = (flag & Register.VM86_REGISTER_EFLAG_CF) == 0;
            } else if (h2 == 'z') {
                res = (flag & Register.VM86_REGISTER_EFLAG_ZF) == 0;
            }
        } else if (h1 == 'b') {
            if (h2 == 'e') {
                res = (flag & Register.VM86_REGISTER_EFLAG_ZF) != 0 || (flag & Register.VM86_REGISTER_EFLAG_CF) != 0;
            }
        } else if (h1 == 'a') {
            res = (flag & Register.VM86_REGISTER_EFLAG_ZF) == 0 || (flag & Register.VM86_REGISTER_EFLAG_CF) != 0;
        }

        return res;
    }

    static int doneCMP(int v0, int v1) {
        int flag = 0;
        if (v0 == v1) {
            flag |= Register.VM86_REGISTER_EFLAG_ZF;
        }
        if (v0 < v1) {
            flag |= Register.VM86_REGISTER_EFLAG_SF;
        }
        if (v0 < v1) {
            flag |= Register.VM86_REGISTER_EFLAG_CF;
        }

        return flag;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(detail)
                .append(", name is ")
                .append(name);
        if (!TextUtils.isEmpty(functionName)) {
            stringBuilder
                    .append(", function is ")
                    .append(functionName);
        }

        if (r0 != -1) {
            stringBuilder
                    .append(", r0 ")
                    .append(Register.getName(r0))
                    .append(" is ")
                    .append(Register.getValue(mRegisterData, r0));
        }

        if (r1 != -1) {
            stringBuilder
                    .append(", r1 ")
                    .append(Register.getName(r1))
                    .append(" is ")
                    .append(Register.getValue(mRegisterData, r1));
        }

        if (r2 != -1) {
            stringBuilder
                    .append(", r0 ")
                    .append(Register.getName(r2))
                    .append(" is ")
                    .append(Register.getValue(mRegisterData, r2));
        }

        stringBuilder
                .append(", v0 is ")
                .append(v0)
                .append(", v1 is ")
                .append(v1)
                .append(".");

        return stringBuilder.toString();
    }
}
