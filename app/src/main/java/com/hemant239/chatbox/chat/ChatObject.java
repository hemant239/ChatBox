package com.hemant239.chatbox.chat;

public class ChatObject {
    String uid, name , imageUri;


    public ChatObject(String uid){
        this.uid=uid;
    }

    public ChatObject(String uid,String name){
        this.uid=uid;
        this.name=name;
    }
    public ChatObject(String uid, String name, String imageUri){
        this.uid=uid;
        this.name=name;
        this.imageUri=imageUri;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUri() {
        return imageUri;
    }
}
