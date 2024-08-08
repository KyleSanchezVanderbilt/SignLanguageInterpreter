package com.example.signlanguageinterpreter.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * The Subject class maintains a list of observers and provides methods to add, remove,
 * and notify these observers. It implements the Observer Design Pattern to allow
 * objects to be notified of changes in the subject's state.
 */
public class Subject {
    // A list to hold all the observers that are observing this subject
    private List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    public void addObserver(Observer observer) {
        observers.add(observer); // Add the observer to the list
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer The observer to be removed.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer); // Remove the observer from the list
    }

    /**
     * Notifies all observers by calling their update method with the given message.
     *
     * @param message The message to be sent to all observers.
     */
    public void notifyObservers(String message) {
        // Iterate through all observers and call their update method with the message
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
