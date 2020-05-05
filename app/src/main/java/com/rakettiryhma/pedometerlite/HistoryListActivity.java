package com.rakettiryhma.pedometerlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.List;

public class HistoryListActivity extends AppCompatActivity {

    public static final String HISTORY_LIST_MESSAGE_DATE = "com.rakettiryhma.pedometerlite.DATE";
    public static final String HISTORY_LIST_MESSAGE_STEPS = "com.rakettiryhma.pedometerlite.STEPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        final List<StepsByDate> stepsByDateArrayList = StepsSaver.getAllSteps(getSharedPreferences(StepsSaver.STEPS_PREFERENCES, Activity.MODE_PRIVATE));

        /*
        stepsByDateArrayList.add(new StepsByDate("toimiiks1", 1));          // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks2", 12));         // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks3", 23));         // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks4", 145));        // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks5", 12465));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks6", 123));        // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks7", 1123));       // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks8", 15534));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks9", 1345));       // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks10", 15345));     // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks11", 1345));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks12", 1534));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks13", 1345));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks14", 1534));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks15", 13));        // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks16", 1512));      // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks17", 112));       // TESTI
        stepsByDateArrayList.add(new StepsByDate("toimiiks18", 1512));      // TESTI
         */

        //Log.d("HistoryListActivity", "List size: " + stepsByDateArrayList.size());

        ListView listView = findViewById(R.id.historyListView);

        listView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                stepsByDateArrayList
        ));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(HISTORY_LIST_MESSAGE_DATE, stepsByDateArrayList.get(position).getDate());
                bundle.putInt(HISTORY_LIST_MESSAGE_STEPS, stepsByDateArrayList.get(position).getSteps());

                Intent historyActivity = new Intent(HistoryListActivity.this, HistoryActivity.class);
                historyActivity.putExtras(bundle);
                startActivity(historyActivity);
            }
        });
    }
}
