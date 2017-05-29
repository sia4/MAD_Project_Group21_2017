package it.polito.mad.mad_app;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Invite;
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
    private boolean imageC = false;
    private Uri imageUrl;

    private Map<String,String>userNames = new TreeMap<>();
    private Map<String,Boolean>userKeys = new TreeMap<>();

    private String[] newUsers = new String[10];
    int iU;

    private UserAdapterIm uAdapter = null;
    String uKey = null;
    String uName = null;
    private Button loadIm;
    private Button userbutton;

    private boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_group);

        auth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(InsertGroupActivity.this, LoginActivity.class));
                    finish();
                } else {

                }
            }
        };

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
        loadIm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&  checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED ) {
                    if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    }

                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    Log.d("Insert Group Activity","ho richiesto i permessi");
                    return;
                }

                Log.d("Insert Group Activity","ho già i permessi");
                LoadImage();
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



                userEmail = Uemail.getText().toString().toLowerCase();
                if(!userEmail.equals("")) {
                    disableUsers();
                    quer.equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                                ud = userSnapshot.getValue(User.class);
                                key = userSnapshot.getKey();
                            }

                            if (key == null) {

                                new AlertDialog.Builder(InsertGroupActivity.this)
                                        .setTitle("You friend has not downloaded the app, yet!")
                                        .setMessage("Create the group and invite him later from group options.")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                if(iU < 9) {
                                                    String newUserEmail = Uemail.getText().toString().toLowerCase().trim();
                                                    newUsers[iU++] = newUserEmail;
                                                    Uemail.setText("");
                                                    usersList.add(new User(newUserEmail, newUserEmail, "", ""));
                                                    uAdapter.notifyDataSetChanged();
                                                } else {
                                                    new AlertDialog.Builder(InsertGroupActivity.this)
                                                            .setTitle("Error!")
                                                            .setMessage("You can add at least 10 new users!")
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {}
                                                            }).show();
                                                }
                                            }
                                        }).show();

                            } else {
                                Uemail.setText("");

                                if(uKey.equals(key)) {
                                    new AlertDialog.Builder(InsertGroupActivity.this)
                                            .setTitle("Warning!")
                                            .setMessage("You are already present in this group.")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {                                      }
                                            }).show();
                                } else if(userKeys.containsKey(key)){
                                    new AlertDialog.Builder(InsertGroupActivity.this)
                                            .setTitle("Warning!")
                                            .setMessage("The user is already present.")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {                                      }
                                            }).show();
                                } else {
                                    userKeys.put(key, true);
                                    userNames.put(key, ud.getName() + " " + ud.getSurname());

                                    usersList.add(ud);

                                    uAdapter.notifyDataSetChanged();
                                }
                                key = null;
                            }

                            enableUsers();
                        }

                        @Override
                        public void onCancelled(DatabaseError eError) {

                        }
                    });
                }

            }

        });

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
                    imageC = true;
                    final PackageManager pManager = getPackageManager();
                    Intent cropIntent=performCrop(imageUrl,pManager);
                    if(cropIntent!=null){
                        final Intent cIntent = Intent.createChooser(cropIntent, "The image should be cropped,select a source");
                        startActivityForResult(cIntent , 2);
                    }
                    else{
                        Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                } else {
                    if (data != null) {
                        imageC = true;
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
                    ImageView imageG = (ImageView) findViewById(R.id.ImageG);
                    imageG.setImageBitmap(photo);

                }else{
                    Bitmap thePic = extras.getParcelable("data");
                    final ImageView picView = (ImageView) findViewById(R.id.ImageG);
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d("Insert Group Activity","dentro on request permission result");
        switch (requestCode) {
            case 1: {
                Log.d("Insert Group Activity","case 1");
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
        String code = c.getCurrencyCode(curr); // EUR € --> EUR per l'inserimento in DB

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
                myRef = database.getReference("/Activities/" + key);
                DatabaseReference ActRead=database.getReference().child("ActivitiesRead").child(key).child(groupId);
                String actId=myRef.push().getKey();
                myRef.child(actId).setValue(new ActivityData( myname, myname + " added you in group " + groupN, Long.toString(System.currentTimeMillis()), "addgroup", groupId, groupId));
                ActRead.child(actId).setValue(false);
            }
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/name/");
            myRef.setValue(G.getName());
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/imagePath/");
            myRef.setValue(G.getImagePath());
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/lastOperation/");

            if(key.equals(uKey))
                myRef.setValue("You have created the group.");
            else
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

            for(String s : newUsers) {

                if (s == null)
                    break;
                Log.d("Insert Group Activity", "Sono qui");
                DatabaseReference myRef2 = database.getReference("/Invites/");

                String inviteId = myRef2.push().getKey();
                Log.d("Insert Group Activity", inviteId);
                Invite invite = new Invite(s, groupId, G.getName(), null);
                myRef2.child(inviteId).setValue(invite);
            }

            if(iU != 0) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, newUsers);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your are invited on YourSlice");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi! I invited you to join a group on YourSlice. Download the app and SignIn with this email to join the group. See you on YourSlice!");
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(InsertGroupActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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

    /*
    private void getImageFromDevice() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "YourSlice" + File.separator);
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
*/
    public void LoadImage(){
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
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


}