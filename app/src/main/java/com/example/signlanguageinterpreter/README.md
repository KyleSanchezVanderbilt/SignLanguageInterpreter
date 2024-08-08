# Sign Language Interpreter App

This project is a sign language interpreter app built using Java and Kotlin. It leverages 
TensorFlow Lite for sign language detection and employs several 
design patterns to ensure a robust and maintainable codebase. The app utilizes the Builder, 
Observer, Singleton, and Facade design patterns to achieve its functionality.

## Detailed Explanation
The Sign Language Interpreter App is organized in a way that each component has a specific role, 
making the system modular and maintainable. The process begins in the MainActivity.kt, which serves 
as the entry point of the application. Upon launching the app, MainActivity initializes the 
SignLanguageInterpreterFacade, which acts as a simplified interface to the complex subsystems of 
the app. This facade initializes the CameraXManager, a singleton class responsible for managing 
the camera lifecycle and operations, and the TensorFlowModel, which handles the TensorFlow Lite 
model for gesture recognition.

CameraXManager sets up the camera using the Android CameraX library and ensures that the camera 
preview is displayed on the PreviewView. When the camera captures frames, the facade uses the 
TensorFlowModel to classify these frames. The model processes each frame, runs inference to detect 
gestures, and produces classification results.

Observers, such as UIObserver and LogObserver, are added to the SignLanguageInterpreterFacade to 
listen for classification results. These observers update the UI and log the results whenever a 
new gesture is recognized. This interaction pattern ensures that the application components remain 
loosely coupled, making the system flexible and easier to maintain.

In summary, the app flow starts from MainActivity, initializes the facade, which in turn sets up the camera and the TensorFlow model. Observers are notified of classification results, updating the UI and logs, ensuring real-time feedback and a seamless user experience.

## Purpose

The purpose of this app is to interpret sign language gestures using a pre-trained TensorFlow Lite 
model. The app captures gestures through the device's camera, processes the frames using OpenCV, 
and interprets the gestures to display the corresponding text.

## Design Patterns

### 1. Builder Pattern

**Purpose**: To construct and configure the TensorFlow model.

**Reason for Choice**: The Builder pattern is a creational design pattern that allows for the 
incremental construction of complex objects. This is particularly useful for configuring the 
TensorFlow model, where multiple parameters need to be set before the model can be used.

The `TensorFlowModelBuilder` class is responsible for setting up and configuring the TensorFlow 
Lite model. This pattern ensures that the model configuration is flexible and can be easily 
modified.

**Code Example**:
```java
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
```

### 2. Observer Pattern

**Purpose**: To update the UI and other components in response to gesture interpretation results.

**Reason for Choice**: The Observer pattern is useful for implementing distributed event handling 
systems. It allows the app to update various components (like the UI) whenever new data 
(gesture interpretation) is available, without tightly coupling these components.

The `Observer` interface and `Subject` class are used to implement the `observer` pattern.
Observers such as `UIObserver` and `LogObserver` are notified whenever a gesture is interpreted.

**Code Example**:
```java
public interface Observer {
    void update(String message);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

public class UIObserver implements Observer {
    private TextView textView;

    public UIObserver(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void update(String message) {
        textView.setText("Interpreted gesture: " + message);
    }
}

public class LogObserver implements Observer {
    private static final String TAG = "LogObserver";

    @Override
    public void update(String message) {
        Log.d(TAG, "Interpreted gesture: " + message);
    }
}
```

### 3. Singleton Pattern

**Purpose**: To ensure a single instance of the CameraXManager for managing the camera 
lifecycle and operations.

**Reason for Choice**: The Singleton pattern ensures that only one instance of the CameraXManager 
exists, which manages the camera lifecycle and operations, providing a centralized point of control.


The `CompositeFrameProcessor` class and its related components are used to create
a flexible and modular image processing pipeline.

**Code Example**:
```java
public interface ProcessingComponent {
    void process(Frame frame);
}

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

public class ColourThresholdFrameProcessor implements ProcessingComponent {
    @Override
    public void process(Frame frame) {
        // Implement color threshold processing logic
    }
}

public class CannyEdgeFrameProcessor implements ProcessingComponent {
    @Override
    public void process(Frame frame) {
        // Implement Canny edge detection logic
    }
}
```
### 4. Facade Pattern

**Purpose**: To simplify interactions with the complex subsystems like TensorFlow and OpenCV.

**Reason for Choice**: The Facade pattern provides a simplified interface to a complex subsystem. 
This makes it easier for clients to interact with the system without needing to understand its 
complexity.

The `SignLanguageInterpreterFacade` class provides a simplified interface for interacting with
the TensorFlow model and the image processing components.

**Code Example**:
```java
public class SignLanguageInterpreterFacade {
    private TensorFlowModel model;
    private CompositeFrameProcessor processor;
    private Subject subject;

    public SignLanguageInterpreterFacade(Context context) {
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(context);
        this.model = builder.setModelPath("detect.tflite").setNumThreads(4).build();
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

```

**License**
This project is licensed under the MIT License.

**Acknowledgments**
Special thanks to all contributors and the open-source community for providing valuable resources and tools.

