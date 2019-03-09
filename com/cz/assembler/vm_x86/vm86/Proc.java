package com.cz.assembler.vm_x86.vm86;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

class Proc {
    private static final String TAG = "Proc";

    private String mName;
    private Machine mMachine;
    private HashMap<String, Integer> mLabels;
    private HashMap<String, Integer> mLocals;
    private Instruction[] mInstructions;
    private int mInstructionsCount = 0;
    private String mLastDataName;
    private ArrayList<String> mInstructionStrings;

    Proc(Machine machine) {
        mMachine = machine;
        mLabels = new HashMap<>();
        mLocals = new HashMap<>();
        mInstructionStrings = new ArrayList<>();
    }

    String getName() {
        return mName;
    }

    HashMap<String, Integer> getLabels() {
        return mLabels;
    }

    HashMap<String, Integer> getLocals() {
        return mLocals;
    }

    int getInstructionsCount() {
        return mInstructionsCount;
    }

    boolean compile(String code) {
        mInstructionStrings = readInstructionStrings(code);
        int index = findName();
        if (index < 0 || TextUtils.isEmpty(mName)) {
            mMachine.logOutput("Can not find proc name.");
            return false;
        }

        if (index >= mInstructionStrings.size() - 1) {
            mMachine.logOutput("Can not get any instructions!");
            return false;
        }

        index++;
        index = prepare(index);
        mInstructions = new Instruction[mInstructionsCount];
        for (int i = 0; i != mInstructions.length; i++) {
            mInstructions[i] = new Instruction(mMachine.getRegisterData());
        }

        int count = compileDone(index);
        return count == mInstructionsCount;
    }

    boolean run() {
        mMachine.logOutput("===========================");
        mMachine.logOutput("Run proc: " + mName);
        mMachine.getStack().push(Stack.RETURN_ADDRESS);
        Instruction.ip[0] = 0;
        while (Instruction.ip[0] < mInstructionsCount) {
            if (!mMachine.isRunning()) {
                ALog.w(TAG, "Canceled running!");
                return false;
            }

            Instruction instruction = mInstructions[Instruction.ip[0]];
            ALog.d(TAG, "Run start: " + instruction.toString());
            Instruction.ip[0] = instruction.done.done(instruction, mMachine);
            if (Instruction.ip[0] == -1) {
                mMachine.logOutput("Run error: (class " + instruction.done.getClass().getSimpleName() + ")");
                return false;
            }
            Register.registerLog(mMachine.getRegisterData());
        }

        mMachine.logOutput("============================");
        mMachine.logOutput("Touch screen to continue...");

        return true;
    }

    private int findName() {
        for (int i = 0; i != mInstructionStrings.size(); i++) {
            String s = mInstructionStrings.get(i);
            int p = s.indexOf(Command.PROC);
            if (p >= 0) {
                int pos = p;
                p += 4;
                p = s.substring(p).trim().indexOf(Command.NEAR);
                if (p >= 0) {
                    int t = pos - 1;
                    while (t >= 0 && UtilsMachine.isSpace(s.charAt(t))) {
                        t--;
                    }
                    if (t < 0) {
                        return -1;
                    }
                    t++;
                    int h = t - 1;
                    while (h >= 0 && !UtilsMachine.isSpace(s.charAt(h))) {
                        h--;
                    }
                    h++;

                    mName = s.substring(h, t);
                    ALog.d(TAG, "Proc name is " + mName);
                    return i;
                }
            }
        }

        return -1;
    }

