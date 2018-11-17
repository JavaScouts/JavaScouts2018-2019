package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Vuforia;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;


@Autonomous(name = "Auto2")

public class Auto2 extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();
    private ElapsedTime runtime = new ElapsedTime();
    String POSITION_GOLD = "UNKNOWN";

    static final double COUNTS_PER_MOTOR_MOTOR_REV = 1440;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 2.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_MOTOR_INCH = (COUNTS_PER_MOTOR_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    DcMotor Cup;
    DcMotor Screw;

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

        Cup = hardwareMap.dcMotor.get("Cup");
        Screw = hardwareMap.dcMotor.get("Screw");
        Cup.setDirection(DcMotorSimple.Direction.REVERSE);

//        VisionThread vthread = new VisionThread();

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.leftDrive.setMode(STOP_AND_RESET_ENCODER);
        robot.rightDrive.setMode(STOP_AND_RESET_ENCODER);
        robot.backRDrive.setMode(STOP_AND_RESET_ENCODER);
        robot.backLDrive.setMode(STOP_AND_RESET_ENCODER);
        Cup.setMode(STOP_AND_RESET_ENCODER);
        Screw.setMode(STOP_AND_RESET_ENCODER);

        robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Cup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Screw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);




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
        if (POSITION_GOLD.equals("LEFT")) {
            encoderDrive(0.5, -1000, 400, -1000, 400, 0, 0, 7.0);
            encoderDrive(0.75, -3900, -3900, -3900, -3900, 0, 0, 5.0);
            encoderDrive(0.5, 800, -800, 800, -800, 0, 0, 3.0);
            encoderDrive(0.5, -2900, -2900, -2900, -2900, 0, 0, 4.0);
            encoderDrive(0.5, 1200, -1200, 1200, -1200, 0, 0, 3.0);
            encoderDrive(0.75, -1000, -350, -350, -350, 0, 0, 2.0);
            encoderDrive(0.75, 0, 0, 0, 0, -175, 0, 2.0);
            encoderDrive(0.75, -500, 500, -500, 500, 0, 0, 4.0);
            encoderDrive(10, -10000, -10000, -10000, -10000, 0, 0, 5.0);
            sleep(1000);
            telemetry.addData("Path", "Complete");
            telemetry.update();
        } else if (POSITION_GOLD.equals("CENTER")) {
            encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 3.0);
            encoderDrive(0.75, -700, -700, -700, -700, 0, 0, 3.0);
            encoderDrive(0.5, 80, -80, 80, -80, 0, 0, 3.0);
            encoderDrive(0.75, -5000, -5000, -5000, -5000, 0, 0, 5.0);
            encoderDrive(0.5, 1900, -1900, 1900, -1900, 0, 0, 5.0);
            encoderDrive(0.75, 0, 0, 0, 0, -175, 0, 2.0);
            encoderDrive(0.75, -550, 550, -550, 550, 0, 0, 4.0);
            encoderDrive(10, -10000, -10000, -10000, -10000, 0, 0, 7.0);


        } else {
            encoderDrive(0.5, -550, 550, -550, 550, 0, 0, 3.0);
            encoderDrive(0.75, -600, -600, -600, -600, 0, 0, 3.0);
            encoderDrive(0.5, 600, -600, 600, -600, 0, 0, 4.0);
            encoderDrive(0.75, -4000, -4000, -4000, -4000, 0, 0, 3.0);
            encoderDrive(0.5, 1500, -1500, 1500, -1500, 0, 0, 4.0);
            encoderDrive(0.75, 2500, 2500, 2500, 2500, 0, 0, 3.0);
            encoderDrive(0.75, 0, 0, 0, 0, -175, 0, 2.0);
            encoderDrive(0.75, -550, 550, -550, 550, 0, 0, 4.0);
            encoderDrive(10, -10000, -10000, -10000, -10000, 0, 0, 7.0);

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
            Cup.setTargetPosition((int) CupCounts + Cup.getCurrentPosition());
            Screw.setTargetPosition( (int) ScrewCounts + Screw.getCurrentPosition());

            // Turn On RUN_TO_POSITION
            robot.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Cup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Screw.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftDrive.setPower(Math.abs(speed));
            robot.rightDrive.setPower(Math.abs(speed));
            robot.backLDrive.setPower(Math.abs(speed));
            robot.backRDrive.setPower(Math.abs(speed));
            Cup.setPower(Math.abs(speed));
            Screw.setPower(Math.abs(speed));


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continuess
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftDrive.isBusy() || robot.rightDrive.isBusy() || robot.backLDrive.isBusy() || robot.backRDrive.isBusy() || Cup.isBusy() || Screw.isBusy())) {
            }

            robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Cup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Screw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Stop all motion;
            robot.leftDrive.setPower(0);
            robot.rightDrive.setPower(0);
            robot.backLDrive.setPower(0);
            robot.backRDrive.setPower(0);
            Cup.setPower(0);
            Screw.setPower(0);


            sleep(100);   // optional pause after each move
        }
    }
/*
    private class VisionThread implements Runnable {

        public VisionThread() {
        }

        public void run() {
            try {
            if (opModeIsActive()) {
                if (tracking.tfod != null) {
                    while (POSITION_GOLD.equals("UNKNOWN")) {
                        POSITION_GOLD = tracking.getPosition();
                    }
                }
            }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}
