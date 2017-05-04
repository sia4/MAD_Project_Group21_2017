package it.polito.mad.mad_app.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExpenseData {
    private String idEx;
    private String name;
    private String descr;
    private String category;
    private String currency;
    private float value;
    private float myvalue;
    private String algorithm;
    private String date;
    private String creator;
    private String contested;

    public ExpenseData(String n, String d, String ca, String cu, float value,float myvalue, String algorithm){
        this.name = n;
        this.descr = d;
        this.category = ca;
        this.currency = cu;
        this.value = value;
        this.myvalue = myvalue;
        this.algorithm = algorithm;
        DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
        this.date = df.format(Calendar.getInstance().getTime());
    }
    public String getIdEx(){return this.idEx;}
    public void setIdEx(String idEx){this.idEx = idEx;}
    public void setContested(String i){this.contested=i;}
    public String getContested(){return this.contested;}
    public String getDate(){ return this.date;}
    public String getName(){
        return this.name;
    }
    public void setCreator(String creator){this.creator = creator;}
    public String getCreator(){return this.creator;}

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
