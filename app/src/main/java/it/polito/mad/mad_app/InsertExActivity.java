package it.polito.mad.mad_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Balance;
import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.UserData;

import static android.util.LayoutDirection.LOCALE;

import static it.polito.mad.mad_app.model.ImageMethod.create_image;
import static it.polito.mad.mad_app.model.ImageMethod.performCrop;
import static it.polito.mad.mad_app.model.ImageMethod.require_image;



public class InsertExActivity extends AppCompatActivity {


    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    private String name;
    private String description;
    private String category;
    private String currency;
    private double value, myvalue=0;
    private Boolean fish=false;
    private String algorithm;
    private String myname, mysurname;
    private String Gname = new String(), groupName = new String();
    private StorageReference mStorageRef;
    private Map<String,Balance>users_l=new TreeMap<>();
    private int i=0, ii=0;
    private Map<String, Map<String, Map<String, Object>>> balancemap;
    private double v = 0, v1 = 0;
    private Map<String, Double> values = new TreeMap<>();
    private String defaultcurrency;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<UserData> users;
    private Map<String,UserData>m_users=new TreeMap<>();
    static private RecyclerView userRecyclerView;
    static private AlgorithmParametersAdapter uAdapter;
    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri outputFileUri;
    private String tmp;
    String mCurrentPhotoPath;
    private Map<String, Float> Cambi = new TreeMap<>();
    private Uri downloadUrl;
    private String groupImage;
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
        groupImage=intent.getStringExtra("imagePath");

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
                                    m_users.put(k,u);
                                    users=new ArrayList<>(m_users.values());
                                    System.out.println("lista utenti: "+users);
                                    System.out.println("lista utenti: "+users.size());
                                    System.out.println("+++++++utente trovato"+k);
                                    if(k.equals(mAuth.getCurrentUser().getUid())){
                                        myname = (String)map3.get("name");
                                        mysurname = (String)map3.get("surname");
                                    }
                                    //uAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        i++;

                    }
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

        final List<String> Currencies = new ArrayList<>();
        Currencies.add("Select currency");
        //Currencies.add("EUR €");

        //Currencies.add(defaultcurrency);
        // Create an ArrayAdapter using the string array and a default spinner layout
        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef4 = database4.getReference("Groups").child(Gname);
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                it.polito.mad.mad_app.model.Group g = dataSnapshot.getValue(it.polito.mad.mad_app.model.Group.class);

                if(map!=null) {
                  
                  defaultcurrency = (String)map.get("defaultcurrency");
                  
                } else {
                    Toast.makeText(InsertExActivity.this, "no user key found!", Toast.LENGTH_LONG).show();
                }

                if (g != null) {
                    Currencies.addAll(g.getCurrencies().keySet());
                    Cambi.putAll(g.getCurrencies());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        System.out.println("3");

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    }

                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    return;
                }
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
                if (isCamera) {
                    imageUrl = outputFileUri;
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
                            startActivityForResult(cropIntent , 2);
                        }
                        else{
                            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }

                }
            }else if(requestCode==CROP_PIC){
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.ImageG);
                picView.setImageBitmap(thePic);
                create_image(outputFileUri,thePic);
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
                } else {

                }
                return;
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

                    System.out.println("valuebuggggggggggggggggggg " + value);
                    if (algorithm.equals("equally")) {
                        v = value / users.size();
                        for (UserData k : users) {
                            values.put(k.getuId(), v);
                        }
                        myvalue = v;
                        flagok = 1;
                    } else {
                        double algValue, algSum = 0, meValue = 0;
                        int i;
                        for (i = 0; i < uAdapter.getItemCount(); i++) {
                            View view = userRecyclerView.getChildAt(i);
                            EditText EditValue = (EditText) view.findViewById(R.id.alg_value);
                            if (EditValue.getText().toString().equals("")) {
                                algValue = 0;
                                myvalue = 0;
                                if (algorithm.equals("by percentuage"))
                                    values.put(users.get(i).getuId(), algValue);
                                else
                                    values.put(users.get(i).getuId(), algValue);
                            } else {
                                algValue = Float.parseFloat(EditValue.getText().toString());

                                if (algorithm.equals("by percentuage")) {
                                    values.put(users.get(i).getuId(), value * algValue / 100);
                                    if (users.get(i).getuId().equals(mAuth.getCurrentUser().getUid()))
                                        myvalue = value * algValue / 100;
                                } else {
                                    values.put(users.get(i).getuId(), algValue);
                                    if (users.get(i).getuId().equals(mAuth.getCurrentUser().getUid()))
                                        myvalue = value;
                                }
                            }
                            algSum += algValue;
                        }

                        if ((algorithm.equals("by percentuage") && algSum == 100)) {

                            flagok = 1;
                        }

                        if ((algorithm.equals("by import") && algSum == value)) {

                            flagok = 1;
                        }

                        if ((algorithm.equals("by percentuage") && algSum != 100)) {
                            flagok = 0;
                            String text = String.format("Percentuage sum values must be equal to 100!", algSum, i);
                            Toast.makeText(InsertExActivity.this, text, Toast.LENGTH_LONG).show();
                        }
                        if ((algorithm.equals("by import") && algSum != value)) {
                            flagok = 0;
                            Toast.makeText(InsertExActivity.this, "Import sum values must be equal to the total value!", Toast.LENGTH_LONG).show();
                        }

                    }

                    if (flagok == 1 && values.size() == users.size()) {
                        //Quì inseriamo la nuova ttività relativa all'inserimento della spesa
                        DatabaseReference ActRef = database.getReference("Activities").child(Gname).push();
                        ActRef.setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " added a new expense in group " + groupName, Long.toString(System.currentTimeMillis()), "expense", refkey, Gname));
                        //Quì inseriamo una nuova spesa con relative immagine, se è stata caricata
                        myRef.setValue(new ExpenseData(name, description, category, currency, String.format(Locale.US, "%.2f", value), "0.00", algorithm));
                        final DatabaseReference myRef2 = myRef;
                        myRef.child("creator").setValue(myname + " " + mysurname);
                        myRef.child("creatorId").setValue(mAuth.getCurrentUser().getUid());
                        System.out.println(String.format("%.2f", value));
                        for (Map.Entry<String, Double> e : values.entrySet())
                            myRef.child("users").child(e.getKey()).setValue(String.format(Locale.US, "%.2f", e.getValue()));
                        myRef.child("contested").setValue("no");
                        ii = 0;

                        final float cambio = Cambi.get(currency);
                        for(final UserData k:users){
                            myRef = database.getReference("/Users/" + k.getuId() + "/Groups/" + Gname + "/lastOperation/");
                            myRef.setValue(myname + " added an expense.");
                            myRef = database.getReference("/Users/" + k.getuId() + "/Groups/" + Gname + "/dateLastOperation/");
                            myRef.setValue(Long.toString(System.currentTimeMillis()).toString());
                        }

                        final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Balance").child(Gname);
                        System.out.println("Path "+myRef3);

                        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                balancemap = (Map<String, Map<String, Map<String, Object>>>) dataSnapshot.getValue();
                                if(balancemap!=null) {

                                        for (UserData u : users) {
                                            final FirebaseDatabase database5 = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef5 = database5.getReference("Balance").child(Gname);
                                            float value1, value2, value3, value4;
                                            if (!u.getuId().equals(mAuth.getCurrentUser().getUid()) && balancemap != null) {
                                                value1 = Float.parseFloat((String) balancemap.get(mAuth.getCurrentUser().getUid()).get(u.getuId()).get("value"));
                                                value2 = Float.parseFloat((String) balancemap.get(u.getuId()).get(mAuth.getCurrentUser().getUid()).get("value"));
                                                System.out.println("buttonnnnnnn1 " + value1);
                                                System.out.println("buttonnnnnnn2 " + value2);

                                                value3 = values.get(u.getuId()).floatValue();
                                                value1 = value1 + value3;
                                                value2 = value2 - value3;
                                                myRef5.child(mAuth.getCurrentUser().getUid()).child(u.getuId()).child("value").setValue(String.format(Locale.US, "%.2f", value1));
                                                myRef5.child(u.getuId()).child(mAuth.getCurrentUser().getUid()).child("value").setValue(String.format(Locale.US, "%.2f", value2));

                                        if(!key.getuId().equals(mAuth.getCurrentUser().getUid())) {
                                            Float value = mutableData.child("value").getValue(Float.class);
																					
                                            System.out.println("1+++valueeeeeeeeeeeeeeeeeeeeee " + value);
                                            if (cambio != 0) {
                                                if (value == null) {
                                                    mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                                    mutableData.child("value").setValue(String.valueOf(values.get(key.getuId()) * cambio));
                                                } else {
                                                    mutableData.child("value").setValue(String.valueOf((value + values.get(key.getuId())) * cambio));
                                                    mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                                }
                                            } else {
                                                if (value == null) {
                                                    mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                                    mutableData.child("value").setValue(String.valueOf(values.get(key.getuId())));
                                                } else {
                                                    mutableData.child("value").setValue(String.valueOf(value + values.get(key.getuId())));
                                                    mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                                }
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
																					System.out.println("+++valueeeeeeeeeeeeeeeeeeeeee " + value);
                                            if (cambio != 0) {
                                                if (value == null) {
                                                    mutableData.child("value").setValue(String.valueOf(-values.get(key.getuId()) * cambio));
                                                    mutableData.child("name").setValue(myname + " " + mysurname);
                                                } else {
                                                    mutableData.child("value").setValue(String.valueOf((value - values.get(key.getuId())) * cambio));
                                                    mutableData.child("name").setValue(myname + " " + mysurname);
                                                }
                                            } else {
                                                if (value == null) {
                                                    mutableData.child("value").setValue(String.valueOf(-(values.get(key.getuId()))));
                                                    mutableData.child("name").setValue(myname + " " + mysurname);
                                                } else {
                                                    mutableData.child("value").setValue(String.valueOf(value - values.get(key.getuId())));
                                                    mutableData.child("name").setValue(myname + " " + mysurname);
                                                }
                                            }


                                        }
                                    System.out.println("balancemapppppppppppppppp " + balancemap);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if(ImageC==true) {
                            mStorageRef = FirebaseStorage.getInstance().getReference();
                            try {
                                ProgressBar p = (ProgressBar) findViewById(R.id.progress_bar_insertex);
                                p.setVisibility(View.VISIBLE);
                                System.out.println(".......carica in" + outputFileUri.toString().substring(7));
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
                                        System.out.println("+++++++++++++++++" + Gname + groupName);
                                        i2.putExtra("groupId", Gname);
                                        i2.putExtra("groupName", groupName);

                                        setResult(RESULT_OK, i2);
                                        finish();
                                        Log.d("myStorage", "success!");
                                    }
                                });
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                            //final DatabaseReference myRef3 = database.getReference("Balance").child(Gname);
                                /*
                                myRef3.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                        if(!key.getuId().equals(mAuth.getCurrentUser().getUid())) {
                                            Float value = Float.parseFloat((String)mutableData.child(mAuth.getCurrentUser().getUid()).child(key.getuId()).child("value").getValue());
                                            System.out.println("value prima " + value);

                                            System.out.println(String.valueOf(values.get(key.getuId())));
                                            if (value == null) {
                                                //mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                                mutableData.child(mAuth.getCurrentUser().getUid()).child(key.getuId()).child("value").setValue(String.valueOf(values.get(key.getuId())));
                                                mutableData.child(key.getuId()).child(mAuth.getCurrentUser().getUid()).child("value").setValue(String.valueOf(-values.get(key.getuId())));

                                            } else {

                                                mutableData.child(mAuth.getCurrentUser().getUid()).child(key.getuId()).child("value").setValue(String.valueOf(value + values.get(key.getuId())));
                                                mutableData.child(key.getuId()).child(mAuth.getCurrentUser().getUid()).child("value").setValue(String.valueOf(value - values.get(key.getuId())));

                                                // mutableData.child("name").setValue(key.getName() + " " + key.getSurname());
                                            }
                                        }

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                       //Log.d(TAG, "transaction:onComplete:" + databaseError);
                                    }
                                });*/
                            /*

                            ii++;

                            System.out.println(String.format("balance update cycles: %d\n", ii));
                        }
                        }
                        else{*/
                            Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);
                            System.out.println("+++++++++++++++++" + Gname + groupName);
                            i2.putExtra("groupId", Gname);
                            i2.putExtra("groupName", groupName);

                            setResult(RESULT_OK, i2);
                            finish();
                            Log.d("myStorage", "success!");
                            //}
                            //return true;
                        } else{
                            String p = String.format("problems... flagok: %d, values.size: %d, users.size: %d", flagok, values.size(), users.size());
                            Toast.makeText(InsertExActivity.this, p, Toast.LENGTH_LONG).show();
                            return super.onOptionsItemSelected(item);
                        }
                    }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
