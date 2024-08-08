package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;
import android.widget.TextView;

import com.example.signlanguageinterpreter.observer.UIObserver;

public class UIObserverTest {

    @Test
    public void testUIObserverUpdate() {
        TextView mockTextView = new TextView(null); // Simple TextView for testing
        UIObserver observer = new UIObserver(mockTextView);
        String testResult = "Test result";

        observer.update(testResult);
        assertEquals(testResult, mockTextView.getText().toString());
    }
}
