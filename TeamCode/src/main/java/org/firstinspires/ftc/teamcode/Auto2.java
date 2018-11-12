package org.firstinspires.ftc.teamcode;



import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Vuforia;


@Autonomous(name="Auto2")

public class Auto2 extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();
    private ElapsedTime runtime = new ElapsedTime();
    String POSITION_GOLD;

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
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

        tracking.preInit(hardwareMap,this);
        tracking.initVuforia();
        tracking.initTfod();

        Cup = hardwareMap.dcMotor.get("Cup");
        Screw = hardwareMap.dcMotor.get("Screw");
        Cup.setDirection(DcMotorSimple.Direction.REVERSE);


        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Cup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Screw.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Cup.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Screw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                robot.leftDrive.getCurrentPosition(),
                robot.rightDrive.getCurrentPosition(),
                robot.backLDrive.getCurrentPosition(),
                robot.backRDrive.getCurrentPosition(),
                Cup.getCurrentPosition(),
                Screw.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        tracking.activateTfod();
        if(tracking.tfod != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (tracking.getPosition().equals("UNKNOWN")) {

                        POSITION_GOLD = tracking.getPosition();

                    }
                }
            }).start();
        }
        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)

        encoderDrive(0.75,  0,  0, 0, 0, 0, 43, 10.0);  // S1: Forward 47 Inches with 5 Sec timeout
        telemetry.addData("POSITION after first move", POSITION_GOLD);
        telemetry.update();
        encoderDrive(0.5, -2, 3, -2,0, 0,0,7.0);
        telemetry.addData("POSITION after second move", POSITION_GOLD);
        telemetry.update();
        encoderDrive(0.75, -15, -15, 0,0, 0,0,10.0);
        telemetry.addData("POSITION after third move", POSITION_GOLD);
        telemetry.update();
        sleep(1000);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches, double backleftInches, double backrightInches, double Cupinches, double Screwinches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;
        int newCupTarget;
        int newScrewTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = robot.leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = robot.rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newBackLeftTarget = robot.backLDrive.getCurrentPosition() + (int)(backleftInches * COUNTS_PER_INCH);
            newBackRightTarget = robot.backRDrive.getCurrentPosition() + (int)(backrightInches * COUNTS_PER_INCH);
            newCupTarget = Cup.getCurrentPosition() + (int)(Cupinches * COUNTS_PER_INCH);
            newScrewTarget = Screw.getCurrentPosition() + (int)(Screwinches * COUNTS_PER_INCH);
            robot.leftDrive.setTargetPosition(newLeftTarget);
            robot.rightDrive.setTargetPosition(newRightTarget);
            robot.backLDrive.setTargetPosition(newBackLeftTarget);
            robot.backRDrive.setTargetPosition(newBackRightTarget);
            Cup.setTargetPosition(newCupTarget);
            Screw.setTargetPosition(newScrewTarget);


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

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        robot.leftDrive.getCurrentPosition(),
                        robot.rightDrive.getCurrentPosition(), robot.backLDrive.getCurrentPosition(),
                        robot.backRDrive.getCurrentPosition(), Cup.getCurrentPosition(), Screw.getCurrentPosition());
                telemetry.update();
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



            sleep(250);   // optional pause after each move
        }
    }
}
