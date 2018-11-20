package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Vuforia;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;


@Autonomous(name = "Final Autonomous 2")

public class Autonomous2 extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();
    private ElapsedTime runtime = new ElapsedTime();
    private String POSITION_GOLD = "UNKNOWN";

    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap, this);

        tracking.preInit(hardwareMap, this);
        tracking.initVuforia();
        tracking.initTfod();

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.setMode(STOP_AND_RESET_ENCODER);

        robot.setMode(RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        tracking.activateTfod();
        POSITION_GOLD = tracking.getPosition();
        telemetry.addData("Position before move", POSITION_GOLD);
        telemetry.update();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)

        encoderDrive(0.75, 0, 0, 0, 0, 0, 9200, 10.0);  // S1: Forward 47 Inches with 5 Sec timeout
        POSITION_GOLD = tracking.getPosition();
        telemetry.addData("Position after move 1", POSITION_GOLD);
        telemetry.update();
        if(POSITION_GOLD == null) {
            telemetry.addData("ERROR", "NULL");
        }

            else {

                if (POSITION_GOLD.equals("LEFT")) {
                    encoderDrive(0.5, -1000, 400, -1000, 400, 0, 0, 7.0);
                    encoderDrive(0.75, -3900, -3900, -3900, -3900, 0, 0, 5.0);
                    encoderDrive(0.5, 800, -800, 800, -800, 0, 0, 3.0);

                } else if (POSITION_GOLD.equals("CENTER")) {
                    encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 3.0);
                    encoderDrive(0.75, -700, -700, -700, -700, 0, 0, 3.0);
                    encoderDrive(0.5, 80, -80, 80, -80, 0, 0, 3.0);

                } else {
                    encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 3.0);
                    encoderDrive(0.75, -600, -600, -600, -600, 0, 0, 3.0);
                    encoderDrive(0.5, 600, -600, 600, -600, 0, 0, 4.0);

                }
            }
        }

    public void encoderDrive(double speed,
                             double leftCounts, double rightCounts, double backleftCounts, double backrightCounts, double CupCounts, double ScrewCounts,
                             double timeoutS) {

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            robot.leftDrive.setTargetPosition( (int) leftCounts + robot.leftDrive.getCurrentPosition());
            robot.rightDrive.setTargetPosition( (int) rightCounts + robot.rightDrive.getCurrentPosition());
            robot.backLDrive.setTargetPosition( (int) backleftCounts + robot.backLDrive.getCurrentPosition());
            robot.backRDrive.setTargetPosition( (int) backrightCounts + robot.backRDrive.getCurrentPosition());
            robot.cup.setTargetPosition((int) CupCounts + robot.cup.getCurrentPosition());
            robot.screw.setTargetPosition( (int) ScrewCounts + robot.screw.getCurrentPosition());

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
