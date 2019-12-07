# Paint4

https://github.com/gevo222/Paint4	(Clone to android studio)

If you just want to see code without cloning:
MainActivity: 	https://github.com/gevo222/Paint4/blob/master/app/src/main/java/com/example/paint4/MainActivity.java
XML: 		https://github.com/gevo222/Paint4/blob/master/app/src/main/res/layout/activity_main.xml
Manifest: 	https://github.com/gevo222/Paint4/blob/master/app/src/main/AndroidManifest.xml


Note: I would recommend running the app on an actual android phone if possible. 
The android emulators are slow and take longer to process the onTouch coordinates and circle drawing/
This can result in tasks being skipped, and ultimately missing some circles.


Original List of Requirments:

-Pencil and eraser with my own algorigthm using OpenCV.


New Features Since POC:

-Pallete Button: Show/Hide tools for more space

-Color Wheel Button: Show/Hide color options

-Color options (Red, Blue, Green) [More can be added fairly easily]

-Database: Autosave every 5 minutes [Thread], Autoload from database onCreate




App Instructions:
*If you want the database functions to work on your end you might need to
-Connect to firebase to get the database working on your android studio.	Tools->Firebase->Storage->Connect
-Use your own FireBase Cloud Store. 

1) Run app. 
2) Draw.
3) Click the Pallete button on the bottom right to open more tools.




Tools:

Save button: 	Saves to photos.

Load button: 	Loads from photos.

'+' '-' button:	Changes brush size.

x button:	Clears the image.

Pencil	:	Changes to white color.

Eraser:		Changes to black color.

Color wheel:	Opens other color options.

Database autosaves to firebase storage every 5 minutes (can tweak the sleep timer on the thread to change that)
Database autoloads from firebase storage when app is "created"




Brief explanation of drawing algo:
1) User touches/motions on the screen. (OnTouchListener on ImageView)
2) Gets touch coordinates and draws a circle at that coordinate on the Mat.
3) Mat converted to Bitmap.
4) Displays Bitmap on ImageView.
