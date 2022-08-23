package com.sensustech.iconthemer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.activities.MainActivity;
import com.sensustech.iconthemer.models.AppList;

import java.util.List;

public class InstalledAppsAdapter extends RecyclerView.Adapter<InstalledAppsAdapter.ViewHolder> {

    private List<AppList> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public InstalledAppsAdapter(Context context, List<AppList> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.app_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.appNameView.setText(mData.get(position).getName());
        holder.appIconView.setImageDrawable(mData.get(position).getIcon());
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView appNameView;
        ImageView appIconView;

        ViewHolder(View itemView) {
            super(itemView);
            appNameView = itemView.findViewById(R.id.info_text);
            appIconView = itemView.findViewById(R.id.app_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getName(int id) {
        return mData.get(id).getName();
    }

    public Drawable getIcon(int id) {
        return mData.get(id).getIcon();
    }

    public String getPackage(int id) {
        return mData.get(id).getPackages();
    }

    public int getId(int id) {
        return mData.get(id).getId();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void filterList(List<AppList> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}

