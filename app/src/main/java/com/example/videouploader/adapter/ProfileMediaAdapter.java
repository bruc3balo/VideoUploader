package com.example.videouploader.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.videouploader.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class ProfileMediaAdapter extends BaseAdapter {

    private final int[] mainPageIcons = new int[]{R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans, R.drawable.ic_video_trans,};

    public ProfileMediaAdapter() {
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return mainPageIcons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_media_row, null);
        }

        RoundedImageView icon = convertView.findViewById(R.id.profileThumbnail);
        Glide.with(parent.getContext()).load(mainPageIcons[0]).into(icon);

        return convertView;
    }
}
