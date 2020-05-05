package com.rakettiryhma.pedometerlite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

// Luokka jolla otetaan vastaan StepDetectorin lähettämää dataa askelista
public class StepsReceiver extends BroadcastReceiver {

    private TextView stepsTextView;

    public StepsReceiver(TextView stepsTextView) {
        this.stepsTextView = stepsTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("STEP_UPDATE")) {
            int steps = intent.getIntExtra("STEP_COUNT", 0);
            stepsTextView.setText(Integer.toString(steps));
        }

    }
}
