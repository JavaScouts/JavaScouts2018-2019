package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

/**
 * Created by seed on 9/11/17.
 */

public class LZRobot {

    //initialize motors
    public DcMotor leftDrive;
    public DcMotor rightDrive;
    public DcMotor backLDrive;
    public DcMotor backRDrive;
    public Servo s1;
    public Servo s2;
    private ElapsedTime runtime = new ElapsedTime();
    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    public DcMotor slide;
    public DcMotor Arm;
    //public ModernRoboticsI2cRangeSensor range1;
    public ColorSensor color;
    public Servo s5;
    public Servo s6;

    public float Y1;
    public float X1;
    public float X2;
    public int[] positions = {};
    private double  driveAxial      = 0 ;   // Positive is forward
    private double  driveLateral    = 0 ;   // Positive is right
    private double  driveYaw        = 0 ;   // Positive is CCW
    //public Servo servo1;
    private LinearOpMode myOpMode;
    //create map
    HardwareMap map;

    public LZRobot() {

    }

    public void init(HardwareMap map1, LinearOpMode opMode) {

        map = map1;
        myOpMode = opMode;

        //get motors from map
        leftDrive = map.dcMotor.get("fl");
        rightDrive = map.dcMotor.get("fr");
        backLDrive = map.dcMotor.get("bl");
        backRDrive = map.dcMotor.get("br");


        myOpMode.telemetry.addData("Initialization:", "Motors Initialized.");
        myOpMode.telemetry.update();



        myOpMode.telemetry.addData("Initialization:", "Servos Initialized.");
        myOpMode.telemetry.update();


        //range1 = map.get(ModernRoboticsI2cRangeSensor.class, "range1");
        //range2 = map.get(ModernRoboticsI2cRangeSensor.class, "range2");

        myOpMode.telemetry.addData("Initialization:", "Sensors Initialized.");
        myOpMode.telemetry.update();

        //set directions
        //s1.setDirection(Servo.Direction.REVERSE);

        //STOP EVERYTHING
        moveRobot(0, 0, 0);
        //s1.setPosition(0.4);
        myOpMode.telemetry.addData("Initialization:", "Complete!");
        myOpMode.telemetry.addData(" Left Encoder Count", leftDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" Right Encoder Count", rightDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" BLeft Encoder Count", backLDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" BRight Encoder Count", backRDrive.getCurrentPosition());


        myOpMode.telemetry.update();

    }

    //function move takes a direction and a power
    //probably a better way to do this
    public void move(String dir, double power) {

        //self explanatory
            switch (dir) {
                case "left":

                    backLDrive.setPower(power);
                    leftDrive.setPower(-power);
                    backRDrive.setPower(-power);
                    rightDrive.setPower(power);

                    break;
                case "right":

                    backLDrive.setPower(-power);
                    leftDrive.setPower(power);
                    backRDrive.setPower(power);
                    rightDrive.setPower(-power);

                    break;
                case "forwards":

                    backLDrive.setPower(power);
                    leftDrive.setPower(power);
                    backRDrive.setPower(power);
                    rightDrive.setPower(power);

                    break;
                case "backwards":

                    backLDrive.setPower(-power);
                    leftDrive.setPower(-power);
                    backRDrive.setPower(-power);
                    rightDrive.setPower(-power);

                    break;
        }

    }

