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


    public GroupData(String n, String d){
        this.name = n;
        this.description = d;
    }

    public String getName(){
        return this.name;
    }
    public String getDescription() { return this.description;}

    public  void addExpenseToUser(String name, float value){

        float tmp;
        if(lBudget.containsKey(name)){
            tmp = lBudget.get(name).getValue() + value;
        }
        else
        {
            tmp = value;
        }
        lBudget.put(name, new BalanceData(name, tmp));

    }
    public void allaRomana(float value){
        float quote = value/(lUsers.size()+1);
        for (UserData key: lUsers)
             addExpenseToUser(key.getName(), quote);
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
    public List<BalanceData> getExpensesList(){
        return new ArrayList<BalanceData>(lBudget.values());
    }



    public  void addExpensive(String name, String descr, String category, String currency, float value, float myvalue, String algorithm){
        this.lexpensive.add(new ExpenseData(name, descr, category, currency, value,myvalue, algorithm));
    }

    public void addUser(UserData user){
        lUsers.add(user);
    }

    public List<ExpenseData> getExpensies(){

        return lexpensive;
    }

    public List<UserData> getlUsers() { return lUsers;}

}
