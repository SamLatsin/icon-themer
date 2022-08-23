package com.sensustech.iconthemer.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.fragments.AppsFragment;
import com.sensustech.iconthemer.fragments.LibraryFragment;
import com.sensustech.iconthemer.fragments.PhotoFragment;
import com.sensustech.iconthemer.utils.AppListGlobal;
import com.sensustech.iconthemer.utils.AppPreferences;
import com.sensustech.iconthemer.widgets.IconWidget;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class AppActivity extends AppCompatActivity {
    String app_name_str;
    String app_package;
    EditText app_name;
    Drawable icon;
    ImageView app_icon;
    int app_id;
    int app_widget_id = 0;
    private FrameLayout frame;
    private ConstraintLayout library_constraints;
    private ConstraintLayout apps_constraints;
    private ConstraintLayout photo_constraints;
    private TextView library_text;
    private TextView apps_text;
    private TextView photo_text;
    private Toast toast;
    private ShortcutAddedReceiver shortcutAddedReceiver;
    private Boolean is_miui = !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    private Boolean is_oppo = !TextUtils.isEmpty(getSystemProperty("ro.build.version.opporom"));
    private Boolean is_emui = !TextUtils.isEmpty(getSystemProperty("ro.build.version.emui"));
    private Boolean is_vivo = !TextUtils.isEmpty(getSystemProperty("ro.vivo.os.version"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        Intent intent = getIntent();
        app_name_str = intent.getStringExtra("app_name");
        app_package = intent.getStringExtra("app_package");
        app_id = intent.getIntExtra("app_id", 0);
//        app_widget_id = intent.getIntExtra("app_widget_id", 0);
        app_name = findViewById(R.id.rename);
        app_name.setText(app_name_str);
        app_icon = findViewById(R.id.selected_app_icon);
        AppListGlobal apps;
        apps = AppListGlobal.getInstance();
        icon = apps.getAppList().get(app_id).getIcon();
        app_icon.setImageDrawable(icon);
        frame = findViewById(R.id.host_fragment);
        loadFragment(LibraryFragment.newInstance());
        photo_text = findViewById(R.id.photo_text);
        apps_text = findViewById(R.id.apps_text);
        library_text = findViewById(R.id.library_text);
        apps_constraints = findViewById(R.id.apps_constraint);
        photo_constraints = findViewById(R.id.photo_constraint);
        library_constraints = findViewById(R.id.library_constraint);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.host_fragment, fragment);
        ft.commit();
    }

    public void backClick(View view) {
        finish();
    }

    public void libraryClick(View view) {
        loadFragment(LibraryFragment.newInstance());
        library_text.setTextColor(Color.WHITE);
        library_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category_active));
        apps_text.setTextColor(getResources().getColor(R.color.text));
        apps_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category));
        photo_text.setTextColor(getResources().getColor(R.color.text));
        photo_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category));
    }

    public void photoClick(View view) {
        loadFragment(PhotoFragment.newInstance());
        library_text.setTextColor(getResources().getColor(R.color.text));
        library_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category));
        apps_text.setTextColor(getResources().getColor(R.color.text));
        apps_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category));
        photo_text.setTextColor(Color.WHITE);
        photo_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category_active));
    }

    public void appsClick(View view) {
        loadFragment(AppsFragment.newInstance());
        library_text.setTextColor(getResources().getColor(R.color.text));
        library_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category));
        apps_text.setTextColor(Color.WHITE);
        apps_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category_active));
        photo_text.setTextColor(getResources().getColor(R.color.text));
        photo_constraints.setBackground(ContextCompat.getDrawable(this, R.drawable.b_category));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createWidget(@NonNull Activity activity){
        if (app_widget_id == 0) {
            AppWidgetManager appWidgetManager =
                    activity.getSystemService(AppWidgetManager.class);
            ComponentName provider =
                    new ComponentName(activity, IconWidget.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                    AppPreferences.getInstance(AppActivity.this).saveData("app_name", app_name.getText().toString());
                    AppPreferences.getInstance(AppActivity.this).saveData("app_package", app_package);
                    Bitmap bitmap = drawableToBitmap(app_icon.getDrawable());
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 20, bStream);
                    byte[] byteArray = bStream.toByteArray();
                    AppPreferences.getInstance(AppActivity.this).saveData("app_icon", Base64.encodeToString(byteArray, Base64.DEFAULT));
                    Intent pinnedShortcutCallbackIntent = new Intent("ACTION_SHORTCUT_ADDED_CALLBACK");
                    PendingIntent successCallback = PendingIntent.getBroadcast(activity, 0,
                            pinnedShortcutCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Bundle bundle = new Bundle();
                    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.icon_widget);
                    remoteViews.setImageViewBitmap(R.id.icon_main, bitmap);
                    remoteViews.setTextViewText(R.id.text_main, app_name.getText().toString());
                    bundle.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, remoteViews);
                    appWidgetManager.requestPinAppWidget(provider, bundle, successCallback);
                }
            }
        }
        else {
//            showAToast(Integer.toString(app_widget_id));
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
//
//            RemoteViews views = new RemoteViews(activity.getPackageName(),
//                    R.layout.icon_widget);
//
//            Bitmap bitmap = drawableToBitmap(app_icon.getDrawable());
//            views.setImageViewBitmap(R.id.icon_main, bitmap);
//            views.setTextViewText(R.id.text_main, app_name.getText().toString());
//
//            appWidgetManager.updateAppWidget(app_widget_id, views);
//
//            Intent resultValue = new Intent();
//            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, app_widget_id);
//            setResult(RESULT_OK, resultValue);
//            finish();
        }
    }

    public void createShortcut(@NonNull Activity activity) throws PackageManager.NameNotFoundException {
        Intent shortcutIntent = getPackageManager().getLaunchIntentForPackage(app_package);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { // code for adding shortcut on pre oreo device
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, Long.toString(System.currentTimeMillis()));
            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, app_name.getText().toString());
            Drawable d = app_icon.getDrawable();
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            sendBroadcast(addIntent);
            finish();
        } else {
            ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
            assert shortcutManager != null;
            if (shortcutManager.isRequestPinShortcutSupported()) {
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(activity, Long.toString(System.currentTimeMillis()))
                                .setIntent(shortcutIntent.setAction("ACTION_VIEW"))
                                .setIcon(Icon.createWithBitmap(drawableToBitmap(app_icon.getDrawable())))
                                .setShortLabel(app_name.getText())
                                .build();
                //callback if user allowed to place the shortcut
                Intent pinnedShortcutCallbackIntent = new Intent("ACTION_SHORTCUT_ADDED_CALLBACK");
                PendingIntent successCallback = PendingIntent.getBroadcast(activity, 0,
                        pinnedShortcutCallbackIntent,  0);
                shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
            } else {
                showAToast("Failed to Add Shortcut");
            }
        }
    }

    private void registerShortcutAddedReceiver(){
        if(shortcutAddedReceiver == null){
            shortcutAddedReceiver = new ShortcutAddedReceiver();
        }
        IntentFilter shortcutAddedFilter = new IntentFilter("ACTION_SHORTCUT_ADDED_CALLBACK");
        registerReceiver(shortcutAddedReceiver, shortcutAddedFilter);
    }

    private void unregisterShortcutAddedReceiver(){
        if(shortcutAddedReceiver != null){
            unregisterReceiver(shortcutAddedReceiver);
            shortcutAddedReceiver = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerShortcutAddedReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterShortcutAddedReceiver();
    }

    private class ShortcutAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void saveClick(View view) throws PackageManager.NameNotFoundException {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e){

        }
        if (Build.VERSION.SDK_INT >= 26 && !is_miui && !is_emui && !is_oppo && !is_vivo){
            createWidget(AppActivity.this);
        }
        else {
            createShortcut(AppActivity.this);
        }
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}