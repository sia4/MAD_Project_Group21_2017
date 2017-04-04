package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainData {

    private static final MainData ourInstance = new MainData();
    private String myUsername;
    private String myPassword;
    private Map<String, GroupData> lGroups = new TreeMap<>();
    private Map<String, UserData> lUser = new TreeMap<>();

    public static MainData getInstance() {
        return ourInstance;
    }

    private MainData() {

        GroupData group1 = new GroupData("Coinquilini", "Gruppo dei coinquilini di via Tolmino 7");
        UserData user1 = new UserData("edoardo.operti@gmail.com", "Edoardo", "Operti", 333333333);
        UserData user2 = new UserData("lucia.larocca@gmail.com", "Lucia", "Larocca", 444444444);

        group1.addUser(user1);
        group1.addUser(user2);
        group1.addExpensive("Pane", "Pane per domani a pranzo", "Spesa", "EUR", (float)10.2, "Alla Romana");
        group1.addExpensive("Vino", "Vino per domani a pranzo", "Spesa", "EUR", (float)10.2, "Alla Romana");

        lGroups.put(group1.getName(), group1);
        lUser.put(user1.getName(), user1);
        lUser.put(user2.getName(), user2);

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
    public String getMyUsername(){ return this.myUsername;}
    public String getMyPassword(){ return  this.myPassword;}

    public void addExpensiveToGroup(String Gname, String name, String descr, String category, String currency, float value, String algorithm){
        this.lGroups.get(Gname).addExpensive(name, descr, category, currency, value, algorithm);
    }


}