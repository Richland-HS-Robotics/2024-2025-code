package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.RobotOpMode;

@Autonomous
public class HelloAuto extends RobotOpMode {
    Pose2d startPose = new Pose2d(0,0,0) ;

    private Action a;
    @Override
    public void runInit() {
        a = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(48, 0), 0)
                .turn(Math.toRadians(-90))
                //.splineTo(new Vector2d(24, -24), 2*Math.PI)
                .splineTo(new Vector2d(0,0),2*Math.PI)
                .build();
    }


    @Override
    public void runStart(){
        Actions.runBlocking(a);
    }


    @Override
    public void runLoop(TelemetryPacket packet) {


    }
}
