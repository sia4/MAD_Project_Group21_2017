package it.polito.mad.mad_app.model;

/**
 * Created by Luca on 04/04/2017.
 */

public class BalanceData {

    private String name;
    private float value;


    public BalanceData(String name, float value){

        this.name = name;
        this.value = value;
    }

    public String getName(){return this.name;}
    public float getValue(){return this.value;}
    public void addValue(float value){this.value += value;}



}
