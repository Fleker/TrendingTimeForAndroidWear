package com.felkertech.tv.trendingtime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.felkertech.n.utils.SettingsManager;
import com.felkertech.n.weatherdelta.utils.WeatherBroadcasterUtils;

public class WeatherReceiver extends BroadcastReceiver {
    String TAG = "trendingtime:WR";
    public WeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receive data");
        WeatherBroadcasterUtils wbu = new WeatherBroadcasterUtils(intent);
        SettingsManager sm = new SettingsManager(context);
        sm.setString("TEMPERATURE", wbu.getCurrentTemperatureSummary());
        Log.d(TAG, wbu.getCurrentTemperatureSummary()+" < "+wbu.getCurrentTemperature());
        Log.d(TAG, "API VERSION "+wbu.getApiVersion());
    }
}
