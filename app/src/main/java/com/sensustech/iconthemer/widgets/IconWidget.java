package com.sensustech.iconthemer.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.RemoteViews;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.utils.AppPreferences;

import java.text.DateFormat;
import java.util.Date;

public class IconWidget extends AppWidgetProvider {

    private void updateAppWidget(Context context,
                                 AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.icon_widget);
        byte[] app_icon_array = Base64.decode(AppPreferences.getInstance(context).getString("app_icon", "null"), Base64.DEFAULT);
        Bitmap app_icon = BitmapFactory.decodeByteArray(app_icon_array, 0, app_icon_array.length);
        views.setImageViewBitmap(R.id.icon_main, app_icon);
        views.setTextViewText(R.id.text_main, AppPreferences.getInstance(context).getString("app_name", "null"));
        Intent launch_intent = context.getPackageManager().getLaunchIntentForPackage(AppPreferences.getInstance(context).getString("app_package", "null"));
        launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launch_intent.setPackage(AppPreferences.getInstance(context).getString("app_package", "null"));
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, launch_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.group_main, notifyPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        updateAppWidget(context, appWidgetManager, appWidgetIds[appWidgetIds.length-1]);
    }
}
