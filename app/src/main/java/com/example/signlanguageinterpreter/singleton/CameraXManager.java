package com.example.signlanguageinterpreter.singleton;

import static androidx.compose.material3.ShapesKt.start;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.LifecycleOwner;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class CameraXManager {
    private static CameraXManager instance;
    private ProcessCameraProvider cameraProvider;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private VideoCapture<Recorder> videoCapture;
    private Recording recording;
    private ExecutorService cameraExecutor;
    private static final String TAG = "CameraXManager";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    private CameraXManager(Context context, PreviewView previewView, ExecutorService cameraExecutor) {
        this.previewView = previewView;
        this.cameraExecutor = cameraExecutor;
        try {
            cameraProvider = ProcessCameraProvider.getInstance(context).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static CameraXManager getInstance(Context context, PreviewView previewView, ExecutorService cameraExecutor) {
        if (instance == null) {
            instance = new CameraXManager(context, previewView, cameraExecutor);
        }
        return instance;
    }

    public void startCamera(Context context) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().build();

        Recorder recorder = new Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build();
        videoCapture = VideoCapture.withOutput(recorder);

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    public void takePhoto(Context context) {
        ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null) return;

        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults output) {
                        String msg = "Photo capture succeeded: " + output.getSavedUri();
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }
                }
        );
    }

    public void captureVideo(Context context) {
        VideoCapture<Recorder> videoCapture = this.videoCapture;
        if (videoCapture == null) return;

        // Disable the UI until the request action is completed by CameraX
        // Re-enable UI inside our registered VideoRecordListener
        // Example: viewBinding.videoCaptureButton.isEnabled = false;

        if (recording != null) {
            // Stop the current recording session.
            recording.stop();
            recording = null;
            return;
        }

        // Create and start a new recording session
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video");
        }

        MediaStoreOutputOptions mediaStoreOutputOptions = new MediaStoreOutputOptions.Builder(
                context.getContentResolver(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues)
                .build();

        recording = videoCapture.getOutput()
                .prepareRecording(context, mediaStoreOutputOptions)
                .apply {
            if (PermissionChecker.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED) {
                withAudioEnabled();
            }
        }
                .start(ContextCompat.getMainExecutor(context), videoRecordEvent -> {
            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                // Update the UI to reflect the start of the recording
                // Example: viewBinding.videoCaptureButton.setText(R.string.stop_capture);
                // Example: viewBinding.videoCaptureButton.setEnabled(true);
            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                    String msg = "Video capture succeeded: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, msg);
                } else {
                    recording.close();
                    recording = null;
                    Log.e(TAG, "Video capture ends with error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError());
                }
                // Update the UI to reflect the stop of the recording
                // Example: viewBinding.videoCaptureButton.setText(R.string.start_capture);
                // Example: viewBinding.videoCaptureButton.setEnabled(true);
            }
        });
    }
}




