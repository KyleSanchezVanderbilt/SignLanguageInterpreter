package com.example.signlanguageinterpreter.model;

import android.graphics.Bitmap;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.label.TensorLabel;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

public class TensorFlowModel {
    private Interpreter interpreter;
    private ImageProcessor imageProcessor;
    private List<String> labels;

    public TensorFlowModel(Interpreter interpreter, List<String> labels) {
        this.interpreter = interpreter;
        this.labels = labels;

        // Initialize the ImageProcessor with required preprocessing steps
        imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(0, 1)) // Normalization
                .build();
    }

    public List<String> classify(Bitmap bitmap, int rotation) {
        // Create a TensorImage object to hold the image data
        TensorImage tensorImage = new TensorImage(DataType.UINT8);
        tensorImage.load(bitmap);

        // Preprocess the image
        tensorImage = imageProcessor.process(tensorImage);

        // Prepare output buffer
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.UINT8);

        // Run inference
        interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());

        // Dequantize the output
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(0, 1 / 255.0f)).build();
        TensorBuffer dequantizedOutputBuffer = probabilityProcessor.process(outputBuffer);

        // Map the output probabilities to labels
        Map<String, Float> labeledProbability = new TensorLabel(labels, dequantizedOutputBuffer).getMapWithFloatValue();

        // Get the best classification result
        List<Map.Entry<String, Float>> sortedLabels = new ArrayList<>(labeledProbability.entrySet());
        Collections.sort(sortedLabels, (entry1, entry2) -> Float.compare(entry2.getValue(), entry1.getValue()));

        List<String> results = new ArrayList<>();
        for (Map.Entry<String, Float> entry : sortedLabels) {
            results.add(entry.getKey() + ": " + entry.getValue());
        }

        return results;
    }
}
