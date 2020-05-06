package com.rakettiryhma.pedometerlite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

/**
 * @author Antti Taponen
 *
 * Luokka jolla otetaan vastaan StepDetector-luokan lähettämää dataa askelista.
 */
public class StepsReceiver extends BroadcastReceiver {

    private TextView stepsTextView;

    /**
     * Konstruktori, jossa asetetaan olion luovan luokan päivitettävä tekstinäkymä.
     *
     * @param stepsTextView TextView
     */
    public StepsReceiver(TextView stepsTextView) {
        this.stepsTextView = stepsTextView;
    }

    /**
     * Metodi jota kutsutaan kun vastaanotetaan uusi Intent.
     * Intentin extroista puretaan askelmäärä ja se päivitetään konstruktorissa asetettuun tekstinäkymään.
     *
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("STEP_UPDATE")) {
            int steps = intent.getIntExtra("STEP_COUNT", 0);
            stepsTextView.setText(Integer.toString(steps));
        }

    }
}
