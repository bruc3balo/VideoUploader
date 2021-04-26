package com.example.videouploader.login.create;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.videouploader.R;
import com.example.videouploader.Welcome;
import com.example.videouploader.models.Models;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.example.videouploader.login.LoginActivity.saveUserSp;
import static com.example.videouploader.models.Models.User.FAIL;
import static com.example.videouploader.models.Models.User.SUCCESS;
import static com.example.videouploader.models.Models.User.USER_DB;


public class CreateFragment extends Fragment {

    private final Models.User newUser = new Models.User("");
    private final LinkedList<String> emailList = new LinkedList<>();
    private ProgressBar loginPb;
    private Button createB;
    private boolean internet;
    private InternetAvailabilityChecker internetAvailabilityChecker;
    private InternetConnectivityListener connectivityListener;
    private boolean isWaiting = false;

    public CreateFragment() {
        // Required empty public constructor
    }

    public static CreateFragment newInstance() {
        return new CreateFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_create, container, false);

        InternetAvailabilityChecker.init(requireContext());
        connectivityListener = isConnected -> internet = isConnected;
        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(connectivityListener);

        EditText name = v.findViewById(R.id.usernameF), email = v.findViewById(R.id.emailF), pass = v.findViewById(R.id.passwordF), cPass = v.findViewById(R.id.cPasswordF);
        createB = v.findViewById(R.id.createB);
        createB.setOnClickListener(v1 -> {
            if (validateForm(name, email, pass, cPass)) {
                if (internet) {
                    authNewUser(email.getText().toString(), cPass.getText().toString());
                } else {
                    Toast.makeText(requireContext(), "Check your Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginPb = v.findViewById(R.id.loginPb);
        populateEmailList();


        return v;
    }

    private void authNewUser(String s, String s1) {
        inProgress(loginPb, createB);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(s, s1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                newUser.setUid(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());
                Toast.makeText(requireContext(), "User Created", Toast.LENGTH_SHORT).show();
                saveUserDetails(newUser);
            } else {
                outProgress(loginPb, createB);
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm(EditText name, EditText email, EditText pass, EditText cPass) {
        boolean valid = false;
        String required = "Required";
        if (name.getText().toString().isEmpty()) {
            name.setError(required);
            name.requestFocus();
        } else if (!email.getText().toString().contains("@")) {
            email.setError("Wrong email format");
            email.requestFocus();
        } else if (emailList.contains(email.getText().toString())) {
            email.setError("Already in use");
            email.requestFocus();
        } else if (pass.getText().toString().length() < 6) {
            pass.setError("Cannot be less than 6");
            pass.requestFocus();
        } else if (!cPass.getText().toString().equals(pass.getText().toString())) {
            pass.setError("Not matching");
            cPass.setError("Not matching");
            cPass.requestFocus();
        } else {
            newUser.setCreatedAt(Calendar.getInstance().getTime().toString());
            newUser.setEmailAddress(email.getText().toString());
            newUser.setUsername(name.getText().toString());
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

    private void populateEmailList() {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getEmailList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (!isWaiting) {
                    outProgress(loginPb, createB);
                    if (!strings.isEmpty()) {
                        emailList.clear();
                        emailList.addAll(strings);
                    }
                }
            }
        });
    }

    private void saveUserDetails(Models.User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(user.getUid()).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "User details saved", Toast.LENGTH_SHORT).show();
                showWelcomeScreen();
                saveUserSp(requireContext(), newUser, SUCCESS);
            } else {
                outProgress(loginPb, createB);
                saveUserSp(requireContext(), newUser, FAIL);
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showWelcomeScreen() {
        requireActivity().startActivity(new Intent(requireContext(), Welcome.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        internetAvailabilityChecker.removeInternetConnectivityChangeListener(connectivityListener);
    }
}