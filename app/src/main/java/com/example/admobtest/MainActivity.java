package com.example.admobtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentRequestParameters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button interstitialButton, nativeButton;
    private InterstitialAd mInterstitialAd;
    public AdView bannerAd;
    public static String interstitialTestId =
            "ca-app-pub-3940256099942544/1033173712";
    public static final String rewardedStringId =
            "ca-app-pub-3940256099942544/5224354917";
    private RewardedAd mRewardedAd;
    private final String TAG = "MainActivity Ad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> testDeviceIds = Arrays.asList("B2681743EA4EB0215EA7C5797E47E37B");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.initialize(this, initializationStatus -> {
            //check each adapter init status before making an ad
            /*
            Initialize your ad object with an Activity instance
In the constructor for a new ad object (for example, AdView), you must pass in an object of type Context. This Context is passed on to other ad networks when using mediation. Some ad networks require a more restrictive Context that is of type Activity and may not be able to serve ads without an Activity instance. Therefore, we recommend passing in an Activity instance when initializing ad objects to ensure a consistent experience with your mediated ad networks.
             */

            /*

It is a best practice to load an ad inside the callback of the OnInitializationCompleteListener. Even if a mediation network is not ready, the Google Mobile Ads SDK will still ask that network for an ad. So if a mediation network finishes initializing after the timeout, it can still service future ad requests in that session.

You can continue to poll the initialization status of all adapters throughout your app session by calling MobileAds.getInitializationStatus().

AdapterStatus.getDescription() describes why an adapter is not ready to service ad requests.
*/


            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for(String adapter : statusMap.keySet()){
                AdapterStatus status = statusMap.get(adapter);
                Log.i("Mobile ads SDK", String.format("Adapter : %s, Desc : %s, Latency : %d", adapter, status.getDescription(), status.getLatency()));
            }
            MobileAds.setRequestConfiguration(configuration);
        });
        bannerAdFunction();
        interstitialLoadAdFunction();
        setButtonOnClick();
        nativeFunction();
        rewardedFunction();
        getUserConsent();
    }

    private void getUserConsent() {
        ConsentInformation consentInfo = ConsentInformation.getInstance(this);
        //use your publisher id
        String[] publisherIds = {"pub-2588678730557792"};
        /*
                The call to requestConsentInfoUpdate() requires two arguments:

        An array of valid, fully activated publisher IDs that your app requests ads from. Find your publisher ID.

        An instance of ConsentInfoUpdateListener.
         */
        consentInfo.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                //user's consent status update successfully
                /*
The returned ConsentStatus may have the values listed below:

ConsentStatus.PERSONALIZED
The user has granted consent for personalized ads.

ConsentStatus.NON_PERSONALIZED
The user has granted consent for non-personalized ads.

ConsentStatus.UNKNOWN
The user has neither granted nor declined consent for personalized or non-personalized ads.
                 */

            }

            @Override
            public void onFailedToUpdateConsentInfo(String reason) {
                //user's consent failed to update
                t(reason);
            }
        });
        boolean locationInfoKnown = consentInfo.isRequestLocationInEeaOrUnknown();
