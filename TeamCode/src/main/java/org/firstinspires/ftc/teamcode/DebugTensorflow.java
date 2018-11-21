package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Liam on 9/8/2018.
 */

@TeleOp(name = "Debug Wheels")
public class DebugTensorflow extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    VuforiaTracking tracking = new VuforiaTracking();

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);
        tracking.preInit(hardwareMap,this);
        tracking.initVuforia();
        tracking.initTfod();

        waitForStart();

        tracking.activateTfod();

        while (opModeIsActive()) {

            telemetry.addData("POS>", tracking.getPosition());
            telemetry.addData("NUM>", tracking.getNumberRecognitions());

        }

    }

}

