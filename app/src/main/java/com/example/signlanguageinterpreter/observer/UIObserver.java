package com.example.signlanguageinterpreter.observer;

import android.widget.TextView;

/**
 * UIObserver is an implementation of the Observer interface.
 * It is responsible for updating a TextView with the interpreted gesture results.
 */
public class UIObserver implements Observer {
    private TextView textView;

    /**
     * Constructor that initializes the UIObserver with a TextView.
     *
     * @param textView The TextView to be updated with the interpreted gesture.
     */
    public UIObserver(TextView textView) {
        this.textView = textView;
    }

    /**
     * This method is called when the observed object is changed.
     * It updates the TextView with the new interpreted gesture message.
     *
     * @param message The interpreted gesture result to be displayed.
     */
    @Override
    public void update(String message) {
        // Set the text of the TextView to display the interpreted gesture result
        textView.setText("Interpreted gesture: " + message);
    }
}
