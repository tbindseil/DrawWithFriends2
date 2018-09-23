package com.tj.drawwithfriends2.Input;
import android.graphics.Point;
/**
 * Created by TJ on 8/26/2018.
 */
public class HashPoint extends Point {
    public HashPoint(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HashPoint) {
            return this.x == ((HashPoint) o).x && this.y == ((HashPoint) o).y;
        }
        return false;
    }

    public int hashCode() {
        // note: I am masking the upper bits because that way
        // neighboring pixels do not collide
        return ((x & 0x0000ffff) << 16) | ((y & 0x0000ffff));
    }
}