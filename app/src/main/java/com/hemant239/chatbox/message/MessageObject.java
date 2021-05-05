package com.hemant239.chatbox.message;

public class MessageObject {


    String  messageId,
            text,
            senderId,
            senderName,
            imageUri,
            time,
            date;


    public MessageObject(String messageId,String text,String imageUri,String senderId,String senderName, String time, String date) {
        this.messageId=messageId;
        this.text=text;
        this.imageUri=imageUri;
        this.senderId=senderId;
        this.senderName=senderName;
        this.time=time;
        this.date=date;
    }


    public MessageObject() {
                messageId  =null;
                text=null;
                senderId=null;
                senderName=null;
                imageUri=null;
                time=null;
                date=null;
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

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getDate() {
        return date;
    }
}
