package it.polito.mad.mad_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Map;

import static it.polito.mad.mad_app.model.ImageMethod.circle_image;
import static it.polito.mad.mad_app.model.ImageMethod.create_image;
import static it.polito.mad.mad_app.model.ImageMethod.performCrop;
import static it.polito.mad.mad_app.model.ImageMethod.require_image;

public class UserInformationActivity extends AppCompatActivity {

    private TextView name, surname, email;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private Button changePhoto;
    private EditText nameed, surnameed;
    private StorageReference mStorageRef;
    private ImageView im;
    private Uri imageUrl;
    private boolean flag_name_edited = false;
    private boolean flag_surname_edited = false;
    private Uri outputFileUri;
    private Uri downloadUrl;
    private boolean ImageC=false;
    private ProgressBar progressBar;
    String uId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_user_toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        uId = i.getStringExtra("userId");
        String UserInfo=i.getStringExtra("UserInfo");
        changePhoto=(Button) findViewById(R.id.changePhoto);
        System.out.println("+++++++++"+i.getComponent());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("User Information");
        } else {
            System.out.println("ELLO?");
        }

        name=(TextView) findViewById(R.id.name_u);
        surname = (TextView) findViewById(R.id.surname_u);
        email = (TextView) findViewById(R.id.email_u);

        //username = (TextView) findViewById(R.id.username_u);

        nameed = (EditText) findViewById(R.id.name_u_ed);
        surnameed = (EditText) findViewById(R.id.surname_u_ed);

        im = (ImageView) findViewById(R.id.im_u);

        if(UserInfo!=null)
        {
            changePhoto.setVisibility(View.VISIBLE);

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
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String n = (String)name.getText();
                    name.setVisibility(View.GONE);
                    nameed.setVisibility(View.VISIBLE);
                    nameed.setText(n);
                    flag_name_edited = true;

                }
            });

            surname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String n = (String)surname.getText();
                    surname.setVisibility(View.GONE);
                    surnameed.setVisibility(View.VISIBLE);
                    surnameed.setText(n);
                    flag_surname_edited = true;

                }
            });

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map!=null) {
                    name.setText((String)map.get("name"));
                    surname.setText((String)map.get("surname"));
                    email.setText((String)map.get("email"));
                    if(map.get("imagePath")!=null) {
                        String p = (String) map.get("imagePath");
                        if (p != null) {
                            circle_image(getApplicationContext(),im,p);
                        }
                    }else {
                        circle_image(getApplicationContext(),im,R.drawable.user_icon_default);
                            }
                    }
                }

            @Override
            public void onCancelled(DatabaseError error) {
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
                    ImageView imageG = (ImageView) findViewById(R.id.im_u);
                    imageG.setImageBitmap(photo);

                }else{
                    Bitmap thePic = extras.getParcelable("data");
                    final ImageView picView = (ImageView) findViewById(R.id.im_u);
                    //picView.setImageBitmap(thePic);
                    create_image(outputFileUri,thePic);
                    circle_image(getApplicationContext(),picView,outputFileUri);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_menu_done:


                if(flag_name_edited) {
                    Log.d("USERINFO", "Nome cambiato");
                    String newName = nameed.getText().toString();
                    if(newName != null && !newName.equals("")) {
                        Log.d("USERINFO", "Nome cambiato: "+uId+ " " +newName);
                        DatabaseReference myRef = database.getReference("Users/"+uId + "/name");
                        myRef.setValue(newName);
                    }
                }

                if(flag_surname_edited) {
                    String newSurname = surnameed.getText().toString();
                    if(newSurname!= null && !newSurname.equals("")) {
                        DatabaseReference myRef = database.getReference("Users/"+uId + "/surname");
                        myRef.setValue(newSurname);
                    }
                }
                if(ImageC==true){
                    progressBar = (ProgressBar) findViewById(R.id.progress_bar_user_info);
                    progressBar.setVisibility(View.VISIBLE);
                    LinearLayout ll=(LinearLayout) findViewById(R.id.llayout_user_info);
                    ll.setVisibility(View.INVISIBLE);
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    try {
                        System.out.println(".......carica in"+outputFileUri.toString().substring(7));
                        InputStream stream = new FileInputStream(new File(outputFileUri.toString().substring(7)));
                        StorageReference imageStorage = mStorageRef.child(uId);
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
                                DatabaseReference myRef = database.getReference("Users/"+uId + "/imagePath");
                                myRef.setValue(downloadUrl.toString());
                                setResult(RESULT_OK, null);
                                finish();
                                Log.d("myStorage", "success!");
                        }
                    });
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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

}
