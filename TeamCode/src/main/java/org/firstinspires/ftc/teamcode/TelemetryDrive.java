package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Liam on 4/8/2018.
 */

@TeleOp(name="DataDrive")
public class TelemetryDrive extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    DcMotor mineralarm;
    Servo mineralservo;
    @Override
    public void runOpMode() {

//        robot.init(hardwareMap, this);

        mineralarm = hardwareMap.dcMotor.get("m");
        mineralservo = hardwareMap.servo.get("ms");

        /*

        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);
*/

        waitForStart();

        while(opModeIsActive()) {
/*

            robot.manualDrive();
            robot.moveRobot();
*/

            mineralarm.setPower(gamepad2.right_stick_y * 0.15);

            telemetry.addLine();
            telemetry.addData("ServoPos", mineralservo.getPosition());
            telemetry.update();

        }

    }

}
