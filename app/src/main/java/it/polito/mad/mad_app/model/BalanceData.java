package it.polito.mad.mad_app.model;

/**
 * Created by Luca on 04/04/2017.
 */

public class BalanceData {

    private String name;
    private float value;
    private String gname;
    private String currency;


    public BalanceData(String name, String gname, float value, String currency){

        this.name = name;
        this.gname = gname;
        this.value = value;
        this.currency = currency;
    }

    public String getName(){return this.name;}
    public String getGName(){return this.gname;}
    public float getValue(){return this.value;}
    public String getCurrency(){return this.currency;}
    public void changeValue(float value){this.value -= value;}



}
