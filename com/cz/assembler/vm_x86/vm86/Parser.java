package com.cz.assembler.vm_x86.vm86;

import android.text.TextUtils;

class Parser {
    private static final String TAG = "Parser";

    static String getVariableName(String s) {
        int[] index = {0};
        return getVariableName(s, index);
    }

    static String getVariableName(String s, int[] index) {
        int b = index[0];
        while (index[0] < s.length() && (s.charAt(index[0]) == '_' || UtilsMachine.isAlpha(s.charAt(index[0])) || UtilsMachine.isDigit(s.charAt(index[0])))) {
            index[0]++;
        }

        String name = s.substring(b, index[0]);

        while (index[0] < s.length() && UtilsMachine.isSpace(s.charAt(index[0]))) {
            index[0]++;
        }

        return name;
    }

    static boolean getNumberValue(String s, int[] value) {
        int[] index = {0};
        return getNumberValue(s, index, value);
    }

    static boolean getValue(String s, int[] index, int[] value, Machine machine, Proc proc) {
        if (getNumberValue(s, index, value)) {
            return true;
        }

        if (getLocalValue(s, index, value, proc)) {
            return true;
        }

        return getOffsetValue(s, index, value, machine, proc);
    }

    static boolean getNumberValue(String s, int[] index, int[] value) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        int b = index[0];
        boolean isSigned = s.charAt(index[0]) == '-';
        int end = s.length();
        boolean isHex = false;
        for (int i = index[0]; i != s.length(); i++) {
            char c = s.charAt(i);
            if (UtilsMachine.isSpace(c)) {
                end = i;
                break;
            } else if (c == 'h') {
                isHex = true;
                end = i;
                break;
            }
        }
        String str = s.substring(index[0], end);
        str = isSigned ? str.substring(1) : str;
        if (TextUtils.isEmpty(str)) {
            index[0] = b;
            return false;
        }
        for (int i = 0; i != str.length(); i++) {
            if (!UtilsMachine.isDigit16(str.charAt(i))) {
                index[0] = b;
                return false;
            }
        }

        if (isHex) {
            value[0] = Integer.parseInt(str, 16);
        } else {
            value[0] = Integer.parseInt(str);
        }

        if (isSigned) {
            value[0] *= -1;
        }

        index[0] = end;

