package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;

public class BalanceActivity extends AppCompatActivity {

    private String name;
    private float value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.balance_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Balance");

        Intent i = getIntent();
        String userName = i.getStringExtra("uname");
        String bValue = i.getStringExtra("value");
        String currency = i.getStringExtra("currency");

        final TextView textDisplayed = (TextView) findViewById(R.id.uName);
        textDisplayed.setText(userName + " owes you " + bValue + " " + currency + ".\nPlease insert the amount "
                + userName + " gives you("+currency+"):");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = getIntent();
        String groupName = i.getStringExtra("gname");
        String userName = i.getStringExtra("uname");

        switch (item.getItemId()) {

            case android.R.id.home:
                setResult(RESULT_OK, null);
                finish();
                return true;

            case R.id.action_menu_done:

                final EditText iValue = (EditText) findViewById(R.id.iValue);

                if(iValue.getText().toString().equals("")) {
                    Toast.makeText(BalanceActivity.this, "Please insert an amount!", Toast.LENGTH_LONG).show();
                } else {
                    float insertValue = Float.parseFloat(iValue.getText().toString());

                    //if (insertValue > -(MainData.getInstance().getGroup(groupName).getExpense(userName))) {
                       // Toast.makeText(BalanceActivity.this, "The value is too high!", Toast.LENGTH_LONG).show();
                    //} else {
                        MainData.getInstance().getGroup(groupName).updateExpense(userName, insertValue);
                        //    Toast.makeText(BalanceActivity.this, String.valueOf(MainData.getInstance().getGroup(groupName).getExpense(userName)+insertValue), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, null);
                        finish();
                    //}
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
