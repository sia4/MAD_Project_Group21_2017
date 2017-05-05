package it.polito.mad.mad_app.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Luca on 05/04/2017.
 */

public class ActivityData {

    private String creator;
    private String text;
    private String date;
    private String type;

    public ActivityData(String creator1, String text1, String date1, String type1) {
        this.creator = creator1;
        this.text = text1;
        this.date = date1;
        this.type = type1;

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

}
