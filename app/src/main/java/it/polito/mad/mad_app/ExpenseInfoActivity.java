package it.polito.mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ExpenseInfoActivity extends AppCompatActivity {


    public TextView name_ex, date_ex,  value_ex, description_ex,creator_ex, category_ex, currency_ex, myvalue_ex, algorithm_ex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_expense_toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        String name, value, creator, description, category, currency, myvalue, algorithm, date;
        name = i.getStringExtra("name");
        value = i.getStringExtra("value");
        creator = i.getStringExtra("creator");
        description = i.getStringExtra("description");
        category = i.getStringExtra("category");
        currency = i.getStringExtra("currency");
        myvalue = i.getStringExtra("myvalue");
        algorithm = i.getStringExtra("algorithm");
        date = i.getStringExtra("date");

        name_ex = (TextView) findViewById(R.id.exName);
        value_ex = (TextView) findViewById(R.id.exValue);
        creator_ex = (TextView) findViewById(R.id.exCreator);
        description_ex = (TextView) findViewById(R.id.exDescription);
        category_ex = (TextView) findViewById(R.id.exCategory);
        currency_ex = (TextView) findViewById(R.id.exCurrency);
        myvalue_ex = (TextView) findViewById(R.id.exMyvalue);
        algorithm_ex = (TextView) findViewById(R.id.exAlgorithm);
        date_ex = (TextView) findViewById(R.id.exDate);

        name_ex.setText(name);
        value_ex.setText(value);
        creator_ex.setText(creator);
        description_ex.setText(description);
        category_ex.setText(category);
        currency_ex.setText(currency);
        myvalue_ex.setText(myvalue);
        algorithm_ex.setText(algorithm);
        date_ex.setText(date);


    }

}
