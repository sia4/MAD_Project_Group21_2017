package it.polito.mad.mad_app;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import it.polito.mad.mad_app.model.PagerAdapter;
import it.polito.mad.mad_app.model.ServiceManager;
import it.polito.mad.mad_app.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DatabaseReference Firebase_DB;
    private boolean user_exists = false;
    private FirebaseAuth mAuth;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth.AuthStateListener mAuthListener;
        String uKey;
        //TODO correzione: 1 sola istanza firebase e utente?
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Firebase_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        CheckLoggedUser();

        //TODO correzione: serve un listener qui?
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.d("Main Activity","onAuthStateChanged IN");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d("Main Activity", "Utente loggato: "+user.toString());
                    CheckUser(user);

                } else {

                    Log.d("Main Activity", "Utente non loggato, torno al login.");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                }
            }
        };
        super.onCreate(savedInstanceState);
        if(!isMyServiceRunning(ServiceManager.class)){
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                Intent intent = new Intent(MainActivity.this, ServiceManager.class);
                //intent.putExtra("class","main");
                startService(intent);
            }
        }
        setContentView(R.layout.activity_user_info);

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //menu con info utente e impostazioni
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        final TextView nav_name = (TextView)hView.findViewById(R.id.nameU);
        final TextView nav_surname = (TextView)hView.findViewById(R.id.surnameU);
        final ImageView nav_photo = (ImageView)hView.findViewById(R.id.imageU);

        Log.d("Main Activity", "Drawer creato");
        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if(currentFUser != null) {

            uKey = currentFUser.getUid();

            if (uKey != null) {

                DatabaseReference myRef = Firebase_DB.child("Users").child(uKey);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User myu = dataSnapshot.getValue(User.class);
                        nav_surname.setText(myu.getSurname() + " " + myu.getName());
                        nav_name.setText(myu.getEmail());
                        String p = myu.getImagePath();
                        Log.d("Main Activity", "Image path: "+p);
                        if (p == null) {
                            Log.d("Main Activity", "Image not present.");
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
                            Log.d("Main Activity", "Set image");
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
                        Log.d("Main Activity", "Failed to read value.", error.toException());
                    }
                });

                //Swap between fragment
                PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
                final TabLayout tabL = (TabLayout) findViewById(R.id.tabs);
                final TabLayout.OnTabSelectedListener OnT=new TabLayout.OnTabSelectedListener(){
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mViewPager.setCurrentItem(tab.getPosition());
                        /*
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
                        transaction.commit();*/

                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                };

                mViewPager = (ViewPager) findViewById(R.id.pager);
                mViewPager.setAdapter(mPagerAdapter);
                tabL.setupWithViewPager(mViewPager);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        tabL.addOnTabSelectedListener(OnT);
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                        /*addOnPageChangeListener(
                        new ViewPager.SimpleOnPageChangeListener() {
                            @Override
                            public void onPageSelected(int position) {
                                // When swiping between pages, select the
                                // corresponding tab.
                                tabL.addOnTabSelectedListener(OnT);
                            }
                        });*/

                /*FragmentManager f = getSupportFragmentManager();
                FragmentTransaction transaction = f.beginTransaction();
                transaction.replace(R.id.main_framelayout, new GroupsFragment());
                transaction.commit();*/


                //floating button
                final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addGroup);

                tabL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mViewPager.setCurrentItem(tab.getPosition());
                        switch (tab.getPosition()) {
                            case 0:
                                fab.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                fab.setVisibility(View.INVISIBLE);
                                break;
                            default:
                                fab.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        FirebaseUser userF = mAuth.getCurrentUser();
        if (id == R.id.nav_logout) {
            Log.d("Main Activity", "Logout");
            mAuth.signOut();

            //TODO a cosa serve questo controllo sull'user @edo?
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                CheckUser(user);
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        } else if (id == R.id.nav_setting && userF!=null) {
            Intent i = new Intent(MainActivity.this, UserInformationActivity.class);
            i.putExtra("userId", userF.getUid());
            i.putExtra("UserInfo","true");
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void CheckUser(FirebaseUser U) {

        Log.d("Main Activity", "Check user");
        final String uID = U.getUid();
        Log.d("Main activity", "Check user - " + U.toString());

        U.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override

            public void onComplete(@NonNull Task<GetTokenResult> task) {

                if (task.isSuccessful()) {

                    ValueEventListener SingleEvent = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {

                                Log.d("Main Activity", "Utente esistente in db: " + dataSnapshot.getValue().toString());
                                user_exists = true;

                            } else {
                                user_exists = false;
                            }

                            if (!user_exists) {

                                Log.d("Main Activity", "Utente non esistente in db.");
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
                    Log.d("Main Activity", "TOKEN ERROR!!!" );
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

            Log.d("Main Activity", "Error: utente nullo!! "+user.toString());

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
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
