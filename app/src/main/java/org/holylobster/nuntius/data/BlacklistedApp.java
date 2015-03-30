/*
 * Copyright (C) 2015 - Holy Lobster
 *
 * Nuntius is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Nuntius is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Nuntius. If not, see <http://www.gnu.org/licenses/>.
 */

package org.holylobster.nuntius.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by fly on 24/03/15.
 */
public class BlacklistedApp {
    private static final String TAG = BlacklistedApp.class.getSimpleName();

    private static List<ApplicationInfo> blacklistedApp;
    private static Context context;
    private static PackageManager pm;
    private static SharedPreferences settings;

    public BlacklistedApp(Context c) {
        context = c;
        pm = context.getPackageManager();
        settings = context.getSharedPreferences("BlackListSP", context.MODE_PRIVATE);
        getFromPref();
    }

    public static void getFromPref(){
        ArrayList<String> bl = new ArrayList<>(settings.getStringSet("BlackList", new HashSet<String>()));
        blacklistedApp = new ArrayList<>();
        for (int i = 0; i< bl.size(); i++){
            try {
                blacklistedApp.add(pm.getApplicationInfo(bl.get(i), 0));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Error retrieving application info", e);
            }
        }
        Collections.sort(blacklistedApp, new ApplicationInfo.DisplayNameComparator(pm));
    }

    public static List<ApplicationInfo> getBlacklistedApp(){
        return blacklistedApp;
    }

    public static void add(ApplicationInfo app){
        blacklistedApp.add(app);
        sortAndPush();
    }

    public static void remove(int i){
        blacklistedApp.remove(i);
        sortAndPush();
    }

    public static void sortAndPush(){
        Collections.sort(blacklistedApp, new ApplicationInfo.DisplayNameComparator(pm));
        pushToPref();
    }

    public static void pushToPref(){
        SharedPreferences.Editor editor = settings.edit();
        ArrayList<String> bl = new ArrayList<>();
        for (int i = 0; i< blacklistedApp.size(); i++){
            bl.add(blacklistedApp.get(i).packageName);
        }
        editor.putStringSet("BlackList", new HashSet<>(bl));
        editor.commit();
    }
}
