package com.example.signlanguageinterpreter;

import com.example.signlanguageinterpreter.builder.TensorFlowModelBuilder;
import com.example.signlanguageinterpreter.model.TensorFlowModel;
import org.junit.Test;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TensorFlowModelLabelsTest {

    @Test
    public void testSetLabels() {
        Context mockContext = null;
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(mockContext);
        List<String> labels = Arrays.asList("Label1", "Label2", "Label3");

        builder.setLabels(labels);
        TensorFlowModel model = builder.build();

        assertNotNull(model.getLabels());
        assertEquals(labels, model.getLabels());
    }
}
