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
    String pos1, pos2, finalpos, elimpos = "UNKNOWN";
    int cycles = 10;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);
        tracking.preInit(hardwareMap, this);
        tracking.initVuforia();
        tracking.initTfod();
        waitForStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                finalpos = "UNKNOWN";
                if (opModeIsActive() && tracking.tfod != null) {
                    while (finalpos.equals("UNKNOWN")) {
                        pos1 = tracking.getPosition();
                        if (!pos1.equals("UNKNOWN")) {
                            finalpos = pos1;
                        }
                    }
                }
                telemetry.addLine("Thread created with normal strategy.");
                telemetry.update();
            }
        }).start();

        tracking.activateTfod();

        while (opModeIsActive()) {

            //display results from main thread and separate thread.
            telemetry.addData("POS>", tracking.getPosition());
            telemetry.addData("NUM>", tracking.getNumberRecognitions());
            telemetry.addData("THREAD NO ELIM>", finalpos);
            telemetry.addData("THREAD ELIM>", elimpos);

            tracking.setVerbose(false);

            double right = -gamepad2.right_stick_y;
            robot.screw.setPower(right);

            if(gamepad2.right_bumper) {
                tracking.setVerbose(true);
                telemetry.addLine("Format: #: conf, label, x, y");
                telemetry.update();
            }

            if(gamepad2.left_bumper && !elimpos.equals("UNKNOWN")) {
                elimpos = "UNKNOWN";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (opModeIsActive() && tracking.tfod != null) {

                            elimpos = "UNKNOWN"; //combat NullPointerException

                            while (!(elimpos.equals("RIGHT") || elimpos.equals("RIGHT") || elimpos.equals("CENTER"))) {

                                pos2 = tracking.getPositionbyElimination(0);
                                telemetry.addLine();
                                telemetry.addData("Thread result", elimpos);
                                telemetry.update();
                                if (!pos2.equals("UNKNOWN")) {
                                    elimpos = pos2;
                                }
                            }
                        }
                    }
                }).start();
                telemetry.addLine("Thread created with elim strategy.");
                telemetry.update();
            }

            if(gamepad2.a && !finalpos.equals("UNKNOWN")) {
                finalpos = "UNKNOWN";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (opModeIsActive() && tracking.tfod != null) {
                            while (finalpos.equals("UNKNOWN")) {
                                pos1 = tracking.getPosition();
                                if (!pos1.equals("UNKNOWN")) {
                                    finalpos = pos1;
                                }
                            }
                        }
                    }
                }).start();
                telemetry.addLine("Thread created with regular strategy.");
                telemetry.update();
            }

            if(tracking.getVerbose()) {

                tracking.getPosition();
                List<Recognition> recognitions = tracking.getDebug();
                for(int i=0; i < recognitions.size(); i++) {

                    telemetry.addData("Data: "+i,
                            ""+ recognitions.get(i).getConfidence()+
                            ", "+recognitions.get(i).getLabel()+
                            ", "+recognitions.get(i).getLeft() +
                            ", "+recognitions.get(i).getTop());

                }
                telemetry.addData("ANGLE TO PROMINENT GOLD MINERAL",tracking.getAngleToGold(recognitions));
            }

            telemetry.update();

        }

    }

}

