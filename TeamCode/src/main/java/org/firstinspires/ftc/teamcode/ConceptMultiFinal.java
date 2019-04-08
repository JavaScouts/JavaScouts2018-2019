package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name = "1 New Multi-Thread Final Tele-op Concept")
public class ConceptMultiFinal extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    Servo hiccup;

    //VuforiaTracking tracking = new VuforiaTracking();

    double power = 0.4;
    double pos = 0;
    double multiplier = 0.92;
    boolean moved = false;
    boolean slid = false;
    String kys = "";


    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);
        hiccup = hardwareMap.get(Servo.class, "m");

        //not sure what this mess does but i don't trust myself to fix it
        robot.cup.setDirection(DcMotorSimple.Direction.REVERSE);

        moved = false;
        slid = false;
        kys = "screw mode";

        waitForStart();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (opModeIsActive()) {

                    if (gamepad1.left_bumper) {
                        multiplier = 0.92;
                    } else if (gamepad1.right_bumper) {
                        multiplier = 0.3;
                    }

                    if (gamepad1.right_stick_x == 0) {
                        robot.smartPower = 0;
                        //telemetry.addLine("Smart Power resetting due to release of joystick.");
                    }

                    robot.smartManualDrive(multiplier);
                    robot.moveRobot();

                    //use dpad to drive robot slowly
                    if (gamepad1.dpad_left) {

                        robot.backLDrive.setPower(power);
                        robot.leftDrive.setPower(-power);
                        robot.backRDrive.setPower(-power);
                        robot.rightDrive.setPower(power);

                    } else if (gamepad1.dpad_right) {

                        robot.backLDrive.setPower(-power);
                        robot.leftDrive.setPower(power);
                        robot.backRDrive.setPower(power);
                        robot.rightDrive.setPower(-power);

                    } else if (gamepad1.dpad_up) {

                        robot.backLDrive.setPower(power);
                        robot.leftDrive.setPower(power);
                        robot.backRDrive.setPower(power);
                        robot.rightDrive.setPower(power);

                    } else if (gamepad1.dpad_down) {

                        robot.backLDrive.setPower(-power);
                        robot.leftDrive.setPower(-power);
                        robot.backRDrive.setPower(-power);
                        robot.rightDrive.setPower(-power);

                    }

                }
            }
        }).start();

        while (opModeIsActive()) {

            if (gamepad2.left_bumper) {
                hiccup.setPosition(0);
            }

            if (gamepad2.right_bumper) {
                hiccup.setPosition(1.0);
            }

            if (gamepad2.x) {
                pos = 0.4;
                moved = true;
            }

            if (gamepad2.a) {
                pos = 0.9;
                moved = true;
            }

            if (gamepad2.b) {
                pos = 0;
                moved = true;
            }

            if (moved) {
                robot.ball.setPosition(pos);
            }

            if (gamepad2.left_trigger == 1) {
                slid = false;
            }
            if (gamepad2.right_trigger == 1) {
                slid = true;
            }

            double right = -gamepad2.right_stick_y;
            double left = -gamepad2.left_stick_y;
            //double re = -gamepad2.right_stick_x;

            robot.cup.setPower(left);
            robot.cup2.setPower(left);

            if (!slid) {
                robot.screw.setPower(right);
                robot.rev.setPower(0);
                kys = "screw mode";
            }
            if (slid) {
                robot.screw.setPower(0);
                robot.rev.setPower(-right);
                kys = "slider mode";
            }
            //rev.setPower(re);

            //add all data from sensors and encoders
            telemetry.addData("Current mode is", kys);
            telemetry.addData("Current speed multiplier is", multiplier);
            telemetry.addLine();
            telemetry.addLine();
            telemetry.addData("cup", robot.cup.getCurrentPosition());
            telemetry.addData("screw", robot.screw.getCurrentPosition());
            telemetry.addData("fl", robot.leftDrive.getCurrentPosition());
            telemetry.addData("fr", robot.rightDrive.getCurrentPosition());
            telemetry.addData("bl", robot.backLDrive.getCurrentPosition());
            telemetry.addData("br", robot.backRDrive.getCurrentPosition());
            telemetry.addData("range", robot.range.getDistance(DistanceUnit.INCH));

            telemetry.update();
        }

    }

}



