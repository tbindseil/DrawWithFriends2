package com.tj.drawwithfriends2.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

/**
 * Created by TJ on 8/14/2018.
 */

public interface InputSaver {
    void toOutputStream(DataOutputStream out);
    void fromInputStream(DataInputStream in) throws EOFException;
}
