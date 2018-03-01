package com.example.teju.racing;

/**
 * Created by Teju on 2/21/2018.
 */

public interface RacingEventListener {
    /* True -> User Logged In
       False -> User Logged Out
    */
    void logInStatusChanged(boolean newLoggedInValue);
}
