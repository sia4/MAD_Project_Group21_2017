package it.polito.mad.mad_app;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.Invite;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.User;
import it.polito.mad.mad_app.model.UserData;


public class InsertGroupActivity extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private static final int REQUEST_INVITE = 0;
    private FirebaseAuth auth;
    private Boolean ImageC;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String GroupName;
    private Uri outputFileUri;
    private String GroupDescription;
    private String UserEmail;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private User ud;
    private List<User> u_l=new ArrayList<>();
    private DatabaseReference myRef;
    private String key;
    private Uri imageUrl;
    private Uri downloadUrl;
    private ProgressBar progressBar;
    private String picturePath;
    private List<String> u= new ArrayList<>();
    private Map<String,String>my= new TreeMap<>();//TODO da cambiare
    private Map<String,Boolean>m= new TreeMap<>();
    private UserAdapterIm uAdapter = null;
    String uKey = null;
    String uName = null;
    private List<String> userNotPresentInDb = new ArrayList<String>();
    private String userNotPresentInDbMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageC=false;
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
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;
        uKey = currentFUser.getUid();
        if(currentFUser != null) {

            uKey = currentFUser.getUid();

            if (uKey != null) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(uKey);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User myu = dataSnapshot.getValue(User.class);
                        uName=myu.getName()+" "+myu.getSurname();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            }
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_group_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button loadIm=(Button) findViewById(R.id.load);
        loadIm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //todo this works well
                /*Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);*/

                // Determine Uri of camera image to save.
                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
                root.mkdirs();
                final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
                final File sdImageMainDirectory = new File(root, fname);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
                System.out.println("------>outputFileUri"+outputFileUri);
                final List<Intent> cameraIntents = new ArrayList<Intent>();
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                final PackageManager pManager = getPackageManager();
                final List<ResolveInfo> Im =pManager.queryIntentActivities(pickIntent, 0);
                for(ResolveInfo res : Im) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(pickIntent);
                    intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    /*intent.putExtra("crop", "true");
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);*/
                    System.out.println(".........image intent " + intent);
                    cameraIntents.add(intent);
                }
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = getPackageManager();
                final List<ResolveInfo> Cam =packageManager.queryIntentActivities(captureIntent, 0);
                for(ResolveInfo res : Cam) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    /*intent.putExtra("crop", "true");
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);*/
                    System.out.println(".........camera intent " + intent);
                    cameraIntents.add(intent);
                }
                /*final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                captureIntent.putExtra("crop", "true");
                captureIntent.putExtra("outputX", 200);
                captureIntent.putExtra("outputY", 200);
                captureIntent.putExtra("aspectX", 1);
                captureIntent.putExtra("aspectY", 1);
                captureIntent.putExtra("scale", true);*/
                /*// Filesystem. if we want also include Documenti.
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //cameraIntents.add(captureIntent );
                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");*/
                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size()-1), "Select Source");
                cameraIntents.remove(cameraIntents.size()-1);
                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, 1);
            }
        });
        getSupportActionBar().setTitle("New Group");
        final EditText Uemail = (EditText) findViewById(R.id.User1);
        Button userbutton = (Button) findViewById(R.id.Adduser1);
        final RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.usersToAdd);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertGroupActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));
        uAdapter = new UserAdapterIm(u_l);
        userRecyclerView.setAdapter(uAdapter);
        final DatabaseReference mTest = FirebaseDatabase.getInstance().getReference();
        final Query quer=mTest.child("Users").orderByChild("email");
        userbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserEmail = Uemail.getText().toString().toLowerCase();
                quer.equalTo(UserEmail).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            ud = userSnapshot.getValue(User.class);
                            key=userSnapshot.getKey();
                        }                        if(key == null) {
                            Uemail.setText("");
                            new AlertDialog.Builder(InsertGroupActivity.this)
                                    .setTitle("You friend has not downloaded the app, yet!")
                                    .setMessage("Create the group and invite him later from group options.")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            //userNotPresentInDbMail = UserEmail;
                                            //onInviteClicked(UserEmail);
                                            //onInviteClicked("nome", "cognome", "groupname", "identificativo");
                                        }}).show();

                        } else {
                            Uemail.setText("");
                            m.put(key,true);
                            my.put(key,ud.getName() + " " + ud.getSurname());
                            u_l.add(ud);
                            u.add(ud.getName() + " " + ud.getSurname());
                            uAdapter.notifyDataSetChanged();
                            key=null;
                        }
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
        final int CAMERA_CAPTURE = 1;
        final int CROP_PIC = 2;
        /*if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageG=(ImageView) findViewById(R.id.ImageG);
            imageG.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/
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
                    imageUrl = outputFileUri;
                    System.out.println("......."+imageUrl);
                    //downloadUrl = selectedImageUri;
                    ImageC = true;
                    performCrop();
                        /*Bitmap photo = null;
                        try {
                            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(".................camera" + photo);
                        ImageView imageG = (ImageView) findViewById(R.id.ImageG);
                        imageG.setImageBitmap(photo);*/
                } else {
                    if (data != null) {
                        System.out.println("++++++----------"+data);
                        ImageC = true;
                        selectedImageUri = data.getData();
                            /*if(selectedImageUri==null){
                                String p=data.getAction();
                                selectedImageUri = Uri.parse(p);
                            }*/
                        imageUrl = selectedImageUri;
                        performCrop();
                        System.out.println("++++++----->"+selectedImageUri);
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
                        System.out.println("................." + photo);
                        ImageView imageG = (ImageView) findViewById(R.id.ImageG);
                        imageG.setImageBitmap(photo);

                    }

                }
            }else if(requestCode==CROP_PIC){
                Bundle extras = data.getExtras();
                System.out.println("......bundle"+extras);
                Bitmap thePic = extras.getParcelable("data");
                System.out.println("...bitmap"+thePic);
                ImageView picView = (ImageView) findViewById(R.id.ImageG);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                final EditText Gname = (EditText) findViewById(R.id.GroupName);
                final EditText Gdescription = (EditText) findViewById(R.id.GroupDescription);
                final Spinner Tcurrency = (Spinner) findViewById(R.id.GroupCurrency);

                GroupName = Gname.getText().toString();
                GroupDescription = Gdescription.getText().toString();


                if (GroupName.equals("")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert group name.", Toast.LENGTH_LONG).show();

                } else if (GroupDescription.equals("")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert group description.", Toast.LENGTH_LONG).show();

                }/*else if (m.isEmpty()) {
                    Toast.makeText(InsertGroupActivity.this, "Please insert at least one other member.", Toast.LENGTH_LONG).show();
                }*/ else if (Tcurrency.getSelectedItem().toString().equals("Select currency")) {

                    Toast.makeText(InsertGroupActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();

                } else if(ImageC==false){
                    Toast.makeText(InsertGroupActivity.this, "Please choose an image.", Toast.LENGTH_LONG).show();
                } else{
                    progressBar = (ProgressBar) findViewById(R.id.progress_bar_insertgroups);
                    progressBar.setVisibility(View.VISIBLE);
                    LinearLayout ll=(LinearLayout) findViewById(R.id.llayout);
                    ll.setVisibility(View.INVISIBLE);
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Groups");
                    final String groupId = myRef.push().getKey();
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    try {
                        //try uploading it
                        //InputStream stream = new FileInputStream(new File(downloadUrl.toString().substring(7)));
                        System.out.println(".......carica in"+outputFileUri.toString().substring(7));
                        //InputStream stream = new FileInputStream(new File(downloadUrl.toString().substring(7)));
                        InputStream stream = new FileInputStream(new File(outputFileUri.toString().substring(7)));
                        StorageReference imageStorage = mStorageRef.child(groupId);
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
                                Group G = new Group(GroupName, GroupDescription, Tcurrency.getSelectedItem().toString());
                                G.addMembers(m);
                                G.addMember(uKey);
                                G.setImagePath(downloadUrl.toString());
                                my.put(uKey, uName);
                                myRef.child(groupId).setValue(G);
                                Set keys = m.keySet();
                                Set others = m.keySet();
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

                                    for (Iterator n = others.iterator(); n.hasNext(); ) {
                                        String k = (String) n.next();
                                        if (k != key) {
                                            myRef = database.getReference("/Balance/" + groupId + "/" + key + "/" + k + "/" + "name");
                                            myRef.setValue(my.get(k));
                                            myRef = database.getReference("/Balance/" + groupId + "/" + key + "/" + k + "/" + "value");
                                            myRef.setValue(0.00);
                                        }
                                    }
                                }
                                setResult(RESULT_OK, null);
                                finish();
                                Log.d("myStorage", "success!");
                            }
                        });
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                    /*for(String s : userNotPresentInDb) {;
                        DatabaseReference myRef2 = database.getReference("/Invites");
                        String inviteId = myRef2.push().getKey();
                        //myRef.setValue(gPath);
                        Invite invite = new Invite(s, groupId, G.getName(), G.getImagePath());
                        myRef.child(inviteId).setValue(invite);
                    }*/
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /*private void onInviteClicked(String email) {
        Intent intent = new AppInviteInvitation.IntentBuilder("Invite your friends!")
                .setMessage("You have been invited to AllaRomana (mail: "+ email)
                .setEmailHtmlContent("Hi! I invited you to join a group on AllaRomana. Download the app and SignIn with the email "+ email +" to join the group. See you on AllaRomana!")
                .setDeepLink(Uri.EMPTY)
                .setEmailSubject("Invite you on AllaRomana")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("INFO", "onActivityResult: sent invitation " + id);
                }
                userNotPresentInDb.add(userNotPresentInDbMail);
                finish();
            } else {
                System.out.println("Errore..." + resultCode);
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }*/
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