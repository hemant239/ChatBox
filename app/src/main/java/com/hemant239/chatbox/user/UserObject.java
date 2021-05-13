package com.hemant239.chatbox.user;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String  uid,
                    name,
                    phoneNumber,
                    status,
                    profileImageUri;

    private boolean isSelected=false;


    public UserObject(String uid,String name,String phoneNumber,String status,String profileImageUri){
        this.uid            =uid;
        this.name           =name;
        this.phoneNumber    =phoneNumber;
        this.status         =status;
        this.profileImageUri=profileImageUri;
    }



    public UserObject(String uid,String name,String phoneNumber,String status){
        this.uid            =uid;
        this.name           =name;
        this.phoneNumber    =phoneNumber;
        this.status         =status;
        this.profileImageUri="";
    }

    public UserObject(String name, String phoneNumber){
        this.name           =name;
        this.phoneNumber    =phoneNumber;
    }

    public UserObject(){
        this.uid            ="";
        this.name           ="";
        this.phoneNumber    ="";
        this.status         ="";
        this.profileImageUri="";

    }


    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public boolean isSelected() {
        return isSelected;
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
