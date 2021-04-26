package com.example.videouploader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.example.videouploader.models.Models;
import com.example.videouploader.services.UploadMedia;
import com.example.videouploader.utils.MyStuff;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.okhttp.Cache;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.videouploader.MainActivity.discardDialog;
import static com.example.videouploader.login.LoginActivity.getUserSpMap;
import static com.example.videouploader.models.Models.Media.AVERAGE_RATING;
import static com.example.videouploader.models.Models.Media.DESCRIPTION;
import static com.example.videouploader.models.Models.Media.DUMMY_VIDEO;
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

public class AddMedia extends AppCompatActivity implements OnPreparedListener {
    private static final int IMAGE_RESULT_CODE = 3;
    private static final int STORAGE_PERMISSION_CODE = 4;
    private static final int VIDEO_RESULT_CODE = 2;
    private static final int MENTION_RESULT_CODE = 5;
    private String mediaType = "";
    Models.Media media = new Models.Media(MyStuff.getMediaID(mediaType, FirebaseAuth.getInstance().getCurrentUser().getUid()));

    private VideoView video_preview;
    private RoundedImageView posterPreview;
    private Uri file = null;
    private final ArrayList<String> mentionList = new ArrayList<>();
    private boolean backPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        Toolbar tb = findViewById(R.id.addingNewMediaTb);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        Button getMediaB = findViewById(R.id.getMediaB);
        getMediaB.setOnClickListener(v -> showMediaTypeDialog());

        video_preview = findViewById(R.id.video_preview);
        posterPreview = findViewById(R.id.posterPreview);

        EditText title = findViewById(R.id.titleField);
        EditText descriptionField = findViewById(R.id.descriptionField);

        Button postMtaaButton = findViewById(R.id.postMtaaButton);
        postMtaaButton.setOnClickListener(v -> {
            if (validateForm(title,descriptionField)) {
                sendObj(media);
            }
        });


        backPressed = false;
    }

    private boolean validateForm(EditText title, EditText desc) {
        boolean valid = false;
        if (title.getText().toString().isEmpty()) {
            title.setError("Need a title");
            title.requestFocus();
        } else if (desc.getText().toString().isEmpty()) {
            desc.requestFocus();
            desc.setError("Need a description");
        } else {
            media.setTitle(title.getText().toString());
            media.setDescription(desc.getText().toString());
            media.setPostedByUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

            media.setCreatedAt(Calendar.getInstance().getTime().toString());
            media.setLastModifiedAt(Calendar.getInstance().getTime().toString());
            media.setMediaType(mediaType);

            media.setMediaUrl(String.valueOf(file));
            media.setMediaId(MyStuff.getMediaID(mediaType,FirebaseAuth.getInstance().getCurrentUser().getUid()));

            media.setAverageRating("0");
            media.setShares("");
            media.setTags("");

            media.setViewers("");
            media.setMentionList("");
            media.setUsername(getUserSpMap(AddMedia.this).get(USERNAME));

            media.setComments(null);
            valid = true;
        }

        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressed = false;
    }

    @Override
    public void onBackPressed() {
        if (!backPressed) {
            Toast.makeText(this, getResources().getString(R.string.press_back), Toast.LENGTH_SHORT).show();
            backPressed = true;
        } else {
            discardDialog(AddMedia.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Info").setIcon(R.drawable.ic_info).setOnMenuItemClickListener(item -> {
            showInfoDialog();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }


    private void showInfoDialog() {
        Dialog d = new Dialog(this);
        //d.setContentView(R.layout.);
    }

    private void showMediaTypeDialog() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.media_type_upload);
        d.show();
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton videoL = d.findViewById(R.id.videoB);
        ImageButton imageL = d.findViewById(R.id.imageB);
        Button dismissButton = d.findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(v -> d.dismiss());
        videoL.setOnClickListener(v -> {
            d.dismiss();
            getVideo();
        });
        imageL.setOnClickListener(v -> {
            d.dismiss();
            getImage();
        });
    }

    private void getMedia() {
        if (media.getMediaType().equals(Models.Media.VIDEO)) {
            getVideo();
        } else if (media.getMediaType().equals(Models.Media.IMAGE)) {
            getImage();
        } else {
            getImage();
        }
    }

    private void getImage() {
        setImage();
        onlyImage();
    }

    private void getVideo() {
        setVideo();
        onlyVideo();
    }

    private void setImage() {
        media.setMediaType(Models.Media.IMAGE);
        posterPreview.setVisibility(View.VISIBLE);
        if (video_preview.isPlaying()) {
            video_preview.stopPlayback();
            video_preview.setVideoURI(null);
        }
        video_preview.setVisibility(View.GONE);
    }

    private void setVideo() {
        media.setMediaType(Models.Media.VIDEO);
        posterPreview.setVisibility(View.GONE);
        video_preview.setVisibility(View.VISIBLE);
    }


    private void sendObj(Models.Media media) {
        Intent intent = new Intent(AddMedia.this, UploadMedia.class);
        intent.putExtra(MEDIA_DB, media);
        Toast.makeText(this, "Your content will be uploaded", Toast.LENGTH_SHORT).show();
        System.out.println(media.getMediaUrl() + "mediaUrl" + media.getMediaId() + "mediaid");
        startService(intent);
        //ContextCompat.startForegroundService(AddMedia.this, intent);
        finish();

    }

    private void onlyImage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_RESULT_CODE);
        }
    }

    private void onlyVideo() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_RESULT_CODE);
        }
    }

    private void setupVideoView(String link) {
        try {
            video_preview.setVideoURI(Uri.parse(link));
        } catch (Exception e) {
            Toast.makeText(this, "failed to play link", Toast.LENGTH_SHORT).show();
            video_preview.setVideoURI(Uri.parse(DUMMY_VIDEO));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            default:
                break;

            case MENTION_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    mentionList.addAll(data.getExtras().getStringArrayList(MENTION_LIST));
//                    mentionListRvAdapter.notifyDataSetChanged();
                    Toast.makeText(this, mentionList.toString(), Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    if (mentionList.isEmpty()) {
                        Toast.makeText(this, "No mentions", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, mentionList.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case VIDEO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            assert data != null;
                            setupVideoView(data.getData().toString());
                            file = data.getData();
                            System.out.println("Extension is " + getExtension(getFileName(data.getData(), AddMedia.this)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 1000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Video request cancelled", Toast.LENGTH_SHORT).show();
                }
                break;

            case IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            assert data != null;
                            posterPreview.setImageURI(data.getData());
                            file = data.getData();
                            System.out.println("Extension is " + getExtension(getFileName(data.getData(), AddMedia.this)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 1000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Image request cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPrepared() {
        //Starts the video playback as soon as it is ready
        Toast.makeText(this, "Video Ready", Toast.LENGTH_SHORT).show();
        video_preview.start();
    }


    public static String getExtension(String fileName) {
        final String emptyExtension = "";
        if (fileName == null) {
            return emptyExtension;
        }
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return emptyExtension;
        }
        return fileName.substring(index + 1);
    }
    //todo use shared preferences for getting user data

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        System.out.println("file " + result);
        return result;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Storage permission not granted");
                        return;
                    }
                }
                System.out.println("Storage permission granted");

            }
        }
    }
}