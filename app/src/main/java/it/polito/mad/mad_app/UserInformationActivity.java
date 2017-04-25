package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserInformationActivity extends AppCompatActivity {

    private TextView name, surname, email, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String uId=i.getStringExtra("userId");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Information");

        name=(TextView) findViewById(R.id.name_u);
        surname = (TextView) findViewById(R.id.surname_u);
        email = (TextView) findViewById(R.id.email_u);
        username = (TextView) findViewById(R.id.username_u);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map!=null) {
                    name.setText((String)map.get("name"));
                    surname.setText((String)map.get("surname"));
                    email.setText((String)map.get("email"));
                    username.setText((String)map.get("username"));

                    //String p = (String) map.get("imagePath");
                    /*
                    if (p == null) {
                        im.setImageResource(R.drawable.group_default);
                    } else {
                        im.setImageBitmap(BitmapFactory.decodeFile(p));
                    }

                    //if(progressBar.isActivated())
                    //progressBar.setVisibility(View.INVISIBLE);
                    //gAdapter.notifyDataSetChanged();
                    */
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }

}
