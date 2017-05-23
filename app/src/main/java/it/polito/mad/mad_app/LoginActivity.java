package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.mad_app.model.ServiceManager;

import static android.view.View.VISIBLE;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin;
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

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this, SignInActivity.class));
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
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            if (user != null) {

                                                if (!user.isEmailVerified()) {

                                                    Toast.makeText(getApplicationContext(), "Error: You must verify your email!", Toast.LENGTH_LONG).show();
                                                    email_verified = false;
                                                    showSendAgainButton();
                                                    //mAuth.signOut();

                                                } else {

                                                    Intent i = new Intent(LoginActivity.this, ServiceManager.class);
                                                    i.putExtra("class","main");
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
