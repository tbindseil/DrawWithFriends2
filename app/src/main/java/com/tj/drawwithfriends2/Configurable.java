package com.tj.drawwithfriends2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by TJ on 12/8/2018.
 */

// being an abstract class could screw me... guess I'll find out as I go
public abstract class Configurable {
    // todo, dirty flag
    // also todo, probably could use package encapsulation and java style protected
    private String tag;
    private Map<String, Configuration> settings;

    public Configurable(String tag) {
        this.tag = tag;
        settings = new HashMap<>();
        // extending classes need to populate the map,
        // upon constucting configurables, they will be
        // given a default value,
        // if the setting is found, its default value
        // will be overwritten, or it won't be found and
        // its default value will remain
    }

    public void write(OutputStream fileOutputStream) throws Exception {
        Iterator it = settings.keySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Configuration> currSetting = (Map.Entry<String, Configuration>)it.next();
            currSetting.getValue().write(fileOutputStream);
        }
    }

    // return the line read if we read a line with two consecutive colons, or
    // return empty string if we reach end of file
    public String init(InputStream fileInputStream) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
        String nextLine;
        while ((nextLine = br.readLine()) != null) {
            if (nextLine.contains("::")) {
                return nextLine;
            }

            String[] tokens = nextLine.split(":");
            Map.Entry<String, Configuration> curr = (Map.Entry<String, Configuration>)settings.get(tokens[0]);
            curr.getValue().setVal(tokens[1]);
            settings.put(tokens[0], new Configuration(tokens[0], tokens[1]));
        }

        return "";
    }

    public void returnToDefault() {
        Iterator it = settings.keySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Configuration> currSetting = (Map.Entry<String, Configuration>) it.next();
            currSetting.getValue().returnToDefault();
        }
    }
}
