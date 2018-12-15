package com.tj.drawwithfriends2;

import java.io.OutputStream;

/**
 * Created by TJ on 12/8/2018.
 */

// note: this base class keeps track of value as string for writing
// and reading settings
public abstract class Configuration {
    public Configuration() throws Exception {
    }

    public void write(String tag, OutputStream fileOutputStream) throws Exception {
        String valStr = getString();
        if (valStr.isEmpty() || valStr.charAt(0) == ':') {
            throw new Exception("invalid string!");
        }

        String toWrite = tag + ":" + valStr;
        try {
            fileOutputStream.write(toWrite.getBytes());
            fileOutputStream.write(System.getProperty("line.seperator").getBytes());
        } catch (Exception e) {
            throw e;
        }
    }

    // anything involving the actual value is done by the base class
    public abstract void returnToDefault();
    public abstract String getString();
    public abstract void fromString(String str);
}
