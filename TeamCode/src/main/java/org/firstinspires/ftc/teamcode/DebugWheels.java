package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "Debug Wheels")
public class DebugWheels extends LinearOpMode {

    //sometimes, we have individual wheels break. this class can test each one individually.

    RobotHardware robot = new RobotHardware();
    double pos = 1.0;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);

        waitForStart();

        while (opModeIsActive()) {

            double left = -gamepad1.left_stick_y;
            double right = -gamepad1.right_stick_y;
            double left2 = -gamepad2.left_stick_y;
            double right2 = -gamepad2.right_stick_y;

            robot.leftDrive.setPower(left);
            robot.rightDrive.setPower(right);
            robot.backLDrive.setPower(left2);
            robot.backRDrive.setPower(right2);

            if(gamepad2.a) {
                pos = 0.37;
            }
            if(gamepad2.b) {
                pos = 0.0;
            }
            if(gamepad2.y) {
                pos = 0.5;
            }
            robot.ball.setPosition(pos);
            telemetry.addData("fl", robot.leftDrive.getCurrentPosition());
            telemetry.addData("fr", robot.rightDrive.getCurrentPosition());
            telemetry.addData("bl", robot.backLDrive.getCurrentPosition());
            telemetry.addData("br", robot.backRDrive.getCurrentPosition());
            telemetry.addData("servoPos", robot.ball.getPosition());
            telemetry.update();

        }

    }

}

