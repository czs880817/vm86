package com.cz.assembler.vm_x86.vm86;

class Stack {
    private static final String TAG = "Stack";

    static final int INVALID_VALUE = Integer.MIN_VALUE;
    static final int RETURN_ADDRESS = 0xBEAF;

    private byte[] mData;
    private int mSize;
    private int[] mTop;

    Stack(int size, int[] esp, int[] ss, int[] ebp, byte[] memoryPointer) {
        mData = memoryPointer;
        mSize = size;
        mTop = esp;
        mTop[0] = mData.length;
        ss[0] = esp[0] - 1;
        ebp[0] = mTop[0];
    }

    void clear() {
        for (int i = mData.length - mSize; i != mData.length; i++) {
            mData[i] = 0;
        }
        mTop[0] = mData.length;
    }

    int top() {
        if (mTop[0] == mData.length) {
            ALog.e(TAG, "Stack is null");
            return INVALID_VALUE;
        }

        return UtilsMachine.getIntegerFromBytes(mData, mTop[0]);
    }

    boolean push(int data) {
        if (mTop[0] == mData.length - mSize) {
            ALog.e(TAG, "Stack overflow!");
            return false;
        }

        ALog.i(TAG, "Push " + data + "(" + mTop[0] + ")");
        mTop[0] -= 4;
        UtilsMachine.copyIntegerToBytes(mData, mTop[0], data);
        return true;
    }

    int pop() {
        if (mTop[0] == mData.length) {
            ALog.e(TAG, "Stack is null");
            return INVALID_VALUE;
        }

        ALog.i(TAG, "Pop " + mTop[0]);
        int res = UtilsMachine.getIntegerFromBytes(mData, mTop[0]);
        mTop[0] += 4;
        return res;
    }
}
