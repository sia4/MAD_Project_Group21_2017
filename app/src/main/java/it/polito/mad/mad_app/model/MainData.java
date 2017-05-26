package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainData {

    private static final MainData ourInstance = new MainData();
    private String myName;
    private String mySurname;
    private String myEmail;

    private Map<String, GroupData> lGroups = new TreeMap<>();
    private Map<String, UserData> lUser = new TreeMap<>();
    private String GroupsFragmentData ="";
    private Map<String, String> myGroupsId = new TreeMap<>();
    private Map<String, Float> balanceByGroupsPos = new TreeMap<>();
    private Map<String, Float> balanceByGroupsNeg = new TreeMap<>();

    public static MainData getInstance() {
        return ourInstance;
    }

    private MainData() {
        myEmail = "marco.rossi@gmail.it";
        myName = "Marco";
        mySurname = "Rossi (Me)";
    }



    public void setGroupsFragmentData(String search_text){this.GroupsFragmentData=search_text;}
    public String getGroupsFragmentData(){return  this.GroupsFragmentData;}

    public void setMyGroupsId(Map<String, String> gIds){this.myGroupsId = gIds; }
    public Map<String, String> getMyGroupsId(){return this.myGroupsId;}

    public void clearBalanceByGroup(){
        balanceByGroupsNeg.clear();
        balanceByGroupsPos.clear();
    }
    public void clearBalanceByGroupByKey(String key){
        balanceByGroupsPos.put(key, 0f);
        balanceByGroupsNeg.put(key, 0f);
    }
    public void addToBalanceByGroup(float tttt, String gKey){
        float oldValue;
        if (tttt >= 0) {
            if (balanceByGroupsPos.containsKey(gKey)) {
                oldValue = balanceByGroupsPos.get(gKey);
                balanceByGroupsPos.put(gKey, oldValue + tttt);
            } else {
                balanceByGroupsPos.put(gKey, tttt);
            }
        }
        else {
            if (balanceByGroupsNeg.containsKey(gKey)) {
                oldValue = balanceByGroupsNeg.get(gKey);
                balanceByGroupsNeg.put(gKey, oldValue - tttt);
            } else {
                balanceByGroupsNeg.put(gKey, - tttt);
                System.out.println("balanceByGroupNeg: "+ balanceByGroupsNeg);
            }
        }

    }
    public  Map<String, Float> getBalanceByGroupsPos(){return this.balanceByGroupsPos;}
    public  Map<String, Float> getBalanceByGroupsNeg(){return this.balanceByGroupsNeg;}

}