package it.polito.mad.mad_app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.PolData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

public class PolActivity extends AppCompatActivity {
    private List<String> users = new ArrayList(), usersid = new ArrayList<>();
    private String myname, mysurname;
    private String totu, creator;
    PieChart PiePol;
    float[] yData = {2,3};
    String[] xData = {"Accepts", "Total"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pol);

        Toolbar toolbar = (Toolbar) findViewById(R.id.pol_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pol informations");
        }
        Intent i = getIntent();
        final String GroupName = i.getStringExtra("groupName");
        final String GroupId = i.getStringExtra("groupId");
        final String PolId = i.getStringExtra("polId");
        String text = i.getStringExtra("text");
        final String type = i.getStringExtra("type");
        if(i.getStringExtra("activId")!=null){
            String activId=i.getStringExtra("activId");
            String user=FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase database3 = FirebaseDatabase.getInstance();
            DatabaseReference notRead=database3.getReference("ActivitiesRead").child(user).child(GroupId);
            notRead.child(activId).setValue(true);
        }
        System.out.println("INTENTTTTTTTTTTTTTT "+GroupName+" "+PolId+" "+GroupId);
        TextView poltext = (TextView) findViewById(R.id.polText);
        final TextView totusers = (TextView) findViewById(R.id.unvalue);
        final TextView already = (TextView) findViewById(R.id.AlreadyAccepted);
        final TextView acceptedusers = (TextView) findViewById(R.id.totvalue);
        final Button button = (Button) findViewById(R.id.AcceptPropose);
        poltext.setText(text);

        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.polUsersList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UsersAdapter uAdapter = new UsersAdapter(users);
        userRecyclerView.setAdapter(uAdapter);

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference("Pols").child(GroupId).child(PolId).child("acceptsUsers");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                usersid.clear();
                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                System.out.println("POLLLLLLLLLLLLLLLLLLLL "+map2);
                if(map2!=null) {
                    for (final String k : map2.keySet()){
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(k)) {
                            button.setVisibility(View.GONE);
                            already.setVisibility(View.VISIBLE);
                            already.setTextColor(Color.parseColor("#27B011"));
                        }
                        if(!usersid.contains(k)) {
                            users.add((String) map2.get(k));
                            usersid.add(k);
                        }
                    }


                    uAdapter.notifyDataSetChanged();
                    yData[1] = users.size();
                    ArrayList<PieEntry> yEntrys = new ArrayList<>();
                    ArrayList<String> xEntrys = new ArrayList<>();
                    for(int r=0; r<yData.length; r++){
                        yEntrys.add(new PieEntry(yData[r], r));
                    }


                    for(int r=0; r<xData.length; r++){
                        xEntrys.add(xData[r]);
                    }

                    PieDataSet pieDataSet = new PieDataSet(yEntrys, "Accepts over total");
                    pieDataSet.setSliceSpace(2);
                    pieDataSet.setValueTextSize(12);

                    ArrayList<Integer> color = new ArrayList<>();

                    color.add(Color.parseColor("#D51111"));
                    color.add(Color.parseColor("#27B011"));

