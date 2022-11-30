package app.orosoft.safaricom;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView textView;
    CameraSource cameraSource;
    Button btn_finish;
    private static final int PERMISSION = 100023;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        surfaceView = findViewById(R.id.readerview);
        textView = findViewById(R.id.tv_num_holder);
        btn_finish = findViewById(R.id.btn_finish);

        themefy();

        startCamera();

        /////////////////////////////////////////////////////////////////// BANNER AD
        MobileAds.initialize(this, initializationStatus -> { });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        /////////////////////////////////////////////////////////////////// BANNER AD

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("cardnum", textView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void startCamera() {
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if ( !textRecognizer.isOperational() ){
            Log.e("CAMERA ERROR", "Dependencies not loaded yet !");
        }
        else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing( CameraSource.CAMERA_FACING_BACK )
//                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedPreviewSize(120, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ReaderActivity.this, new String[]{
                                Manifest.permission.CAMERA
                        }, PERMISSION);
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if ( items.size() !=0 ) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
//                                StringBuilder stringBuilder = new StringBuilder();
//                                for (int i = 0; i < items.size(); i++) {
//                                    TextBlock item = items.valueAt( i );
//                                    stringBuilder.append( item.getValue() );
//                                    stringBuilder.append("\n");
//                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                TextBlock item = items.valueAt( 0 );
                                stringBuilder.append( item.getValue() );
                                textView.setText( stringBuilder.toString().trim().replace(" ", "") );
                            }
                        });
                    }
                }
            });


        }
    }

    private void themefy() {
        boolean darkTheme = prefs.getBoolean("dark_theme_enabled", false);
        if ( darkTheme ) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}

