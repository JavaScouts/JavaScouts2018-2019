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
    String pos, finalpos, elimpos = "UNKNOWN";
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
                        pos = tracking.getPosition();
                        if (!pos.equals("UNKNOWN")) {
                            finalpos = pos;
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

            if(gamepad2.left_bumper && !finalpos.equals("UNKNOWN")) {
                elimpos = "UNKNOWN";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (opModeIsActive() && tracking.tfod != null) {
                            while (elimpos.equals("UNKNOWN")) {
                                pos = tracking.getPositionbyElimination(cycles);
                                if (!pos.equals("UNKNOWN")) {
                                    elimpos = pos;
                                }
                            }
                        }
                    }
                }).start();
                telemetry.addLine("Thread created with elim strategy.");
                telemetry.update();
            }

            if(tracking.getVerbose()) {

                List<Recognition> recognitions = tracking.getDebug();
                for(int i=0; i < recognitions.size(); i++) {

                    telemetry.addData("Conf: "+i,
                            ""+ recognitions.get(i).getConfidence()+
                            ", "+recognitions.get(i).getLabel()+
                            ", "+recognitions.get(i).getLeft() +
                            ", "+recognitions.get(i).getTop());
                }
            }

            telemetry.update();

        }

    }

}

