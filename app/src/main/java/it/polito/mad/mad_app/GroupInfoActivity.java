package it.polito.mad.mad_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

public class GroupInfoActivity extends AppCompatActivity {
    //private GroupData GD;
    private TextView namet, desc;
    private boolean flag_name_edited = false, flag_desc_edited = false, flag_img_edited = false;
    private static String tmp, nametmp, desctmp, gId, gName,  image;
    private EditText nameted, desced;
    private ImageView im;
    private List<String> users = new ArrayList();
    private List<String> usersId = new ArrayList<>();
    private List<String> currencies = new ArrayList();

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
        gId =i.getStringExtra("groupId");
        gName = i.getStringExtra("groupName");
        System.out.println(gId);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");

        im=(ImageView) findViewById(R.id.im_g);

        Button button = (Button) findViewById(R.id.addUserInExistingGroup);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent( getApplicationContext(), InsertUserToGroupActivity.class);
                i.putExtra("groupId", gId);
                i.putExtra("groupName", nametmp);
                i.putExtra("groupPath", image);
                startActivity(i);
                finish();
            }
        });

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
        DatabaseReference myRef = database.getReference("Groups").child(gId);
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
                    image = p;

                    if (p == null) {
                        im.setImageResource(R.drawable.group_default);
                    } else {
                        Glide
                                .with(getApplicationContext())
                                .load(p)
                                .into(im);
                    }

                    //Group g = new Group((String)map.get("gId"),(String) map.get("surname"), (String)map.get("defaultCurrency"));

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




        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.users);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UsersAdapter uAdapter = new UsersAdapter(users);
        userRecyclerView.setAdapter(uAdapter);

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference("Groups").child(gId).child("members");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                if(map2!=null) {
                    for (final String k : map2.keySet()){
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Users").child(k);
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();
                                if(map3!=null) {
                                    String s = map3.get("name")+" "+map3.get("surname");
                                    users.add(s);
                                    usersId.add(k);
                                    uAdapter.notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(GroupInfoActivity.this, "no user key found!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }


                    uAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(GroupInfoActivity.this, "no users found!", Toast.LENGTH_LONG).show();

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
        final CurrenciesAdapter cAdapter = new CurrenciesAdapter(currencies);
        CurrenciesRecyclerView.setAdapter(cAdapter);
        */

        //TODO CLICKLISTENER DEL RECYCLERVIEW PER APRIRE LA USERINFORMATIONACTIVITY
        namet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                namet.setVisibility(View.GONE);
                nameted.setVisibility(View.VISIBLE);
                nameted.setText(nametmp);
                flag_name_edited = true;

            }
        });


        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                desc.setVisibility(View.GONE);
                desced.setVisibility(View.VISIBLE);
                desced.setText(desctmp);
                flag_desc_edited = true;

            }
        });

        userRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, userRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent intent = new Intent().setClass(view.getContext(), UserInformationActivity.class);
                intent.putExtra("userId", usersId.get(position));
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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

        switch(item.getItemId()){
            case android.R.id.home:

                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupId", gId);
                intent.putExtra("groupName", gName);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();

                return true;

            case R.id.action_menu_done:

                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);

                if (flag_name_edited) {

                    final EditText Ename = (EditText) findViewById(R.id.name_g_ed);
                    String newname = Ename.getText().toString();

                    if (newname != null && !newname.equals("") && !newname.equals(gId)) {
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Groups").child(gId).child("gId");
                        myRef3.setValue(newname);
                        //GD.setName(newname);
                        //MainData.getInstance().changeGroupName(GroupName, newname);
                        in.putExtra("groupId", gId);
                        in.putExtra("groupName", gName);
                    } else {
                        in.putExtra("groupId", gId);
                        in.putExtra("groupName", gName);
                    }

                    Log.e("DEBUG", newname + " " + gId);
                    System.out.println();
                } else {
                    in.putExtra("groupId", gId);
                    in.putExtra("groupName", gName);
                }

                if (flag_desc_edited) {

                    final EditText desc = (EditText) findViewById(R.id.de_g_ed);
                    String newdesc = desc.getText().toString();

                    if (!newdesc.equals("")) {
                        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef4 = database4.getReference("Groups").child(gId).child("description");
                        myRef4.setValue(newdesc);
                        //GD.setDescription(newdesc);
                    }

                }

                if (flag_img_edited) {
                    FirebaseDatabase database5 = FirebaseDatabase.getInstance();
                    DatabaseReference myRef5 = database5.getReference("Groups").child(gId).child("imagePath");
                    myRef5.setValue(tmp);
                    //GD.setImagePath(tmp);
                }

                setResult(RESULT_OK, in);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
