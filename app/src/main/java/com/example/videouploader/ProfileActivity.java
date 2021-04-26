package com.example.videouploader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.videouploader.adapter.ProfileMediaAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar profile_toolbar;
    private final String vader = "https://bgr.com/wp-content/uploads/2015/08/darth-vader.jpg?quality=70&strip=all&w=640&h=500&crop=1";
    private final String warden = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/020e1179-46f8-43ff-9c44-4280cde630ec/ddcca82-c18ba2fe-8d5f-4c8f-90a5-eb69b5a8a555.jpg/v1/fill/w_1231,h_649,q_70,strp/rainbow_six_siege__2019__wallpaper_hd_4k_by_sahibdm_ddcca82-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9Njc1IiwicGF0aCI6IlwvZlwvMDIwZTExNzktNDZmOC00M2ZmLTljNDQtNDI4MGNkZTYzMGVjXC9kZGNjYTgyLWMxOGJhMmZlLThkNWYtNGM4Zi05MGE1LWViNjliNWE4YTU1NS5qcGciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.OH_W3uifsPMNqp6JYYv_ZHhDC4lrr1DsCA27V1muOJ4";

    float xFinger = 0;
    float yFinger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(profile_toolbar);
        profile_toolbar.setNavigationOnClickListener(v -> finish());

        LinearLayout profileDataLayout = findViewById(R.id.profileDataLayout);


        CircleImageView profilePic = findViewById(R.id.profilePic);
        profilePic.setLongClickable(true);
        profilePic.setOnLongClickListener(v -> {
            showPictureDialog(profilePic,"Changing Profile Picture");
            return false;
        });
        Glide.with(this).load(vader).into(profilePic);

        RoundedImageView user_profile_picture = findViewById(R.id.user_profile_picture);
        user_profile_picture.setLongClickable(true);
        user_profile_picture.setOnLongClickListener(v -> {
            showPictureDialog(user_profile_picture,"Changing cover image");
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


    private void showPictureDialog (View v , String info) {
        v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        BottomSheetDialog d = new BottomSheetDialog(ProfileActivity.this,R.style.SheetDialog);
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
        ImageButton sheet_drag_button = d.findViewById(R.id.sheet_drag_button);
        /*sheet_drag_button.setOnTouchListener((v14, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:

                    xFinger = event.getX();
                    yFinger = event.getY();

                    break;

                case MotionEvent.ACTION_MOVE:
                    float fingerMovedX, fingerMovedY;
                    fingerMovedX = event.getX();
                    fingerMovedY = event.getY();

                    float distanceX = fingerMovedX - xFinger;
                    float distanceY = fingerMovedY - yFinger;



                    float newY = d.getBehavior().getPeekHeight() + distanceY;

                    d.getBehavior().setPeekHeight((int) newY,true);



                    break;

                case MotionEvent.ACTION_UP:

                    break;

                default:
                    return false;
            }
            return true;
        });*/
        TextView infoTv = d.findViewById(R.id.infoDialog);
        infoTv.setText(info);
        TextView getImage = d.findViewById(R.id.actionTv);
        getImage.setOnClickListener(v13 -> {
            Toast.makeText(ProfileActivity.this, "Getting image", Toast.LENGTH_SHORT).show();
            d.dismiss();
        });
        TextView remove = d.findViewById(R.id.removeTv);
        remove.setOnClickListener(v12 -> {
            Toast.makeText(ProfileActivity.this, "Removed", Toast.LENGTH_SHORT).show();
            d.dismiss();
        });
        TextView cancel = d.findViewById(R.id.cancelDialog);
        cancel.setOnClickListener(v1 -> d.dismiss());
    }
}