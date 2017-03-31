package it.polito.mad.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class InsertExActivity extends AppCompatActivity {
    private String name;
    private String description;
    private String category;
    private String currency;
    private int value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);
        setTitle("Insert expensies");
        Button btn = (Button)findViewById(R.id.EnterButton);
        final EditText Tname = (EditText) findViewById(R.id.Name);
        final EditText Tdescription = (EditText) findViewById(R.id.Description);
        final Spinner Tcategory = (Spinner)findViewById(R.id.Category);
        final Spinner Tcurrency = (Spinner)findViewById(R.id.Currency);
        final EditText Tvalue = (EditText) findViewById(R.id.value);

        btn.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                name = Tname.getText().toString();
                description = Tdescription.getText().toString();
                category = Tcategory.getSelectedItem().toString();
                currency = Tcurrency.getSelectedItem().toString();
                value = Integer.parseInt(Tvalue.getText().toString());

                GroupData.addExpensive(name, description, category, currency, value);

                startActivity(new Intent(InsertExActivity.this, GroupActivity.class));
            }
        });

            /*

            public void onClick(View v) {
                ET.setVisibility(View.INVISIBLE);

                ET.setVisibility(View.VISIBLE);
            } });*/
    }

}
