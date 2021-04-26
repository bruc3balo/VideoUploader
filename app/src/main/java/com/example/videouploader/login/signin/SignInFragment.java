package com.example.videouploader.login.signin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.videouploader.MainActivity;
import com.example.videouploader.R;
import com.example.videouploader.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.Objects;

import static com.example.videouploader.login.LoginActivity.getUserStatus;
import static com.example.videouploader.login.LoginActivity.showSnackBar;
import static com.example.videouploader.login.LoginActivity.tryDeleteFailedAccount;
import static com.example.videouploader.models.Models.User.FAIL;
import static com.example.videouploader.models.Models.User.SUCCESS;


public class SignInFragment extends Fragment {

    private Button signInB;
    private ProgressBar progressBar;

    private boolean internet;
    private InternetAvailabilityChecker internetAvailabilityChecker;
    private InternetConnectivityListener connectivityListener;
    private boolean isWaiting = false;
    private CoordinatorLayout signInCoordinator;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInCoordinator = v.findViewById(R.id.signInCoordinator);

        InternetAvailabilityChecker.init(requireContext());
        connectivityListener = isConnected -> internet = isConnected;
        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(connectivityListener);

        progressBar = v.findViewById(R.id.signInPb);
        progressBar.setVisibility(View.GONE);

        EditText email = v.findViewById(R.id.emailF), pass = v.findViewById(R.id.passwordF);

        signInB = v.findViewById(R.id.signInB);
        signInB.setOnClickListener(v1 -> {
            if (internet) {
                if (validateForm(email, pass)) {
                    signInUser(email.getText().toString(), pass.getText().toString());
                }
            } else {
                Toast.makeText(requireContext(), "Check internet", Toast.LENGTH_SHORT).show();
            }
        });



        return v;
    }

    private boolean validateForm(EditText email, EditText pass) {
        boolean valid = false;
        if (!email.getText().toString().contains("@")) {
            email.setError("Wrong email format");
            email.requestFocus();
        } else if (pass.getText().toString().length() < 6) {
            pass.setError("Too short password");
            pass.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    private void inProgress(ProgressBar progressBar, Button button) {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        isWaiting = true;
    }

    private void outProgress(ProgressBar progressBar, Button button) {
        progressBar.setVisibility(View.GONE);
        button.setEnabled(true);
        isWaiting = false;
    }

    private void signInUser(String s, String s1) {
        inProgress(progressBar, signInB);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(s, s1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "User sign in", Toast.LENGTH_SHORT).show();
                updateUi(task.getResult().getUser());
            } else {
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                outProgress(progressBar, signInB);
            }
        });
    }

    private void updateUi(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);

        String status = "";

        try {
            status = getUserStatus(requireContext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to get account status", Toast.LENGTH_SHORT).show();
        } finally {
            String finalStatus = status;
            new Handler(Looper.myLooper()).postDelayed(() -> {
                if (finalStatus.equals(String.valueOf(FAIL))) {
                    if (user != null) {
                        tryDeleteFailedAccount(signInCoordinator);
                    } else {
                        Toast.makeText(requireContext(),"Create a new account, The previous failed or try again later" , Toast.LENGTH_SHORT).show();
                        showSnackBar(signInCoordinator,"Create a new account, The previous failed or try again later" );
                    }
                } else if (finalStatus.equals(String.valueOf(SUCCESS))) {
                    if (user != null) {
                        goToMainPage();
                    } else {
                        Toast.makeText(requireContext(), "Sign in to continue", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to get status", Toast.LENGTH_SHORT).show();
                }
            },1000);
        }
    }

    private void goToMainPage() {
        requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        internetAvailabilityChecker.removeInternetConnectivityChangeListener(connectivityListener);
    }
}