                    pieDataSet.setColors(color);
                    Legend legend = PiePol.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                    PieData pieData = new PieData(pieDataSet);
                    PiePol.setData(pieData);
                    PiePol.invalidate();
                }
                else{
                    Toast.makeText(PolActivity.this, "no users found!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        DatabaseReference myRef3 = database2.getReference("Pols").child(GroupId).child(PolId);
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                System.out.println("POLLLLLLLLLLLLLLLLLLLL "+map2);
                if(map2!=null) {
                      totu = (String)map2.get("usersNumber");
                      yData[0] = Integer.parseInt(totu) - users.size();
                      creator = (String)map2.get("creator");
                      totusers.setText(String.format("%d", users.size()));
                      acceptedusers.setText(totu);
                    if(Integer.parseInt(totu)==users.size())
                        already.setText("Propose successful completed");

                    ArrayList<PieEntry> yEntrys = new ArrayList<>();
                    ArrayList<String> xEntrys = new ArrayList<>();
                    for(int r=0; r<yData.length; r++){
                        yEntrys.add(new PieEntry(yData[r], r));
                    }


                    for(int r=0; r<xData.length; r++){
                        xEntrys.add(xData[r]);
                    }

                    PieDataSet pieDataSet = new PieDataSet(yEntrys, "Accepts over total");
                    pieDataSet.setSliceSpace(2);
                    pieDataSet.setValueTextSize(12);

                    ArrayList<Integer> color = new ArrayList<>();

                    color.add(Color.parseColor("#D51111"));
                    color.add(Color.parseColor("#27B011"));

                    pieDataSet.setColors(color);
                    Legend legend = PiePol.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                    PieData pieData = new PieData(pieDataSet);
                    PiePol.setData(pieData);
                    PiePol.invalidate();

                }
                else{
                    Toast.makeText(PolActivity.this, "no users found!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        PiePol = (PieChart) findViewById(R.id.piepol);
        PiePol.setRotationEnabled(true);
        PiePol.setHoleRadius(50f);
        PiePol.setTransparentCircleAlpha(0);
        PiePol.setCenterText("Accepts over total users");
        PiePol.setCenterTextSize(10);
        //PiePol.setDrawEntryLabels(true);


        //yData[0] = users.size();
        //yData[1] = Float.parseFloat(totu);




        PiePol.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                /*
                int pos1 = e.toString().indexOf("(sum): ");
                String ss = e.toString().substring(pos1 + 7);
                for(int r = 0; r<yData.length; r++){
                    if(yData[r]==Float.parseFloat(ss)){
                        pos1 = r;
                    }
                }
                String sss = xData[pos1];
            */
            }

            @Override
            public void onNothingSelected() {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getMyName = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getMyName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        if(mapname!=null) {
                            usersid.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference PolRef = database.getReference("Pols").child(GroupId).child(PolId);
                            PolRef.child("acceptsUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myname+" "+mysurname);

                            DatabaseReference ActRef = database.getReference("Activities");
                            DatabaseReference ActRead=database.getReference("ActivitiesRead");
                            if(type.equals("leavegroup")) {
                                for(String k : usersid) {
                                    if(!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {
                                        ActRef.child(k).push();
                                        String actId=ActRef.getKey();
                                        ActRef.child(k).child(actId).setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " accepted the propose to leave group " + GroupName, Long.toString(System.currentTimeMillis()), "acceptleavegroup", PolId, GroupId));
                                        ActRead.child(k).child(GroupId).child(actId).setValue(false);
                                    }

                                }

                                if(users.size()==(Integer.parseInt(totu)-1)){
                                    for(String k : usersid) {
                                        if (!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        {
                                            ActRef.child(k).push();
                                            String actId=ActRef.getKey();
                                            ActRef.child(k).child(actId).setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " has been successful deleted from group " + GroupName, Long.toString(System.currentTimeMillis()), "acceptleavegroup", PolId, GroupId));
                                            ActRead.child(k).child(GroupId).child(actId).setValue(false);
                                        }
                                    }
                                    database.getReference("Groups").child(GroupId).child("members").child(creator).setValue(false);
                                    database.getReference("Users").child(creator).child("Groups").child(GroupId).child("missing").setValue("yes");
                                    database.getReference("Balance").child(GroupId).child(creator).removeValue();
                                    for(String k:usersid){
                                        database.getReference("Balance").child(GroupId).child(k).child(creator).removeValue();

                                    }
                                }
                            }
                            if(type.equals("deletegroup")){
                                for(String k : usersid) {
                                    if (!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {
                                        ActRef.child(k).push();
                                        String actId=ActRef.getKey();
                                        ActRef.child(k).child(actId).setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " accepted the propose to delete group " + GroupName, Long.toString(System.currentTimeMillis()), "acceptdeletegroup", PolId, GroupId));
                                        ActRead.child(k).child(GroupId).child(actId).setValue(false);
                                    }
                                }
                                if(users.size()==(Integer.parseInt(totu)-1)){
                                    for(String k : usersid) {
                                        if (!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        {
                                            ActRef.child(k).push();
                                            String actId=ActRef.getKey();
                                            ActRef.child(k).child(actId).setValue(new ActivityData(myname + " " + mysurname, "Group " + GroupName + " has been successful deleted", Long.toString(System.currentTimeMillis()), "acceptdeletegroup", PolId, GroupId));
                                            ActRead.child(k).child(GroupId).child(actId).setValue(false);
                                        }

                                    }
                                    for(String k:usersid){
                                        database.getReference("Users").child(k).child("Groups").child(GroupId).child("missing").setValue("yes");
                                    }


                                }
                            }

                            //TODO SE IL NUMERO DI USER CHE HANNO ACCETTATO E' UGUALE AL NUMERO TOTALE ESEGUI AZIONE A SECONDA DEL TIPO DI POL (CANCELLA/ABBANDONA GRUPPO)


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });



        userRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, userRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent intent = new Intent().setClass(view.getContext(), UserInformationActivity.class);
                intent.putExtra("userId", usersid.get(position));
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }
    @Override
    protected void onResume() {
        super.onResume();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = getIntent();

        String GroupName = i.getStringExtra("groupName");
        String GroupId = i.getStringExtra("groupId");
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("groupName", GroupName);
                intent.putExtra("groupId", GroupId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
