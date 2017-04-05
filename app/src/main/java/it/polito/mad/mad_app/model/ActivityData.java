package it.polito.mad.mad_app.model;

/**
 * Created by Luca on 05/04/2017.
 */

public class ActivityData {

    private String email;
    private String text;
    private String date;

    public ActivityData(String email, String text, String date) {
        this.email = email;
        this.text = text;
        this.date = date;
    }

    public String getEmail(){
        return this.email;
    }
    public String getText(){
        return this.text;
    }
    public String getDate(){
        return this.date;
    }

}
