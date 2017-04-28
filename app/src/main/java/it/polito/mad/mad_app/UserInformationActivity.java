package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserInformationActivity extends AppCompatActivity {

    private TextView name, surname, email, username;
    private ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_user_toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String uId=i.getStringExtra("userId");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("User Information");
        } else {
            System.out.println("ELLO?");
        }

        name=(TextView) findViewById(R.id.name_u);
        surname = (TextView) findViewById(R.id.surname_u);
        email = (TextView) findViewById(R.id.email_u);
        //username = (TextView) findViewById(R.id.username_u);
        im = (ImageView) findViewById(R.id.im_u);

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
                    //username.setText((String)map.get("username"));
                    String p = (String) map.get("imagePath");
                    //TODO controllare che l'immagine sia presa!!
                    if (p == null) {
                        im.setImageResource(R.drawable.group_default);
                    } else {
                        im.setImageBitmap(BitmapFactory.decodeFile(p));
                    }
                    //if(progressBar.isActivated())
                    //progressBar.setVisibility(View.INVISIBLE);
                    //gAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        Button button = (Button) findViewById(R.id.sendVerificationEmail);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();

                u.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email verification sent.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error sending email verification.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

}
