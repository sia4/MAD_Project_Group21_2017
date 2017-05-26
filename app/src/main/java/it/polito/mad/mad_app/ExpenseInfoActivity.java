package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.ExpenseData;

public class ExpenseInfoActivity extends AppCompatActivity {

    int n;
    public TextView name_ex,date_ex, s_ex, value_ex, description_ex,creator_ex, category_ex, currency_ex, myvalue_ex, algorithm_ex;
    private String myname, mysurname, name, value, creator, description, category, currency, myvalue, algorithm, date, exid, groupName;
    private Map<String, Float> usermap = new TreeMap<>();
    private ImageView image_info;
    private Map<String, Map<String, Map<String, Object>>> balancemap;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Map<String, String> usermapTemp;
    private String DefaultCurrency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_expense_toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expense Information");

        exid = i.getStringExtra("ExpenseId");
        groupName = i.getStringExtra("groupName");
        final String GroupId = i.getStringExtra("groupId");
        //DefaultCurrency = i.getStringExtra("defaultcurrency");

        image_info=(ImageView) findViewById(R.id.im_ex_info);
        s_ex=(TextView) findViewById(R.id.iMyvalue);
        name_ex = (TextView) findViewById(R.id.exName);
        value_ex = (TextView) findViewById(R.id.exValue);
        creator_ex = (TextView) findViewById(R.id.exCreator);
        description_ex = (TextView) findViewById(R.id.exDescription);
        category_ex = (TextView) findViewById(R.id.exCategory);
        //currency_ex = (TextView) findViewById(R.id.exCurrency);
        myvalue_ex = (TextView) findViewById(R.id.exMyvalue);
        algorithm_ex = (TextView) findViewById(R.id.exAlgorithm);
        date_ex = (TextView) findViewById(R.id.exDate);
        final TextView Tdeny = (TextView) findViewById(R.id.exContested);

        final Button button = (Button) findViewById(R.id.exDeny);
        final Button addbutton = (Button) findViewById(R.id.addNewExpense);
        final TextView Tdenydescr = (TextView) findViewById(R.id.denydescription);

        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef4 = database4.getReference("Expenses").child(GroupId).child(exid);
        int flagcontest = 0;
        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                System.out.println("mappaaaaaaaaaaaaaa"+ map);
                if(map!=null) {
                    String cont = (String)map.get("contested");
                    if(cont.equals("yes")){
                        Tdeny.setVisibility(View.VISIBLE);
                        button.setVisibility(View.GONE);
                        Tdenydescr.setVisibility(View.GONE);


                    }
                    if(map.get("imagePath")!=null) {
                        String p=map.get("imagePath").toString();
                        image_info.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext()).load(p).into(image_info);
                    }
                    name = (String)map.get("name");
                    System.out.println("nameeeeeeeeeeeeeee"+name);
                    description = (String)map.get("description");
                    if(description.equals("expense retrieved")){
                        button.setVisibility(View.GONE);
                        Tdenydescr.setVisibility(View.GONE);
                    }
                    value = (String)map.get("value");
                    creator = (String)map.get("creator");
                    if(cont.equals("yes")){
                        addbutton.setVisibility(View.VISIBLE);
                    }
                    category = (String)map.get("category");
                    currency = (String)map.get("currency");
                    DefaultCurrency = (String) map.get("defaultcurrency");

                    algorithm = (String)map.get("algorithm");
                    date = (String)map.get("date");
                    System.out.println("groupName Expenseinfo: "+ groupName);
                    name_ex.setText(name);
                    System.out.println("ExpenseInfo - " + map.toString());
                    String tmp = (String) map.get("currency");
                    Currencies c_tmp = new Currencies();
                    String symbol = c_tmp.getCurrencySymbol(tmp);
                    value_ex.setText(value + " " + symbol);
                    creator_ex.setText(creator);
                    if(description.equals(""))
                        description_ex.setText("  -");
                    else
                        description_ex.setText(description);
                    category_ex.setText(category);
                    //currency_ex.setText(currency);

                    s_ex.setText("Your part:");
                    algorithm_ex.setText(algorithm);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                    Date resultdate = new Date(new Long(date));

