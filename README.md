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

In summary, the app flow starts from MainActivity, initializes the facade, which in turn sets up 
the camera and the TensorFlow model. Observers are notified of classification results, updating 
the UI and logs, ensuring real-time feedback and a seamless user experience.

## Purpose

The purpose of this app is to interpret sign language gestures using a pre-trained TensorFlow Lite 
model. The app captures gestures through the device's camera, processes the frames using the observer, 
to interpret the gestures to display the corresponding text.

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
/**
 * Builder class to construct and configure a TensorFlowModel.
 * This uses the Builder design pattern to provide a flexible way to build the model.
 */
public class TensorFlowModelBuilder {
    private Interpreter.Options options; // Options for the TensorFlow Lite interpreter
    private String modelPath; // Path to the TensorFlow Lite model file
    private Context context; // Android context to access assets
    private List<String> labels; // Labels corresponding to the model's output
    private int numThreads; // Number of threads for the interpreter

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
     * Gets the number of threads set for the interpreter.
     *
     * @return The number of threads.
     */
    public int getNumThreads() {
        return this.numThreads;
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
        return new TensorFlowModel(interpreter, labels, numThreads);
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

/**
 * The Subject class maintains a list of observers and provides methods to add, remove,
 * and notify these observers. It implements the Observer Design Pattern to allow
 * objects to be notified of changes in the subject's state.
 */
public class Subject {
    // A list to hold all the observers that are observing this subject
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    public void addObserver(Observer observer) {
        observers.add(observer); // Add the observer to the list
    }

    /**
     * Returns the list of observers.
     *
     * @return The list of observers.
     */
    public Observer getObserver() {
        return (Observer) observers;
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer The observer to be removed.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer); // Remove the observer from the list
    }

    /**
     * Notifies all observers by calling their update method with the given message.
     *
     * @param message The message to be sent to all observers.
     */
    public void notifyObservers(String message) {
        // Iterate through all observers and call their update method with the message
        for (Observer observer : observers) {
            observer.update(message);
        }
    }


```

### 3. Singleton Pattern

**Purpose**: To ensure a single instance of the CameraXManager for managing the camera 
lifecycle and operations.

**Reason for Choice**: The Singleton pattern ensures that only one instance of the CameraXManager 
exists, which manages the camera lifecycle and operations, providing a centralized point of control.


The `CameraXManager` class and its related components are used to create
a flexible and modular image processing pipeline.

**Code Example**:
```java

//In MainActivity
// Initialize CameraXManager with the context, preview view, and executor
        cameraXManager = CameraXManager.getInstance(this, previewView, cameraExecutor)

```
### 4. Facade Pattern

**Purpose**: To simplify interactions with the complex subsystems like TensorFlow

**Reason for Choice**: The Facade pattern provides a simplified interface to a complex subsystem. 
This makes it easier for clients to interact with the system without needing to understand its 
complexity.

The `SignLanguageInterpreterFacade` class provides a simplified interface for interacting with
the TensorFlow model and the image processing components.

**Code Example**:
```java

public SignLanguageInterpreterFacade(Context context, PreviewView previewView) {
        // Initialize TensorFlow model using the builder pattern
        TensorFlowModelBuilder builder = new TensorFlowModelBuilder(context);
        try {
            MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, "detect.tflite");
            List<String> labels = FileUtil.loadLabels(context, "labels.txt");
            this.model = builder.setModelPath("detect.tflite")
                    .setNumThreads(4)
                    .setLabels(labels)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }


```

**License**
This project is licensed under the MIT License.

**Acknowledgments**
Special thanks to all contributors and the open-source community for providing valuable resources and tools.
