package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.PolData;

public class PolActivity extends AppCompatActivity {
    private List<String> users = new ArrayList();
    private String myname, mysurname;
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

                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                System.out.println("POLLLLLLLLLLLLLLLLLLLL "+map2);
                if(map2!=null) {
                    for (final String k : map2.keySet()){
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(k)) {
                            button.setVisibility(View.GONE);
                            already.setVisibility(View.VISIBLE);
                            already.setTextColor(Color.parseColor("#27B011"));
                        }
                       users.add((String)map2.get(k));
                    }


                    uAdapter.notifyDataSetChanged();
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
                      totusers.setText((String)map2.get("usersNumber"));
                      acceptedusers.setText(String.format("%d", users.size()));

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


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getMyName = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getMyName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        if(mapname!=null) {
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference PolRef = database.getReference("Pols").child(GroupId).child(PolId);
                            PolRef.child("acceptsUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myname+" "+mysurname);

                            DatabaseReference ActRef = database.getReference("Activities").child(GroupId).push();
                            if(type.equals("leavegroup")) {
                                ActRef.setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " accepted the propose to leave group " + GroupName, new SimpleDateFormat("d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime()), "acceptleavegroup", PolId, GroupId));
                            }
                            if(type.equals("deletegroup")){
                                ActRef.setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " accepted the propose to delete group " + GroupName, new SimpleDateFormat("d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime()), "acceptdeletegroup", PolId, GroupId));
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
