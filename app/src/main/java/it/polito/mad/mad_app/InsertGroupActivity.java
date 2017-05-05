package it.polito.mad.mad_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    private static final int REQUEST_INVITE = 0;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String GroupName;
    private String GroupDescription;
    private String UserEmail;
    private User ud;
    private String key;
    private List<String> u= new ArrayList<>();
    private Map<String,String>my= new TreeMap<>();//TODO da cambiare
    private Map<String,Boolean>m= new TreeMap<>();
    private UsersToAddAdapter uAdapter = null;
    String uKey = null;
    String uName = null;
    private List<String> userNotPresentInDb = new ArrayList<String>();
    private String userNotPresentInDbMail;
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

        getSupportActionBar().setTitle("New Group");
        final EditText Uemail = (EditText) findViewById(R.id.User1);
        Button userbutton = (Button) findViewById(R.id.Adduser1);
        final RecyclerView userRecyclerView = (RecyclerView) findViewById(R.id.usersToAdd);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(InsertGroupActivity.this,
                android.support.v7.widget.DividerItemDecoration.VERTICAL));
        uAdapter = new UsersToAddAdapter(u);
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

                } else {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Groups");
                    String groupId = myRef.push().getKey();
                    Group G = new Group(GroupName, GroupDescription, Tcurrency.getSelectedItem().toString());
                    G.setImagePath("https://firebasestorage.googleapis.com/v0/b/allaromana-3f98e.appspot.com/o/group_default.png?alt=media&token=40bc93f4-6b97-466e-b130-e140f57c5895");
                    G.addMembers(m);
                    G.addMember(uKey);
                    my.put(uKey, uName);
                    myRef.child(groupId).setValue(G);
                    Set keys = m.keySet();
                    Set others = m.keySet();
                    database = FirebaseDatabase.getInstance();
                    for (Iterator i = keys.iterator(); i.hasNext(); ) {
                        String key = (String) i.next();
                        myRef = database.getReference("/Users/"+key+"/Groups/"+groupId+"/name/");
                        myRef.setValue(G.getName());
                        myRef = database.getReference("/Users/"+key+"/Groups/"+groupId+"/imagePath/");
                        myRef.setValue(G.getImagePath());
                        myRef = database.getReference("/Users/"+key+"/Groups/"+groupId+"/lastOperation/");
                        myRef.setValue(uName + " has created the group.");
                        myRef = database.getReference("/Users/"+key+"/Groups/"+groupId+"/dateLastOperation/");
                        myRef.setValue(Long.toString(System.currentTimeMillis()).toString());

                        for(Iterator n = others.iterator();n.hasNext();){
                            String k = (String) n.next();
                            if(k!=key) {
                                myRef = database.getReference("/Balance/" + groupId+"/" + key +"/" + k+"/"+"name");
                                myRef.setValue(my.get(k));
                                myRef = database.getReference("/Balance/" + groupId+"/" + key +"/" + k+"/"+"value");
                                myRef.setValue(0.00);
                            }
                        }
                    }

                    /*for(String s : userNotPresentInDb) {;
                        DatabaseReference myRef2 = database.getReference("/Invites");
                        String inviteId = myRef2.push().getKey();
                        //myRef.setValue(gPath);
                        Invite invite = new Invite(s, groupId, G.getName(), G.getImagePath());
                        myRef.child(inviteId).setValue(invite);
                    }*/
                    setResult(RESULT_OK, null);
                    finish();
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
}
