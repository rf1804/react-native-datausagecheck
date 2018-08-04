
package com.datausagecheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import java.util.Calendar;

public class RNDatausagecheckModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNDatausagecheckModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDatausagecheck";
  }
  private boolean isAccessGranted() {
      try {
          PackageManager packageManager = reactContext.getApplicationContext().getPackageManager();
          ApplicationInfo applicationInfo = packageManager.getApplicationInfo(reactContext.getApplicationContext().getPackageName(), 0);
          AppOpsManager appOpsManager = null;
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
              appOpsManager = (AppOpsManager) reactContext.getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
          }
          int mode = 0;
          if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
              mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                      applicationInfo.uid, applicationInfo.packageName);
          }
          return (mode == AppOpsManager.MODE_ALLOWED);

      } catch (PackageManager.NameNotFoundException e) {
          return false;
      }

  }


  @ReactMethod
  public void openDataUsagePermission() {
    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    if (intent.resolveActivity(reactContext.getPackageManager()) != null) {
        reactContext.startActivityForResult(intent,100,Bundle.EMPTY);
    }
  }



  @ReactMethod
  public void currentTodayDataUsage(final Callback callback) {

    if (callback == null) return;
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (!isAccessGranted()) {
            AlertDialog.Builder builder= new AlertDialog.Builder(reactContext.getCurrentActivity());
            builder.setMessage("To access data usage you have to give permissions manually");
            builder.setPositiveButton("Open Settings",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openDataUsagePermission();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog= builder.create();
            dialog.show();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                NetworkStatsManager networkStatsManager = (NetworkStatsManager) reactContext.getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
                TelephonyManager manager = (TelephonyManager) reactContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                String permission = "android.permission.READ_PHONE_STATE";
                int res = reactContext.checkSelfPermission(permission);
                if (res != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String subscriberId = manager.getSubscriberId();
                Calendar calStrt= Calendar.getInstance();
                calStrt.set(Calendar.MONTH,calStrt.get(Calendar.MONTH));
                calStrt.set(Calendar.DAY_OF_MONTH,calStrt.get(Calendar.DAY_OF_MONTH));
                calStrt.set(Calendar.YEAR,calStrt.get(Calendar.YEAR));
                calStrt.set(Calendar.HOUR,0);
                calStrt.set(Calendar.AM_PM,Calendar.AM);
                calStrt.set(Calendar.MINUTE,5);
                calStrt.set(Calendar.SECOND,0);
                long startTime = calStrt.getTimeInMillis();
                long endTime = Calendar.getInstance().getTimeInMillis();
                try {
                    NetworkStats.Bucket dataUsage = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,subscriberId,startTime, endTime);
                    long datausageInNumber= dataUsage.getRxBytes()+dataUsage.getTxBytes();
                    callback.invoke(""+datausageInNumber);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

      }
    }, 1000);
  }
}
