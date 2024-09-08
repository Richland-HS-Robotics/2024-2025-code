package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.roadrunner.Vector2d;

public class MathFunctions {
    public static double sigmoid(double x){
        if(x == 0) {
            return 0;
        }
        double sign = x / Math.abs(x); // will be either +1 or -1
        return sign * (1.0 / (1 + Math.pow(Math.E, -(10*Math.abs(x) - 5))));
    }


    /**
     * Round a number to the nearest multiple of another number.
     * @param number The number to round
     * @param multiple The multiple to round to
     * @return The rounded number
     */
    public static double roundToMultiple(double number, int multiple){
        return multiple * Math.round(number / multiple);
    }



    public static double roundToMultipleInDirection(double number, int multiple, double direction){
        double half = multiple / 2.0;

        if(direction >= 0) {
            return Math.ceil((number + half)  / multiple)  * multiple;
        }else{
            return Math.floor((number + half) / multiple) * multiple;
        }
    }


    public static Vector2d rotateVector(Vector2d v, double angle){
        return new Vector2d(
                Math.cos(angle) * v.x - Math.sin(angle) * v.y,
                Math.sin(angle) * v.x + Math.cos(angle) * v.y
        );
    }
}
