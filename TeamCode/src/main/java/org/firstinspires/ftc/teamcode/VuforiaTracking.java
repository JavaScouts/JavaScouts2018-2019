package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.vuforia.Image;
import com.vuforia.ObjectTracker;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.sun.tools.javac.util.Constants.format;
import static org.firstinspires.ftc.robotcore.external.ClassFactory.createVuforiaLocalizer;

/**
 * Created by Liam on 9/13/2018.
 */
@TeleOp(name="PLEASE WORK")
public class VuforiaTracking extends LinearOpMode {

    RobotHardware robot = new RobotHardware();
    EnderCVTestYellow vision = new EnderCVTestYellow();

    private int centerX;
    private VuforiaLocalizer vuforia;
    private Image img,gray;
    WebcamName webcamName;

    @Override
    public void runOpMode() {

        /*robot.init(hardwareMap, this);

        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);
*/
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "Abd1Glv/////AAAAGZmYidRnmEU+hDYHKdO9PSlKOkMzut3jACYyynIBk/aI/uy1g5waDFv0hQlDLLoROL/RcHOLRIXYoEeTj0xW6JPELJd94fYr72YQ8A/hFOPLDO1UuM1je+Y2KbABDDilKaqShhHzfPinH1M7NLA7aZCwUk4ZRiDsLcB9f4hKVa9g//sUxId3KFb4GW438tD8t/xdZfcLcm+vP4yREaW6NirbqzCwTvp22/2eDuKIHGvVn3Fju4PcqCYYaFTvubLX2iXOwrEJWEVD+qtvQsqr5Dk+ClaUlv9amad/14aEU5jUohRU04PUlEgtaGSfLGNBmOXW14ugnipSbC1Lr423HlaaGgNjseX+D3nbZlX9a2Zo\n";

        parameters.cameraName = webcamName;

        this.vuforia = ClassFactory.getInstance().createVuforia(parameters);

        waitForStart();

        while(opModeIsActive()) {

            vision.processFrame(getImage());

            List<MatOfPoint> contours = vision.getContours();
            for (int i = 0; i < contours.size(); i++) {
                // get the bounding rectangle of a single contour, we use it to get the x/y center
                // yes there's a mass center using Imgproc.moments but w/e
                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                centerX = (boundingRect.x + boundingRect.width) / 2;
                telemetry.addData("contour" + Integer.toString(i),
                        String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2));
            }

        }

    }

    private Mat getImage() {

        try {

            VuforiaLocalizer.CloseableFrame frame = this.vuforia.getFrameQueue().take(); //takes the frame at the head of the queue
            long numImages = frame.getNumImages();

            for (int i = 0; i < numImages; i++) {
                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    img = frame.getImage(i);
                    break;
                }
            }

            Bitmap bm = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(img.getPixels());

            Mat tmp = new Mat(img.getWidth(), img.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(bm, tmp);
            frame.close();
            return tmp;

        } catch(InterruptedException exc){
            return null;
        }

    }

}
