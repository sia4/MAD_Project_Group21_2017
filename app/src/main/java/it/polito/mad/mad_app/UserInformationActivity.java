package it.polito.mad.mad_app;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.mad.mad_app.model.Group;

public class UserInformationActivity extends AppCompatActivity {

    private TextView name, surname, email, username;
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
        changePhoto=(Button) findViewById(R.id.changePhoto);
        //username = (TextView) findViewById(R.id.username_u);

        nameed = (EditText) findViewById(R.id.name_u_ed);
        surnameed = (EditText) findViewById(R.id.surname_u_ed);

        im = (ImageView) findViewById(R.id.im_u);

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
                    //username.setText((String)map.get("username"));
                    String p = (String) map.get("imagePath");
                    //TODO controllare che l'immagine sia presa!!
                    Glide.with(getApplicationContext()).load(p).into(im);
                    //if(progressBar.isActivated())
                    //progressBar.setVisibility(View.INVISIBLE);
                    //gAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        /*Button button = (Button) findViewById(R.id.sendVerificationEmail);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();

                u.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email verification sent.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error sending email verification.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });*/
        changePhoto.setOnClickListener(new View.OnClickListener(){
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
                    intent.putExtra("crop", "true");
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
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
                    intent.putExtra("crop", "true");
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
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
                final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size() - 1), "Select Source");
                cameraIntents.remove(cameraIntents.size() - 1);
                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, 1);
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
                        System.out.println("++++++++---->" + selectedImageUri.toString());
                        Bitmap photo = null;
                        try {
                            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("................. galleria photo" + photo);
                        ImageView imageG = (ImageView) findViewById(R.id.im_u);
                        imageG.setImageBitmap(photo);

                    }

                }
            }else if(requestCode==CROP_PIC){
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.im_u);
                picView.setImageBitmap(thePic);
                File f = new File(imageUrl.getPath());
                if (f.exists()) {
                    f.delete();
                }
                f = new File(imageUrl.getPath());
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
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
                        System.out.println(".......carica in"+imageUrl.toString().substring(7));
                        InputStream stream = new FileInputStream(new File(imageUrl.toString().substring(7)));
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
                                DatabaseReference myRef = database.getReference("Users/"+uId + "/ImageUPath");
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
