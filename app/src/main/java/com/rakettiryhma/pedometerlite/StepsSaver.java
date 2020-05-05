package com.rakettiryhma.pedometerlite;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class StepsSaver {

    public static final String STEPS_PREFERENCES = "PedometerLitePreferences";
    public static final String STEPS_KEY = "StepsKey"; //  avain kokonaisaskelille

    public static int getSteps(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(STEPS_KEY, 0);
    }

    public static int getStepsByDate(SharedPreferences sharedPreferences, String date) {
        return sharedPreferences.getInt(date, 0);
    }

    public static void resetSteps(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.clear();                // poistetaan kaikki arvot, myös historia
        sharedPreferencesEditor.putInt(STEPS_KEY, 0);   // kokonaisaskelet 0, että MainActivityssä näkyy kokoajan joku numero
        sharedPreferencesEditor.commit();
    }

    /*
    public static void resetStepsByDate(SharedPreferences sharedPreferences, String date) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putInt(STEPS_KEY, 0);
        sharedPreferencesEditor.remove(date);
        sharedPreferencesEditor.commit();
    }
    */

    // Kasvatetaan kokonaisaskelia sekä päivän askelia yhdellä
    public static void incrementSteps(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        int currentSteps = getSteps(sharedPreferences);
        int stepsByDate = getStepsByDate(sharedPreferences, currentDate);

        sharedPreferencesEditor.putInt(STEPS_KEY, currentSteps + 1); // put total
        sharedPreferencesEditor.putInt(currentDate, stepsByDate + 1); // put by date
        sharedPreferencesEditor.commit();
    }

    public static List<StepsByDate> getAllSteps(SharedPreferences sharedPreferences) {
        List<StepsByDate> list = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ((Map<String, Integer>) sharedPreferences.getAll()).entrySet()) {
            String date = entry.getKey();
            int steps = entry.getValue();

            // ei lisätä kokonaisaskelia listaan
            if (!date.equals(STEPS_KEY)) {
                list.add(new StepsByDate(date, steps));
            }
        }

        return list;
    }
}
