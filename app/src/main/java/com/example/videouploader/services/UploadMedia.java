package com.example.videouploader.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.videouploader.MainActivity;
import com.example.videouploader.R;
import com.example.videouploader.db.MediaRepository;
import com.example.videouploader.models.Models;
import com.example.videouploader.utils.ResultReceiver;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Calendar;
import java.util.List;

import static com.example.videouploader.MainActivity.FAIL_MEDIA_CODE;
import static com.example.videouploader.MainActivity.PROGRESS_MEDIA_CODE;
import static com.example.videouploader.MainActivity.SUCCESS_MEDIA_CODE;
import static com.example.videouploader.models.Models.Media.AVERAGE_RATING;
import static com.example.videouploader.models.Models.Media.DESCRIPTION;
import static com.example.videouploader.models.Models.Media.LAST_MODIFIED_AT;
import static com.example.videouploader.models.Models.Media.MEDIA_DB;
import static com.example.videouploader.models.Models.Media.MEDIA_ID;
import static com.example.videouploader.models.Models.Media.MEDIA_TYPE;
import static com.example.videouploader.models.Models.Media.MEDIA_URL;
import static com.example.videouploader.models.Models.Media.MENTION_LIST;
import static com.example.videouploader.models.Models.Media.POSTED_BY_UID;
import static com.example.videouploader.models.Models.Media.SHARES;
import static com.example.videouploader.models.Models.Media.TAGS;
import static com.example.videouploader.models.Models.Media.TITLE;
import static com.example.videouploader.models.Models.Media.USERNAME;
import static com.example.videouploader.models.Models.Media.VIEWERS;
import static com.example.videouploader.models.Models.User.CREATED_AT;
import static com.example.videouploader.utils.NotificationApp.CHANNEL_ID;
import static com.example.videouploader.utils.ResultReceiver.ACTION;


//todo 1. Handle notification on Fail, Success, Progress
//todo 2. Handle Room DB on fail and success
//todo 3. Handle in App progress

public class UploadMedia extends Service {
    public static final String TRY_AGAIN = "Try Again";
    public static final String IN_PROGRESS = "Progress";
    public static final String C_PROGRESS = "Current Progress";
    public static final String SUCCESS = "Success";
    public static final String CANCEL = "Cancel";

    public static boolean uploadingInProgress;

    private boolean uploadComplete = false;

    Models.Media media = new Models.Media("");
    private NotificationManagerCompat notificationManager;

    public UploadMedia() {

    }

    @Override
    public void onCreate() {
        System.out.println("Upload service started");
        notificationManager = NotificationManagerCompat.from(this);
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Upload service started");
        makeObject(intent);
        uploadingInProgress = true;
        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void postSuccessNotification(String title) {
        //foreground

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, SUCCESS_MEDIA_CODE, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Click to find view it")
                .setSmallIcon(R.drawable.ic_video_black)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.yellow))
                .build();


