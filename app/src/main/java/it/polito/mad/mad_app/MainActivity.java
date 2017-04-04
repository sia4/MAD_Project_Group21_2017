/*package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.mad_app.model.MainData;

public class MainActivity extends AppCompatActivity {

    class Group {

        private String name;
        private ArrayList<String> expenses;

        public Group(String name) {
            this.name = name;
            expenses = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                expenses.add("expenses" + i);
            }
        }
        public String getName() {
            return name;
        }

    }
    private MainData myData;
    private ListView lv;
    private ArrayList<Group> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("MADAPP");
        getSupportActionBar().setLogo(R.drawable.ic_monetization_on_black_24dp);


        myData = new MainData("malnati", "0000");

        lv = (ListView) findViewById(R.id.lv);

        Group di = new Group("Coinquilini");
        data.add(di);
        di = new Group("Regalo Laurea");
        data.add(di);
        di = new Group("Colleghi");
        data.add(di);

        BaseAdapter a=new BaseAdapter() {

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView==null){
                    convertView=getLayoutInflater().inflate(R.layout.data_item,parent,false);
                }

                TextView name=(TextView)convertView.findViewById(R.id.name_tv);
                Group di=data.get(position);
                name.setText(di.getName());
                return convertView;
            }
        };

        lv.setAdapter(a);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(
                        getApplicationContext(),GroupActivity.class
                );

                String groupName = data.get(position).getName().toString();// ((TextView) view.findViewById(R.id.lv)).getText().toString();
                //Toast toast = Toast.makeText(getApplicationContext(), groupName, Toast.LENGTH_SHORT);
                //toast.show();
                intent.putExtra("name",groupName);

                startActivity(intent);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(
                        getApplicationContext(),InsertGroupActivity.class
                );

                startActivity(intent);

            }
        });
    }

}
*/

package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.util.ThreadUtil;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

public class MainActivity extends AppCompatActivity {


    private static MainData myData = new MainData("malnati", "0000");


    public static MainData getMyData(){ return myData;}
    //private ListView lv;
    private ArrayList<GroupData> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("MADAPP");
        getSupportActionBar().setLogo(R.drawable.ic_monetization_on_black_24dp);
        myData = new MainData("malnati", "0000");
*/
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
//        mainToolbar.setClickable(true);
         //mainToolbar.setTitle("Groups");
        //getSupportActionBar().
        FragmentManager f = getSupportFragmentManager();
        FragmentTransaction transaction = f.beginTransaction();
        transaction.replace(R.id.main_framelayout, new GroupsFragment());
        transaction.commit();
        TabLayout tabL = (TabLayout) findViewById(R.id.tabs);
        tabL.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag;
                switch (tab.getPosition()) {
                    case 0:
                        frag = new GroupsFragment();
                        break;
                    case 1:
                        frag = new GroupsFragment();
                        break;
                    default:
                        frag = new GroupsFragment();
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

        /*
        lv = (ListView) findViewById(R.id.lv);

        Group di = new Group("Coinquilini");
        data.add(di);
        di = new Group("Regalo Laurea");
        data.add(di);
        di = new Group("Colleghi");
        data.add(di);

        BaseAdapter a=new BaseAdapter() {

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView==null){
                    convertView=getLayoutInflater().inflate(R.layout.data_item,parent,false);
                }

                TextView name=(TextView)convertView.findViewById(R.id.name_tv);
                Group di=data.get(position);
                name.setText(di.getName());
                return convertView;
            }
        };

        lv.setAdapter(a);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(
                        getApplicationContext(),GroupActivity.class
                );

                String groupName = data.get(position).getName().toString();// ((TextView) view.findViewById(R.id.lv)).getText().toString();
                //Toast toast = Toast.makeText(getApplicationContext(), groupName, Toast.LENGTH_SHORT);
                //toast.show();
                intent.putExtra("name",groupName);

                startActivity(intent);

            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(
                        getApplicationContext(),InsertGroupActivity.class
                );

                startActivity(intent);
                finish();

            }
        });
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
