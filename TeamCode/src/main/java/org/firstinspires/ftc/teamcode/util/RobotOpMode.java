package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Controller;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.Arm;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that extends the main `LinearOpMode` class with
 * miscellaneous setup that happens for every opmode.
 */
public abstract class RobotOpMode extends LinearOpMode {
    protected final FtcDashboard dash = FtcDashboard.getInstance();
    protected List<Action> runningActions = new ArrayList<>();


    protected MecanumDrive drive;
    protected Controller controller;
    protected Arm arm;


    @Override
    public void runOpMode(){
        // Run initialization
        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        // TODO set initial location


        arm = new Arm(hardwareMap);
        controller = new Controller();

        runInit();

        waitForStart();

        if(isStopRequested()){
            return;
        }

        runStart();

        while(opModeIsActive()){
            TelemetryPacket packet = new TelemetryPacket();
            drive.updatePoseEstimate();
            controller.update(gamepad1, gamepad2);

            runLoop();

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

    /**
     * User code to run in the initialization phase.
     * This function is called once at the start of the opmode.
     * <p>
     * This could be stuff like setting up vision in auto.
     */
    public abstract void runInit();

    /**
     * User code to run in the main loop.
     * This function is called on every tick.
     */
    public abstract void runLoop();

    /**
     * User code to run just before the main loop starts.
     * This is perfect for autonomous.
     */
    public abstract void runStart();

    public void addAction(Action a){
        boolean replaced = false;

        Class<?> newClass = a.getClass();
        for(int i = 0; i < runningActions.size(); i++){
            if (runningActions.get(i).getClass() == newClass){
                runningActions.set(i, new SequentialAction(runningActions.get(i), a));
                replaced = true;
            }
        }

        if(!replaced){
            runningActions.add(a);
        }
    }


}