        startForeground(SUCCESS_MEDIA_CODE, notification);
    }

    private void postFailNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, FAIL_MEDIA_CODE, notificationIntent, 0);

        Intent broadcastIntent = new Intent(this, ResultReceiver.class);
        broadcastIntent.putExtra(ACTION, TRY_AGAIN);

        PendingIntent actionIntent = PendingIntent.getBroadcast(this, FAIL_MEDIA_CODE, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Failed to upload your content")
                .setContentText("We saved it you can try again")
                .setSmallIcon(R.drawable.ic_video_trans)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_cancel, TRY_AGAIN, actionIntent)
                .setColor(Color.RED)
                .build();


        if (checkIfExists(media)) {
            notificationManager.notify(FAIL_MEDIA_CODE, notification);
            stopSelf();
        } else {
            saveOffline(media);
            notificationManager.notify(FAIL_MEDIA_CODE, notification);
        }


    }

    private void postProgressNotification(String title, int progress) {

        System.out.println("progress : " + progress);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, PROGRESS_MEDIA_CODE, notificationIntent, 0);

        Intent broadcastIntent = new Intent(this, ResultReceiver.class);
        broadcastIntent.putExtra(ACTION, IN_PROGRESS);
        broadcastIntent.putExtra(C_PROGRESS, progress);

        PendingIntent actionIntent = PendingIntent.getBroadcast(this, PROGRESS_MEDIA_CODE, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Do not turn off your internet")
                .setSmallIcon(R.drawable.ic_video_trans)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setOnlyAlertOnce(false)
                .setAutoCancel(false)
                .setColor(getResources().getColor(R.color.yellow))
                .setProgress(100, progress, false)
                .addAction(R.drawable.ic_upload, "Show Progress", actionIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,CANCEL,actionIntent)
                .build();

        //todo set actions

        startForeground(PROGRESS_MEDIA_CODE, notification);
    }




    private int getMaxProgress(long max, long current) {
        return (int) ((current * 100) / max);
    }

    private void postSavingNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your content is posting")
                .setContentText("Almost done")
                .setSmallIcon(R.drawable.line_ic)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setColor(Color.GREEN)
                .setProgress(100, 50, true)
                .build();

        notificationManager.notify(SUCCESS_MEDIA_CODE, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void uploadContent(Models.Media media) {
        StorageReference mediaBucket = FirebaseStorage.getInstance().getReference().child(MEDIA_DB).child(media.getMediaId());

        try {
            mediaBucket.putFile(Uri.parse(media.getMediaUrl())).addOnProgressListener(snapshot -> postProgressNotification(media.getTitle().concat(" is being uploaded"), getMaxProgress(snapshot.getTotalByteCount(), snapshot.getBytesTransferred()))).addOnSuccessListener(taskSnapshot -> mediaBucket.getDownloadUrl().addOnSuccessListener(uri -> {
                media.setMediaUrl(uri.toString()); //save new link
                System.out.println("Stored link uri" + media.getMediaUrl());
                saveMediaDetails(media);
            }).addOnFailureListener(e -> {
                System.out.println(e.toString());
                if (checkIfExists(media)) {
                    postFailNotification();
                } else {
                    saveOffline(media);
                }
            })).addOnFailureListener(e -> {
                System.out.println(e.toString());
                if (checkIfExists(media)) {
                    postFailNotification();
                } else {
                    saveOffline(media);
                }
                System.out.println("Failed to upload " + media.getMediaUrl());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveMediaDetails(Models.Media media) {
        postSavingNotification();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Calendar cal = Calendar.getInstance();
        db.collection(MEDIA_DB).document(String.valueOf(cal.get(Calendar.WEEK_OF_YEAR))).collection(media.getPostedByUid()).document(media.getMediaId()).set(media).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadComplete = true;
                System.out.println("Upload successful");
                postSuccessNotification(media.getTitle().concat(" has been uploaded"));

                if (checkIfExists(media)) {
                    deleteObjOffline(media);
                } else {
                    stopSelf();
                }

            } else {
                System.out.println("Upload failed");
                if (checkIfExists(media)) {
                    updateOffline(media);
                } else {
                    saveOffline(media);
                }
            }
        });
    }

    private void updateOffline(Models.Media media) {
        System.out.println("Updating content offline");
        MediaRepository mediaRepository = new MediaRepository(getApplication());
        mediaRepository.update(media);
        stopSelf();
    }

    private void saveOffline(Models.Media media) {
        System.out.println("Saving content offline");
        new Handler(Looper.getMainLooper()).post(() -> {
            MediaRepository mediaRepository = new MediaRepository(getApplication());
            mediaRepository.insert(media);
        });
        stopSelf();
    }

    private void deleteObjOffline(Models.Media media) {
        System.out.println("Deleting content offline");
        MediaRepository mediaRepository = new MediaRepository(getApplication());
        mediaRepository.delete(media);
        stopSelf();
    }

    private boolean checkIfExists(Models.Media media) {
        final boolean[] exist = {false};
        MediaRepository mediaRepository = new MediaRepository(getApplication());
        List<Models.Media> allContentList = mediaRepository.getAllMediaList();

        if (allContentList == null) {
            return false;
        } else {
            for (int i = 0; i <= allContentList.size() - 1; i++) {
                exist[0] = allContentList.get(i).getMediaId().equals(media.getMediaId());
                System.out.println("Content exists");
            }
        }

        System.out.println("Checking if content exists");
        return exist[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeObject(Intent intent) {

        Bundle extra = intent.getExtras();
        if (extra != null) {
            media = (Models.Media) extra.get(MEDIA_DB);
        }
        uploadContent(media);
    }

    @Override
    public void onDestroy() {
        System.out.println("Upload service stopped");

        uploadingInProgress = false;

     if (!uploadComplete) {
         System.out.println("Upload failed");
         if (!checkIfExists(media)) {
             saveOffline(media);
             System.out.println("Upload saved");
         } else {
             updateOffline(media);
             System.out.println("Content updated");
         }
     } else {
         if (checkIfExists(media)) {
             deleteObjOffline(media);
             System.out.println("Upload deleted");
         } else {
             System.out.println("Content not drafted");
         }
         System.out.println("Upload complete");
     }

        super.onDestroy();
    }
}