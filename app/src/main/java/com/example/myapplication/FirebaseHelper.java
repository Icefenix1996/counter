package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    private FirebaseDatabase firebaseDatabase;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "PushupPrefs";
    private static final String COUNTER_KEY = "totalCounter";

    public FirebaseHelper(Context context) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Сохранение общего количества отжиманий в Firebase
    public void saveCounterToFirebase(int totalCounter) {
        DatabaseReference counterRef = firebaseDatabase.getReference("totalCounter");
        counterRef.setValue(totalCounter);
    }

    // Загрузка общего количества отжиманий из Firebase
    public void loadCounterFromFirebase(final CounterCallback callback) {
        DatabaseReference counterRef = firebaseDatabase.getReference("totalCounter");
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                if (value != null) {
                    callback.onCallback(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Ошибка чтения данных", error.toException());
            }
        });
    }

    // Сохранение отжиманий за день
    public void savePushupsForDay(String date, int count) {
        DatabaseReference dayRef = firebaseDatabase.getReference("pushups").child(date);
        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentCount = snapshot.getValue(Integer.class);
                if (currentCount == null) {
                    currentCount = 0;  // Если данных за этот день нет, начинаем с нуля
                }
                dayRef.setValue(currentCount + count);  // Обновляем количество отжиманий за день
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Ошибка обновления данных по дням", error.toException());
            }
        });
    }

    // Локальное сохранение общего количества отжиманий
    public void saveCounterLocally(int totalCounter) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNTER_KEY, totalCounter);
        editor.apply();
    }

    // Загрузка общего количества отжиманий с устройства
    public int loadCounterLocally() {
        return sharedPreferences.getInt(COUNTER_KEY, 0);
    }

    // Интерфейс callback для асинхронной загрузки данных
    public interface CounterCallback {
        void onCallback(int value);
    }
}
