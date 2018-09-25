package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.vuforia.ObjectTracker;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
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

import java.util.ArrayList;
import java.util.List;

import static com.sun.tools.javac.util.Constants.format;
import static org.firstinspires.ftc.robotcore.external.ClassFactory.createVuforiaLocalizer;

/**
 * Created by Liam on 9/13/2018.
 */
@TeleOp(name="VuforiaTracking")
public class VuforiaTracking extends LinearOpMode {

    RobotHardware robot = new RobotHardware();

    VuforiaLocalizer vuforia;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap, this);

        robot.rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "Abd1Glv/////AAAAGZmYidRnmEU+hDYHKdO9PSlKOkMzut3jACYyynIBk/aI/uy1g5waDFv0hQlDLLoROL/RcHOLRIXYoEeTj0xW6JPELJd94fYr72YQ8A/hFOPLDO1UuM1je+Y2KbABDDilKaqShhHzfPinH1M7NLA7aZCwUk4ZRiDsLcB9f4hKVa9g//sUxId3KFb4GW438tD8t/xdZfcLcm+vP4yREaW6NirbqzCwTvp22/2eDuKIHGvVn3Fju4PcqCYYaFTvubLX2iXOwrEJWEVD+qtvQsqr5Dk+ClaUlv9amad/14aEU5jUohRU04PUlEgtaGSfLGNBmOXW14ugnipSbC1Lr423HlaaGgNjseX+D3nbZlX9a2Zo\n";

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        VuforiaTrackables minerals = this.vuforia.loadTrackablesFromAsset("Minerals_OT");
        VuforiaTrackable gold = minerals.get(0);

        waitForStart();

        minerals.activate();

        while(opModeIsActive()) {

            OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) minerals.get(0).getListener()).getPose();

                if (pose != null) {

                    VectorF trans = pose.getTranslation();
                    Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                    telemetry.addData("Pose", format(pose));

                    // Extract the X, Y, and Z components of the offset of the target relative to the robot
                    double tX = trans.get(0);
                    double tY = trans.get(1);
                    double tZ = trans.get(2);

                    // Extract the rotational components of the target relative to the robot
                    double rX = rot.firstAngle;
                    double rY = rot.secondAngle;
                    double rZ = rot.thirdAngle;

                }
            }

            telemetry.update();

    }

}
