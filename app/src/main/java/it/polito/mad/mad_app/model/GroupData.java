package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;

public class GroupData {

    private String name;
    private String description;
    private static List<ExpensiveData> lexpensive = new ArrayList<>();
    private static List<UserData> lUsers = new ArrayList<>();

    public GroupData(String n, String d){
        this.name = n;
        this.description = d;
    }

    public String getName(){
        return this.name;
    }
    public String getDescription() { return this.description;}


    public static void addExpensive(String name, String descr, String category, String currency, float value, String algorithm){
        lexpensive.add(new ExpensiveData(name, descr, category, currency, value, algorithm));
    }

    public static  void addUser(String email){
        lUsers.add(new UserData(email));
    }

    public static List<ExpensiveData> getExpensies(){

        lexpensive.add(new ExpensiveData("Spesa 2", "spesa", "spesa", "EUR", (float)10.2, "alla romana"));
        lexpensive.add(new ExpensiveData("Spesa 2", "spesa", "spesa", "EUR", 888, "alla romana"));
        return lexpensive;
    }

    public static List<UserData> getlUsers() { return lUsers;}

}