    private int prepare(int start) {
        ALog.i(TAG, "Prepare...");
        int count = 0;
        boolean isData = false;
        int res = start;
        for (int i = start; i != mInstructionStrings.size(); i++) {
            String instructionStr = mInstructionStrings.get(i);
            if (prepareLocals(instructionStr)) {
                continue;
            }

            if (res == start) {
                res = i;
            }

            if (instructionStr.equals(Command.SEGMENT_DATA)) {
                isData = true;
                continue;
            } else if (instructionStr.equals(Command.SEGMENT_CODE)) {
                isData = false;
                continue;
            }

            if (isData) {
                continue;
            }

            if (prepareLabels(instructionStr, count)) {
                ALog.d(TAG, "#prepare: label str is " + instructionStr);
                continue;
            }

            if (instructionStr.contains(Command.ENDP)) {
                break;
            }

            count++;
        }

        mInstructionsCount = count;
        ALog.i(TAG, "Prepare count: " + mInstructionsCount);
        return res;
    }

    private boolean prepareLocals(String str) {
        int index = str.indexOf("=");
        if (index < 0) {
            return false;
        }

        String name = Parser.getVariableName(str);
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        ALog.d(TAG, "#prepareLocals local name: " + name);
        String s = str.substring(index + 1).trim();
        String type;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            if (UtilsMachine.isSpace(c)) {
                break;
            } else {
                stringBuilder.append(c);
            }
        }
        type = stringBuilder.toString();

        if (!type.equals(Command.DWORD) && !type.equals(Command.WORD) && !type.equals(Command.BYTE) && !type.equals(Command.QWORD)) {
            return false;
        }

