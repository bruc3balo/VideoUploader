package com.example.videouploader.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.example.videouploader.MainActivity;
import com.example.videouploader.R;
import com.example.videouploader.login.create.CreateFragment;
import com.example.videouploader.login.signin.SignInFragment;
import com.example.videouploader.models.Models;
import com.example.videouploader.utils.MyLinkedMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static com.example.videouploader.models.Models.User.CREATION_STATUS;
import static com.example.videouploader.models.Models.User.EMAIL_ADDRESS;
import static com.example.videouploader.models.Models.User.FAIL;
import static com.example.videouploader.models.Models.User.SUCCESS;
import static com.example.videouploader.models.Models.User.USERNAME;
import static com.example.videouploader.models.Models.User.USER_DB;
import static com.example.videouploader.models.Models.User.USER_SP;
import static com.example.videouploader.utils.MyStuff.HY;


public class LoginActivity extends AppCompatActivity {

    private static final int PHONE_STATE_CODE = 12;
    private FrameLayout loginFrame;
    private FragmentManager fragmentManager;
    private boolean signIn;
    private TextView changeFrameTv;
    private ProgressBar loginProgress;
    private boolean backPressed;
    private CoordinatorLayout loginCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.loginTb);
        setSupportActionBar(toolbar);
        setAnimatedBg(toolbar);


        loginFrame = findViewById(R.id.loginFrame);
        fragmentManager = getSupportFragmentManager();
        changeFrameTv = findViewById(R.id.changeFrameTv);
        changeFrameTv.setOnClickListener(v -> toggleSignIn());
        loginProgress = findViewById(R.id.loginProgress);
        loginProgress.setVisibility(View.VISIBLE);

        loginCoordinator = findViewById(R.id.loginCoordinator);

        setStart();

        Button skipTv = findViewById(R.id.skipTv);
        skipTv.setOnClickListener(v -> skip());


        // new Handler(Looper.myLooper()).postDelayed(() -> startActivity(new Intent(LoginActivity.this, MainActivity.class)), 2000);

    }

    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> updateUi(firebaseAuth.getCurrentUser());

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void updateUi(FirebaseUser user) {
        loginProgress.setVisibility(View.VISIBLE);
        String status = "";
        try {
            status = getUserStatus(this);
        } catch (Exception e) { e.printStackTrace(); loginProgress.setVisibility(View.GONE);Toast.makeText(this, "Failed to get account status", Toast.LENGTH_SHORT).show(); } finally {
            String finalStatus = status;
            new Handler(Looper.myLooper()).postDelayed(() -> {
                if (finalStatus.equals(String.valueOf(FAIL))) {
                    if (user != null) {
                        tryDeleteFailedAccount(loginCoordinator);
                    } else {
                        loginProgress.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Create a new account, The previous failed or try again later" , Toast.LENGTH_SHORT).show();
                        showSnackBar(loginCoordinator,"Create a new account, The previous failed or try again later" );
                    }
                } else if (finalStatus.equals(String.valueOf(SUCCESS))) {
                    if (user != null) {
                        goToMainPage();
                    } else {
                        loginProgress.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Sign in to continue", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loginProgress.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to get status", Toast.LENGTH_SHORT).show();
                }
            },1000);
        }
    }

    public static void tryDeleteFailedAccount (ViewGroup v) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Deleted account successfully");
                tryDeleteDetails(uid);
                showSnackBar(v,"You can make a new account with the save details");
            } else {
                System.out.println("Failed to delete account");
                showSnackBar(v,"Failed to delete account. Try again later");
            }
        });
    }

    private static void tryDeleteDetails (String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(uid).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Details removed");
            } else {
                System.out.println("Failed to remove details");
            }
        });
    }

    public static void showSnackBar(ViewGroup v, String message) {
        Snackbar.make(v,message,Snackbar.LENGTH_LONG).show();
    }

    private void goToMainPage() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }


    private void setAnimatedBg(Toolbar toolbar) {
        AnimationDrawable animationDrawable = (AnimationDrawable) toolbar.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
    }


    private void skip() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        Toast.makeText(this, "You will not be able to back up till you have signed in. Offline still works", Toast.LENGTH_SHORT).show();
        finish();
    }

    public static String getUserStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SP, MODE_PRIVATE);
        String status = sharedPreferences.getString(CREATION_STATUS, String.valueOf(FAIL));
        System.out.println("sp status is " + status);
        return status;
    }


    public static MyLinkedMap<String,String> getUserSpMap(Context context) {
        MyLinkedMap<String,String> map = new MyLinkedMap<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SP, MODE_PRIVATE);
        map.put(USERNAME,sharedPreferences.getString(USERNAME,HY));
        map.put(EMAIL_ADDRESS,sharedPreferences.getString(EMAIL_ADDRESS,FirebaseAuth.getInstance().getUid()));
        map.put(CREATION_STATUS,sharedPreferences.getString(CREATION_STATUS, String.valueOf(SUCCESS)));
        return map;
    }

    public static boolean saveUserSp(Context context, Models.User user, int status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SP, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
       try {
           myEdit.clear();
           myEdit.apply();
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           try {
               myEdit.putString(USERNAME, user.getUsername());
               myEdit.putString(EMAIL_ADDRESS, user.getEmailAddress());
               myEdit.putString(CREATION_STATUS, String.valueOf(status));
               System.out.println("user sp saved status success");
           } catch (Exception e) {
               e.printStackTrace();
               myEdit.putString(USERNAME, "-");
               myEdit.putString(EMAIL_ADDRESS, "-");
               myEdit.putInt(CREATION_STATUS, FAIL);
               System.out.println("user sp not saved");
           }
       }

        return myEdit.commit();
    }

    private static boolean updateUserSp (Context context, MyLinkedMap<String,String> object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SP, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        try {
            for (int i = 0; i <= object.size() - 1;i++) {
                myEdit.putString(object.getKey(i),object.getValue(i));
                System.out.println("Updated user sp");
            }
        } catch (Exception e) { for (int i = 0; i <= object.size() - 1;i++) {
                myEdit.putString(object.getKey(i),"-");
                System.out.println("Failed to update user sp");
            }}

        return myEdit.commit();
    }

    public static void clearUserDetails (Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SP, MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.clear();
            myEdit.apply();
            System.out.println("sp cleared");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "SP failed to clear", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleSignIn() {
        signIn = !signIn;
        changeFrame(signIn);
    }

    private void setStart() {
        signIn = true;
        backPressed = false;
        fragmentManager.beginTransaction().replace(loginFrame.getId(), SignInFragment.newInstance()).commit();
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        loginProgress.setAlpha(0);
        //loginProgress.setProgress(50);
    }

    private void changeFrame(boolean signIn) {
        if (signIn) {
            fragmentManager.beginTransaction().replace(loginFrame.getId(), SignInFragment.newInstance()).commit();
            changeFrameTv.setText(getResources().getString(R.string.you_don_t_have_an_account_n_click_here_to_create_one));
        } else {
            fragmentManager.beginTransaction().replace(loginFrame.getId(), CreateFragment.newInstance()).commit();
            changeFrameTv.setText(getResources().getString(R.string.already_have_an_acc));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PHONE_STATE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!backPressed) {
            backPressed = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }
}