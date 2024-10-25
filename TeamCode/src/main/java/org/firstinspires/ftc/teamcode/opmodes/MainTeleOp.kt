package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.util.RobotOpMode

@TeleOp
class MainTeleOp: RobotOpMode() {
    override fun runInit() {
        // Nothing to do here
    }

    override fun runLoop() {
        drive.setDrivePowers(controller.movementControl()) // Drive

        arm.setShoulderPowerManual(controller.manualShoulder()) // Move arm
        arm.setIntakeSpeed(controller.intakeSpeed()) // Set intake speed

    }


    override fun runStart() {
        // Nothing to do here
    }



}