    public void moveDist(String dir, double power, double distance, double timeoutS) {

        int newLeftTarget;
        int newRightTarget;
        int newBackLTarget;
        int newBackRTarget;
        int newArmTarget;

        // Ensure that the opmode is still active
        if (myOpMode.opModeIsActive()) {

            newLeftTarget = 0;
            newBackLTarget = 0;
            newRightTarget = 0;
            newBackRTarget = 0;

            // Determine new target position, and pass to motor controller
            if (dir.equals("forwards")) {

                newLeftTarget = leftDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newBackLTarget = backLDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newBackRTarget = backRDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);

            } else if (dir.equals("backwards")) {

                newLeftTarget = leftDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newBackLTarget = backLDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newBackRTarget = backRDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);

            } else if (dir.equals("left")) {

                newLeftTarget = leftDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newBackLTarget = backLDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newBackRTarget = backRDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);

            } else if (dir.equals("right")) {

                newLeftTarget = leftDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newBackLTarget = backLDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newBackRTarget = backRDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);

            } else if (dir.equals("cclock")) {

                newLeftTarget = leftDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newBackLTarget = backLDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newBackRTarget = backRDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);

            } else if (dir.equals("clock")) {

                newLeftTarget = leftDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newBackLTarget = backLDrive.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);
                newBackRTarget = backRDrive.getCurrentPosition() - (int)(distance * COUNTS_PER_INCH);

            }

            newArmTarget = slide.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);

            leftDrive.setTargetPosition(newLeftTarget);
            backLDrive.setTargetPosition(newBackLTarget);
            rightDrive.setTargetPosition(newRightTarget);
            backRDrive.setTargetPosition(newBackRTarget);
            slide.setTargetPosition(newArmTarget);

            // Turn On RUN_TO_POSITION
            setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftDrive.setPower(Math.abs(power));
            backLDrive.setPower(Math.abs(power));
            rightDrive.setPower(Math.abs(power));
            backRDrive.setPower(Math.abs(power));
            slide.setPower(Math.abs(power));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (myOpMode.opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftDrive.isBusy() || backLDrive.isBusy() || rightDrive.isBusy() || backRDrive.isBusy() || slide.isBusy())) {

                // Display it for the driver.
                myOpMode.telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget, newBackLTarget,  newRightTarget, newBackRTarget, newArmTarget);
                myOpMode.telemetry.addData("Path2",  "Running at %7d :%7d",
                        leftDrive.getCurrentPosition(),
                        backLDrive.getCurrentPosition(),
                        rightDrive.getCurrentPosition(),
                        backRDrive.getCurrentPosition(),
                        slide.getCurrentPosition());
                myOpMode.telemetry.update();
            }

            // Stop all motion;
            moveRobot(0, 0, 0);
            slide.setPower(0);

            // Turn off RUN_TO_POSITION
            setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }

    }

    //rotate function, takes direction and power
    public void rotateTime(String dir, double power, long timeMS)
    {

        //self explanatory rotation for mecanum wheels
        if(dir.equals("cclock")) {

            backLDrive.setPower(-power);
            leftDrive.setPower(-power);
            backRDrive.setPower(power);
            rightDrive.setPower(power);
            try {
                TimeUnit.MILLISECONDS.sleep(timeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if(dir.equals("clock")) {

            backLDrive.setPower(power);
            leftDrive.setPower(power);
            backRDrive.setPower(-power);
            rightDrive.setPower(-power);
            try {
                TimeUnit.MILLISECONDS.sleep(timeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void mecDrive(float leftY, float leftX, float rightX, double threshold) {

        if (abs(leftY) > threshold) {
            Y1 = leftY;
        } else {
            Y1 = 0;
        }
        if (abs(leftX) > threshold) {
            X1 = leftX;
        } else {
            X1 = 0;
        }
        if (abs(rightX) > threshold) {
            X2 = rightX;
        } else {
            X2 = 0;
        }

        leftDrive.setPower(Y1 - X2 - X1);
        backLDrive.setPower(Y1 - X2 + X1);
        rightDrive.setPower(Y1 + X2 + X1);
        backRDrive.setPower(Y1 + X2 - X1);

    }

    public void moveTime(String dir, double power, long timeMS) {

        if(dir == "left") {

            backLDrive.setPower(power);
            leftDrive.setPower(-power);
            backRDrive.setPower(-power);
            rightDrive.setPower(power);
            try {
                TimeUnit.MILLISECONDS.sleep(timeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            moveRobot(0,0,0);

        } else if(dir == "right") {

            backLDrive.setPower(-power);
            leftDrive.setPower(power);
            backRDrive.setPower(power);
            rightDrive.setPower(-power);
            try {
                TimeUnit.MILLISECONDS.sleep(timeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveRobot(0,0,0);

        } else if(dir == "forwards") {

            backLDrive.setPower(power);
            leftDrive.setPower(power);
            backRDrive.setPower(power);
            rightDrive.setPower(power);
            try {
                TimeUnit.MILLISECONDS.sleep(timeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveRobot(0,0,0);

        } else if(dir == "backwards") {

            backLDrive.setPower(-power);
            leftDrive.setPower(-power);
            backRDrive.setPower(-power);
            rightDrive.setPower(-power);
            try {
                TimeUnit.MILLISECONDS.sleep(timeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveRobot(0, 0, 0);

        } else if (dir == "sliderUp"){

            slide.setPower(power);
            try{
                TimeUnit.MILLISECONDS.sleep(timeMS);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            moveRobot(0,0,0);
        } else if (dir == "slidedown"){

            slide.setPower(-power);
            try{
                TimeUnit.MILLISECONDS.sleep(timeMS);
            }catch (InterruptedException e){
                e.printStackTrace();
          }
         moveRobot(0,0,0);
      } else if (dir == "glyphup"){

        Arm.setPower(power);
        try{
            TimeUnit.MILLISECONDS.sleep(timeMS);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        moveRobot(0,0,0);
    } else if (dir == "glyphdown"){

        Arm.setPower(-power);
        try{
            TimeUnit.MILLISECONDS.sleep(timeMS);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        moveRobot(0,0,0);
            }
    }
    
    public void manualDrive()  {
        // In this mode the Left stick moves the robot fwd & back, and Right & Left.
        // The Right stick rotates CCW and CW.

        //  (note: The joystick goes negative when pushed forwards, so negate it)
        setAxial(-myOpMode.gamepad1.left_stick_y);
        setLateral(myOpMode.gamepad1.left_stick_x);
        setYaw(myOpMode.gamepad1.right_stick_x);

    }
    
    public void newManualDrive() {

        double r = Math.hypot(-myOpMode.gamepad1.left_stick_x, -myOpMode.gamepad1.left_stick_y);
        double robotAngle = Math.atan2(-myOpMode.gamepad1.left_stick_y, -myOpMode.gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = -myOpMode.gamepad1.right_stick_x;
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        leftDrive.setPower(v1);
        rightDrive.setPower(v2);
        backLDrive.setPower(v3);
        backRDrive.setPower(v4);
        
    }

    public void moveRobot(double axial, double lateral, double yaw) {

        setAxial(axial);
        setYaw(yaw);
        setLateral(lateral);
        moveRobot();

    }

    public void moveRobot() {
        // calculate required motor powers to acheive axis motions
        double backL = driveAxial - driveLateral + driveYaw;
        double backR = driveAxial + driveLateral - driveYaw;
        double left = driveAxial + driveLateral + driveYaw;
        double right = driveAxial - driveLateral - driveYaw;

        // normalize all motor speeds so no values exceeds 100%.
        double max = Math.max(Math.abs(left), Math.abs(right));
        max = Math.max(max, Math.abs(backL));
        max = Math.max(max, Math.abs(backR));
        if (max > 1.0)
        {
            backR /= max;
            backL /= max;
            right /= max;
            left /= max;
        }

        // Set drive motor power levels.
        backLDrive.setPower(backL);
        backRDrive.setPower(backR);
        leftDrive.setPower(left);
        rightDrive.setPower(right);

        // Display Telemetry
        myOpMode.telemetry.addData("Axes  ", "A[%+5.2f], L[%+5.2f], Y[%+5.2f]", driveAxial, driveLateral, driveYaw);
        myOpMode.telemetry.addData("Wheels", "L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]", left, right, backL, backR);

    }

    public void dpadDrive(double power) {

        if(myOpMode.gamepad1.dpad_left) {

            backLDrive.setPower(power);
            leftDrive.setPower(-power);
            backRDrive.setPower(-power);
            rightDrive.setPower(power);

        } else if(myOpMode.gamepad1.dpad_right) {

            backLDrive.setPower(-power);
            leftDrive.setPower(power);
            backRDrive.setPower(power);
            rightDrive.setPower(-power);

        } else if(myOpMode.gamepad1.dpad_up) {

            backLDrive.setPower(power);
            leftDrive.setPower(power);
            backRDrive.setPower(power);
            rightDrive.setPower(power);

        } else if(myOpMode.gamepad1.dpad_down) {

            backLDrive.setPower(-power);
            leftDrive.setPower(-power);
            backRDrive.setPower(-power);
            rightDrive.setPower(-power);

        }
    }

    public void setAxial(double axial)      {driveAxial = Range.clip(axial, -1, 1);}
    public void setLateral(double lateral)  {driveLateral = Range.clip(lateral, -1, 1); }
    public void setYaw(double yaw)          {driveYaw = Range.clip(yaw, -1, 1); }

    public void setAxialtoLateral(double axial)          {driveLateral = Range.clip(axial, -1, 1);}
    public void setLateraltoAxial(double lateral)        {driveAxial = -Range.clip(lateral, -1, 1);}
    public void setChangeYaw(double yaw)                 {driveYaw = Range.clip(yaw, -1, 1);}

    public void setMode(DcMotor.RunMode mode ) {

        leftDrive.setMode(mode);
        rightDrive.setMode(mode);
        backLDrive.setMode(mode);
        backRDrive.setMode(mode);
        slide.setMode(mode);

    }

    public int[] getCurrentPosition() {

        positions = new int[]{

                leftDrive.getCurrentPosition(),

                rightDrive.getCurrentPosition(),

                backLDrive.getCurrentPosition(),

                backRDrive.getCurrentPosition(),

                slide.getCurrentPosition(),

        };

        return positions;

    }

    {/*  motor integer numbers for \/ getCurrentPosition(int motor)
            1(l)        2(r)
                5(slide)



            3(bl)       4(br)

     */}

    public int getCurrentPosition(int motor) {

        motor = motor-1;

        return positions[motor];

    }
}
