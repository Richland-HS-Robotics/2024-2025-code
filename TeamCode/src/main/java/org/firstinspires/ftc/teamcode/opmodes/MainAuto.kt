package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.PoseVelocity2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.components.GoldArm
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose
import org.firstinspires.ftc.teamcode.util.RobotOpMode

@Autonomous
class MainAuto: RobotOpMode() {
    override fun runInit() {

    }

    override fun runLoop(packet: TelemetryPacket?) {
    }

    override fun runStart() {
        drive.setDrivePowers(PoseVelocity2d(Vector2d(0.3, 0.0), 0.0))

        sleep(1000)
        drive.setDrivePowers(PoseVelocity2d(Vector2d(0.0, 0.0), 0.0))
//        arm.shoulder.targetPosition = arm.angleToTicks(75.0)
//        arm.shoulder.mode = DcMotor.RunMode.RUN_TO_POSITION
//
//        arm.shoulder.power = 0.7

//        sleep(1000)
//
//        drive.setDrivePowers(PoseVelocity2d(Vector2d(0.0, -0.3), 0.0))
//        sleep(2000)
//        drive.setDrivePowers(PoseVelocity2d(Vector2d(0.0, 0.0), 0.0))
//
//        (arm as GoldArm).setIntakePower(1.0)
//
//        sleep(1000)
//
//        (arm as GoldArm).setIntakePower(0.0)




    }

}