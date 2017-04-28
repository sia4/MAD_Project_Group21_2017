package it.polito.mad.mad_app.model;

import android.provider.Settings;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lucia on 26/04/2017.
 */

public class Balance {
    private String key;
    private String name;
    private float value;
    private String gID;
    public Balance() { }

    // TODO: creare classe currency e passare un oggetto currency da cui prendo una stringa identificativa
    public Balance(String n, String d, float v ,String g) {
        this.name = d;
        this.key = n;
        this.value=v;
        this.gID=g;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey( String s) {  this.key = s; }

    public void setValue(float p) {
        this.value-=p;
        System.out.println("..Balance"+value);
    }

    public float getValue(){return this.value;}

    public String getgID(){return this.gID;}

    public void setgID(String g) {
        this.gID = g;
    }
}
