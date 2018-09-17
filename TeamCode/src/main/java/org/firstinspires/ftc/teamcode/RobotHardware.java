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

public class RobotHardware {

    //initialize motors
    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor backLDrive;
    DcMotor backRDrive;

    public ColorSensor color;

    private double  driveAxial      = 0 ;   // Positive is forward
    private double  driveLateral    = 0 ;   // Positive is right
    private double  driveYaw        = 0 ;   // Positive is CCW
    private LinearOpMode myOpMode;

    private HardwareMap map;

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

        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        //STOP EVERYTHING
        moveRobot(0, 0, 0);

        myOpMode.telemetry.addData("Initialization:", "Complete!");
        myOpMode.telemetry.addData(" Left Encoder Count", leftDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" Right Encoder Count", rightDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" BLeft Encoder Count", backLDrive.getCurrentPosition());
        myOpMode.telemetry.addData(" BRight Encoder Count", backRDrive.getCurrentPosition());


        myOpMode.telemetry.update();

    }

    void manualDrive()  {
        // In this mode the Left stick moves the robot fwd & back, and Right & Left.
        // The Right stick rotates CCW and CW.

        //  (note: The joystick goes negative when pushed forwards, so negate it)
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

    private void setAxial(double axial)      {driveAxial = Range.clip(axial, -1, 1);}
    private void setLateral(double lateral)  {driveLateral = Range.clip(lateral, -1, 1); }
    private void setYaw(double yaw)          {driveYaw = Range.clip(yaw, -1, 1); }

    //not sure what the next 3 functions do but maybe they might be important even if they are not used
    public void setAxialtoLateral(double axial)          {driveLateral = Range.clip(axial, -1, 1);}
    public void setLateraltoAxial(double lateral)        {driveAxial = -Range.clip(lateral, -1, 1);}
    public void setChangeYaw(double yaw)                 {driveYaw = Range.clip(yaw, -1, 1);}

}
