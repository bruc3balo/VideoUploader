package com.example.videouploader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.videouploader.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.videouploader.utils.MyStuff.boldStringBuild;
import static com.example.videouploader.utils.MyStuff.colorSpan;
import static com.example.videouploader.utils.MyStuff.underlineStringBuild;

public class MainActivity extends AppCompatActivity {

    private boolean backPressed;
    public static final int SUCCESS_MEDIA_CODE = 3;
    public static final int PROGRESS_MEDIA_CODE = 2;
    public static final int FAIL_MEDIA_CODE = 4;
    private Toolbar mainTitleTb;
    private TextView noPostsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTitleTb = findViewById(R.id.mainTitleTb);
        noPostsTv = findViewById(R.id.noPostsTv);
        // Typeface font = Typeface.createFromAsset(getAssets(), R.font.bebas_neue_regular);
        TypefaceSpan typefaceSpan = new TypefaceSpan("roboto.ttf");


        SpannableStringBuilder s = new SpannableStringBuilder(getResources().getString(R.string.app_name));
        // s.setSpan(new CustomTypefaceSpan(font),0,s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        s.setSpan(typefaceSpan, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainTitleTb.setTitle(colorSpan(boldStringBuild(underlineStringBuild(s)),getResources().getColor(R.color.Gulf_Blue)));
        setSupportActionBar(mainTitleTb);
        //todo add tags

        backPressed = false;
    }

    private SpannableString clickString(SpannableStringBuilder s) {
        SpannableString clickSpan = new SpannableString(s);
        clickSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                for (int i = 0; i < 4   ; i++) {

                }
            }
        }, 0, s.length(), 0);
        return clickSpan;
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Profile").setIcon(R.drawable.ic_user_profile).setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.skyBlue))).setOnMenuItemClickListener(item -> {
            goToMyProfile();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Add Media").setIcon(R.drawable.ic_add).setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.skyBlue))).setOnMenuItemClickListener(item -> {
            goToAddPage();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Logout").setIcon(R.drawable.ic_exit).setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.red))).setOnMenuItemClickListener(item -> {
            logout();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        return super.onCreateOptionsMenu(menu);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        //clearUserDetails();
        //updateUi(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void goToAddPage() {
        startActivity(new Intent(MainActivity.this, AddMedia.class));
    }

    private void goToMyProfile() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    private void updateUi(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            goBackToLoginScreen();
        }
    }

    private void goBackToLoginScreen() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    public static void exitAppDialog(Activity activity) {
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.yes_no_dialog);
        TextView info = d.findViewById(R.id.deleteInfoTv);
        Button yes = d.findViewById(R.id.yesButton);
        Button no = d.findViewById(R.id.noButton);
        d.show();
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        info.setText(activity.getResources().getString(R.string.exit_app));
        yes.setOnClickListener(v -> {
            d.dismiss();
            activity.finishAffinity();
        });
        no.setOnClickListener(v -> d.dismiss());
    }


    public static void discardDialog(Activity activity) {
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.yes_no_dialog);
        TextView info = d.findViewById(R.id.deleteInfoTv);
        Button yes = d.findViewById(R.id.yesButton);
        Button no = d.findViewById(R.id.noButton);
        d.show();
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        info.setText(activity.getResources().getString(R.string.discard));
        yes.setOnClickListener(v -> {
            d.dismiss();
            activity.finish();
        });
        no.setOnClickListener(v -> d.dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressed = false;
    }

    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> updateUi(firebaseAuth.getCurrentUser());


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        if (!backPressed) {
            Toast.makeText(this, getResources().getString(R.string.press_back), Toast.LENGTH_SHORT).show();
            backPressed = true;
        } else {
            exitAppDialog(MainActivity.this);
        }
    }
}