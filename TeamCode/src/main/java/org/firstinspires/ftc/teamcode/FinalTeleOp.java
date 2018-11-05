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
    boolean LastDetent;
    boolean Detent;


    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);
        Cup = hardwareMap.dcMotor.get("Cup");
        Screw = hardwareMap.dcMotor.get("Screw");
        Ball = hardwareMap.servo.get("Ball");

        Cup.setDirection(DcMotorSimple.Direction.REVERSE);
        Cup.setMode(DcMotor.RunMode.RESET_ENCODERS);
        Cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LastDetent = true;



        waitForStart();



        while(opModeIsActive()) {

            robot.manualDrive();
            robot.moveRobot();

            if (gamepad2.a) {
                Ball.setPosition(0.5);
            } else if (gamepad2.b) {
                Ball.setPosition(0.8);
            }

            Detent = (Math.abs(gamepad2.left_stick_y) <= 0.1);
            // check for hold or move
            if (Detent) {
                // we are in hold so use RTP
                Cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                Cup.setPower(0.1);

                // Did we JUST let go of the stick?  if so, save location.
                if (!LastDetent)
                    Cup.setTargetPosition(Cup.getCurrentPosition());

            } else if (gamepad2.x) {
                Cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                Cup.setPower(0.1);
                Cup.setTargetPosition(-138);

            }else{

                // we are in move so use RWE
                Cup.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                if (gamepad2.left_stick_y > 0)
                    Cup.setPower(gamepad2.left_stick_y);
                else
                    Cup.setPower(gamepad2.left_stick_y);
            }

            // remember last detent state for next time around.
            LastDetent = Detent;


            double right = -gamepad2.right_stick_y;
            Screw.setPower(right);
            telemetry.addData("Cup", Cup.getCurrentPosition());
            telemetry.update();
        }


        }

    }


