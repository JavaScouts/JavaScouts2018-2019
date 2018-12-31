package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name = "Rev")
public class Rev extends LinearOpMode {

    DcMotor Rev1;
    DcMotor Rev2;

    @Override
    public void runOpMode() {

        Rev1 = hardwareMap.dcMotor.get("R1");
        Rev2 = hardwareMap.dcMotor.get("R2");
        waitForStart();


        while (opModeIsActive()) {


             /*if(gamepad1.dpad_up) {
                 Rev1.setPower(1.0);
                 Rev2.setPower(1.0);

            } else if(gamepad1.dpad_down) {
                 Rev1.setPower(-1.0);
                 Rev2.setPower(-1.0);


            }
*/

            float power = gamepad1.right_stick_y;

            Rev1.setPower(power);
            Rev2.setPower(power);

        }

    }

}



