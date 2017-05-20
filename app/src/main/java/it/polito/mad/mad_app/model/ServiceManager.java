package it.polito.mad.mad_app.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import it.polito.mad.mad_app.MainActivity;
import it.polito.mad.mad_app.R;

import static android.R.attr.level;

/**
 * Created by Lucia on 18/05/2017.
 */

public class ServiceManager extends Service {
    private int i=0;
    private String gname;
    private String exname;
    private String printText;
    private Map<String, Map<String,Object>> not_read=new TreeMap<>();
    private Map<String, Map<String,Object>> activities=new TreeMap<>();
    @Override
    public void onCreate() {
        final Context c = this;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String key = currentFirebaseUser.getUid();
        if (key != null) {
        DatabaseReference db = database.getReference().child("Activities").child(key);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (i == 0) {
                    i += 1;
                } else {
                    activities = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                    Set<String> activId = activities.keySet();
                    for (String str : activId) {
                        String read = (String) activities.get(str).get("read");
                        if ( read ==null) {
                            not_read.put(str, activities.get(str));
                        }
                    }
                    if ( not_read.size()>1) {
                        printText=not_read.size()+" new notifications";
                    }
                    else{
                        Set<String> k_act=not_read.keySet();
                        final String print = (String) not_read.get(k_act).get("type");
                        String gId = (String) not_read.get(k_act).get("groupId");
                        DatabaseReference forName = database.getReference().child("Groups").child(gId).child("name");
                        forName.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getValue();
                            gname = (String) dataSnapshot.getValue();
                            switch (print) {
                                case ("expense"):
                                    printText = "In " + gname + " there's a new expense";
                                    break;
                                case ("contest"):
                                    printText = "In " + gname + " an expense has been contested";
                                    break;
                                case ("leavegroup"):
                                    printText = "Someaone leave the group" + gname;
                                    break;
                                case ("deletegroup"):
                                    printText = "Propose to leave group" + gname;
                                    break;
                                case ("addgroup"):
                                    printText = "You have a new expense";
                                    break;
                                default:
                                    printText = "You have a new notification";
                                    break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(c)
                                    .setContentTitle("AllaRomana")
                                    .setContentText(printText)
                                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_stat_ac_unit))
                                    .setSmallIcon(R.drawable.logo_white) //TODO qui è sostanzialmento dove specifico l'icona da impostare.
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setAutoCancel(true)
                                    .setPriority(Notification.PRIORITY_MAX);
                    Intent resultIntent = new Intent(c, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Insert Group Activity", "Failed to read value.", error.toException());
            }
        });
    }
        // code to execute when the service is first created
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}
