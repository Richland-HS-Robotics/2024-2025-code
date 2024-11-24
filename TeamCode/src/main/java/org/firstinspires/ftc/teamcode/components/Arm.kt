package org.firstinspires.ftc.teamcode.components

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.acmerobotics.roadrunner.clamp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.MathFunctions.toRadians
import org.firstinspires.ftc.teamcode.util.PIDController
import kotlin.math.cos

open class Arm(hardwareMap: HardwareMap) {
    var disabled = false


    lateinit var shoulder: DcMotor

    init{
        try{
            shoulder = hardwareMap.get(DcMotor::class.java,"shoulderMotor")
        }catch (e: Exception){
            disabled = true
        }

        if (!disabled){
            shoulder.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            shoulder.direction = DcMotorSimple.Direction.FORWARD
            shoulder.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
    }

    @Config
    companion object ArmConfiguration{
        val KP = 0.1
        val KI = 0.0
        val KD = 0.0

        var TICKS_PER_REVOLUTION = 5580


        @JvmField var MIN_POWER: Double = 0.2
        @JvmField var TOP_ANGLE: Double = 100.0
    }

    /**
     * Convert a number of ticks from the motor to degrees
     * above the ground.
     *
     * @param [encoderTicks] the number of encoder ticks above the ground
     * @return The angle above the ground
     */
    private fun ticksToAngle(encoderTicks: Int): Double{
        // The final gear ratio is 180:1
        // So there are 5580 ticks per one revolution of the arm.
        // (28 counts per revolution at the motor)

        val rotations: Double = encoderTicks.toDouble()/TICKS_PER_REVOLUTION.toDouble()
        val degrees = rotations * 360.0

        return degrees
    }


    /**
     * Convert an angle to a number of encoder ticks on the motor.
     * @param [angle] the angle above the ground
     * @return The number of ticks above the ground
     */
    fun angleToTicks(angle: Double): Int{
        val rotations = angle / 360.0
        val ticks = (rotations * TICKS_PER_REVOLUTION).toInt()
        return ticks
    }


    /**
     * Rotate the arm to a specific angle above the ground.
     * @param [angle] The angle to rotate, in degrees
     * @return An Action that will rotate the arm.
     * You must tick this action for it to actually do anything.
     */
    fun rotateToAngle(angle: Double): Action{
        val ticks = angleToTicks(angle)
        return RotateAction(ticks.toDouble())
    }

    inner class RotateAction(var target: Double): Action{
        private var PID = PIDController(KP, KI, KD)
        private var threshold = 0.0

        override fun run(p: TelemetryPacket): Boolean {
            if (disabled) return false
            shoulder.power = PID.tick(shoulder.currentPosition.toDouble(),target)

            return !(PID.error < threshold)
        }
    }

    /**
     * Manually set the shoulder motor power.
     * @param [power] the power to set. Should be between -1.0 and 1.0.
     */
    fun setShoulderPowerManual(power: Double){
        if (!this.disabled){
            shoulder.mode = DcMotor.RunMode.RUN_USING_ENCODER
            shoulder.power = power
        }
    }


    /**
     * Set the shoulder power, but perform some augmentations.
     * @param [power]
     */
    fun setShoulderPowerAssisted(power: Double, packet: TelemetryPacket){
        if(disabled) return

        shoulder.mode = DcMotor.RunMode.RUN_USING_ENCODER

        val angleAboveFloor = ticksToAngle(shoulder.currentPosition)
        packet.put("Motor ticks", shoulder.currentPosition)
        packet.put("Arm Angle", angleAboveFloor)
        packet.put("Cosine of Angle", cos(toRadians(angleAboveFloor)))


        if(angleAboveFloor > TOP_ANGLE) {
            shoulder.power = -power
        }else{
            shoulder.power = power
        }


//        val cosAngle = cos((360.0 / (TOP_ANGLE * 4)) * toRadians(angleAboveFloor))
//        if(cosAngle > 0){
//            shoulder.power = power * clamp(cosAngle, MIN_POWER, 9999.0)
//        }else if(cosAngle < 0){
//            shoulder.power = power * clamp(cosAngle, -9999.0, -MIN_POWER)
//        }else{
//            shoulder.power = 0.1 * power
//        }
//
//        shoulder.power = power * (clamp(cos(0.72 *  toRadians(angleAboveFloor)), MIN_POWER, 9999.0))
        packet.put("Power", shoulder.power)

    }




}
