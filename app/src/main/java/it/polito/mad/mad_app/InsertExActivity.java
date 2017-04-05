package it.polito.mad.mad_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

public class InsertExActivity extends AppCompatActivity {

    private String name;
    private String description;
    private String category;
    private String currency;
    private float value;
    private String algorithm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_ex_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("New Expense");

        /*Button btn = (Button)findViewById(R.id.EnterButton);

        final EditText Tname = (EditText) findViewById(R.id.Name);
        final EditText Tdescription = (EditText) findViewById(R.id.Description);
        final Spinner Tcategory = (Spinner)findViewById(R.id.Category);
        final Spinner Tcurrency = (Spinner)findViewById(R.id.Currency);
        final Spinner Talgorithm = (Spinner)findViewById(R.id.ChooseAlgorithm);
        final EditText Tvalue = (EditText) findViewById(R.id.value);

        Intent i = getIntent();

        final String Gname = i.getStringExtra("GroupName");
        //final GroupData g = MainData.getInstance().getGroup(Gname);
        btn.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                name = Tname.getText().toString();
                description = Tdescription.getText().toString();
                category = Tcategory.getSelectedItem().toString();
                currency = Tcurrency.getSelectedItem().toString();
                value = Float.parseFloat(Tvalue.getText().toString());
                algorithm = Talgorithm.getSelectedItem().toString();
                MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, (value -value/(MainData.getInstance().getGroup(Gname).getlUsers().size()+1)), algorithm);
                //g.addExpensive(name, description, category, currency, value, algorithm);
                if(algorithm.equals("Alla Romana"))
                    MainData.getInstance().getGroup(Gname).allaRomana(value);

                Intent i = new Intent(InsertExActivity.this, GroupActivity.class);

                i.putExtra("name", Gname);
                //startActivity(i);
                //finish();

                setResult(RESULT_OK, i);
                finish();


            }
        });*/

            /*

            public void onClick(View v) {
                ET.setVisibility(View.INVISIBLE);

                ET.setVisibility(View.VISIBLE);
            } });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:

                final EditText Tname = (EditText) findViewById(R.id.Name);
                final EditText Tdescription = (EditText) findViewById(R.id.Description);
                final Spinner Tcategory = (Spinner)findViewById(R.id.Category);
                final Spinner Tcurrency = (Spinner)findViewById(R.id.Currency);
                final Spinner Talgorithm = (Spinner)findViewById(R.id.ChooseAlgorithm);
                final EditText Tvalue = (EditText) findViewById(R.id.value);

                Intent i1 = getIntent();

                final String Gname = i1.getStringExtra("GroupName");

                name = Tname.getText().toString();
                description = Tdescription.getText().toString();
                category = Tcategory.getSelectedItem().toString();
                currency = Tcurrency.getSelectedItem().toString();
                value = Float.parseFloat(Tvalue.getText().toString());
                algorithm = Talgorithm.getSelectedItem().toString();
                MainData.getInstance().addExpensiveToGroup(Gname, name, description, category, currency, value, algorithm);
                //g.addExpensive(name, description, category, currency, value, algorithm);
                Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);

                i2.putExtra("name", Gname);
                //startActivity(i);
                //finish();

                setResult(RESULT_OK, i2);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
