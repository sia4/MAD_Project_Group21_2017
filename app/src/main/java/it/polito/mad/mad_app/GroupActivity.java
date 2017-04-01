package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.ExpensiveData;

public class GroupActivity extends AppCompatActivity {

    private ListView lv;
    private List<ExpensiveData> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();

        //Get information from previuos Activity
        String name = intent.getStringExtra("name");
        setTitle(name);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.group_framelayout, new HistoryFragment());
        transaction.commit();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsGroup);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag;
                switch (tab.getPosition()){
                    case 0:
                        frag = new HistoryFragment();
                        break;
                    case 1:
                        frag = new BudgetFragment();
                        break;
                    default:
                        frag = new HistoryFragment();
                        break;
                }
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(
                        getApplicationContext(),InsertExActivity.class
                );

                startActivity(intent);

            }
        });
        /*Button btn = (Button)findViewById(R.id.button3);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupActivity.this, BudgetActivity.class));
            }
        });*/

    }
    protected void onResume() {
        super.onResume();


        /*
        una volta tornati in questa attività dopo l'aggiunta di una spesa
        bisogna refreshare in qualche modo l'adapter
        vedi:
            - notifyDataChanged()
         */
    }
}
