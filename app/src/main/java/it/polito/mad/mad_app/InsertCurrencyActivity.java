package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

public class InsertCurrencyActivity extends AppCompatActivity {

    private GroupData GD;

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
        String name = i.getStringExtra("name");
        GD = MainData.getInstance().getGroup(name);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Insert Currency");

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
                final Spinner Currency = (Spinner) findViewById(R.id.Currency);
                final EditText change = (EditText) findViewById(R.id.change);

                if (change.getText().toString().equals("")) {

                    Toast.makeText(InsertCurrencyActivity.this, "Please insert value for change.", Toast.LENGTH_LONG).show();

                } else {

                    Float c = new Float(change.getText().toString());

                    if (c.floatValue() <= 0) {

                        Toast.makeText(InsertCurrencyActivity.this, "Please insert positive value.", Toast.LENGTH_LONG).show();

                    } else if (Currency.getSelectedItem().toString().equals("Select currency")) {

                        Toast.makeText(InsertCurrencyActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();

                    } else {

                        GD.addCurrency(Currency.getSelectedItem().toString(), c);

                        //Intent back = new Intent(InsertCurrencyActivity.this, GroupActivity.class);

                        setResult(RESULT_OK, null);
                        finish();

                    }

                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
