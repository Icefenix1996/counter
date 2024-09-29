package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInHelper {

    private static final int RC_SIGN_IN = 9001;  // Код для результата входа
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity activity;

    // Конструктор, который принимает активность и инициализирует FirebaseAuth и GoogleSignInClient
    public GoogleSignInHelper(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

        // Настройка параметров Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("644801641032-fhvtalaqglhkh7phvorcsoqp9n84cfse.apps.googleusercontent.com")  // Вставлен Web Client ID напрямую
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    // Метод для начала процесса авторизации
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Обработка результата авторизации
    public void handleSignInResult(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(activity, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для авторизации в Firebase с помощью Google токена
    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Авторизация успешна, обновляем UI
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Ошибка авторизации
                        updateUI(null);
                        Toast.makeText(activity, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Метод для обновления интерфейса в зависимости от состояния пользователя
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(activity, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }
}
