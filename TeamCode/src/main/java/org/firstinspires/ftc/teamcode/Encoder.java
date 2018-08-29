package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Autonomous(name="Encodertt")
public class Encoder extends LinearOpMode {


    RobotHardware robot = new RobotHardware();



    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap, this);
        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        robot.leftDrive.setMode(DcMotor.RunMode.RESET_ENCODERS); //Resets encoders
        robot.rightDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
        robot.backLDrive.setMode(DcMotor.RunMode.RESET_ENCODERS); //Resets encoders
        robot.backRDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);



        while(robot.leftDrive.getCurrentPosition() != 0 || robot.rightDrive.getCurrentPosition() != 0
                || robot.backLDrive.getCurrentPosition() != 0 || robot.backRDrive.getCurrentPosition() != 0) { //Ensures encoders are zero
            robot.leftDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
            robot.rightDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
            robot.backLDrive.setMode(DcMotor.RunMode.RESET_ENCODERS); //Resets encoders
            robot.backRDrive.setMode(DcMotor.RunMode.RESET_ENCODERS);
            waitOneFullHardwareCycle(); //Needed within all loops
        }
        robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODERS); //Sets mode to use encoders
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODERS); //setMode() is used instead of setChannelMode(), which is now deprecated
        robot.backLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);; //Resets encoders
        robot.backRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        waitForStart();
        telemetry.addData(" Left Encoder Count", robot.leftDrive.getCurrentPosition());
        telemetry.addData(" Right Encoder Count", robot.rightDrive.getCurrentPosition());
        telemetry.addData(" BLeft Encoder Count", robot.backLDrive.getCurrentPosition());
        telemetry.addData(" BRight Encoder Count", robot.backRDrive.getCurrentPosition());
        telemetry.update();

        robot.leftDrive.setTargetPosition(5440); //Sets motor to move 1440 ticks (1440 is one rotation for Tetrix motors)
        robot.rightDrive.setTargetPosition(5440);
        robot.backLDrive.setTargetPosition(5440);
        robot.backRDrive.setTargetPosition(5440);
        robot.leftDrive.setPower(.5);
        robot.rightDrive.setPower(.5);
        robot.backLDrive.setPower(.5);
        robot.backRDrive.setPower(.5);



        while(robot.leftDrive.getCurrentPosition() < robot.leftDrive.getTargetPosition() || robot.rightDrive.getCurrentPosition() <  robot.rightDrive.getTargetPosition() ||
                robot.backLDrive.getCurrentPosition() <  robot.backLDrive.getTargetPosition() || robot.backRDrive.getCurrentPosition() <  robot.backRDrive.getTargetPosition()) { //While target has not been reached
            waitOneFullHardwareCycle(); //Needed within all loops
        }

        robot.leftDrive.setTargetPosition(10000); //Sets motor to move 1440 ticks (1440 is one rotation for Tetrix motors)
        robot.backLDrive.setTargetPosition(10000);
        robot.leftDrive.setPower(.5);
        robot.rightDrive.setPower(0);
        robot.backLDrive.setPower(0.5);
        robot.backRDrive.setPower(0);

        while(robot.leftDrive.getCurrentPosition() < robot.leftDrive.getTargetPosition() || robot.rightDrive.getCurrentPosition() <  robot.rightDrive.getTargetPosition() ||
                robot.backLDrive.getCurrentPosition() <  robot.backLDrive.getTargetPosition() || robot.backRDrive.getCurrentPosition() <  robot.backRDrive.getTargetPosition()) { //While target has not been reached
            waitOneFullHardwareCycle(); //Needed within all loops
        }



        robot.leftDrive.setPower(0);
        robot.rightDrive.setPower(0);
        robot.backLDrive.setPower(0);
        robot.backRDrive.setPower(0);







    }
}