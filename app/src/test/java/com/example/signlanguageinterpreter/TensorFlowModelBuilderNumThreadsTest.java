package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import com.example.signlanguageinterpreter.builder.TensorFlowModelBuilder;

public class TensorFlowModelBuilderNumThreadsTest {

    @Test
    public void testSetNumThreads() {
        Context mockContext = null; // Assuming context is not needed for this simple test
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(mockContext);
        int numThreads = 4;
        builder.setNumThreads(numThreads);
        assertEquals(numThreads, builder.build().getNumThreads());
    }
}
