package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous
public class HelloAuto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d startPose = new Pose2d(0,0,0) ;

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);


        Action thing = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(48, 0), 0)
                .turn(Math.toRadians(-90))
                //.splineTo(new Vector2d(24, -24), 2*Math.PI)
                .splineTo(new Vector2d(0,0),2*Math.PI)
                .build();


        waitForStart();

        Actions.runBlocking(thing);
    }
}
