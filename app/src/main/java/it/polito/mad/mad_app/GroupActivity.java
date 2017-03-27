package it.polito.mad.mad_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    class Ex {
        private String name;
        private int money;

        public Ex(String name,int money) {
            this.name = name;
            this.money=money;
        }
        public String getName() {
            return name;
        }

        public int getPrice() {
            return money;
        }
    }
    private ListView lv;
    private ArrayList<GroupActivity.Ex> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        lv = (ListView) findViewById(R.id.lv_ex);


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
                GroupActivity.Ex di=data.get(position);
                name.setText(di.getName());
                return convertView;
            }
        };

        lv.setAdapter(a);

    }
}