        s = s.substring(type.length()).trim();
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            if (c == '-' || UtilsMachine.isDigit16(c)) {
                s = s.substring(i).trim().toLowerCase();
                break;
            }
        }

        int[] value = {0};
        if (!Parser.getNumberValue(s, value)) {
            return false;
        }
        mLocals.put(name, value[0]);

        ALog.d(TAG, "local: " + name + ", type: " + type + ", value: " + value[0]);

        return true;
    }

    private boolean prepareLabels(String str, int offset) {
        int index = 0;
        for (int i = 0; i != str.length(); i++) {
            char c = str.charAt(i);
            if (UtilsMachine.isSpace(c) || c == ':') {
                break;
            }

            index = i;
        }

        String label = str.substring(0, index + 1).trim();
        if (TextUtils.isEmpty(label) || index + 1 == str.length() || str.charAt(index + 1) != ':') {
            return false;
        }

        ALog.d(TAG, "label: " + label + "->" + offset);
        mLabels.put(label, offset);
        return true;
    }

    private ArrayList<String> readInstructionStrings(String code) {
        ArrayList<String> res = new ArrayList<>();
        String[] strings = code.split("\n");
        for (String s : strings) {
            String str = s.trim().toLowerCase();
            if (!TextUtils.isEmpty(str)) {
                int index = str.indexOf(";");
                if (index > 0) {
                    String string = str.substring(0, index).trim();
                    if (!TextUtils.isEmpty(string)) {
                        res.add(string);
                    }
                } else if (index < 0) {
                    res.add(str);
                }
            }
        }

        return res;
    }

    private int compileDone(int index) {
        ALog.d(TAG, "Compile...");
        int count = 0;
        boolean isData = false;
        for (int i = index; i != mInstructionStrings.size(); i++) {
            String s = mInstructionStrings.get(i);
            if (s.startsWith(Command.SEGMENT_DATA)) {
                isData = true;
                continue;
            } else if (s.startsWith(Command.SEGMENT_CODE)) {
                isData = false;
                continue;
            }

            if (isData) {
                if (!compileData(s)) {
                    mMachine.logOutput("Compile data error: " + s);
                    break;
                }
            } else {
                if (s.trim().endsWith(":")) {
                    continue;
                } else if (s.contains(Command.ENDP)) {
                    break;
                }

                if (!compileCode(s, mInstructions[count])) {
                    mMachine.logOutput("Compile code error: " + s);
                    break;
                }

                count++;
            }
        }

        ALog.d(TAG, "Compile count: " + count);

        return count;
    }

    private boolean compileCode(String s, Instruction instruction) {
        int[] p = {0};
        instruction.detail = s;
        instruction.name = Parser.getInstructionName(s, p);
        if (TextUtils.isEmpty(instruction.name)) {
            mMachine.logOutput("Instruction name error: " + s);
            return false;
        }

        int[] value = {0};
        if (p[0] == s.length()) {
            // xxx?
            instruction.done = Gxxx.findInstructionDone(instruction.name);
        } else if (s.charAt(p[0]) == '[') {
            // xxx [ ... ], ... ?
            p[0]++;
            instruction.r0 = Parser.getRegister(s, p);
            if (instruction.r0 == -1) {
                mMachine.logOutput("Illegal register name(r0): " + s);
                return false;
            }

            char c = s.charAt(p[0]);
            if (c != ']') {
                // [r0 +- v0]?
                if (c != '+') {
                    mMachine.logOutput("Illegal operator(" + c + "): " + s);
                    return false;
                }

                p[0]++;
                while (p[0] < s.length() && UtilsMachine.isSpace(s.charAt(p[0]))) {
                    p[0]++;
                }

                if (Parser.getValue(s, p, value, mMachine, this)) {
                    instruction.v0 = value[0];
                } else {
                    mMachine.logOutput("Failed to get Value(v0): " + s);
                    return false;
                }
            }

            while (p[0] < s.length() && (UtilsMachine.isSpace(s.charAt(p[0])) || s.charAt(p[0]) == ',' || s.charAt(p[0]) == ']')) {
                p[0]++;
            }

            if (p[0] == s.length()) {
                // xxx [r0 + v0]?
                instruction.done = GxxxR0AddV0.findInstructionDone(instruction.name);
            } else {
                instruction.r1 = Parser.getRegister(s, p);
                if (instruction.r1 == -1) {
                    // xxx [r0 + v0], v1?
                    if (!Parser.getValue(s, p, value, mMachine, this)) {
                        mMachine.logOutput("Failed to get Value(v1): " + s);
                        return false;
                    }
                    instruction.v1 = value[0];
                    instruction.done = GxxxR0AddV0V1.findInstructionDone(instruction.name);
                } else {
                    // xxx [r0 + v0], r1?
                    instruction.done = GxxxR0AddV0R1.findInstructionDone(instruction.name);
                }
            }
        } else if ((instruction.r0 = Parser.getRegister(s, p)) != -1) {
            // xxx r0, ...?
            while (p[0] < s.length() && (s.charAt(p[0]) == ',' || UtilsMachine.isSpace(s.charAt(p[0])))) {
                p[0]++;
            }

            if (p[0] == s.length()) {
                // xxx r0?
                instruction.done = GxxxR0.findInstructionDone(instruction.name);
            } else if ((instruction.r1 = Parser.getRegister(s, p)) != -1) {
                // xxx r0, r1, ...?
                while (p[0] < s.length() && (s.charAt(p[0]) == ',' || UtilsMachine.isSpace(s.charAt(p[0])))) {
                    p[0]++;
                }

                if (p[0] == s.length()) {
                    // xxx r0, r1?
                    instruction.done = GxxxR0R1.findInstructionDone(instruction.name);
                } else if((instruction.r2 = Parser.getRegister(s, p)) != -1) {
                    // xxx r0, r1, r2?
                    instruction.done = GxxxR0R1R2.findInstructionDone(instruction.name);
                } else {
                    mMachine.logOutput("Compile error: " + s);
                    return false;
                }
            } else if (s.charAt(p[0]) == '[') {
                // xxx r0, [r1 + ...]?
                p[0]++;
                if ((instruction.r1 = Parser.getRegister(s, p)) == -1) {
                    mMachine.logOutput("Illegal register name(r1): " + s);
                    return false;
                }

                char c = s.charAt(p[0]);
                if (c != '+') {
                    mMachine.logOutput("Illegal operator(" + c + "): " + s);
                    return false;
                }

                p[0]++;
                while (p[0] < s.length() && UtilsMachine.isSpace(s.charAt(p[0]))) {
                    p[0]++;
                }

                if ((instruction.r2 = Parser.getRegister(s, p)) != -1) {
                    // xxx r0, [r1 + r2 op v0]?
                    instruction.op = s.charAt(p[0]);
                    if (instruction.op != '+' && instruction.op != '*') {
                        mMachine.logOutput("Illegal operator(" + instruction.op + "): " + s);
                        return false;
                    }

                    p[0]++;
                    while (p[0] < s.length() && UtilsMachine.isSpace(s.charAt(p[0]))) {
                        p[0]++;
                    }

                    if (!Parser.getValue(s, p, value, mMachine, this)) {
                        mMachine.logOutput("Failed to get Value(v0): " + s);
                        return false;
                    }

                    instruction.v0 = value[0];
                    instruction.done = GxxxR0R1AddR2OpV0.findInstructionDone(instruction.name);
                } else {
                    // xxx r0, [r1 + v0]?
                    if (!Parser.getValue(s, p, value, mMachine, this)) {
                        mMachine.logOutput("Failed to get Value(v0): " + s);
                        return false;
                    }

                    instruction.v0 = value[0];
                    instruction.done = GxxxR0R1AddV0.findInstructionDone(instruction.name);
                }
            } else if (s.substring(p[0]).startsWith(Command.OFFSET)) {
                // xxx r0, offset label?
                p[0] += Command.OFFSET.length();
                while (p[0] < s.length() && UtilsMachine.isSpace(s.charAt(p[0]))) {
                    p[0]++;
                }

                if (!Parser.getOffsetValue(s, p, value, mMachine, this)) {
                    mMachine.logOutput("Failed to get offset value");
                    return false;
                }

                instruction.v0 = value[0];
                instruction.done = GxxxR0V0.findInstructionDone(instruction.name);
            } else if (Parser.getNumberValue(s, p, value)) {
                // xxx r0, v0?
                instruction.v0 = value[0];
                instruction.done = GxxxR0V0.findInstructionDone(instruction.name);
            } else {
                mMachine.logOutput("Compile error: " + s);
                return false;
            }
        } else if (Parser.getValue(s, p, value, mMachine, this)) {
            // xxx v0...?
            instruction.v0 = value[0];
            if (p[0] < s.length() && s.charAt(p[0]) == '[' && s.endsWith("]")) {
                // xxx v0[r0 * v1]?
                p[0]++;
                if ((instruction.r0 = Parser.getRegister(s, p)) == -1) {
                    mMachine.logOutput("Illegal register name(r0): " + s);
                    return false;
                }

                if (s.charAt(p[0]) == '*') {
                    p[0]++;
                }

                while (p[0] < s.length() && UtilsMachine.isSpace(s.charAt(p[0]))) {
                    p[0]++;
                }

                if (!Parser.getValue(s.substring(0, s.length() - 1), p, value, mMachine, this)) {
                    mMachine.logOutput("Failed to get Value(v1): " + s);
                    return false;
                }

                instruction.v1 = value[0];
                instruction.done = GxxxV0R0MulV1.findInstructionDone(instruction.name);
            } else {
                // xxx v0?
                instruction.done = GxxxV0.findInstructionDone(instruction.name);
            }
        } else {
            instruction.functionName = Parser.getVariableName(s, p);
            if (TextUtils.isEmpty(instruction.functionName)) {
                mMachine.logOutput("Failed to get function name: " + instruction.functionName);
                return false;
            }

            instruction.done = GxxxFunc.findInstructionDone(instruction.name);
        }

        if (instruction.done == null) {
            mMachine.logOutput("Illegal instruction: " + instruction.name);
            return false;
        }

        return true;
    }

    private boolean compileData(String s) {
        Data data = mMachine.getData();
        boolean onlyData = false;
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            if (UtilsMachine.isSpace(c)) {
                index = i;
                break;
            }

            stringBuilder.append(c);
        }
        String name = stringBuilder.toString();
        if (name.equals(Command.DD) || name.equals(Command.DW) || name.equals(Command.DB)) {
            onlyData = true;
        }

        String str = onlyData ? s : s.substring(index).trim();
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        ArrayList<Object> objects = readData(str);
        if (objects.size() == 0) {
            mMachine.logOutput("Read data error: " + str);
            return false;
        }

        if (onlyData) {
            if (!data.add(mLastDataName, objects)) {
                mMachine.logOutput("Data add exception.");
                return false;
            }
        } else {
            if (!data.add(name, objects)) {
                mMachine.logOutput("Data add exception.");
                return false;
            }
            mLastDataName = name;
        }

        return true;
    }

    private ArrayList<Object> readData(String s) {
        ArrayList<Object> objects = new ArrayList<>();
        int index = 0;
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            if (UtilsMachine.isSpace(c)) {
                index = i;
                break;
            }
        }

        String type = s.substring(0, index);
        String dataStr = s.substring(index).trim();
        boolean ok = true;
        int[] p = {0};
        switch (type) {
            case Command.DB:
                boolean isStr = false;
                StringBuilder stringBuilder = new StringBuilder();
                while (p[0] < dataStr.length()) {
                    char c = dataStr.charAt(p[0]);
                    if (!isStr) {
                        if (c == '\'' || c == '\"') {
                            isStr = true;
                        } else if (UtilsMachine.isDigit16(c)) {
                            int[] value = {0};
                            if (Parser.getNumberValue(dataStr, p, value)) {
                                if (value[0] > 0xFF) {
                                    mMachine.logOutput("Byte overflow!");
                                    ok = false;
                                    break;
                                } else {
                                    objects.add(value[0]);
                                }
                            } else {
                                mMachine.logOutput("Parse db number error!");
                                ok = false;
                                break;
                            }
                        } else if (c == ';') {
                            break;
                        } else  if (c != ',' && !UtilsMachine.isSpace(c)) {
                            mMachine.logOutput("Invalid char: " + c);
                            ok = false;
                            break;
                        }
                    } else {
                        if (c == '\'' || c == '\"') {
                            objects.add(stringBuilder.toString());
                            isStr = false;
                        } else if (c > 0xFF) {
                            mMachine.logOutput("Invalid char: " + c);
                            ok = false;
                            break;
                        } else {
                            stringBuilder.append(c);
                        }
                    }

                    p[0]++;
                }

                if (!ok) {
                    objects.clear();
                }
                break;
            case Command.DD:
                if (dataStr.startsWith(Command.OFFSET)) {
                    p[0] += Command.OFFSET.length();
                    int[] value = {0};
                    if (Parser.getOffsetValue(dataStr, p, value, mMachine, this)) {
                        objects.add(value[0]);
                    } else {
                        mMachine.logOutput("Get offset error!");
                        ok = false;
                    }
                } else {
                    while (p[0] < dataStr.length()) {
                        char c = dataStr.charAt(p[0]);
                        if (UtilsMachine.isDigit16(c)) {
                            int[] value = {0};
                            if (!Parser.getNumberValue(dataStr, p, value)) {
                                objects.add(value[0]);
                            } else {
                                mMachine.logOutput("Parse dd number error!");
                                ok = false;
                                break;
                            }
                        } else if (c == ';') {
                            break;
                        } else if (c != ',' && !UtilsMachine.isSpace(c)) {
                            mMachine.logOutput("Invalid char: " + c);
                            ok = false;
                            break;
                        }
                    }
                }

                if (!ok) {
                    objects.clear();
                }
                break;
            case Command.DW:
                mMachine.logOutput("DW is not supported now.");
                break;
            default:
                mMachine.logOutput("Error Data type: " + type + "(" + s + ")");
                break;
        }

        return objects;
    }
}
