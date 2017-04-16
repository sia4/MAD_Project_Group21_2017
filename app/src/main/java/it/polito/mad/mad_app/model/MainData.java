package it.polito.mad.mad_app.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainData {

    private static final MainData ourInstance = new MainData();
    private String myName;
    private String mySurname;
    private String myEmail;
    private String myPassword;
    private UserData me;
    private Map<String, GroupData> lGroups = new TreeMap<>();
    private Map<String, UserData> lUser = new TreeMap<>();
    private List<ActivityData> lActivities = new ArrayList<>();

    public static MainData getInstance() {
        return ourInstance;
    }

    private MainData() {
        myEmail = "marco.rossi@gmail.it";
        myPassword = "0000";
        myName = "Marco";
        mySurname = "Rossi (Io)";

        me = new UserData(myEmail, myName, mySurname, 33333332);

        GroupData group1 = new GroupData("Coinquilini", "Gruppo dei coinquilini di via Tolmino 7");
        GroupData group2 = new GroupData("Quartiere", "Gruppo dei vicini di quartiere Crocetta");
        GroupData group3 = new GroupData("Viaggio", "Gruppo dei compagni di viaggio");
        UserData user1 = new UserData("edoardo.operti@gmail.com", "Edoardo", "Operti", 33333333);
        UserData user2 = new UserData("lucia.larocca@gmail.com", "Lucia", "Larocca", 444444444);
        UserData user3 = new UserData("luca.mazzucco@gmail.com", "Luca", "Mazzucco", 337773333);
        UserData user4 = new UserData("silvia.vitali@gmail.com", "Silvia", "Vitali", 446664444);
        UserData user5 = new UserData("simona.verney@gmail.com", "Simona", "Verney", 335553833);
        UserData user6 = new UserData("giovanni.malnati@gmail.com", "Giovanni", "Malnati", 44999444);
        UserData user7 = new UserData("pietro.laface@gmail.com", "Pietro", "Laface", 33337773);
        UserData user8 = new UserData("silvia.chiusano@gmail.com", "Silvia", "Chiusano", 22224444);
        UserData user9 = new UserData("gianpaolo.cabodi@gmail.com", "Gianpaolo", "Cabodi", 31113333);
        UserData user10 = new UserData("stefano.quer@gmail.com", "Stefano", "Quer", 444234554);

        group1.addUser(user1);
        group1.addUser(user4);
        group1.addUser(user6);
        group1.addUser(user2);
        group1.addUser(user3);
        group2.addUser(user4);
        group2.addUser(user5);
        group2.addUser(user6);
        group2.addUser(user7);
        group3.addUser(user8);
        group3.addUser(user6);
        group3.addUser(user9);
        group3.addUser(user10);
        group1.addExpensive("Pane", "Pane per domani a pranzo", "Spesa", "EUR", (float)10.2,(float)10.2, "Alla Romana");
        group1.addExpensive("Vino", "Vino per domani a pranzo", "Spesa", "EUR", (float)17.2,(float)17.2, "Alla Romana");
        group2.addExpensive("Tosaerba", "Tosaerba per il prato in comune", "Giardino", "EUR", (float)150.7,(float)150.7, "Alla Romana");
        group2.addExpensive("Intonaco", "Rifacimento facciata condominio", "Muratura", "EUR", (float)210.2,(float)210.2, "Alla Romana");
        group3.addExpensive("Volo", "Volo per Valencia", "Trasporti", "EUR", (float)80.2,(float)80.2, "Alla Romana");
        group3.addExpensive("Museo", "Entrata città delle scienze", "Cultura", "EUR", (float)14.9,(float)14.9, "Alla Romana");
        group1.addExpenseToUser(user1.getEmail(), 4, "EUR");
        group1.addExpenseToUser(user6.getEmail(), -144, "EUR");
        group1.addExpenseToUser(user2.getEmail(), 7, "EUR");
        group1.addExpenseToUser(user3.getEmail(), 14, "EUR");
        group2.addExpenseToUser(user4.getEmail(), -4, "EUR");
        group2.addExpenseToUser(user5.getEmail(), 40, "EUR");
        group2.addExpenseToUser(user6.getEmail(), 24, "EUR");
        group2.addExpenseToUser(user7.getEmail(), -94, "EUR");
        group3.addExpenseToUser(user8.getEmail(), -74, "EUR");
        group3.addExpenseToUser(user6.getEmail(), -54, "EUR");
        group3.addExpenseToUser(user9.getEmail(), 61, "EUR");
        group3.addExpenseToUser(user10.getEmail(), 42, "EUR");
        lGroups.put(group1.getName(), group1);
        lGroups.put(group2.getName(), group2);
        lGroups.put(group3.getName(), group3);
        lUser.put(user1.getEmail(), user1);
        lUser.put(user2.getEmail(), user2);
        lUser.put(user3.getEmail(), user3);
        lUser.put(user4.getEmail(), user4);
        lUser.put(user5.getEmail(), user5);
        lUser.put(user6.getEmail(), user6);
        lUser.put(user7.getEmail(), user7);
        lUser.put(user8.getEmail(), user8);
        lUser.put(user9.getEmail(), user9);
        lUser.put(user10.getEmail(), user10);

        lActivities.add(new ActivityData("gianpaolo.cabodi@gmail.it", "Gianpaolo added an expense on group Coinquilini", "4 apr 2017, 11:45"));
        lActivities.add(new ActivityData("stefano.quer@gmail.it", "Stefano added an expense on group Viaggio", "4 apr 2017, 18:21"));
        lActivities.add(new ActivityData("giovanni.malnati@gmail.it", "Giovanni invited you on group MAD21", "8 mar 2017, 8:17"));
        lActivities.add(new ActivityData("silvia.chiusano@gmail.it", "Silvia added an user on group Quartiere", "4 apr 2017, 19:44"));

    }
    public GroupData addGroup(String n, String d){

        GroupData g = new GroupData(n, d);
        lGroups.put(n, g);

        return g;
    }

    public void addActivity(String email, String text, String date){this.lActivities.add(new ActivityData(email, text, date));    }
    public List<ActivityData> getActivitiesList(){return lActivities;}

    public List<GroupData> getGroupList(){
        return new ArrayList<GroupData>(lGroups.values());
    }
    public  GroupData getGroup(String name){
        return lGroups.get(name);
    }
    public String getMyEmail(){ return this.myEmail;}
    public String getMyPassword(){ return  this.myPassword;}
    public String getMyName(){return this.myName;}
    public String getMySurname(){return this.mySurname;}
    public void setMyEmail(String email){this.myEmail = email;}
    public void setMyName(String name){this.myName = name;}
    public void setMySurname(String surname){this.mySurname = surname;}
    public void setMyPassword(String password){this.myPassword = password;}
    public UserData returnMyData() {return me;}

    public void addExpensiveToGroup(String Gname, String name, String descr, String category, String currency, float value, float myvalue, String algorithm){
        this.lGroups.get(Gname).addExpensive(name, descr, category, currency, value, myvalue, algorithm);
    }

    public UserData findUserByMail(String email) {
        if(!lUser.containsKey(email))
            return null;
        else
            return lUser.get(email);
    }

}