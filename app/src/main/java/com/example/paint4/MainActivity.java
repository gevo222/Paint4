package com.example.paint4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnTouchListener {

    // Declare variables
    private static final String TAG = "MainActivityy";

    // Initialize OpenCV
    static {
        OpenCVLoader.initDebug();
    }

    final int RQS_IMAGE1 = 1;
    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    Mat m;
    Bitmap bm;
    ImageView iv;
    Button btp, btm, btc;
    ImageButton bts, btl, btPen, btEra, bttools, btColor, btRed, btBlue, btGreen;
    int rad;
    Scalar sc, white, black, red, blue, green;
    int width, height;
    // Create a reference to "mountains.jpg"
    StorageReference mountainsRef = mStorageRef.child("latest.jpg");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables


        try {
            loadFromDB();
        } catch (Exception e) {

        }

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
        m = Mat.zeros(height, width, CvType.CV_8UC3);
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
                } else {
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
                if (btl.getVisibility() == View.INVISIBLE) {
                    allVisible();
                } else {
                    allInvisible();
                }
            }
        });

        Context context = getApplicationContext();
        final Toast toast = Toast.makeText(context, "Autosaved", Toast.LENGTH_SHORT);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                saveToDB();
                toast.show();
                Log.d(TAG, "run: saved" + System.nanoTime());
            }
        }, 30, 30, TimeUnit.SECONDS);

        // autosave every _ seconds

        /* Used to use thread, trying ScheduledExecutorService
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(30000);
                        saveToDB();
                        toast.show();
                        Log.d(TAG, "run: saved" + System.nanoTime());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        */

        Runnable save = new Runnable() {
            @Override
            public void run() {
                saveToDB();
                toast.show();
                Log.d(TAG, "run: saved" + System.nanoTime());
            }
        };

        draw(2000, 2000);
    }

    public void allVisible() {
        btl.setVisibility(View.VISIBLE);
        bts.setVisibility(View.VISIBLE);
        btEra.setVisibility(View.VISIBLE);
        btPen.setVisibility(View.VISIBLE);
        btc.setVisibility(View.VISIBLE);
        btm.setVisibility(View.VISIBLE);
        btp.setVisibility(View.VISIBLE);
        btColor.setVisibility(View.VISIBLE);
    }

    public void allInvisible() {
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

    private void saveToDB() {
        // Get the data from an ImageView as bytes
        iv.setDrawingCacheEnabled(true);
        iv.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

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

    public void loadFromDB() {

        final long ONE_MEGABYTE = 1024 * 1024;
        mountainsRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: fail");
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
            default:
                return super.onTouchEvent(motionEvent);
        }
    }

    // Loading image from photos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_IMAGE1) {
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
            }
        }
    }
}



