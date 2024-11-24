package org.firstinspires.ftc.teamcode.components

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class GoldArm(hardwareMap: HardwareMap): Arm(hardwareMap) {
    private lateinit var intake: DcMotor
//    private lateinit var intakeServo: CRServo
    private lateinit var slideMotor: DcMotor

    init{
        try{
            intake = hardwareMap.get(DcMotor::class.java, "intakeMotor")
            intake.direction = DcMotorSimple.Direction.REVERSE
//            intakeServo = hardwareMap.get(CRServo::class.java, "intakeServo")
            slideMotor = hardwareMap.get(DcMotor::class.java, "slideMotor")

        }catch (e: Exception){
            disabled = true
        }

        if(!disabled){
            slideMotor.direction = DcMotorSimple.Direction.REVERSE
        }
    }


    /**
     * Set the power of the intake, taking into account the rotation of the arm.
     * @param [power] The power with which to intake. Positive is a suck in,
     * negative is a spit out.
     */
    fun setIntakePower(power: Double){

        if (!disabled){
//            intakeServo.power = power
//            if(shoulder.currentPosition > angleToTicks(TOP_ANGLE)){
                intake.power = power
//            }else{
//                intake.power = power
//            }
        }
    }

    fun setSlidePower(power: Double){
        if(!disabled){
            slideMotor.power = power
        }
    }

}