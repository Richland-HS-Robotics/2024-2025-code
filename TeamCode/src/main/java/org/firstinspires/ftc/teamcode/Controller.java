package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Pair;
import org.firstinspires.ftc.teamcode.util.Triple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/*
 1 Gamepad
 */

/*
 2 Gamepads:
 - [X] XY: gamepad1 left_joystick
 - [X] Rotation: gamepad1 right_joystick-x
 - [X] Launch airplane: gamepad1 b
 - [X] field/robot mode swap: gamepad1 a (falling edge, toggle)
 - [ ] slow movement: gamepad1 right_trigger
 - [X] slide up/down: gamepad2 left-joystick-y
 - [X] claw up/down: gamepad2 right-joystick-y
 - [X] release pixel L: gamepad2 y (rising edge)
 - [X] release pixel R: gamepad2 b (rising edge)
 - [ ] Grab pixel: gamepad2 right-trigger
 */


/**
 * A class for managing the gamepads, both 1 and 2 gamepads.
 */
public class Controller {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface OneDriverControl{
        public GamepadType gamepad() default GamepadType.GAMEPAD_1;
        public GamepadKey[] key();
        public String description();
    };


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface TwoDriverControl{
        public GamepadType gamepad();
        public GamepadKey[] key();
        public String description();
    }


    public class Keybinding{
        public String description;
        public GamepadType gamepad;
        public GamepadKey key;
    }



    public enum GamepadType {
        GAMEPAD_1,
        GAMEPAD_2
    }

    public enum GamepadKey{
        A,
        B,
        X,
        Y,
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        LEFT_BUMPER,
        LEFT_TRIGGER,
        RIGHT_BUMPER,
        RIGHT_TRIGGER,
        LEFT_STICK_X,
        LEFT_STICK_Y,
        RIGHT_STICK_X,
        RIGHT_STICK_Y,
        LEFT_STICK_BUTTON,
        RIGHT_STICK_BUTTON
    }




    private Gamepad currentGamepad1;
    private Gamepad currentGamepad2;
    private Gamepad prevGamepad1;
    private Gamepad prevGamepad2;

    private ElapsedTime timer;

    private Telemetry telemetry;

    private ControlMode controlMode;

    public enum ControlMode{
        ONE_DRIVER,
        TWO_DRIVERS
    }
    public Controller(Telemetry telemetry){
        this.timer = new ElapsedTime();

        this.currentGamepad1 = new Gamepad();
        this.currentGamepad2 = new Gamepad();

        this.prevGamepad1 = new Gamepad();
        this.prevGamepad2 = new Gamepad();

        this.telemetry = telemetry;

    }


    public void setControlMode(ControlMode mode){
        this.controlMode = mode;
    }

