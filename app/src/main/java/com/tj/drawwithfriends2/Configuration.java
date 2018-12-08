package com.tj.drawwithfriends2;

import java.io.OutputStream;

/**
 * Created by TJ on 12/8/2018.
 */

// Note, I'm just storing things as strings, and
// its the responsibility of the owner to parse the strings
public class Configuration {
    final private String defaultVal;
    private String val;

    public Configuration(String val) throws Exception {
        if (val.isEmpty() || val.charAt(0) == ':') {
            throw new Exception("invalid val");
        }
        this.defaultVal = val;
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

    public void setVal(String val) {
        this.val = val;
    }

    public void returnToDefault() {
        this.val = defaultVal;
    }

    public String getVal() {
        return val;
    }
}
