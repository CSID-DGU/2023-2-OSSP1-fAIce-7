package com.flinter.android.faiceapp
import android.content.Context
import android.util.Log
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.osgi.OpenCVInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files

private const val TAG = "opencv"

class CameraViewListener(private val context: MainActivity) : CameraBridgeViewBase.CvCameraViewListener2 {

    private lateinit var rgba: Mat
    private lateinit var gray: Mat
    private lateinit var rotatedRgba: Mat
    private lateinit var rotatedGray: Mat

    // 얼굴 검출을 위한 CascadeClassifier 객체 생성
    private var faceCascade: CascadeClassifier? = null

    override fun onCameraViewStarted(width: Int, height: Int) {
        rgba = Mat(height, width, CvType.CV_8UC4)
        gray = Mat(height, width, CvType.CV_8UC1)

        // haarcascade_frontalface_default.xml 파일을 복사
        val cascadeFileDir = File(context.cacheDir, "haarcascade_frontalface_default.xml")
        if (!cascadeFileDir.exists()) {
            copyCascadeFileToCache(context, R.raw.haarcascade_frontalface_default, cascadeFileDir)
        }

        // CascadeClassifier 초기화
        faceCascade = CascadeClassifier(cascadeFileDir.absolutePath)
        if (faceCascade!!.empty()) {
            Log.e(TAG, "cascade classifier 모델 로드 실패")
        }
    }

    private fun copyCascadeFileToCache(context: Context, resourceId: Int, destination: File) {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val outputStream = FileOutputStream(destination)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()
    }

    override fun onCameraViewStopped() {
        rgba.release()
        gray.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        rgba = inputFrame.rgba()
        gray = inputFrame.gray()

        // 얼굴 검출
        val faces = MatOfRect()
        faceCascade?.detectMultiScale(
            gray, faces, 1.1, 1, 0,
            Size(absoluteFaceSize.toDouble(), absoluteFaceSize.toDouble())
        )

        // 검출된 얼굴 주위에 사각형 그리기
        val facesArray = faces.toArray()
        for (rect in facesArray) {
            Imgproc.rectangle(
                rgba,
                rect.tl(),
                rect.br(),
                Scalar(255.0, 0.0, 0.0, 255.0),
                3
            )
        }

        return rgba
    }

    companion object {
        private const val absoluteFaceSize = 0
    }
}
