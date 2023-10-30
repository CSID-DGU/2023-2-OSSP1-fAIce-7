package com.flinter.android.faiceapp
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val spinnerMinimum: Spinner = findViewById(R.id.spinnerMinimum) // "최소인원" 스피너 추가
        val spinnerEmotion: Spinner = findViewById(R.id.spinnerEmotion)
        val editTextEmotionValue: EditText = findViewById(R.id.editTextEmotionValue)
        val confirmButton: Button = findViewById(R.id.confirmButton)

        // "최소인원" 콤보박스 설정
        val minimumValues = (1..9).map { it.toString() }
        val minimumAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minimumValues)
        minimumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMinimum.adapter = minimumAdapter

        // "감정 선택" 콤보박스 설정
        val emotionValues = arrayOf("neutral", "happiness", "surprise", "sadness", "anger", "disgust", "fear")
        val emotionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emotionValues)
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEmotion.adapter = emotionAdapter

        // 확인 버튼 클릭 이벤트 처리
        confirmButton.setOnClickListener {
            val selectedMinimum = spinnerMinimum.selectedItem.toString()
            val selectedEmotion = spinnerEmotion.selectedItem.toString()
            val emotionValue = editTextEmotionValue.text.toString()

            // MainActivity로 선택한 값들을 전달
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("minimum", selectedMinimum)
            intent.putExtra("emotion", selectedEmotion)
            intent.putExtra("emotionValue", emotionValue)
            startActivity(intent)
        }
    }
}
