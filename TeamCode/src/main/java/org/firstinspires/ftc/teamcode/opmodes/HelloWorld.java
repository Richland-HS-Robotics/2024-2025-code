package org.firstinspires.ftc.teamcode.opmodes;


import android.drm.DrmStore;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.HolonomicController;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.PoseVelocity2dDual;
import com.acmerobotics.roadrunner.Rotation2dDual;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic hello world teleOp.
 *
 */
@TeleOp
@Config
public class HelloWorld extends LinearOpMode {

   private FtcDashboard dash = FtcDashboard.getInstance();
   private List<Action> runningActions = new ArrayList<>();

//    private DcMotor leftFront;
//    private DcMotor leftBack;
//    private DcMotor rightFront;
//    private DcMotor rightBack;



    double sigmoid(double x){
        if(x == 0) {
            return 0;
        }
        double sign = x / Math.abs(x); // will be either +1 or -1

        return sign * (1.0 / (1 + Math.pow(Math.E, -(10*Math.abs(x) - 5))));
    }

    @Override
    public void runOpMode() throws InterruptedException {


        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));
//        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
//        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
//        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
//        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
//
//        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
//        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);


        waitForStart();

        if(isStopRequested()) {
            return;
        }

        while(opModeIsActive()){
            TelemetryPacket packet = new TelemetryPacket();

            drive.updatePoseEstimate();


            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;


            double newY = sigmoid(y);
            double newX = sigmoid(x);
            double newRx = sigmoid(rx);


            PoseVelocity2d input = new PoseVelocity2d(new Vector2d(newY,-newX),-newRx);
            if(gamepad1.dpad_up){
                input = snapToRotation(input, drive.pose, 0);
            }else if(gamepad1.dpad_right){
                input = snapToRotation(input, drive.pose, -Math.PI/2);
            }else if(gamepad1.dpad_down){
                input = snapToRotation(input, drive.pose, Math.PI);
            }else if(gamepad1.dpad_left){
                input = snapToRotation(input, drive.pose, Math.PI/2);
            }
//            if(gamepad1.right_bumper){
//                input = snapToRotation(input, drive.pose);
//            }


            drive.setDrivePowers( input );

            //drive.teleOp(y * 10,x * 10,rx);


//            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
//            double leftFrontPower = (y + x + rx) / denominator;
//            double leftBackPower = (y - x + rx) / denominator;
//            double rightFrontPower = (y -x - rx) / denominator;
//            double rightBackPower = (y + x - rx) / denominator;
//
//
//            leftFront.setPower(leftFrontPower);
//            rightFront.setPower(rightFrontPower);
//            leftBack.setPower(leftBackPower);
//            rightBack.setPower(rightBackPower);


            List<Action> newActions = new ArrayList<>();
            for(Action action: runningActions){
                action.preview(packet.fieldOverlay());
                if(action.run(packet)){
                    newActions.add(action);
                }
            }
            runningActions = newActions;

            dash.sendTelemetryPacket(packet);
        }


    }


    public static double P = 0.5;
    PoseVelocity2d snapToRotation(PoseVelocity2d input, Pose2d currentPose, double desiredDirection){
        Pose2d target = new Pose2d(currentPose.position, desiredDirection);

        double error = target.heading.toDouble() - currentPose.heading.toDouble();

        return new PoseVelocity2d(input.linearVel, Math.abs(input.angVel) * P * error);
    }
}
