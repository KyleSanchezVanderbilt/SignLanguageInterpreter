package com.example.signlanguageinterpreter;

import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.camera.view.PreviewView;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.signlanguageinterpreter.singleton.CameraXManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraXManagerSingletonTest {

    @Test
    public void testSingletonInstance() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PreviewView previewView = new PreviewView(context);
        ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();

        CameraXManager instance1 = CameraXManager.getInstance(context, previewView, cameraExecutor);
        CameraXManager instance2 = CameraXManager.getInstance(context, previewView, cameraExecutor);

        assertSame("Both instances should be the same", instance1, instance2);
    }
}
