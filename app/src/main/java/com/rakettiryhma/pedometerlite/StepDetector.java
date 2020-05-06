package com.rakettiryhma.pedometerlite;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.rakettiryhma.pedometerlite.App.CHANNEL_ID;

/**
 * Service-luokan perivä ja SensorEventListener-rajapinnan toteuttava luokka, jolla tunnistetaan askel.
 */
public class StepDetector extends Service implements SensorEventListener {

    public static boolean active = false;

    double magnitudePrevious = 0;
    int stepMagnitudeTreshold = 6;

    private SensorManager sensorManager;

    private boolean hasStepDetector;

    /**
     * Testaa onko laitteessa Step Detector-sensoria ja rekisteröi sen jos sensori löytyy, jos sensoria ei löydy niin
     * testaa onko laitteessa Accelerometer-sensoria ja rekisteröi sen. Jos kumpaakaan sensoria ei löydy niin sammuttaa itsensä.
     *
     * Sensorin rekisteröinnin jälkeen luodaan notification ja käynnistetään foreground service, joka jatkaa askelten laskua niin kauan
     * kunnes se sammutetaan. Foreground service ei sammu vaikka sen käynnistämä sovellus sammutettaisiin.
     *
     * @param intent Intent
     * @param flags int
     * @param startId int
     * @return int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        active = true;
        //Log.d("StepDetector", "onStartCommand");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Kokeillaan ensin onko laitteessa Step Detector sensoria, jos on käytetään sitä
        hasStepDetector = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        if (!hasStepDetector) {
            // Ei Step Counter sensoria, kokeillaan onko accelerometer
            boolean hasAccelerometer = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            if (!hasAccelerometer) {
                Toast.makeText(getApplicationContext(), "ERROR: No sensors found!", Toast.LENGTH_LONG).show();
                stopSelf();
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE HH:mm, dd MMMM yyyy");
        String notificationContentText = "Counting steps since " + simpleDateFormat.format(calendar.getTime()) + ".";

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pedometer Lite")
                .setContentText(notificationContentText)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
        //return START_STICKY;
    }

    /**
     * Vapauttaa sensorin ja asettaa muuttujan active arvoksi false.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);
        active = false;

        Log.d("DEBUG", "StepDetector: onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event != null) {

            // NÄILLE LASKUILLE OMA LUOKKA
            float x_acceleration = event.values[0];
            float y_acceleration = event.values[1];
            float z_acceleration = event.values[2];

            double magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
            double magnitudeDelta = magnitude - magnitudePrevious;
            magnitudePrevious = magnitude;

            if (magnitudeDelta > stepMagnitudeTreshold) {

                StepsSaver.incrementSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE));

                // Jos MainActivity on aktiivinen niin lähetetään askelet, jotta ne päivittyvät ruudulle
                if (MainActivity.active) {
                    sendStepsToActivity(StepsSaver.getSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE)));
                }
            }
        }
    }
    */

    /**
     * Metodi jota kutsutaan käytössä olevan sensorin tunnistaessa uuden tapahtuman.
     * Reagoi onStartCommandissa rekisteröidyn sensorin tapahtumiin.
     * Jos tapahtuma on Step Detecotr-sensorin niin lisää yhden askeleen.
     * Jos tapahtuma on Accelerometer-sensorin niin lasketaan tapahtuman arvoista onko käyttäjä ottanut askeleen.
     *
     * Lähettää askeleet MainActivitylle jos MainActivityn active muuttujan arvo on true.
     *
     * @param event sensorin tunnistama tapahtuma
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event != null) {
            // Jos Step Detector sensori on käytössä, muuten käytetään Accelerometer sensoria
            if (hasStepDetector) {
                StepsSaver.incrementSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE));

                // Jos MainActivity on aktiivinen niin lähetetään askelet, jotta ne päivittyvät ruudulle
                if (MainActivity.active) {
                    sendStepsToActivity(StepsSaver.getSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE)));
                }

            } else {
                float x_acceleration = event.values[0];
                float y_acceleration = event.values[1];
                float z_acceleration = event.values[2];

                double magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                double magnitudeDelta = magnitude - magnitudePrevious;
                magnitudePrevious = magnitude;

                if (magnitudeDelta > stepMagnitudeTreshold) {

                    StepsSaver.incrementSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE));

                    // Jos MainActivity on aktiivinen niin lähetetään askelet, jotta ne päivittyvät ruudulle
                    if (MainActivity.active) {
                        sendStepsToActivity(StepsSaver.getSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE)));
                    }
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Metodi jolla voidaan lähettää dataa Activityyn, tässä tapauksessa laskettujen askelten lukumäärä MainActivitylle.
     *
     * @param stepCount askelten lukumäärä
     */
    private void sendStepsToActivity(int stepCount) {
        Intent intent = new Intent("STEP_UPDATE");

        intent.putExtra("STEP_COUNT", stepCount);
        sendBroadcast(intent);
    }

}
