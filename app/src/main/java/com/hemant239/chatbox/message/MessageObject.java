package com.hemant239.chatbox.message;

import java.util.Objects;

public class MessageObject {


    String messageId,
            text,
            imageUri,
            senderId,
            senderName,
            time,
            date;

    long timeStamp;

    boolean isInfo,
            isDeletedForEveryone;


    public MessageObject(String messageId) {
        this.messageId = messageId;
    }

    public MessageObject() {
        messageId = null;
        text = null;
        senderId = null;
        senderName = null;
        imageUri = null;
        time = null;
        date = null;
    }

    public MessageObject(String messageId, String text, String imageUri, String senderId, String senderName, String time, String date, long timeStamp, boolean isInfo, boolean isDeletedForEveryone) {
        this.messageId = messageId;
        this.text = text;
        this.imageUri = imageUri;
        this.senderId = senderId;
        this.senderName = senderName;
        this.time = time;
        this.date = date;
        this.timeStamp = timeStamp;
        this.isInfo = isInfo;
        this.isDeletedForEveryone = isDeletedForEveryone;
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

    public String getMessageId() {
        return messageId;
    }

    public boolean isInfo() {
        return isInfo;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isDeletedForEveryone() {
        return isDeletedForEveryone;
    }

    public void setDeletedForEveryone(boolean deletedForEveryone) {
        isDeletedForEveryone = deletedForEveryone;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageObject)) return false;
        MessageObject that = (MessageObject) o;
        return messageId.equals(that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
}
