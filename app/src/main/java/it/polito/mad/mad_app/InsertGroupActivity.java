package it.polito.mad.mad_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.User;

import static it.polito.mad.mad_app.model.ImageMethod.circle_image;
import static it.polito.mad.mad_app.model.ImageMethod.create_image;
import static it.polito.mad.mad_app.model.ImageMethod.performCrop;
import static it.polito.mad.mad_app.model.ImageMethod.require_image;


public class InsertGroupActivity extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String groupName;
    private Uri outputFileUri;
    private String groupDescription;
    private String userEmail;
    private FirebaseDatabase database;
    private User ud;
    private List<User> usersList = new ArrayList<>();
    private DatabaseReference myRef;
    private String key;
    private Uri downloadUrl;

    private Map<String,String>userNames = new TreeMap<>();
    private Map<String,Boolean>userKeys = new TreeMap<>();

    private UserAdapterIm uAdapter = null;
    String uKey = null;
    String uName = null;
    private Button loadIm;
    private Button userbutton;

    private boolean imageC;
    private boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_group);

        auth = FirebaseAuth.getInstance();
        //todo chiedere cos'è
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(InsertGroupActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        //todo fine

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_group_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Group");

        imageC = false;

        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if(currentFUser != null) {

            uKey = currentFUser.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(uKey);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User myUser = dataSnapshot.getValue(User.class);
                    uName = myUser.getName()+" "+myUser.getSurname();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("Insert Group Activity", "Failed to read value.", error.toException());
                }
            });

        }

        loadIm = (Button) findViewById(R.id.load);
        loadIm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    }
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    return;
                }

                getImageFromDevice();

            }
        });

        final EditText Uemail = (EditText) findViewById(R.id.User1);

        userbutton = (Button) findViewById(R.id.Adduser1);

        final RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.usersToAdd);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertGroupActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        uAdapter = new UserAdapterIm(usersList);
        userRecyclerView.setAdapter(uAdapter);

        final DatabaseReference mTest = FirebaseDatabase.getInstance().getReference();
        final Query quer = mTest.child("Users").orderByChild("email");

        userbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                disableUsers();

                userEmail = Uemail.getText().toString().toLowerCase();
                quer.equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                            ud = userSnapshot.getValue(User.class);
                            key = userSnapshot.getKey();
                        }

                        if (key == null) {
                            Uemail.setText("");
                            new AlertDialog.Builder(InsertGroupActivity.this)
                                    .setTitle("You friend has not downloaded the app, yet!")
                                    .setMessage("Create the group and invite him later from group options.")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {                                      }
                                    }).show();

                        } else {
                            Uemail.setText("");

                            userKeys.put(key, true);
                            userNames.put(key, ud.getName() + " " + ud.getSurname());

                            usersList.add(ud);

                            uAdapter.notifyDataSetChanged();
                            key = null;
                        }

                        enableUsers();
                    }

                    @Override
                    public void onCancelled(DatabaseError eError) {

                    }
                });

            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int CROP_PIC = 2;
        Uri imageUrl;
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri;

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
                    imageC = true;
                    imageUrl = outputFileUri;
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
                    imageC = true;
                    selectedImageUri = data.getData();
                    imageUrl= selectedImageUri;
                    final PackageManager pManager = getPackageManager();
                    Intent cropIntent=performCrop(imageUrl,pManager);
                    if(cropIntent!=null){
                        startActivityForResult(cropIntent , 2);
                    }
                    else{
                        Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }else if(requestCode == CROP_PIC){
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.ImageG);
                create_image(outputFileUri,thePic);
                circle_image(getApplicationContext(),picView,outputFileUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getImageFromDevice();
                }
                return;
            }
            default: {
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                EditText Gname = (EditText) findViewById(R.id.GroupName);
                EditText Gdescription = (EditText) findViewById(R.id.GroupDescription);
                final Spinner Tcurrency = (Spinner) findViewById(R.id.GroupCurrency);


                groupName = Gname.getText().toString();
                groupDescription = Gdescription.getText().toString();

                if (groupName.equals("")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert group name.", Toast.LENGTH_LONG).show();

                } else if (groupDescription.equals("")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert group description.", Toast.LENGTH_LONG).show();

                } else if (Tcurrency.getSelectedItem().toString().equals("Select currency")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();

                } else{

                    disableAll();

                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Groups");

                    final String groupId = myRef.push().getKey();
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    if(imageC) {
                        try {
                            //try uploading it
                            Log.d("Insert Group Activity", "carica in: "+ outputFileUri.toString().substring(7));
                            InputStream stream = new FileInputStream(new File(outputFileUri.toString().substring(7)));
                            StorageReference imageStorage = mStorageRef.child(groupId);
                            UploadTask uploadTask = imageStorage.putStream(stream);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("myStorage", "failure :(");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @SuppressWarnings("VisibleForTests")
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    downloadUrl = taskSnapshot.getDownloadUrl();

                                    uploadGroup(groupName, groupDescription, Tcurrency.getSelectedItem().toString(), downloadUrl.toString(), groupId);
                                      //TODO: aggiungere le cose di currencies alla uploadGroup
                                    Log.d("Insert Group Activity", "insertion success!");
                                    setResult(RESULT_OK, null);
                                    finish();

                                }
                            });
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        uploadGroup(groupName, groupDescription, Tcurrency.getSelectedItem().toString(), "", groupId);

                        Log.d("Insert Group Activity", "insertion success!");
                        setResult(RESULT_OK, null);
                        finish();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void uploadGroup(String groupN, String groupD, String curr, String imagePath, String groupId) {

        Currencies c = new Currencies();
        String code = c.getCurrencyCode(curr);

        Group G = new Group(groupN, groupD, code);
        G.addMembers(userKeys);
        G.addMember(uKey);
        G.setImagePath(imagePath);
        userNames.put(uKey, uName);
        myRef.child(groupId).setValue(G);
        Set keys = userKeys.keySet();
        Set others = userNames.keySet();

        String myname = userNames.get(FirebaseAuth.getInstance().getCurrentUser().getUid());

        database = FirebaseDatabase.getInstance();

        for (Iterator i = keys.iterator(); i.hasNext(); ) {

            String key = (String) i.next();
            if (!key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                myRef = database.getReference("/Activities/" + key).push();
                myRef.setValue(new ActivityData( myname, myname + " added you in group " + groupN, Long.toString(System.currentTimeMillis()), "addgroup", groupId, groupId));
            }
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/name/");
            myRef.setValue(G.getName());
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/imagePath/");
            myRef.setValue(G.getImagePath());
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/lastOperation/");
            myRef.setValue(uName + " has created the group.");
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/dateLastOperation/");
            myRef.setValue(Long.toString(System.currentTimeMillis()));
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/missing/");
            myRef.setValue("no");

            for (Iterator n = others.iterator(); n.hasNext(); ) {
                String k = (String) n.next();
                if (!k.equals(key)) {
                    myRef = database.getReference("/Balance/" + groupId + "/" + key + "/" + k + "/" + "name");
                    myRef.setValue(userNames.get(k));
                    myRef = database.getReference("/Balance/" + groupId + "/" + key + "/" + k + "/" + "value");
                    myRef.setValue("0.00");
                }
            }
        }
    }

    private void disableAll(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_insertgroups);
        progressBar.setVisibility(View.VISIBLE);

        userbutton.setEnabled(false);
        loadIm.setEnabled(false);

        findViewById(R.id.GroupName).setEnabled(false);
        findViewById(R.id.GroupDescription).setEnabled(false);
        findViewById(R.id.GroupCurrency).setEnabled(false);
        findViewById(R.id.User1).setEnabled(false);

    }

    private void disableUsers() {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_insertgroups);
        progressBar.setVisibility(View.VISIBLE);

        findViewById(R.id.User1).setFocusable(false);
        userbutton.setEnabled(false);

    }

    private void enableUsers() {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_insertgroups);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.User1).setFocusableInTouchMode(true);
        userbutton.setEnabled(true);

    }

    private void getImageFromDevice() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "AllaRomana" + File.separator);
        root.mkdirs();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        final PackageManager pManager = getPackageManager();
        List<Intent> cameraIntents=require_image(outputFileUri,pManager);
        final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size()-1), "Select Source");
        cameraIntents.remove(cameraIntents.size()-1);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, 1);

    }
}