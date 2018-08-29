package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Liam on 8/16/2018.
 */

@Autonomous(name="Goodencoder")

public class GoodEncoder extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    private ElapsedTime runtime = new ElapsedTime();



    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap, this);

        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        encoderDrive( 0.5, 0.5, 10, 10, 5);

    }

    public void encoderDrive(double Lspeed, double Rspeed, double Inches, double timeoutS, double rampup) throws InterruptedException {

        double     COUNTS_PER_MOTOR_REV    = 1120 ;    //Set for NevRest 20 drive. For 40's change to 1120. For 60's 1680
        double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is the ratio between the motor axle and the wheel
        double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
        double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                (WHEEL_DIAMETER_INCHES * 3.1415);
        //initialise some variables for the subroutine
        int newLeftTarget;
        int newRightTarget;
        // Ensure that the opmode is still active
        // Determine new target position, and pass to motor controller we only do this in case the encoders are not totally zero'd
        newLeftTarget = (robot.leftDrive.getCurrentPosition() + robot.backLDrive.getCurrentPosition() )/2 + (int)(Inches * COUNTS_PER_INCH);
        newRightTarget = (robot.rightDrive.getCurrentPosition() + robot.backRDrive.getCurrentPosition() )/2 + (int)(Inches * COUNTS_PER_INCH);
        // reset the timeout time and start motion.
        runtime.reset();
        // keep looping while we are still active, and there is time left, and neither set of motors have reached the target
        while ( (runtime.seconds() < timeoutS) &&
                (Math.abs(robot.leftDrive.getCurrentPosition() + robot.backLDrive.getCurrentPosition()) /2 < newLeftTarget  &&
                        Math.abs(robot.rightDrive.getCurrentPosition() + robot.backRDrive.getCurrentPosition())/2 < newRightTarget)) {
            double rem = (Math.abs(robot.leftDrive.getCurrentPosition()) + Math.abs(robot.backLDrive.getCurrentPosition())+Math.abs(robot.rightDrive.getCurrentPosition()) + Math.abs(robot.backRDrive.getCurrentPosition()))/4;
            double NLspeed;
            double NRspeed;
            //To Avoid spinning the wheels, this will "Slowly" ramp the motors up over
            //the amount of time you set for this SubRun
            double R = runtime.seconds();
            if (R < rampup) {
                double ramp = R / rampup;
                NLspeed = Lspeed * ramp;
                NRspeed = Rspeed * ramp;
            }
//Keep running until you are about two rotations out
            else if(rem > (1000) )
            {
                NLspeed = Lspeed;
                NRspeed = Rspeed;
            }
            //start slowing down as you get close to the target
            else if(rem > (200) && (Lspeed*.2) > .1 && (Rspeed*.2) > .1) {
                NLspeed = Lspeed * (rem / 1000);
                NRspeed = Rspeed * (rem / 1000);
            }
            //minimum speed
            else {
                NLspeed = Lspeed * .2;
                NRspeed = Rspeed * .2;

            }
            //Pass the seed values to the motors
            robot.leftDrive.setPower(NLspeed);
            robot.backLDrive.setPower(NLspeed);
            robot.rightDrive.setPower(NRspeed);
            robot.backRDrive.setPower(NRspeed);
        }
        // Stop all motion;
        //Note: This is outside our while statement, this will only activate once the time, or distance has been met
        robot.leftDrive.setPower(0);
        robot.rightDrive.setPower(0);
        robot.backLDrive.setPower(0);
        robot.backRDrive.setPower(0);
        // show the driver how close they got to the last target
        telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
        telemetry.addData("Path2",  "Running at %7d :%7d", robot.leftDrive.getCurrentPosition(), robot.rightDrive.getCurrentPosition());
        telemetry.update();
        //setting resetC as a way to check the current encoder values easily
        double resetC = ((Math.abs(robot.leftDrive.getCurrentPosition()) + Math.abs(robot.backLDrive.getCurrentPosition())+ Math.abs(robot.rightDrive.getCurrentPosition())+Math.abs(robot.rightDrive.getCurrentPosition())));
        //Get the motor encoder resets in motion
        robot.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //keep waiting while the reset is running
        while (Math.abs(resetC) > 0){
            resetC =  ((Math.abs(robot.leftDrive.getCurrentPosition()) + Math.abs(robot.backLDrive.getCurrentPosition())+ Math.abs(robot.rightDrive.getCurrentPosition())+Math.abs(robot.rightDrive.getCurrentPosition())));
            idle();
        }
        // switch the motors back to RUN_USING_ENCODER mode
        robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//give the encoders a chance to switch modes.
        waitOneFullHardwareCycle();
        //  sleep(250);   // optional pause after each move
    }

}

