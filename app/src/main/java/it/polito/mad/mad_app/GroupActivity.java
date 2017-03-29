package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    class Ex {
        private String name;
        private String money;

        public Ex(String name,String money) {
            this.name = name;
            this.money=money;
        }
        public String getName() {
            return name;
        }

        public String getPrice() {
            return money;
        }
    }
    private ListView lv;
    private ArrayList<GroupActivity.Ex> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();
        lv = (ListView) findViewById(R.id.lv_ex);
        Ex di = new Ex("Pane","10$");
        data.add(di);
        di = new Ex("Carta Igenica","6$");
        data.add(di);
        di = new Ex("Dolci","3$");
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
                    convertView=getLayoutInflater().inflate(R.layout.ex_item,parent,false);

                }
                TextView name=(TextView)convertView.findViewById(R.id.name_ex);
                TextView money=(TextView)convertView.findViewById(R.id.money_ex);
                GroupActivity.Ex di=data.get(position);
                name.setText(di.getName());
                money.setText(di.getPrice());
                return convertView;
            }
        };

        lv.setAdapter(a);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(
                        getApplicationContext(),InsertExActivity.class
                );
                //intent.putExtra("ID1","ciao");
                startActivity(intent);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });
        Button btn = (Button)findViewById(R.id.button3);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupActivity.this, BudgetActivity.class));
            }
        });

    }
}
