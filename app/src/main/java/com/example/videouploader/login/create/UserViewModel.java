package com.example.videouploader.login.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.videouploader.models.Models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.videouploader.models.Models.User.EMAIL_ADDRESS;
import static com.example.videouploader.models.Models.User.USER_DB;

public class UserViewModel extends ViewModel {

    public UserViewModel() {

    }

    MutableLiveData<List<User>> getUsers () {
        MutableLiveData<List<User>> usersList = new MutableLiveData<>();
        return usersList;
    }

    MutableLiveData<User> getUsersData (String id) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        return userMutableLiveData;
    }

    MutableLiveData<List<String>> getAllEmails () {
        MutableLiveData<List<String>>  emailMutable = new MutableLiveData<>();
        List<String> emailList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult()).getDocuments()) {
                    emailList.add(Objects.requireNonNull(ds.get(EMAIL_ADDRESS)).toString());
                }
                emailMutable.setValue(emailList);
            } else {
                emailMutable.setValue(null);
            }
        });

        return emailMutable;
    }

    LiveData<List<User>> getUserList () {
        return getUsers();
    }

    LiveData<User> getSingleUser (String id) {
        return getUsersData(id);
    }

    LiveData<List<String>> getEmailList () {
        return getAllEmails();
    }
}
