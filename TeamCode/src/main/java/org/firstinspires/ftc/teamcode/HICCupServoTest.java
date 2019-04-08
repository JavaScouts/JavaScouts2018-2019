package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "ZHICCupTest")
public class HICCupServoTest extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    Servo hiccup;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap, this);
        hiccup = hardwareMap.get(Servo.class, "m");
        waitForStart();


        while (opModeIsActive()) {
            robot.manualDrive(1.0);
            robot.moveRobot();

            if (gamepad2.a) {
                hiccup.setPosition(0);
            }
            if (gamepad2.b) {
                hiccup.setPosition(1.0);
            }

            robot.cup.setPower(gamepad2.left_stick_y);
        }
    }
}
