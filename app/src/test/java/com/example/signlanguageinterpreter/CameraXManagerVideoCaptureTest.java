package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.camera.view.PreviewView;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.signlanguageinterpreter.singleton.CameraXManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraXManagerVideoCaptureTest {

    @Test
    public void testCaptureVideo() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PreviewView previewView = new PreviewView(context);
        ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
        CameraXManager manager = CameraXManager.getInstance(context, previewView);

        try {
            manager.captureVideo(context);
        } catch (Exception e) {
            fail("Video capture should not throw an exception");
        }
    }
}
