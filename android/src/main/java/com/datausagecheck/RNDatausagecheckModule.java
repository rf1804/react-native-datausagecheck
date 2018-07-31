
package com.datausagecheck;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

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
  @ReactMethod
  public void currentTodayDataUsage(final Callback callback) {

    if (callback == null) return;

    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        callback.invoke("You just waited on android for seconds.");
      }
    }, 1000);
  }
}
