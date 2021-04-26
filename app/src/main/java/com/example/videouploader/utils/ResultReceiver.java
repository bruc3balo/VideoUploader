package com.example.videouploader.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.example.videouploader.MainActivity;
import com.example.videouploader.services.UploadMedia;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static com.example.videouploader.services.UploadMedia.CANCEL;
import static com.example.videouploader.services.UploadMedia.C_PROGRESS;
import static com.example.videouploader.services.UploadMedia.IN_PROGRESS;
import static com.example.videouploader.services.UploadMedia.SUCCESS;
import static com.example.videouploader.services.UploadMedia.TRY_AGAIN;

public class ResultReceiver extends BroadcastReceiver {

    public static final String ACTION = "Action";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getExtras().get(ACTION).toString()) {
            default:
                break;

            case TRY_AGAIN:
                tryAgainMtaa(context);
                break;

            case IN_PROGRESS:
                goShowProgress(context, intent);
                break;

            case SUCCESS:
                showSuccess(context);
                break;

            case CANCEL:
                cancelUpload(context);
                break;
        }
    }

    private void tryAgainMtaa(Context context) {
        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
        System.out.println("Try again triggered");
    }

    private void goShowProgress(Context context, Intent intent) {
        String progress = intent.getExtras().get(C_PROGRESS).toString();
        context.startActivity(new Intent(context, MainActivity.class).putExtra(IN_PROGRESS, progress).setFlags(FLAG_ACTIVITY_NEW_TASK));
        Toast.makeText(context, "Progress triggered " + progress, Toast.LENGTH_SHORT).show();
        System.out.println("Progress triggered " + progress);
    }

    private void showSuccess(Context context) {
        Toast.makeText(context, "Post Success", Toast.LENGTH_SHORT).show();
        System.out.println("Post Success");
    }

    private void cancelUpload(Context context) {
        Intent intent = new Intent(context, UploadMedia.class);
        context.stopService(intent);
    }

}
