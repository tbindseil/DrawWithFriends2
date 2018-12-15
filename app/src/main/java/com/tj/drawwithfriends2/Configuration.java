package com.tj.drawwithfriends2;

import java.io.OutputStream;

/**
 * Created by TJ on 12/8/2018.
 */

// note: this base class keeps track of value as string for writing
// and reading settings
public abstract class Configuration {
    private String val;

    public Configuration(String val) throws Exception {
        if (val.isEmpty() || val.charAt(0) == ':') {
            throw new Exception("invalid val");
        }

        this.val = val;
    }

    public void write(String tag, OutputStream fileOutputStream) throws Exception {
        // val string can't contain colons at beginning, configurable is marked by ::
        String valStr = val.toString();
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

    public abstract String getString();
    public abstract void fromString(String str);
}
