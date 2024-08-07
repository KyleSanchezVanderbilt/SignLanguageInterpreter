package com.example.signlanguageinterpreter.observer;

import android.util.Log;

public class LogObserver implements Observer {
    private static final String TAG = "LogObserver";

    @Override
    public void update(String message) {
        Log.d(TAG, "Interpreted gesture: " + message);
    }
}
