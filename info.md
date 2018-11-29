# Information on Tensorflow and Mecanum Algorithms

The main goals of the algorithms detailed on the following pages is to:
1. Make the robot more effective in autonomous
2. Make the robot easier for the drivers to control

The first goal is achieved through the Tensorflow algorithm. By processing the image gathered by the webcam and using Tensorflow to detect the position of the gold mineral. By using this data to determine which mineral to drive to and knock off, we score more points more reliably in autonomous than with another method, say color sensor.

The second goal is achieved through the Mecanum algorithm. Mecanum wheels provide omnidirectional movement, and without the proper algorithm a driver would not be able to realize its full potential. True omnidirectional and omnirotational movement can be achieved only through combining a variety of driver inputs in order to produce the nessecary driver output. Our driving system is based off of the movement of most games nowadays, where moving is controlled using the left stick and looking around (or in our case, rotating) is controlled using the right stick. Our algorithm makes it easy for the driver to do this!

# Evolution of the image processing system

The main goal of our image recognition is to detect the position of the minerals using a webcam(Logitech C920), in order to knock the gold of its post.

## Attempt 1 - Vuforia Object Scanner

Since last year, we had used Vuforia to detect and identify the VuMarks, our first idea this year was to extend Vuforias usage into the third dimension. Vuforia has a built in 3D object recognition in their Vuforia Object Scanner Android app. So, we created a model of the gold and silver minerals and uploaded them to a Vuforia target database.

![](att1-gold.PNG)

However, **the implementation of detecting these objects in an android app was not well documented online.** I attempted to repurpose the FTC SDK's built-in Vuforia recognizer to use the 3D models instead of just VuMarks. This proved to be quite challenging for me, though, so through quests online I stumbled on what would be our next attempt.

## Attempt 2 - Vuforia → OpenCV → EnderCV 

OpenCV has been more well documented by FTC teams than Vuforia has. There are several libraries and tools which I discovered which had the possibility of working for detecting the minerals. One library, EnderCV, developed by team 5484 the EnderBots(who may or may not be at this competition), had all of the basics which I was looking to implement. Their library has classes, which, with one implementation could detect colors in an image and report their relative location to an OpMode. 

**However, the major drawback, which ended up not allowing us to use this fantastic library, is my relative failure to figure out how to use it with a webcam.** All of its vuforia-calling methods are in several factories and generators, and I could not for the life of me figure out how to change what should have been 3 lines of code to use the webcam instead of the phone. In the documentation to EnderCV I did find the program GRIP, which I would use for the next attempt.

## Attempt 3 - Vuforia → GRIP

GRIP is the Graphically Represented Image Processing Engine. It provides a graphical interface, in which you can transform an input to detect certain things. The wide variety of processing GRIP has availble, and the **ability to export one's transformations into Java code** made it seem like a likely contender for being effective. I first created a series of transformations which would isolate certain aspects of the gold and silver minerals.

### 1. Blur to remove noise

![](att2-grip.PNG)

### 2. Isolate gold mineral by hue**

![](att2-grip2.PNG)

### 3. Apply mask and track contours**

![](att2-grip3.PNG)

GRIP was going well, when FTC changed our whole gameplan with the release of FTC SDK 4.2.

## Attempt 3.5 - Vuforia → OpenCV → DogeCV

Developed by the mechanical masterminds, DogeCV is another image recognition which claimed to recognize colors. However, this was merely a side project as I concentrated on GRIP.

## Attempt 4 - Vuforia → Tensorflow Model

SDK 4.2 introduced support for Google's Tensorflow Lite library to detect, with a pre-trained machine learning model, gold and silver minerals. At first, we simply tried to finish up the GRIP system. However, taking too long was a major issue - our competition was in around 3 weeks. Plus, an official FTC system for detecting the minerals would make our lives in the future much safer, with official updates and bugfixes.

The only issue with Tensorflow is that it is not compatible with Android versions below 6.0. So, we had a decision to make: continue with GRIP and sacrifice the benefits of TF, or buy a whole new phone. Our choice: buy Moto G4 Play phones.

With the new phones we set about incoroporating TF into our autonomous programs. Thanks to the sample opmode, ConceptTensorFlowObjectDetectionWebcam we had an official source off of which we could build our program. As usual, we ran the sample to see if it would work with our system. We had to learn to use a USB hub to connect both the webcam and REV expansion hub. As it worked, we wanted to streamline and make more efficient the entire process.

