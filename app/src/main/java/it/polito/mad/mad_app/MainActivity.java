package it.polito.mad.mad_app;

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
