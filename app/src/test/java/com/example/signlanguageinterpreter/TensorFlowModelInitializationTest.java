package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.signlanguageinterpreter.model.TensorFlowModel;

import org.tensorflow.lite.Interpreter;

import java.util.Arrays;
import java.util.List;

public class TensorFlowModelInitializationTest {

    @Test
    public void testTensorFlowModelInitialization() {
        Interpreter interpreter = null; // Mock interpreter instance
        List<String> labels = Arrays.asList("label1", "label2"); // Mock labels
        TensorFlowModel model = new TensorFlowModel(interpreter, labels, 4); // Pass labels and threads
        assertNotNull(model);
    }
}
