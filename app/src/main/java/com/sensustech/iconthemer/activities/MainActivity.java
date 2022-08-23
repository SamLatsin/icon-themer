package com.sensustech.iconthemer.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.sensustech.iconthemer.adapters.InstalledAppsAdapter;
import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.models.AppList;
import com.sensustech.iconthemer.utils.AppListGlobal;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InstalledAppsAdapter.ItemClickListener {
    int side = Gravity.LEFT;
    InstalledAppsAdapter adapter;
    private List<AppList> installedApps;
    private EditText search_et;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toast toast;
    private AppListGlobal apps;
    private int app_widget_id;
    private Intent target_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );
        target_intent = new Intent(this, AppActivity.class);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        apps = AppListGlobal.getInstance();
        installedApps = apps.getAppList();
        installedApps = getInstalledApps();
        apps.setAppList(installedApps);
        RecyclerView recyclerView = findViewById(R.id.installedAppsList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new InstalledAppsAdapter(this, installedApps);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        search_et = findViewById(R.id.search);
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
//            app_widget_id = intent.getIntExtra("ida", 0);
            app_widget_id = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            target_intent.putExtra("app_widget_id", app_widget_id);
        }

    }

    private void filter(String text) {
        List<AppList> filteredList = new ArrayList<AppList>();
        for (AppList item : installedApps) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(R.id.premium):
                startActivity(new Intent(this, PremiumActivity.class));
                break;
            case(R.id.share):
                showAToast("Share");
                break;
            case(R.id.rate):
                showAToast("Rate");
                break;
            case(R.id.contact):
                showAToast("Contact");
                break;
            case(R.id.policy):
                showAToast("Policy");
                break;
            case(R.id.terms):
                showAToast("Terms");
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        boolean equals = "android.intent.action.CREATE_SHORTCUT".equals(getIntent().getAction());
        Log.d("test", Boolean.toString(equals));
        Log.i("TAG", "You clicked number " + adapter.getName(position) + ", which is at cell position " + position);
        target_intent.putExtra("app_id", adapter.getId(position));
        target_intent.putExtra("app_name", adapter.getName(position));
        target_intent.putExtra("app_package", adapter.getPackage(position));
        startActivity(target_intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private List<AppList> getInstalledApps() {
        int id = 0;
        List<AppList> apps = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (getPackageManager().getLaunchIntentForPackage(p.applicationInfo.packageName) != null) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packages = p.applicationInfo.packageName;
                apps.add(new AppList(appName, icon, packages, id));
                id++;
            }
        }
        return apps;
    }

    public void OpenMenuClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e){

        }
        drawerLayout.openDrawer(side);
    }

    public void CloseMenuClick(View view) {
        drawerLayout.closeDrawer(side);
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
