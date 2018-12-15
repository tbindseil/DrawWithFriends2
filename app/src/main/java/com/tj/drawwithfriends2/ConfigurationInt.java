package com.tj.drawwithfriends2;

/**
 * Created by TJ on 12/15/2018.
 */

public class ConfigurationInt extends Configuration {
    private int val;
    private final int defalutVal;

    public ConfigurationInt(int val) throws Exception {
        super();

        this.defalutVal = val;
        this.val = val;
    }

    public int getInt() {
        return val;
    }

    public void setInt(int newVal) {
        val = newVal;
    }

    @Override
    public void returnToDefault() {
        val = defalutVal;
    }

    @Override
    public String getString() {
        return Integer.toString(val);
    }

    @Override
    public void fromString(String str) {
        try {
            val = Integer.parseInt(str);
        } catch (Exception e) {
            val = defalutVal;
        }
    }
}
