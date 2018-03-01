package com.example.teju.u_and_e;

/**
 * Created by Teju on 1/18/2018.
 */

public class Notification {
    private String messageBody;
    private String number;
    private int message_id;
    private int message_count;

    public Notification() {
        message_id = -1;
        message_count = 0;
    }

    public Notification(String number) {
        message_id = -1;
        message_count = 0;
        this.number = number;
    }

    public void increaseMeassageCountByOne(){
        message_count = message_count+1;
    }

    public void resetNotification(){
        message_count = 0;
        message_id = -1;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getMessage_count() {
        return message_count;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }
}
