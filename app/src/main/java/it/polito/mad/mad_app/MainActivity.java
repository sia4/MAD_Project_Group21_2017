package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.mad_app.model.MainData;

//import android.support.v7.util.ThreadUtil;


/*

        TODO semplice esempio per scrivere

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/Groups/G1/Members/u3");
        myRef.setValue(true);
        myRef = database.getReference("/Groups/G1/Members");



        TODO semplice esempio per leggere

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                System.out.println("Value is: " + map.get("u1"));
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });

*/

/*
        * TODO : Implementare il retrieve dei dati dell'utente.
        *
        * > Dati User
        * > Dati Gruppi a cui è iscritto per popolare fragment
        *
        * Notare che Firebase Auth ha delle informazioni relative agli utenti
        * loggati
        *
        * >> https://firebase.google.com/docs/auth/web/manage-users
        *
        * */

public class MainActivity extends AppCompatActivity {

    private DatabaseReference Firebase_DB;
    private boolean user_exists = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    MainData ad = MainData.getInstance();

    protected void CheckUser(FirebaseUser U) {

        // verifica che l'utente sia presente in DB

        final String uID = U.getUid();

        U.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override

            public void onComplete(@NonNull Task<GetTokenResult> task) {

                if (task.isSuccessful()) {

                    ValueEventListener SingleEvent = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {

                                System.out.println("++++++ UTENTE ESISTENTE IN DB: " + dataSnapshot.getValue().toString());
                                user_exists = true;

                            } else {
                                user_exists = false;
                            }

                            if (!user_exists) {

                                System.out.println("++++++ NOPE --> UTENTE NON ESISTENTE +++++");
                                mAuth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    Firebase_DB.child("Users").child(uID).addListenerForSingleValueEvent(SingleEvent);

                    //OK
                } else {
                    System.out.println("++++++ NOPE --> TOKEN ERROR +++++");
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                }

            }
        });

    }

    protected void CheckLoggedUser() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            System.out.println("++++++QUI che cazzo succede+++++");
            System.out.println(user.toString());

            CheckUser(user);


        } else {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        }

    }


    /*
        public static MainData getMyData() {
            return ad;
        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        Firebase_DB = FirebaseDatabase.getInstance().getReference();

        CheckLoggedUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() { // listener lo istanzio comunque, se ho modifiche che lo triggerano

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                System.out.println("++++++onAuthStateChanged IN+++++");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    System.out.println("++++++QUI che cazzo succede+++++");
                    System.out.println(user.toString());

                    CheckUser(user);

                } else {

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);


        FragmentManager f = getSupportFragmentManager();
        FragmentTransaction transaction = f.beginTransaction();
        transaction.replace(R.id.main_framelayout, new GroupsFragment());
        transaction.commit();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addGroup);
        TabLayout tabL = (TabLayout) findViewById(R.id.tabs);
        tabL.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag;
                switch (tab.getPosition()) {
                    case 0:
                        frag = new GroupsFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        frag = new ActivitiesFragment();

                        fab.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        frag = new GroupsFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                }
                FragmentManager f = getSupportFragmentManager();
                FragmentTransaction transaction = f.beginTransaction();
                transaction.replace(R.id.main_framelayout, frag);
                transaction.commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(
                        getApplicationContext(),InsertGroupActivity.class
                );

                startActivityForResult(intent, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
        }

    }
/*
    protected void onResume(Bundle savedInstanceState) {

        super.onResume();

        Fragment frag = getSupportFragmentManager().findFragmentByTag("GroupsFragment");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frag);
        ft.attach(frag);
        ft.commit();

    }
*/
}
