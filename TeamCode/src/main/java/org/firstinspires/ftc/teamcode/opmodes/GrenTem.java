// Epic robot :). Robot no work :(
package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class GrenTem extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        //DcMotor shoulderMotor = hardwareMap.dcMotor.get("shoulderMotor");
        Servo clawServoLeft = hardwareMap.servo.get("clawServoLeft");
        Servo clawServoRight = hardwareMap.servo.get("clawServoRight");

        //shoulderMotor.setTargetPosition(100);
        //shoulderMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        //shoulderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        //if (isStopRequested()) return;


        //shoulderMotor.setPower(0.7);

        //sleep(5000);
        //shoulderMotor.setPower(0);

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            if (gamepad1.right_trigger > 0.2) {
                if (clawServoLeft.getPosition() < 60){
                    clawServoLeft.setPosition(clawServoLeft.getPosition() + 10);
                }
                if (clawServoRight.getPosition() < 60){
                    clawServoRight.setPosition(clawServoRight.getPosition() + 10);
                }
            }
            if (gamepad1.left_trigger > 0.2) {
                if (clawServoLeft.getPosition() > 0) {
                    clawServoLeft.setPosition(clawServoLeft.getPosition() - 10);
                }
                if (clawServoRight.getPosition() > 0){
                    clawServoRight.setPosition(clawServoRight.getPosition() - 10);
                }
            }
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
        }
    }
}