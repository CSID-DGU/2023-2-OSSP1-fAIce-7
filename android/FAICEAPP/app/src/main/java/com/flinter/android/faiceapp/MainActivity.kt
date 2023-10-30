package com.flinter.android.faiceapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var cameraView: CameraBridgeViewBase

    private lateinit var selectedMinimum: String
    private lateinit var selectedEmotion: String
    private lateinit var emotionValue: String


    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    private val loaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    cameraView.enableView()
                }
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedMinimum = intent.getStringExtra("selectedMinimum") ?: "1"
        selectedEmotion = intent.getStringExtra("selectedEmotion")  ?: "happiness"
        emotionValue = intent.getStringExtra("emotionValue") ?: "30"

        Log.d(TAG, "$selectedMinimum $selectedEmotion $emotionValue")

        cameraView = findViewById(R.id.camera_view)
//        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT) // 전면 카메라
        cameraView.visibility = CameraBridgeViewBase.VISIBLE
        cameraView.setCvCameraViewListener(CameraViewListener(this,
            selectedMinimum,
            selectedEmotion,
            emotionValue))

        // 카메라 권한 확인 및 요청
        if (hasCameraPermission()) {
            cameraView.setCameraPermissionGranted()
        } else {
            requestCameraPermission()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView.setCameraPermissionGranted()
                } else {
                    // Handle permission denied
                    Log.d(TAG, "카메라 퍼미션 거절 됨")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()
        OpenCVLoader.initDebug()
        loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    override fun onPause() {
        super.onPause()
        if (cameraView.isEnabled) {
            cameraView.disableView()
        }
    }
}
