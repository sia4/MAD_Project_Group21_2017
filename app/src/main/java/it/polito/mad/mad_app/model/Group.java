package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Group {
    private String name;
    private String description;
    private String imagePath;
    private String defaultCurrency;

    private Map<String, Boolean> members = new TreeMap<>();
    private Map<String, Boolean> expenses = new TreeMap<>();
    private Map<String, Float> currencies = new TreeMap<>();

    public Group() { }

    // TODO: creare classe currency e passare un oggetto currency da cui prendo una stringa identificativa
    public Group(String n, String d, String c) {
        this.name = n;
        this.description = d;
        defaultCurrency = c;
        currencies.put(c, new Float(0));
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

    public void addMembers(Map<String,Boolean> m) {this.members=m;    }

    public Map<String, Boolean> getMembers() {return members; }

    public List<String> getMemberList() {

        List<String> m = new ArrayList<>();

        for (String key : members.keySet()) {

            if (members.get(key) == true) {
                m.add(key);
            }

        }

        return m;
    }

    public Map<String, Float> getCurrencies() {return currencies; }

    public Map<String, Boolean> getExpenses(){return expenses; }

    public void addMember(String s) {
        members.put(s, true);
    }

    public void addCurrencies(Map<String, Float> currencies) {
        this.currencies = currencies;
    }
}
