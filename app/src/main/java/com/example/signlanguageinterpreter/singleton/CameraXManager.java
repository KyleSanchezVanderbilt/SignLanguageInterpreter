package com.example.signlanguageinterpreter.singleton;


import static androidx.compose.material3.ShapesKt.start;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;


public class CameraXManager {
    private static CameraXManager instance;
    private ProcessCameraProvider cameraProvider;
    private PreviewView previewView;
    public ImageCapture imageCapture;
    private VideoCapture<Recorder> videoCapture;
    private Recording recording;
    private OnPhotoSavedCallback onPhotoSavedCallback;
    private OnVideoSavedCallback onVideoSavedCallback;
    private static ExecutorService cameraExecutor;
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

    public static CameraXManager getInstance(Context context, PreviewView previewView) {
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
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        String msg = "Photo capture succeeded: " + output.getSavedUri();
                        if (onPhotoSavedCallback != null) {
                            onPhotoSavedCallback.onPhotoSaved(Objects.requireNonNull(output.getSavedUri()).toString());
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }
                }
        );
    }

    public void captureVideo(Context context) {
        if (videoCapture == null) return;

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

        recording = videoCapture.getOutput().prepareRecording(context, mediaStoreOutputOptions).apply {
            if (PermissionChecker.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED) {
                withAudioEnabled();
            }
        }
            .start(ContextCompat.getMainExecutor(context), new VideoRecordEvent.Listener() {
            @Override
            public void onEvent(VideoRecordEvent event) {
                if (event instanceof VideoRecordEvent.Start) {
                    Log.d(TAG, "Video recording started");
                } else if (event instanceof VideoRecordEvent.Finalize) {
                    if (!((VideoRecordEvent.Finalize) event).hasError()) {
                        String msg = "Video capture succeeded: " +
                                ((VideoRecordEvent.Finalize) event).getOutputResults().getOutputUri();
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    } else {
                        recording.close();
                        recording = null;
                        Log.e(TAG, "Video capture ends with error: " +
                                ((VideoRecordEvent.Finalize) event).getError());
                    }
                }
            }
        });
    }
    // Callback interfaces for photo and video save events
    public interface OnPhotoSavedCallback {
        void onPhotoSaved(String photoPath);
    }

    public interface OnVideoSavedCallback {
        void onVideoSaved(String videoPath);
    }

    public void setOnPhotoSavedCallback(OnPhotoSavedCallback callback) {
        this.onPhotoSavedCallback = callback;
    }

    public void setOnVideoSavedCallback(OnVideoSavedCallback callback) {
        this.onVideoSavedCallback = callback;
    }
}




