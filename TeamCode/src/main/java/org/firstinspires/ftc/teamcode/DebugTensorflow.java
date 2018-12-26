package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;


@TeleOp(name = "Debug Tensorflow")
public class DebugTensorflow extends LinearOpMode {

    //this class is mostly used to debug camera position

    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();
    String pos1, pos2, finalpos, finalpos2 = "pre-use";


    @Override
    public void runOpMode() {

        pos1 = "pre-use";
        pos2 = "pre-use";
        finalpos = "pre-use";
        finalpos2 = "pre-use";
        robot.init(hardwareMap, this);
        tracking.preInit(hardwareMap, this);
        tracking.initVuforia();
        tracking.initTfod();
        waitForStart();

        tracking.activateTfod();

        while (opModeIsActive()) {

            //display results from main thread and separate thread.
            telemetry.addData("POS>", tracking.getPosition());
            telemetry.addData("NUM>", tracking.getNumberRecognitions());
            telemetry.addData("THREAD elim>", finalpos);
            telemetry.addData("THREAD (2)>", finalpos2);

            double right = -gamepad2.right_stick_y;
            robot.screw.setPower(right);

            if(gamepad2.left_bumper && !finalpos.equals("UNKNOWN")) {
                finalpos = "UNKNOWN";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (opModeIsActive() && tracking.tfod != null) {
                            while (finalpos.equals("UNKNOWN")) {
                                pos1 = tracking.getPositionByElimination();
                                if (!pos1.equals("UNKNOWN")) {
                                    finalpos = pos1;
                                }
                            }
                        }
                    }
                }).start();
                telemetry.addLine("Thread created with elim strategy.");
            }

            if(gamepad2.right_bumper && !finalpos2.equals("UNKNOWN")) {
                finalpos2 = "UNKNOWN";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (opModeIsActive() && tracking.tfod != null) {
                            while (finalpos2.equals("UNKNOWN")) {
                                pos2 = tracking.getPositionByTwo();
                                if (!pos2.equals("UNKNOWN")) {
                                    finalpos2 = pos2;
                                }
                            }
                        }
                    }
                }).start();
                telemetry.addLine("Thread created with (2) strategy.");
            }

            telemetry.update();

        }

    }

}

