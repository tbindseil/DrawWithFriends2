package com.tj.drawwithfriends2.Settings;

/**
 * Created by TJ on 12/8/2018.
 */

public class BasicSettings extends Configurable {
    private final String DEFAULT_TITLE = "Enter Title";

    private final ConfigurationString title;

    public BasicSettings() throws Exception {
        super();

        title = new ConfigurationString(DEFAULT_TITLE);
        settings.put("title", title);
    }

    public void setTitle(String title) {
        this.title.setString(title);
    }

    public String getTitle() {
       return title.getString();
    }
}
