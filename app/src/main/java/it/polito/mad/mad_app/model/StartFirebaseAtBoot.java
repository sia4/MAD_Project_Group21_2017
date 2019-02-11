package it.polito.mad.mad_app.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lucia on 18/05/2017.
 */

public class StartFirebaseAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, ServiceManager.class);
        i.putExtra("class","broadcast");
        context.startService(i);
        //context.startService(new Intent(context, ServiceManager.class));
    }
}
