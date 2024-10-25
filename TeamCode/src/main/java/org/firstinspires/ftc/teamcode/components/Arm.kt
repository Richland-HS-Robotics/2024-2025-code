package org.firstinspires.ftc.teamcode.components

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap

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
    }


    fun setShoulderPowerManual(power: Double){
        if (!this.disabled){
            shoulder.power = power
        }
    }

    fun setIntakeSpeed(speed: Double){
        // TODO reverse motor when arm is on the other side

        if (!this.disabled){
            intake.power = speed
        }
    }

}
