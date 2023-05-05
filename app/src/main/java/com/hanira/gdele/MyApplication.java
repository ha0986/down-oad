package com.hanira.gdele;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;
import com.yausername.aria2c.Aria2c;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.BuildConfig;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;




/** Application class that initializes, loads and show ads when activities change states. */
public class MyApplication extends Application
        implements ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;
    private static final String TAG = "MyApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        // Log the Mobile Ads SDK version.

        MobileAds.initialize(
                this,
                initializationStatus -> {});

        appOpenAdManager = new AppOpenAdManager();













        //initializing RxJava
        configureRxJavaErrorHandler();
        Completable.fromAction(this::initLibraries).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                // it worked
            }

            @Override
            public void onError(Throwable e) {
                if(BuildConfig.DEBUG) Log.e(TAG, "failed to initialize youtubedl-android", e);
                Toast.makeText(getApplicationContext(), "initialization failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}


    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {

        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }


    private class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        private long loadTime = 0;

        public AppOpenAdManager() {}


        private void loadAd(Context context) {
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context,
                    AD_UNIT_ID,
                    request,
                    new AppOpenAdLoadCallback() {

                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                        }


                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                        }
                    });
        }

        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        private boolean isAdAvailable() {

            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }


        private void showAdIfAvailable(@NonNull final Activity activity) {
            showAdIfAvailable(
                    activity,
                    () -> {
                    });
        }


        private void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            if (isShowingAd) {
                return;
            }

            if (!isAdAvailable()) {
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }


            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;


                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;


                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }
                    });

            isShowingAd = true;
            appOpenAd.show(activity);
        }
    }















    //Rxjava code for initializing youtubeDl android
    private void configureRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(e -> {

            if (e instanceof UndeliverableException) {
                // As UndeliverableException is a wrapper, get the cause of it to get the "real" exception
                e = e.getCause();
            }

            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }

            Log.e(TAG, "Undeliverable exception received, not sure what to do", e);
        });
    }

    private void initLibraries() throws YoutubeDLException {
        YoutubeDL.getInstance().init(this);
        FFmpeg.getInstance().init(this);
        Aria2c.getInstance().init(this);
    }
}