package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name="FinalTeleOp")
public class FinalTeleOp extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    DcMotor Cup;
    DcMotor Screw;
    Servo Ball;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);
        Cup = hardwareMap.dcMotor.get("Cup");
        Screw = hardwareMap.dcMotor.get("Screw");
        Ball = hardwareMap.servo.get("Ball");

        waitForStart();

        while(opModeIsActive()) {

            robot.manualDrive();
            robot.moveRobot();

            if (gamepad2.a){
                Ball.setPosition(1.0);
            } else if (gamepad2.b){
                Ball.setPosition(0);
            }

            if (-gamepad2.left_stick_y >= 0.1 || -gamepad2.left_stick_y <= -0.1) {
                Cup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                Cup.setPower(-gamepad2.left_stick_y * 0.5);
           // } else if (-gamepad2.left_stick_y <= -0.01){
                //Cup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                //Cup.setPower(-0.5);
            } else {
                Cup.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                Cup.setPower(-0.075);
            }
            //double left = -gamepad2.left_stick_y;
            double right = -gamepad2.right_stick_y;
           // Cup.setPower(left);
            Screw.setPower(right);
            telemetry.addData("Cup", Cup.getCurrentPosition());
            telemetry.update();


        }

    }

}
