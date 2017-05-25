package it.polito.mad.mad_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import it.polito.mad.mad_app.model.Invite;
import it.polito.mad.mad_app.model.ServiceManager;
import it.polito.mad.mad_app.model.UserData;

import static android.view.View.VISIBLE;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnResetPwd;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference Firebase_DB;
    private boolean user_exists = false;
    private boolean email_verified = true;
    private boolean showing_button = false;

    protected void showSendAgainButton() {

        if (!showing_button) {

            Button button = (Button) findViewById(R.id.sendVerificationEmail);
            button.setVisibility(VISIBLE);
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

            return;

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        Firebase_DB = FirebaseDatabase.getInstance().getReference();

        email_verified = true;

        CheckLoggedUser();

        if (!email_verified) {
            showSendAgainButton();
        }

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnResetPwd= (Button) findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this, SignInActivity.class));
            }
        });

        btnResetPwd.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                final String email = inputEmail.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Send Password Reset Email");

                final EditText input = new EditText(LoginActivity.this);
                input.setHint("Email");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setPadding(50,50,50,50);
                if(!email.equals(""))
                    input.setText(email);
                //TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                //params.setMargins(50,50,50,50);
                //input.setLayoutParams(params);
                builder.setView(input);

                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(input.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
                        } else {

                            FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Reset email sent to " + input.getText().toString(), Toast.LENGTH_LONG).show();
                                                Log.d("Login Activity", "Email sent.");
                                            }
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_LONG).show();
                    return;
                }


                progressBar.setVisibility(VISIBLE);

                //Authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {

                                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(getApplicationContext(), "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();

                                    }

                                } else {
                                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            if (user != null) {

                                                if (!user.isEmailVerified()) {

                                                    Toast.makeText(getApplicationContext(), "Error: You must verify your email!", Toast.LENGTH_LONG).show();
                                                    email_verified = false;
                                                    showSendAgainButton();
                                                    //mAuth.signOut();

                                                } else {


                                                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                                    final Query quer=database.child("Invites").orderByChild("email");
                                                    Log.d("Login Activity", "Invites 1");
                                                    Log.d("Login Acitivity", "User "+user.getEmail());
                                                    quer.equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {

                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot invitesSnapshot: dataSnapshot.getChildren()) {

                                                                Log.d("Login Activity", "Invites 2");

                                                                Invite is = invitesSnapshot.getValue(Invite.class);
                                                                String keyInvite = invitesSnapshot.getKey();
                                                                final String gId = is.getGroupId();
                                                                String gName = is.getGroupName();
                                                                String gPath = is.getGroupPath();
                                                                final String key = user.getUid();
                                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                DatabaseReference myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/name/");
                                                                myRef.setValue(gName);
                                                                myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/imagePath/");
                                                                myRef.setValue(gPath);
                                                                myRef = database.getReference("/Users/" + key + "/Groups/" + gId + "/lastOperation/");
                                                                myRef.setValue("You have been invited to join the group.");
                                                                myRef = database.getReference("/Users/" + key + "/Groups/" + gId + "/dateLastOperation/");
                                                                myRef.setValue(Long.toString(System.currentTimeMillis()));
                                                                myRef = database.getReference("/Groups/"+gId+"/members/"+key);
                                                                myRef.setValue(true);

                                                                DatabaseReference myRef2 = database.getReference("/Invites/");
                                                                myRef2.child(keyInvite).removeValue();


                                                                FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                                                DatabaseReference myRefUsers = database2.getReference("Groups").child(gId).child("members");

                                                                myRefUsers.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                                        Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                                                                        if(map2!=null) {

                                                                            //map2.put(key, key); //aggiungo user corrente
                                                                            for (final String k : map2.keySet()){
                                                                                FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                                                                                DatabaseReference myRef3 = database3.getReference("Users").child(k);
                                                                                myRef3.addValueEventListener(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();
                                                                                        if(map3!=null) {
                                                                                            String s = String.format("user %s added\n", (String)map3.get("name"));
                                                                                            System.out.println(s);
                                                                                            UserData u = new UserData("aaaa", (String)map3.get("name"), (String)map3.get("surname"), 5555);
                                                                                            u.setuId(k);

                                                                                            System.out.println("/Balance/" + gId + "/" + key + "/" + u.getuId() + "/" + "name");
                                                                                            FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                                                                                            database3.getReference("/Balance/" + gId + "/" + key + "/" + u.getuId() + "/" + "name").setValue(u.getName() + " " + u.getSurname());

                                                                                            database3.getReference("/Balance/" + gId + "/" + key + "/" + u.getuId() + "/" + "value").setValue("0.00");

                                                                                            database3.getReference("/Balance/" + gId + "/" + u.getuId() + "/" + key + "/" + "name").setValue("");
                                                                                            database3.getReference("/Balance/" + gId + "/" + u.getuId() + "/" + key + "/" + "value").setValue("0.00");


                                                                                        }
                                                                                        else{
                                                                                            //Toast.makeText(InsertUserToGroupActivity.this, "no user key found!", Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });


                                                                            }

                                                                        }
                                                                        else{
                                                                            //Toast.makeText(InsertUserToGroupActivity.this, "no users found!", Toast.LENGTH_LONG).show();

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

                                                        @Override
                                                        public void onCancelled(DatabaseError eError) {

                                                        }
                                                    });

                                                    Intent i = new Intent(LoginActivity.this, ServiceManager.class);
                                                    startService(i);
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }

                                            }


                                }
                            }
                        });
            }
        });
    }

    protected void CheckUser(FirebaseUser U) {

        // verifica che l'utente sia presente in DB

        final String uID = U.getUid();

        if (!U.isEmailVerified()) {
            email_verified = false;
            Log.d("Login Activity","Email for User:" + U.toString() + " is not verified");
        }

        ValueEventListener SingleEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    user_exists = true;

                } else {

                    user_exists = false;

                }

                if (user_exists) {

                    if (email_verified) {
                        //TODO start here the service
                        Intent i = new Intent(LoginActivity.this, ServiceManager.class);
                        i.putExtra("class","main");
                        startService(i);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)); //ok
                        finish();

                    }

                } else {

                    mAuth.signOut();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class)); //refresh
                    finish();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        Firebase_DB.child("Users").child(uID).addListenerForSingleValueEvent(SingleEvent);

    }

    protected void CheckLoggedUser() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            CheckUser(user); // in this case, it performs also the check on email

        }


    }

    @Override
    protected void onStart(){
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
