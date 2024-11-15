package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.PoseVelocity2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.util.MathFunctions
import org.firstinspires.ftc.teamcode.util.RobotOpMode
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import kotlin.math.abs


/**
 * A basic hello world teleOp.
 */
@TeleOp
@Config
class HelloWorld : RobotOpMode() {
    override fun runInit() {
    }

    override fun runLoop(packet: TelemetryPacket) {
        var input = controller.movementControl()
        if (gamepad1.dpad_up) {
            input = snapToRotation(input, drive.pose, 0.0)
        } else if (gamepad1.dpad_right) {
            input = snapToRotation(input, drive.pose, -Math.PI / 2)
        } else if (gamepad1.dpad_down) {
            input = snapToRotation(input, drive.pose, Math.PI)
        } else if (gamepad1.dpad_left) {
            input = snapToRotation(input, drive.pose, Math.PI / 2)
        }

        if (gamepad1.right_bumper) { input = snapToGrid(input, drive.pose) }
        if (controller.globalDriveActivated()) { input = fieldCentric(input, drive.pose) }
        if(!controller.outOfJailFree()){ input = stayOnField(input, drive.pose) }

        if(controller.setTopRightCorner()){ rightTopCorner = drive.pose.position }
        if(controller.setBottomLeftCorner()){ leftBottomCorner = drive.pose.position}

        drive.setDrivePowers(input)
    }


    /**
     * Snap the robot to a specific direction, using a PID loop.
     * @param input The direction input, relative to the robot
     * @param currentPose The current pose
     * @param desiredDirection The direction to turn to
     * @return The new direction input
     */
    private fun snapToRotation(
        input: PoseVelocity2d,
        currentPose: Pose2d,
        desiredDirection: Double
    ): PoseVelocity2d {
        val target = Pose2d(currentPose.position, desiredDirection)

        val error = target.heading.toDouble() - currentPose.heading.toDouble()

        return PoseVelocity2d(input.linearVel, abs(input.angVel) * Prot * error)
    }


    /**
     * Snap the robot to the nearest tile, using a PID loop.
     * @param input The direction input, relative to the robot
     * @param currentPose The current pose
     * @return The new direction input
     */
    private fun snapToGrid(input: PoseVelocity2d, currentPose: Pose2d): PoseVelocity2d {
        val target = Pose2d(
            MathFunctions.roundToMultipleInDirection(currentPose.position.x, 24, input.linearVel.x),
            MathFunctions.roundToMultipleInDirection(currentPose.position.y, 24, input.linearVel.y),
            currentPose.heading.toDouble()
        )

        val xErr = target.position.x - currentPose.position.x
        val yErr = target.position.y - currentPose.position.y

        return PoseVelocity2d(
            Vector2d(
                abs(input.linearVel.x) * Px * xErr,
                abs(input.linearVel.y) * Py * yErr
            ), input.angVel
        )
    }


    /**
     * Convert to a field centric coordinate system
     * @param input The direction input, relative to the robot
     * @param currentPose The current pose
     * @return The new direction input relative to the field
     */
    private fun fieldCentric(input: PoseVelocity2d, currentPose: Pose2d): PoseVelocity2d {
        return PoseVelocity2d(
            MathFunctions.rotateVector(input.linearVel, currentPose.heading.toDouble()),
            input.angVel
        )
    }




    private fun stayOnField(input: PoseVelocity2d, currentPose: Pose2d): PoseVelocity2d {
        val negativeY = rightTopCorner.y
        val positiveY = leftBottomCorner.y
        val negativeX = leftBottomCorner.x
        val positiveX = rightTopCorner.x

        val currentInputGlobal =  MathFunctions.rotateVector(input.linearVel, currentPose.heading.toDouble())


        var vecToReturn = currentInputGlobal

        val wallY = if(abs(negativeY - currentPose.position.y) < abs(positiveY - currentPose.position.y)){
            negativeY
        }else{
            positiveY
        }
        val errY = wallY - currentPose.position.y
        val inputErrY = wallY - (currentPose.position.y + currentInputGlobal.y)

        if(abs(errY) < 10 && abs(inputErrY) < abs(errY)){ // We are headed towards the barrier
            vecToReturn = Vector2d(currentInputGlobal.x, 0.0)
        }

        val wallX = if(abs(negativeX - currentPose.position.x) < abs(positiveX - currentPose.position.x)){
            negativeX
        }else{
            positiveX
        }

        val errX = wallX - currentPose.position.x
        val inputErrX = wallX - (currentPose.position.x + currentInputGlobal.x)

        if(abs(errX) < 10 && abs(inputErrX) < abs(errX)){
            vecToReturn = Vector2d(0.0, vecToReturn.y)
        }

        return PoseVelocity2d(MathFunctions.rotateVector(vecToReturn, -currentPose.heading.toDouble()), input.angVel)
    }


    override fun runStart() {
    }

    companion object {
        var Prot: Double = 0.5

        var Px: Double = 0.1
        var Py: Double = 0.1

        var leftBottomCorner: Vector2d = Vector2d(0.0, 20.0)
        var rightTopCorner: Vector2d = Vector2d(50.0, -26.0)
    }
}
