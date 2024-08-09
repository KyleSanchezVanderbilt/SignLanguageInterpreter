package com.example.signlanguageinterpreter.observer;

import android.util.Log;

public class LogObserver implements Observer {
    private static final String TAG = "LogObserver";

    @Override
    public void update(String result) {
        Log.d(TAG, "Result: " + result);
    }

    @Override
    public void onPhotoCaptured(String photoPath) {
        Log.d(TAG, "Photo captured: " + photoPath);
    }

    @Override
    public void onVideoCaptured(String videoPath) {
        Log.d(TAG, "Video captured: " + videoPath);
    }
}
