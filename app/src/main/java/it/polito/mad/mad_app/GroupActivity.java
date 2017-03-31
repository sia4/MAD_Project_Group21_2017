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
import java.util.List;

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


        lv = (ListView) findViewById(R.id.lv_ex);
        data = GroupData.getExpensies();


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
                TextView category=(TextView)convertView.findViewById(R.id.category_ex);
                TextView currency=(TextView)convertView.findViewById(R.id.currency_ex);
                TextView description=(TextView)convertView.findViewById(R.id.description_ex);

                ExpensiveData di=data.get(position);
                name.setText(di.getName());
                money.setText(di.getValue());
                category.setText(di.getCategory());
                currency.setText(di.getCurrency());
                description.setText(di.getDescription());
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

                startActivity(intent);

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
    protected void onResume(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        una volta tornati in questa attività dopo l'aggiunta di una spesa
        bisogna refreshare in qualche modo l'adapter
        vedi:
            - notifyDataChanged()
         */
    }
}
