package com.flinter.android.faiceapp

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.opencv.core.Mat
import retrofit2.http.Field
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private val TAG = "SERVER"

interface ConnectToServer {
    @Multipart
    @POST("/") // Django 서버의 API 엔드포인트 URL로 대체해야 합니다.
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("selected_emotion") selectedEmotion: String,
        @Part("min_people") minPeople: Int,
        @Part("emotion_threshold") emotionThreshold: Float
    ): Call<ResponseBody>
}

fun sendRgbaDataToDjangoServer(rgba: Mat) {
    // Django 서버의 base URL 설정
    val baseUrl = "http://127.0.0.1:8000"

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(ConnectToServer::class.java)

    // 이미지 데이터를 바이트 배열로 변환
    val rgbaByteArray = MatToByteArrayConverter.matToByteArray(rgba)

    // 이미지 데이터를 RequestBody로 변환
    val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), rgbaByteArray)

    val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", imageRequestBody)

    val selectedEmotion = "웃을"
    val minPeople = 1
    val emotionThreshold = 20.0f

    val call = service.uploadImage(imagePart, selectedEmotion, minPeople, emotionThreshold)
    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                // 성공적으로 서버로 데이터가 업로드되었습니다.
                // response.body()를 사용하여 서버에서 받은 응답을 처리할 수 있습니다.
            } else {
                // 서버로의 업로드에 실패하였거나 서버에서 오류 응답을 반환했습니다.
                Log.e(TAG, "-------서버 업로드 실패-------")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            // 네트워크 오류 또는 예외 처리
            Log.e(TAG, "-------네트워크 연결 실패-------")
        }
    })
}

object MatToByteArrayConverter {
    fun matToByteArray(mat: Mat): ByteArray {
        val data = ByteArray(mat.total().toInt() * mat.channels())
        mat.get(0, 0, data)
        return data
    }
}
