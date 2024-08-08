package com.example.signlanguageinterpreter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.signlanguageinterpreter.databinding.ActivityMainBinding
import com.example.signlanguageinterpreter.facade.SignLanguageInterpreterFacade
import com.example.signlanguageinterpreter.observer.LogObserver
import com.example.signlanguageinterpreter.observer.UIObserver
import com.example.signlanguageinterpreter.singleton.CameraXManager
import com.example.signlanguageinterpreter.ui.theme.SignLanguageInterpreterTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    // View binding instance to access UI elements
    private lateinit var viewBinding: ActivityMainBinding
    // Facade instance for sign language interpretation
    private lateinit var interpreterFacade: SignLanguageInterpreterFacade
    // Image capture use case for taking photos
    private var imageCapture: ImageCapture? = null
    // Singleton instance for managing CameraX operations
    private lateinit var cameraXManager: CameraXManager
    // Executor service for handling camera operations in a background thread
    private lateinit var cameraExecutor: ExecutorService

    // Activity result launcher for requesting multiple permissions
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                // Check if any of the required permissions were denied
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                // Show a toast message if permissions were denied
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Start the camera if permissions were granted
                startCamera()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Inflate the layout using view binding
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Initialize the SignLanguageInterpreterFacade with the context and preview view
        val previewView = viewBinding.previewView
        interpreterFacade = SignLanguageInterpreterFacade(this, previewView)

        // Initialize CameraXManager with the context, preview view, and executor
        cameraXManager = CameraXManager.getInstance(this, previewView, cameraExecutor)

        // Check if all permissions are granted, if not, request permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        setContent{

            SignLanguageInterpreterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CameraPreview(
                        modifier = Modifier.padding(innerPadding),
                        context = this,
                        previewView = PreviewView(this),
                        cameraExecutor = cameraExecutor
                    )
                }
            }
        }



        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        // Adding observers to the interpreter facade
        val resultText = findViewById<TextView>(R.id.result_text)
        val uiObserver = UIObserver(resultText)
        val logObserver = LogObserver()
        interpreterFacade.addObserver(uiObserver)
        interpreterFacade.addObserver(logObserver)
    }

    // Function to take a photo using CameraXManager
    private fun takePhoto() {
        cameraXManager.takePhoto(this)
    }

    // Function to start the camera using CameraXManager
    private fun startCamera() {
        cameraXManager.startCamera(this)
    }

    // Function to request permissions using the activity result launcher
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }






    // Function to check if all required permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down the camera executor service
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        // List of required permissions
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                // Add WRITE_EXTERNAL_STORAGE permission for Android versions <= P
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
