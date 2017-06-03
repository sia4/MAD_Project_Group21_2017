package it.polito.mad.mad_app;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.PolData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;
import it.polito.mad.mad_app.model.User;

import static it.polito.mad.mad_app.model.ImageMethod.circle_image;
import static it.polito.mad.mad_app.model.ImageMethod.create_image;
import static it.polito.mad.mad_app.model.ImageMethod.performCrop;
import static it.polito.mad.mad_app.model.ImageMethod.require_image;

public class GroupInfoActivity extends AppCompatActivity {
    //private GroupData GD;
    private TextView namet, desc;
    private boolean flag_name_edited = false, flag_desc_edited = false, ImageC= false;
    private String nametmp, desctmp, gId, gName,  image, myname, mysurname, deletePolId, leavePolId, PolKey;
    private EditText nameted, desced;
    private CheckBox favourite;
    private ImageView im;
    private Uri outputFileUri;
    private Uri imageUrl;
    private List<User> user_l=new ArrayList<>();
    private List<String> users = new ArrayList();
    private List<String> usersId = new ArrayList<>();
    private List<String> currencies = new ArrayList();
    private Currencies c = new Currencies();
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private Uri downloadUrl;
    private String gImage;

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
        gImage = i.getStringExtra("imagePath");
        System.out.println("---->intent info"+i.getExtras());
        System.out.println(gId);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");

