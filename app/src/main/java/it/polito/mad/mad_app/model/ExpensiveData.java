package it.polito.mad.mad_app.model;

public class ExpensiveData {

    private String name;
    private String descr;
    private String category;
    private String currency;
    private float value;
    private float myvalue;
    private String algorithm;

    public ExpensiveData(String n, String d, String ca, String cu, float value, float myvalue, String algorithm){
        this.name = n;
        this.descr = d;
        this.category = ca;
        this.currency = cu;
        this.value = value;
        this.myvalue = myvalue;
        this.algorithm = algorithm;
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
    public float getMyvalue(){return this.myvalue;}
    public String getAlgorithm(){ return this.algorithm;}
}
