package it.polito.mad.mad_app.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Luca on 05/04/2017.
 */

public class ActivityData implements Comparable<ActivityData>{

    private String creator;
    private String text;
    private String date;
    private String type;
    private String itemId;
    private String groupId;
    private String groupName;
    public ActivityData(String creator1, String text1, String date1, String type1, String id, String gId) {
        this.creator = creator1;
        this.text = text1;
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        //Date resultdate = new Date(new Long(dateLastOperation));
        this.date = date1;
        this.type = type1;
        this.itemId = id;
        this.groupId = gId;
    }

    public String getCreator(){
        return this.creator;
    }
    public String getText(){
        return this.text;
    }
    public String getDate(){
        return this.date;
    }
    public String getType(){ return this.type; }
    public String getItemId(){return this.itemId;}
    public String getGroupId(){return  this.groupId;}
    public String getGroupName(){return  this.groupName;}
    public void setGroupName(String s){this.groupName=s;}
    public void setItemId(String id){this.itemId = id;}

    @Override
    public int compareTo(@NonNull ActivityData a) {

        long o1, o2;

        o1 = Long.valueOf(date);

        o2 = Long.valueOf(a.getDate());


        return (int)(o2 - o1);
    }
}
