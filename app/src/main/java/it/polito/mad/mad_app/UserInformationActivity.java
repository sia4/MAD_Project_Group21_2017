package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText nameed, surnameed;
    private ImageView im;
    private boolean flag_name_edited = false;
    private boolean flag_surname_edited = false;
    String uId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_user_toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        uId = i.getStringExtra("userId");

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

        nameed = (EditText) findViewById(R.id.name_u_ed);
        surnameed = (EditText) findViewById(R.id.surname_u_ed);

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


        /*Button button = (Button) findViewById(R.id.sendVerificationEmail);
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
        });*/

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String n = (String)name.getText();
                name.setVisibility(View.GONE);
                nameed.setVisibility(View.VISIBLE);
                nameed.setText(n);
                flag_name_edited = true;

            }
        });

        surname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String n = (String)surname.getText();
                surname.setVisibility(View.GONE);
                surnameed.setVisibility(View.VISIBLE);
                surnameed.setText(n);
                flag_surname_edited = true;

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_menu_done:


                if(flag_name_edited) {
                    Log.d("USERINFO", "Nome cambiato");
                    String newName = nameed.getText().toString();
                    if(newName != null && !newName.equals("")) {
                        Log.d("USERINFO", "Nome cambiato: "+uId+ " " +newName);
                        DatabaseReference myRef = database.getReference("Users/"+uId + "/name");
                        myRef.setValue(newName);
                    }
                }

                if(flag_surname_edited) {
                    String newSurname = surnameed.getText().toString();
                    if(newSurname!= null && !newSurname.equals("")) {
                        DatabaseReference myRef = database.getReference("Users/"+uId + "/surname");
                        myRef.setValue(newSurname);
                    }
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

}
