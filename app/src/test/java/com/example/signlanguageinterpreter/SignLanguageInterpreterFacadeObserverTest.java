package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;
import android.widget.TextView;

import androidx.camera.view.PreviewView;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.signlanguageinterpreter.facade.SignLanguageInterpreterFacade;
import com.example.signlanguageinterpreter.observer.LogObserver;
import com.example.signlanguageinterpreter.observer.Observer;
import com.example.signlanguageinterpreter.observer.UIObserver;

import java.util.List;
import java.util.ArrayList;

public class SignLanguageInterpreterFacadeObserverTest {

    @Test
    public void testAddObserver() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PreviewView previewView = new PreviewView(context);
        SignLanguageInterpreterFacade facade = new SignLanguageInterpreterFacade(context, previewView);

        UIObserver uiObserver = new UIObserver(new TextView(context));
        LogObserver logObserver = new LogObserver();

        facade.addObserver(uiObserver);
        facade.addObserver(logObserver);

        List<Observer> observers = facade.getObservers();
        assertTrue("Observer list should contain UIObserver", observers.contains(uiObserver));
        assertTrue("Observer list should contain LogObserver", observers.contains(logObserver));
    }
}