        im=(ImageView) findViewById(R.id.im_g);
        Button changePhoto= (Button) findViewById(R.id.m_image_g);
        changePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&  checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED ) {
                    if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    }

                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    Log.d("Group Info Activity","ho richiesto i permessi");
                    return;
                }

                Log.d("Group Info Activity","ho già i permessi");
                LoadImage();
            }
        });
        favourite = (CheckBox) findViewById(R.id.favourite);

        favourite.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                DatabaseReference favRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gId).child("favourite");

                if(favourite.isChecked()){
                    favRef.setValue("yes");
                }
                else{
                    favRef.setValue("no");
                }


            }
        });



        Button button = (Button) findViewById(R.id.addUserInExistingGroup);
        final Button buttonDelete = (Button) findViewById(R.id.DeleteGroup);
        final Button buttonLeave = (Button) findViewById(R.id.LeaveGroup);
        final Button buttonPoldel = (Button) findViewById(R.id.DeletePol);
        final Button buttonPolleave = (Button) findViewById(R.id.LeavePol);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(GroupInfoActivity.this, "A notification has been sent to all the group's member", Toast.LENGTH_LONG).show();
                DatabaseReference getMyName = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getMyName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        System.out.println("mapnameeeeeeeeeeeeeeeeeeee"+mapname);
                        if(mapname!=null) {
                            buttonDelete.setVisibility(View.GONE);
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference ActRef = database.getReference("Activities");
                            DatabaseReference ActRead = database.getReference("ActivitiesRead");
                            DatabaseReference PolRef = database.getReference("Pols").child(gId).push();
                            PolKey = PolRef.getKey();
                            deletePolId = PolKey;
                            DatabaseReference PolGroup = database.getReference("Groups").child(gId).child("deletePolId");
                            PolGroup.setValue(PolKey);
                            PolData p = new PolData(String.format("%d", users.size()), "1");
                            PolRef.setValue(p);
                            PolRef.child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            PolRef.child("acceptsUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myname+" "+mysurname);


                            for(String k: usersId) {
                                if(!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    String actId = ActRef.child(k).push().getKey();
                                    ActRef.child(k).child(actId).setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " proposed to delete group " + gName, Long.toString(System.currentTimeMillis()), "deletegroup", PolKey, gId));
                                    ActRead.child(k).child(gId).child(actId).setValue(false);
                                }
                            }
                            if(users.size()==1){
                                findViewById(R.id.DeleteGroup).setVisibility(View.GONE);
                                findViewById(R.id.LeaveGroup).setVisibility(View.GONE);
                                findViewById(R.id.okdeleted).setVisibility(View.VISIBLE);
                                database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gId).child("missing").setValue("yes");

                            }

                            //database.getReference("Groups").child(gId).removeValue();

                            buttonPoldel.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        buttonLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(GroupInfoActivity.this, "A notification has been sent to all the group's member", Toast.LENGTH_LONG).show();
                DatabaseReference getMyName = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getMyName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        System.out.println("mapnameeeeeeeeeeeeeeeeeeee"+mapname);
                        if(mapname!=null) {
                            buttonLeave.setVisibility(View.GONE);
                            buttonPolleave.setVisibility(View.VISIBLE);
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference PolRef = database.getReference("Pols").child(gId).push();
                            DatabaseReference ActRead = database.getReference("ActivitiesRead");
                            PolKey = PolRef.getKey();
                            DatabaseReference PolGroup = database.getReference("Groups").child(gId).child("leavePolId");
                            PolGroup.setValue(PolKey);

                            PolData p = new PolData(String.format("%d", users.size()), "1");
                            PolRef.setValue(p);
                            PolRef.child("acceptsUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myname+" "+mysurname);
                            PolRef.child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            DatabaseReference ActRef = database.getReference("Activities");
                            for(String k: usersId) {
                                if (!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    String actId = ActRef.child(k).push().getKey();
                                    ActRef.child(k).child(actId).setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " proposed to leave group " + gName, Long.toString(System.currentTimeMillis()), "leavegroup", PolKey, gId));
                                    ActRead.child(k).child(gId).child(actId).setValue(false);
                                }
                            }
                            if(users.size()==1){
                                findViewById(R.id.DeleteGroup).setVisibility(View.GONE);
                                findViewById(R.id.LeaveGroup).setVisibility(View.GONE);
                                findViewById(R.id.okdeleted).setVisibility(View.VISIBLE);
                                database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gId).child("missing").setValue("yes");

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        buttonPoldel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), PolActivity.class);
                intent.putExtra("polId", deletePolId);
                intent.putExtra("groupId", gId);
                intent.putExtra("groupName", gName);
                intent.putExtra("text", "Delete Group");
                intent.putExtra("type", "deletegroup");
                startActivity(intent);
            }

        });

        buttonPolleave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), PolActivity.class);
                intent.putExtra("polId", leavePolId);
                intent.putExtra("groupId", gId);
                intent.putExtra("groupName", gName);
                intent.putExtra("text", "Leave Group");
                intent.putExtra("type", "leavegroup");
                startActivity(intent);
            }

        });


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent( getApplicationContext(), InsertUserToGroupActivity.class);
                i.putExtra("groupId", gId);
                i.putExtra("groupName", nametmp);
                i.putExtra("groupPath", image);
                startActivity(i);
            }
        });

        /*im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }

        });*/

        /*RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.users);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UsersAdapter uAdapter = new UsersAdapter(users);
        userRecyclerView.setAdapter(uAdapter);*/

        final RecyclerView CurrenciesRecyclerView = (RecyclerView) findViewById(R.id.currencies);
        CurrenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.users);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UserAdapterIm uAdapter = new UserAdapterIm(user_l);
        userRecyclerView.setAdapter(uAdapter);


        namet=(TextView) findViewById(R.id.name_g);
        nameted = (EditText) findViewById(R.id.name_g_ed);
        desc = (TextView) findViewById(R.id.de_g);
        desced = (EditText) findViewById(R.id.de_g_ed);

        DatabaseReference myRef = database.getReference("Groups").child(gId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                    nametmp = (String) map.get("name");
                    desctmp = (String) map.get("description");
                    deletePolId = (String) map.get("deletePolId");
                    leavePolId = (String) map.get("leavePolId");
                    System.out.println("pol delete id: "+deletePolId);
                    System.out.println("pol leave id: "+leavePolId);
                    if(deletePolId!=null){
                        buttonDelete.setVisibility(View.GONE);
                        buttonPoldel.setVisibility(View.VISIBLE);
                    }
                    if(leavePolId!=null){
                        buttonLeave.setVisibility(View.GONE);
                        buttonPolleave.setVisibility(View.VISIBLE);
                    }
                    namet.setText(nametmp);
                    desc.setText(desctmp);
                    if(map.get("imagePath").equals("")){
                        circle_image(getApplicationContext(),im,R.drawable.group_icon_default);

                    }
                    else{
                        final String p = (String) map.get("imagePath");
                        image = p;

                        if (p == null) {

                        } else {
                            circle_image(getApplicationContext(),im,p);
                            im.setOnClickListener(new View.OnClickListener(){

                                @Override
                                public void onClick(View v){

                                    Log.d("Group Info Activity", p);

                                    Glide.with(getApplicationContext())
                                            .load(p).asBitmap().into(new SimpleTarget<Bitmap>(200,200) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            Intent intent = new Intent(GroupInfoActivity.this, FullImageActivity.class);
                                            intent.putExtra("BitmapImage", resource);
                                            startActivity(intent);
                                            //image.setImageBitmap(resource); // Possibly runOnUiThread()
                                        }
                                    });
                                    /*
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                Bitmap bitmapImageGroup = Glide.with(getApplicationContext())
                                                        .load(p).asBitmap().into(-1, -1).
                                                                get();
                                                showImage(bitmapImageGroup);
                                            } catch (final ExecutionException e) {
                                                Log.e("Group Info Activity", e.getMessage());
                                            } catch (final InterruptedException e) {
                                                Log.e("Group Info Activity", e.getMessage());
                                            }
                                            return null;
                                        }
                                    };*/
                                }
                            });
                        }
                    }
                }
                if (g != null) {

                    if (g.getCurrencies().size() != 0) {

                        System.out.println(g.getCurrencies().toString());

                        for (Entry<String, Float> e : g.getCurrencies().entrySet()) {
                            if (e.getValue() == 0.0) {

                                String s = "Primary Currency: " + c.getCurrencyString(e.getKey());
                                currencies.add(s);
                            } else {

                                String s = c.getCurrencyString(e.getKey());
                                currencies.add(s + " " + e.getValue());
                            }

                        }
                        CurrenciesAdapter cAdapter = new CurrenciesAdapter(currencies);
                        CurrenciesRecyclerView.setAdapter(cAdapter);
                        cAdapter.notifyDataSetChanged();

                    } else {

                        Toast.makeText(GroupInfoActivity.this, "No Currencies found!", Toast.LENGTH_LONG).show();

                    }

                    if (g.showMemberList().size() != 0) {

                        for (final String k : g.showMemberList()) {


                            FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef3 = database3.getReference("Users").child(k);
                            myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();

                                    if (map3 != null) {

                                        String s = map3.get("name") + " " + map3.get("surname");
                                        String fav = ((Map<String, String>) ((Map<String, Object>) map3.get("Groups")).get(gId)).get("favourite");
                                        if (fav != null && fav.equals("yes") && k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            favourite.setChecked(true);
                                        }
                                        String p = null;

                                        if (map3.get("imagePath") == null) {
                                            p = null;
                                        } else {
                                            p = map3.get("imagePath").toString();
                                        }

                                        users.add(s);
                                        user_l.add(new User(null, null, map3.get("name").toString(), map3.get("surname").toString(), p));
                                        usersId.add(k);
                                        uAdapter.notifyDataSetChanged();

                                    } else {

                                        Toast.makeText(GroupInfoActivity.this, "no user key found!", Toast.LENGTH_LONG).show();

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    //log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });



                /*Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if(map!=null) {

                    System.out.println(map.toString());

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
                }*/


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
        final int CROP_PIC = 2;
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = null;
            if (requestCode == 1) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (isCamera) {imageUrl = outputFileUri;
                    ImageC = true;
                    final PackageManager pManager = getPackageManager();
                    Intent cropIntent=performCrop(imageUrl,pManager);
                    if(cropIntent!=null){
                        final Intent cIntent = Intent.createChooser(cropIntent, "Tha image should be cropped,select a source");
                        startActivityForResult(cIntent , 2);
                    }
                    else{
                        Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                } else {
                    if (data != null) {
                        ImageC = true;
                        selectedImageUri = data.getData();
                        imageUrl= selectedImageUri;
                        final PackageManager pManager = getPackageManager();
                        Intent cropIntent=performCrop(imageUrl,pManager);
                        if(cropIntent!=null){
                            final Intent cIntent = Intent.createChooser(cropIntent, "Tha image should be cropped,select a source");
                            startActivityForResult(cIntent , 2);
                        }
                        else{
                            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                }
            }else if(requestCode==CROP_PIC){
                Bundle extras = data.getExtras();
                if(extras==null){
                    selectedImageUri = data.getData();
                    Bitmap photo = null;
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("................." + photo);
                    ImageView imageG = (ImageView) findViewById(R.id.im_g);
                    imageG.setImageBitmap(photo);

                }else{
                    Bitmap thePic = extras.getParcelable("data");
                    final ImageView picView = (ImageView) findViewById(R.id.im_g);
                    //picView.setImageBitmap(thePic);
                    create_image(outputFileUri,thePic);
                    circle_image(getApplicationContext(),picView,outputFileUri);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

       // cAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d("Group Info Activity","dentro on request permission result");
        switch (requestCode) {
            case 1: {
                Log.d("Group Info Activity","case 1");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                        LoadImage();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupId", gId);
                intent.putExtra("groupName", gName);
                intent.putExtra("imagePath", gImage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();

                return true;

            case R.id.action_menu_done:

                if (flag_name_edited) {

                    final EditText Ename = (EditText) findViewById(R.id.name_g_ed);
                    String newname = Ename.getText().toString();

                    if (newname != null && !newname.equals("") && !newname.equals(gId)) {
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Groups").child(gId).child("name");
                        myRef3.setValue(newname);
                        for (String u : usersId) {
                            myRef3 = database3.getReference("Users").child(u).child("Groups").child(gId).child("name");
                            myRef3.setValue(newname);
                        }

                        //GD.setName(newname);
                        //MainData.getInstance().changeGroupName(GroupName, newname);
                        gName = newname;
                    }
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

                if (ImageC) {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_group_info);
                    progressBar.setVisibility(View.VISIBLE);
                    LinearLayout ll = (LinearLayout) findViewById(R.id.llgourpinfo);
                    ll.setVisibility(View.INVISIBLE);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    try {
                        System.out.println(".......carica in" + outputFileUri.toString().substring(7));
                        InputStream stream = new FileInputStream(new File(outputFileUri.toString().substring(7)));
                        StorageReference imageStorage = mStorageRef.child(gId);
                        UploadTask uploadTask = imageStorage.putStream(stream);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("myStorage", "failure :(");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUrl = taskSnapshot.getDownloadUrl();
                                DatabaseReference myRef = database.getReference("Groups/" + gId + "/imagePath");
                                myRef.setValue(downloadUrl.toString());
                                System.out.println("------------>GroupPAth" + "Groups/" + gId + "/imagePath" + downloadUrl);
                                System.out.println("------------>UserPAth" + "Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + gId + "/imagePath" + downloadUrl);
                                for (String u : usersId) {
                                    DatabaseReference myRef2 = database.getReference("Users/" + u + "/Groups/" + gId + "/imagePath");
                                    myRef2.setValue(downloadUrl.toString());
                                }
                                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);
                                in.putExtra("groupId", gId);
                                in.putExtra("groupName", gName);
                                in.putExtra("imagePath", downloadUrl.toString());
                                setResult(RESULT_OK, null);
                                finish();
                                Log.d("myStorage", "success!");
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);
                in.putExtra("imagePath", gImage);
                in.putExtra("groupId", gId);
                in.putExtra("groupName", gName);
                System.out.println("Devo capire" + in.getExtras());
                setResult(RESULT_OK, in);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void LoadImage(){
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "YourSlice" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        final PackageManager pManager = getPackageManager();
        List<Intent> cameraIntents=require_image(outputFileUri,pManager);
        final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size()-1), "Select Source");
        cameraIntents.remove(cameraIntents.size()-1);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, 1);
    }

    public void showImage(Bitmap b) {

        Intent intent = new Intent(this, FullImageActivity.class);
        intent.putExtra("BitmapImage", b);
        startActivity(intent);

        /*
        final Dialog nagDialog = new Dialog(GroupInfoActivity.this,android.R.style.Theme_Material_Light_LightStatusBar);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);
        nagDialog.setContentView(R.layout.prewiew_image);
        //Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
        ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
        ivPreview.setBackgroundResource(R.drawable.group_default);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                nagDialog.dismiss();
            }
        });
        nagDialog.show();*/
    }
}
