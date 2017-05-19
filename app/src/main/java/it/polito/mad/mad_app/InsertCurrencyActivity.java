package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.Currencies;

public class InsertCurrencyActivity extends AppCompatActivity {

    //private GroupData GD;
    private String gId;
    private String gName;
    private Currencies cref = new Currencies();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_currency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.insert_currency_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent i = getIntent();
        gId = i.getStringExtra("groupId");
        gName = i.getStringExtra("groupName");
        Spinner spinner = (Spinner) findViewById(R.id.Currency);
        //GD = MainData.getInstance().getGroup(gId);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Insert Currency");

        final List<String> Currencies = new ArrayList<>();
        Currencies.add("Select currency");
        Currencies.addAll(cref.getCurrenciesCodes());

        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef4 = database4.getReference("Groups").child(gId);
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);

                if (g != null) {
                    System.out.println(g.getCurrencies().keySet());
                    Currencies.removeAll(g.getCurrencies().keySet());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.currency_item, Currencies);
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
                final Spinner Currency = (Spinner) findViewById(R.id.Currency);
                final EditText change = (EditText) findViewById(R.id.change);

                if (change.getText().toString().equals("")) {

                    Toast.makeText(InsertCurrencyActivity.this, "Please insert value for change.", Toast.LENGTH_LONG).show();

                } else {

                    final Float c = new Float(change.getText().toString());

                    if (c.floatValue() <= 0) {

                        Toast.makeText(InsertCurrencyActivity.this, "Please insert positive value.", Toast.LENGTH_LONG).show();

                    } else if (Currency.getSelectedItem().toString().equals("Select currency")) {

                        Toast.makeText(InsertCurrencyActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();

                    } else {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef = database.getReference("Groups").child(gId);
                        myRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);
                                String code = Currency.getSelectedItem().toString();
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
