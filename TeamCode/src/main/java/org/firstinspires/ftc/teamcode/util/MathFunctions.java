package org.firstinspires.ftc.teamcode.util;

public class MathFunctions {
    public static double sigmoid(double x){
        if(x == 0) {
            return 0;
        }
        double sign = x / Math.abs(x); // will be either +1 or -1
        return sign * (1.0 / (1 + Math.pow(Math.E, -(10*Math.abs(x) - 5))));
    }


}
