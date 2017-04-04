package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;

public class GroupData {

    private String name;
    private String description;
    private List<ExpensiveData> lexpensive = new ArrayList<>();
    private List<UserData> lUsers = new ArrayList<>();



    public GroupData(String n, String d){
        this.name = n;
        this.description = d;
    }

    public String getName(){
        return this.name;
    }
    public String getDescription() { return this.description;}


    public  void addExpensive(String name, String descr, String category, String currency, float value, String algorithm){
        this.lexpensive.add(new ExpensiveData(name, descr, category, currency, value, algorithm));
    }

    public void addUser(UserData user){
        lUsers.add(user);
    }

    public List<ExpensiveData> getExpensies(){

        return lexpensive;
    }

    public List<UserData> getlUsers() { return lUsers;}

}
