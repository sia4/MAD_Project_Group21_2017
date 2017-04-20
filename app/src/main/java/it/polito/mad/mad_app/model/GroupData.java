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
    private Map<String, BalanceData> lBudget = new TreeMap<>(); //mail, oggetto
    private Map<String, Integer> uPercentuage = new TreeMap<>();
    private Map<String, Integer> uImport = new TreeMap<>();
    private Map<String, Float> currencies = new TreeMap<>();


    public GroupData(String n, String d, String c) {
        this.name = n;
        this.description = d;
        Float f = new Float(1);
        currencies.put(c, f);
    }

    public String getName(){
        return this.name;
    }
    public String getDescription() { return this.description;}

    public  void addExpenseToUser(String email, float value, String currency){

        float tmp;
        if(lBudget.containsKey(email)){
            tmp = lBudget.get(email).getValue() + value;
        }
        else
        {
            tmp = value;
        }
        lBudget.put(email, new BalanceData(email, this.name, tmp, currency));

    }
    public void allaRomana(float value, String currency){
        float quote = value/(lUsers.size());
        for (UserData key: lUsers)
             addExpenseToUser(key.getEmail(), quote, currency);
    }
    public void byPercentuage(float value, String currency){
        for (UserData key: lUsers)
            addExpenseToUser(key.getEmail(), value*uPercentuage.get(key.getEmail())/100, currency);
    }
    public void byImport(float value, String currency){
        for (UserData key: lUsers)
            addExpenseToUser(key.getEmail(), uImport.get(key.getEmail()), currency);
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

    public void updateExpense(String mail, float value) {
        if(lBudget.containsKey(mail)) {
            lBudget.get(mail).changeValue(value);
        }
    }
    public void addTouPercentuageMap(String email, int algValue) {
        uPercentuage.put(email, algValue);
    }
    public void addTouImportMap(String email, int algValue) {
        uImport.put(email, algValue);
    }
    public  void addExpensive(String name, String descr, String category, String currency, float value, float myvalue, String algorithm){
        this.lexpensive.add(new ExpenseData(name, descr, category, currency, value,myvalue, algorithm));
    }
    public void addUser(UserData user){
        lUsers.add(user);
    }
    public List<ExpenseData> getExpensies(){return lexpensive;}
    public List<UserData> getlUsers() { return lUsers;}

    /* Funzioni per la gestione delle currencies relative al gruppo*/

    public void addCurrency(String c, Float change) {
        if (!currencies.containsKey(c)) {
            currencies.put(c, change);
        }
    }

    public Map<String, Float> getCurrencies() {
        return currencies;
    }

    public Float getChange(String c) {

        if (currencies.containsKey(c)) {
            return currencies.get(c);
        } else {
            return new Float(0);
        }

    }

    public float getPosExpensesChanged() {

        float sum = 0;
        float value = 0;
        BalanceData b;
        float change;
        String c;

        for (String key : lBudget.keySet()) {

            b = lBudget.get(key);

            if (b.getValue() > 0) {

                c = b.getCurrency();
                change = getChange(c);
                value = b.getValue();
                value = value * change;
                sum += value;

            }

        }

        return sum;

    }

    public float getNegExpensesChanged() {

        float sum = 0;
        float value = 0;
        BalanceData b;
        float change;
        String c;

        for (String key : lBudget.keySet()) {

            b = lBudget.get(key);

            if (b.getValue() < 0) {

                c = b.getCurrency();
                change = getChange(c);
                value = b.getValue();
                value = value * change;
                sum += value;

            }

        }

        return sum;

    }

}
