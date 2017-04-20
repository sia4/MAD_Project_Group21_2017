package it.polito.mad.mad_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

public class GroupInfoActivity extends AppCompatActivity {
    private GroupData GD;
    private TextView namet, desc;
    private boolean flag_name_edited = false, flag_desc_edited = false, flag_img_edited = false;
    private String tmp;
    private EditText nameted, desced;
    private ImageView im;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        String name=i.getStringExtra("name");
        GD=MainData.getInstance().getGroup(name);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");

        im=(ImageView) findViewById(R.id.im_g);

        String p = GD.getImagePath();

        if (p == null) {
            im.setImageResource(R.drawable.group_default);
        } else {
            im.setImageBitmap(BitmapFactory.decodeFile(p));
        }

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }

        });

        namet=(TextView) findViewById(R.id.name_g);
        nameted = (EditText) findViewById(R.id.name_g_ed);

        namet.setText(name);

        namet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                namet.setVisibility(View.GONE);
                nameted.setVisibility(View.VISIBLE);
                nameted.setText(GD.getName());
                flag_name_edited = true;

            }
        });

        desc = (TextView) findViewById(R.id.de_g);
        desced = (EditText) findViewById(R.id.de_g_ed);

        desc.setText(GD.getDescription());

        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                desc.setVisibility(View.GONE);
                desced.setVisibility(View.VISIBLE);
                desced.setText(GD.getName());
                flag_desc_edited = true;

            }
        });

        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.users);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UsersAdapter uAdapter = new UsersAdapter(GD.getlUsers());
        userRecyclerView.setAdapter(uAdapter);

        RecyclerView CurrenciesRecyclerView = (RecyclerView) findViewById(R.id.currencies);
        CurrenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final CurrenciesAdapter cAdapter = new CurrenciesAdapter(GD.getCurrencies());
        CurrenciesRecyclerView.setAdapter(cAdapter);
    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            c.moveToFirst();
            int cIndex = c.getColumnIndex(filePathColumn[0]);
            String picturePath = c.getString(cIndex);
            c.close();
            ImageView imageView = (ImageView) findViewById(R.id.im_g);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            flag_img_edited = true;
            tmp = picturePath;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent i = getIntent();
        String GroupName = GD.getName();
        System.out.println("GNAME: " + GroupName);

        switch(item.getItemId()){
            case android.R.id.home:

                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("name", GroupName);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();

                return true;

            case R.id.action_menu_done:

                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);

                if (flag_name_edited) {

                    final EditText name = (EditText) findViewById(R.id.name_g_ed);
                    String newname = name.getText().toString();

                    if (newname != null && !newname.equals("") && !newname.equals(GroupName)) {
                        GD.setName(newname);
                        MainData.getInstance().changeGroupName(GroupName, newname);
                        in.putExtra("name", newname);
                    } else {
                        in.putExtra("name", GroupName);
                    }

                    Log.e("DEBUG", newname + " " + GroupName);
                    System.out.println();
                } else {
                    in.putExtra("name", GroupName);
                }

                if (flag_desc_edited) {

                    final EditText desc = (EditText) findViewById(R.id.de_g_ed);
                    String newdesc = desc.getText().toString();

                    if (!newdesc.equals("")) {
                        GD.setDescription(newdesc);
                    }

                }

                if (flag_img_edited) {
                    GD.setImagePath(tmp);
                }

                setResult(RESULT_OK, in);
                System.out.println("WAT");
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
