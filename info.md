# Detecting the minerals in Autonomous

The main goal of our image recognition is to detect the position of the minerals using a webcam(Logitech C920), in order to knock the gold of its post.

## Attempt 1 - Vuforia Object Scanner

Since last year, we had used Vuforia to detect and identify the VuMarks, our first idea this year was to extend Vuforias usage into the third dimension. Vuforia has a built in 3D object recognition in their Vuforia Object Scanner Android app. So, we created a model of the gold and silver minerals and uploaded them to a Vuforia target database.

![](att1-gold.PNG)

However, the implementation of detecting these objects in an android app was not well documented online. I attempted to repurpose the FTC SDK's built-in Vuforia recognizer to use the 3D models instead of just VuMarks. This proved to be quite challenging for me, though, so through quests online I stumbled on what would be our next attempt.

## Attempt 2 - Vuforia → OpenCV → EnderCV 

OpenCV has been more well documented by FTC teams than Vuforia has. There are several libraries and tools which I discovered which had the possibility of working for detecting the minerals. One library, EnderCV, developed by team 5484 the EnderBots(who may or may not be at this competition), had all of the basics which I was looking to implement. Their library has classes, which, with one implementation could detect colors in an image and report their relative location to an OpMode. However, the major drawback, which ended up not allowing us to use this fantastic library, is my relative failure to figure out how to use it with a webcam. All of its vuforia-calling methods are in several factories and generators, and I could not for the life of me figure out how to change what should have been 3 lines of code to use the webcam instead of the phone. In the documentation to EnderCV I did find the program GRIP, which I would use for the next attempt

## Attempt 3 - Vuforia → GRIP

GRIP is the Graphically Represented Image Processing Engine. It provides a graphical interface, in which you can transform an input to detect certain things. The wide variety of processing GRIP has availble, and the ability to export one's transformations into Java code made it seem like a likely contender for being effective. I first created a series of transformations which would isolate certain aspects of the gold and silver minerals.

1. Blur to remove noise
![](att2-grip.PNG)

2. Isolate gold mineral by hue
![](att2-grip2.PNG)

3. Apply mask and track contours
![](att2-grip3.PNG)

GRIP was going well, when FTC changed our whole gameplan with the release of FTC SDK 4.2.

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

```java
/*
    we create a new thread, separate from the main thread in order to run the vision processing while the robot is moving.
    the thread will only run until a valid position is found. this helps with an issue with detecting more than 3 objects or less than 3.
    however, the main thread will only check to see if the thread has returned a result after each move.
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
