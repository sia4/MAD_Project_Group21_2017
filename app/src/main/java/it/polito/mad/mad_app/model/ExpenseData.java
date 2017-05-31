package it.polito.mad.mad_app.model;

import android.support.annotation.NonNull;

import java.util.Map;

public class ExpenseData implements Comparable<ExpenseData>{
    private String idEx;
    private String name;
    private String description;
    private String category;
    private String currency;
    private String value;
    private String myvalue;
    private String algorithm;
    private String date;
    private String creator;
    private String contested;
    private String creatorId="0";
    private String defaultcurrency;
    private String imagePath;
    private Map<String,Boolean> users;
    public ExpenseData(String n, String d, String ca, String cu, String value, String myvalue, String algorithm, String dflt) {
        this.name = n;
        this.description = d;
        this.category = ca;
        this.currency = cu;
        this.value = value;
        this.myvalue = myvalue;
        this.algorithm = algorithm;
        this.date = Long.toString(System.currentTimeMillis());
        this.defaultcurrency = dflt;
        this.imagePath=null;
    }
    public ExpenseData(String n, String d, String ca, String cu, String value, String myvalue, String algorithm, String dflt,String Image) {
        this.name = n;
        this.description = d;
        this.category = ca;
        this.currency = cu;
        this.value = value;
        this.myvalue = myvalue;
        this.algorithm = algorithm;
        this.date = Long.toString(System.currentTimeMillis());
        this.defaultcurrency = dflt;
        this.imagePath=Image;
    }
    public String getCreatorId(){return this.creatorId;}
    public void setCreatorId(String id){this.creatorId=id;}
    public String getIdEx(){return this.idEx;}
    public void setIdEx(String idEx){this.idEx = idEx;}
    public void setContested(String i){this.contested=i;}
    public void setMyvalue(String i){this.myvalue = i;}
    public void setDate(String date){this.date = date;}
    public void setImagePath(String image){this.imagePath=image;}
    public String getContested(){return this.contested;}
    public String getDate(){ return this.date;}
    public String getName(){
        return this.name;
    }
    public void setCreator(String creator){this.creator = creator;}
    public String getCreator(){return this.creator;}
    public String getImagePath(){return this.imagePath;}
    public String getDefaultcurrency() {
        return this.defaultcurrency;
    }

    public String getDescription(){
        return this.description;
    }

    public String getCategory(){
        return this.category;
    }

    public String getCurrency(){
        return this.currency;
    }

   /* public String getCurrencyRow() {
        Currencies c = new Currencies();
        return c.getCurrencyString(this.currency);

    }

    public String getCurrencySymbol() {
        Currencies c = new Currencies();
        String Symbol = c.getCurrencySymbol(this.currency);
        return Symbol;
    } */

    public String getValue(){
        return this.value;
    }
    public String getMyvalue(){return this.myvalue;}
    public String getAlgorithm(){ return this.algorithm;}

    public int compareTo(@NonNull ExpenseData e) {

        long o1, o2;

        o1 = Long.valueOf(date);

        o2 = Long.valueOf(e.getDate());


        return (int)(o2 - o1);
    }

    public String toString() {

        String s = "{" + "idEX: " + idEx + ", Name: " + name + ", Descr: " + description + ", Category: " + category + ", Currency: " + currency + ", Default Currency:" + defaultcurrency + ", Value: " + value + ", Algorithm: " + algorithm + ", Date: " + date + ", Creator: " + creator + ", Contested: " + contested + ", CreatorID: " + creatorId + "}";
        return s;
    }
}
