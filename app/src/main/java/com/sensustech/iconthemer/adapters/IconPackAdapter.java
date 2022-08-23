package com.sensustech.iconthemer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.models.AppList;

import java.util.List;

public class IconPackAdapter extends RecyclerView.Adapter<IconPackAdapter.ViewHolder> {

    private List<AppList> mData;
    private LayoutInflater mInflater;
    private ItemPackClickListener mClickListener;

    // data is passed into the constructor
    public IconPackAdapter(Context context, List<AppList> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pack_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.PackIconView.setImageDrawable(mData.get(position).getIcon());
    }


    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView PackIconView;

        ViewHolder(View itemView) {
            super(itemView);
            PackIconView = itemView.findViewById(R.id.app_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemPackClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position

    public Drawable getIcon(int id) {
        return mData.get(id).getIcon();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemPackClickListener itemPackClickListener) {
        this.mClickListener = itemPackClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemPackClickListener {
        void onItemPackClick(View view, int position);
    }
}
