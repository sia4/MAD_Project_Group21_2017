package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.Currencies;

public class InsertCurrencyActivity extends AppCompatActivity {

    //private GroupData GD;
    private String gId;
    private String gName;
    private Currencies cref = new Currencies();
    private List<String> changes_l = new ArrayList<>();
    private String dfltcurrency;
    private CurrenciesAdapter cAdapter;
    private List<String> Currencies = new ArrayList<>();
    private TextView frase;

    private void retrieveData() {

        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef4 = database4.getReference("Groups").child(gId);
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);

                if (g != null) {
                    System.out.println(g.getCurrencies().entrySet());
                    Currencies.removeAll(g.getCurrencies().keySet());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        gId = i.getStringExtra("groupId");
        gName = i.getStringExtra("groupName");
        dfltcurrency = i.getStringExtra("defaultcurrency");

        retrieveData();
        //frase = (TextView) findViewById(R.id.frase);

        setContentView(R.layout.activity_insert_currency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.insert_currency_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Insert Currency");

        final RecyclerView CurrencyRecyclerView = (RecyclerView) findViewById(R.id.changes);
        final Spinner spinner = (Spinner) findViewById(R.id.Currency);
        final EditText change = (EditText) findViewById(R.id.change);
        final TextView default_insert = (TextView) findViewById(R.id.default_insert);
        default_insert.setText(dfltcurrency + " to:");
        CurrencyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Currencies.add("Select currency");
        Currencies.addAll(cref.getCurrenciesCodes());

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://api.fixer.io/latest?base=" + dfltcurrency;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changes_l.clear();
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    System.out.println(response.toString());
                                    String last_update = response.getString("date");
                                    //frase.setText("Changes referred to "+dfltcurrency+" - Last Update: "+last_update);
                                    JSONObject rates = response.getJSONObject("rates");
                                    for (String c : Currencies) {
                                        if (c.equals(response.getString("base")) || c.equals("Select currency")) {
                                            continue;
                                        }
                                        //changes_l.add(c + " " + rates.getString(c.toString()));
                                        if(spinner.getSelectedItem().toString().equals(c)){
                                            change.setText(rates.getString(c.toString()));
                                        }

                                    }

                                    System.out.println("Changes:" + changes_l.toString());

                                    cAdapter = new CurrenciesAdapter(changes_l);
                                    CurrencyRecyclerView.setAdapter(cAdapter);
                                    cAdapter.notifyDataSetChanged();

                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                            }
                        });
                queue.add(jsObjRequest);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.currency_item, Currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_menu_done:
                final Spinner cspinner = (Spinner) findViewById(R.id.Currency);
                final EditText change = (EditText) findViewById(R.id.change);

                if (change.getText().toString().equals("")) {

                    Toast.makeText(InsertCurrencyActivity.this, "Please insert value for change.", Toast.LENGTH_LONG).show();

                } else {

                    final Float c = new Float(change.getText().toString());

                    if (c.floatValue() <= 0) {

                        Toast.makeText(InsertCurrencyActivity.this, "Please insert positive value.", Toast.LENGTH_LONG).show();

                    } else if (cspinner.getSelectedItem().toString().equals("Select currency")) {

                        Toast.makeText(InsertCurrencyActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();

                    } else {

                        final String code = cspinner.getSelectedItem().toString();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef = database.getReference("Groups").child(gId);
                        myRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);
                                //String code = cref.getCurrencyCode(Currency.getSelectedItem().toString());
                                if (!g.getCurrencies().containsKey(code)) {
                                    g.addCurrency(code, c);
                                    myRef.setValue(g);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                //log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

                        Intent back = new Intent(InsertCurrencyActivity.this, GroupActivity.class);
                        back.putExtra("groupId", gId);
                        back.putExtra("groupName", gName);
                        setResult(RESULT_OK, back);
                        finish();

                    }

                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
