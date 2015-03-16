package com.felkertech.tv.trendingtime;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felkertech.n.utils.SettingsManager;

import wearables.jasonsalas.com.trendingtime.ConfigurationActivity;
import wearables.jasonsalas.com.trendingtime.UpdateTrendingTopicsReceiver;

/**
 * This class is a sample implementation of a DreamService. When activated, a
 * TextView will repeatedly, move from the left to the right of screen, at a
 * random y-value.
 * <p/>
 * Daydreams are only available on devices running API v17+.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class TrendingTime extends DreamService {
    LinearLayout ll;
    String TAG = "trendingtime::DD";
    TextView[] hashtags;
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch?
        setInteractive(false);

        // Hide system UI?
        setFullscreen(true);

        // Keep screen at full brightness?
        setScreenBright(true);

        // Set the content view, just like you would with an Activity.
        setContentView(R.layout.lucid_layout);
        ll = (LinearLayout) findViewById(R.id.ll);
        hashtags = new TextView[]{
                ((TextView) findViewById(R.id.ht1)),
                ((TextView) findViewById(R.id.ht2)),
                ((TextView) findViewById(R.id.ht3)),
                ((TextView) findViewById(R.id.ht4)),
                ((TextView) findViewById(R.id.ht5))
        };
        //Get the SettingsManager
        final SettingsManager sm = new SettingsManager(this);
        final android.os.Handler h = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                for(int i = 1; i <= 5; i++) {
                    getTV(i).setText(sm.getString("HT"+i));
                    if(sm.getString("HT"+i).length() > 16)
                        getTV(i).setTextSize(20);
                    if(sm.getString("HT"+i).length() > 36)
                        getTV(i).setTextSize(16);
                    Log.d(TAG, i+" "+getTV(i).getTextSize());
                }

                String clk = new SimpleDateFormat("HH:mm").format(new Date());
                ((TextView) ll.findViewById(R.id.clock)).setText(clk);
                String dt = new SimpleDateFormat("EEEE,\nd MMMM yyyy").format(new Date());
                ((TextView) ll.findViewById(R.id.date)).setText(dt);
                this.sendEmptyMessageDelayed(0, 1000*10);
            }
        };
        Log.d(TAG, "Started dreaming");
        h.sendEmptyMessageDelayed(0, 0);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();

        // TODO: Begin animations or other behaviors here.
        Intent alarmIntent = new Intent(TrendingTime.this, UpdateTrendingTopicsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TrendingTime.this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
        startService(alarmIntent);
//        startTextViewScrollAnimation();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();

        // TODO: Stop anything that was started in onDreamingStarted()
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // TODO: Dismantle resources
        // (for example, detach from handlers and listeners).
    }

    private TextView getTV(int index) {
        /*int id = getResources().getIdentifier("HT"+index, "id", getPackageName());
        Log.d(TAG, "HT" + index + " " + id);
        View value = id == 0 ? findViewById(R.id.ht1) : findViewById(id);
        return (TextView) value;*/
        return hashtags[index-1];
    }
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
