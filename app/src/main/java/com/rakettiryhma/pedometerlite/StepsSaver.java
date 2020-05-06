package com.rakettiryhma.pedometerlite;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Lindborg
 *
 * Luokka jolla askelia tallennetaan laitteen muistiin ja hallitaan.
 * Avain määritelty public static final String muuttujana, jota kutsuvan luokan tulee käyttää luodessaan SharedPreferences oliota.
 */
public class StepsSaver {

    public static final String STEPS_PREFERENCES = "PedometerLitePreferences";
    public static final String STEPS_KEY = "StepsKey"; //  avain kokonaisaskelille

    /**
     * Haetaan kaikkien tallennettujen askelten määrä.
     *
     * @param sharedPreferences SharedPreferences
     * @return int askelet
     */
    public static int getSteps(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(STEPS_KEY, 0);
    }

    /**
     * Palauttaa tietylle päivämäärälle tallennettujen askelten määrän.
     *
     * @param sharedPreferences SharedPreferences
     * @param date String, päivämäärä jonka askelet halutaan palauttaa
     * @return int askelet
     */
    public static int getStepsByDate(SharedPreferences sharedPreferences, String date) {
        return sharedPreferences.getInt(date, 0);
    }

    /**
     * Poistaa kaikkien tallennettujen askelien arvot ja asettaa kokonaisaskielien arvoksi 0.
     *
     * @param sharedPreferences SharedPreferences
     */
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

    /**
     * Kasvattaa kokonaisaskelia sekä päivän askelia yhdellä.
     *
     * @param sharedPreferences SharedPreferences
     */
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

    /**
     * Palauttaa askelia_per_päivämäärä-olio(StepsByDate) listan.
     *
     * @param sharedPreferences SharedPreferences
     * @return List, lista tallennetusita päivämääristä askelineen
     */
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
