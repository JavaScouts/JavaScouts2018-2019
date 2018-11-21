package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name = "Debug Wheels")
public class DebugWheels extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

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

            telemetry.addData("fl", robot.leftDrive.getCurrentPosition());
            telemetry.addData("fr", robot.rightDrive.getCurrentPosition());
            telemetry.addData("bl", robot.backLDrive.getCurrentPosition());
            telemetry.addData("br", robot.backRDrive.getCurrentPosition());

        }

    }

}