/*

If the isRequestLocationInEeaOrUnknown() method returns true:

If the returned ConsentStatus is PERSONALIZED or NON_PERSONALIZED, the user has already provided consent. You can now forward consent to the Google Mobile Ads SDK.

If the returned ConsentStatus is UNKNOWN, see the Collect consent section below, which describes the use of utility methods to collect consent.

The default behavior of the Google Mobile Ads SDK is to serve personalized ads. If a user has consented to receive only non-personalized ads, you can configure an AdRequest object to specify that only non-personalized ads should be requested. The following code causes non-personalized ads to be requested regardless of whether or not the user is in the EEA:

If non-personalized ads are requested, the ad request URL currently includes &npa=1. However, note that this is an internal implementation detail of the Google Mobile Ads SDK and is subject to change.

Google's Consent SDK. provides two ways to collect consent from a user:


Present a Google-rendered consent form to the user.

Request the list of ad technology providers and collect consent yourself using the Publisher-managed consent collection option.

 The Google-rendered consent form is a full-screen configurable form that displays over your app content. You can configure the form to present the user with combinations of the following options:

Consent to view personalized ads
Consent to view non-personalized ads
Use a paid version of the app instead of viewing ads*/

        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        //request
        AdRequest req = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();


        /*
        Once consent information is successfully updated, you can also check ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() to see if the user is located in the European Economic Area or the request location is unknown.

        You should review the consent text carefully: what appears by default is a message that might be appropriate if you use Google to monetize your app; but we cannot provide legal advice on the consent text that is appropriate for you. To update consent text of the Google-rendered consent form, modify the consentform.html file included in the Consent SDK as required.

        The methods above prepare the Google-rendered consent form with the following options:

withListener()
Registers a listener for the ConsentForm. Each of the overridable methods in ConsentFormListener corresponds to an event in the lifecycle of the consent form.
Overridable methods
onConsentFormLoaded	The consent form successfully loaded.
onConsentFormError	The consent form failed to load. The errorDescription parameter provides a description of the error.
onConsentFormOpened	The consent form was opened.
onConsentFormClosed	The consent form was closed. The parameters of the method provides the following information:
consentStatus is a ConsentStatus value that describes the updated consent status of the user.
userPrefersAdFree has a value of true when the user chose to use a paid version of the app instead of viewing ads.
         */


        /*

         */
    }

    private void rewardedFunction() {
        Button rewardedAdButton = findViewById(R.id.rewardedButtonContent);
        rewardedAdButton.setOnClickListener(view -> {
       setContentView(R.layout.rewarded_ads);
       Button openRewardedAdButton = findViewById(R.id.rewardedButton);
       openRewardedAdButton.setOnClickListener(v -> {
           t("button clicked 1");
           AdRequest req = new AdRequest.Builder().build();
           RewardedAd.load(this, rewardedStringId, req, new RewardedAdLoadCallback() {
               @Override
               public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                   //handles what happens when the ad fails to load
                   super.onAdFailedToLoad(loadAdError);
                   Log.d(TAG, loadAdError.getMessage());
                   mRewardedAd = null;
               }

               @Override
               public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                   //handles what happens when the ad is loaded perfectly
                   super.onAdLoaded(rewardedAd);
                   mRewardedAd = rewardedAd;
                   Log.d(TAG, "Ad was loaded perfectly");
                   mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                       @Override
                       public void onAdClicked() {
                           t("rewarded ad was clicked");
                           super.onAdClicked();
                       }

                       @Override
                       public void onAdDismissedFullScreenContent() {
                           t("rewarded ad dismissed");
                           super.onAdDismissedFullScreenContent();
                           mRewardedAd = null;
                       }

                       @Override
                       public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                           t("rewarded ad failed to show");
                           super.onAdFailedToShowFullScreenContent(adError);
                       }

                       @Override
                       public void onAdImpression() {
                           t("rewarded ad recorded an impression");
                           super.onAdImpression();
                       }

                       @Override
                       public void onAdShowedFullScreenContent() {
                           t("rewarded ad shown perfectly");
                           super.onAdShowedFullScreenContent();
                       }
                   });
                   if(mRewardedAd != null){
                       Activity context = MainActivity.this;
                       mRewardedAd.show(context, rewardItem -> {
                           t("user received reward");
                           int rewardAmount = rewardItem.getAmount();
                           String rewardType = rewardItem.getType();

                       });
                   } else {
                       t("Rewarded ad still not ready");
                   }
               }
           });
       });
        });
    }

    private void nativeFunction() {
        nativeButton = findViewById(R.id.nativeButton);
        nativeButton.setOnClickListener(view -> {
        Intent i = new Intent(MainActivity.this, NativeAdActivity.class);
        startActivity(i);
        });
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
                        /*
                        /*
                         It's important to make sure that when your app displays an interstitial ad, it also suspends its use of some resources to allow the ad to take advantage of them. For example, when you make the call to display an interstitial ad, be sure to pause any audio output being produced by your app

                         */
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
        /*
        it's also important to make sure the user doesn't have to wait for them to load. Loading the ad in advance by calling load() before you intend to call show() can ensure that your app has a fully loaded interstitial ad at the ready when the time comes to display one
        */
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
                        /*
                         It's important to make sure that when your app displays an interstitial ad, it also suspends its use of some resources to allow the ad to take advantage of them. For example, when you make the call to display an interstitial ad, be sure to pause any audio output being produced by your app
                         */
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