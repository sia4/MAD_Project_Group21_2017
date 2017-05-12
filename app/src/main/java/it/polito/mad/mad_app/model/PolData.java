package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 06/05/2017.
 */

public class PolData {

    private String usersNumber;
    private String acceptsNumber;
    private List<String> acceptUsers = new ArrayList<>();

    public PolData(String usersNumber, String acceptsNumber) {
        this.usersNumber = usersNumber;
        this.acceptsNumber = acceptsNumber;
    }

    public void addAccept(String userid){
        acceptUsers.add(userid);
    }
    public String getUsersNumber(){return this.usersNumber;}
    public String getAcceptsNumber(){return this.acceptsNumber;}
}
