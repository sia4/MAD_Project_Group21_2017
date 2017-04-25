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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;

import static it.polito.mad.mad_app.R.id.progressBar;

public class GroupInfoActivity extends AppCompatActivity {
    //private GroupData GD;
    private TextView namet, desc;
    private boolean flag_name_edited = false, flag_desc_edited = false, flag_img_edited = false;
    private static String tmp, nametmp, desctmp, name;
    private EditText nameted, desced;
    private ImageView im;
    private List<String> users;
    private List<String> currencies;

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
        name=i.getStringExtra("name");
        System.out.println(name);
        //GD=MainData.getInstance().getGroup(name);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");

        im=(ImageView) findViewById(R.id.im_g);

        //String p = GD.getImagePath();


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
        desc = (TextView) findViewById(R.id.de_g);
        desced = (EditText) findViewById(R.id.de_g_ed);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups").child(name);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map!=null) {
                    nametmp = (String) map.get("name");
                    desctmp = (String) map.get("description");
                    namet.setText(nametmp);
                    desc.setText(desctmp);

                    String p = (String) map.get("imagePath");

                    if (p == null) {
                        im.setImageResource(R.drawable.group_default);
                    } else {
                        im.setImageBitmap(BitmapFactory.decodeFile(p));
                    }

                    //Group g = new Group((String)map.get("name"),(String) map.get("surname"), (String)map.get("defaultCurrency"));

                    //if(progressBar.isActivated())
                    //progressBar.setVisibility(View.INVISIBLE);
                    //gAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        /*


        //namet.setText(name);

        namet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                namet.setVisibility(View.GONE);
                nameted.setVisibility(View.VISIBLE);
                nameted.setText(nametmp);
                flag_name_edited = true;

            }
        });




        //desc.setText(GD.getDescription());

        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                desc.setVisibility(View.GONE);
                desced.setVisibility(View.VISIBLE);
                desced.setText(desctmp);
                flag_desc_edited = true;

            }
        });

        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.users);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UsersAdapter uAdapter = new UsersAdapter(users);
        userRecyclerView.setAdapter(uAdapter);

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference("Groups").child(name).child("members");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                if(map2!=null) {
                    for (String k : map2.keySet())
                        users.add((String) map2.get(k));

                    uAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        //TODO ADAPTER CURRENCIES QUANDO SARANNO GESTITE
        /*
        RecyclerView CurrenciesRecyclerView = (RecyclerView) findViewById(R.id.currencies);
        CurrenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final CurrenciesAdapter cAdapter = new CurrenciesAdapter(GD.getCurrencies());
        CurrenciesRecyclerView.setAdapter(cAdapter);
        */
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
            //Toast.makeText(this, "path:"+ tmp, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent i = getIntent();
        System.out.println("GNAME: " + nametmp);

        switch(item.getItemId()){
            case android.R.id.home:

                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("name", name);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();

                return true;

            case R.id.action_menu_done:

                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);

                if (flag_name_edited) {

                    final EditText Ename = (EditText) findViewById(R.id.name_g_ed);
                    String newname = Ename.getText().toString();

                    if (newname != null && !newname.equals("") && !newname.equals(name)) {
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Groups").child(name).child("name");
                        myRef3.setValue(newname);
                        //GD.setName(newname);
                        //MainData.getInstance().changeGroupName(GroupName, newname);
                        in.putExtra("name", name);
                    } else {
                        in.putExtra("name", name);
                    }

                    Log.e("DEBUG", newname + " " + name);
                    System.out.println();
                } else {
                    in.putExtra("name", name);
                }

                if (flag_desc_edited) {

                    final EditText desc = (EditText) findViewById(R.id.de_g_ed);
                    String newdesc = desc.getText().toString();

                    if (!newdesc.equals("")) {
                        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef4 = database4.getReference("Groups").child(name).child("description");
                        myRef4.setValue(newdesc);
                        //GD.setDescription(newdesc);
                    }

                }

                if (flag_img_edited) {
                    FirebaseDatabase database5 = FirebaseDatabase.getInstance();
                    DatabaseReference myRef5 = database5.getReference("Groups").child(name).child("imagePath");
                    myRef5.setValue(tmp);
                    //GD.setImagePath(tmp);
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
