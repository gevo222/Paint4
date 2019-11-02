package com.example.paint4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnTouchListener {

    // Initialize OpenCV
    static {
        OpenCVLoader.initDebug();
    }


    // Declare variables
    private static final String TAG = "MainActivity";
    Mat m;
    Bitmap bm;
    ImageView iv;
    Button btp, btm, btc;
    ImageButton bts, btl, btPen, btEra;
    int thickness;
    final int RQS_IMAGE1 = 1;
    Uri source;
    Bitmap tempBitmap;
    Scalar sc, white, black;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        sc = new Scalar(255, 0, 0);
        black = new Scalar(0, 0, 0);
        white = new Scalar(255, 255, 255);
        thickness = 50;
        btp = findViewById(R.id.button2);
        btm = findViewById(R.id.button3);
        btc = findViewById(R.id.clearButton);
        bts = findViewById(R.id.saveButton);
        btl = findViewById(R.id.loadButton);
        btPen = findViewById(R.id.pencilButton);
        btEra = findViewById(R.id.eraserButton);
        m = Mat.zeros(1700, 1070, CvType.CV_8UC3);
        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        iv = (ImageView) findViewById(R.id.imageView1);

        // Listen for touches on image
        iv.setOnTouchListener(this);


        // Increase brush size
        btp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thickness += 5;
            }
        });

        // Decrease brush size
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thickness -= 5;
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


        // Save image to photos
        bts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(bm);
            }
        });

        // Load image from photos
        btl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });


        draw(2000, 2000);


    }


    // Drawing
    public void draw(int x, int y) {

        // Draw circle with these params (Mat, touched point, circle radius, color, thickness)
        Imgproc.circle(m, new Point(x, y), 2, sc /*new Scalar(sX, sY, sZ)*/, thickness);
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

    // Clear the drawing by reinitializing to blank Mat
    public void clear() {
        m = Mat.zeros(1700, 1070, CvType.CV_8UC3);
        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        iv.setImageBitmap(bm);
        draw(2000, 2000);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                //Log.d(TAG,"Action was DOWN");
                draw((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            case (MotionEvent.ACTION_MOVE):
                //Log.d(TAG,"Action was MOVE");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE1:
                    source = data.getData();


                    try {
                        //tempBitmap is Immutable bitmap,
                        //cannot be passed to Canvas constructor
                        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        /*tempBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                        bm = tempBitmap;*/


                        //Mat m2 = Mat.zeros(1700, 1070, CvType.CV_8UC3);
                        Utils.bitmapToMat(bm, m);
                        //Imgproc.cvtColor(m, m, Imgproc.COLOR_GRAY2RGB);
                        Imgproc.cvtColor(m, m, Imgproc.COLOR_BGRA2BGR);
                        Imgproc.resize(m, m, new Size(1070, 1700));
                        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
                        iv.setImageBitmap(bm);
                        draw(2000, 2000);
                        Log.d(TAG, "" + m.size());
                        Log.d(TAG, "" + m.channels());
                        // Log.d(TAG,""+m.());
                        //
                        /*bm = tempBitmap;
                        Utils.bitmapToMat(bm, m);
                        //Log.d(TAG,""+m.size());
                        iv.setImageBitmap(bm);*/


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

}



