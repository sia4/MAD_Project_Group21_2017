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
    private String itemValue;
    private String groupName;
    private Boolean read = false;
    private String activityId;
    private int imgId;
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

    public ActivityData(String creator1, String text1, String date1, String type1, String id, String gId, Integer imgId) {
        this.creator = creator1;
        this.text = text1;
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        //Date resultdate = new Date(new Long(dateLastOperation));
        this.date = date1;
        this.type = type1;
        this.itemId = id;
        this.groupId = gId;
        this.imgId = imgId;
    }
    public ActivityData(String creator1, String text1, String date1, String type1, String id, String gId,boolean read) {
        this.creator = creator1;
        this.text = text1;
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        //Date resultdate = new Date(new Long(dateLastOperation));
        this.date = date1;
        this.type = type1;
        this.itemId = id;
        this.groupId = gId;
        this.read=read;
    }
    public ActivityData(String creator1, String text1, String date1, String type1, String id, String gId, String itemValue) {
        this.creator = creator1;
        this.text = text1;
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        //Date resultdate = new Date(new Long(dateLastOperation));
        this.date = date1;
        this.type = type1;
        this.itemId = id;
        this.groupId = gId;
        this.itemValue=itemValue;
    }
    public ActivityData(String creator1, String text1, String date1, String type1, String id, String gId, Integer imgId, String itemValue) {
        this.creator = creator1;
        this.text = text1;
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        //Date resultdate = new Date(new Long(dateLastOperation));
        this.date = date1;
        this.type = type1;
        this.itemId = id;
        this.groupId = gId;
        this.imgId = imgId;
        this.itemValue=itemValue;
    }
    public void setItemValue(String value){this.itemValue=itemValue;}
    public String getItemValue(){return this.itemValue;}
    public void setActivityId(String s){this.activityId = s;}
    public String getActivityId(){return this.activityId;}
    public void setRead(boolean s){read = s;}
    public boolean getRead(){return read;}
    public String getCreator(){
        return this.creator;
    }
    public String getText(){
        return this.text;
    }
    public String getDate(){return this.date;}
    public String getType(){ return this.type;}
    public int getImgId(){return this.imgId;}
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
