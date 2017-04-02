package it.polito.mad.mad_app.model;

/**
 * Created by Luca on 02/04/2017.
 */

public class UserData {

    private String name;
    private String surname;
    private String username;
    private String email;
    private int phonenumber;


    public UserData(String email){
        this.email = email;
    }

    public void setName(String n){ this.name = n;}
    public void setSurname(String n){ this.surname = n;}
    public void setUsername(String n){ this.username = n;}
    public void setPhonenumber(int pn){ this.phonenumber = pn;}
    public String getName(){ return this.name;}
    public String getSurname(){ return this.surname;}
    public String getUsername(){ return this.username;}
    public int getPhonenumber(){ return this.phonenumber;}
}
