package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Liam on 4/8/2018.
 */

@TeleOp(name="Test")
public class Test extends LinearOpMode {

    LZRobot robot = new LZRobot();

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);

        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while(opModeIsActive()) {

            robot.manualDrive();
            robot.moveRobot();

            telemetry.addData(" Left Encoder Count", robot.leftDrive.getCurrentPosition());
            telemetry.addData(" Right Encoder Count", robot.rightDrive.getCurrentPosition());
            telemetry.addData(" BLeft Encoder Count", robot.backLDrive.getCurrentPosition());
            telemetry.addData(" BRight Encoder Count", robot.backRDrive.getCurrentPosition());
            telemetry.update();




        }

    }

}
