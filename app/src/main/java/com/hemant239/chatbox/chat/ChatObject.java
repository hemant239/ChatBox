package com.hemant239.chatbox.chat;

import java.util.Objects;

public class ChatObject {
    private String  uid,
            name,
            imageUri,
            lastMessageText,
            lastMessageSender,
            lastMessageTime,
            lastMessageId;

    private int numberOfUsers;

    private boolean isSingleChat;


    public ChatObject(String uid, String name, String imageUri, int numberOfUsers, boolean isSingleChat) {
        this.uid=uid;
        this.name=name;
        this.imageUri=imageUri;
        this.numberOfUsers=numberOfUsers;
        this.isSingleChat=isSingleChat;
    }


    public ChatObject(String uid, String name, String imageUri,String lastMessageText,String lastMessageSender, String lastMessageTime,int numberOfUsers, String lastMessageId,boolean isSingleChat){
        this.uid=uid;
        this.name=name;
        this.imageUri=imageUri;
        this.lastMessageText=lastMessageText;
        this.lastMessageSender=lastMessageSender;
        this.lastMessageTime=lastMessageTime;
        this.numberOfUsers=numberOfUsers;
        this.lastMessageId=lastMessageId;
        this.isSingleChat=isSingleChat;
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


    public String getLastMessageText() {
        return lastMessageText;
    }

    public String getLastMessageId() {
        return lastMessageId;
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

    public boolean isSingleChat() {
        return isSingleChat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatObject that = (ChatObject) o;
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
