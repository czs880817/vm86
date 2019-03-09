package com.cz.assembler.vm_x86.vm86;

class Text {
    private static final String TAG = "Text";

    private Proc mProc;
    private Machine mMachine;

    Text(Machine machine) {
        mMachine = machine;
        mProc = new Proc(mMachine);
    }

    boolean compileText(String code) {
        mMachine.logOutput("Compile start...");
        if (!mProc.compile(code)) {
            return false;
        }

        mMachine.logOutput("Compile end.");
        return true;
    }

    Proc getProc() {
        return mProc;
    }
}
