package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name="TankDrive2")
public class TankDrive2 extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);

        waitForStart();

        while(opModeIsActive()) {

            robot.manualDrive();
            robot.moveRobot();

        }

    }

}
