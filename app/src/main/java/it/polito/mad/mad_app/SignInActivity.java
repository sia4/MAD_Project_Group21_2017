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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.mad_app.model.Invite;
import it.polito.mad.mad_app.model.User;



public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputSurname;
    private Button btnSignIn, btnLogIn, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference Firebase_DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        Firebase_DB = FirebaseDatabase.getInstance().getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnLogIn = (Button) findViewById(R.id.log_in_button);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        inputSurname = (EditText) findViewById(R.id.surname);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //TODO dare la possiblità di riottenere la password

        //btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        /*

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        */
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                final String name = inputName.getText().toString().trim();
                final String surname = inputSurname.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(surname)) {
                    Toast.makeText(getApplicationContext(), "Enter Surname!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), getString(R.string.minimum_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignInActivity.this, "User Correctly Registered.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (!task.isSuccessful() || user == null) {
                                    Toast.makeText(SignInActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    writeNewUser(user.getUid(), name, surname, email);
                                    final FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();

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

                                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    final Query quer=database.child("Invites").orderByChild("email");

                                    quer.equalTo(u.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {

                                      @Override
                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                          for (DataSnapshot invitesSnapshot: dataSnapshot.getChildren()) {

                                              Invite is = invitesSnapshot.getValue(Invite.class);
                                              String keyInvite = invitesSnapshot.getKey();
                                              String gId = is.getGroupId();
                                              String gName = is.getGroupName();
                                              String gPath = is.getGroupPath();
                                              String key = u.getUid();
                                              FirebaseDatabase database = FirebaseDatabase.getInstance();
                                              DatabaseReference myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/name/");
                                              myRef.setValue(gName);
                                              myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/imagePath/");
                                              myRef.setValue(gPath);
                                              myRef = database.getReference("/Groups/"+gId+"/members/"+key);
                                              myRef.setValue(true);

                                              DatabaseReference myRef2 = database.getReference("/Invites/");
                                              myRef2.child(keyInvite).removeValue();

                                          }

                                      }

                                        @Override
                                        public void onCancelled(DatabaseError eError) {

                                        }
                                     });

                                    System.out.println("Andiamo al Login");
                                    startActivity(new Intent(SignInActivity.this, LoginActivity.class));
                                    finish();

                                }
                            }

                        });


            }
        });
    }

    private void writeNewUser(String userId, String name, String surname, String email) {

        String username = email + "_" + name + surname;
        User user = new User(username, email, name, surname);

        Firebase_DB.child("Users").child(userId).setValue(user);

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    @Override
    protected void onStart(){
        super.onStart();
        //status="OTHER";
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        //status="OTHER";
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

