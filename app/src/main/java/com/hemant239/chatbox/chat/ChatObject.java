package com.hemant239.chatbox.chat;

public class ChatObject {
    String uid, name , imageUri,lastMessage,lastMessageSender,lastMessageTime;

    int numberOfUsers;


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
    public ChatObject(String uid, String name, String imageUri,int numberOfUsers){
        this.uid=uid;
        this.name=name;
        this.imageUri=imageUri;
        this.numberOfUsers=numberOfUsers;
    }
    public ChatObject(String uid, String name, String imageUri,String lastMessage,String lastMessageSender, String lastMessageTime){
        this.uid=uid;
        this.name=name;
        this.imageUri=imageUri;
        this.lastMessage=lastMessage;
        this.lastMessageSender=lastMessageSender;
        this.lastMessageTime=lastMessageTime;
    }

    public ChatObject(String uid, String name, String imageUri,String lastMessage,String lastMessageSender, String lastMessageTime,int numberOfUsers){
        this.uid=uid;
        this.name=name;
        this.imageUri=imageUri;
        this.lastMessage=lastMessage;
        this.lastMessageSender=lastMessageSender;
        this.lastMessageTime=lastMessageTime;
        this.numberOfUsers=numberOfUsers;
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


    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageSender() {
        return lastMessageSender;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }
}
