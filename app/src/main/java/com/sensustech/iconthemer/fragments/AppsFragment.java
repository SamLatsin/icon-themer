package com.sensustech.iconthemer.fragments;

import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.adapters.InstalledAppsAdapter;
import com.sensustech.iconthemer.adapters.ReplaceAppsAdapter;
import com.sensustech.iconthemer.models.AppList;
import com.sensustech.iconthemer.utils.AppListGlobal;

import java.util.List;

public class AppsFragment extends Fragment implements ReplaceAppsAdapter.ItemClickListener {
    private AppListGlobal apps;
    ReplaceAppsAdapter adapter;
    private List<AppList> installedApps;
    private ImageView app_icon;
    public AppsFragment() {
        // Required empty public constructor
    }
    public static AppsFragment newInstance() {
        return new AppsFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_apps, container, false);
        app_icon = getActivity().findViewById(R.id.selected_app_icon);
        apps = AppListGlobal.getInstance();
        installedApps = apps.getAppList();
        RecyclerView recyclerView = root.findViewById(R.id.replaceAppsList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new ReplaceAppsAdapter(getContext(), installedApps);
        adapter.setClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
//        Log.i("TAG", "You clicked number " + adapter.getIcon(position) + ", which is at cell position " + position);
        app_icon.setImageDrawable(adapter.getIcon(position));
    }
}