package it.polito.mad.mad_app.model;

public class ExpensiveData {

    private String name;
    private String descr;
    private String category;
    private String currency;
    private float value;

    public ExpensiveData(String n, String d, String ca, String cu, float value){
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

    public float getValue(){
        return this.value;
    }
}