Our choice was to place all of Vuforia and Tensorflow into a single class, which could be called as an object and used in any other class. VuforiaTracking.java contains the nessecary functions to initialize Vuforia, initialize Tensorflow, activate Tensorflow, and a function to get the position of the gold.

VuforiaTracking.java
```java
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
                //loop through all the found objects, and label each with their respective x values
                //(there should be three distinct)
                for (Recognition recognition : updatedRecognitions) {
                    if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                        goldMineralX = (int) recognition.getLeft();
                    } else if (silverMineral1X == -1) {
                        silverMineral1X = (int) recognition.getLeft();
                    } else {
                        silverMineral2X = (int) recognition.getLeft();
                    }
                }
                        /*if all are detected and are distinct, 
                        if gold is to the left of both silver 1 and 2, it is in position left. 
                        if gold is to the right of both silver 1 and 2, it is in position right. 
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
}
```
This function returns a string with the position of the GOLD mineral, and this is used in the Autonomous programs to figure out which position to go to. However, if running the Autonomous program in a LinearOpMode, one can only run this getPosition() function once after every move. This system would be very unreliable. We wrote a debug program to see which position the mineral is in *checked every game tick.* We found that it sometimes flickers back and forth between "THE CORRECT RESULT" and "UNKNOWN". If we had checked it only once after every move, we would have had quite a high probability of receiving the wrong result.

So, in each Autonomous program we use multi-threading to run the vision computation in the background. This allows us to continuously run the program *while the robot is still performing moves*. Even more importantly, we can run the thread *just* until it returns a result. In this way, it is almost guaranteed that we will receive a result by the time we need to act on it.

Autonomous1.java
```java
/*
    we create a new thread, 
    separate from the main thread in order to run 
    the vision processing while the robot is moving.
    the thread will only run until a valid position is found. 
    this helps with an issue with detecting more than 3 objects or less than 3.
    however, the main thread will only check to see 
    if the thread has returned a result after each move.
*/

new Thread(new Runnable() {
    @Override
    public void run() {
        if (opModeIsActive() && tracking.tfod != null) {
            POSITION_GOLD = "UNKNOWN"; //combat NullPointerException
                while (POSITION_GOLD.equals("UNKNOWN")) {
                    pos = tracking.getPosition();
                        if (!pos.equals("UNKNOWN")) {
                            POSITION_GOLD = pos;
                        }
                    }
                }
            }
}).start();
``` 

This code is one of the most vital in the entire program. After it receives it's result, it places it into a String POSITION_GOLD. When the main thread is free(after a move), it checkes POSITION_GOLD, which after several checks will almost definitely have the result. See this awesome system in action during one of our games.

## Future goals

- Right now, we have tape on the top of the webcam to try and stop it from seeing minerals in the background (facing the crater). We are going to perfect the position of the tape.
- I am planning to add more exception handling. For the *very rare* case that tensorflow does detect less than 3 or greater than 3 objects, it will not currently send a result.
  - I have an idea to improve the probabilities of correct results if these cases happen.
  ```
  if(numberDetected == 2)
    if(posG1 != -1)
      if(posS1 != -1 || posS2 != -1)
        if(posG1 > posS1 && posS1 != -1)
          G1 is either in position 2 or 3
        same for G2
        if(posG1 < posS1 && posS1 != -1)
          G1 is either in position 1 or 2
  ```
  - This will improve our probability from 33% to 50%, a significant improvement.
  ```
  if(numDetected > 3)
    find the objects with the least confidence, and delete until we only have 3 left
  ```
  - This will get us down to a hopefully correct 3 object system.
  - An issue: we usually can get down to a 3 object system after a certain amount of time. So, we make var called minimumCycles that has to be reached before the actual exceptions can be used.
  
  
# Information on mecanum drive algorithm

Philbot's code for omnidirectional drive of a three-wheeled system gave me the inspiration to try and develop it for a mecanum system. The code for the algorithm is as follows.

