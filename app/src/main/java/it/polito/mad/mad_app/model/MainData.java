package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;

public class MainData {
    private String myUsername;
    private String myPassword;
    private static List<GroupData> lGroups = new ArrayList<>();

    public MainData(String s, String p){
        this.myUsername = s;
        this.myPassword = p;
    }

    public static void addGroup(String n, String d){
        lGroups.add(new GroupData(n, d));
    }
    public static List<GroupData> getGroupList(){
        return lGroups;
    }
    public String getMyUsername(){ return this.myUsername;}
    public String getMyPassword(){ return  this.myPassword;}




}
