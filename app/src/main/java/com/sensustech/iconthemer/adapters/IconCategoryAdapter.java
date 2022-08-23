package com.sensustech.iconthemer.adapters;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

public class IconCategoryAdapter extends RecyclerView.Adapter<IconCategoryAdapter.ViewHolder> {

    private List<AppList> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Integer selectedPos = 0;

    // data is passed into the constructor
    public IconCategoryAdapter(Context context, List<AppList> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.icon_category_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.CategoryIconView.setImageDrawable(mData.get(position).getIcon());
        if (position == 0) {
            holder.bg.setBackground(holder.itemView.getContext().getResources().getDrawable( R.drawable.bg_selected_themer));
        }
        holder.itemView.setBackground(selectedPos == position ? holder.itemView.getContext().getResources().getDrawable( R.drawable.bg_selected_themer) : holder.itemView.getContext().getResources().getDrawable( R.color.bg));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView CategoryIconView;
        ConstraintLayout bg;

        ViewHolder(View itemView) {
            super(itemView);
            CategoryIconView = itemView.findViewById(R.id.category_icon);
            bg = itemView.findViewById(R.id.category_bg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getAdapterPosition();
            notifyItemChanged(selectedPos);
        }
    }

    // convenience method for getting data at click position

    public Drawable getIcon(int id) {
        return mData.get(id).getIcon();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
