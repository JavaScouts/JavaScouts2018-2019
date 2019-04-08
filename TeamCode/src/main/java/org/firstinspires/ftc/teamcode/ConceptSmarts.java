package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "1 New Smart Turn Concept (Control)")
public class ConceptSmarts extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    public void runOpMode() {

        robot.init(hardwareMap, this);

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.right_stick_x == 0) {
                robot.smartPower = 0;
                telemetry.addLine("Smart Power resetting due to release of joystick.");
            }

            robot.smartManualDrive(0.92);
            robot.moveRobot();

            telemetry.update();

        }

    }

}
