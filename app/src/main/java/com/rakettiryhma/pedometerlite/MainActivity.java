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

/**
 * Sovelluksen MainActivity.
 */
public class MainActivity extends AppCompatActivity {
    public static boolean active = false;

    private StepsReceiver receiver;

    private ImageButton onButton;   // Käynnistysnappi
    private ImageButton offButton;  // Sammutusnappi
    private TextView textViewSteps; // Askeleiden tekstinäkymä
    private TextView textViewDate;  // Päivämäärä tekstinäkymä
    private Calendar calendar;      // Kalenteri päivämäärää ja kelloa varten

    /**
     * Testataan onko laitteessa Step Detector- tai Accelerometer-sensori. Jos kumpaakaan sensoria ei löydy
     * niin käyttäjälle näytetään Dialogi jossa ilmoitetaan, että sensoreita ei löytynyt ja sovellus sammutetaan.
     * Jos laitteessa on ainakin toinen sensoreista niin UI-näkymille asetetaan arvot ja rekisteröidään BroadcastReceviver(StepsReceiver)
     * jolla vastaanotetaan dataa askeltenlaskija-servicestä.
     *
     * @param savedInstanceState
     */
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
        registerReceiver(receiver, new IntentFilter("STEP_UPDATE")); // Rekisteröidään BroadcastReceiver joka vastaanottaa askel-määrää

        updateUI();
    }

    /**
     * Asettaa muuttujan active arvoksi true.
     */
    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    /**
     * Asettaa muuttujan active arvoksi false.
     */
    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        //unregisterReceiver(receiver); // tässä alunperin, siirretty onDestroyhyn
    }

    /**
     * Vapauttaa BroadcastReceiverin(StepsReceiver).
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        //sensorManager.unregisterListener(stepDetector); // tämä tapahtuu jo StepDetectorin onDestroyssa
    }

    /**
     * On- ja Off-napin onClick-metodi, jolla joko käynnistetään tai pysäytetään askeltenlasku-service ja
     * päivittää nappien näkyvyydet.
     *
     * @param view On- tai Off-napin näkymä
     */
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

    /**
     * Reset-napin onClick-metodi, näyttää dialogin joka kysyy halutaanko askelet resetoida,
     * jos valitsee "YES" askelet resetoidaan ja UI päivitetään, muulloin toiminto peruutetaan.
     *
     * @param view reset-napin näkymä
     */
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

    /**
     * Kalenterinapin onClick-metodi, joka käynnistää Kalenteri-activityn.
     *
     * @param view kalenterinapin näkymä
     */
    public void calendarButtonPressed(View view) {
        Intent historyListActivity = new Intent(this, HistoryListActivity.class);
        startActivity(historyListActivity);
    }

    /**
     * Päivittää UI:n päivämäärä ja askelmäärä tekstielementit, sekä On- ja Off-napin näkyvyyden
     */
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

    /**
     * Palauttaa arvon true jos laitteessa on joko Accelerometer- tai Step Detector-sensori, muulloin palauttaa arvon false.
     *
     * @return true jos Accelerometer- tai Step Detector-sensori on käytettävissä, muulloin false.
     */
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
