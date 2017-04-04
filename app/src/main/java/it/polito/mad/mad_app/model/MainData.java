package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainData {

    private String myUsername;
    private String myPassword;
    private static Map<String, GroupData> lGroups = new TreeMap<>();

    public MainData(String s, String p){
        this.myUsername = s;
        this.myPassword = p;
    }

    public void addGroup(String n, String d){
        lGroups.put(n, new GroupData(n, d));
    }
    public List<GroupData> getGroupList(){
        return new ArrayList<GroupData>(lGroups.values());
    }
    public  GroupData getGroup(String name){
        return lGroups.get(name);
    }
    public void addExpensiveToGroup(String Gname, String name, String descr, String category, String currency, float value, String algorithm){
        this.lGroups.get(Gname).addExpensive(name, descr, category, currency, value, algorithm);
    }
    public String getMyUsername(){ return this.myUsername;}
    public String getMyPassword(){ return  this.myPassword;}




}
