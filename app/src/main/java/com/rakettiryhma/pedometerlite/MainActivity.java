package com.rakettiryhma.pedometerlite;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static boolean active = false;

    private StepsReceiver receiver;

    private ImageButton onButton;   // Käynnistysnappi
    private ImageButton offButton;  // Sammutusnappi
    private TextView textViewSteps; // Askeleiden tekstinäkymä
    private TextView textViewDate;  // Päivämäärä tekstinäkymä
    private Calendar calendar;      // Kalenteri päivämäärää ja kelloa varten

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tarkistetaan onko laitteessa tarvittavat sensorit
        if (!sensorCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Error: No sensors found!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            System.exit(0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Pedometer Lite");
            alert.show();
        }

        onButton = findViewById(R.id.imageButtonOn);
        offButton = findViewById(R.id.imageButtonOff);

        calendar = Calendar.getInstance();

        textViewSteps = findViewById(R.id.textViewSteps);
        textViewDate = findViewById(R.id.textViewDate);

        receiver = new StepsReceiver(textViewSteps);
        registerReceiver(receiver, new IntentFilter("STEP_UPDATE"));

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        //unregisterReceiver(receiver); // tässä alunperin, siirretty onDestroyhyn
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        //sensorManager.unregisterListener(stepDetector); // tämä tapahtuu jo StepDetectorin onDestroyssa
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onoffButtonPressed(View view) {
        if (view == onButton) {
            onButton.setVisibility(View.GONE);
            offButton.setVisibility(View.VISIBLE);

            if (!StepDetector.active) {
                startService(new Intent(this, StepDetector.class));
            }
        } else if (view == offButton) {
            onButton.setVisibility(View.VISIBLE);
            offButton.setVisibility(View.GONE);

            if (StepDetector.active) {
                stopService(new Intent(this, StepDetector.class));
            }
        }
    }

    public void resetButtonPressed(View view) {

        // Varmistetaan käyttäjältä askelten resetointi
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to reset steps?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Resetoidaan
                        StepsSaver.resetSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE));
                        updateUI();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Ei resetoida
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Pedometer Lite");
        alert.show();
    }

    public void calendarButtonPressed(View view) {
        Intent historyListActivity = new Intent(this, HistoryListActivity.class);
        startActivity(historyListActivity);
    }

    private void updateUI() {
        if (StepDetector.active) {
            onButton.setVisibility(View.GONE);
            offButton.setVisibility(View.VISIBLE);
        } else {
            onButton.setVisibility(View.VISIBLE);
            offButton.setVisibility(View.GONE);
        }

        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        textViewDate.setText(currentDate);
        textViewSteps.setText(Integer.toString(StepsSaver.getSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE))));
    }

    private boolean sensorCheck() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            return true;
        } else {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                return true;
            } else {
                return false;
            }
        }
    }

}
