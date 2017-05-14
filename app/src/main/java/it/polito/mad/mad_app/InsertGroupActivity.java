package it.polito.mad.mad_app;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.User;


public class InsertGroupActivity extends AppCompatActivity {
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
    private Uri imageUrl;
    private Uri downloadUrl;

    private Map<String,String>userNames = new TreeMap<>();
    private Map<String,Boolean>userKeys = new TreeMap<>();

    private UserAdapterIm uAdapter = null;
    String uKey = null;
    String uName = null;
    private Button loadIm;
    private Button userbutton;

    private boolean imageC;

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

                // Determine Uri of camera image to save.
                //todo cambiare nome cartella con nome app
                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
                root.mkdirs();
                final String fname = "img_" + System.currentTimeMillis() + ".jpg";
                final File sdImageMainDirectory = new File(root, fname);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);

                Log.d("Insert Group Activity", "output File Uri: " + outputFileUri);

                final List<Intent> cameraIntents = new ArrayList<>();
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
                    Log.d("Insert Group Activity", "Image intent " + intent);
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
                    Log.d("Insert Group Activity", "Image intent " + intent);
                    cameraIntents.add(intent);
                }


            /*

            // Filesystem. if we want also include Documenti.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            //cameraIntents.add(captureIntent );
            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");*/

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size() - 1), "Select Source");
                cameraIntents.remove(cameraIntents.size() - 1);
                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, 1);

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
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

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

                    Log.d("Insert Group Activity", "image Url: "+ imageUrl);
                    System.out.println("......."+imageUrl);

                    performCrop();

                } else {
                    imageC = true;

                    Log.d("Insert Group Activity", "Data: "+ data);
                    selectedImageUri = data.getData();
                        /*if(selectedImageUri==null){
                            String p=data.getAction();
                            selectedImageUri = Uri.parse(p);
                        }*/
                    imageUrl = selectedImageUri;
                    performCrop();
                    Log.d("Insert Group Activity", "selectedImageUri: "+ selectedImageUri);

                    //System.out.println("++++++++---->" + selectedImageUri.toString());
                        /*String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImageUri,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        System.out.println("........... galleria picturePath "+picturePath);
                        cursor.close();
                        ImageView imageG=(ImageView) findViewById(R.id.ImageG);
                        imageG.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/

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

                    Log.d("Insert Group Activity", "photo: "+ photo);
                    ImageView imageG = (ImageView) findViewById(R.id.ImageG);
                    imageG.setImageBitmap(photo);

                }
            }else if(requestCode == CROP_PIC){
                Bundle extras = data.getExtras();
                Log.d("Insert Group Activity", "bundle: "+ extras);
                Bitmap thePic = extras.getParcelable("data");
                Log.d("Insert Group Activity", "bitmap: "+ thePic);
                ImageView picView = (ImageView) findViewById(R.id.ImageG);
                picView.setImageBitmap(thePic);
                Log.d("Insert Group Activity", "Url image: "+ imageUrl);
                Log.d("Insert Group Activity", "Url image: "+ outputFileUri);

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
        Group G = new Group(groupN, groupD, curr);
        G.addMembers(userKeys);
        G.addMember(uKey);
        G.setImagePath(imagePath);
        userNames.put(uKey, uName);
        myRef.child(groupId).setValue(G);
        Set keys = userKeys.keySet();
        Set others = userNames.keySet();

        database = FirebaseDatabase.getInstance();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/name/");
            myRef.setValue(G.getName());
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/imagePath/");
            myRef.setValue(G.getImagePath());
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/lastOperation/");
            myRef.setValue(uName + " has created the group.");
            myRef = database.getReference("/Users/" + key + "/Groups/" + groupId + "/dateLastOperation/");
            myRef.setValue(Long.toString(System.currentTimeMillis()));
            DecimalFormat df = new DecimalFormat("0.00");
            float f = 0;
            for (Iterator n = others.iterator(); n.hasNext(); ) {
                String k = (String) n.next();
                if (!k.equals(key)) {
                    myRef = database.getReference("/Balance/" + groupId + "/" + key + "/" + k + "/" + "name");
                    myRef.setValue(userKeys.get(k));
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
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            //
            // indicate image type and Uri
            cropIntent.setDataAndType(imageUrl, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            final Intent cIntent = Intent.createChooser(cropIntent, "Tha image should be cropped,select a source");
            // Add the camera options.
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cIntent , 2);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}