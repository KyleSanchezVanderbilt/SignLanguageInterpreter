package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import com.example.signlanguageinterpreter.builder.TensorFlowModelBuilder;

public class TensorFlowModelBuilderTest {

    @Test
    public void testSetModelPath() {
        Context mockContext = null; // Assuming context is not needed for this simple test
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(mockContext);
        String modelPath = "test_model.tflite";
        builder.setModelPath(modelPath);
        assertEquals(modelPath, builder.build().getModelPath());
    }
}
