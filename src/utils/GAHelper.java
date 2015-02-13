package utils;

import android.content.Context;
import com.example.myapp.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.HashMap;

/**
 * Copyright by Jerome Chan. All rights reserved.
 *
 * @author chenjinlong
 * @datetime 11/12/2014
 * @description Google Analytics Easy Tracking Functions
 */
public class GAHelper {
    /**
     * 应用级选择停用标志
     */
    private static final Boolean IS_OPTED_OUT = false;

    private Context context;
    private static TrackerName mTrackerId = TrackerName.APP_TRACKER;
    private static Tracker mGaTracker;
    private static GoogleAnalytics mGaInstance;
    private static GAHelper _instance = null;
    private static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private static Date beginTime;

    /**
     * 预定义的GA跟踪器
     * <p/>
     * APP_TRACKER: Tracker used only in this app.
     * GLOBAL_TRACKER: Tracker used by all the apps from a company. eg: roll-up tracking.
     * ECOMMERCE_TRACKER: Tracker used by all ecommerce transactions from a company.
     */
    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERCE_TRACKER,
    }

    private GAHelper(Context context) {
        this.context = context;
    }

    public static synchronized GAHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new GAHelper(context);
            _instance.init();
        }
        return _instance;
    }

    /**
     * 设置GA跟踪器
     *
     * @param trackerId An Defined TrackerName Var
     */
    public static void setTrackerId(TrackerName trackerId) {
        mTrackerId = trackerId;
    }

    /**
     * 获取GA跟踪器
     *
     * @return TrackerName
     */
    public static TrackerName getTrackerId() {
        return mTrackerId;
    }

    /**
     * 发送页面浏览
     *
     * @param screenName
     */
    public void sendAppView(String screenName) {
        mGaTracker.setScreenName(screenName);
        mGaTracker.send(new HitBuilders.AppViewBuilder().build());
        mGaTracker.setScreenName(null);
    }

    /**
     * 发送事件
     *
     * @param category
     * @param action
     * @param label
     * @param value
     */
    public void sendEvent(String category, String action, String label, long value) {
        mGaTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

    /**
     * 发送用户计时
     *
     * @param timingCategory
     * @param timingInterval
     * @param timingName
     * @param timingLabel
     */
    public void sendTiming(String timingCategory, long timingInterval, String timingName, String timingLabel) {
        mGaTracker.send(new HitBuilders.TimingBuilder()
                .setCategory(timingCategory)
                .setValue(timingInterval)
                .setVariable(timingName)
                .setLabel(timingLabel)
                .build());
    }

    /**
     * 设置开始时间
     *
     * @param beginTime
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * 获取开始时间到结束时间的时间开销
     *
     * @return
     */
    public long getTimeSpend() {
        Date curTime = new Date();
        long span = curTime.getTime() - beginTime.getTime();
        return span;
    }

    /**
     * 发送异常捕获
     *
     * @param description
     */
    public void sendException(String description) {
        mGaTracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(description)
                .setFatal(false)
                .build());
    }

    /**
     * 初始化GA参数配置
     */
    private void init() {
        if (mGaInstance == null) {
            if (!IS_OPTED_OUT) {
                mGaInstance = GoogleAnalytics.getInstance(context);
            } else {
                mGaInstance = GoogleAnalytics.getInstance(context.getApplicationContext());
                mGaInstance.setAppOptOut(true);
            }
        }

        if (mGaTracker == null) {
            if (!mTrackers.containsKey(mTrackerId)) {
                Tracker t = (mTrackerId == TrackerName.APP_TRACKER) ? mGaInstance.newTracker(R.xml.app_tracker)
                        : (mTrackerId == TrackerName.GLOBAL_TRACKER) ? mGaInstance.newTracker(R.xml.global_tracker)
                        : mGaInstance.newTracker(R.xml.ecommerce_tracker);
                mTrackers.put(mTrackerId, t);
            }
            mGaTracker = mTrackers.get(mTrackerId);
        }
    }
}
