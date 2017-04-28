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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.mad_app.model.Balance;
import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;

public class BalanceActivity extends AppCompatActivity {

    private Balance b;
    private String auKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        String gname = i.getStringExtra("gname");
        String ukey = i.getStringExtra("uKey");
        String uname = i.getStringExtra("uname");
        float v=Float.parseFloat(i.getStringExtra("value"));

        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;
        auKey=null;
        if(currentFUser != null) {
            auKey = currentFUser.getUid();
        }

        System.out.println("value"+v);
        b=new Balance(ukey,uname,v,gname);
        System.out.println("value"+b.getValue());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.balance_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Balance");

        //Intent i = getIntent();
        String userName = b.getName();
        String userKey = b.getKey();
        String bValue = String.format("%.2f", b.getValue());
        //String currency = i.getStringExtra("currency");

        final TextView textDisplayed = (TextView) findViewById(R.id.uName);
        textDisplayed.setText(userName + " owes you " + bValue + " " /*+ currency*/ + ".\nPlease insert the amount "
                + userName + " gives you"/*("+currency+"):"*/);

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
                        System.out.println("+++++++"+insertValue);
                        b.setValue(insertValue);
                        float o=0-b.getValue();
                        System.out.println("+++++++++"+o);
                        System.out.println("+++++++++"+b.getValue());
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("/Balance/"+b.getgID()+"/"+auKey+"/"+b.getKey()+"/"+"value");
                        myRef.setValue(b.getValue());
                        myRef = database.getReference("/Balance/"+b.getgID()+"/"+b.getKey()+"/"+auKey+"/"+"value");
                        myRef.setValue(o);
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
