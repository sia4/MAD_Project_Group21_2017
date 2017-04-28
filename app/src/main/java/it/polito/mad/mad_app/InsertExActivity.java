package it.polito.mad.mad_app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;


public class InsertExActivity extends AppCompatActivity {


    private String name;
    private String description;
    private String category;
    private String currency;
    private float value;
    private String algorithm;
    private String myname, mysurname;
    private String Gname = new String(), groupName = new String();
    private int i=0, ii=0;
    private float v = 0, v1 = 0;
    private List<Float> values = new ArrayList<>();
    private String defaultcurrency;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<UserData> users = new ArrayList<>();
    static private RecyclerView userRecyclerView;
    static private AlgorithmParametersAdapter uAdapter;
    static final int REQUEST_TAKE_PHOTO = 1;
    private boolean flag_name_edited = false, flag_desc_edited = false, flag_img_edited = false;
    private String tmp;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ex);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.insert_ex_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("New Expense");
        ImageButton bill = (ImageButton)findViewById(R.id.AddPhotoBill);





        bill.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                dispatchTakePictureIntent();
                //galleryAddPic();
                }

        } );

        userRecyclerView = (RecyclerView) findViewById(R.id.algorithmParameters);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertExActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        Gname = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");
        Toast.makeText(InsertExActivity.this, Gname+" "+groupName, Toast.LENGTH_LONG).show();
        System.out.println("1");
        final String uid = mAuth.getCurrentUser().getUid().toString();
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference("Groups").child(Gname).child("members");

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                map2.put(uid, uid); //aggiungo user corrente
                if(map2!=null) {
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
        final EditText Tvalue = (EditText) findViewById(R.id.value);
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
                        uAdapter = new AlgorithmParametersAdapter(users, position, Float.parseFloat(Tvalue.getText().toString()), algInfo, algInfoSmall);
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

        String s = "";

    }
    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            c.moveToFirst();
            int cIndex = c.getColumnIndex(filePathColumn[0]);
            String picturePath = c.getString(cIndex);
            c.close();
            ImageButton bill = (ImageButton) findViewById(R.id.AddPhotoBill);
            bill.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            flag_img_edited = true;
            tmp = picturePath;
            //Toast.makeText(this, "path:"+ tmp, Toast.LENGTH_LONG).show();

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
                    value = Float.parseFloat(Tvalue.getText().toString());

                   if (algorithm.equals("equally")) {
                        v = value/users.size();
                       for(UserData k : users)
                            values.add(v);
                        flagok = 1;
                    }


                else{
                    float algValue, algSum=0, meValue=0;
                    int i;
                    for(i = 0; i< uAdapter.getItemCount(); i++){
                        View view = userRecyclerView.getChildAt(i);
                        EditText EditValue = (EditText)view.findViewById(R.id.alg_value);
                        if(EditValue.getText().toString().equals(""))
                            algValue = 0;
                        else
                            algValue = Float.parseFloat(EditValue.getText().toString());
                        if(i==uAdapter.getItemCount()-1)
                            meValue = algValue;
                        else {
                            if (algorithm.equals("by percentuage"))


                                values.add(value*algValue/100);

                            else
                                values.add(algValue);
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
                        myRef.setValue(new ExpenseData(name, description, category, currency, value, 0, algorithm));

                        ii = 0;

                        for(final UserData key : users){

                            if(!key.getuId().equals(mAuth.getCurrentUser().getUid())) {

                                final DatabaseReference myRef3 = database.getReference("Balance").child(Gname).child(mAuth.getCurrentUser().getUid()).child(key.getuId());

                                myRef3.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        Float value = mutableData.child("value").getValue(Float.class);
                                        System.out.println("valueeeeeeeeeeeeeeeeeeeeee "+ values.get(ii));
                                        if (value == null) {
                                            mutableData.child("name").setValue(key.getName()+" "+key.getSurname());
                                            mutableData.child("value").setValue(6);
                                        }
                                        else {
                                            mutableData.child("value").setValue(value + values.get(ii));
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
                                        Float value = mutableData.child("value").getValue(Float.class);
                                        System.out.println("valueeeeeeeeeeeeeeeeeeeeee "+ values.get(ii));
                                        if (value == null) {
                                            mutableData.child("value").setValue(6);
                                            mutableData.child("name").setValue(key.getName()+" "+key.getSurname());
                                        }
                                        else {
                                            mutableData.child("value").setValue(value - values.get(ii));
                                        }

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                        // Log.d(TAG, "transaction:onComplete:" + databaseError);
                                    }
                                });
                            }

                            ii++;

                            System.out.println(String.format("balance update cycles: %d\n", ii));
                        }



                        Intent i2 = new Intent(InsertExActivity.this, GroupActivity.class);
                        System.out.println("+++++++++++++++++"+Gname + groupName);
                        i2.putExtra("groupId", Gname);
                        i2.putExtra("groupName", groupName);

                        setResult(RESULT_OK, i2);
                        finish();

                        return true;
                    } else {
                        Toast.makeText(InsertExActivity.this, "problems...", Toast.LENGTH_LONG).show();
                        return super.onOptionsItemSelected(item);
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
