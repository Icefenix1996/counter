package com.example.pushupcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Переменная для хранения количества отжиманий
    private var pushUpCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем ссылки на элементы интерфейса
        val counterText: TextView = findViewById(R.id.counterText)
        val pushupButton: Button = findViewById(R.id.pushupButton)

        // Обработка нажатия на кнопку
        pushupButton.setOnClickListener {
            pushUpCount++
            counterText.text = pushUpCount.toString()
        }
    }
}

