package com.tj.drawwithfriends2.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.IntBuffer;

/**
 * Created by TJ on 8/14/2018.
 */

public interface InputSaver {
    public void toOutputStream(DataOutputStream out);
    public void fromInputStream(DataInputStream in);
}
