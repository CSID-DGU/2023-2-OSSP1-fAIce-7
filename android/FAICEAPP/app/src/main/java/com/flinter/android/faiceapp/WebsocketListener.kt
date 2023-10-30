
package com.flinter.android.faiceapp

import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

private const val TAG = "SERVER"

class WebsocketListener(
    private val type: String = "start_stream",
    private val selectedEmotion: String,
    private val minPeople: Int,
    private val emotionThreshold: Double,
    private val frameData: String
) : WebSocketListener() {

//    private val webSocketUrl = "ws://0.0.0.0:8000/ws/emotion/" // 가상 디바이스용 주소
    // 실제 디바이스용 주소 - 이 코드를 실행할 wifi ip 주소를 입력할 것 + 디바이스 wifi 와 같은 wifi로 연결할 것
    private val webSocketUrl = "ws://:8000/ws/emotion/" // 여기에

    private var webSocket: WebSocket? = null
    var receivedData: String? = null
//    var receivedData: ByteString? = null

    init {
        val client = OkHttpClient()
        val request = Request.Builder().url(webSocketUrl).build()
        webSocket = client.newWebSocket(request, this)
    }

    fun sendDataToServer() {
        val data = JSONObject().apply {
            put("type", type)
            put("selected_emotion", selectedEmotion)
            put("min_people", minPeople)
            put("emotion_threshold", emotionThreshold)
            put("frameData", frameData)
        }
        webSocket?.send(data.toString())
        Log.d(TAG, "Sent data to server: $data")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        // TODO: Handle the received data here
        Log.d(TAG, "Received data from server: $text")
        receivedData = text
    }
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "Websocket Error: " + t.localizedMessage)
    }

    fun closeConnection() {
        webSocket?.close(1000, "Goodbye!")
    }
}
