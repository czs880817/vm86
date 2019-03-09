package com.cz.assembler.vm_x86.vm86;

import java.util.ArrayList;
import java.util.HashMap;

class Data {
    private static final String TAG = "Data";

    private byte[] mData;
    private int mSize;
    private int mBase;
    private HashMap<String, DataChunk> mLabels;

    Data(int size, byte[] memoryPointer) {
        mData = memoryPointer;
        mLabels = new HashMap<>();
        mSize = size;
        mBase = 0;
    }

    boolean is(String name) {
        return mLabels.containsKey(name);
    }

    void clear() {
        for (int i = 0; i != mSize; i++) {
            mData[i] = 0;
        }
        mBase = 0;
        mLabels.clear();
    }

    int get(String name, int[] pSize) {
        DataChunk dataChunk = mLabels.get(name);
        if (dataChunk == null) {
            throw new RuntimeException();
        }

        if (pSize != null) {
            pSize[0] = dataChunk.size;
        }

        return dataChunk.offset;
    }

    boolean add(String name, ArrayList<Object> objects) {
        DataChunk dataChunk = mLabels.get(name);
        int size = 0;
        for (Object object : objects) {
            if (object instanceof Integer) {
                size += 4;
            } else if (object instanceof String) {
                size += ((String)object).length();
            } else {
                ALog.e(TAG, "Illegal data!");
                return false;
            }
        }

        if (dataChunk == null) {
            if (size + mBase > mSize) {
                ALog.e(TAG, "Data overflow");
                return false;
            }

            DataChunk chunk = new DataChunk();
            chunk.offset = mBase;
            chunk.size = size;
            mLabels.put(name, chunk);
            addDataToMemory(objects, mBase);
            mBase += size;
        } else {
            if (dataChunk.offset + dataChunk.size != mBase) {
                ALog.e(TAG, "Must be the last chunk!");
                return false;
            }

            if (size + mBase > mSize) {
                ALog.e(TAG, "Data overflow");
                return false;
            }

            addDataToMemory(objects, mBase);
            mBase += size;
            dataChunk.size += objects.size();
        }

        return true;
    }

    private void addDataToMemory(ArrayList<Object> objects, int p) {
        int index = p;
        for (Object object : objects) {
            if (object instanceof Integer) {
                UtilsMachine.copyIntegerToBytes(mData, index, (Integer)object);
                index += 4;
            } else if (object instanceof String) {
                String s = (String)object;
                UtilsMachine.copyStringToBytes(mData, index, s);
                index += (s.length() + 1);
            }
        }
    }

    static class DataChunk {
        int offset;
        int size;
    }
}
