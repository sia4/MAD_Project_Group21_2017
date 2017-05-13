package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.polito.mad.mad_app.model.PagerAdapter;
import it.polito.mad.mad_app.model.PagerAdapterGroup;


public class GroupActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private String gName, gKey,gImage;
    private ListView lv;
    private float tmppos=0;
    private float tmpneg=0;
    Float pos = (float)10.2;//datigruppo.getPosExpenses();
    Float neg = (float)10.2;//datigruppo.getNegExpenses();
    private Map<String, Map<String, Object>> balancemap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        gKey = intent.getStringExtra("groupId");
        gName = intent.getStringExtra("groupName");
        gImage= intent.getStringExtra("imagePath");
        System.out.println("---->intent"+intent);
        System.out.println("---->intent boooooooo"+intent.getExtras());

        final Bundle b = new Bundle();
        b.putString("GroupId", gKey);
        b.putString("GroupName", gName);
        b.putString("imagePath", gImage);

        //System.out.println("CICCIOBOMBA" + datigruppo.getName());





        final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
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
                     subtitle +=" "+ "They Owe You: " + String.valueOf(tmppos)+ " - You Owe: " + String.valueOf(tmpneg);



                     getSupportActionBar().setSubtitle(subtitle);

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
         String subtitle = "";
         subtitle +=" "+ "They Owe You: " + String.valueOf(tmppos)+ " - You Owe: " + String.valueOf(tmpneg);



         getSupportActionBar().setSubtitle(subtitle);

        //Drawable dr = getResources().getDrawable(R.drawable.group_default);
        Glide
                .with(getApplicationContext())
                .load(gImage)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(60,60) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        //image.setImageBitmap(resource); // Possibly runOnUiThread()
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        //Drawable d = new BitmapDrawable(getResources(), circularBitmapDrawable);
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
        //toolbar.setLogo(ContextCompat.getDrawable(getApplicationContext(), R.drawable.group_default));
        //toolbar.setNavigationIcon(d);
        //getSupportActionBar().setIcon(R.drawable.group_default);
        //getSupportActionBar().setLogo(R.);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
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
                startActivityForResult(intent, 1);
            }
        });
        System.out.println("------>GroupActivity arrivata alla fine");
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
                startActivity(i2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}