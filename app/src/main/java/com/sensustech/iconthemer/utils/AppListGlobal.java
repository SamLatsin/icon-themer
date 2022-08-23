package com.sensustech.iconthemer.utils;

import com.sensustech.iconthemer.models.AppList;

import java.util.List;

public class AppListGlobal {
    private List<AppList> installedApps;
    private static volatile AppListGlobal instance;

    public static AppListGlobal getInstance() {
        AppListGlobal localInstance = instance;
        if (localInstance == null) {
            synchronized (AppListGlobal.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AppListGlobal();
                }
            }
        }
        return localInstance;
    }

    private AppListGlobal() { }

    public void setAppList(List<AppList> installedApps) {
        this.installedApps = installedApps;
    }

    public List<AppList> getAppList() {
        return installedApps;
    }
}
