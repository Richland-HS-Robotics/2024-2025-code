package org.firstinspires.ftc.teamcode.components

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class GoldArm(hardwareMap: HardwareMap): Arm(hardwareMap) {
    private lateinit var intake: DcMotor

    init{
        try{
            intake = hardwareMap.get(DcMotor::class.java, "intakeMotor")
            intake.direction = DcMotorSimple.Direction.REVERSE
        }catch (e: Exception){
            disabled = true
        }
    }




    /**
     * Set the power of the intake, taking into account the rotation of the arm.
     * @param [power] The power with which to intake. Positive is a suck in,
     * negative is a spit out.
     */
    fun setIntakePower(power: Double){

        if (!disabled){
            if(shoulder.currentPosition > angleToTicks(TOP_ANGLE)){
                intake.power = -power
            }else{
                intake.power = power
            }
        }
    }

}