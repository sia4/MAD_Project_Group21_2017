package it.polito.mad.mad_app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.PagerAdapterGroup;

import static it.polito.mad.mad_app.model.ImageMethod.circle_image;


public class GroupActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private String gName, gKey,gImage, gArchive;
    private ListView lv;
    private float tmppos=0;
    private float tmpneg=0;
    private String defaultcurrency;
    private Map<String,Map<String,Object>> notification=new TreeMap<>();
    Float pos = (float)10.2;//datigruppo.getPosExpenses();
    Float neg = (float)10.2;//datigruppo.getNegExpenses();
    private Map<String, Map<String, Object>> balancemap;
    private Group g;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
                intent.putExtra("groupId", gKey);
                intent.putExtra("groupName", gName);
                intent.putExtra("imagePath", gImage);
                startActivityForResult(intent, 1);
            }
        });
        gKey = intent.getStringExtra("groupId");
        gName = intent.getStringExtra("groupName");
        gImage= intent.getStringExtra("imagePath");
        gArchive = intent.getStringExtra("archive");

        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database3 = FirebaseDatabase.getInstance();

        System.out.println("---->intent"+intent);
        System.out.println("---->intent boooooooo"+intent.getExtras());

        DatabaseReference myRef = database3.getReference("Groups").child(gKey);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);
                if (g != null) {
                    defaultcurrency = g.getPrimaryCurrency();
                    System.out.println("DFLT # GroupActvity - L87" + defaultcurrency);
                    Currencies c = new Currencies();
                    MainData.getInstance().setDefaultCurrencyForStats(c.getCurrencySymbol(defaultcurrency));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        final Bundle b = new Bundle();
        b.putString("GroupId", gKey);
        b.putString("GroupName", gName);
        b.putString("imagePath", gImage);


        final Query notRead=database3.getReference("Activities").child(user).orderByChild("groupId");
        notRead.equalTo(gKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notification=(Map<String, Map<String,Object>>) dataSnapshot.getValue();
                if(notification!=null){
                    Log.d("GroupActivity","lista di notifiche"+notification);
                    Set<String> ActiveID=notification.keySet();
                    FirebaseDatabase read = FirebaseDatabase.getInstance();
                    DatabaseReference readRef=read.getReference("ActivitiesRead").child(user).child(gKey);
                    for(String id:ActiveID){
                        readRef.child(id).setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(gImage==null) {

            /*gImage=g.getImagePath();
            Glide
                    .with(getApplicationContext())
                    .load(gImage)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(60,60) {
                              @Override
                              public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                  RoundedBitmapDrawable circularBitmapDrawable =
                                          RoundedBitmapDrawableFactory.create(getResources(), resource);
                                  circularBitmapDrawable.setCircular(true);
                                  getSupportActionBar().setLogo(circularBitmapDrawable);
                              }
                          });
            */

            DatabaseReference Ref_imagePath = database3.getReference("Groups").child(gKey).child("imagePath");

            if (Ref_imagePath == null) {
                getSupportActionBar().setLogo(R.drawable.group_default);
            } else {
                Ref_imagePath.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        gImage = dataSnapshot.getValue(String.class);
                        Glide
                                .with(getApplicationContext())
                                .load(gImage)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>(60, 60) {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        getSupportActionBar().setLogo(circularBitmapDrawable);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        if (gImage != null && gImage.equals("")) {
            Glide
                    .with(getApplicationContext())
                    .load(R.drawable.group_default)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(60, 60) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            getSupportActionBar().setLogo(circularBitmapDrawable);
                        }
                    });
        }

        //final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Balance").child(gKey).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                balancemap = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                if(balancemap!=null) {
                    System.out.println("MAPPAAAAAAAAAAHAHHAHAH +" + balancemap);
                    for(String u : balancemap.keySet()){
                        float tttt = Float.parseFloat(balancemap.get(u).get("value").toString());
                        if(tttt>=0)
                            tmppos +=  tttt;
                        else
                            tmpneg +=tttt;
                    }

                    String subtitle = "";
                    Currencies c_tmpp = new Currencies();
                    String symboll = "";
                    if (defaultcurrency != null) {
                        symboll = c_tmpp.getCurrencySymbol(defaultcurrency);
                        MainData.getInstance().setDefaultCurrencyForStats(symboll);
                    }
                    subtitle += " " + "They Owe You: " + String.format(Locale.US, "%.2f", tmppos) + " " + symboll + " - You Owe: " + String.format(Locale.US, "%.2f", tmpneg) + " " + symboll;
                    getSupportActionBar().setSubtitle(subtitle);
                    Glide
                            .with(getApplicationContext())
                            .load(gImage)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(60,60) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    getSupportActionBar().setLogo(circularBitmapDrawable);
                                }
                            });
                    try{
                        Field field = Toolbar.class.getDeclaredField( "mSubtitleTextView" );
                        field.setAccessible( true );
                        TextView subtitleTextView = (TextView)field.get( toolbar );
                        subtitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        subtitleTextView.setFocusable(true);
                        subtitleTextView.setFocusableInTouchMode(true);
                        subtitleTextView.requestFocus();
                        subtitleTextView.setSingleLine(true);
                        subtitleTextView.setSelected(true);
                        subtitleTextView.setMarqueeRepeatLimit(1);
                        //subtitleTextView.setMarqueeRepeatLimit(-1); //continua all'infinito
                    }catch (NoSuchFieldException e) {
                    } catch (IllegalAccessException e) {
                    }

                }
                else{
                    Toast.makeText(GroupActivity.this, "no balance found!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        getSupportActionBar().setTitle(" "+gName);
        //String subtitle = "";
        //subtitle +=" "+ "They Owe You: " + String.valueOf(tmppos)+ " - You Owe: " + String.valueOf(tmpneg);



        //getSupportActionBar().setSubtitle(subtitle);

        //Drawable dr = getResources().getDrawable(R.drawable.group_default);
        //toolbar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.group_default));
        //toolbar.setNavigationIcon(d);
        //getSupportActionBar().setIcon(R.drawable.group_default);
        //getSupportActionBar().setLogo(R.);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        System.out.println(" DFLT # GroupActivity - L251 : " + defaultcurrency);
        b.putString("defaultcurrency", defaultcurrency);
        b.putString("gKey", gKey);
        PagerAdapterGroup mPagerAdapter = new PagerAdapterGroup(getSupportFragmentManager(),b);
        mViewPager = (ViewPager) findViewById(R.id.pager_group);
        mViewPager.setAdapter(mPagerAdapter);
        /*Fragment hfrag = new HistoryFragment();
        hfrag.setArguments(b);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        transaction.replace(R.id.group_framelayout, hfrag);
        transaction.commit();*/
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final TabLayout.OnTabSelectedListener OnT=new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                       /* switch (tab.getPosition()) {
                            case 0:
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
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsGroup);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.addOnTabSelectedListener(OnT);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag;
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        fab.setVisibility(View.VISIBLE);
                        break;
                }

                /*frag.setArguments(b);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.group_framelayout, frag);
                transaction.commit();*/

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
                        getApplicationContext(), InsertExActivity.class
                );
                intent.putExtra("groupId", gKey);
                intent.putExtra("groupName", gName);
                intent.putExtra("imagePath",gImage);
                intent.putExtra("defaultcurrency", defaultcurrency);
                startActivityForResult(intent, 1);
            }
        });
        System.out.println("------>GroupActivity arrivata alla fine");
    }
    @Override
    protected void onResume() {
        super.onResume();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final String gkey1 = data.getStringExtra("groupId");
            final String gName1 = data.getStringExtra("groupName");
            final String gImg1 = data.getStringExtra("imagePath");
            Intent refresh = new Intent(this, GroupActivity.class);

            refresh.putExtra("groupId", gkey1);
            refresh.putExtra("groupName", gName1);
            refresh.putExtra("imagePath", gImg1);
            startActivity(refresh);
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        MenuItem remove = (MenuItem) menu.findItem(R.id.removeFromArchive);
        MenuItem add = (MenuItem) menu.findItem(R.id.addToArchive);

        if(gArchive!=null && gArchive.equals("yes")) {
            remove.setVisible(true);
            add.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.options:
                Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
                intent.putExtra("groupId", gKey);
                intent.putExtra("groupName", gName);
                intent.putExtra("imagePath", gImage);
                startActivityForResult(intent, 1);
                return true;

            case R.id.addcurrency:
                Intent i2 = new Intent(getApplicationContext(), InsertCurrencyActivity.class);
                i2.putExtra("groupId", gKey);
                i2.putExtra("groupName", gName);
                i2.putExtra("defaultcurrency", defaultcurrency);
                startActivity(i2);
                return true;

            case R.id.addToArchive:
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gKey).child("archive").setValue("yes");
                Toast.makeText(GroupActivity.this, "Group added to archive", Toast.LENGTH_LONG).show();

                return true;
            case R.id.removeFromArchive:
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gKey).child("archive").setValue("no");
                Toast.makeText(GroupActivity.this, "Group removed from archive", Toast.LENGTH_LONG).show();

                return true;
            /*case R.id.GroupStatistics:
                Intent i3 = new Intent(getApplicationContext(), GroupStatisticsActivity.class);
                i3.putExtra("groupId", gKey);
                i3.putExtra("groupName", gName);
                i3.putExtra("defaultcurrency", defaultcurrency);
                startActivity(i3);
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}