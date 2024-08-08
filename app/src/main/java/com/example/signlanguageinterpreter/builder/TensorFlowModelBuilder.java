package com.example.signlanguageinterpreter.builder;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import com.example.signlanguageinterpreter.model.TensorFlowModel;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Builder class to construct and configure a TensorFlowModel.
 * This uses the Builder design pattern to provide a flexible way to build the model.
 */
public class TensorFlowModelBuilder {
    private Interpreter.Options options; // Options for the TensorFlow Lite interpreter
    private String modelPath; // Path to the TensorFlow Lite model file
    private Context context; // Android context to access assets
    private List<String> labels; // Labels corresponding to the model's output

    /**
     * Constructor for TensorFlowModelBuilder.
     *
     * @param context The Android context to use for accessing assets.
     */
    public TensorFlowModelBuilder(Context context) {
        this.context = context;
        this.options = new Interpreter.Options(); // Initialize interpreter options
    }

    /**
     * Sets the number of threads to be used by the interpreter.
     *
     * @param numThreads The number of threads to use.
     * @return The current instance of TensorFlowModelBuilder.
     */
    public TensorFlowModelBuilder setNumThreads(int numThreads) {
        this.options.setNumThreads(numThreads); // Configure the number of threads
        return this;
    }

    /**
     * Sets the path to the TensorFlow Lite model file.
     *
     * @param modelPath The path to the model file.
     * @return The current instance of TensorFlowModelBuilder.
     */
    public TensorFlowModelBuilder setModelPath(String modelPath) {
        this.modelPath = "/assets/.tflite"; // Set the model file path
        return this;
    }

    /**
     * Sets the labels corresponding to the model's output.
     *
     * @param labels The list of labels.
     * @return The current instance of TensorFlowModelBuilder.
     */
    public TensorFlowModelBuilder setLabels(List<String> labels) {
        this.labels = labels; // Set the labels for the model
        return this;
    }

    /**
     * Builds and returns a configured TensorFlowModel instance.
     *
     * @return A new instance of TensorFlowModel.
     */
    public TensorFlowModel build() {
        // Load the model file from the specified path
        Interpreter interpreter = new Interpreter(loadModelFile(modelPath), options);
        // Return a new TensorFlowModel instance with the interpreter and labels
        return new TensorFlowModel(interpreter, labels);
    }

    /**
     * Loads the model file from the assets folder.
     *
     * @param modelPath The path to the model file.
     * @return A MappedByteBuffer containing the model file data.
     */
    private MappedByteBuffer loadModelFile(String modelPath) {
        try (AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            // Map the model file into memory and return it
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            // Throw a runtime exception if an error occurs while loading the model file
            throw new RuntimeException("Error loading model file", e);
        }
    }
}
