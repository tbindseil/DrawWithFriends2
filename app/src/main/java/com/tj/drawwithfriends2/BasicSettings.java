package com.tj.drawwithfriends2;

/**
 * Created by TJ on 12/8/2018.
 */

public class BasicSettings extends Configurable {
    public BasicSettings(String tag) throws Exception {
        super(tag);
        settings.put("title", new Configuration("Masterpiece"));
    }

    public void setTitle(String title) {
        settings.get("title").setVal(title);
    }

    public String getTitle() {
       return settings.get("title").getVal();
    }
}
