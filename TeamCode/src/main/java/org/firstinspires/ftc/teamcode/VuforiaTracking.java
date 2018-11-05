package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
<<<<<<< HEAD
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.vuforia.Image;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Locale;

import static com.sun.tools.javac.util.Constants.format;
=======
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;
>>>>>>> 1b45de96f9c91a82095f03347097adc292295373

/**
 * Created by Liam on 9/13/2018.
 */
@TeleOp(name="VuforiaTracking")
public class VuforiaTracking extends LinearOpMode {

<<<<<<< HEAD
    //RobotHardware robot = new RobotHardware();
    VuforiaOpenCVBridge vuforia;
    float centerX;
=======
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    RobotHardware robot = new RobotHardware();

>>>>>>> 1b45de96f9c91a82095f03347097adc292295373
    WebcamName webcamName;

    @Override
    public void runOpMode() {

        /*robot.init(hardwareMap, this);

<<<<<<< HEAD
        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);
*/
        webcamName = hardwareMap.get(WebcamName.class, "w1");
=======
        robot.rightDrive.setDirection(DcMotor.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotor.Direction.REVERSE);
        */
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
>>>>>>> 1b45de96f9c91a82095f03347097adc292295373

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AVoYG9X/////AAABmTTi1I64gUR7oUdT0QMIZVw7tlyP8f2ssEm01N2IGE6F3ikzxOypSwQXtCSPhv8PqIRohipOk2GI+pn/+QAtwZtTPVoCId9/w9wuDgGqsO/g8HbJ9pwGfO6Rb85AM2BJa5ogWpCbXerFtp9Qb2lhvfUW50pWEFbA54SI6lbyePynx3844TgIpEhQ7jHLElcxchcrX0is8VCfhjHjV53sUsFSanRL86B7tALf8ULPz2e0hISvqV9/IoJVkeIWsRzoISGMooLydCsXUJF2QQc095ktlp0sRcekZoNcNp3+wIFzP2Em6Bce5WBvpeciVuBDy3ebgK5a3WqPB2yTmkSFuu24BGI3XbEmNVFzuQdH3//r";

        parameters.cameraName = webcamName;

<<<<<<< HEAD
        vuforia = new VuforiaOpenCVBridge(parameters);
        vuforia.start();
=======
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


>>>>>>> 1b45de96f9c91a82095f03347097adc292295373

        waitForStart();


<<<<<<< HEAD
            List<MatOfPoint> contours = vuforia.getContours();
            for (int i = 0; i < contours.size(); i++) {
                // get the bounding rectangle of a single contour, we use it to get the x/y center
                // yes there's a mass center using Imgproc.moments but w/e
                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                centerX = (boundingRect.x + boundingRect.width) / 2;
                telemetry.addData("contour" + Integer.toString(i),
                        String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2));
=======

        if(opModeIsActive()) {

            if(tfod != null) {
                tfod.activate();
>>>>>>> 1b45de96f9c91a82095f03347097adc292295373
            }

            while (opModeIsActive()) {

                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

<<<<<<< HEAD
=======
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

>>>>>>> 1b45de96f9c91a82095f03347097adc292295373
}
