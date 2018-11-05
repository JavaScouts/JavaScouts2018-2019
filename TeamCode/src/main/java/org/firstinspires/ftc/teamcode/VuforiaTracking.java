package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

/**
 * Created by Liam on 9/13/2018.
 */
@TeleOp(name="VuforiaTracking")
public class VuforiaTracking extends LinearOpMode {

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    RobotHardware robot = new RobotHardware();

    WebcamName webcamName;

    @Override
    public void runOpMode() {

        /*robot.init(hardwareMap, this);

        robot.rightDrive.setDirection(DcMotor.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotor.Direction.REVERSE);
        */
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "Abd1Glv/////AAAAGZmYidRnmEU+hDYHKdO9PSlKOkMzut3jACYyynIBk/aI/uy1g5waDFv0hQlDLLoROL/RcHOLRIXYoEeTj0xW6JPELJd94fYr72YQ8A/hFOPLDO1UuM1je+Y2KbABDDilKaqShhHzfPinH1M7NLA7aZCwUk4ZRiDsLcB9f4hKVa9g//sUxId3KFb4GW438tD8t/xdZfcLcm+vP4yREaW6NirbqzCwTvp22/2eDuKIHGvVn3Fju4PcqCYYaFTvubLX2iXOwrEJWEVD+qtvQsqr5Dk+ClaUlv9amad/14aEU5jUohRU04PUlEgtaGSfLGNBmOXW14ugnipSbC1Lr423HlaaGgNjseX+D3nbZlX9a2Zo\n";

        parameters.cameraName = webcamName;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);



        if(ClassFactory.getInstance().canCreateTFObjectDetector()) {
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }



        telemetry.addData("Initialization", "Complete.");
        telemetry.update();



        waitForStart();



        if(opModeIsActive()) {

            if(tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {

                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                    if (updatedRecognitions != null) {

                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        if (updatedRecognitions.size() == 3) {

                            //-1 means not yet detected

                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;

                            //loop through all the found objects, and label each with their respective x values(there should be three distinct)

                            for (Recognition recognition : updatedRecognitions) {

                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                    telemetry.addData("Gold Mineral Angle", recognition.estimateAngleToObject(AngleUnit.DEGREES));
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
                                    telemetry.addData("Gold Mineral Position", "Left");
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Right");
                                } else {
                                    telemetry.addData("Gold Mineral Position", "Center");
                                }
                            }
                        }

                        telemetry.update();

                    }

                }

            }

        }

    }

}
