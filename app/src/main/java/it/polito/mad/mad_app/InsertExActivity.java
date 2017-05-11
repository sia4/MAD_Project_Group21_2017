package it.polito.mad.mad_app;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Balance;
import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;


public class InsertExActivity extends AppCompatActivity {


    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private String name;
    private String description;
    private String category;
    private String currency;
    private double value;
    private Boolean fish=false;
    private String algorithm;
    private String myname, mysurname;
    private String Gname = new String(), groupName = new String();
    private StorageReference mStorageRef;
    private Map<String,Balance>users_l=new TreeMap<>();
    private int i=0, ii=0;
    private double v = 0, v1 = 0;
    private Map<String, Double> values = new TreeMap<>();
    private String defaultcurrency;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<UserData> users = new ArrayList<>();
    static private RecyclerView userRecyclerView;
    static private AlgorithmParametersAdapter uAdapter;
    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri outputFileUri;
    private boolean flag_name_edited = false, flag_desc_edited = false, flag_img_edited = false;
    private String tmp;
    private Uri downloadUrl;
    private Boolean ImageC=false;
    private Uri imageUrl;
    private Button load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_ex_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("New Expense");

        userRecyclerView = (RecyclerView) findViewById(R.id.algorithmParameters);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertExActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        Gname = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");

        String cname = intent.getStringExtra("name");
        String cdescr = intent.getStringExtra("description");
        String cvaluetmp = intent.getStringExtra("value");
        final EditText Tname = (EditText) findViewById(R.id.Name);
        final EditText Tdescription = (EditText) findViewById(R.id.Description);
        final EditText Tvalue = (EditText) findViewById(R.id.value);

        if(cname!=null)
            Tname.setText(cname);
        if(cdescr!=null)
            Tdescription.setText(cdescr);

