package com.example.signlanguageinterpreter.composite;

import com.example.signlanguageinterpreter.utils.Frame;

import java.util.ArrayList;
import java.util.List;

public class CompositeFrameProcessor implements ProcessingComponent {
    private List<ProcessingComponent> children = new ArrayList<>();

    public void add(ProcessingComponent component) {
        children.add(component);
    }

    public void remove(ProcessingComponent component) {
        children.remove(component);
    }

    @Override
    public void process(Frame frame) {
        for (ProcessingComponent child : children) {
            child.process(frame);
        }
    }
}
