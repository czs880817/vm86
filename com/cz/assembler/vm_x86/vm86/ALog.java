package com.cz.assembler.vm_x86.vm86;

import android.util.Log;

public class ALog {
    public static void i(String tag, String log) {
        if (Machine.isDebugMode()) {
            Log.i(tag, log);
        }
    }

    public static void v(String tag, String log) {
        if (Machine.isDebugMode()) {
            Log.v(tag, log);
        }
    }

    public static void d(String tag, String log) {
        if (Machine.isDebugMode()) {
            Log.d(tag, log);
        }
    }

    public static void w(String tag, String log) {
        if (Machine.isDebugMode()) {
            Log.w(tag, log);
        }
    }

    public static void e(String tag, String log) {
        if (Machine.isDebugMode()) {
            Log.e(tag, log);
        }
    }
}
