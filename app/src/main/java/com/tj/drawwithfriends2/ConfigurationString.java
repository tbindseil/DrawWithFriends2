package com.tj.drawwithfriends2;

/**
 * Created by TJ on 12/15/2018.
 */

public class ConfigurationString extends Configuration {
    private String val;
    private final String defalutVal;

    public ConfigurationString(String val) throws Exception {
        super();

        this.defalutVal = val;
        this.val = val;
    }

    public void setString(String newVal) {
        val = newVal;
    }

    @Override
    public void returnToDefault() {
        val = defalutVal;
    }

    @Override
    public String getString() {
        return val;
    }

    @Override
    public void fromString(String str) {
        try {
            val = str;
        } catch (Exception e) {
            val = defalutVal;
        }
    }
}
