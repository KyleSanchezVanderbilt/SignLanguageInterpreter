package com.example.signlanguageinterpreter.facade;

import android.content.Context;
import com.example.signlanguageinterpreter.builder.TensorFlowModelBuilder;
import com.example.signlanguageinterpreter.model.TensorFlowModel;
import com.example.signlanguageinterpreter.singleton.CameraXManager;
import com.example.signlanguageinterpreter.observer.Observer;

import android.graphics.Bitmap;
import androidx.camera.view.PreviewView;

import java.util.ArrayList;
import java.util.List;

public class SignLanguageInterpreterFacade {
    private TensorFlowModel model;
    private CameraXManager cameraManager;
    private List<Observer> observers;

    public SignLanguageInterpreterFacade(Context context, PreviewView previewView) {
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(context);
        this.model = builder.setModelPath("detect.tflite").setNumThreads(4).build();
        this.cameraManager = CameraXManager.getInstance(context, previewView);
        this.observers = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void startCamera(Context context) {
        cameraManager.startCamera(context);
    }

    public List<String> classifyImage(Bitmap bitmap, int rotation) {
        List<String> results = model.classify(bitmap, rotation);
        notifyObservers(results.toString());
        return results;
    }

    public void takePhoto(Context context) {
        cameraManager.takePhoto(context);
        cameraManager.setOnPhotoSavedCallback(this::notifyPhotoObservers);
    }

    public void captureVideo(Context context) {
        cameraManager.captureVideo(context);
        cameraManager.setOnVideoSavedCallback(this::notifyVideoObservers);
    }

    private void notifyObservers(String result) {
        for (Observer observer : observers) {
            observer.update(result);
        }
    }

    private void notifyPhotoObservers(String photoPath) {
        for (Observer observer : observers) {
            observer.onPhotoCaptured(photoPath);
        }
    }

    private void notifyVideoObservers(String videoPath) {
        for (Observer observer : observers) {
            observer.onVideoCaptured(videoPath);
        }
    }
}

