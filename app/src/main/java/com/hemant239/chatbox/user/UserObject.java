package com.hemant239.chatbox.user;

public class UserObject {

    private String  uid,
                    name,
                    phoneNumber,
                    profileImageUri;

    private boolean isSelected=false;


    public UserObject(){
    }

    public UserObject(String uid,String name,String phoneNumber,String profileImageUri){
        this.uid            =uid;
        this.name           =name;
        this.phoneNumber    =phoneNumber;
        this.profileImageUri=profileImageUri;

    }

    public UserObject(String uid,String name,String phoneNumber){
        this.uid            =uid;
        this.name           =name;
        this.phoneNumber    =phoneNumber;
        this.profileImageUri="";
    }

    public UserObject(String name, String phoneNumber){
        this.name           =name;
        this.phoneNumber    =phoneNumber;
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

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
