# Detecting the minerals in Autonomous

The main goal of our image recognition is to detect the position of the minerals, in order to knock the gold of its post.

## Attempt 1 - Vuforia Object Scanner

Since last year, we had used Vuforia to detect and identify the VuMarks, our first idea this year was to extend Vuforias usage into the third dimension. Vuforia has a built in 3D object recognition in their Vuforia Object Scanner Android app. So, we created a model of the gold and silver minerals and uploaded them to a Vuforia target database.

![](Images/att1-gold.png)

However, the implementation of detecting these objects in an android app was not well documented online. I attempted to repurpose the FTC SDK's built-in Vuforia recognizer to use the 3D models instead of just VuMarks. This proved to be quite challenging for me, though, so through quests online I stumbled on what would be our next attempt.

## Attempt 2 - Vuforia → OpenCV

OpenCV has been more well documented by FTC teams than Vuforia has. There are several libraries /
