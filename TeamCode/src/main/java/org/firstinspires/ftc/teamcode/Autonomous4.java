package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;

@Autonomous(name = "A4: Crater to claim to friendly")
public class Autonomous4 extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    VuforiaTracking tracking = new VuforiaTracking();
    private ElapsedTime runtime = new ElapsedTime();
    String POSITION_GOLD, pos;
    Thread t;

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

                        pos = tracking.getPositionByElimination();
                        if (!pos.equals("UNKNOWN")) {
                            POSITION_GOLD = pos;
                            telemetry.addData("Thread result", POSITION_GOLD);
                            telemetry.addData("Number", tracking.getNumberRecognitions());
                            telemetry.update();
                        }
                    }
                }
            }
        }).start();
                tracking.activateTfod();

        telemetry.addData("Position before move", POSITION_GOLD);
        telemetry.update();

        //lower robot
        encoderDrive(0.75, 0, 0, 0, 0, 0, 9200, 7.0);


        telemetry.addData("Position after move 1", POSITION_GOLD);
        telemetry.update();


        switch (POSITION_GOLD) {
            case "LEFT":

                robot.gyroTurn(0.75, 25);
                encoderDrive(0.75, -2800, -2800, -2800, -2800, 0, 0, 3.0);
                encoderDrive(0.5, 2000, 2000, 2000, 2000, 0, 0, 3.0);
                robot.gyroTurn(0.5, 45);
                while (robot.range.getDistance(DistanceUnit.INCH) > 6.0) {
                    robot.setPower(-0.57);
                }
                robot.setPower(0);
                sleep(300);
                robot.gyroTurn(0.5, 128);
                encoderDrive(1.0, -5500, -5500, -5500, -5500, 0, 0, 5.0);
                robot.gyroTurn(0.5, 130);
//                robot.yeet.setPosition(1.0);
                sleep(600);
                robot.gyroTurn(0.5, 140);
                encoderDrive(1.0, 6000, 6000, 6000, 6000, 0, 0, 5.0);
                encoderDrive(1.0, 0, 0, 0, 0, 500, 0, 5.0);
                runtime.reset();
                while (runtime.seconds() < 1 && opModeIsActive()) {
                    robot.rev.setPower(-1.0);
                }

                break;
            case "CENTER":
                robot.gyroTurn(0.75, 15);
                encoderDrive(0.75, -700, -700, -700, -700, 0, 0, 3.0);
                robot.gyroTurn(0.5, -7);
                encoderDrive(0.75, -1800, -1800, -1800, -1800, 0, 0, 3.0);
                encoderDrive(0.75, 2100, 2100, 2100, 2100, 0, 0, 3.0);
                robot.gyroTurn(0.5, 45);
                while (robot.range.getDistance(DistanceUnit.INCH) > 6.0) {
                    robot.setPower(-0.5);
                }
                robot.setPower(0);
                sleep(600);
                robot.gyroTurn(0.5, 128);
                encoderDrive(1.0, -5500, -5500, -5500, -5500, 0, 0, 5.0);
//                robot.gyroTurn(0.5,130);
                robot.yeet.setPosition(1.0);
                sleep(600);
                robot.gyroTurn(0.5, 139);
                encoderDrive(1.0, 6000, 6000, 6000, 6000, 0, 0, 5.0);
                encoderDrive(1.0, 0, 0, 0, 0, 500, 0, 5.0);
                runtime.reset();
                while (runtime.seconds() < 1 && opModeIsActive()) {
                    robot.rev.setPower(-1.0);
                }

                break;
            default:  //this is exception handling. it includes the "RIGHT" case and all other situations. RIGHT is the most reliable.

                encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 1.4);
                encoderDrive(0.75, -600, -600, -600, -600, 0, 0, 1.4);
                robot.gyroTurn(0.75, -35);
                encoderDrive(0.75, -2300, -2300, -2300, -2300, 0, 0, 2.0);
                encoderDrive(0.75, 2700, 2700, 2700, 2700, 0, 0, 2.0);
                robot.gyroTurn(0.75, 43);
                while (robot.range.getDistance(DistanceUnit.INCH) > 6.0) {
                    robot.setPower(-0.75);
                }
                robot.setPower(0);
                sleep(600);
                robot.gyroTurn(0.5, 128);
                encoderDrive(1.0, -5500, -5500, -5500, -5500, 0, 0, 5.0);
//                robot.gyroTurn(0.5,130);
                robot.yeet.setPosition(1.0);
                sleep(600);
                robot.gyroTurn(0.5, 137);
                encoderDrive(1.0, 6000, 6000, 6000, 6000, 0, 0, 5.0);
                encoderDrive(1.0, 0, 0, 0, 0, 500, 0, 5.0);
                runtime.reset();
                while (runtime.seconds() < 1 && opModeIsActive()) {
                    robot.rev.setPower(-1.0);
                }


                break;
        }

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


