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

        /*
        lexpensive.add(new ExpensiveData("luca", "sushi", "cene", "EUR", (float)12.3));
        lexpensive.add(new ExpensiveData("lucia", "pasta", "spesa", "EUR", (float)1.1));
        lexpensive.add(new ExpensiveData("edo", "winnie the pooh", "cinema", "EUR", (float)7));
        lexpensive.add(new ExpensiveData("sia", "pane", "spesa", "EUR", (float)44));
        */
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
        return lexpensive;
    }
    public static List<UserData> getlUsers() { return lUsers;}

}
