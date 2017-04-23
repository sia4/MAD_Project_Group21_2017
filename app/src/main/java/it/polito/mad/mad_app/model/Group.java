package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Group {
    private String name;
    private String description;
    private String imagePath;

    private Map<String, Boolean> members = new TreeMap<>();
    private List<UserData> lUsers = new ArrayList<>();
    private Map<String, Boolean> expenses = new TreeMap<>();
    private Map<String, Float> currencies = new TreeMap<>();

    public Group() { }

    // TODO: creare classe currency e passare un oggetto currency da cui prendo una stringa identificativa
    public Group(String n, String d, String c) {
        this.name = n;
        this.description = d;
        currencies.put("EUR", new Float(0)); //la currency di default ha valore 0
    }

    public String getName(){
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String s) {
        this.description = s;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String p) {
        this.imagePath = p;
    }

    public void addMember(String member) {
        members.put(member, true);
    }
    public void addUser(UserData user){
        lUsers.add(user);
    }

    public Map<String, Boolean> getMembers() {return members; }

    public Map<String, Float> getCurrencies() {return currencies; }

    public Map<String, Boolean> getExpenses(){return expenses; }

}
