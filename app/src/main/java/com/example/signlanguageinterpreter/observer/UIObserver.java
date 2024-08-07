package com.example.signlanguageinterpreter.observer;

import android.widget.TextView;

public class UIObserver implements Observer {
    private TextView textView;

    public UIObserver(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void update(String message) {
        textView.setText("Interpreted gesture: " + message);
    }
}
