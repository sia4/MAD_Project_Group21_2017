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
    private float value;


    public UserData(String email){
        this.email = email;
    }
    public UserData(String email, String name, String surname, int phonenumber) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phonenumber = phonenumber;
    }

    public void setName(String n){ this.name = n;}
    public void setSurname(String n){ this.surname = n;}
    public void setUsername(String n){ this.username = n;}
    public void setPhonenumber(int pn){ this.phonenumber = pn;}
    public String getName(){ return this.name;}
    public String getSurname(){ return this.surname;}
    public String getUsername(){ return this.username;}
    public int getPhonenumber(){ return this.phonenumber;}
    public float getValue(){return this.value;}
    public void addValue(float value){this.value += value;}
}
