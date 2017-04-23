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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    MainData ad = MainData.getInstance();
/*
    public static MainData getMyData() {
        return ad;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

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


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { //controllo che l'utente sia loggato

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

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
