package com.example.videouploader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videouploader.R;
import com.example.videouploader.models.Models;

public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.SliderViewHolder> {

    private Context context;
    private final Models.Welcome welcome;

    public WelcomeAdapter() {
        welcome= new Models.Welcome();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        return new SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.welcome_layout, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.title.setText(welcome.getWelcomeList().get(position).getTitle());
        holder.description.setText(welcome.getWelcomeList().get(position).getDescription());
        Glide.with(context).load(welcome.getWelcomeList().get(position).getImageUrl()).into(holder.welcomeImage);
    }


    @Override
    public int getItemCount() {
        return welcome.getWelcomeList().size();
    }


    public static class SliderViewHolder extends RecyclerView.ViewHolder {

        TextView title,description;
        ImageView welcomeImage;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleId);
            description = itemView.findViewById(R.id.descriptionId);
            welcomeImage = itemView.findViewById(R.id.welcomeImage);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
