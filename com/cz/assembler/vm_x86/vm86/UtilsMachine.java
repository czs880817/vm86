package com.cz.assembler.vm_x86.vm86;

class UtilsMachine {
    static boolean isSpace(char c) {
        return c == 0x20 || c == 0x08 || c == 0x0E || c == 0x09;
    }

    static boolean isAlpha(char c) {
        return  c > 0x40 && c < 0x5B || c > 0x60 && c < 0x7B;
    }

    static boolean isDigit(char c) {
        return c > 0x2F && c < 0x3A;
    }

    static boolean isDigit16(char c) {
        return c > 0x2F && c < 0x3A || c > 0x40 && c < 0x47 || c > 0x60 && c < 0x67;
    }

    static String getStringFromBytes(byte[] bytes, int p) {
        int index = p;
        StringBuilder stringBuilder = new StringBuilder();
        while (bytes[index] != 0) {
            stringBuilder.append((char)bytes[index]);
            index++;
        }
        return stringBuilder.toString();
    }

    static void copyStringToBytes(byte[] bytes, int p, String s) {
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            bytes[p + i] = (byte)c;
        }
        bytes[p + s.length()] = 0;
    }

    static int getIntegerFromBytes(byte[] bytes, int p) {
        int data = 0;
        for (int i = p; i != p + 4; i++) {
            int shift = (3 - i + p) * 8;
            data += (bytes[i] & 0x000000FF) << shift;
        }

        return data;
    }

    static void copyIntegerToBytes(byte[] bytes, int p, int data) {
        bytes[p] = (byte)((data >> 24) & 0xFF);
        bytes[p + 1] = (byte)((data >> 16) & 0xFF);
        bytes[p + 2] = (byte)((data >> 8) & 0xFF);
        bytes[p + 3] = (byte)(data & 0xFF);
    }
}
