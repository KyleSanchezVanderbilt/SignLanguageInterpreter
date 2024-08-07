package com.example.signlanguageinterpreter.facade;

import android.content.Context;

import com.example.signlanguageinterpreter.builder.TensorFlowModelBuilder;
import com.example.signlanguageinterpreter.composite.CompositeFrameProcessor;
import com.example.signlanguageinterpreter.composite.ProcessingComponent;
import com.example.signlanguageinterpreter.model.TensorFlowModel;
import com.example.signlanguageinterpreter.observer.Observer;
import com.example.signlanguageinterpreter.observer.Subject;
import com.example.signlanguageinterpreter.utils.Frame;

public class SignLanguageInterpreterFacade {
    private TensorFlowModel model;
    private CompositeFrameProcessor processor;
    private Subject subject;

    public SignLanguageInterpreterFacade(Context context) {
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(context);
        this.model = builder.setModelPath("model.tflite").setNumThreads(4).build();
        this.processor = new CompositeFrameProcessor();
        this.subject = new Subject();
    }

    public void addObserver(Observer observer) {
        subject.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        subject.removeObserver(observer);
    }

    public void addProcessingComponent(ProcessingComponent component) {
        processor.add(component);
    }

    public void interpretGesture(Frame frame) {
        processor.process(frame);
        float[][] input = frame.toInputArray(); // Convert frame to model input format
        float[][] output = model.interpretGesture(input);
        String result = output[0][0] > 0.5 ? "Hello" : "Goodbye";
        subject.notifyObservers(result);
    }
}
