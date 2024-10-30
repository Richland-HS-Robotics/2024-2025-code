package org.firstinspires.ftc.teamcode.components

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.PIDController

@Config
class Arm(hardwareMap: HardwareMap) {
    private var disabled = false

    private lateinit var shoulder: DcMotor
    private lateinit var intake: DcMotor

    init{
        try{
            shoulder = hardwareMap.get(DcMotor::class.java,"shoulderMotor")
            intake = hardwareMap.get(DcMotor::class.java, "intakeMotor")
        }catch (e: Exception){
            disabled = true
        }

        if (!disabled){
            shoulder.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }
    }

    companion object{
        const val KP = 0.1
        const val KI = 0.0
        const val KD = 0.0


        private const val TICKS_PER_REVOLUTION = 5580
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


        val rotations = encoderTicks/TICKS_PER_REVOLUTION
        val degrees = rotations * 360.0

        return degrees
    }


    /**
     * Convert an angle to a number of encoder ticks on the motor.
     * @param [angle] the angle above the ground
     * @return The number of ticks above the ground
     */
    private fun angleToTicks(angle: Double): Int{
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

    fun setShoulderPowerManual(power: Double){
        if (!this.disabled){
            shoulder.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            shoulder.power = power
        }
    }

    fun setIntakeSpeed(speed: Double){
        // TODO reverse motor when arm is on the other side

        if (!this.disabled){
            intake.power = speed
        }
    }

    /**
     * Set the power of the intake, taking into account the rotation of the arm.
     * @param [power] The power with which to intake. Positive is a suck in,
     * negative is a spit out.
     */
    fun setIntakePower(power: Double){
        // TODO Update threshold angle
        if (!disabled){
            if(shoulder.currentPosition > angleToTicks(90.0)){
                intake.power = -power
            }else{
                intake.power = power
            }
        }
    }
}
