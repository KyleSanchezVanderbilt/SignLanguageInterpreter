package com.example.signlanguageinterpreter.observer;

public interface Observer {
    void update(String result);
    void onPhotoCaptured(String photoPath);  // New method to handle photo notifications
    void onVideoCaptured(String videoPath);  // New method to handle video notifications
}
