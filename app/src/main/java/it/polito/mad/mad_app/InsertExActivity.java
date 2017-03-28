package it.polito.mad.mad_app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.polito.mad.mad_app.R;

public class InsertExActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);
        final EditText ET = (EditText) findViewById(R.id.EnterText);
        Button B1 = (Button) findViewById(R.id.EnterButton);
        B1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ET.setVisibility(View.INVISIBLE);

                ET.setVisibility(View.VISIBLE);
            } });
    }

}
