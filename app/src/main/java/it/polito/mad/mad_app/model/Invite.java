package it.polito.mad.mad_app.model;

/**
 * Created by Silvia Vitali on 27/04/2017.
 */

public class Invite {
    private String email;
    private String groupId;
    private String groupName;
    private String groupPath;

    public Invite() {}
    public Invite(String email, String groupId, String groupName, String groupPath) {
        this.email = email;
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPath = groupPath;
    }

    public String getEmail(){
        return this.email;
    }
    public String getGroupId(){
        return this.groupId;
    }
    public String getGroupPath(){
        return this.groupPath;
    }
    public String getGroupName(){
        return this.groupName;
    }
}
