package it.polito.mad.mad_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import it.polito.mad.mad_app.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class BudgetActivity extends AppCompatActivity {

    class cred_deb {
        private String name;
        private int value;

        public cred_deb(String name,int value) {
            this.name = name;
            this.value=value;
        }
        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
    private ListView lvalues;
    private ArrayList<BudgetActivity.cred_deb> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        Intent intent = getIntent();
        lvalues = (ListView) findViewById(R.id.lv_bud);
        cred_deb n = new cred_deb("Edoardo", 25);
        data.add(n);
        n = new cred_deb("Luca", -48);
        data.add(n);
        n = new cred_deb("Silvia", 27);
        data.add(n);
        n = new cred_deb("Lucia", 81);
        data.add(n);

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
                    convertView=getLayoutInflater().inflate(R.layout.cred_deb_item,parent,false);

                }
                TextView name1=(TextView)convertView.findViewById(R.id.right_item);
                TextView name2=(TextView)convertView.findViewById(R.id.left_item);
                BudgetActivity.cred_deb di=data.get(position);
                name1.setText(di.getName());
                name2.setText(di.getValue());
                return convertView;
            }
        };
        lvalues.setAdapter(a);

    }
}