    /**
     * Update the state. This must be called every tick.
     * @param gamepad1 Gamepad 1.
     * @param gamepad2 Gamepad 2
     */
    public void update(Gamepad gamepad1, Gamepad gamepad2){
        boolean g1Exists = gamepad1 != null;
        boolean g2Exists = gamepad2 != null;

        if (g1Exists){
            this.prevGamepad1.copy(this.currentGamepad1);
            this.currentGamepad1.copy(gamepad1);
        }

        if (g2Exists){
            this.prevGamepad2.copy(this.currentGamepad2);
            this.currentGamepad2.copy(gamepad2);
        }

        if (g1Exists && g2Exists){
            this.controlMode = ControlMode.TWO_DRIVERS;
        }
        else if(g1Exists){
            this.controlMode = ControlMode.ONE_DRIVER;
        }

    }


    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = {GamepadKey.LEFT_STICK_X,GamepadKey.LEFT_STICK_Y,GamepadKey.RIGHT_STICK_Y},description = "drive the robot")
    @OneDriverControl(key = {GamepadKey.LEFT_STICK_X,GamepadKey.LEFT_STICK_Y,GamepadKey.RIGHT_STICK_Y},description = "drive the robot")
    public PoseVelocity2d movementControl(){
//        return new Triple<>(
//                (double) currentGamepad1.left_stick_x,
//                (double) -currentGamepad1.left_stick_y,
//                (double) currentGamepad1.right_stick_x
//        );

        return new PoseVelocity2d(
                new Vector2d(
                    -currentGamepad1.left_stick_y, // Forward-backward
                    -currentGamepad1.left_stick_x // Left-right (positive x is left)
                ),
                -currentGamepad1.right_stick_x // turn (positive movements counter-clockwise)
        );
    }


    /**
     * Returns the direction the linear slide should go.
     * For 2 drivers it uses the second gamepad left stick y,
     * and for 1 driver it uses the right stick y. (same stick as rotation)
     *
     * @return Direction of linear slide, from -1 to 1
     */
    @OneDriverControl(key = GamepadKey.RIGHT_STICK_Y,description = "move the linear slide up and down")
    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_2, key = GamepadKey.RIGHT_STICK_Y, description = "move the linear slide up and down")
    public double linearSlideInOut(){
        if(controlMode == ControlMode.ONE_DRIVER) {
            return -currentGamepad1.right_stick_y;
        }else if(controlMode == ControlMode.TWO_DRIVERS){
            return -currentGamepad2.right_stick_y;
        }else{
            return 0;
        }
    }

    /**
     * Returns whether the airplane should be launched
     * @return
     */
    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = GamepadKey.B, description = "Launch the airplane")
    @OneDriverControl(key = GamepadKey.B,description = "Launch the airplane")
    public boolean launchAirplane(){
        return currentGamepad1.b && currentGamepad1.left_bumper;
    }


    /**
     * Returns whether either pixel should be released.
     * @return A pair of booleans. The first is left and the second is right.
     */
    @Deprecated
    public Pair<Boolean, Boolean> releasePixels(){
        if(controlMode == ControlMode.TWO_DRIVERS){
            return new Pair<>(currentGamepad2.y,currentGamepad2.x);
        }else{
            return new Pair<>(currentGamepad1.b,currentGamepad1.a);
        }
    }


    public boolean overridePressed(){
        return currentGamepad1.left_bumper;
    }



    /**
     * Converts 2 button inputs (like the dpad) to a double input.
     * @param b1 The first button. When this is pressed the output will be high.
     * @param b2 The second button. When this is pressed the output will be low.
     * @return An analog output based on both buttons.
     */
    private double twoButtonsToAnalog(boolean b1,boolean b2){
        if (b1 && !b2){
            return 1;
        }
        if(b2 && !b1){
            return -1;
        }
        if(!b1 && !b2){
            return 0;
        }
        if(b1 && b2){
            return 0;
        }
        return 0;
    }


    /**
     * Converts 2 trigger inputs to single double input.
     * @param t1 The first trigger. When this is pressed the output will be low.
     * @param t2 The second trigger. When this is pressed the output will be high.
     * @return The double value to send to the motor
     */
    private double twoTriggersToAnalog(float t1, float t2){
        return t2-t1;
    }



    public double movementSlownessFactor(){
        //return 1 - currentGamepad1.right_trigger;
        return 1;
    }


    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_2, key = GamepadKey.RIGHT_STICK_Y, description = "move the linear slide up or down")
    @OneDriverControl(key = {GamepadKey.DPAD_UP,GamepadKey.DPAD_DOWN}, description = "move the linear slide up and down")
    public double intakeUpDown(){
        if(controlMode == ControlMode.TWO_DRIVERS) {
            telemetry.addData("Two drivers: ",-currentGamepad2.right_stick_y);
            return -currentGamepad2.right_stick_y;
        }else{
            telemetry.addData("One driver: ",twoButtonsToAnalog(currentGamepad1.dpad_up,currentGamepad1.dpad_down));
            return twoButtonsToAnalog(currentGamepad1.dpad_up,currentGamepad1.dpad_down);
        }
    }


    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_2, key = GamepadKey.RIGHT_TRIGGER, description = "close the grabbing claw")
    @OneDriverControl(key = GamepadKey.LEFT_TRIGGER, description = "close the grabbing claw")
    public boolean clawGrab(){
        if(controlMode == ControlMode.TWO_DRIVERS){
            return currentGamepad2.right_trigger > 0.5;

        }else{
            return currentGamepad1.left_trigger > 0.5;
        }
    }


    /**
     * The controls for running the hanging winch
     * @return the power to send to the winch motor
     */
    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = {GamepadKey.DPAD_UP,GamepadKey.DPAD_DOWN}, description = "power the hang winch")
    public double hangWinch(){
        if(controlMode == ControlMode.TWO_DRIVERS) {
            return twoButtonsToAnalog(currentGamepad1.dpad_up,currentGamepad1.dpad_down);
        }else{
            return 0;
        }
    }


    /**
     * The controls for running the hanging arm.
     * @return the power to send to the hang arm motor.
     */
    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = {GamepadKey.RIGHT_STICK_Y}, description = "move the hang arm")
    @OneDriverControl(key = {GamepadKey.LEFT_TRIGGER,GamepadKey.RIGHT_TRIGGER}, description = "move the hang arm")
    public double hangArm(){

        if(controlMode == ControlMode.TWO_DRIVERS){
            return -currentGamepad1.right_stick_y;
        }
        return twoTriggersToAnalog(currentGamepad1.left_trigger,currentGamepad1.right_trigger);
    }

    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = {GamepadKey.RIGHT_TRIGGER}, description = "Set the movement speed")
    public double movementSpeed(){
        if(controlMode == ControlMode.TWO_DRIVERS){
            // Range of 0.25 to 1
            if(currentGamepad1.right_bumper){
                return 0;
            }
            return 0.25 + ((-currentGamepad1.right_trigger + 1) / 1.3333333);
        }
        return 1;
    }


    /**
     * The controls for setting the linear slide sub arm.
     * @return Whether or not the arm should be up.
     */
    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_2, key = GamepadKey.LEFT_STICK_Y, description = "move the linear slide sub arm up or down")
    public boolean slideSubArm(){
        if(controlMode==ControlMode.TWO_DRIVERS){
            return -currentGamepad2.left_stick_y > 0.5;
        }
        return false;
    }



    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = GamepadKey.X, description = "switch robot to robot centric")
    @OneDriverControl(key = GamepadKey.X, description = "switch robot to robot centric")
    public boolean robotCentric(){
        return currentGamepad1.x;
    }

    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = GamepadKey.Y, description = "switch robot to field centric")
    @OneDriverControl(key = GamepadKey.Y, description = "switch robot to field centric")
    public boolean fieldCentric(){
        return currentGamepad1.y;
    }


    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = {GamepadKey.LEFT_BUMPER,GamepadKey.RIGHT_BUMPER}, description = "snap to a position on the field")
    @OneDriverControl(key = {GamepadKey.LEFT_BUMPER,GamepadKey.RIGHT_BUMPER}, description = "snap to a position on the field")
    public boolean snapToPosition(){
        return currentGamepad1.left_bumper && currentGamepad1.right_bumper;
    }

}

