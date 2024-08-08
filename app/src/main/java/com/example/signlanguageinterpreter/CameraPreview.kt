package com.example.signlanguageinterpreter.presentation

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.signlanguageinterpreter.singleton.CameraXManager

@Composable
fun CameraPreview(modifier: Modifier, context: Context, previewView: PreviewView) {
    val cameraManager = CameraXManager.getInstance(context, previewView)
    AndroidView(factory = { previewView }, modifier = modifier)
    cameraManager.startCamera(context)
}
