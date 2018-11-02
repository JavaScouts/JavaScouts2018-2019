package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.view.View;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Frame;

import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaLocalizerImpl;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 11/1/2018.
 */

public class VuforiaOpenCVBridge extends VuforiaLocalizerImpl {

    Bitmap bitmap;
    Thread vuforiaThread;
    Mat mat;
    Mat hsv = new Mat();
    Mat thresholded = new Mat();
    private List<MatOfPoint> contours = new ArrayList<>();

    public VuforiaOpenCVBridge(Parameters parameters){
        super(parameters);
    }

    public void start() {

        vuforiaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!vuforiaThread.isInterrupted()) {
                    retrieveFrame();
                }
            }
        });
        vuforiaThread.start();

    }

    public Mat outputFrame(Frame frame) {

        if (frame != null) {

            bitmap = convertFrameToBitmap(frame);

            mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

            Utils.bitmapToMat(bitmap, mat);

            mat = processFrame(mat, null);

        }

        return mat;

    }

    public void retrieveFrame() {

        if(!getFrameQueue().isEmpty()) {
            try {
                outputFrame(getFrameQueue().take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    public Mat processFrame(Mat rgba, Mat gray) {
        // First, we change the colorspace from RGBA to HSV, which is usually better for color
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);
        // Then, we threshold our hsv image so that we get a black/white binary image where white
        // is the blues listed in the specified range of values
        // you can use a program like WPILib GRIP to find these values, or just play around.
        Core.inRange(hsv, new Scalar(19, 137.58992805755395, 139.88309352517985), new Scalar(50, 255.0, 255.0), thresholded);

        // we blur the thresholded image to remove noise
        // there are other types of blur like box blur or gaussian which can be explored.
        Imgproc.blur(thresholded, thresholded, new Size(25, 25));

        // create a list to hold our contours.
        // Conceptually, there is going to be a single contour for the outline of every blue object
        // that we can find. We can iterate over them to find objects of interest.
        // the Imgproc module has many functions to analyze individual contours by their area, avg position, etc.
        contours = new ArrayList<>();
        // this function fills our contours variable with the outlines of blue objects we found
        Imgproc.findContours(thresholded, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        // Then we display our nice little binary threshold on screen

            // this draws the outlines of the blue contours over our original image.
            // they are highlighted in green.
            Imgproc.drawContours(rgba, contours, -1, new Scalar(255, 0, 255), 2, 8);


        return rgba; // display the image seen by the camera
    }

    public synchronized List<MatOfPoint> getContours() {
        return contours;
    }

}
