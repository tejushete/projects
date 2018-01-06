package com.example.teju.testapp;

/**
 * Created by Teju on 12/24/2017.
 */

public class UserTextMessage {
    private String Number;
    private String MessageBody;
    private String Direction;
    private String Date;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDirection() {
        return Direction;

    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(String messageBody) {
        MessageBody = messageBody;
    }
}
