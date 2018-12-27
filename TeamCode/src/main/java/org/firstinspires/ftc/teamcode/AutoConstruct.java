package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;

public class AutoConstruct extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();
    private ElapsedTime runtime = new ElapsedTime();
    String POSITION_GOLD, pos = "UNKNOWN";
    @Override
    public void runOpMode() {
        robot.init(hardwareMap, this);

        robot.gyro.calibrate();
        // make sure the gyro is calibrated before continuing

        while (!isStopRequested() && robot.gyro.isCalibrating()) {
            sleep(50);
            idle();
        }

        //init vuforia and tfod
        tracking.preInit(hardwareMap, this);
        tracking.initVuforia();
        tracking.initTfod();

        robot.setMode(STOP_AND_RESET_ENCODER);

        robot.setMode(RUN_USING_ENCODER);

        //same as waitForStart(), but displays robot heading
        while (!isStarted()) {
            telemetry.addData(">", "Robot Heading = %d", robot.gyro.getIntegratedZValue());
            telemetry.update();
        }

        robot.gyro.resetZAxisIntegrator();

        ImageRecog();

    }

    public void ImageRecog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (opModeIsActive() && tracking.tfod != null) {

                    POSITION_GOLD = "UNKNOWN"; //combat NullPointerException

                    while (POSITION_GOLD.equals("UNKNOWN")) {

                        pos = tracking.getPositionByTwo();

                        if (!pos.equals("UNKNOWN")) {
                            POSITION_GOLD = pos;

                        }
                    }
                }
            }
        }).start();

        tracking.activateTfod();
    }
}