                    date_ex.setText(sdf.format(resultdate));



                }
                else{
                    Toast.makeText(ExpenseInfoActivity.this, "no expense found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference("Expenses").child(GroupId).child(exid);
        myRef2.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usermapTemp = (Map<String, String>) dataSnapshot.getValue();

                if(usermapTemp !=null) {
                    for(Map.Entry<String, String> e : usermapTemp.entrySet()) {
                        usermap.put(e.getKey(), Float.parseFloat(e.getValue()));
                    }
                    if (DefaultCurrency != null) {

                        if (!DefaultCurrency.equals("")) {
                            Currencies tmpc = new Currencies();
                            String tmp_symbol = tmpc.getCurrencySymbol(DefaultCurrency);
                            myvalue_ex.setText(String.format(Locale.US, "%.2f", usermap.get(mAuth.getCurrentUser().getUid())) + " " + tmp_symbol);
                        } else {
                            myvalue_ex.setText(String.format(Locale.US, "%.2f", usermap.get(mAuth.getCurrentUser().getUid())));
                        }

                    } else {
                        myvalue_ex.setText(String.format(Locale.US, "%.2f", usermap.get(mAuth.getCurrentUser().getUid())));
                    }


                    if(usermap.get(mAuth.getCurrentUser().getUid())>0){
                        s_ex.setTextColor(Color.parseColor("#27B011"));
                        myvalue_ex.setTextColor(Color.parseColor("#27B011"));

                    }
                    else{
                        s_ex.setTextColor(Color.parseColor("#D51111"));
                        myvalue_ex.setTextColor(Color.parseColor("#D51111"));

                    }
                    System.out.println("usermapppppppppppppppp " + usermap);
                    } else{
                    Toast.makeText(ExpenseInfoActivity.this, "no users found!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Balance").child(GroupId);

        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                balancemap = (Map<String, Map<String, Map<String, Object>>>) dataSnapshot.getValue();
                if(balancemap==null) {
                    Toast.makeText(ExpenseInfoActivity.this, "no balance found!", Toast.LENGTH_LONG).show();
                }
                else{
                    System.out.println("balancemapppppppppppppppp " + balancemap);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Tdeny.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                Tdenydescr.setVisibility(View.GONE);

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Expenses").child(GroupId);
                myRef.child(exid).child("contested").setValue("yes");
                DatabaseReference myRef2 = myRef.push();


                myRef2.setValue(new ExpenseData(name + "(retrieve)", "expense retrieved", category, currency, value, "0.00", algorithm, DefaultCurrency));
                myRef2.child("creator").setValue(creator);
                myRef2.child("missing").setValue("yes");
                //myRef2.child("value").setValue(value);
                myRef2.child("contested").setValue("no");
                myRef2.child("users").setValue(usermapTemp);


                DatabaseReference getMyName = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
                getMyName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        System.out.println("mapnameeeeeeeeeeeeeeeeeeee"+mapname);
                        if(mapname!=null) {
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference ActRef = database.getReference("Activities");
                            for(String k : usermap.keySet()) {
                                if(!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                   ActRef.child(k).push().setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " contested expense " + name + " in group " + groupName, Long.toString(System.currentTimeMillis()), "contest", exid, GroupId));
                            }
                        }
                        else{
                            Log.d("ExpenseInfo", "balancemapppppppppppppppp " + balancemap);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                final FirebaseDatabase database5 = FirebaseDatabase.getInstance();
                DatabaseReference myRef5 = database5.getReference("Balance").child(GroupId);
                float value1, value2, value3, value4;
                for( Map.Entry<String, Float> e : usermap.entrySet()) {
                    if(!e.getKey().equals(mAuth.getCurrentUser().getUid())&&balancemap!=null) {

                        value1 = Float.parseFloat((String)balancemap.get(mAuth.getCurrentUser().getUid()).get(e.getKey()).get("value"));
                        value2 = Float.parseFloat((String)balancemap.get(e.getKey()).get(mAuth.getCurrentUser().getUid()).get("value"));
                        //value3 = new Float (Float.parseFloat(usermap.get(k).toString()));
                        System.out.println("buttonnnnnnn1 "+value1);
                        System.out.println("buttonnnnnnn2 "+value2);
                        //System.out.println("buttonnnnnnn3 "+value3);
                        System.out.println("USERMAPPPPPPP" + e.getValue());
                        value3 = e.getValue();
                        value1 = value1 - value3;
                        value2 = value2 + e.getValue();
                        myRef5.child(mAuth.getCurrentUser().getUid()).child(e.getKey()).child("value").setValue(String.format(Locale.US, "%.2f",value1));
                        myRef5.child(e.getKey()).child(mAuth.getCurrentUser().getUid()).child("value").setValue(String.format(Locale.US, "%.2f",value2));


                    }

                }

            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), InsertExActivity.class
                );
                intent.putExtra("groupId", GroupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("name", name);
                intent.putExtra("value", value);
                intent.putExtra("description", description);
                intent.putExtra("defaultcurrency", DefaultCurrency);
                startActivityForResult(intent, 1);
                finish();
            }
        });


        }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
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
