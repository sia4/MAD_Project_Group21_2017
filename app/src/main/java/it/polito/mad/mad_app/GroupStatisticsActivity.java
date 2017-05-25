package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ExpenseData;

public class GroupStatisticsActivity extends AppCompatActivity {
    private String gId, gName, defaultcurrency;
    PieChart PieCategory;
    BarChart BarByDate;
    Map<String, Float> exSum = new TreeMap<>();
    Map<String, Float> exSumbyDate = new TreeMap();
    //TODO OTTENERE LISTA CATEGORIES DA CATEGORIES.XML IN MODO AUTOMATICO
    String[] categories = { "Entertainment", "Food and Drinks", "House and Utilities","Clothing","Present","Medical Expenses","Transport","Hotel","Cleaning","General", "Other"};
    String[] xEntrysArray;
    ArrayList<Integer> dates = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_statistics_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Group Statistics");
        }

        Intent i = getIntent();
        gId = i.getStringExtra("groupId");
        gName = i.getStringExtra("groupName");
        defaultcurrency = i.getStringExtra("defaultcurrency");


        PieCategory = (PieChart) findViewById(R.id.pieGroupCategory);
        PieCategory.setRotationEnabled(true);
        PieCategory.setHoleRadius(50f);
        PieCategory.setTransparentCircleAlpha(0);
        PieCategory.setCenterText("Expenses by categories");
        PieCategory.setCenterTextSize(10);

        BarByDate = (BarChart) findViewById(R.id.barGroupDate);



        //System.out.println("categories " + categories);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Expenses").child(gId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map1 = (Map<String, Object>) dataSnapshot.getValue();
                if (map1 != null) {
                    exSum = new TreeMap<>();
                    String category;
                    Long datemilliseconds;
                    Float oldValue, oldValuebyDate, newValue;
                    for (String exId : map1.keySet()) {
                        System.out.println("mappa" + map1.get(exId));
                        Map<String, Object> exItem = (Map<String, Object>) map1.get(exId);
                        System.out.println("expensestatistic " + exItem);
                        category = (String) exItem.get("category");
                        datemilliseconds = Long.parseLong((String)exItem.get("date"));
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd");
                        String dateString = formatter.format(new Date(datemilliseconds));
                        dates.add(Integer.parseInt(formatter2.format(new Date(datemilliseconds))));
                        System.out.println("date expense: "+dateString);
                        newValue = Float.parseFloat((String) exItem.get("myvalue"));

                        if (exSum.containsKey(category)) {
                            oldValue = exSum.get(category);
                            exSum.put(category, oldValue + newValue);
                        } else {
                            exSum.put(category, newValue);
                        }


                        if (exSumbyDate.containsKey(dateString)) {
                            oldValuebyDate = exSumbyDate.get(dateString);
                            exSumbyDate.put(dateString, oldValuebyDate + newValue);
                        } else {
                            exSumbyDate.put(dateString, newValue);
                        }

                    }
                    System.out.println("mappa statistiche: " + exSumbyDate);

                    ArrayList<PieEntry> yEntrys = new ArrayList<>();
                    ArrayList<BarEntry> yEntrysBar = new ArrayList<>();
                    ArrayList<String> xEntrys = new ArrayList<>();
                    int i = 0;
                    for (String r : exSum.keySet()) {
                        yEntrys.add(new PieEntry(exSum.get(r), i));
                        i++;
                    }


                    for (int r = 0; r < categories.length; r++) {
                        xEntrys.add(categories[r]);
                    }

                    PieDataSet pieDataSet = new PieDataSet(yEntrys, "Categories");

                    pieDataSet.setValueTextSize(12);

                    ArrayList<Integer> color = new ArrayList<>();

                    color.add(Color.parseColor("#ef9a9a"));
                    color.add(Color.parseColor("#f48fb1"));
                    color.add(Color.parseColor("#ce93d8"));
                    color.add(Color.parseColor("#81d4fa"));
                    color.add(Color.parseColor("#80cbc4"));
                    color.add(Color.parseColor("#a5d6a7"));
                    color.add(Color.parseColor("#e6ee9c"));
                    color.add(Color.parseColor("#ffcc80"));
                    color.add(Color.parseColor("#bcaaa4"));
                    color.add(Color.parseColor("#eeeeee"));
                    color.add(Color.parseColor("#b0bec5"));

                    pieDataSet.setColors(color);
                    Legend legend = PieCategory.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                    PieData pieData = new PieData(pieDataSet);
                    PieCategory.setData(pieData);
                    PieCategory.invalidate();


                    i = 0;
                    xEntrys.clear();
                    yEntrysBar.clear();
                    xEntrysArray = new String[exSumbyDate.size()];
                    for (String r : exSumbyDate.keySet()) {
                        yEntrysBar.add( new BarEntry(dates.get(i), exSumbyDate.get(r))  );
                        xEntrys.add(r);
                        xEntrysArray[i] = r;
                        i++;
                    }


                    BarDataSet barDataSet = new BarDataSet(yEntrysBar, "Date");

                    barDataSet.setValueTextSize(12);



                    barDataSet.setColor(Color.parseColor("#b0bec5"));
                    Legend legend2 = BarByDate.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                    BarData barData = new BarData(barDataSet);
                    //BarByDate.animateXY(2000,2000);
                    BarByDate.setData(barData);

                    BarByDate.invalidate();



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final TextView CatSpec = (TextView) findViewById(R.id.CategorySpecs);
        PieCategory.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                int pos1 = e.toString().indexOf("y: ");
                String ss = e.toString().substring(pos1 + 2);
                int i=0;
                for(String r : exSum.keySet()){
                    i++;
                    if(exSum.get(r)==Float.parseFloat(ss)){
                        pos1 = i;
                        break;
                    }
                }
                String sss = categories[pos1];
                String bau ="Category "+sss+"\n"+"Total spent: "+ss;
                CatSpec.setText(bau);
            }

            @Override
            public void onNothingSelected() {
                CatSpec.setText("");
            }

        });
        final TextView DateSpec = (TextView) findViewById(R.id.DataSpecs);
        BarByDate.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override

            public void onValueSelected(Entry e, Highlight h) {
                String r = "";
                int pos1 = e.toString().indexOf("y: ");
                String ss = e.toString().substring(pos1 + 2);
                int i=0;
                for(String rtmp : exSumbyDate.keySet()){
                    r = rtmp;
                    i++;
                    if(exSumbyDate.get(r)==Float.parseFloat(ss)){
                        pos1 = i;
                        break;
                    }
                }
                String sss = r;
                String bau ="Date "+sss+"\n"+"Total spent: "+ss;
                DateSpec.setText(bau);
            }

            @Override
            public void onNothingSelected() {
                DateSpec.setText("");
            }
        });


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
