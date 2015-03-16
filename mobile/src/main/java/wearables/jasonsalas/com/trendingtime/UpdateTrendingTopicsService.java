package wearables.jasonsalas.com.trendingtime;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.felkertech.n.utils.SettingsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UpdateTrendingTopicsService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /*
    * This service is invoked by a broadcast receiver that's fired periodically by an AlarmManager.
    * It calls a proxy web service on Google App Engine using Temboo's Twitter library (https://www.temboo.com/library/Library/Twitter/Trends/Place/)
    * to fetch the latest trending topics from Twitter's Trends API (https://dev.twitter.com/rest/reference/get/trends/place) for the 'USA' region.
    * */

    private static final String PATH = "/trendingtopics";
    private static final String URL = "http://divine-display-828.appspot.com/";   // replace this value with your own URL
    private static final String TAG = "TrendingTime::TopicsService";

    GoogleApiClient googleApiClient;
    String responseBody;
    HttpClient client;
    HttpGet request;
    HttpResponse response;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "attempting HTTP request");
                    client = new DefaultHttpClient();
                    request = new HttpGet(URL);
                    response = client.execute(request);
                    responseBody = EntityUtils.toString(response.getEntity());
                    Log.d(TAG, responseBody);
                    Log.d(TAG, "And then ");
                    Log.d(TAG, responseBody.split(";").toString());

                    //TODO Now we add each one to a SharedPreference
                    SettingsManager sm = new SettingsManager(getApplication());
                    sm.setString("HT1", responseBody.split(";")[0]);
                    sm.setString("HT2", responseBody.split(";")[1]);
                    sm.setString("HT3", responseBody.split(";")[2]);
                    sm.setString("HT4", responseBody.split(";")[3]);
                    sm.setString("HT5", responseBody.split(";")[4]);
                    //The daydream will take the SP and display it in a layout

//                    googleApiClient.connect();
                } catch (ClientProtocolException ex) {
                    Log.i(TAG, String.format("ClientProtocolException: %s", ex.getMessage()));
                    ex.printStackTrace();
                } catch (IOException ex) {
                    Log.i(TAG, String.format("IOException: %s", ex.getMessage()));
                    ex.printStackTrace();
                }
            }
        }).start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();

        /*googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                *//*.addApi(Wearable.API)*//*
                .build();*/
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        if(googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }
}
