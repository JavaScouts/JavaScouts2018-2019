package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Liam on 4/8/2018.
 */
@Disabled
@TeleOp(name="DataDrive")
public class ZTelemetryDrive extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    DcMotor mineralarm;
    Servo mineralservo;
    @Override
    public void runOpMode() {

        mineralarm = hardwareMap.dcMotor.get("m");
        mineralservo = hardwareMap.servo.get("ms");

        waitForStart();

        while(opModeIsActive()) {

            mineralarm.setPower(gamepad2.right_stick_y * 0.15);

            telemetry.addLine();
            telemetry.addData("ServoPos", mineralservo.getPosition());
            telemetry.update();

        }

    }

}
