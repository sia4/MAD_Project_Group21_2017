package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;

public class InsertGroupActivity extends AppCompatActivity {


    private String GroupName;
    private String GroupDescription;
    private UserData u1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_group);

        Button groupbutton = (Button)findViewById(R.id.CreateGroup);
        final EditText Gname = (EditText) findViewById(R.id.GroupName);
        final EditText Gdescription = (EditText) findViewById(R.id.GroupDescription);
        final EditText UserMail = (EditText) findViewById(R.id.User1);
        Button userbutton = (Button) findViewById(R.id.Adduser1);

        userbutton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                u1 = new UserData(UserMail.getText().toString());


            }
        } );

        groupbutton.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                GroupName = Gname.getText().toString();
                GroupDescription = Gdescription.getText().toString();

                //Intent gotomain = new Intent(InsertGroupActivity.this, MainActivity.class);
                MainData.getInstance().addGroup(GroupName, GroupDescription);
                Intent gotomain = new Intent(InsertGroupActivity.this, MainActivity.class);
                //gotomain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startActivity(gotomain);

                setResult(RESULT_OK, null);
                finish();

            }

        });
    }
}
