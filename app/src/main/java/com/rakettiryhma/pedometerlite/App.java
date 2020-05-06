package com.rakettiryhma.pedometerlite;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * Application-luokan perivä luokka, jota käytetään notificationin luomiseen.
 */
public class App extends Application {

    public static final String CHANNEL_ID = "StepDetectorChannel";

    /**
     * Kutsuu createNotificationChannel-metodia.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    /**
     * Luo notification kanavan.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "StepDetector Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
