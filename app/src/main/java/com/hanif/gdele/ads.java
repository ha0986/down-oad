package com.hanif.gdele;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Objects;





public class ads {
    public static InterstitialAd mInterstitialAd;
    private static RewardedAd mRewardedAd;
    public static boolean isLoading;
    public static boolean RewardShowing;
    public static boolean isRewarded;


    public static void loadInter(Context context, Activity activity) {
        if (mInterstitialAd== null){
            AdRequest loadInter = new AdRequest.Builder().build();

            InterstitialAd.load(context, "ca-app-pub-9422110628550448/4299605336", loadInter,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });
        }else {
            showInter(activity);
        }


    }



    public static void showInter(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }




    public static void loadReward(Context context,Activity activity, String screen) {
        String id = "ca-app-pub-9422110628550448/7165403837";
        if (mRewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(context, id, adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mRewardedAd = null;
                            isLoading = false;
                            Toast.makeText(context, "Ads failed to load try again later", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            isLoading = false;
                            showReward(context, activity, id, screen);
                        }

                    });
        }else {
            showReward(context, activity, id, screen);
        }

    }




    public static void showReward(Context context,Activity activity, String id, String screen) {

        if (mRewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        RewardShowing = true;
                    }


                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        mRewardedAd = null;

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mRewardedAd = null;
                        if(screen == "bonus"){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.app_name);
                            builder.setMessage("You will get your offer within a day. Please keep patience");
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        RewardShowing = false;
                    }
                });

        mRewardedAd.show(
                activity,
                rewardItem -> {
                    int rewardAmount = rewardItem.getAmount();
                    if (Objects.equals(screen, "doTask")){


                    } else if (Objects.equals(screen, "profile")) {

                    }else {
                        isRewarded = true;
                    }
                });

    }






}
