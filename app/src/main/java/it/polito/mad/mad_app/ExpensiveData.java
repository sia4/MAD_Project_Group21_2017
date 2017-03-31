package it.polito.mad.mad_app;

/**
 * Created by Luca on 31/03/2017.
 */

public class ExpensiveData {

    private String name;
    private String descr;
    private String category;
    private String currency;
    private int value;

    public ExpensiveData(String n, String d, String ca, String cu, int value){
        this.name = n;
        this.descr = d;
        this.category = ca;
        this.currency = cu;
        this.value = value;
    }
    public String getName(){
        return this.name;
    }
    public String getDescription(){
        return this.descr;
    }
    public String getCategory(){
        return this.category;
    }
    public String getCurrency(){
        return this.currency;
    }
    public int getValue(){
        return this.value;

    }
}
