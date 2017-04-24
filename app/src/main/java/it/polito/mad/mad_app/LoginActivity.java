package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference Firebase_DB;
    private boolean user_exists = false;
    private String request = "AUTH_LOGIN";

    /*

        TODO: PROBLEMA di Gestione di Sessione

        Se vedi, entra erroneamente dentro a user != null
        anche se l'utente è stato cancellato dall'auth di
        FireBase.

        (Provato su Emulatore)

        Stesso problema si riscontra su SignIn e Main Activity


        */

    protected void CheckUser(FirebaseUser U) {

        // verifica che l'utente sia presente in DB

        final String uID = U.getUid();

        ValueEventListener SingleEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    System.out.println("++++++Login UTENTE ESISTENTE: " + dataSnapshot.getValue().toString());
                    user_exists = true;

                } else {

                    user_exists = false;

                }

                if (user_exists) {

                    System.out.println("++++++Login UTENTE ESISTENTE E LOGGATO +++++");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class)); //ok
                    finish();

                } else {


                    System.out.println("++++++Login NOPE --> UTENTE NON ESISTENTE +++++");

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

            System.out.println("++++++Login CHECKLOGGED -- QUI che cazzo succede+++++");
            System.out.println(user.getEmail());

            CheckUser(user);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        Firebase_DB = FirebaseDatabase.getInstance().getReference();

        CheckLoggedUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    System.out.println("++++++Login ONAUTH -- QUI che cazzo succede+++++");
                    System.out.println(user.getEmail());

                    CheckUser(user);


                } else { // User is signed out

                    //Log.d(TAG, "onAuthStateChanged:signed_out");

                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        System.out.println("++++++Login CICCIOBANANA+++++");

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

                progressBar.setVisibility(View.VISIBLE);

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

                                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    //startActivity(intent);
                                    //finish();

                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
