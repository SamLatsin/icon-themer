package com.sensustech.iconthemer.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.adapters.IconCategoryAdapter;
import com.sensustech.iconthemer.adapters.IconPackAdapter;
import com.sensustech.iconthemer.adapters.ReplaceAppsAdapter;
import com.sensustech.iconthemer.models.AppList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment implements IconCategoryAdapter.ItemClickListener, IconPackAdapter.ItemPackClickListener {
    IconCategoryAdapter categoryAdapter;
    IconPackAdapter packAdapter;
    private ImageView app_icon;
    private List<AppList> categories;
    private List<AppList> category_items;

    public LibraryFragment() {
        // Required empty public constructor
    }
    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        app_icon = getActivity().findViewById(R.id.selected_app_icon);
        categories = new ArrayList<AppList>();
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_0_3), "", 0));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_1_21), "", 1));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_2_24), "", 2));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_3_19), "", 3));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_4_13), "", 4));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_5_3), "", 5));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_6_24), "", 6));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_7_10), "", 7));
        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_8_0), "", 8));
//        categories.add(new AppList("", getResources().getDrawable( R.drawable.ic_9_0), ""));
        category_items = new ArrayList<AppList>();
        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            char category_id = field.getName().charAt(3);
            if ( category_id == '0' ) {
                int resID = getResId(field.getName(), R.drawable.class);
                category_items.add(new AppList("", getResources().getDrawable(resID), "", 0));
            }
        }
        RecyclerView recyclerViewCategory = root.findViewById(R.id.iconCategories);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new IconCategoryAdapter(getContext(), categories);
        categoryAdapter.setClickListener(this);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setAdapter(categoryAdapter);
        RecyclerView recyclerViewPack = root.findViewById(R.id.iconPack);
        recyclerViewPack.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.HORIZONTAL, false));
        packAdapter = new IconPackAdapter(getContext(), category_items);
        packAdapter.setClickListener(this);
        recyclerViewPack.setHasFixedSize(true);
        recyclerViewPack.setAdapter(packAdapter);
        RecyclerView.ViewHolder holder = recyclerViewCategory.findViewHolderForAdapterPosition(0);
        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        category_items.clear();
        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            char category_id = field.getName().charAt(3);
            if ( category_id ==  Integer.toString(position).charAt(0)) {
                int resID = getResId(field.getName(), R.drawable.class);
                category_items.add(new AppList("", getResources().getDrawable(resID), "", 0));
            }
        }
        packAdapter.notifyDataSetChanged();
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onItemPackClick(View view, int position) {
        app_icon.setImageDrawable(packAdapter.getIcon(position));
    }
}