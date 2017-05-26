package it.polito.mad.mad_app;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.RadarChart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;



import java.util.ArrayList;

import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.MainData;

public class UserStatisticsActivity extends AppCompatActivity {
    private Map<String, String> myGroups = new TreeMap<>();

    private Map<String, Float> balanceByGroupsPos = new TreeMap<>();
    private Map<String, Float> balanceByGroupsNeg = new TreeMap<>();
    private float oldValue = 0;
    private RadarChart RadarGroups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_statistics_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Statistics");
        }
        myGroups = MainData.getInstance().getMyGroupsId();
        RadarGroups = (RadarChart) findViewById(R.id.radarGroups);


        System.out.println("myGroupsId: "+ myGroups);
        balanceByGroupsNeg = MainData.getInstance().getBalanceByGroupsNeg();
        balanceByGroupsPos = MainData.getInstance().getBalanceByGroupsPos();
        System.out.println("balanceByGroupPos: "+ balanceByGroupsPos);
        System.out.println("balanceByGroupNeg: "+ balanceByGroupsNeg);

        ArrayList<Entry> yEntrysPos = new ArrayList<>();
        ArrayList<Entry> yEntrysNeg = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        int i = 0;
        xEntrys.clear();
        yEntrysPos.clear();
        yEntrysNeg.clear();

        for (String r : balanceByGroupsPos.keySet()) {
            yEntrysPos.add(new Entry(balanceByGroupsPos.get(r), i));
            i++;
        }
        i = 0;
        for (String r : balanceByGroupsNeg.keySet()) {
            yEntrysNeg.add(new Entry(balanceByGroupsNeg.get(r), i));
            i++;
        }

        RadarDataSet dataset_comp1 = new RadarDataSet(yEntrysPos, "Positive");
        RadarDataSet dataset_comp2 = new RadarDataSet(yEntrysNeg, "Negative");

        dataset_comp1.setColor(Color.GREEN);
        dataset_comp1.setDrawFilled(true);

        dataset_comp2.setColor(Color.RED);
        dataset_comp2.setDrawFilled(true);

        ArrayList<RadarDataSet> dataSets = new ArrayList<RadarDataSet>();

        ArrayList<String> labels = new ArrayList<String>(myGroups.values());
        dataSets.add(dataset_comp1);
        dataSets.add(dataset_comp2);

        RadarData data = new RadarData(labels, dataSets);
        RadarGroups.setData(data);
        String description = "Cumulative balance by group";
        RadarGroups.setDescription(description);
        RadarGroups.setWebLineWidthInner(0.5f);
        RadarGroups.setDescriptionColor(Color.RED);

        //chart.setSkipWebLineCount(10);
        RadarGroups.invalidate();
        RadarGroups.animate();



    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = getIntent();


        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

}
