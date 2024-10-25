package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.util.MathFunctions.sigmoid;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.MathFunctions;
import org.firstinspires.ftc.teamcode.util.Pair;
import org.firstinspires.ftc.teamcode.util.Triple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



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




    private final Gamepad currentGamepad1;
    private final Gamepad currentGamepad2;
    private final Gamepad prevGamepad1;
    private final Gamepad prevGamepad2;

    private ElapsedTime timer;

    private ControlMode controlMode;


    /**
     * Whether gamepad1 is being supervised by gamepad 2
     */
    private boolean supervisedMode;

    /**
     * Whether the robot is being controlled by 1 or 2 drivers.
     */
    public enum ControlMode{
        ONE_DRIVER,
        TWO_DRIVERS
    }


    public Controller(){
        this.timer = new ElapsedTime();

        this.currentGamepad1 = new Gamepad();
        this.currentGamepad2 = new Gamepad();

        this.prevGamepad1 = new Gamepad();
        this.prevGamepad2 = new Gamepad();
    }


    public void setControlMode(ControlMode mode){
        this.controlMode = mode;
    }

   private boolean globalDrive = false;

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

        // if the a button was just released, toggle globalDrive
        if(prevGamepad1.a && !currentGamepad1.a){
            globalDrive = !globalDrive;
        }

    }


    @TwoDriverControl(
            gamepad = GamepadType.GAMEPAD_1,
            key = {
                    GamepadKey.LEFT_STICK_X,
                    GamepadKey.LEFT_STICK_Y,
                    GamepadKey.RIGHT_STICK_Y,
                    GamepadKey.RIGHT_TRIGGER
            },
            description = "drive the robot"
    )
    @OneDriverControl(
            key = {
                    GamepadKey.LEFT_STICK_X,
                    GamepadKey.LEFT_STICK_Y,
                    GamepadKey.RIGHT_STICK_Y,
                    GamepadKey.RIGHT_TRIGGER
            },
            description = "drive the robot"
    )
    public PoseVelocity2d movementControl(){
        // This should be a range of 1/3 to 1
        double multiplier = ((1.0 - currentGamepad1.right_trigger) * (2.0/3.0)) + (1.0/3.0);

        PoseVelocity2d input =  new PoseVelocity2d(
                new Vector2d(
                    sigmoid(-currentGamepad1.left_stick_y) * multiplier, // Forward-backward
                    sigmoid(-currentGamepad1.left_stick_x) * multiplier     // Left-right (positive x is left)
                ),
                sigmoid(-currentGamepad1.right_stick_x) * multiplier        // turn (positive movements counter-clockwise)
        );

        if(controlMode == ControlMode.TWO_DRIVERS && supervisedMode){
            if (currentGamepad2.left_bumper) {
                multiplier = ((1.0 - currentGamepad2.right_trigger) * (2.0/3.0)) + (1.0/3.0);
                input = new PoseVelocity2d(
                        new Vector2d(
                                sigmoid(-currentGamepad2.left_stick_y) * multiplier,
                                sigmoid(-currentGamepad2.left_stick_x) * multiplier
                        ),
                        sigmoid(-currentGamepad2.right_stick_x) * multiplier
                );
            }
        }


        return input;
    }


    @TwoDriverControl(gamepad = GamepadType.GAMEPAD_1, key = {GamepadKey.A}, description = "toggle field centric driving")
    @OneDriverControl(key = {GamepadKey.A}, description = "toggle field centric driving")
    public boolean globalDriveActivated(){
        return globalDrive;
    }


    public boolean outOfJailFree(){
        return currentGamepad1.left_bumper;
    }


    public boolean setBottomLeftCorner(){
        return currentGamepad1.x;
    }

    public boolean setTopRightCorner(){
        return currentGamepad1.b;
    }

    public double manualShoulder(){
        if(this.controlMode == ControlMode.TWO_DRIVERS) {
            return -currentGamepad2.right_stick_y;
        }else{
            return -currentGamepad1.right_stick_y;
        }
    }

    /**
     * Returns the speed that the intake should go at. A positive number sucks in,
     * a positive number spits out. This number may need to be reversed based on what
     * orientation the arm is in.
     * @return The speed the intake should move at, from 0 to 1.
     */
    public double intakeSpeed() {
        if(this.controlMode == ControlMode.TWO_DRIVERS){
            return twoTriggersToAnalog(currentGamepad2.left_trigger, currentGamepad2.right_trigger);
        }else{
            return twoTriggersToAnalog(currentGamepad1.left_trigger, currentGamepad1.right_trigger);
        }
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
}

