package com.example.admobtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {

    Button interstitialButton;
    private InterstitialAd mInterstitialAd;
    public static String interstitialTestId =
            "ca-app-pub-3940256099942544/1033173712";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, initializationStatus -> {

        });
        bannerAdFunction();
        interstitialLoadAdFunction();
        setButtonOnClick();
    }

    private void setButtonOnClick() {
        interstitialButton = findViewById(R.id.interstitialButton);

        interstitialButton.setOnClickListener(view -> {
            if(mInterstitialAd != null){
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
                mInterstitialAd.show(MainActivity.this);
            } else {
                t("Ad was not ready");
            }
        });
    }

    private void interstitialLoadAdFunction() {
        AdRequest adReq = new AdRequest.Builder().build();
        InterstitialAd.load(this, interstitialTestId,
                adReq, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        //when ad errors
                        //handle the rror
                        t("Ad load failed");
                        Log.e("interstitial ad error code : ", loadAdError.getMessage());
                        super.onAdFailedToLoad(loadAdError);
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        //mInterstitial is always null until this function comes then instantiate it
                        mInterstitialAd = interstitialAd;
                        super.onAdLoaded(interstitialAd);
                        t("Interstitial Ad loaded ");
                    }
                });
    }

    private void bannerAdFunction() {
        AdView bannerAd = new AdView(this);
        bannerAd.setAdSize(AdSize.BANNER);
        bannerAd.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        bannerAd = findViewById(R.id.bannerAd);
        AdRequest req = new AdRequest.Builder().build();
        bannerAd.loadAd(req);
        bannerAd.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                t("Ad Clicked");
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                t("After user is back from ad after clicking on it");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                t("ad load error");
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                t("impression recorded, only when setoverrideimpressionrecording is true");
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                t("ad finished loading");
            }

            @Override
            public void onAdOpened() {
                t("ad opened overlay that covers the screen");
                super.onAdOpened();
            }
        });

        // hardware acc for video ads programmatically
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        //disable HA for specific views
        bannerAd.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        /*
        View.isHardwareAccelerated() returns true if the View is attached to a hardware accelerated window.
Canvas.isHardwareAccelerated() returns true if the Canvas is hardware accelerated

#Individual ad views cannot be HA if the Activity is not HA enabled
         */
    }

    public void t(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}