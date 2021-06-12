# Paint4
An Android Editor application built with Java, OpenCV, and Firebase.

# What I Learned
Save images to gallery.
Upload images from gallery.
Using and converting Mats and Bitmaps.
Converting between RGBA and RGB when saving/loading images.
Scaling to different screen sizes and screen orientaions.
Downloading and uploading images to Firebase storage.
Using a Thread to autosave and upload to firebase in the background.
Implementing drawing with OpenCV circles to challenge myself.

# Extra Info:

Save button: 	Saves to photos.
Load button: 	Loads from photos.
'+' '-' button:	Changes brush size.
x button:	Clears the image.
Pencil	:	Changes to white color.
Eraser:		Changes to black color.
Color wheel:	Opens other color options.
Database autosaves to firebase storage every 5 minutes.
Database autoloads from firebase storage when app is "created".

Brief explanation of drawing algo:
1) User touches/motions on the screen. (OnTouchListener on ImageView)
2) Gets touch coordinates and draws a circle at that coordinate on the Mat.
3) Mat converted to Bitmap.
4) Displays Bitmap on ImageView.