        return true;
    }

    static boolean getLocalValue(String s, int[] index, int[] value, Proc proc) {
        int b = index[0];
        String name = getVariableName(s, index);
        if (TextUtils.isEmpty(name)) {
            index[0] = b;
            return false;
        }

        Integer integer = proc.getLocals().get(name);
        if (integer == null) {
            index[0] = b;
            return false;
        }

        value[0] = integer;
        return true;
    }

    static boolean getOffsetValue(String s, int[] index, int[] value, Machine machine, Proc proc) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }

        int b = index[0];
        String segmentName = getSegmentName(s, index);

        if (index[0] >= s.length()) {
            index[0] = b;
            machine.logOutput("Can not get variable name!");
            return false;
        }

        while (UtilsMachine.isSpace(s.charAt(index[0]))) {
            index[0]++;
        }

        if (s.substring(index[0]).startsWith("short ")) {
            index[0] += 6;
        }

        String variableName = getVariableName(s, index);
        if (TextUtils.isEmpty(variableName)) {
            machine.logOutput("Variable name is null!");
            index[0] = b;
            return false;
        } else {
            ALog.d(TAG, "Variable name is " + variableName);
        }

        Integer integer;
        switch (segmentName) {
            case Command.SEGMENT_DS:
                value[0] = machine.getData().get(variableName, null);
                break;
            case Command.SEGMENT_CS:
                integer = proc.getLabels().get(variableName);
                if (integer == null) {
                    machine.logOutput("Can not get offset value: " + Command.SEGMENT_CS);
                    index[0] = b;
                    return false;
                } else {
                    value[0] = integer;
                }
                break;
            default:
                if (proc.getLabels().containsKey(variableName)) {
                    integer = proc.getLabels().get(variableName);
                    if (integer == null) {
                        machine.logOutput("Can not get offset value: " + variableName);
                        index[0] = b;
                        return false;
                    } else {
                        value[0] = integer;
                    }
                } else if (machine.getData().is(variableName)) {
                    value[0] = machine.getData().get(variableName, null);
                } else {
                    ALog.e(TAG, "Get offset value failed(" + variableName + ")");
                    index[0] = b;
                    return false;
                }
                break;
        }

        ALog.d(TAG, "Offset: " + variableName + " : " + value[0]);

        return true;
    }

    static String getSegmentName(String s, int[] index) {
        int i = s.indexOf(':');
        if (i < 0 || i <= index[0]) {
            return "";
        }

        String res = s.substring(index[0], i);
        index[0] = i + 1;
        return res;
    }

    static String getInstructionName(String s, int[] index) {
        String str = s.substring(index[0]);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i != str.length(); i++) {
            char c = str.charAt(i);
            index[0] = i;
            if (UtilsMachine.isAlpha(c)) {
                stringBuilder.append(c);
            } else if (UtilsMachine.isSpace(c)) {
                break;
            } else {
                return null;
            }
        }

        do {
            index[0]++;
        } while (index[0] < s.length() && UtilsMachine.isSpace(s.charAt(index[0])));

        return stringBuilder.toString();
    }

    static int getRegister(String s, int[] index) {
        int b = index[0];
        while (index[0] < s.length() && UtilsMachine.isAlpha(s.charAt(index[0]))) {
            index[0]++;
        }
        String name = s.substring(b, index[0]);

        if (TextUtils.isEmpty(name)) {
            index[0] = b;
            return -1;
        }

        while (index[0] < s.length() && UtilsMachine.isSpace(s.charAt(index[0]))) {
            index[0]++;
        }

        int res = -1;
        switch (name) {
            case Command.REGISTER_AH:
                res = Register.VM86_REGISTER_EAX | Register.VM86_REGISTER_AH;
                break;
            case Command.REGISTER_AL:
                res = Register.VM86_REGISTER_EAX | Register.VM86_REGISTER_AL;
                break;
            case Command.REGISTER_AX:
                res = Register.VM86_REGISTER_EAX | Register.VM86_REGISTER_AX;
                break;
            case Command.REGISTER_BH:
                res = Register.VM86_REGISTER_EBX | Register.VM86_REGISTER_BH;
                break;
            case Command.REGISTER_BL:
                res = Register.VM86_REGISTER_EBX | Register.VM86_REGISTER_BL;
                break;
            case Command.REGISTER_BX:
                res = Register.VM86_REGISTER_EBX | Register.VM86_REGISTER_BX;
                break;
            case Command.REGISTER_CH:
                res = Register.VM86_REGISTER_ECX | Register.VM86_REGISTER_CH;
                break;
            case Command.REGISTER_CL:
                res = Register.VM86_REGISTER_ECX | Register.VM86_REGISTER_CL;
                break;
            case Command.REGISTER_CX:
                res = Register.VM86_REGISTER_ECX | Register.VM86_REGISTER_CX;
                break;
            case Command.REGISTER_DH:
                res = Register.VM86_REGISTER_EDX | Register.VM86_REGISTER_DH;
                break;
            case Command.REGISTER_DL:
                res = Register.VM86_REGISTER_EDX | Register.VM86_REGISTER_DL;
                break;
            case Command.REGISTER_DX:
                res = Register.VM86_REGISTER_EDX | Register.VM86_REGISTER_DX;
                break;
            case Command.REGISTER_EAX:
                res = Register.VM86_REGISTER_EAX;
                break;
            case Command.REGISTER_EBP:
                res = Register.VM86_REGISTER_EBP;
                break;
            case Command.REGISTER_EBX:
                res = Register.VM86_REGISTER_EBX;
                break;
            case Command.REGISTER_ECX:
                res = Register.VM86_REGISTER_ECX;
                break;
            case Command.REGISTER_EDI:
                res = Register.VM86_REGISTER_EDI;
                break;
            case Command.REGISTER_EDX:
                res = Register.VM86_REGISTER_EDX;
                break;
            case Command.REGISTER_ESI:
                res = Register.VM86_REGISTER_ESI;
                break;
            case Command.REGISTER_ESP:
                res = Register.VM86_REGISTER_ESP;
                break;
        }

        if (res == -1) {
            index[0] = b;
        }
        ALog.d(TAG, "register: " + name + ":" + res);
        return res;
    }
}
