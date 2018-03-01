package com.example.teju.u_and_e;

import android.net.Uri;

/**
 * Created by Teju on 12/12/2017.
 */

public class contacts_Items {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Uri img;
    private String contact_id;

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getImg() {
        if(img == null) return Uri.EMPTY;
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }
}
