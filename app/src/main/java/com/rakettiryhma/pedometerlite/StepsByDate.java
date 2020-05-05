package com.rakettiryhma.pedometerlite;

import androidx.annotation.NonNull;

// Luokka josta luodaan olioita, joihin tallennetaan askelet per päivämäärä
public class StepsByDate {

    private String date;
    private int steps;

    public StepsByDate(String date, int steps) {
        this.date = date;
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    @NonNull
    @Override
    public String toString() {
        return date;
    }
}
