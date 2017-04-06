package com.rosa.swift.core.business.utils;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;

import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.network.json.sap.security.AppListItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by inurlikaev on 29.04.2016.
 */
public class SecurityUtils {
    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        List<String> paths = getPath();
//        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
//                "/system/bin/failsafe/su", "/data/local/su" };
        for (String path : paths) {
            if (new File(path + "/su").exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private static boolean checkRootMethod4() {
        return isPackageInstalled("eu.chainfire.supersu");
    }

    private static boolean isPackageInstalled(String packageName) {
        PackageManager pm = SwiftApplication.getApplication().getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static final boolean isEmulator() {
        int rating = 0;

        if (Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("google_sdk") ||
                Build.PRODUCT.contains("Droid4X") ||
                Build.PRODUCT.contains("sdk_x86") ||
                Build.PRODUCT.contains("sdk_google") ||
                Build.PRODUCT.contains("vbox86p")) {
            rating++;
        }

        if (Build.MANUFACTURER.equals("unknown") ||
                Build.MANUFACTURER.equals("Genymotion")) {
            rating++;
        }

        if (Build.BRAND.equals("generic") ||
                Build.BRAND.equals("generic_x86")) {
            rating++;
        }

        if (Build.DEVICE.contains("generic") ||
                Build.DEVICE.contains("generic_x86") ||
                Build.DEVICE.contains("Droid4X") ||
                Build.DEVICE.contains("generic_x86_64") ||
                Build.DEVICE.contains("vbox86p")) {
            rating++;
        }

        if (Build.MODEL.equals("sdk") ||
                Build.MODEL.equals("google_sdk") ||
                Build.MODEL.contains("Droid4X") ||
                Build.MODEL.equals("Android SDK built for x86_64") ||
                Build.MODEL.equals("Android SDK built for x86")) {
            rating++;
        }

        if (Build.HARDWARE.equals("goldfish") ||
                Build.HARDWARE.equals("vbox86")) {
            rating++;
        }

        if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                Build.FINGERPRINT.contains("generic_x86_64") ||
                Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                Build.FINGERPRINT.contains("vbox86p") ||
                Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
            rating++;
        }

        return rating > 4;
    }

    public static List<String> getPath() {
        return Arrays.asList(System.getenv("PATH").split(":"));
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4();
    }

    public static List<PackageInfo> getPackageList() {
        PackageManager pm = SwiftApplication.getApplication().getPackageManager();
        final List<PackageInfo> appinstall =
                pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        List<PackageInfo> packageList = new ArrayList<PackageInfo>();

        for (PackageInfo pInfo : appinstall) {
            if ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                packageList.add(pInfo);
            }
        }

        return packageList;
    }

    public static List<PackageInfo> getSuspectPackageList() {
        List<PackageInfo> packageList = getPackageList();

        List<PackageInfo> packageListFiltered = new ArrayList<PackageInfo>();

        for (PackageInfo pInfo : packageList) {
            boolean found = false;
            for (String permission : pInfo.requestedPermissions) {
                found = (Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permission));
                if (found) break;
            }
            if (found) {
                packageListFiltered.add(pInfo);
            }
        }

        return packageListFiltered;
    }

    public static List<AppListItem> getAppList(List<PackageInfo> packageList) {
        PackageManager pm = SwiftApplication.getApplication().getPackageManager();

        List<AppListItem> returnAppList = new ArrayList<AppListItem>();

        for (PackageInfo pInfo : packageList) {
            final AppListItem appListItem = new AppListItem((String) pm.getApplicationLabel(pInfo.applicationInfo), pInfo.packageName);
            returnAppList.add(appListItem);
        }

        return returnAppList;
    }

    public static boolean hasBannedApps(@Nullable List<String> bannedAppsList) {
        if (bannedAppsList != null) {
            for (String packageName : bannedAppsList) {
                if (isPackageInstalled(packageName)) return true;
            }
        }
        return false;
    }
}
