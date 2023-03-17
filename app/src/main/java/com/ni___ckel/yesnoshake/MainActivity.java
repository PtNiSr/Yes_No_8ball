package com.ni___ckel.yesnoshake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView TextOnMain;
    private MediaPlayer mediaPlayer;        //For shaking sound
    private Accelerometer accelerometer;
    private Vibrator vibrator;
    private MainViewModel viewModel;    //According to MVVM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);   //According to MVVM

        accelerometer = new Accelerometer(this);    //reaction on shaking
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                //After shaking the loading of the prediction and sounds are starting
                viewModel.soundOn(tx, mediaPlayer);     //Sounds of shaking
                viewModel.printPrediction(tx, ty, tz);

            }
        });

        viewModel.getStr().observe(MainActivity.this, new Observer<String>() {
            //If prediction is changed (due to shaking) it will be installed at "TextOnMain"
            @Override
            public void onChanged(String s) {
                viewModel.setStartOrNot(false);     //StartOrNot is false to make possibility to have new prediction at least in 2 sec.
                vibrator.vibrate(700);      //Start vibration (after new prediction is downloaded)
                TextOnMain.setText(s);      //The downloaded text is installed in TextOnMain
                viewModel.setStartOrNotTrueByTimer();   //The new one prediction will be able in 2 sec, after last one
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setQuake(true);       //it is necessary to have access to make the sound of shaking for the 1st time.
        accelerometer.register();
    }

    private void initViews() {
        TextOnMain = findViewById(R.id.TextOnMain);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

}