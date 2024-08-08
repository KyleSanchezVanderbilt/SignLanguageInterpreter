package com.example.signlanguageinterpreter.facade;

import android.content.Context;
import com.example.signlanguageinterpreter.builder.TensorFlowModelBuilder;
import com.example.signlanguageinterpreter.model.TensorFlowModel;
import com.example.signlanguageinterpreter.singleton.CameraXManager;
import com.example.signlanguageinterpreter.observer.Observer;
import com.example.signlanguageinterpreter.observer.Subject;
import android.graphics.Bitmap;
import androidx.camera.view.PreviewView;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;

public class SignLanguageInterpreterFacade {
    private TensorFlowModel model;
    private CameraXManager cameraManager;
    private Subject subject;

    public SignLanguageInterpreterFacade(Context context, PreviewView previewView) {
        // Initialize TensorFlow model using the builder pattern
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(context);
        try {
            MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, "detect.tflite");
            List<String> labels = FileUtil.loadLabels(context, "labels.txt");
            this.model = builder.setModelPath("detect.tflite")
                    .setNumThreads(4)
                    .setLabels(labels)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize CameraX manager using the singleton pattern
        this.cameraManager = CameraXManager.getInstance(context, previewView);

        // Initialize Subject for the observer pattern
        this.subject = new Subject();
    }

    // Start the camera using CameraX
    public void startCamera(Context context) {
        cameraManager.startCamera(context);
    }

    // Add an observer to the subject
    public void addObserver(Observer observer) {
        subject.addObserver(observer);
    }

    // Remove an observer from the subject
    public void removeObserver(Observer observer) {
        subject.removeObserver(observer);
    }

    // Classify an image and notify observers with the results
    public List<String> classifyImage(Bitmap bitmap, int rotation) {
        List<String> results = model.classify(bitmap, rotation);

        // Notify all observers with the classification result
        for (String result : results) {
            subject.notifyObservers(result);
        }

        return results;
    }
}
