package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;

public class MainData {
    private String myUsername;
    private String myPassword;
    private List<GroupData> lGroups = new ArrayList<>();

    public MainData(String s, String p){
        this.myUsername = s;
        this.myPassword = p;
    }

    public void addGroup(String n, String d){
        this.lGroups.add(new GroupData(n, d));
    }
    public List<GroupData> getGroupList(){
        return this.lGroups;
    }
    public String getMyUsername(){ return this.myUsername;}
    public String getMyPassword(){ return  this.myPassword;}




}
