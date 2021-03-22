package com.hemant239.chatbox.message;

import android.os.Message;

public class MessageObject {


    String  messageId,
            text,
            senderId,
            senderName,
            imageUri,
            time;


    public MessageObject(String messageId,String text,String imageUri,String senderId,String senderName, String time){
        this.messageId=messageId;
        this.text=text;
        this.imageUri=imageUri;
        this.senderId=senderId;
        this.senderName=senderName;
        this.time=time;
    }

    public MessageObject(String messageId,String text,String imageUri,String senderId,String senderName){
        this.messageId=messageId;
        this.text=text;
        this.imageUri=imageUri;
        this.senderId=senderId;
        this.senderName=senderName;
    }

    public MessageObject(String messageId,String text){
        this.messageId=messageId;
        this.text=text;
    }


    public MessageObject(String messageId){
        this.messageId=messageId;
    }


    public String getMessageId() {
        return messageId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getTime() {
        return time;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }


}
