package org.firstinspires.ftc.teamcode.opmodes;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.RobotOpMode;

/**
 * A basic hello world teleOp.
 *
 */
@TeleOp
@Config
public class HelloWorld extends RobotOpMode {
    @Override
    public void runInit() {

    }

    @Override
    public void runLoop() {
        PoseVelocity2d input = controller.movementControl();
        if(gamepad1.dpad_up){ input = snapToRotation(input, drive.pose, 0); }
        else if(gamepad1.dpad_right){ input = snapToRotation(input, drive.pose, -Math.PI/2); }
        else if(gamepad1.dpad_down){ input = snapToRotation(input, drive.pose, Math.PI); }
        else if(gamepad1.dpad_left){ input = snapToRotation(input, drive.pose, Math.PI/2); }

        drive.setDrivePowers(input);
    }


    public static double P = 0.5;
    PoseVelocity2d snapToRotation(PoseVelocity2d input, Pose2d currentPose, double desiredDirection){
        Pose2d target = new Pose2d(currentPose.position, desiredDirection);

        double error = target.heading.toDouble() - currentPose.heading.toDouble();

        return new PoseVelocity2d(input.linearVel, Math.abs(input.angVel) * P * error);
    }

    @Override
    public void runStart(){

    }
}
