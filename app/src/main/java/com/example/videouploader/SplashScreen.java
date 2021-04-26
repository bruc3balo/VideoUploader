package com.example.videouploader;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videouploader.login.LoginActivity;
import com.example.videouploader.utils.MyStuff;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.videouploader.login.LoginActivity.getUserStatus;
import static com.example.videouploader.models.Models.User.FAIL;
import static com.example.videouploader.models.Models.User.SUCCESS;


public class SplashScreen extends AppCompatActivity {

    private boolean goneToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);

        hideNavBar();

        new Handler(Looper.myLooper()).postDelayed(() -> {
            ImageView splashImage = findViewById(R.id.splashImage);
            splashImage.setImageTintList(ColorStateList.valueOf(Color.YELLOW));
        },1500);

        new Handler(Looper.myLooper()).postDelayed(() -> {
            TextView appNameTv = findViewById(R.id.appNameTv);
            appNameTv.setText(MyStuff.underlineString(getResources().getString(R.string.app_name)));
            appNameTv.setTextColor(Color.WHITE);
        },1000);

        new Handler(Looper.myLooper()).postDelayed(() -> {
            ProgressBar splashProgressBar = findViewById(R.id.splashProgressBar);
            splashProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.MAGENTA));
        },2000);


        goneToLogin = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> updateUi(firebaseAuth.getCurrentUser());
        new Handler(Looper.myLooper()).postDelayed(()-> FirebaseAuth.getInstance().addAuthStateListener(authStateListener), 2000);
    }

    private void updateUi(FirebaseUser user) {
        String status = "";
        try {
            status = getUserStatus(this);
        } catch (Exception e) { e.printStackTrace();
            Toast.makeText(this, "Failed to get account status", Toast.LENGTH_SHORT).show(); } finally {
            String finalStatus = status;
            new Handler(Looper.myLooper()).postDelayed(() -> {
                if (finalStatus.equals(String.valueOf(FAIL))) {
                    goToLogin();
                } else if (finalStatus.equals(String.valueOf(SUCCESS))) {
                    if (user != null) {
                       goToMain();
                    } else {
                        goToLogin();
                    }
                } else {
                   goToLogin();
                }
            },1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavBar();
    }

    private void goToLogin() {
        if (!goneToLogin) {
            goneToLogin = true;
            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private void goToMain() {
        startActivity(new Intent(SplashScreen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void hideNavBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

}