        final String uid = mAuth.getCurrentUser().getUid().toString();
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference("Groups").child(Gname).child("members");

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                if(map2!=null) {
                    map2.put(uid, uid); //aggiungo user corrente
                    for (final String k : map2.keySet()){
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Users").child(k);
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();
                                if(map3!=null) {
                                    String s = String.format("user %s added\n", (String)map3.get("name"));
                                    System.out.println(s);
                                    UserData u = new UserData("aaaa", (String)map3.get("name"), (String)map3.get("surname"), 5555);
                                    u.setuId(k);
                                    users.add(u);
                                    System.out.println("+++++++utente trovato"+k);
                                    if(k.equals(mAuth.getCurrentUser().getUid())){
                                        myname = (String)map3.get("name");
                                        mysurname = (String)map3.get("surname");
                                    }
                                    //uAdapter.notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(InsertExActivity.this, "no user key found!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        i++;

                    }
                    String sss = String.format("%d user founds", i);
                    Toast.makeText(InsertExActivity.this, sss, Toast.LENGTH_LONG).show();
                    //uAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(InsertExActivity.this, "no users found!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        System.out.println("2");

        Spinner spinner = (Spinner) findViewById(R.id.Currency);
        // Create an ArrayAdapter using the string array and a default spinner layout
        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef4 = database4.getReference("Groups").child(Gname);
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map!=null) {
                    defaultcurrency = (String)map.get("defaultcurrency");

                }
                else{
                    Toast.makeText(InsertExActivity.this, "no user key found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        System.out.println("3");

        List<String> Currencies = new ArrayList<>();
        Currencies.add("Select currency");
        Currencies.add("EUR €");
        //Currencies.add(defaultcurrency);
        //TODO AGGIUNGERE TUTTE LE CURRENCIES ALLO SPINNER

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.currency_item, Currencies);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //users.add(0, new UserData("null", "Me", "", 000));
        final Spinner Talgorithm = (Spinner)findViewById(R.id.ChooseAlgorithm);
        //final EditText Tvalue = (EditText) findViewById(R.id.value);
        final Spinner Tcurrency = (Spinner) findViewById(R.id.Currency);
        final TextView algInfo = (TextView) findViewById(R.id.alg_info);
        final TextView algInfoSmall = (TextView) findViewById(R.id.alg_info_small);


        Talgorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==1) {
                    if(Tvalue.getText().toString().equals("")) {
                        Toast.makeText(InsertExActivity.this, "Please insert the expense value.", Toast.LENGTH_LONG).show();
                        Talgorithm.setSelection(0);
                    } else {
                        String aaa = String.format("users before adapter: %d", users.size());
                        System.out.println(aaa);
                        uAdapter = new AlgorithmParametersAdapter(users, position, 10, algInfo, algInfoSmall);
                        userRecyclerView.setAdapter(uAdapter);
                    }

                }
                else if(position == 2) {
                    if (Tvalue.getText().toString().equals("")) {
                        Toast.makeText(InsertExActivity.this, "Please insert the expense value.", Toast.LENGTH_LONG).show();
                        Talgorithm.setSelection(0);

                    } else if( Tcurrency.getSelectedItem().toString().equals("Select currency")) {
                        Toast.makeText(InsertExActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();
                    } else {
                        uAdapter = new AlgorithmParametersAdapter(users, position, Float.parseFloat(Tvalue.getText().toString()),currency, algInfo, algInfoSmall);
                        userRecyclerView.setAdapter(uAdapter);
                    }
                } else {
                    userRecyclerView.setAdapter(new AlgorithmParametersAdapter(new ArrayList<UserData>(), position, 10, algInfo, algInfoSmall));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );

        System.out.println("4");
        /*
       Tvalue.addTextChangedListener(new TextWatcher() {
           int p;
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               if(Talgorithm.getSelectedItem().toString().equals("by import") && !Tcurrency.getSelectedItem().toString().equals("Select currency")) {
                   if(!Tvalue.getText().toString().isEmpty()) {
                       uAdapter = new AlgorithmParametersAdapter(users, 2, Float.parseFloat(Tvalue.getText().toString()), Tcurrency.getSelectedItem().toString(), algInfo, algInfoSmall);
                       userRecyclerView.setAdapter(uAdapter);
                   }
                   else{
                       uAdapter = new AlgorithmParametersAdapter(users, 2, 0, Tcurrency.getSelectedItem().toString(), algInfo, algInfoSmall);
                       userRecyclerView.setAdapter(uAdapter);
                   }
               }
           }
       });
        */
        String s = "";
        load=(Button) findViewById(R.id.load_ex);
        load.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
                    System.out.println(".........camera intent " + intent);
                    cameraIntents.add(intent);
                }
                final Intent chooserIntent = Intent.createChooser(cameraIntents.get(cameraIntents.size()-1), "Select Source");
                cameraIntents.remove(cameraIntents.size()-1);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
                startActivityForResult(chooserIntent, 1);
            }
        });

    }
    /*
    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
         //       ".jpg",         /* suffix */
           //     storageDir      /* directory */
        /*);

         Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/
    /*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(InsertExActivity.this, "Error creating photo file!", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                //Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    */
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
                    //downloadUrl = selectedImageUri;
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

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_menu_done:

                final EditText Tname = (EditText) findViewById(R.id.Name);
                final EditText Tdescription = (EditText) findViewById(R.id.Description);
                final Spinner Tcategory = (Spinner) findViewById(R.id.Category);
                final Spinner Tcurrency = (Spinner) findViewById(R.id.Currency);
                final Spinner Talgorithm = (Spinner) findViewById(R.id.ChooseAlgorithm);
                final EditText Tvalue = (EditText) findViewById(R.id.value);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Expenses").child(Gname).push();
                String refkey = myRef.getKey();
                int flagok = 1;
                name = Tname.getText().toString();
                description = Tdescription.getText().toString();
                category = Tcategory.getSelectedItem().toString();
                currency = Tcurrency.getSelectedItem().toString();
                algorithm = Talgorithm.getSelectedItem().toString();
                if(name.equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert name.", Toast.LENGTH_LONG).show();
                } else if(currency.equals("Select currency")) {
                    Toast.makeText(InsertExActivity.this, "Please insert currency.", Toast.LENGTH_LONG).show();
                } else if(Tvalue.getText().toString().equals("")) {
                    Toast.makeText(InsertExActivity.this, "Please insert value.", Toast.LENGTH_LONG).show();
                } else if(category.equals("Select category")) {
                    Toast.makeText(InsertExActivity.this, "Please insert category.", Toast.LENGTH_LONG).show();
                } else {
                    value = Double.valueOf(Tvalue.getText().toString());

                   if (algorithm.equals("equally")) {
                        v = value/users.size();
                       for(UserData k : users)
                            values.put(k.getuId(), v);
                        flagok = 1;
                    }


                else{
                    double algValue, algSum=0, meValue=0;
                    int i;
                    for(i = 0; i< uAdapter.getItemCount(); i++){
                        View view = userRecyclerView.getChildAt(i);
                        EditText EditValue = (EditText)view.findViewById(R.id.alg_value);
                        if(EditValue.getText().toString().equals("")) {
                            algValue = 0;
                            if (algorithm.equals("by percentuage"))
                                values.put(users.get(i).getuId(), algValue);
                            else
                                values.put(users.get(i).getuId(), algValue);
                        }
                        else {
                            algValue = Float.parseFloat(EditValue.getText().toString());

                            if (algorithm.equals("by percentuage"))
                                values.put(users.get(i).getuId(), value * algValue / 100);
                            else
                                values.put(users.get(i).getuId(), algValue);
                        }
                        algSum += algValue;
                    }

                    if((algorithm.equals("by percentuage") && algSum==100)) {

                        flagok = 1;
                    }

                    if((algorithm.equals("by import") && algSum==value)) {

                        flagok = 1;
                    }

                    if((algorithm.equals("by percentuage") && algSum!=100)){
                        flagok = 0;
                        String text = String.format("Percentuage sum values must be equal to 100!", algSum, i);
                        Toast.makeText(InsertExActivity.this, text, Toast.LENGTH_LONG).show();
                    }
                    if((algorithm.equals("by import") && algSum!=value)){
                        flagok = 0;
                        Toast.makeText(InsertExActivity.this, "Import sum values must be equal to the total value!", Toast.LENGTH_LONG).show();
                    }

                }

                    if (flagok == 1 && values.size() == users.size()) {
                        DatabaseReference ActRef = database.getReference("Activities").child(Gname).push();

                        ActRef.setValue(new ActivityData(myname+" "+mysurname, myname +" "+mysurname+" added a new expense in group "+ groupName, Long.toString(System.currentTimeMillis()), "expense", refkey, Gname));
                        myRef.setValue(new ExpenseData(name, description, category, currency, String.format("%.2f", value), "0.00", algorithm));
                        final DatabaseReference myRef2=myRef;
                        myRef.child("creator").setValue(myname + " " + mysurname);

                        for(Map.Entry<String, Double> e : values.entrySet())
                            myRef.child("users").child(e.getKey()).setValue(String.format("%.2f", e.getValue()));

                        myRef.child("contested").setValue("no");
                        ii = 0;
                        for(final UserData key : users){


                            myRef = database.getReference("/Users/"+key.getuId()+"/Groups/"+Gname+"/lastOperation/");
                            myRef.setValue(myname + " added an expense.");
                            myRef = database.getReference("/Users/"+key.getuId()+"/Groups/"+Gname+"/dateLastOperation/");
                            myRef.setValue(Long.toString(System.currentTimeMillis()).toString());



                            final DatabaseReference myRef3 = database.getReference("Balance").child(Gname).child(mAuth.getCurrentUser().getUid()).child(key.getuId());

                                myRef3.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                        if(!key.getuId().equals(mAuth.getCurrentUser().getUid())) {
                                            Float value = mutableData.child("value").getValue(Float.class);
                                            System.out.println("valueeeeeeeeeeeeeeeeeeeeee " + value);
                                            if (value == null) {
                                                mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                                mutableData.child("value").setValue(String.format("%.2f", values.get(key.getuId())));
                                            } else {
                                                mutableData.child("value").setValue(String.format("%.2f", value + values.get(key.getuId())));
                                                mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                            }
                                        }

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                       //Log.d(TAG, "transaction:onComplete:" + databaseError);
                                    }
                                });



                                final DatabaseReference myRef5 = database.getReference("Balance").child(Gname).child(key.getuId()).child(mAuth.getCurrentUser().getUid());
                                myRef5.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        if(!key.getuId().equals(mAuth.getCurrentUser().getUid())) {
                                            Float value = mutableData.child("value").getValue(Float.class);
                                            System.out.println("valueeeeeeeeeeeeeeeeeeeeee " + value);
                                            if (value == null) {
                                                mutableData.child("value").setValue(String.format("%.2f", -values.get(key.getuId())));
                                                mutableData.child("name").setValue(myname + " " + mysurname);
                                            } else {
                                                mutableData.child("value").setValue(String.format("%.2f", value - values.get(key.getuId())));
                                                mutableData.child("name").setValue(myname + " " + mysurname);
                                            }
                                        }
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                        // Log.d(TAG, "transaction:onComplete:" + databaseError);
                                    }
                                });

                            ii++;

                            System.out.println(String.format("balance update cycles: %d\n", ii));
                        }
                        if(ImageC==true){
                            mStorageRef = FirebaseStorage.getInstance().getReference();
                            try {
                                ProgressBar p=(ProgressBar) findViewById(R.id.progress_bar_insertex);
                                p.setVisibility(View.VISIBLE);
                                System.out.println(".......carica in"+outputFileUri.toString().substring(7));
                                InputStream stream = new FileInputStream(new File(outputFileUri.toString().substring(7)));
                                StorageReference imageStorage = mStorageRef.child(refkey);
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
                                        myRef2.child("imagePath").setValue(downloadUrl.toString());
                                        Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);
                                        System.out.println("+++++++++++++++++"+Gname + groupName);
                                        i2.putExtra("groupId", Gname);
                                        i2.putExtra("groupName", groupName);

                                        setResult(RESULT_OK, i2);
                                        finish();
                                        Log.d("myStorage", "success!");
                                    }
                                });
                            }catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);
                            System.out.println("+++++++++++++++++"+Gname + groupName);
                            i2.putExtra("groupId", Gname);
                            i2.putExtra("groupName", groupName);

                            setResult(RESULT_OK, i2);
                            finish();
                            Log.d("myStorage", "success!");
                        }
                        //return true;
                    } else {
                        String p = String.format("problems... flagok: %d, values.size: %d, users.size: %d, uAdapter: %d", flagok, values.size(), users.size(), uAdapter.getItemCount());
                        Toast.makeText(InsertExActivity.this, p, Toast.LENGTH_LONG).show();
                        return super.onOptionsItemSelected(item);
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void performCrop() {
        try {
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
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
