package com.cz.assembler.vm_x86.vm86;

import java.util.concurrent.atomic.AtomicBoolean;

public class Machine {
    private static final String TAG = "Machine";

    public static final int STACK_SIZE_SMALL = 1024;
    public static final int STACK_SIZE_BIG = 2048;
    public static final int DATA_SIZE_SMALL = 1024;
    public static final int DATA_SIZE_BIG = 2048;
    public static final int MEMORY_SIZE_SMALL = 1024 * 16;
    public static final int MEMORY_SIZE_BIG = 1024 * 32;

    private static boolean mIsDebug = false;

    private Stack mStack;
    private Register.RegisterData[] mRegisterData;
    private Data mData;
    private Text mText;
    private Memory mMemory;

    private ILogOutput iLogOutput;
    private AtomicBoolean mIsRunning = new AtomicBoolean(false);

    private static Machine mInstance;

    private Machine(ILogOutput logOutput, int stackSize, int dataSize, int memorySize) {
        iLogOutput = logOutput;
        mMemory = new Memory(memorySize);
        mRegisterData = new Register.RegisterData[Register.VM86_REGISTER_MAXN];
        for (int i = 0; i != mRegisterData.length; i++) {
            mRegisterData[i] = new Register.RegisterData();
        }
        Register.clear(mRegisterData);
        Instruction.ip = mRegisterData[Register.VM86_REGISTER_EIP].getU32Pointer();
        mStack = new Stack(stackSize,
                mRegisterData[Register.VM86_REGISTER_ESP].getU32Pointer(),
                mRegisterData[Register.VM86_REGISTER_SS].getU32Pointer(),
                mRegisterData[Register.VM86_REGISTER_EBP].getU32Pointer(),
                mMemory.getMemoryPointer());
        mData = new Data(dataSize, mMemory.getMemoryPointer());
        mText = new Text(this);
    }

    public static void setDebugMode(boolean isDebug) {
        mIsDebug = isDebug;
    }

    public static boolean isDebugMode() {
        return mIsDebug;
    }

    public static Machine getInstance(ILogOutput logOutput, int stackSize, int dataSize, int memorySize) {
        if (mInstance == null) {
            mInstance = new Machine(logOutput, stackSize, dataSize, memorySize);
        }

        return mInstance;
    }

    public void stop() {
        mIsRunning.set(false);
    }

    public void release() {
        mInstance = null;
    }

    public boolean codeCompile(String code) {
        mIsRunning.set(true);
        boolean res = mText.compileText(code);
        if (!res) {
            logOutput("Compile error!");
            reset();
        }

        return res;
    }

    public boolean run() {
        boolean res = mText.getProc().run();
        reset();
        return res;
    }

    public boolean isRunning() {
        return mIsRunning.get();
    }

    public Text getText() {
        return mText;
    }

    public Data getData() {
        return mData;
    }

    public Stack getStack() {
        return mStack;
    }

    public Register.RegisterData[] getRegisterData() {
        return mRegisterData;
    }

    Memory getMemory() {
        return mMemory;
    }

    void logOutput(String log) {
        ALog.d(TAG, log);
        if (iLogOutput != null) {
            iLogOutput.logOutput(log + "\n");
        }
    }

    private void reset() {
        Register.clear(mRegisterData);
        mStack.clear();
        mData.clear();
    }
}
