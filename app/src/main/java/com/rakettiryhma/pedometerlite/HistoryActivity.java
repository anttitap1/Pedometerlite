package com.rakettiryhma.pedometerlite;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Jonnie Anker
 *
 * Activity joka käynnistetään kun HistoryListActivityssä on tehty valinta.
 */
public class HistoryActivity extends AppCompatActivity {

    /**
     *Vaihtaa kahden tekstinäkymän tekstit Intentin extroissa saatujen tietojen mukaisiksi.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle bundle = getIntent().getExtras();
        String date = bundle.getString(HistoryListActivity.HISTORY_LIST_MESSAGE_DATE);
        int steps = bundle.getInt(HistoryListActivity.HISTORY_LIST_MESSAGE_STEPS);

        ((TextView) findViewById(R.id.textViewHistoryDate)).setText(date);
        ((TextView) findViewById(R.id.textViewHistorySteps)).setText(Integer.toString(steps));
    }
}
