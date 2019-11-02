package com.example.paint4;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.security.AccessController.getContext;

public class MainActivity extends Activity implements View.OnTouchListener {
    static {
        OpenCVLoader.initDebug();
    }

// TO-DO: Add saving and loading, redo comments

    private static final String TAG = "MainActivity";
    Mat m = Mat.zeros(1700, 1070, CvType.CV_8UC3);
    Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
    ImageView iv;
    Button btp;
    Button btm;
    Button btc;
    ImageButton bts;
    ImageButton btl;
    ImageButton btPen;
    ImageButton btEra;
    int thickness;
    int sX, sY, sZ;
    final int RQS_IMAGE1 = 1;
    Uri source;
    Bitmap tempBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sX = 255;
        sY = 255;
        sZ = 255;
        thickness = 20;
        btp = findViewById(R.id.button2);
        btm = findViewById(R.id.button3);
        btc = findViewById(R.id.clearButton);
        bts = findViewById(R.id.saveButton);
        btl = findViewById(R.id.loadButton);
        btPen = findViewById(R.id.pencilButton);
        btEra = findViewById(R.id.eraserButton);


        iv = (ImageView) findViewById(R.id.imageView1);
        iv.setOnTouchListener(this);

        btp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thickness += 5;
            }
        });

        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thickness -= 5;
            }
        });

        btc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        btPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sX = 255;
                sY = 255;
                sZ = 255;
            }
        });

        btEra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sX = 0;
                sY = 0;
                sZ = 0;
            }
        });

        bts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmapToGallery(bm);
            }
        });

        btl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });
        helloworld(2000, 2000);


    }

    private void saveBitmapToGallery(Bitmap bitmap) {

        String filename = "paint4-" + System.currentTimeMillis() + ".png";
        String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename, "paint4 app");

    }

    public void helloworld(int x, int y) {
        // make a mat and draw something

        Imgproc.circle(m, new Point(x, y), 2, new Scalar(sX, sY, sZ), thickness);
        // convert to bitmap:

        Utils.matToBitmap(m, bm);

        // find the imageview and draw it!

        iv.setImageBitmap(bm);

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                //Log.d(TAG,"Action was DOWN");
                helloworld((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            case (MotionEvent.ACTION_MOVE):
                //Log.d(TAG,"Action was MOVE");
                helloworld((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            case (MotionEvent.ACTION_UP):
                //Log.d(TAG,"Action was UP");
                helloworld((int) motionEvent.getX(), (int) motionEvent.getY());
                return true;
            case (MotionEvent.ACTION_CANCEL):
                //Log.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                //Log.d(TAG,"Movement occurred outside bounds " +
                //"of current screen element");
                return true;
            default:
                return super.onTouchEvent(motionEvent);
        }


    }

    public void clear() {
        m = Mat.zeros(1700, 1070, CvType.CV_8UC3);
        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        iv.setImageBitmap(bm);
        helloworld(2000, 2000);
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
                        tempBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                        Bitmap.Config config;
                        if (tempBitmap.getConfig() != null) {
                            config = tempBitmap.getConfig();
                        } else {
                            config = Bitmap.Config.ARGB_8888;
                        }

                        bm = tempBitmap;
                        Utils.bitmapToMat(bm,m);
                        iv.setImageBitmap(bm);

                        //bitmapMaster is Mutable bitmap
                        /*bitmapMaster = Bitmap.createBitmap(
                                tempBitmap.getWidth(),
                                tempBitmap.getHeight(),
                                config);

                        canvasMaster = new Canvas(bitmapMaster);
                        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

                        imageResult.setImageBitmap(bitmapMaster);*/
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

}



