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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;

public class InsertGroupActivity extends AppCompatActivity {


    private String GroupName;
    private String GroupDescription;
    private String UserEmail;
    private List<UserData> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_group);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_group_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("New Group");

        //Button groupbutton = (Button)findViewById(R.id.CreateGroup);
        //final EditText Gname = (EditText) findViewById(R.id.GroupName);
        //final EditText Gdescription = (EditText) findViewById(R.id.GroupDescription);
        final EditText Uemail = (EditText) findViewById(R.id.User1);
        Button userbutton = (Button) findViewById(R.id.Adduser1);

        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.usersToAdd);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertGroupActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        final UsersToAddAdapter uAdapter = new UsersToAddAdapter(users);
        userRecyclerView.setAdapter(uAdapter);

        userbutton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                UserEmail = Uemail.getText().toString().toLowerCase();
                UserData ud = MainData.getInstance().findUserByMail(UserEmail);
                if(ud == null) {
                    Toast.makeText(InsertGroupActivity.this, "The user doesn't exist!", Toast.LENGTH_LONG).show();
                } else {
                    Uemail.setText("");
                    users.add(ud);
                    uAdapter.notifyDataSetChanged();
                }
            }
        } );

        /*groupbutton.setOnClickListener(new View.OnClickListener() {


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

        });*/
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
                final EditText Gname = (EditText) findViewById(R.id.GroupName);
                final EditText Gdescription = (EditText) findViewById(R.id.GroupDescription);
                final Spinner Tcurrency = (Spinner) findViewById(R.id.GroupCurrency);

                GroupName = Gname.getText().toString();
                GroupDescription = Gdescription.getText().toString();

                if (GroupName.equals("")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert group name.", Toast.LENGTH_LONG).show();

                } else if (GroupDescription.equals("")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert group description.", Toast.LENGTH_LONG).show();

                } else if (users.isEmpty()) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert at least one other member.", Toast.LENGTH_LONG).show();

                } else if (Tcurrency.getSelectedItem().toString().equals("Select currency")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();

                } else {

                    //Intent gotomain = new Intent(InsertGroupActivity.this, MainActivity.class);
                    GroupData newGroup = MainData.getInstance().addGroup(GroupName, GroupDescription, Tcurrency.getSelectedItem().toString());

                    for (UserData u : users) {
                        newGroup.addUser(u);
                    }
                    newGroup.addUser(MainData.getInstance().returnMyData());

                    Intent gotomain = new Intent(InsertGroupActivity.this, MainActivity.class);
                    //gotomain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    //startActivity(gotomain);

                    setResult(RESULT_OK, null);
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
