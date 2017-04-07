package it.polito.mad.mad_app;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;

public class InsertExActivity extends AppCompatActivity {

    private String name;
    private String description;
    private String category;
    private String currency;
    private float value;
    private String algorithm;
    private List<UserData> users = new ArrayList<>();
    static private RecyclerView userRecyclerView;
    static private AlgorithmParametersAdapter uAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_ex_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("New Expense");

        userRecyclerView = (RecyclerView) findViewById(R.id.algorithmParameters);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertExActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        final String Gname = intent.getStringExtra("GroupName");
        users = MainData.getInstance().getGroup(Gname).getlUsers();
        //users.add(0, new UserData("null", "Me", "", 000));
        final Spinner Talgorithm = (Spinner)findViewById(R.id.ChooseAlgorithm);
        final EditText Tvalue = (EditText) findViewById(R.id.value);
        Talgorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==1) {
                        uAdapter = new AlgorithmParametersAdapter(users, position, 10);
                        userRecyclerView.setAdapter(uAdapter);
                    }
                    else if(position == 2){

                        uAdapter = new AlgorithmParametersAdapter(users, position, Float.parseFloat(Tvalue.getText().toString()));
                        userRecyclerView.setAdapter(uAdapter);
                    }
                    else
                    {
                        userRecyclerView.setAdapter(new AlgorithmParametersAdapter(new ArrayList<UserData>(), position, 10));
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );

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
                Intent i1 = getIntent();
                final String Gname = i1.getStringExtra("GroupName");

                final EditText Tname = (EditText) findViewById(R.id.Name);
                final EditText Tdescription = (EditText) findViewById(R.id.Description);
                final Spinner Tcategory = (Spinner) findViewById(R.id.Category);
                final Spinner Tcurrency = (Spinner) findViewById(R.id.Currency);
                final Spinner Talgorithm = (Spinner) findViewById(R.id.ChooseAlgorithm);
                final EditText Tvalue = (EditText) findViewById(R.id.value);


                int flagok = 1;
                name = Tname.getText().toString();
                description = Tdescription.getText().toString();
                category = Tcategory.getSelectedItem().toString();
                currency = Tcurrency.getSelectedItem().toString();
                algorithm = Talgorithm.getSelectedItem().toString();
                if(name.equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert name.", Toast.LENGTH_LONG).show();
                } else if(description.equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert description.", Toast.LENGTH_LONG).show();
                } else if(currency.equals("Select currency")) {
                    Toast.makeText(InsertExActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();
                } else if(Tvalue.getText().toString().equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert value.", Toast.LENGTH_LONG).show();
                } else {

                    value = Float.parseFloat(Tvalue.getText().toString());

                    if (algorithm.equals("Alla Romana")) {
                        MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, value - (value / (MainData.getInstance().getGroup(Gname).getlUsers().size() + 1)), algorithm);
                        MainData.getInstance().getGroup(Gname).allaRomana(value, currency);
                        flagok = 1;
                    }
                    else{
                        flagok=0;
                        Toast.makeText(InsertExActivity.this, "Just Alla Romana algorithm is already implemented.", Toast.LENGTH_LONG).show();
                    }
                //TODO IL CODICE SOTTOSTANTE SONO GLI ALGORITMI BY PERCENTUAGE E BY IMPORT, COMMENTATO PERCHE' CRASHA; DA RISOLVERE
                /*
                else{
                    float algValue, algSum=0, meValue=0;
                    int i;
                    for(i = 0; i< uAdapter.getItemCount(); i++){
                        View view = userRecyclerView.getChildAt(i);
                        TextView TextName = (TextView)view.findViewById(R.id.alg_name);
                        EditText EditValue = (EditText)view.findViewById(R.id.alg_value);
                        String userName = TextName.getText().toString();
                        algValue = Float.parseFloat(EditValue.getText().toString());
                        if(i==0) meValue = algValue;
                        if(algorithm.equals("by percentuage"))
                            MainData.getInstance().getGroup(Gname).getuPercentuageMap().put(userName, algValue);
                        else
                            MainData.getInstance().getGroup(Gname).getuImportMap().put(userName, algValue);
                        algSum += algValue;
                    }

                    if((algorithm.equals("by percentuage") && algSum==100)) {
                        MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, value - (value*meValue/100), algorithm);
                        MainData.getInstance().getGroup(Gname).byPercentuage(value, currency);
                        flagok = 1;
                    }

                    if((algorithm.equals("by import") && algSum==value)) {
                        MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, (value- meValue) , algorithm);
                        MainData.getInstance().getGroup(Gname).byImport(value, currency);
                        flagok = 1;
                    }

                    if((algorithm.equals("by percentuage") && algSum!=100)){
                        flagok = 0;
                        String text = String.format("Percentuage sum values must be equal to 100! (now: %.2f) i = %d", algSum, i);
                        Toast.makeText(InsertExActivity.this, text, Toast.LENGTH_LONG).show();
                    }
                    if((algorithm.equals("by import") && algSum!=value)){
                        flagok = 0;
                        Toast.makeText(InsertExActivity.this, "Import sum values must be equal to the total value!", Toast.LENGTH_LONG).show();
                    }

                }
                */
                    if (flagok == 1) {
                        Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);
                        i2.putExtra("name", Gname);
                        setResult(RESULT_OK, i2);
                        finish();

                        return true;
                    } else {
                        return super.onOptionsItemSelected(item);
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
