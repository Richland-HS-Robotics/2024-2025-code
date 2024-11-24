package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.components.GoldArm
import org.firstinspires.ftc.teamcode.components.GreenArm
import org.firstinspires.ftc.teamcode.util.RobotOpMode




@TeleOp
class MainTeleOp: RobotOpMode() {
    override fun runInit() {
        // Nothing to do here

//        controller.setControlMode(Controller.ControlMode.ONE_DRIVER)
    }

    override fun runLoop(packet: TelemetryPacket) {
        drive.setDrivePowers(controller.movementControl()) // Drive


//        arm.setShoulderPowerManual(controller.manualShoulder()) // Move arm



        when(arm){
            is GoldArm -> arm.setShoulderPowerManual(controller.manualShoulder())
            is GreenArm -> arm.setShoulderPowerManual(controller.manualShoulderGreen())
        }
        //arm.setShoulderPowerManual(controller.manualShoulder())

        when (arm){
            is GoldArm -> (arm as GoldArm).setIntakePower(controller.intakeSpeed())
            is GreenArm -> (arm as GreenArm).setClawPosition(controller.greenClawOpenClose())
        }

        when(arm){
            is GoldArm -> (arm as GoldArm).setSlidePower(controller.manualSlidePower())
        }


        if(controller.resetArmEncoder()){
            arm.shoulder.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }

    }


    override fun runStart() {
        // Nothing to do here
    }

}