package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VuforiaTracking {

    //class adapted from the example tfod mineral detection class
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    int numDetected = 0;
    List<Recognition> debug;
    Boolean verbose = false;
    VuforiaLocalizer vuforia;
    TFObjectDetector tfod;
    WebcamName webcamName;
    HardwareMap hardwareMap;
    LinearOpMode om;
    Integer cycles = 0;

    //constructor
    VuforiaTracking() {

    }

    //save map and OpMode to object to use later
    void preInit(HardwareMap map, LinearOpMode opMode) {

        hardwareMap = map;
        om = opMode;

    }

    //create Vuforia using webcam as camera
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

    //create TfLite object detector
    void initTfod() {

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            //tfodParameters.minimumConfidence = 0.4;       can be used to adjust confidence, we figured 40% is a good in-between
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
            om.telemetry.addData("Initialization", "TFOD Complete");
            om.telemetry.update();
        } else {
            om.telemetry.addData("Sorry!", "This device is not compatible with TFOD"); //initially we used zte speeds but after reviewing the pros and cons we decided to switch to g4 play
            om.telemetry.update();
        }

    }

    void activateTfod() {
        if (tfod != null) {
            tfod.activate();
        }
    }

    //return a string with a position in it, if none is found return unknown
    String getPosition() {

        String result = "UNKNOWN";

        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

            if (updatedRecognitions != null) {

                numDetected = updatedRecognitions.size();

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

                    if (verbose) {

                        debug = updatedRecognitions;

                    } else {

                        debug = null;

                    }


                            /*if all are detected and are distinct, if gold is to the left of both silver 1 and 2, it is in position left
                                                   if gold is to the right of both silver 1 and 2, it is in position right
                                                   any other position is the center

                             */

                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            result = "LEFT";
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            result = "RIGHT";
                        } else {
                            result = "CENTER";
                        }
                    }
                }

            }

        }

        //in debug we encountered a nullpointerexception so these lines are probably important
        if (result != null) {
            return result;
        } else {
            return "UNKNOWN";
        }


    }

    String getPositionbyElimination(Integer elimCycles) {

        String result = "UNKNOWN";

        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

            if (updatedRecognitions != null) {

                numDetected = updatedRecognitions.size();

                if (numDetected == 3) {

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

                    if (verbose) {

                        debug = updatedRecognitions;

                    } else {

                        debug = null;

                    }
                            /*if all are detected and are distinct, if gold is to the left of both silver 1 and 2, it is in position left
                                                   if gold is to the right of both silver 1 and 2, it is in position right
                                                   any other position is the center

                             */
                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            result = "LEFT";
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            result = "RIGHT";
                        } else {
                            result = "CENTER";
                        }
                    }

                } else if (numDetected > 3) {

                    if (cycles < elimCycles) {

                        cycles++;
                        return cycles.toString() + " CYCLE COMPLETED with greater than 3 minerals seen. ";

                    }

                    Collections.sort(updatedRecognitions, new Comparator<Recognition>() {
                        @Override
                        public int compare(Recognition a, Recognition b) {
                            return Math.round(a.getTop() - b.getTop());
                        }
                    });

                    while (updatedRecognitions.size() != 3) {

                        updatedRecognitions.remove(updatedRecognitions.size() - 1);

                    }

                } else if (numDetected < 3) {

                    if (cycles < elimCycles) {

                        cycles++;
                        return cycles.toString() + " CYCLE COMPLETED with less than 3 minerals seen. ";

                    }

                    int goldMineralX = -1;
                    int silverMineral1X = -1;

                    //loop through all the found objects, and label each with their respective x values(there should be three distinct)

                    for (Recognition recognition : updatedRecognitions) {

                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                        }

                    }

                    if (goldMineralX < silverMineral1X) {
                        long r = Math.round(Math.random());
                        if (r < 0.5) {
                            return "LEFT";
                        } else {
                            return "CENTER";
                        }
                    } else {
                        long r = Math.round(Math.random());
                        if (r < 0.5) {
                            return "CENTER";
                        } else {
                            return "RIGHT";
                        }
                    }

                }

            }

        }

        //in debug we encountered a nullpointerexception so these lines are probably important
        if (result != null) {
            return result;
        } else {
            return "UNKNOWN";
        }


    }


    int getNumberRecognitions() {

        return numDetected;

    }

    List<Recognition> getDebug() {

        if (debug != null) {

            return debug;

        } else {

            return null;

        }

    }

    double getAngleToGold(List<Recognition> recognitions) {

        double result = 0;

        for (Recognition recognition : recognitions) {

            if (recognition.getLabel().equals(LABEL_SILVER_MINERAL)) {

                recognitions.remove(recognition);

            }

            Collections.sort(recognitions, new Comparator<Recognition>() {
                @Override
                public int compare(Recognition a, Recognition b) {
                    return Math.round(a.getTop() - b.getTop());
                }
            });

            while (recognitions.size() != 1) {

                recognitions.remove(recognitions.size() - 1);

            }

            result = recognitions.get(0).estimateAngleToObject(AngleUnit.DEGREES);

        }

        return result;

    }


    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

}
