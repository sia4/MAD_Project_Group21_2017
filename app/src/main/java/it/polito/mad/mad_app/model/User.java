package it.polito.mad.mad_app.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Siltes on 22/04/17.
 */

@IgnoreExtraProperties
public class User {

    private String name;
    private String surname;
    private String email;
    private String imagePath;
    private String username; // da vedere se utilizzarlo...
    private Map<String, Map<String, String>> Groups = new TreeMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String name, String surname) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        //this.imagePath=imagePath;
        //this.Groups.put("", true);
    }
    public User(String username, String email, String name, String surname,String imagePath) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.imagePath=imagePath;
        //this.Groups.put("", true);
    }
    public String getSurname(){
        return this.surname;
    }
    public String getName(){
        return this.name;
    }
    public String getUsername(){
        return this.username;
    }
    public String getEmail(){
        return this.email;
    }
    public String getImagePath(){
        return this.imagePath;
    }

    public Map<String, Map<String, String>> getGroups(){
        return this.Groups;
    }

}
