package com.felkertech.n.weatherdelta.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;

/**
 * Created by N on 6/2/2015.
 * This class is meant to be a simple way for Android apps to interact with Weather Delta for the sake of
 * getting simple weather data in their app without having to program any complex code.
 *
 * ## Pros
 * * All of the download management is managed in the background, you don't need to code it
 * * There is no need to request the user's location or Internet
 * * Most recent data is cached, giving you data instantly, even when offline
 * * As Weather Delta continues to evolve, your app also improves without any extra work
 * * User customization is handled by the Weather Delta app, like metric and download settings,
 * so you don't need to rewrite the wheel
 * * Many weather services have prices per API call online. As the data is cached on this device,
 * there is no cost to you to implement this, it's merely extending data already available.
 * * All of the data parsing is already handled. Getting the temperature is one simple method.
 * There's also richer weather information in the summaries.
 * * l18n for weather data is already built-in
 * * Weather Delta is available on Android phones, tablets, TVs, and Wear. Your app can access this
 * API on all devices. (The Wear app currently doesn't support this API, so you'll have to get the
 * data from the mobile app)
 *
 * ## Cons
 * * Users will need to have Weather Delta installed on their device in order for this API to work.
 * Use `isAppInstalled` to check whether these APIs can be used.
 */
public class WeatherBroadcasterUtils {
    public static String CURRENT_WEATHER_ACTION = "com.felkertech.n.weatherdelta.CURRENT_WEATHER";
    public static String REQUEST_WEATHER_ACTION = "com.felkertech.n.weatherdelta.REQUEST_CACHE";
    public static String CURRENT_TEMPERATURE_SUMMARY = "TEMPERATURE_SUMMARY";
    public static String CURRENT_TEMPERATURE = "TEMPERATURE";
    public static String CURRENT_RAIN_SUMMARY = "RAIN_SUMMARY";
    public static String CURRENT_RAIN_TYPE = "RAIN_TYPE";
    public static String CURRENT_RAIN_INTENSITY = "RAIN_INTENSITY";
    public static String CURRENT_RAIN_PROB = "PROBABILITY_OF_RAIN";
    public static String LAST_SYNC_TIME = "LAST_SYNC_TIME";
    public static String UNITS_METRIC = "UNITS_METRIC";
    public static String API_VERSION = "API_VERSION";
    public static String TAG = "WeatherBroadcasterUtils";

    private Intent message;

    /**
     * Creates an instance of the utility
     * @param msg The intent received from the Weather Delta broadcast
     */
    public WeatherBroadcasterUtils(Intent msg) {
        if(msg.getAction().equals(CURRENT_WEATHER_ACTION))
            message = msg;
        else
            Log.e(TAG, "You are using the wrong intent!");
    }

    /**
     * Retrieves a forecast for today's temperature
     * @return A descriptive sentence forecasting the temperature
     */
    public String getCurrentTemperatureSummary() {
        return message.getStringExtra(CURRENT_TEMPERATURE_SUMMARY);
    }

    /**
     * Retrieves the current temperature at the given location in the user's specified units
     * @return Integer in degrees
     */
    public int getCurrentTemperature() {
        return message.getIntExtra(CURRENT_TEMPERATURE, -1);
    }

    /**
     * Retrieves a forecast for today's precipitation
     * @return A descriptive sentence forecasting the temperature
     */
    public String getCurrentRainSummary() {
        return message.getStringExtra(CURRENT_RAIN_SUMMARY);
    }

    /**
     * Retrieves the forecasted type of precipitation
     * @return A single word describing the type of precipitation (rain, snow, hail, etc)
     */
    public String getCurrentRainType() {
        return message.getStringExtra(CURRENT_RAIN_TYPE);
    }

    /**
     * Retrieves the forecasted strongest intensity of precipitation
     * @return A phrase describing the intensity of the precipitation (drizzle, light, heavy, etc)
     */
    public String getCurrentRainIntensity() {
        return message.getStringExtra(CURRENT_RAIN_INTENSITY);
    }

    /**
     * Retrieves the current forecasted probability of rain (out of 100)
     * @return A number between 0 and 100 describing the likelihood of precipitation right now
     */
    public int getCurrentRainProbability() {
        return message.getIntExtra(CURRENT_RAIN_PROB, -1);
    }

    /**
     * Determines whether Weather Delta is on this user's device and thus can be used to retrieve
     * weather. This method is static, so it can be called without instantiating an object of this
     * class.
     * @param c
     * @return true if the app is on this user's device; false if not.
     */
    public static boolean isAppInstalled(Context c) {
        PackageManager pm = c.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo("com.felkertech.n.weatherdelta", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * Checks the user's settings to determine the units for the data
     * @return True if the units are metric (Celcius, Km, etc)
     */
    public boolean isMetric() {
        return message.getBooleanExtra(UNITS_METRIC, false);
    }

    /**
     * Returns the time in milliseconds of the last time the weather was updated, to enable you
     * to better pinpoint current weather details
     * @return milliseconds since the unix epoch
     */
    public long getLastCheckedTime() {
        return message.getLongExtra(LAST_SYNC_TIME, -1);
    }

    /**
     * Gives the version code for the Weather Delta application, to be used for a better understanding
     * of particular nuances between versions
     * @return Version code
     */
    public int getApiVersion() {
        return message.getIntExtra(API_VERSION, -1);
    }

    /**
     * Gives the intent that will be used to request weather information. This intent should then
     * be started by calling `startService`. When the service completes its request, it sends out
     * a broadcast to all apps that retrieve this data. This method is static, so it can be called
     * without instantiating an object of this class.
     * @return Intent to request weather data
     */
    public static Intent getWeatherRequest(Context context) {
        Intent i = new Intent(REQUEST_WEATHER_ACTION);
        return createExplicitFromImplicitIntent(context, i);
    }

    /* FROM http://blog.android-develop.com/2014/10/android-l-api-21-javalangillegalargumen.html */
    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}