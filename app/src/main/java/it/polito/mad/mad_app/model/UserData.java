package it.polito.mad.mad_app.model;

import java.util.List;

/**
 * Created by Luca on 02/04/2017.
 */

public class UserData {

    private List<String> Groups;
    private String name;
    private String surname;
    private String username;
    private String Email;
    private int phonenumber;

    public UserData(String email){
        this.Email = email;
    }
    public UserData(){}
    public UserData(String email, String name, String surname, int phonenumber) {
        this.Email = email;
        this.name = name;
        this.surname = surname;
        this.phonenumber = phonenumber;
    }
    public String getEmail(){return this.Email;}
    public void setName(String n){ this.name = n;}
    public void setSurname(String n){ this.surname = n;}
    public void setUsername(String n){ this.username = n;}
    public void setPhonenumber(int pn){ this.phonenumber = pn;}
    public String getName(){ return this.name;}
    public String getSurname(){ return this.surname;}
    public String getUsername(){ return this.username;}
    public int getPhonenumber(){ return this.phonenumber;}

}
