package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/*
public class BudgetActivity extends AppCompatActivity {

    class cred_deb {
        private String name;
        private String value;

        public cred_deb(String name,String value) {
            this.name = name;
            this.value=value;
        }
        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
    private ListView lvalues;
    private ArrayList<BudgetActivity.cred_deb> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_budget);
        //Intent intent = getIntent();
        lvalues = (ListView) findViewById(R.id.lv_bud);
        cred_deb n = new cred_deb("Edoardo", "25€");
        users.add(n);
        n = new cred_deb("Luca", "-48€");
        users.add(n);
        n = new cred_deb("Silvia", "27€");
        users.add(n);
        n = new cred_deb("Lucia", "81€");
        users.add(n);

        BaseAdapter a=new BaseAdapter() {
            @Override
            public int getCount() {
                return users.size();
            }

            @Override
            public Object getItem(int position) {
                return users.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                    convertView=getLayoutInflater().inflate(R.layout.cred_deb_item,parent,false);

                }
                TextView name1=(TextView)convertView.findViewById(R.id.right_item);
                TextView name2=(TextView)convertView.findViewById(R.id.left_item);
                BudgetActivity.cred_deb di=users.get(position);
                name2.setText(di.getName());
                name1.setText(di.getValue());
                return convertView;
            }
        };
        lvalues.setAdapter(a);

    }
}*/