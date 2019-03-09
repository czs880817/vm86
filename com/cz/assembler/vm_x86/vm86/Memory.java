package com.cz.assembler.vm_x86.vm86;

import java.util.Arrays;

class Memory {
    private static final String TAG = "Memory";

    private byte[] mData;

    Memory(int size) {
        mData = new byte[size];
        byte b = 0;
        Arrays.fill(mData, b);
    }

    boolean set(int p, int value) {
        if (p >= mData.length || p < 0) {
            ALog.e(TAG, "#set: Out of memory: " + p);
            return false;
        }

        UtilsMachine.copyIntegerToBytes(mData, p, value);
        return true;
    }

    int get(int p) {
        if (p >= mData.length || p < 0) {
            ALog.e(TAG, "#get: Out of memory: " + p);
            return 0;
        }

        return UtilsMachine.getIntegerFromBytes(mData, p);
    }

    String getString(int p) {
        if (p >= mData.length || p < 0) {
            ALog.e(TAG, "#getString: Out of memory: " + p);
            return null;
        }

        return UtilsMachine.getStringFromBytes(mData, p);
    }

    byte[] getMemoryPointer() {
        return mData;
    }
}
