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
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button interstitialButton;
    private InterstitialAd mInterstitialAd;
    public AdView bannerAd;
    public static String interstitialTestId =
            "ca-app-pub-3940256099942544/1033173712";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, initializationStatus -> {
            //check each adapter init status before making an ad
            //
            /*
            Initialize your ad object with an Activity instance
In the constructor for a new ad object (for example, AdView), you must pass in an object of type Context. This Context is passed on to other ad networks when using mediation. Some ad networks require a more restrictive Context that is of type Activity and may not be able to serve ads without an Activity instance. Therefore, we recommend passing in an Activity instance when initializing ad objects to ensure a consistent experience with your mediated ad networks.


             */
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for(String adapter : statusMap.keySet()){
                AdapterStatus status = statusMap.get(adapter);
                Log.i("Mobile ads SDK", String.format("Adapter : %s, Desc : %s, Latency : %d", adapter, status.getDescription(), status.getDescription()));
            }


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
        /*
        Banner ads mediation
Make sure to disable refresh in all third-party ad networks UI for banner ad units used in AdMob mediation. This will prevent a double refresh since AdMob also triggers a refresh based on your banner ad unit's refresh rate.


         */
        bannerAd = new AdView(this);
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
                Log.i("Banner SDK used : ", "" + bannerAd.getResponseInfo().getMediationAdapterClassName());
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