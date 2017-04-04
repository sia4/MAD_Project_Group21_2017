package it.polito.mad.mad_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InsertGroupActivity extends AppCompatActivity {


    private String GroupName;
    private String GroupDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_group);

        Button groupbutton = (Button)findViewById(R.id.CreateGroup);
        final EditText Gname = (EditText) findViewById(R.id.GroupName);
        final EditText Gdescription = (EditText) findViewById(R.id.GroupDescription);

        groupbutton.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                GroupName = Gname.getText().toString();
                GroupDescription = Gdescription.getText().toString();


                MainActivity.getMyData().addGroup(GroupName, GroupDescription);
                //Intent gotomain = new Intent(InsertGroupActivity.this, MainActivity.class);
                //gotomain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startActivity(gotomain);

                setResult(RESULT_OK, null);
                finish();

            }

        });
    }
}
