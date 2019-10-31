package com.example.paint4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
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
    ImageButton btPen;
    ImageButton btEra;
    int thickness;
    int sX, sY, sZ;


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



        helloworld(2000, 2000);

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

    public void clear(){
        m = Mat.zeros(1700, 1070, CvType.CV_8UC3);
        bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        iv.setImageBitmap(bm);
        helloworld(2000, 2000);
    }

}


