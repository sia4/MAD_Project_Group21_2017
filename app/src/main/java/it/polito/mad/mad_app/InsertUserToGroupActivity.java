package it.polito.mad.mad_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.mad_app.model.Invite;
import it.polito.mad.mad_app.model.User;

/**
 * Created by Silvia Vitali on 27/04/2017.
 */

public class InsertUserToGroupActivity extends AppCompatActivity {

    private static final int REQUEST_INVITE = 0;
    public String gId = null;
    public User ud;
    public String key;
    public String gName;
    public String gPath;
    public String email;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user_to_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.insert_user_to_group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent i = getIntent();
        gId = i.getStringExtra("groupId");
        gName = i.getStringExtra("groupName");
        gPath = i.getStringExtra("groupPath");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add user");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case android.R.id.home: //farlo tornare a GroupInfoActivity
                setResult(RESULT_OK, null);
                finish();
                return true;

            case R.id.action_menu_done:
                final EditText uEmail = (EditText) findViewById(R.id.uEmail);

                if(uEmail.getText().toString().equals("")) {
                    Toast.makeText(InsertUserToGroupActivity.this, "Please insert an email!", Toast.LENGTH_LONG).show();
                } else {
                    email = uEmail.getText().toString();
                    final DatabaseReference mTest = FirebaseDatabase.getInstance().getReference();
                    final Query quer=mTest.child("Users").orderByChild("email");
                    quer.equalTo(uEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                ud = userSnapshot.getValue(User.class);
                                key=userSnapshot.getKey(); //ritorna la chive dell'utente che quindi
                                // poi va inserito nell'oggetto gruppo come chiave:true
                                Toast.makeText(InsertUserToGroupActivity.this, key, Toast.LENGTH_LONG).show();
                            }
                            if(key == null) {

                            new AlertDialog.Builder(InsertUserToGroupActivity.this)
                                    .setTitle("You friend has not downloaded the app, yet!")
                                    .setMessage("Do you want to invite him to use the app?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            onInviteClicked(email);
                                            //onInviteClicked("nome", "cognome", "groupname", "identificativo");
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();

                            } else {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/name/");
                                myRef.setValue(gName);
                                myRef = database.getReference("/Users/"+key+"/Groups/"+gId+"/imagePath/");
                                myRef.setValue(gPath);
                                myRef = database.getReference("/Groups/"+gId+"/members/"+key);
                                myRef.setValue(true);

                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError eError) {

                        }
                    });
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("/Invites");
                String inviteId = myRef.push().getKey();
                //myRef.setValue(gPath);
                Invite invite = new Invite(email, gId, gName, gPath);
                myRef.child(inviteId).setValue(invite);

                finish();
            } else {

                System.out.println("Errore..." + resultCode);
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }

    }

    private void onInviteClicked(String email) {

        Intent intent = new AppInviteInvitation.IntentBuilder("Invite your friends!")
                .setMessage("You have been invited to AllaRomana (mail: "+ email+")")
                .setEmailHtmlContent("Hi! I invited you to join a group on AllaRomana. Download the app and SignIn with the email "+ email +" to join the group. See you on AllaRomana!")
                .setDeepLink(Uri.EMPTY)
                .setEmailSubject("Invite you on AllaRomana")
                .build();


        startActivityForResult(intent, REQUEST_INVITE);
    }



}
