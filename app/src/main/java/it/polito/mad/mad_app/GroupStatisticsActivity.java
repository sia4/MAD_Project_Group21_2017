package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class GroupStatisticsActivity extends AppCompatActivity {
    private String gId, gName, defaultcurrency;
    private float total=0;
    PieChart PieCategory;
    BarChart BarByDate;
    Map<String, Float> exSum = new TreeMap<>();
    Map<String, Float> exSumbyDate = new TreeMap();
    private Map<String, Integer> catToId = new TreeMap<>();
    private Map<String, Integer> catToColor = new TreeMap<>();

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

        BarByDate = (BarChart) findViewById(R.id.barGroupDate);


        catToId.put("Entertainment", R.drawable.entertainment);
        catToId.put("Food and Drinks", R.drawable.food);
        catToId.put("House and Utilities", R.drawable.house);
        catToId.put("Clothing", R.drawable.clothing);
        catToId.put("Present", R.drawable.present);
        catToId.put("Medical Expenses", R.drawable.medical);
        catToId.put("Transport", R.drawable.transportation);
        catToId.put("Hotel", R.drawable.hotel);
        catToId.put("Cleaning", R.drawable.cleaning);
        catToId.put("General", R.drawable.general);
        catToId.put("Other", R.drawable.other);

        catToColor.put("Entertainment",Color.parseColor("#fff59d"));
        catToColor.put("Food and Drinks", Color.parseColor("#ffab91"));
        catToColor.put("House and Utilities", Color.parseColor("#9fa8da"));
        catToColor.put("Clothing", Color.parseColor("#a5d6a7"));
        catToColor.put("Present",Color.parseColor("#f48fb1"));
        catToColor.put("Medical Expenses", Color.parseColor("#80cbc3"));
        catToColor.put("Transport", Color.parseColor("#90caf9"));
        catToColor.put("Hotel", Color.parseColor("#b39ddb"));
        catToColor.put("Cleaning", Color.parseColor("#80deea"));
        catToColor.put("General", Color.parseColor("#bcaaa4"));
        catToColor.put("Other", Color.parseColor("#eeeeee"));


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
                    float oldValue=0, oldValuebyDate=0, newValue=0;
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

                        newValue = Float.parseFloat((String) exItem.get("myvalue"));
                        total += newValue;
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
                    ArrayList<Integer> color = new ArrayList<>();
                    ArrayList<Entry> yEntrys = new ArrayList<>();
                    ArrayList<BarEntry> yEntrysBar = new ArrayList<>();
                    ArrayList<String> xEntrys = new ArrayList<>();
                    int i = 0;
                    for (String r : exSum.keySet()) {
                        color.add(catToColor.get(r));
                        yEntrys.add(new Entry(exSum.get(r), i));
                        xEntrys.add(r);
                        i++;
                    }


                    PieDataSet pieDataSet = new PieDataSet(yEntrys, "Categories");
                    pieDataSet.setValueTextSize(12);


                    pieDataSet.setColors(color);

                    Legend legend = PieCategory.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
                    System.out.println("yEntries "+yEntrys);
                    PieData pieData = new PieData(xEntrys, pieDataSet);
                    String description = "";
                    PieCategory.setDescription(description);
                    PieCategory.setData(pieData);
                    PieCategory.invalidate();


                    i = 0;
                    xEntrys.clear();
                    yEntrysBar.clear();
                    xEntrysArray = new String[exSumbyDate.size()];
                    for (String r : exSumbyDate.keySet()) {
                        yEntrysBar.add( new BarEntry(exSumbyDate.get(r), i)  );
                        xEntrys.add(r);
                        xEntrysArray[i] = r;
                        i++;
                    }


                    BarDataSet barDataSet = new BarDataSet(yEntrysBar, "Date");
                    barDataSet.setValueTextSize(12);

                    barDataSet.setColor(Color.parseColor("#b2dfdb"));
                    Legend legend2 = BarByDate.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                    BarData barData = new BarData(xEntrys,barDataSet);
                    BarByDate.animateXY(2000,2000);
                    BarByDate.enableScroll();
                    BarByDate.setMinimumWidth(1);
                    BarByDate.setDescription("");
                    BarByDate.setData(barData);
                    BarByDate.invalidate();



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final TextView CatSpec = (TextView) findViewById(R.id.CategorySpecs);
        final ImageView catImg = (ImageView) findViewById(R.id.categoryImg);
        PieCategory.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

                int pos1 = e.toString().indexOf("(sum): ");
                String ss = e.toString().substring(pos1 + 7);
                String sss="";
                for(String r : exSum.keySet()){
                    if(exSum.get(r)==Float.parseFloat(ss)){
                        sss = r;
                        break;
                    }
                }

                catImg.setImageDrawable(getResources().getDrawable(catToId.get(sss)));

                String bau ="Category "+sss+"\n"+"Total spent: "+ss+" / "+total;
                CatSpec.setText(bau);
            }

            @Override
            public void onNothingSelected() {
                catImg.setVisibility(View.GONE);
                CatSpec.setText("");
            }



        });
        final TextView DateSpec = (TextView) findViewById(R.id.DataSpecs);
        BarByDate.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                String r = "";
                int pos1 = e.toString().indexOf("(sum): ");
                String ss = e.toString().substring(pos1 + 7);
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
                String bau ="Date "+sss+"\n"+"Total spent: "+ss+" / "+total;
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
