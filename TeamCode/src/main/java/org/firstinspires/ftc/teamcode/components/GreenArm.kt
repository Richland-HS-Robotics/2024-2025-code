package org.firstinspires.ftc.teamcode.components

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

/**
 * The class for the green team arm. This inherits from the base arm.
 * @param [hardwareMap] the hardwareMap variable.
 */
class GreenArm(hardwareMap: HardwareMap): Arm(hardwareMap) {
    private lateinit var leftServo: Servo
    private lateinit var rightServo: Servo

    @Config
    companion object TestGreenTeamConstants{
        @JvmField var LEFT_SERVO_OPEN_POSITION: Double = 0.2
        @JvmField var LEFT_SERVO_CLOSED_POSITION: Double = 0.8
        @JvmField var RIGHT_SERVO_OPEN_POSITION: Double = 0.6
        @JvmField var RIGHT_SERVO_CLOSED_POSITION: Double = 0.15
    }


    init{
        try{
            leftServo = hardwareMap.get(Servo::class.java, "clawServoLeft")
            rightServo = hardwareMap.get(Servo::class.java, "clawServoRight")
        }catch (e: Exception){
            disabled = true
        }

        TICKS_PER_REVOLUTION = 288 * (80 / 40)
    }



    /**
     * Set the claw position.
     * @param [closed] Whether the claw should be open or closed. If true, close the claw;
     * if false, open it.
     */
    fun setClawPosition(closed: Boolean){
        if(!disabled){
            if(closed){
                leftServo.position = LEFT_SERVO_CLOSED_POSITION
                rightServo.position = RIGHT_SERVO_CLOSED_POSITION
            }else{
                leftServo.position = LEFT_SERVO_OPEN_POSITION
                rightServo.position = RIGHT_SERVO_OPEN_POSITION
            }
        }
    }
}