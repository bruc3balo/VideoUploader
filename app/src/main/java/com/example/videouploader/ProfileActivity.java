package com.example.videouploader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.videouploader.adapter.ProfileMediaAdapter;
import com.example.videouploader.services.UploadingProfilePictureService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.videouploader.AddMedia.STORAGE_PERMISSION_CODE;
import static com.example.videouploader.AddMedia.getExtension;
import static com.example.videouploader.AddMedia.getFileName;
import static com.example.videouploader.login.LoginActivity.getUserSpMap;
import static com.example.videouploader.models.Models.Media.MEDIA_TYPE;
import static com.example.videouploader.models.Models.Media.USERNAME;
import static com.example.videouploader.models.Models.User.COVER_IMAGE;
import static com.example.videouploader.models.Models.User.PROFILE_PIC;
import static com.example.videouploader.models.Models.User.USER_DB;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar profile_toolbar;
    private final String vader = "https://bgr.com/wp-content/uploads/2015/08/darth-vader.jpg?quality=70&strip=all&w=640&h=500&crop=1";
    private final String warden = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/020e1179-46f8-43ff-9c44-4280cde630ec/ddcca82-c18ba2fe-8d5f-4c8f-90a5-eb69b5a8a555.jpg/v1/fill/w_1231,h_649,q_70,strp/rainbow_six_siege__2019__wallpaper_hd_4k_by_sahibdm_ddcca82-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9Njc1IiwicGF0aCI6IlwvZlwvMDIwZTExNzktNDZmOC00M2ZmLTljNDQtNDI4MGNkZTYzMGVjXC9kZGNjYTgyLWMxOGJhMmZlLThkNWYtNGM4Zi05MGE1LWViNjliNWE4YTU1NS5qcGciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.OH_W3uifsPMNqp6JYYv_ZHhDC4lrr1DsCA27V1muOJ4";

    float xFinger = 0;
    float yFinger = 0;

    private RoundedImageView user_profile_picture;
    private CircleImageView profilePic;
    private Uri file = null;

    private static final int coverCode = 0;
    private static final int profileCode = 1;

    private String profileInfo = "Changing Profile Picture";
    private String coverInfo = "Changing cover image";

    private String coverImageUrl = "";
    private String profilePicUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(profile_toolbar);
        profile_toolbar.setNavigationOnClickListener(v -> finish());

        LinearLayout profileDataLayout = findViewById(R.id.profileDataLayout);
        profilePic = findViewById(R.id.profilePic);
        profilePic.setLongClickable(true);
        profilePic.setOnLongClickListener(v -> {
            showPictureDialog(profilePic, profileInfo);
            return false;
        });
        Glide.with(this).load(vader).into(profilePic);

        user_profile_picture = findViewById(R.id.user_profile_picture);
        user_profile_picture.setLongClickable(true);
        user_profile_picture.setOnLongClickListener(v -> {
            showPictureDialog(user_profile_picture, coverInfo);
            return false;
        });
        Glide.with(this).load(warden).into(user_profile_picture);

        AppBarLayout profileAppBar = findViewById(R.id.profile_app_bar);
        profileAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            double f = (float) Math.abs(verticalOffset);


            System.out.println("profile f " + f);

            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                //Collapsed
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) profileDataLayout.getLayoutParams();
                //   setViewMargins(ProfileActivity.this, p, 10, 10, 10, 10, profileDataLayout);
                profilePic.setAlpha((float) 0.0);
                //  System.out.println("profile " + verticalOffset);
            } else if (verticalOffset == 0) {
                //Expanded
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) profileDataLayout.getLayoutParams();
                //  setViewMargins(ProfileActivity.this, p, 10, 100, 10, 10, profileDataLayout);
                profilePic.setAlpha((float) 1.0);
                //   System.out.println("profile " + verticalOffset);

            } else {
                //Between
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) profileDataLayout.getLayoutParams();
                //  setViewMargins(ProfileActivity.this, p, 10, 50, 10, 10, profileDataLayout);
                profilePic.setAlpha((float) 0.5);
                //  System.out.println("profile " + verticalOffset);

            }
        });

        GridView profileMediaGrid = findViewById(R.id.profileMediaGrid);
        ProfileMediaAdapter profileMediaAdapter = new ProfileMediaAdapter();
        profileMediaGrid.setAdapter(profileMediaAdapter);
        profileMediaGrid.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(ProfileActivity.this, "" + profileMediaAdapter.getCount(), Toast.LENGTH_SHORT).show());


        TextView email_profile_tv = findViewById(R.id.email_profile_tv);
        email_profile_tv.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());

        TextView profileUsernameTv = findViewById(R.id.profileUsernameTv);
        profileUsernameTv.setText(getUserSpMap(ProfileActivity.this).get(USERNAME));

        getWindow().setStatusBarColor(Color.BLACK);
    }

    public void setViewMargins(Context con, ViewGroup.LayoutParams params, int left, int top, int right, int bottom, View view) {

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

    private void changeProfilePicture() {
        startService(new Intent(ProfileActivity.this, UploadingProfilePictureService.class).putExtra(PROFILE_PIC, String.valueOf(file)).putExtra(MEDIA_TYPE, PROFILE_PIC));
        System.out.println("data is 1 "+file);
    }

    private void changeProfileBackground() {
        startService(new Intent(ProfileActivity.this, UploadingProfilePictureService.class).putExtra(COVER_IMAGE, String.valueOf(file)).putExtra(MEDIA_TYPE, COVER_IMAGE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case coverCode:
                if (resultCode == RESULT_OK) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            assert data != null;
                            user_profile_picture.setImageURI(data.getData());
                            file = data.getData();
                            changeProfileBackground();
                            System.out.println("Extension is " + getExtension(getFileName(data.getData(), ProfileActivity.this)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 1000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cover request cancelled", Toast.LENGTH_SHORT).show();
                }
                break;

            case profileCode:
                if (resultCode == RESULT_OK) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            assert data != null;
                            profilePic.setImageURI(data.getData());
                            file = data.getData();
                            changeProfilePicture();
                            System.out.println("Extension is " + getExtension(getFileName(data.getData(), ProfileActivity.this)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 1000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Profile Image request cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPictureDialog(View v, String info) {
        v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        BottomSheetDialog d = new BottomSheetDialog(ProfileActivity.this, R.style.SheetDialog);
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(true);
        d.setDismissWithAnimation(true);
        d.setContentView(R.layout.picture_dialog);
        d.getBehavior().setDraggable(true);
        d.getBehavior().setExpandedOffset(0);
        d.getBehavior().setFitToContents(true);
        d.getBehavior().setPeekHeight(0);
        d.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
        d.setOnDismissListener(dialog -> v.setBackgroundTintList(null));

        TextView infoTv = d.findViewById(R.id.infoDialog);
        infoTv.setText(info);
        TextView getImage = d.findViewById(R.id.actionTv);
        getImage.setOnClickListener(v13 -> {
           if (info.equals(profileInfo)) {
               onlyImageProfile();
           } else if (info.equals(coverInfo)) {
               onlyImageCover();
           }
            d.dismiss();
        });
        TextView remove = d.findViewById(R.id.removeTv);
        remove.setOnClickListener(v12 -> {
            if (info.equals(profileInfo)) {
                if (removeProfilePhoto()) {
                    Toast.makeText(this, "Profile Photo removed", Toast.LENGTH_SHORT).show();
                  // todo default  profilePic.setImageResource();
                } else {
                    Toast.makeText(this, "Failed to remove profile photo. Try later", Toast.LENGTH_SHORT).show();
                }
            } else if (info.equals(coverInfo)) {
                if (removeCoverPhoto()) {
                    Toast.makeText(this, "Cover Photo removed", Toast.LENGTH_SHORT).show();
                   //  todo default  user_profile_picture.setImageResource();
                } else {
                    Toast.makeText(this, "Failed to remove cover photo. Try later", Toast.LENGTH_SHORT).show();
                }
            }
            d.dismiss();
        });
        TextView cancel = d.findViewById(R.id.cancelDialog);
        cancel.setOnClickListener(v1 -> d.dismiss());
    }

    private void onlyImageProfile() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), profileCode);
        }
    }

    private void onlyImageCover() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), coverCode);
        }
    }

    private boolean removeCoverPhoto () {
        final boolean[] removed = {false};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(COVER_IMAGE,"").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Cover image removed");
                removed[0] = true;
            } else {
                System.out.println("Cover image not removed");
            }
        });
        return removed[0];
    }

    private boolean removeProfilePhoto () {
        final boolean[] removed = {false};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(PROFILE_PIC,"").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Profile image removed");
                removed[0] = true;
            } else {
                System.out.println("Profile image not removed");
            }
        });
        return removed[0];
    }

    private void getCoverImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    coverImageUrl = task.getResult().get(COVER_IMAGE).toString();
                    Toast.makeText(ProfileActivity.this, "Failed to get cover image", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    profilePicUrl = task.getResult().get(PROFILE_PIC).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Failed to get profile image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to get images", Toast.LENGTH_SHORT).show();
            }
        });
    }


}