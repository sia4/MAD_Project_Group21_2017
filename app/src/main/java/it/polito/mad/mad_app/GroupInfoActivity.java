package it.polito.mad.mad_app;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.PolData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;
import it.polito.mad.mad_app.model.User;

public class GroupInfoActivity extends AppCompatActivity {
    //private GroupData GD;
    private TextView namet, desc;
    private boolean flag_name_edited = false, flag_desc_edited = false, ImageC= false;
    private String nametmp, desctmp, gId, gName,  image, myname, mysurname;
    private EditText nameted, desced;
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

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");

        im=(ImageView) findViewById(R.id.im_g);
        Button changePhoto= (Button) findViewById(R.id.m_image_g);
        changePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
                root.mkdirs();
                final String fname = "img_" + System.currentTimeMillis() + ".jpg";
                final File sdImageMainDirectory = new File(root, fname);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
                System.out.println("------>outputFileUri" + outputFileUri);
                final List<Intent> cameraIntents = new ArrayList<Intent>();
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                final PackageManager pManager = getPackageManager();
                final List<ResolveInfo> Im = pManager.queryIntentActivities(pickIntent, 0);
                for (ResolveInfo res : Im) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(pickIntent);
                    intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    System.out.println(".........image intent " + intent);
                    cameraIntents.add(intent);
                }
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = getPackageManager();
                final List<ResolveInfo> Cam = packageManager.queryIntentActivities(captureIntent, 0);
                for (ResolveInfo res : Cam) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    System.out.println(".........camera intent " + intent);
                    cameraIntents.add(intent);
                }
                final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size() - 1), "Select Source");
                cameraIntents.remove(cameraIntents.size() - 1);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, 1);
            }
        });

        Button button = (Button) findViewById(R.id.addUserInExistingGroup);
        Button buttonDelete = (Button) findViewById(R.id.DeleteGroup);
        Button buttonLeave = (Button) findViewById(R.id.LeaveGroup);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(GroupInfoActivity.this, "A notification has been sent to all the group's member", Toast.LENGTH_LONG).show();
                DatabaseReference getMyName = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getMyName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        System.out.println("mapnameeeeeeeeeeeeeeeeeeee"+mapname);
                        if(mapname!=null) {
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference ActRef = database.getReference("Activities").child(gId).push();
                            DatabaseReference PolRef = database.getReference("Pols").child(gId).push();
                            String PolKey = PolRef.getKey();
                            PolData p = new PolData(String.format("%d", users.size()), "1");
                            PolRef.setValue(p);
                            PolRef.child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            PolRef.child("acceptsUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myname+" "+mysurname);

                            ActRef.setValue(new ActivityData(myname+" "+mysurname, myname+" "+mysurname +" proposed to delete group " + gName, Long.toString(System.currentTimeMillis()), "deletegroup", PolKey, gId));
                            if(users.size()==1){

                                ActRef.push().setValue(new ActivityData(myname + " " + mysurname, "Group "+gName+ " has been successful deleted", Long.toString(System.currentTimeMillis()), "acceptdeletegroup", PolKey, gId));
                                database.getReference("Groups").child(gId).removeValue();
                                database.getReference("Expenses").child(gId).removeValue();
                                database.getReference("Balance").child(gId).removeValue();
                                database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gId).removeValue();
                            }
                            //database.getReference("Groups").child(gId).removeValue();

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
                getMyName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        System.out.println("mapnameeeeeeeeeeeeeeeeeeee"+mapname);
                        if(mapname!=null) {
                            myname = (String)mapname.get("name");
                            mysurname = (String)mapname.get("surname");
                            DatabaseReference PolRef = database.getReference("Pols").child(gId).push();
                            String PolKey = PolRef.getKey();
                            PolData p = new PolData(String.format("%d", users.size()), "1");
                            PolRef.setValue(p);
                            PolRef.child("acceptsUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(myname+" "+mysurname);
                            PolRef.child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            DatabaseReference ActRef = database.getReference("Activities").child(gId).push();

                            ActRef.setValue(new ActivityData(myname+" "+mysurname, myname+" "+mysurname +" proposed to leave group " + gName, Long.toString(System.currentTimeMillis()), "leavegroup", PolKey, gId));
                            if(users.size()==1){

                                ActRef.push().setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " has been successful deleted from group " + gName, Long.toString(System.currentTimeMillis()), "acceptleavegroup", PolKey, gId));
                                database.getReference("Groups").child(gId).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gId).removeValue();
                                database.getReference("Balance").child(gId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                for(String k:usersId){
                                    database.getReference("Balance").child(gId).child(k).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });



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
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                    nametmp = (String) map.get("name");
                    desctmp = (String) map.get("description");
                    namet.setText(nametmp);
                    desc.setText(desctmp);

                    if (map.get("imagePath") == null) {
                        im.setImageResource(R.drawable.group_default);
                    } else {

                        String p = (String) map.get("imagePath");
                        image = p;

                        if (p == null) {

                        } else {
                            Glide.with(getApplicationContext()).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(im) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    im.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                        }
                    }
                }

                //Group g = new Group((String)map.get("gId"),(String) map.get("surname"), (String)map.get("defaultCurrency"));

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

                if (g.getMemberList().size() != 0) {

                    for (final String k : g.getMemberList()) {

                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Users").child(k);

                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();

                                if (map3 != null) {

                                    String s = map3.get("name") + " " + map3.get("surname");
                                    users.add(s);
                                    uAdapter.notifyDataSetChanged();

                                } else {

                                    Toast.makeText(GroupInfoActivity.this, "no user key found!", Toast.LENGTH_LONG).show();

                                }

                                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();

                                if (map2 != null) {

                                    for (final String k : map2.keySet()) {

                                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef3 = database3.getReference("Users").child(k);
                                        myRef3.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();
                                                if (map3 != null) {
                                                    String p = null;
                                                    if (map3.get("imagePath") == null) {
                                                        p = "https://firebasestorage.googleapis.com/v0/b/allaromana-3f98e.appspot.com/o/group_default.png?alt=media&token=40bc93f4-6b97-466e-b130-e140f57c5895";
                                                    } else {
                                                        p = map3.get("imagePath").toString();
                                                    }
                                                    String s = map3.get("name") + " " + map3.get("surname");
                                                    users.add(s);
                                                    user_l.add(new User(null, null, map3.get("name").toString(), map3.get("surname").toString(), p));
                                                    usersId.add(k);
                                                    uAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                } else {
                                    Toast.makeText(GroupInfoActivity.this, "No users found!", Toast.LENGTH_LONG).show();
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
        final int CAMERA_CAPTURE = 1;
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

                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    imageUrl = selectedImageUri;
                    ImageC = true;
                    performCrop();
                } else {
                    if (data != null) {
                        ImageC = true;
                        selectedImageUri = data.getData();
                        imageUrl= selectedImageUri;
                        performCrop();
                        System.out.println("++++++++---->" + selectedImageUri.toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (shouldShowRequestPermissionRationale(
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                // Explain to the user why we need to read the contacts
                            }

                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                            // app-defined int constant that should be quite unique

                            return;
                        }
                        Bitmap photo = null;
                        try {
                            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("................. galleria photo" + photo);
                        ImageView imageG = (ImageView) findViewById(R.id.im_g);
                        imageG.setImageBitmap(photo);
                    }

                }
            }else if(requestCode==CROP_PIC){
                Bundle extras = data.getExtras();
                System.out.println("......bundle"+extras);
                Bitmap thePic = extras.getParcelable("data");
                System.out.println("...bitmap"+thePic);
                ImageView picView = (ImageView) findViewById(R.id.im_g);
                picView.setImageBitmap(thePic);
                System.out.println(".........Url image"+imageUrl);
                System.out.println(".........Url image"+outputFileUri);
                File f = new File(outputFileUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                f = new File(outputFileUri.getPath());
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 0 , bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
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
                        myRef3 = database3.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups").child(gId).child("name");
                        myRef3.setValue(newname);

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
                    LinearLayout ll=(LinearLayout) findViewById(R.id.llgourpinfo);
                    ll.setVisibility(View.INVISIBLE);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    try {
                        System.out.println(".......carica in"+outputFileUri.toString().substring(7));
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
                                DatabaseReference myRef = database.getReference("Groups/"+ gId + "/imagePath");
                                myRef.setValue(downloadUrl.toString());
                                System.out.println("------------>GroupPAth"+"Groups/"+ gId + "/imagePath"+ downloadUrl);
                                System.out.println("------------>UserPAth"+"Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+gId + "/imagePath"+ downloadUrl);
                                DatabaseReference myRef2 = database.getReference("Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Groups/"+gId + "/imagePath");
                                myRef2.setValue(downloadUrl.toString());
                                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);
                                in.putExtra("groupId", gId);
                                in.putExtra("groupName", gName);
                                in.putExtra("imagePath", downloadUrl.toString());
                                setResult(RESULT_OK, null);
                                finish();
                                Log.d("myStorage", "success!");
                            }
                        });
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Intent in = new Intent(GroupInfoActivity.this, GroupActivity.class);
                in.putExtra("imagePath", gImage);
                in.putExtra("groupId", gId);
                in.putExtra("groupName", gName);
                System.out.println("Devo capire"+in.getExtras());
                setResult(RESULT_OK, in);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }
    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUrl, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("return-data", true);
            final Intent cIntent = Intent.createChooser(cropIntent, "Tha image should be cropped,select a source");
            startActivityForResult(cIntent , 2);
        }
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
