package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.util.ElapsedTime


/**
 * A basic PID class.
 * @param [kP] the P term of the PID
 * @param [kI] the I term of the PID
 * @param [kD] the D term of the PID
 */
class PIDController(var kP: Double, var kI: Double, var kD: Double) {
    private var integralSum = 0.0
    private var lastError = 0.0
    var error: Double = 0.0
        private set

    private var timer = ElapsedTime()


    /**
     * Run the PID and get a result.
     * @param [currentPosition] the current position
     * @param [target] the target position
     * @return The power found with the PID algorithm
     */
    fun tick(currentPosition: Double, target: Double): Double{
        error = target - currentPosition

        val derivative = (error - lastError) / timer.seconds()
        integralSum += (error * timer.seconds())

        val out = (kP * error) + (kI * integralSum) + (kD * derivative)

        lastError = error
        timer.reset()

        return out
    }
}
