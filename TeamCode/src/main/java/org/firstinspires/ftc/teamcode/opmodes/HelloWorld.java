package org.firstinspires.ftc.teamcode.opmodes;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.MathFunctions;
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

        if(gamepad1.right_bumper) {
            input = snapToGrid(input, drive.pose);
        }

        drive.setDrivePowers(input);
    }


    public static double Prot = 0.5;
    PoseVelocity2d snapToRotation(PoseVelocity2d input, Pose2d currentPose, double desiredDirection){
        Pose2d target = new Pose2d(currentPose.position, desiredDirection);

        double error = target.heading.toDouble() - currentPose.heading.toDouble();

        return new PoseVelocity2d(input.linearVel, Math.abs(input.angVel) * Prot * error);
    }


    public static double Px = 0.1;
    public static double Py = 0.1;

    PoseVelocity2d snapToGrid(PoseVelocity2d input, Pose2d currentPose){
        Pose2d target = new Pose2d(MathFunctions.roundToMultipleInDirection(currentPose.position.x, 24, input.linearVel.x),
                MathFunctions.roundToMultipleInDirection(currentPose.position.y, 24, input.linearVel.y),
                currentPose.heading.toDouble());

       double xErr = target.position.x - currentPose.position.x;
       double yErr = target.position.y - currentPose.position.y;

       return new PoseVelocity2d(new Vector2d(
               Math.abs(input.linearVel.x) * Px * xErr,
               Math.abs(input.linearVel.y) * Py * yErr
       ),input.angVel );
    }


    @Override
    public void runStart(){

    }
}
