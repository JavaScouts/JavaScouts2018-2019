package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name = "Final TeleOp")
public class FinalTeleOp extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    boolean LastDetent;
    boolean Detent;
    double power = 0.2;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);

        robot.cup.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.cup.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.screw.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.screw.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        robot.cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.leftDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.rightDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.backRDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.backLDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        robot.backRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        robot.backLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        LastDetent = true;

        waitForStart();

        while (opModeIsActive()) {

            robot.manualDrive();
            robot.moveRobot();

            if (gamepad2.a) {
                robot.ball.setPosition(1.0);
            } else if (gamepad2.b) {
                robot.ball.setPosition(0);
            } else if (gamepad2.y){
                robot.ball.setPosition(0.9);
            }

            if(gamepad1.dpad_left) {

                robot.backLDrive.setPower(power);
                robot.leftDrive.setPower(-power);
                robot.backRDrive.setPower(-power);
                robot.rightDrive.setPower(power);

            } else if(gamepad1.dpad_right) {

                robot.backLDrive.setPower(-power);
                robot.leftDrive.setPower(power);
                robot.backRDrive.setPower(power);
                robot.rightDrive.setPower(-power);

            } else if(gamepad1.dpad_up) {

                robot.backLDrive.setPower(power);
                robot.leftDrive.setPower(power);
                robot.backRDrive.setPower(power);
                robot.rightDrive.setPower(power);

            } else if(gamepad1.dpad_down) {

                robot.backLDrive.setPower(-power);
                robot.leftDrive.setPower(-power);
                robot.backRDrive.setPower(-power);
                robot.rightDrive.setPower(-power);

            }


            Detent = (Math.abs(gamepad2.left_stick_y) <= 0.1);
            // check for hold or move
            if (Detent) {
                // we are in hold so use RTP
                robot.cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.cup.setPower(0.1);

                // Did we JUST let go of the stick?  if so, save location.
                if (!LastDetent)
                    robot.cup.setTargetPosition(robot.cup.getCurrentPosition());

            } else if (gamepad2.x) {
                robot.cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.cup.setPower(0.1);
                robot.cup.setTargetPosition(-138);

            } else {

                // we are in move so use RWE
                robot.cup.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                if (gamepad2.left_stick_y > 0)
                    robot.cup.setPower(gamepad2.left_stick_y * 0.4);
                else
                    robot.cup.setPower(gamepad2.left_stick_y * 0.5);
            }

            // remember last detent state for next time around.
            LastDetent = Detent;

            double right = -gamepad2.right_stick_y;
            robot.screw.setPower(right);
            telemetry.addData("cup", robot.cup.getCurrentPosition());
            telemetry.addData("screw", robot.screw.getCurrentPosition());
            telemetry.addData("fl", robot.leftDrive.getCurrentPosition());
            telemetry.addData("fr", robot.rightDrive.getCurrentPosition());
            telemetry.addData("bl", robot.backLDrive.getCurrentPosition());
            telemetry.addData("br", robot.backRDrive.getCurrentPosition());

            telemetry.update();
        }


    }

}



