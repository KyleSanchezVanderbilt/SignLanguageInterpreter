package com.example.signlanguageinterpreter.observer;

import android.widget.TextView;

public class UIObserver implements Observer {
    private TextView textView;

    public UIObserver(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void update(String result) {
        textView.setText(result);
    }

    @Override
    public void onPhotoCaptured(String photoPath) {
        textView.setText("Photo saved at: " + photoPath);
    }

    @Override
    public void onVideoCaptured(String videoPath) {
        textView.setText("Video saved at: " + videoPath);
    }
}
