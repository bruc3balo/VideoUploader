package com.example.videouploader.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.example.videouploader.models.Models.Media.MEDIA_TYPE;
import static com.example.videouploader.models.Models.User.COVER_IMAGE;
import static com.example.videouploader.models.Models.User.PROFILE_PIC;
import static com.example.videouploader.models.Models.User.USER_DB;

public class UploadingProfilePictureService extends Service {

    private static boolean imageUploaded;
    private String toBeUrl;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        imageUploaded = false;
        System.out.println("Image upload started");


        if (intent.getExtras().getString(MEDIA_TYPE).equals(COVER_IMAGE)) {
            toBeUrl = intent.getExtras().getString(COVER_IMAGE);
            System.out.println("data is "+intent.getExtras().getString(MEDIA_TYPE));
            uploadCoverImage(toBeUrl);
        } else {
            toBeUrl = intent.getExtras().getString(PROFILE_PIC);
            System.out.println("data is "+toBeUrl);
            uploadProfileImage(toBeUrl);
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean uploadProfileImage (String data) {
        StorageReference profileBucket = FirebaseStorage.getInstance().getReference().child(PROFILE_PIC).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        profileBucket.putFile(Uri.parse(data)).addOnProgressListener(snapshot ->System.out.println((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount())).addOnSuccessListener(taskSnapshot -> profileBucket.getDownloadUrl().addOnSuccessListener(uri -> {
            toBeUrl = uri.toString();
            imageUploaded = true;
            updateProfileImage();
            System.out.println("Stored link uri : " + uri.toString());
        }).addOnFailureListener(e -> {
            imageUploaded = false;
            stopSelf();
        }));
        return imageUploaded;
    }

    private boolean uploadCoverImage (String data) {
        StorageReference profileBucket = FirebaseStorage.getInstance().getReference().child(COVER_IMAGE).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        profileBucket.putFile(Uri.parse(data)).addOnProgressListener(snapshot ->System.out.println((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount())).addOnSuccessListener(taskSnapshot -> profileBucket.getDownloadUrl().addOnSuccessListener(uri -> {
            toBeUrl = uri.toString();
            updateCoverImage();
            imageUploaded = true;
            System.out.println("Stored link uri : " + uri.toString());
        }).addOnFailureListener(e -> {
            imageUploaded = false;
            stopSelf();
        }));
        return imageUploaded;
    }

    private void updateProfileImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(PROFILE_PIC,toBeUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Profile image updated");
            } else {
                System.out.println("Profile image not updated");
            }
            stopSelf();
        });
    }

    private void updateCoverImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(COVER_IMAGE,toBeUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Cover image updated");
            } else {
                System.out.println("Cover image not updated");
            }
            stopSelf();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageUploaded) {
            System.out.println("Image successfully uploaded");
        } else {
            System.out.println("Image failed to uploaded");
        }
    }
}
