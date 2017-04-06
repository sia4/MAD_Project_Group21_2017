package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class GroupData {

    private String name;
    private String description;
    private List<ExpenseData> lexpensive = new ArrayList<>();
    private List<UserData> lUsers = new ArrayList<>();
    private Map<String, BalanceData> lBudget = new TreeMap<>();
    private Map<String, Float> uPercentuage = new TreeMap<>();
    private Map<String, Float> uImport = new TreeMap<>();

    public GroupData(String n, String d){
        this.name = n;
        this.description = d;
    }

    public String getName(){
        return this.name;
    }
    public String getDescription() { return this.description;}

    public  void addExpenseToUser(String name, float value, String currency){

        float tmp;
        if(lBudget.containsKey(name)){
            tmp = lBudget.get(name).getValue() + value;
        }
        else
        {
            tmp = value;
        }
        lBudget.put(name, new BalanceData(name, this.name, tmp, currency));

    }
    public void allaRomana(float value, String currency){
        float quote = value/(lUsers.size()+1);
        for (UserData key: lUsers)
             addExpenseToUser(key.getName(), quote, currency);
    }
    public void byPercentuage(float value, String currency){
        for (UserData key: lUsers)
            addExpenseToUser(key.getName(), value*uPercentuage.get(key.getEmail())/100, currency);
    }
    public void byImport(float value, String currency){
        for (UserData key: lUsers)
            addExpenseToUser(key.getName(), uImport.get(key.getEmail()), currency);
    }
    public float getExpense(String name){
        return this.lBudget.get(name).getValue();
    }
    public float getTotExpenses(){
        float sum = 0;
        for (String key: lBudget.keySet())
            sum += lBudget.get(key).getValue();
        return sum;
    }
    public float getPosExpenses(){
        float sum = 0;
        for (String key: lBudget.keySet())
            if(lBudget.get(key).getValue()>0)
                sum += lBudget.get(key).getValue();
        return sum;
    }
    public float getNegExpenses(){
        float sum = 0;
        for (String key: lBudget.keySet())
            if(lBudget.get(key).getValue()<0)
             sum += lBudget.get(key).getValue();
        return sum;
    }
    public List<BalanceData> getExpensesList(){return new ArrayList<BalanceData>(lBudget.values());}
    public void updateExpense(String name, float value) {
        if(lBudget.containsKey(name)) {
            lBudget.get(name).changeValue(value);
        }
    }
    public Map<String, Float> getuPercentuageMap(){return this.uPercentuage;}
    public Map<String, Float> getuImportMap(){return this.uImport;}
    public  void addExpensive(String name, String descr, String category, String currency, float value, float myvalue, String algorithm){
        this.lexpensive.add(new ExpenseData(name, descr, category, currency, value,myvalue, algorithm));
    }
    public void addUser(UserData user){
        lUsers.add(user);
    }
    public List<ExpenseData> getExpensies(){return lexpensive;}
    public List<UserData> getlUsers() { return lUsers;}

}
