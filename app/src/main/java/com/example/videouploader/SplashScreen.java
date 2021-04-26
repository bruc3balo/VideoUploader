package com.example.videouploader;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videouploader.login.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.videouploader.login.LoginActivity.getUserStatus;
import static com.example.videouploader.models.Models.User.FAIL;
import static com.example.videouploader.models.Models.User.SUCCESS;


public class SplashScreen extends AppCompatActivity {

    private boolean goneToLogin;
    private TextView appNameTv;
    //   private ImageView splashImage;
    private LinearLayout splashLinearLayout;
    private ProgressBar splashProgressBar;
    private boolean original = true;
    private int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);

        appNameTv = findViewById(R.id.appNameTv);
        //   splashImage = findViewById(R.id.splashImage);
        splashLinearLayout = findViewById(R.id.splashLinearLayout);
        splashProgressBar = findViewById(R.id.splashProgressBar);


        hideNavBar();

        //todo change font to babas

        Typeface myTypeface = getResources().getFont(R.font.bebas_neue_regular);
        appNameTv.setTypeface(myTypeface);
        animate(appNameTv);
        new Handler(Looper.myLooper()).postDelayed(this::switchColors, 1000);
        new Handler(Looper.myLooper()).postDelayed(this::switchColors, 1500);
        new Handler(Looper.myLooper()).postDelayed(this::switchColors, 2000);
        new Handler(Looper.myLooper()).postDelayed(this::switchColors, 2500);
        new Handler(Looper.myLooper()).postDelayed(this::switchColors, 3000);


        goneToLogin = false;
    }


    private float getMarginValue(int current) {
        return (current * 600) / 5;
    }

    private float getSizeValue(int current) {
        return (current * 100) / 5;
    }


    private void switchColors() {
        count++;
        original = !original;
        splashProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.MAGENTA));

        if (original) {
            appNameTv.setTextColor(getResources().getColor(R.color.Gulf_Blue));
            splashLinearLayout.setBackgroundColor(getResources().getColor(R.color.Spray));
        } else {
            appNameTv.setTextColor(getResources().getColor(R.color.Spray));
            splashLinearLayout.setBackgroundColor(getResources().getColor(R.color.Gulf_Blue));
        }

        setViewMargins(SplashScreen.this, 50, (int) getMarginValue(count), 50, 0, appNameTv);
        appNameTv.setTextSize(getSizeValue(count));

        switch (count) {
            default:
                break;
            case 0:
                // appNameTv.setAlpha((float) 0.2);
                break;

            case 1:
                // appNameTv.setAlpha((float) 0.4);
                break;

            case 2:
                // appNameTv.setAlpha((float) 0.6);
                break;

            case 3:
                //appNameTv.setAlpha((float) 0.8);
                break;

            case 4:
                // appNameTv.setAlpha((float) 0.9);
                break;

            case 5:
                //appNameTv.setAlpha((float) 1.0);
                break;
        }
    }


    private void animateMargins() {
    }

    private void animate(View iv) {

        float min = 0.5f;
        float max = 1.0f;
        System.out.println("visible " + true + " alpha : " + max);
        AlphaAnimation animation1 = new AlphaAnimation(min, max);
        animation1.setDuration(1500);
        animation1.setStartOffset(50);
        animation1.setFillAfter(true);
        iv.startAnimation(animation1);
    }


    public void setViewMargins(Context con, int left, int top, int right, int bottom, View view) {

        ViewGroup.LayoutParams params = view.getLayoutParams();

        final float scale = con.getResources().getDisplayMetrics().density;

        // convert the DP into pixel
        int pixel_left = (int) (left * scale + 0.5f);
        int pixel_top = (int) (top * scale + 0.5f);
        int pixel_right = (int) (right * scale + 0.5f);
        int pixel_bottom = (int) (bottom * scale + 0.5f);

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) params;
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom);

        view.setLayoutParams(params);
        view.requestLayout();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> updateUi(firebaseAuth.getCurrentUser());
        new Handler(Looper.myLooper()).postDelayed(() -> FirebaseAuth.getInstance().addAuthStateListener(authStateListener), 2000);
    }

    private void updateUi(FirebaseUser user) {
        String status = "";
        try {
            status = getUserStatus(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to get account status", Toast.LENGTH_SHORT).show();
        } finally {
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
            }, 2500);
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
        } else {
            System.out.println("Login already started");
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