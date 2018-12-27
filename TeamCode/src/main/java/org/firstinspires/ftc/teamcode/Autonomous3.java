package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;

@Autonomous(name = "A3: Starts depot, ends enemy")
public class Autonomous3 extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    VuforiaTracking tracking = new VuforiaTracking();
    private ElapsedTime runtime = new ElapsedTime();
    String POSITION_GOLD, pos = "UNKNOWN";
    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);

        robot.gyro.calibrate();
        // make sure the gyro is calibrated before continuing

        while (!isStopRequested() && robot.gyro.isCalibrating()) {
            sleep(50);
            idle();
        }

        //init vuforia and tfod
        tracking.preInit(hardwareMap, this);
        tracking.initVuforia();
        tracking.initTfod();

        robot.setMode(STOP_AND_RESET_ENCODER);

        robot.setMode(RUN_USING_ENCODER);

        //same as waitForStart(), but displays robot heading
        while (!isStarted()) {
            telemetry.addData(">", "Robot Heading = %d", robot.gyro.getIntegratedZValue());
            telemetry.update();
        }

        robot.gyro.resetZAxisIntegrator();

        /*
            we create a new thread, separate from the main thread in order to run the vision processing while the robot is moving.
            the thread will only run until a valid position is found. this helps with an issue with detecting more than 3 objects or less than 3.
            however, the main thread will only check to see if the thread has returned a result after each move.
         */

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (opModeIsActive() && tracking.tfod != null) {

                    POSITION_GOLD = "UNKNOWN"; //combat NullPointerException

                    while (POSITION_GOLD.equals("UNKNOWN")) {

                        pos = tracking.getPositionByTwo();

                        if (!pos.equals("UNKNOWN")) {
                            POSITION_GOLD = pos;

                        }
                    }
                }
            }
        }).start();

        tracking.activateTfod();

        telemetry.addData("Position before move", POSITION_GOLD);
        telemetry.update();

        //lower robot
        encoderDrive(0.75, 0, 0, 0, 0, 0, 10000, 5.0);

        telemetry.addData("Position after move 1", POSITION_GOLD);
        telemetry.update();

        //we use a combination of encoders and gyros and range to move the robot accurately
        switch (POSITION_GOLD) {
            case "LEFT":

                encoderDrive(0.5, -1200, 600, -1200, 600, 0, 0, 3.0);
                encoderDrive(0.75, -3900, -3900, -3900, -3900, 0, 0, 4.0);
                robot.gyroTurn(0.5, -45);
                while (robot.range.getDistance(DistanceUnit.INCH) > 10.0) {
                    robot.setPower(-0.5);
                }

                robot.setPower(0);
                sleep(600);
                robot.gyroTurn(0.5, -230);
                encoderDrive(0.75, 0, 0, 0, 0, -175, 0, 2.0);
                robot.gyroTurn(0.5, -233);
                encoderDrive(10, -7000, -7000, -7000, -7000, 0, 0, 4.0);
                encoderDrive(0.75, 0, 0, 0, 0, -700, 0, 3.0);
                robot.ball.setPosition(1.0);
                robot.gyroTurn(0.5, -233);

                break;
            case "CENTER":

                encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 3.0);
                encoderDrive(0.75, -700, -700, -700, -700, 0, 0, 3.0);
                robot.gyroTurn(0.5, -9);
                encoderDrive(0.75, -5000, -5000, -5000, -5000, 0, 0, 5.0);
                robot.gyroTurn(0.5, -230);
                while (robot.range.getDistance(DistanceUnit.INCH) < 10.0) {
                    robot.setPower(-0.5);
                }

                robot.setPower(0);
                sleep(600);
                encoderDrive(0.75, 0, 0, 0, 0, -175, 0, 2.0);
                robot.gyroTurn(0.5, -233);
                encoderDrive(10, -7000, -7000, -7000, -7000, 0, 0, 4.0);
                encoderDrive(0.75, 0, 0, 0, 0, -700, 0, 3.0);
                robot.ball.setPosition(1.0);
                robot.gyroTurn(0.5, -233);

                break;
            default:  //this is exception handling. it includes the "RIGHT" case and all other situations. RIGHT is the most reliable.

                encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 3.0);
                encoderDrive(0.75, -600, -600, -600, -600, 0, 0, 3.0);
                encoderDrive(0.5, 600, -600, 600, -600, 0, 0, 4.0);
                encoderDrive(0.75, -4000, -4000, -4000, -4000, 0, 0, 3.0);
                robot.gyroTurn(0.5, -132);
                encoderDrive(0.75, 3200, 3200, 3200, 3200, 0, 0, 3.0);
                encoderDrive(0.75, 0, 0, 0, 0, -175, 0, 2.0);
                robot.gyroTurn(0.5, -225);
                encoderDrive(10, -7500, -7500, -7500, -7500, 0, 0, 4.0);
                encoderDrive(0.75, 0, 0, 0, 0, -850, 0, 3.0);
                robot.ball.setPosition(1.0);
                robot.gyroTurn(0.5, -231);

                break;
        }

        robot.ball.setPosition(0.85);
        telemetry.addLine("autonomous completed in "+Math.round(runtime.seconds())+" seconds.");
        telemetry.update();

    }

    //this method is adapted from the pushbot example class for encoder driving
    public void encoderDrive(double speed,
                             double leftCounts, double rightCounts, double backleftCounts, double backrightCounts, double CupCounts, double ScrewCounts,
                             double timeoutS) {

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            robot.leftDrive.setTargetPosition((int) leftCounts + robot.leftDrive.getCurrentPosition());
            robot.rightDrive.setTargetPosition((int) rightCounts + robot.rightDrive.getCurrentPosition());
            robot.backLDrive.setTargetPosition((int) backleftCounts + robot.backLDrive.getCurrentPosition());
            robot.backRDrive.setTargetPosition((int) backrightCounts + robot.backRDrive.getCurrentPosition());
            robot.cup.setTargetPosition((int) CupCounts + robot.cup.getCurrentPosition());
            robot.screw.setTargetPosition((int) ScrewCounts + robot.screw.getCurrentPosition());

            // Turn On RUN_TO_POSITION
            robot.setMode(RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftDrive.setPower(Math.abs(speed));
            robot.rightDrive.setPower(Math.abs(speed));
            robot.backLDrive.setPower(Math.abs(speed));
            robot.backRDrive.setPower(Math.abs(speed));
            robot.cup.setPower(Math.abs(speed));
            robot.screw.setPower(Math.abs(speed));


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continuess
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftDrive.isBusy() || robot.rightDrive.isBusy() || robot.backLDrive.isBusy() || robot.backRDrive.isBusy() || robot.cup.isBusy() || robot.screw.isBusy())) {
            }

            robot.setMode(RUN_USING_ENCODER);

            // Stop all motion;
            robot.leftDrive.setPower(0);
            robot.rightDrive.setPower(0);
            robot.backLDrive.setPower(0);
            robot.backRDrive.setPower(0);
            robot.cup.setPower(0);
            robot.screw.setPower(0);


            sleep(100);   // optional pause after each move
        }
    }
}


