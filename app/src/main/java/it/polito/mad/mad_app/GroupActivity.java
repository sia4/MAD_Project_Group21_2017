package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class GroupActivity extends AppCompatActivity {
    private String gName, gKey;
    private ListView lv;

    @Override
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

        final Bundle b = new Bundle();
        b.putString("GroupId", gKey);
        b.putString("GroupName", gName);

        //System.out.println("CICCIOBOMBA" + datigruppo.getName());

        Float pos = (float)10.2;//datigruppo.getPosExpenses();
        Float neg = (float)10.2;//datigruppo.getNegExpenses();

        String subtitle = "";
        subtitle += "They Owe You: " + pos.toString()+ " - You Owe: " + neg.toString();


        getSupportActionBar().setTitle(gName);
        getSupportActionBar().setSubtitle(subtitle);
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

        Fragment hfrag = new HistoryFragment();
        hfrag.setArguments(b);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        transaction.replace(R.id.group_framelayout, hfrag);
        transaction.commit();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsGroup);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag;
                switch (tab.getPosition()) {
                    case 0:
                        frag = new HistoryFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        frag = new BudgetFragment();
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        frag = new HistoryFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                }

                frag.setArguments(b);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.group_framelayout, frag);
                transaction.commit();

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final String gkey1 = data.getStringExtra("groupId");
            final String gName1 = data.getStringExtra("groupName");
            Intent refresh = new Intent(this, GroupActivity.class);

            refresh.putExtra("groupId", gkey1);
            refresh.putExtra("groupName", gName1);
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