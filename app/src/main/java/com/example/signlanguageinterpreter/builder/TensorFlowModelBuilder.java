package com.example.signlanguageinterpreter.builder;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import com.example.signlanguageinterpreter.model.TensorFlowModel;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TensorFlowModelBuilder {
    private Interpreter.Options options;
    private String modelPath;
    private Context context;

    public TensorFlowModelBuilder(Context context) {
        this.context = context;
        this.options = new Interpreter.Options();
    }

    public TensorFlowModelBuilder setNumThreads(int numThreads) {
        this.options.setNumThreads(numThreads);
        return this;
    }

    public TensorFlowModelBuilder setModelPath(String modelPath) {
        this.modelPath = modelPath;
        return this;
    }

    public TensorFlowModel build() {
        Interpreter interpreter = new Interpreter(loadModelFile(modelPath), options);
        return new TensorFlowModel(interpreter);
    }

    private MappedByteBuffer loadModelFile(String modelPath) {
        try (AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            throw new RuntimeException("Error loading model file", e);
        }
    }
}
