package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
import it.polito.mad.mad_app.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView t1;
    private TextView t2;
    private DatabaseReference Firebase_DB;
    private boolean user_exists = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    MainData ad = MainData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String uKey = null;
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Firebase_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        CheckLoggedUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() { // listener lo istanzio comunque, se ho modifiche che lo triggerano

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                System.out.println("++++++onAuthStateChanged IN+++++");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    System.out.println("++++++QUI che succede?+++++");
                    System.out.println(user.toString());

                    CheckUser(user);

                } else {

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                }
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        /*Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        final TextView nav_name = (TextView)hView.findViewById(R.id.nameU);
        final TextView nav_surname = (TextView)hView.findViewById(R.id.surnameU);
        final ImageView nav_photo = (ImageView)hView.findViewById(R.id.imageU);

        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if(currentFUser != null) {

            uKey = currentFUser.getUid();

            if (uKey != null) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(uKey);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User myu = dataSnapshot.getValue(User.class);
                        nav_surname.setText(myu.getSurname() + " " + myu.getName());
                        nav_name.setText(myu.getEmail());
                        String p = myu.getImagePath();
                        System.out.println("+++++++++++++++" + p);
                        if (p == null) {
                            Glide.with(getApplicationContext()).load(R.drawable.group_default).asBitmap().centerCrop().into(new BitmapImageViewTarget(nav_photo) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    nav_photo.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                        } else {
                            p=myu.getImagePath();
                            Glide.with(getApplicationContext()).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(nav_photo) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    nav_photo.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                //t.setImageAlpha();


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
                        Intent intent = new Intent(
                                getApplicationContext(), InsertGroupActivity.class
                        );

                        startActivityForResult(intent, 1);

                    }
                });

            }

        }


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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // TODO devi fare il logout
            System.out.println("LOGOUT");
            mAuth.signOut();

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {

                CheckUser(user);

            } else {

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

            }

        } else if (id == R.id.nav_setting) {

            Intent i = new Intent(MainActivity.this, UserInformationActivity.class);

            i.putExtra("userId", mAuth.getCurrentUser().getUid().toString());
            i.putExtra("UserInfo","true");
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void CheckUser(FirebaseUser U) {

        System.out.println("-- Check User -- ");

        final String uID = U.getUid();
        System.out.println(U.toString());

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

                } else {

                    // lol

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

            if (!user.isEmailVerified()) {

                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

            }

        } else {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

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
