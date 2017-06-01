package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.R;


public class MainData {

    private static final MainData ourInstance = new MainData();
    private String myName;
    private String mySurname;
    private String myEmail;
    private String defaultCurrencyForStats;
    private Map<String, GroupData> lGroups = new TreeMap<>();
    private Map<String, UserData> lUser = new TreeMap<>();
    private String GroupsFragmentData ="";
    private String GroupFragmentArchive="yes";
    private Map<String, String> myGroupsId = new TreeMap<>();
    private Map<String, Float> balanceByGroupsPos = new TreeMap<>();
    private Map<String, Float> balanceByGroupsNeg = new TreeMap<>();

    private List<String> Categories = new ArrayList<>();
    private List<Integer> catToId = new ArrayList<>();
    private List<Integer> catToIdLow = new ArrayList<>();
    public static MainData getInstance() {
        return ourInstance;
    }

    private MainData() {
        myEmail = "marco.rossi@gmail.it";
        myName = "Marco";
        mySurname = "Rossi (Me)";
        this.Categories.add("Entertainment");
        this.Categories.add("Food and Drinks");
        this.Categories.add("House and Utilities");
        this.Categories.add("Clothing");
        this.Categories.add("Present");
        this.Categories.add("Medical Expenses");
        this.Categories.add("Transport");
        this.Categories.add("Hotel");
        this.Categories.add("Cleaning");
        this.Categories.add("General");
        this.Categories.add("Other");

        catToId.add(R.drawable.entertainment);
        catToId.add(R.drawable.food);
        catToId.add(R.drawable.house);
        catToId.add(R.drawable.clothing);
        catToId.add(R.drawable.present);
        catToId.add(R.drawable.medical);
        catToId.add(R.drawable.transportation);
        catToId.add(R.drawable.hotel);
        catToId.add(R.drawable.cleaning);
        catToId.add(R.drawable.general);
        catToId.add(R.drawable.other);

        catToIdLow.add(R.drawable.ic_category_low_entertainment);
        catToIdLow.add(R.drawable.ic_category_low_food);
        catToIdLow.add(R.drawable.ic_category_low_house);
        catToIdLow.add(R.drawable.ic_category_low_clothing);
        catToIdLow.add(R.drawable.ic_category_low_present);
        catToIdLow.add(R.drawable.ic_category_low_medical);
        catToIdLow.add(R.drawable.ic_category_low_transportation);
        catToIdLow.add(R.drawable.ic_category_low_hotel);
        catToIdLow.add(R.drawable.ic_category_low_cleaning);
        catToIdLow.add(R.drawable.ic_category_low_general);
        catToIdLow.add(R.drawable.ic_category_low_other);


    }

    public String getDefaultCurrencyForStats(){return this.defaultCurrencyForStats;}
    public void setDefaultCurrencyForStats(String symbol){this.defaultCurrencyForStats = symbol;}
    public List<String> getCategories(){return  this.Categories;}
    public List<Integer> getCatToId(){return  this.catToId;}
    public List<Integer> getCatToIdLow(){return  this.catToIdLow;}
    public void setGroupsFragmentData(String search_text){this.GroupsFragmentData=search_text;}
    public String getGroupsFragmentData(){return  this.GroupsFragmentData;}

    public void setGroupFragmentArchive(String archive){this.GroupFragmentArchive=archive;}
    public String getGroupFragmentArchive(){return  this.GroupFragmentArchive;}

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