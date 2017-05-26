package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class GroupData {

    private String name;
    private String description;
    private String ImagePath;

    private List<ExpenseData> lexpensive = new ArrayList<>();
    private List<UserData> lUsers = new ArrayList<>();
    //private Map<String,Boolean> Users = new TreeMap<>();
    //private Map<String,Boolean> Expense = new TreeMap<>();
    private Map<String, BalanceData> lBudget = new TreeMap<>(); // mappa che continee solo in default currency
    private Map<String, BalanceData> cBudget = new TreeMap<>(); // mappa contenente gli elementi in altre currencies, la Key è formata da email+currency
    private Map<String, Integer> uPercentuage = new TreeMap<>();
    private Map<String, Integer> uImport = new TreeMap<>();
    private Map<String, Float> currencies = new TreeMap<>();
    private String default_currency;


    public GroupData(String n, String d, String c) {
        this.name = n;
        this.description = d;
        Float f = new Float(1);
        this.default_currency = c;
        currencies.put(c, f);
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

    public  void addExpenseToUser(String email, float value, String currency){

        float tmp;
        float change;

        if (!default_currency.equals(currency)) {

            if (cBudget.containsKey(email + currency)) {
                tmp = cBudget.get(email).getValue() + value;
            } else {
                tmp = value;
            }

            cBudget.put(email + currency, new BalanceData(email, this.name, tmp, currency));

            change = getChange(currency);
            value = value * change; // lo riporto alla currency di default

        }

        currency = default_currency;

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

    public float getExpenseC(String name, String currency) {
        return this.cBudget.get(name + currency).getValue();
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
        for (BalanceData b: lBudget.values())
            if(b.getValue()<0)
             sum += b.getValue();
        return sum;
    }

    public List<BalanceData> getExpensesList(){return new ArrayList<BalanceData>(lBudget.values());}

    public List<BalanceData> getExpensesListC() {
        return new ArrayList<BalanceData>(cBudget.values());
    }

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
    public  ExpenseData addExpensive(String name, String descr, String category, String currency, String value, String myvalue, String algorithm){
        ExpenseData e = new ExpenseData(name, descr, category, currency, value, myvalue, algorithm, this.default_currency);
        this.lexpensive.add(e);
        return e;

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

            b = cBudget.get(key);

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

            b = cBudget.get(key);

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

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String p) {
        this.ImagePath = p;
    }

}
