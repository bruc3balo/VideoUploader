package com.example.videouploader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.videouploader.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListRvAdapter extends RecyclerView.Adapter<ListRvAdapter.ViewHolder> {

    private final ArrayList<String> list;
    private ItemClickListener mClickListener;
    private Context mContext;


    public ListRvAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        this.mContext = context;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.removeImage.setVisibility(View.VISIBLE);
        holder.imageUri.setText(list.get(position));

        holder.removeImage.setOnClickListener(v -> {
            list.remove(list.get(position));
            notifyDataSetChanged();
        });
    }


    // total number of rows
    @Override
    public int getItemCount() {

        return list.size();

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton removeImage;
        TextView imageUri;


        ViewHolder(View itemView) {
            super(itemView);
            removeImage = itemView.findViewById(R.id.removeItem);
            imageUri = itemView.findViewById(R.id.itemText);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}