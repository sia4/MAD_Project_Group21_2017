package it.polito.mad.mad_app;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Luca on 31/03/2017.
 */

public class GroupData {

    private String name;
    private static List<ExpensiveData> lexpensive = new ArrayList<>();


    public GroupData(String n){
        this.name = n;
    }
    public static void addExpensive(String name, String descr, String category, String currency, int value){
        lexpensive.add(new ExpensiveData(name, descr, category, currency, value));
    }
    public static List<ExpensiveData> getExpensies(){
        return lexpensive;
    }

}
