package com.example.paint4;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnTouchListener {

    // Initialize OpenCV
    static {
        OpenCVLoader.initDebug();
    }


    // Declare variables
    private static final String TAG = "MainActivityy";
    Mat m;
    Bitmap bm;
    ImageView iv;
    Button btp, btm, btc;
    ImageButton bts, btl, btPen, btEra, bttools, btColor, btRed, btBlue, btGreen;
    int rad;
    final int RQS_IMAGE1 = 1;
    Scalar sc, white, black, red, blue, green;
    int width, height;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables




        sc = new Scalar(255, 255, 255);
        black = new Scalar(0, 0, 0);
        white = new Scalar(255, 255, 255);
        red = new Scalar(255, 0, 0);
        green = new Scalar(0, 255, 0);
        blue = new Scalar(0, 0, 255);


        rad = 30;
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        width = Resources.getSystem().getDisplayMetrics().widthPixels;

        btp = findViewById(R.id.button2);
        btm = findViewById(R.id.button3);
        btc = findViewById(R.id.clearButton);
        bts = findViewById(R.id.saveButton);
        btl = findViewById(R.id.loadButton);
        btPen = findViewById(R.id.pencilButton);
        btEra = findViewById(R.id.eraserButton);
        bttools = findViewById(R.id.bttools);
        btRed = findViewById(R.id.redButton);
        btBlue = findViewById(R.id.blueButton);
        btGreen = findViewById(R.id.greenButton);
        btColor = findViewById(R.id.colorButton);

        iv = (ImageView) findViewById(R.id.imageView1);
        m = Mat.zeros(height,width , CvType.CV_8UC3);
        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);



        // Listen for touches on image
        iv.setOnTouchListener(this);


        // Increase brush size
        btp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rad += 5;
            }
        });

        // Decrease brush size
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rad -= 5;
            }
        });

        // Clear the drawing
        btc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        // Pencil
        btPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc = white;

            }
        });

        // Eraser
        btEra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc = black;

            }
        });

        btRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc = red;

            }
        });
        btBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc = blue;

            }
        });
        btGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc = green;

            }
        });



        // Save image to photos
        bts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(bm);
                //saveToDB();
            }
        });

        // Load image from photos
        btl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens photo storage to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });

        allInvisible();

        btColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens edit buttons
                if (btRed.getVisibility() == View.INVISIBLE) {
                    btRed.setVisibility(View.VISIBLE);
                    btBlue.setVisibility(View.VISIBLE);
                    btGreen.setVisibility(View.VISIBLE);
                }
                else{
                    btRed.setVisibility(View.INVISIBLE);
                    btBlue.setVisibility(View.INVISIBLE);
                    btGreen.setVisibility(View.INVISIBLE);

                }

            }
        });

        bttools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens edit buttons
                if (btl.getVisibility() == View.INVISIBLE)
                    allVisible();
                else allInvisible();

            }
        });

        draw(2000, 2000);



    }

    public void allVisible(){
        btl.setVisibility(View.VISIBLE);
        bts.setVisibility(View.VISIBLE);
        btEra.setVisibility(View.VISIBLE);
        btPen.setVisibility(View.VISIBLE);
        btc.setVisibility(View.VISIBLE);
        btm.setVisibility(View.VISIBLE);
        btp.setVisibility(View.VISIBLE);
        btColor.setVisibility(View.VISIBLE);
    }

    public void allInvisible(){
        btl.setVisibility(View.INVISIBLE);
        bts.setVisibility(View.INVISIBLE);
        btEra.setVisibility(View.INVISIBLE);
        btPen.setVisibility(View.INVISIBLE);
        btc.setVisibility(View.INVISIBLE);
        btm.setVisibility(View.INVISIBLE);
        btp.setVisibility(View.INVISIBLE);
        btColor.setVisibility(View.INVISIBLE);
        btRed.setVisibility(View.INVISIBLE);
        btBlue.setVisibility(View.INVISIBLE);
        btGreen.setVisibility(View.INVISIBLE);
    }


    // Drawing
    public void draw(int x, int y) {

        // Draw circle with these params (Mat, touched point, circle radius, color, rad)
        Imgproc.circle(m, new Point(x, y), rad, sc /*new Scalar(sX, sY, sZ)*/, -50);
        // convert to bitmap
        Utils.matToBitmap(m, bm);
        // the the bitmap to image
        iv.setImageBitmap(bm);
    }

    // Save bitmap to photos as png
    private void save(Bitmap bitmap) {

        // File is named based on system time
        String filename = "paint4-" + System.currentTimeMillis() + ".png";

        // Saves image
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename, "paint4 app");


    }

   /* private void saveToDB(Bitmap bitmap){
        // Get the data from an ImageView as bytes

        StorageReference latestRef = mStorageRef.child("latest.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = latestRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailue: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + taskSnapshot.getMetadata());
            }
        });
    }*/

   private void saveToDB(){
       // Get the data from an ImageView as bytes
       iv.setDrawingCacheEnabled(true);
       iv.buildDrawingCache();
       Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
       byte[] data = baos.toByteArray();

       // Create a reference to "mountains.jpg"
       StorageReference mountainsRef = mStorageRef.child("latest.jpg");

       // Create a reference to 'images/mountains.jpg'
       StorageReference mountainImagesRef = mStorageRef.child("images/latest.jpg");

       UploadTask uploadTask = mountainsRef.putBytes(data);
       uploadTask.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception exception) {
               // Handle unsuccessful uploads
           }
       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
               // ...
           }
       });

   }

    // Clear the drawing by reinitializing to blank Mat
    public void clear() {
        m = Mat.zeros(height, width, CvType.CV_8UC3);
        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        iv.setImageBitmap(bm);
        draw(2000, 2000);
    }


    // Detects touch and motion
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                // on touch draw circle at (x,y)
                draw((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            case (MotionEvent.ACTION_MOVE):
                // on movement draw circle at (x,y)
                draw((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            /*case (MotionEvent.ACTION_UP):
                //Log.d(TAG,"Action was UP");
                draw((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            case (MotionEvent.ACTION_CANCEL):
                //Log.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                //Log.d(TAG,"Movement occurred outside bounds " +
                //"of current screen element");
                return true;*/
            default:
                return super.onTouchEvent(motionEvent);
        }


    }

    // Loading image from photos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE1:


                    try {

                        // Gets image from photos as Bitmap
                        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                        // Converts Bitmap to Mat so we can draw on it
                        Utils.bitmapToMat(bm, m);

                        // Mat is saved as 4 channels, converting it to 3 Channels
                        Imgproc.cvtColor(m, m, Imgproc.COLOR_BGRA2BGR);

                        // Scaling Mat to fit our screen bounds
                        Imgproc.resize(m, m, new Size(width, height));

                        // Must reinitialize Bitmap
                        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);

                        // Bitmap to image
                        iv.setImageBitmap(bm);
                        draw(2000, 2000);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

}



