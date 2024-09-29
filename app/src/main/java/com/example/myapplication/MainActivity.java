package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Переменная для хранения общего количества отжиманий
    private int totalCounter = 0;

    // Элементы интерфейса
    private TextView tvCounter;  // Поле, где отображается текущее количество отжиманий
    private EditText etInput;    // Поле для ввода количества отжиманий

    // FirebaseHelper для работы с Firebase и локальными данными
    private FirebaseHelper firebaseHelper;

    // GoogleSignInHelper для работы с авторизацией через Google
    private GoogleSignInHelper googleSignInHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Устанавливаем макет активности

        // Инициализация элементов интерфейса
        tvCounter = findViewById(R.id.tvCounter);  // TextView для отображения счётчика
        etInput = findViewById(R.id.etInput);  // EditText для ввода отжиманий
        Button btnAdd = findViewById(R.id.btnAdd);  // Кнопка для добавления отжиманий
        Button btnReset = findViewById(R.id.btnReset);  // Кнопка для сброса счётчика
        Button googleSignInButton = findViewById(R.id.googleSignInButton);  // Кнопка для входа через Google
        Button btnHistory = findViewById(R.id.btnHistory);  // Кнопка для открытия истории

        // Инициализируем FirebaseHelper
        firebaseHelper = new FirebaseHelper(this);

        // Инициализируем GoogleSignInHelper
        googleSignInHelper = new GoogleSignInHelper(this);

        // Загрузка локальных данных при старте приложения
        totalCounter = firebaseHelper.loadCounterLocally();  // Загружаем значение из SharedPreferences
        tvCounter.setText(String.valueOf(totalCounter));  // Обновляем экран счётчиком

        // Загрузка данных из Firebase (асинхронно)
        firebaseHelper.loadCounterFromFirebase(new FirebaseHelper.CounterCallback() {
            @Override
            public void onCallback(int value) {
                totalCounter = value;  // Обновляем локальный счётчик значением из Firebase
                tvCounter.setText(String.valueOf(totalCounter));  // Обновляем экран
            }
        });

        // Обработка нажатия на кнопку добавления отжиманий
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем введённое значение
                String inputText = etInput.getText().toString();
                if (!inputText.isEmpty()) {  // Проверяем, не пустое ли поле
                    int inputCount = Integer.parseInt(inputText);  // Преобразуем строку в число
                    totalCounter += inputCount;  // Добавляем это число к счётчику
                    tvCounter.setText(String.valueOf(totalCounter));  // Обновляем отображение счётчика на экране

                    // Сохраняем обновленные данные локально и в Firebase
                    firebaseHelper.saveCounterLocally(totalCounter);  // Локальное сохранение
                    firebaseHelper.saveCounterToFirebase(totalCounter);  // Сохранение в Firebase

                    // Сохранение отжиманий по дням
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    firebaseHelper.savePushupsForDay(currentDate, inputCount);  // Сохраняем отжимания за день

                    etInput.setText("");  // Очищаем поле ввода после добавления
                } else {
                    // Показываем сообщение, если поле ввода пустое
                    Toast.makeText(MainActivity.this, "Введите количество отжиманий", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Обработка нажатия на кнопку сброса счётчика
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalCounter = 0;  // Сбрасываем счётчик на 0
                tvCounter.setText(String.valueOf(totalCounter));  // Обновляем экран

                // Сохраняем сброшенное значение локально и в Firebase
                firebaseHelper.saveCounterLocally(totalCounter);  // Локальное сохранение
                firebaseHelper.saveCounterToFirebase(totalCounter);  // Сохранение в Firebase
            }
        });

        // Обработка нажатия на кнопку входа через Google
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInHelper.signIn();  // Запуск процесса авторизации через Google
            }
        });

        // Обработка нажатия на кнопку История
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на экран с историей отжиманий
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    // Обрабатываем результат авторизации через Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSignInHelper.handleSignInResult(requestCode, data);  // Передаем результат в GoogleSignInHelper
    }
}
