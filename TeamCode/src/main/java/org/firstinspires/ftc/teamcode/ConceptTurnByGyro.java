package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "1 New Turn Test (control)")
public class ConceptTurnByGyro extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    private boolean usingNew = false;
    private ElapsedTime runtime = new ElapsedTime();
    private boolean isHeldDecrease, isHeldIncrease;

    public void runOpMode() {

        robot.init(hardwareMap, this);

        robot.gyro.calibrate();

        while (!isStopRequested() && robot.gyro.isCalibrating()) {
            sleep(50);
            idle();
        }

        while (!isStarted()) {
            telemetry.addData("Waiting...>", "Robot Heading = %d", robot.gyro.getIntegratedZValue());
            telemetry.update();
        }

        while (opModeIsActive()) {

            if (!gamepad1.a) {
                robot.manualDrive(0.7);
                robot.moveRobot();
                continue;
            }

            int count = 0;
            boolean keepGoing = true;
            runtime.reset();
            int curr = robot.gyro.getIntegratedZValue();
            telemetry.addData("Turn started at heading>", curr);
            telemetry.update();
            sleep(500);

            while (opModeIsActive() && keepGoing) {

                telemetry.addData("Count>", count);
                telemetry.update();

                if (gamepad1.left_bumper) {

                    count = count - 7;
                    runtime.reset();
                    sleep(500);
                    continue;

                }

                if (gamepad1.right_bumper) {

                    count = count + 7;
                    runtime.reset();
                    sleep(500);
                    continue;

                }

                if (runtime.milliseconds() > 1000) {

                    keepGoing = false;

                }

            }

            int next = curr + count;
            robot.gyroTurn(0.7, next);
            usingNew = false;
            telemetry.addData("Turn completed to heading>", next);
            telemetry.update();

        }

    }

}