```java
void manualDrive() {
    // In this mode the Left stick moves the robot fwd & back, and Right & Left.
    // The Right stick rotates CCW and CW.
    setAxial(-myOpMode.gamepad1.left_stick_y);
    setLateral(myOpMode.gamepad1.left_stick_x);     
    setYaw(myOpMode.gamepad1.right_stick_x);
}

void moveRobot() {
    // calculate required motor powers to acheive axis motions
    double backL = driveAxial - driveLateral + driveYaw;
    double backR = driveAxial + driveLateral - driveYaw;
    double left = driveAxial + driveLateral + driveYaw;
    double right = driveAxial - driveLateral - driveYaw;

    // normalize all motor speeds
    double max = Math.max(Math.abs(left), Math.abs(right));
    max = Math.max(max, Math.abs(backL));
    max = Math.max(max, Math.abs(backR));
    if (max > 1.0) {
        backR /= max;
        backL /= max;
        right /= max;
        left /= max;
    }

    // set drive motor powers
    backLDrive.setPower(backL);
    backRDrive.setPower(backR);
    leftDrive.setPower(left);
    rightDrive.setPower(right);

    // Display Telemetry
    myOpMode.telemetry.addData("Axes ", "A[%+5.2f], L[%+5.2f], Y[%+5.2f]", 
            driveAxial, driveLateral, driveYaw);
    myOpMode.telemetry.addData("Power", "L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]",
            left,right,backL,backR);

}
```

This code may look like a bunch of math, because that is exactly what it is.
Let us take a look at the cases which we used to determine how to create this algorithm.
It is important to note that we drive by translating with the left joystick, and rotating with the right joystick. In this way, controlling the robot is as easy as controlling a first-person-shooter type game (aim with right, move with left).
Also, it is important to note that the directions axial, lateral, and yaw can be represented as below:


                      (+ axial)     ⟳ = (+ yaw)  ⟲ = (- yaw)
                          ↑
          (- lateral) ← robot → (+ lateral)
                          ↓
                      (- axial)
  

There have been many studies on the kinematics (motion science) of mecanum wheels, so here is a comprehensive diagram, sourced from a [Chinese study](https://research.ijcaonline.org/volume113/number3/pxc3901586.pdf)(for the link to this study, see our booth):

![](mecanum_kinematics.png)

### Driving Straight

To drive straight, all motors should be outputted with the same, positive power. Since forwards and backwards movement is controlled by the axial variable, when we attempt to drive forwards, a positive axial power will be set. To drive all of the motors in the correct way , all motor powers should first have a positive axial. After this step, we have:

```java
double backL = driveAxial;
double backR = driveAxial;
double left = driveAxial;
double right = driveAxial;
```

### Driving Right

To drive right, according to the diagram, the front-left and back-right motors should move forwards, and the back-left and front-right motors should move backwards. So, when the algorithm receives input of lateral movement, it should move the above mentioned motors. After this step, we have:

```java
double backL = driveAxial - driveLateral;
double backR = driveAxial + driveLateral;
double left = driveAxial + driveLateral;
double right = driveAxial - driveLateral;
```

Testing this stage allows us to see that the translatative aspect of our drive-system works as planned.

### Turning Right

To turn right, according to the diagram, the left motors should move backwards, and the right motors should move forwards. So, when the algorithm receives input of yaw movement, it should move the motors mentioned above in the way mentioned above. After this step, we have:
```java
double backL = driveAxial - driveLateral + driveYaw;
double backR = driveAxial + driveLateral - driveYaw;
double left = driveAxial + driveLateral + driveYaw;
double right = driveAxial - driveLateral - driveYaw;
```

A watchful observer may notice that this is the final algorithm, used in the actual code.

### What about the reverses?

Well, if a negative is passed into axial, lateral, or yaw, it should be processed the same way as the positives and still produce the correct outputs. A simple test of this proved that indeed, the algorithm worked!

### Fixing issues

Testing this program at this stage should make anyone watching realize that something is wrong. Two lines of code at the end of running the algorithm, the telemetry, will show the user what powers the motors are receiving. Oh no! Some of them are greater than one, and even if they are different, they will be rounded down to one. 

```java
double max = Math.max(Math.abs(left), Math.abs(right));
    max = Math.max(max, Math.abs(backL));
    max = Math.max(max, Math.abs(backR));
    if (max > 1.0) {
        backR /= max;
        backL /= max;
        right /= max;
        left /= max;
}
```

Of course, we only need to normalize if some values are greater than one, so `if (max > 1.0) {...}` will streamline things.
With that, the mecanum drive algorithm works completely, and you can see it in action on the field!

## Questions? Contact Javascouts in the booth or at javascouts@gmail.com!
  
        
      
  
