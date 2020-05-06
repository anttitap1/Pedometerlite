package com.rakettiryhma.pedometerlite;

import androidx.annotation.NonNull;

/**
 * @author Jonnie Anker
 *
 * Luokka josta luodaan olioita, joihin tallennetaan askelet per päivämäärä.
 */
public class StepsByDate {

    private String date;
    private int steps;

    /**
     * Konstruktori jossa luotavalle oliolle asetetaan päivämäärä ja askelet.
     *
     * @param date päivämäärä
     * @param steps askelet
     */
    public StepsByDate(String date, int steps) {
        this.date = date;
        this.steps = steps;
    }

    /**
     * Palauttaa olio-muuttujan date (päivämäärä) arvon.
     *
     * @return String date
     */
    public String getDate() {
        return date;
    }

    /**
     * Palauttaa olio-muuttuja steps (askelet) arvon.
     *
     * @return int steps
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Palauttaa olio-muuttuja date arvon.
     *
     * @return String date
     */
    @NonNull
    @Override
    public String toString() {
        return date;
    }
}
