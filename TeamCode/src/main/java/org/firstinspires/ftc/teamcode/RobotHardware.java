package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;

public class RobotHardware {

    //initialize everything
    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor backLDrive;
    DcMotor backRDrive;
    DcMotor cup;
    DcMotor screw;

    Servo ball;

    ModernRoboticsI2cGyro gyro;
    ModernRoboticsI2cRangeSensor range;

    //driving coefficients
    private double driveAxial = 0;   // Positive is forward
    private double driveLateral = 0;   // Positive is right
    private double driveYaw = 0;   // Positive is CCW
    private static final double HEADING_THRESHOLD = 1;      // As tight as we can make it with an integer gyro
    private static final double P_TURN_COEFF = 0.1;

    private LinearOpMode myOpMode;
    private HardwareMap map;

    //constructor
    RobotHardware() {

    }

    void init(HardwareMap map1, LinearOpMode opMode) {

        map = map1;
        myOpMode = opMode;

        //get motors from map
        leftDrive = map.dcMotor.get("fl");
        rightDrive = map.dcMotor.get("fr");
        backLDrive = map.dcMotor.get("bl");
        backRDrive = map.dcMotor.get("br");
        cup = map.dcMotor.get("Cup");
        screw = map.dcMotor.get("Screw");

        //get servos
        ball = map.servo.get("Ball");

        //get sensors
        gyro = map.get(ModernRoboticsI2cGyro.class, "g");
        range = map.get(ModernRoboticsI2cRangeSensor.class, "r");

        //reverse nessecary motors
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        cup.setDirection(DcMotorSimple.Direction.REVERSE);

        //STOP EVERYTHING
        moveRobot(0, 0, 0);

        myOpMode.telemetry.addData("Initialization:", "Complete!");
        myOpMode.telemetry.addData(" Left Encoder Count", leftDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" Right Encoder Count", rightDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" BLeft Encoder Count", backLDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" BRight Encoder Count", backRDrive.getCurrentPosition());


        myOpMode.telemetry.update();

    }

    void setMode(DcMotor.RunMode mode) {

        leftDrive.setMode(mode);
        rightDrive.setMode(mode);
        backLDrive.setMode(mode);
        backRDrive.setMode(mode);
        cup.setMode(mode);
        screw.setMode(mode);

    }

    void setPower(double power) {

        leftDrive.setPower(power);
        rightDrive.setPower(power);
        backLDrive.setPower(power);
        backRDrive.setPower(power);

    }

    void manualDrive() {
        // In this mode the Left stick moves the robot fwd & back, and Right & Left.
        // The Right stick rotates CCW and CW.

        setAxial(-myOpMode.gamepad1.left_stick_y);
        setLateral(myOpMode.gamepad1.left_stick_x);
        setYaw(myOpMode.gamepad1.right_stick_x);

    }

    private void moveRobot(double axial, double lateral, double yaw) {

        setAxial(axial);
        setYaw(yaw);
        setLateral(lateral);
        moveRobot();

    }

    void moveRobot() {
        // calculate required motor powers to achieve axis motions, based on the movement of mecanum wheels
        double backL = driveAxial - driveLateral + driveYaw;
        double backR = driveAxial + driveLateral - driveYaw;
        double left = driveAxial + driveLateral + driveYaw;
        double right = driveAxial - driveLateral - driveYaw;

        // normalize all motor speeds so no values exceeds 100%
        double max = Math.max(Math.abs(left), Math.abs(right));
        max = Math.max(max, Math.abs(backL));
        max = Math.max(max, Math.abs(backR));
        if (max > 1.0) {
            backR /= max;
            backL /= max;
            right /= max;
            left /= max;
        }

        // Set drive motor power levels
        backLDrive.setPower(backL);
        backRDrive.setPower(backR);
        leftDrive.setPower(left);
        rightDrive.setPower(right);

        // Display Telemetry
        myOpMode.telemetry.addData("Axes  ", "A[%+5.2f], L[%+5.2f], Y[%+5.2f]", driveAxial, driveLateral, driveYaw);
        myOpMode.telemetry.addData("Wheels", "L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]", left, right, backL, backR);

    }

    private void setAxial(double axial) {
        driveAxial = Range.clip(axial, -1, 1);
    }

    private void setLateral(double lateral) {
        driveLateral = Range.clip(lateral, -1, 1);
    }

    private void setYaw(double yaw) {
        driveYaw = Range.clip(yaw, -1, 1);
    }

    //the following 4 methods are adapted from the pushbot gyro example class
    void gyroTurn(double speed, double angle) {

        // keep looping while we are still active, and not on heading. onHeading() moves the robot.
        while (myOpMode.opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {

            myOpMode.telemetry.update();

        }
    }

    Boolean onHeading(double speed, double angle, double PCoeff) {
        double error;
        double steer;
        boolean onTarget = false;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        } else {
            steer = getSteer(error, PCoeff);
            rightSpeed = speed * steer;
            leftSpeed = -rightSpeed;
        }

        // Send desired speeds to motors.
        leftDrive.setPower(leftSpeed);
        rightDrive.setPower(rightSpeed);
        backLDrive.setPower(leftSpeed);
        backRDrive.setPower(rightSpeed);

        myOpMode.telemetry.addData("Target", "%5.2f", angle);
        myOpMode.telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        myOpMode.telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);

        return onTarget;
    }

    double getError(double targetAngle) {

        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle - gyro.getHeading();
        while (robotError > 180) robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

    //this method is adapted from the pushbot example class for encoder driving
    public void encoderDrive(double speed,
                             double leftCounts, double rightCounts, double backleftCounts, double backrightCounts, double CupCounts, double ScrewCounts,
                             double timeoutS,
                             boolean opModeActive, ElapsedTime rt) {

        // Ensure that we're not changing the wrong thing
        if (speed > 1 || speed < 0) {
            throw new RuntimeException("InvalidSpeed");
        }

        // Ensure that the opmode is still active
        if (opModeActive) {

            // Determine new target position, and pass to motor controller
            leftDrive.setTargetPosition((int) leftCounts + leftDrive.getCurrentPosition());
            rightDrive.setTargetPosition((int) rightCounts + rightDrive.getCurrentPosition());
            backLDrive.setTargetPosition((int) backleftCounts + backLDrive.getCurrentPosition());
            backRDrive.setTargetPosition((int) backrightCounts + backRDrive.getCurrentPosition());
            cup.setTargetPosition((int) CupCounts + cup.getCurrentPosition());
            screw.setTargetPosition((int) ScrewCounts + screw.getCurrentPosition());

            // Turn On RUN_TO_POSITION
            setMode(RUN_TO_POSITION);

            // reset the timeout time and start motion.
            rt.reset();
            leftDrive.setPower(Math.abs(speed));
            rightDrive.setPower(Math.abs(speed));
            backLDrive.setPower(Math.abs(speed));
            backRDrive.setPower(Math.abs(speed));
            cup.setPower(Math.abs(speed));
            screw.setPower(Math.abs(speed));


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continuess
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeActive &&
                    (rt.seconds() < timeoutS) &&
                    (leftDrive.isBusy() || rightDrive.isBusy() || backLDrive.isBusy() || backRDrive.isBusy() || cup.isBusy() || screw.isBusy())) {
            }

            setMode(RUN_USING_ENCODER);

            // Stop all motion;
            leftDrive.setPower(0);
            rightDrive.setPower(0);
            backLDrive.setPower(0);
            backRDrive.setPower(0);
            cup.setPower(0);
            screw.setPower(0);


            //sleep(100);   // optional pause after each move
        }
    }
}
