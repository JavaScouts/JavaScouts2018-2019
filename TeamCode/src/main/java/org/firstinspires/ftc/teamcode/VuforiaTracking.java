package org.firstinspires.ftc.teamcode;

import android.graphics.YuvImage;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.internal.tfod.VuforiaFrameGenerator;

/**
 * Created by Liam on 9/13/2018.
 */

public class VuforiaTracking {

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private String POSITION_GOLD;
    VuforiaLocalizer vuforia;
    TFObjectDetector tfod;
    WebcamName webcamName;
    HardwareMap hardwareMap;
    LinearOpMode om;

    VuforiaTracking() {

    }

    void preInit(HardwareMap map, LinearOpMode opMode) {

        hardwareMap = map;
        om = opMode;

    }

    void initVuforia() {

        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AVoYG9X/////AAABmTTi1I64gUR7oUdT0QMIZVw7tlyP8f2ssEm01N2IGE6F3ikzxOypSwQXtCSPhv8PqIRohipOk2GI+pn/+QAtwZtTPVoCId9/w9wuDgGqsO/g8HbJ9pwGfO6Rb85AM2BJa5ogWpCbXerFtp9Qb2lhvfUW50pWEFbA54SI6lbyePynx3844TgIpEhQ7jHLElcxchcrX0is8VCfhjHjV53sUsFSanRL86B7tALf8ULPz2e0hISvqV9/IoJVkeIWsRzoISGMooLydCsXUJF2QQc095ktlp0sRcekZoNcNp3+wIFzP2Em6Bce5WBvpeciVuBDy3ebgK5a3WqPB2yTmkSFuu24BGI3XbEmNVFzuQdH3//r";

        parameters.cameraName = webcamName;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        om.telemetry.addData("Initialization", "Vuforia Complete");
        om.telemetry.update();
    }

    void initTfod() {

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
            om.telemetry.addData("Initialization", "TFOD Complete");
            om.telemetry.update();
        } else {
            om.telemetry.addData("Sorry!", "This device is not compatible with TFOD");
            om.telemetry.update();
        }

    }

    void activateTfod() {
        if (tfod != null) {
            tfod.activate();
        }
    }

    String getPosition() {

        String result = "UNKNOWN";

        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

            if (updatedRecognitions != null) {

                om.telemetry.addData("# Object Detected", updatedRecognitions.size());

                if (updatedRecognitions.size() == 3) {

                    //-1 means not yet detected

                    int goldMineralX = -1;
                    int silverMineral1X = -1;
                    int silverMineral2X = -1;

                    //loop through all the found objects, and label each with their respective x values(there should be three distinct)

                    for (Recognition recognition : updatedRecognitions) {

                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                        } else {
                            silverMineral2X = (int) recognition.getLeft();
                        }
                    }

                            /*if all are detected, if gold is to the left of both silver 1 and 2, it is in position left
                                                   if gold is to the right of both silver 1 and 2, it is in position right
                                                   any other position is the center

                             */

                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            POSITION_GOLD = "LEFT";
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            POSITION_GOLD = "RIGHT";
                        } else {
                            POSITION_GOLD = "CENTER";
                        }
                    }
                }

            }

        }

        result = POSITION_GOLD;
        return result;

    }

}
