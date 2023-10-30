
package com.flinter.android.faiceapp
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.osgi.OpenCVInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files

private const val TAG = "opencv"



class CameraViewListener(private val context: MainActivity,
                         private val selectedMinimum: String,
                         private val selectedEmotion: String,
                         private val emotionValue: String) : CameraBridgeViewBase.CvCameraViewListener2 {

    private var webSocketListener: WebsocketListener? = null
    private var lastSendTime: Long = 0
    private lateinit var frame: Mat
    override fun onCameraViewStarted(width: Int, height: Int) {
        frame = Mat(height, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        frame.release()
    }

    // Existing initial content...

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val frame = inputFrame?.rgba() ?: return Mat()

        // Convert frame to base64 string
        val bitmap = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(frame, bitmap)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        if (System.currentTimeMillis() - lastSendTime > 1000) {
            // Initialize websocket listener if not already done
            if (webSocketListener == null) {
                webSocketListener = WebsocketListener(
                    type = "start_stream",
                    selectedEmotion = selectedEmotion,
                    minPeople = selectedMinimum.toInt(),
                    emotionThreshold = emotionValue.toDouble(),
                    frameData = encodedImage
                )
            }

            // Send frame to server
            webSocketListener?.sendDataToServer()
            Log.d(TAG, "Sent frame to server")
            lastSendTime = System.currentTimeMillis()
        }


        // Process the received data from the server
        val responseDataStr = webSocketListener?.receivedData ?: ""
        if (responseDataStr.isNotEmpty()) {
            val responseData = JSONObject(responseDataStr)
            val detectedEmotions = responseData.getJSONArray("detected_emotions")
            Log.d(TAG, "Received response from server: ${detectedEmotions.length()}, $detectedEmotions")
            try {
                for (i in 0 until detectedEmotions.length()) {
                    val emotionData = detectedEmotions.getJSONObject(i)
                    val coordinates = emotionData.getJSONObject("coordinates")
                    val x = coordinates.getDouble("x")
                    val y = coordinates.getDouble("y")
                    val w = coordinates.getDouble("w")
                    val h = coordinates.getDouble("h")
                    val emotionLabel = emotionData.getString("emotion_label")
                    val emotionProbability = emotionData.getDouble("emotion_probability")
                    val colorData = emotionData.getJSONArray("color")
                    val color = Scalar(colorData.getDouble(0), colorData.getDouble(1), colorData.getDouble(2))

                    // Draw rectangle around the detected face
                    Imgproc.rectangle(frame, Point(x, y), Point(x + w, y + h), color, 2)

                    // Display the emotion label and probability
                    val label = "$emotionLabel: ${(emotionProbability * 100).toInt()}%"
                    Imgproc.putText(frame, label, Point(x, y - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)
                }
            } catch (e: Exception) {
                Log.e(TAG, "이미지 가공 실패" + e.localizedMessage)
                return frame
            }
        } else {
            Log.e(TAG, "json 오류")
        }

        return frame
    }
}
