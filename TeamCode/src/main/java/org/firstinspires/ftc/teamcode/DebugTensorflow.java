package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "Debug Tensorflow")
public class DebugTensorflow extends LinearOpMode {

    //this class is mostly used to debug camera position

    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();
    String pos1, pos2, finalpos, finalpos2 = "pre-use";
    double toGo;


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
        robot.gyro.calibrate();

        while (!isStopRequested() && robot.gyro.isCalibrating()) {
            sleep(50);
            idle();
        }

        telemetry.addLine("Finished!");
        telemetry.update();

        waitForStart();

        tracking.activateTfod();

        while (opModeIsActive()) {

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


            /*if(gamepad2.dpad_left) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(toGo == 0.0) {
                            toGo = tracking.getAnAngle();
                        }
                    }
                }).start();
                telemetry.addLine("Thread created to find angles.");
                telemetry.addData("angle>",toGo);


                if(gamepad2.dpad_up) {

                    int current = robot.gyro.getIntegratedZValue();
                    double next = (double) current + toGo;
                    robot.gyroTurn(next, 0.5);
                    telemetry.addData("turning to>", next);

                }

            }*/

            //display results from main thread and separate thread.
            telemetry.addData("POS>", tracking.getPosition());
            telemetry.addData("NUM>", tracking.getNumberRecognitions());
            telemetry.addData("THREAD elim>", finalpos);
            telemetry.addData("THREAD (2)>", finalpos2);

            telemetry.update();

        }

    }

}

