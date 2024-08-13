package org.firstinspires.ftc.teamcode.util;

/**
 * Three values.
 * @param <X> The first value
 * @param <Y> The second value
 * @param <Z> The third value
 */
public class Triple<X,Y,Z> {
    public final X x;
    public final Y y;
    public final Z z;

    public Triple(X x, Y y, Z z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
